package net.minecraft.server.network;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import com.mojang.authlib.services.ProfileResult;
import com.mojang.logging.LogUtils;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.security.MessageDigest;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.concurrent.atomic.AtomicInteger;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.CrashReportDetail;
import net.minecraft.DefaultUncaughtExceptionHandler;
import net.minecraft.SharedConstants;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.Connection;
import net.minecraft.network.DisconnectionDetails;
import net.minecraft.network.TickablePacketListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.configuration.ConfigurationProtocols;
import net.minecraft.network.protocol.cookie.ServerboundCookieResponsePacket;
import net.minecraft.network.protocol.login.ClientboundApplyOptionsPacket;
import net.minecraft.network.protocol.login.ClientboundKeyPacket;
import net.minecraft.network.protocol.login.ClientboundLoginDisconnectPacket;
import net.minecraft.network.protocol.login.ClientboundLoginFinishedPacket;
import net.minecraft.network.protocol.login.ClientboundSetupCompressionPacket;
import net.minecraft.network.protocol.login.ConnectionOptionsPacket;
import net.minecraft.network.protocol.login.ServerLoginPacketListener;
import net.minecraft.network.protocol.login.ServerboundCustomQueryAnswerPacket;
import net.minecraft.network.protocol.login.ServerboundHelloPacket;
import net.minecraft.network.protocol.login.ServerboundKeyPacket;
import net.minecraft.network.protocol.login.ServerboundLoginAcknowledgedPacket;
import net.minecraft.network.protocol.login.ServerboundOptionsAppliedPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.notifications.ServerActivityMonitor;
import net.minecraft.server.players.NameAndId;
import net.minecraft.server.players.PlayerList;
import net.minecraft.util.StringUtil;
import net.minecraft.util.crypto.CryptException;
import net.minecraft.util.crypto.PublicPeerInfo;
import net.minecraft.util.crypto.SessionCommonInfo;
import net.minecraft.util.crypto.SessionKeyExchange;
import net.minecraft.util.crypto.SessionKeys;
import net.minecraft.util.crypto.StreamCryptoUtils;
import org.apache.commons.lang3.Validate;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class ServerLoginPacketListenerImpl implements ServerLoginPacketListener, TickablePacketListener {
   private static final AtomicInteger UNIQUE_THREAD_ID = new AtomicInteger(0);
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int MAX_TICKS_BEFORE_LOGIN = 600;
   private static final String PROTOCOL_ERROR_MESSAGE = "Protocol error";
   public static final boolean SHOULD_AUTHENTICATE = true;
   private static final String SERVER_NAME = "server";
   private final MinecraftServer server;
   private final Connection connection;
   private final ServerActivityMonitor serverActivityMonitor;
   private volatile State state;
   private int tick;
   private @Nullable String requestedUsername;
   private @Nullable GameProfile resolvedUserProfile;
   private final boolean transferred;
   private final OptionalInt requestedCompressionThreshold;
   private @Nullable SessionKeyExchange sessionKeyExchange;
   private @Nullable SessionKeys pendingSessionKeys;
   private StreamCryptoUtils.@Nullable IVPair pendingIvSeeds;

   public ServerLoginPacketListenerImpl(final MinecraftServer server, final Connection connection, final boolean transferred) {
      super();
      this.state = ServerLoginPacketListenerImpl.State.HELLO;
      this.server = server;
      this.connection = connection;
      this.serverActivityMonitor = this.server.getServerActivityMonitor();
      this.transferred = transferred;
      int compressionThreshold = this.server.getCompressionThreshold();
      this.requestedCompressionThreshold = !connection.isMemoryConnection() && compressionThreshold >= 0 ? OptionalInt.of(compressionThreshold) : OptionalInt.empty();
   }

   public void tick() {
      if (this.state == ServerLoginPacketListenerImpl.State.VERIFYING) {
         this.verifyLoginAndFinishConnectionSetup((GameProfile)Objects.requireNonNull(this.resolvedUserProfile));
      }

      if (this.state == ServerLoginPacketListenerImpl.State.WAITING_FOR_DUPE_DISCONNECT && !this.isPlayerAlreadyInWorld((GameProfile)Objects.requireNonNull(this.resolvedUserProfile))) {
         this.finishLoginAndWaitForClient(this.resolvedUserProfile);
      }

      if (this.tick++ == 600) {
         this.disconnect(Component.translatable("multiplayer.disconnect.slow_login"));
      }

   }

   public boolean isAcceptingMessages() {
      return this.connection.isConnected();
   }

   public void disconnect(final Component component) {
      try {
         LOGGER.info("Disconnecting {}: {}", this.getUserName(), component.getString());
         this.connection.send(new ClientboundLoginDisconnectPacket(component));
         this.connection.disconnect(component);
      } catch (Exception e) {
         LOGGER.error("Error whilst disconnecting player", e);
      }

   }

   private boolean isPlayerAlreadyInWorld(final GameProfile gameProfile) {
      return this.server.getPlayerList().getPlayer(gameProfile.id()) != null;
   }

   public void onDisconnect(final DisconnectionDetails details) {
      LOGGER.info("{} lost connection: {}", this.getUserName(), details.reason().getString());
   }

   public String getUserName() {
      String loggableAddress = this.connection.getLoggableAddress(this.server.logIPs());
      return this.requestedUsername != null ? this.requestedUsername + " (" + loggableAddress + ")" : loggableAddress;
   }

   public void handleHello(final ServerboundHelloPacket packet) {
      Validate.validState(this.state == ServerLoginPacketListenerImpl.State.HELLO, "Unexpected hello packet", new Object[0]);
      Validate.validState(StringUtil.isValidPlayerName(packet.name()), "Invalid characters in username", new Object[0]);
      this.requestedUsername = packet.name();
      GameProfile singleplayerProfile = this.server.getSingleplayerProfile();
      if (singleplayerProfile != null && this.requestedUsername.equalsIgnoreCase(singleplayerProfile.name())) {
         this.startOfflineClientVerification(singleplayerProfile);
      } else {
         if (this.server.usesAuthentication() && !this.connection.isMemoryConnection()) {
            int protocolVersion = SharedConstants.getCurrentVersion().protocolVersion();
            String serverName = "server";
            SessionKeyExchange.ServerSideFactory infoFactory = this.server.connectionPeerFactory();
            this.sessionKeyExchange = infoFactory.createServerSide(this.connection.channelId(), "server", protocolVersion, true);
            this.state = ServerLoginPacketListenerImpl.State.KEY;
            SessionCommonInfo commonInfo = this.sessionKeyExchange.commonInfo();
            this.connection.send(new ClientboundKeyPacket(commonInfo.salt(), "server", this.sessionKeyExchange.publicKey(), commonInfo.authenticationRequested()));
         } else {
            this.startOfflineClientVerification(UUIDUtil.createOfflineProfile(this.requestedUsername));
         }

      }
   }

   private void startOfflineClientVerification(final GameProfile profile) {
      this.resolvedUserProfile = profile;
      this.state = ServerLoginPacketListenerImpl.State.VERIFYING;
   }

   private void verifyLoginAndFinishConnectionSetup(final GameProfile profile) {
      PlayerList playerList = this.server.getPlayerList();
      Component error = playerList.canPlayerLogin(this.connection.getRemoteAddress(), new NameAndId(profile));
      if (error != null) {
         this.disconnect(error);
      } else {
         if (this.requestedCompressionThreshold.isPresent()) {
            this.connection.send(new ClientboundSetupCompressionPacket(this.requestedCompressionThreshold.getAsInt()));
         }

         boolean waitForDisconnection = playerList.disconnectAllPlayersWithProfile(profile.id());
         if (waitForDisconnection) {
            this.state = ServerLoginPacketListenerImpl.State.WAITING_FOR_DUPE_DISCONNECT;
         } else {
            this.finishLoginAndWaitForClient(profile);
         }
      }

   }

   private void finishLoginAndWaitForClient(final GameProfile gameProfile) {
      byte optionsToEnable = this.loginFinishedPacketEnableOptions();
      this.state = ServerLoginPacketListenerImpl.State.PROTOCOL_SWITCHING;
      this.connection.send(new ClientboundLoginFinishedPacket(gameProfile, this.server.getConnection().getSessionId(), optionsToEnable));
   }

   private @ConnectionOptionsPacket.Flags byte loginFinishedPacketEnableOptions() {
      return (byte)(this.requestedCompressionThreshold.isPresent() ? 2 : 0);
   }

   public void handleKey(final ServerboundKeyPacket packet) {
      Validate.validState(this.state == ServerLoginPacketListenerImpl.State.KEY, "Unexpected key packet", new Object[0]);
      final String clientName = (String)Objects.requireNonNull(this.requestedUsername, "Player name not initialized");

      final SessionKeys sessionKeys;
      try {
         PublicPeerInfo clientInfo = new PublicPeerInfo(packet.clientKey(), clientName);
         sessionKeys = ((SessionKeyExchange)Objects.requireNonNull(this.sessionKeyExchange, "Not in key negotiation")).createSessionKeys(clientInfo);
         byte[] computedSignature = sessionKeys.computeIntegrityDigest();
         if (!MessageDigest.isEqual(computedSignature, packet.signature())) {
            LOGGER.warn("Signature mismatch, potential tampering detected!");
            throw new IllegalStateException();
         }

         this.pendingIvSeeds = (new StreamCryptoUtils.IVGenerator(computedSignature)).generatePair();
         this.state = ServerLoginPacketListenerImpl.State.AUTHENTICATING;
      } catch (Exception e) {
         throw new IllegalStateException("Protocol error", e);
      } finally {
         this.sessionKeyExchange = null;
      }

      Thread thread = new Thread("User Authenticator #" + UNIQUE_THREAD_ID.incrementAndGet()) {
         {
            Objects.requireNonNull(ServerLoginPacketListenerImpl.this);
         }

         public void run() {
            try {
               ProfileResult result = ServerLoginPacketListenerImpl.this.server.services().sessionService().hasJoinedServer(clientName, sessionKeys.computeKeysAuthenticationDigest(), this.getAddress());
               if (result != null) {
                  GameProfile profile = result.profile();
                  ServerLoginPacketListenerImpl.LOGGER.info("UUID of player {} is {}", profile.name(), profile.id());
                  ServerLoginPacketListenerImpl.this.serverActivityMonitor.reportLoginActivity();
                  ServerLoginPacketListenerImpl.this.applyConnectionEncryption(profile, sessionKeys);
               } else if (ServerLoginPacketListenerImpl.this.server.isSingleplayer()) {
                  ServerLoginPacketListenerImpl.LOGGER.warn("Failed to verify username but will let them in anyway!");
                  ServerLoginPacketListenerImpl.this.applyConnectionEncryption(UUIDUtil.createOfflineProfile(clientName), sessionKeys);
               } else {
                  ServerLoginPacketListenerImpl.this.disconnect(Component.translatable("multiplayer.disconnect.unverified_username"));
                  ServerLoginPacketListenerImpl.LOGGER.error("Username '{}' tried to join with an invalid session", clientName);
               }
            } catch (AuthenticationUnavailableException var3) {
               if (ServerLoginPacketListenerImpl.this.server.isSingleplayer()) {
                  ServerLoginPacketListenerImpl.LOGGER.warn("Authentication servers are down but will let them in anyway!");
                  ServerLoginPacketListenerImpl.this.applyConnectionEncryption(UUIDUtil.createOfflineProfile(clientName), sessionKeys);
               } else {
                  ServerLoginPacketListenerImpl.this.disconnect(Component.translatable("multiplayer.disconnect.authservers_down"));
                  ServerLoginPacketListenerImpl.LOGGER.error("Couldn't verify username because servers are unavailable");
               }
            }

         }

         private @Nullable InetAddress getAddress() {
            SocketAddress remoteAddress = ServerLoginPacketListenerImpl.this.connection.getRemoteAddress();
            InetAddress var10000;
            if (ServerLoginPacketListenerImpl.this.server.getPreventProxyConnections() && remoteAddress instanceof InetSocketAddress inetSocketAddress) {
               var10000 = inetSocketAddress.getAddress();
            } else {
               var10000 = null;
            }

            return var10000;
         }
      };
      thread.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(LOGGER));
      thread.start();
   }

   private void applyConnectionEncryption(final GameProfile authenticatedGameProfile, final SessionKeys secretKey) {
      this.resolvedUserProfile = authenticatedGameProfile;
      this.pendingSessionKeys = secretKey;
      this.state = ServerLoginPacketListenerImpl.State.APPLYING_ENCRYPTION;
      this.connection.send(new ClientboundApplyOptionsPacket((byte)1));
   }

   public void handleOptionsEnabled(final ServerboundOptionsAppliedPacket packet) {
      Validate.validState(this.state == ServerLoginPacketListenerImpl.State.APPLYING_ENCRYPTION, "Unexpected acknowledgement packet", new Object[0]);
      this.applyConnectionOptions((byte)1);
      this.state = ServerLoginPacketListenerImpl.State.VERIFYING;
   }

   private void applyConnectionOptions(final @ConnectionOptionsPacket.Flags byte options) {
      if (ConnectionOptionsPacket.isEncryption(options)) {
         SessionKeys sessionKeys = (SessionKeys)Objects.requireNonNull(this.pendingSessionKeys, "Key not initialized");
         this.pendingSessionKeys = null;
         SecretKey inboundKey = sessionKeys.inboundStreamKey(SessionKeyExchange.DEFAULT_SERVER_SIDE);
         SecretKey outboundKey = sessionKeys.outboundStreamKey(SessionKeyExchange.DEFAULT_SERVER_SIDE);
         StreamCryptoUtils.IVPair ivs = (StreamCryptoUtils.IVPair)Objects.requireNonNull(this.pendingIvSeeds);

         try {
            Cipher decryptCipher = StreamCryptoUtils.getCipher(2, inboundKey, ivs.clientToServer());
            Cipher encryptCipher = StreamCryptoUtils.getCipher(1, outboundKey, ivs.serverToClient());
            this.connection.setEncryptionKey(decryptCipher, encryptCipher);
         } catch (CryptException e) {
            throw new IllegalStateException("Protocol error", e);
         }
      }

      if (ConnectionOptionsPacket.isCompression(options)) {
         if (this.requestedCompressionThreshold.isEmpty()) {
            throw new IllegalStateException("Compression options not initialized");
         }

         this.connection.setupCompression(this.requestedCompressionThreshold.getAsInt(), true);
      }

   }

   public void handleCustomQueryPacket(final ServerboundCustomQueryAnswerPacket packet) {
      this.disconnect(ServerCommonPacketListenerImpl.DISCONNECT_UNEXPECTED_QUERY);
   }

   public void handleLoginAcknowledgement(final ServerboundLoginAcknowledgedPacket packet) {
      this.applyConnectionOptions(this.loginFinishedPacketEnableOptions());
      Validate.validState(this.state == ServerLoginPacketListenerImpl.State.PROTOCOL_SWITCHING, "Unexpected login acknowledgement packet", new Object[0]);
      this.connection.setupOutboundProtocol(ConfigurationProtocols.CLIENTBOUND);
      CommonListenerCookie cookie = CommonListenerCookie.createInitial((GameProfile)Objects.requireNonNull(this.resolvedUserProfile), this.transferred);
      ServerConfigurationPacketListenerImpl configPacketListener = new ServerConfigurationPacketListenerImpl(this.server, this.connection, cookie);
      this.connection.setupInboundProtocol(ConfigurationProtocols.SERVERBOUND, configPacketListener);
      configPacketListener.startConfiguration();
      this.state = ServerLoginPacketListenerImpl.State.ACCEPTED;
   }

   public void fillListenerSpecificCrashDetails(final CrashReport report, final CrashReportCategory connectionDetails) {
      connectionDetails.setDetail("Login phase", (CrashReportDetail)(() -> this.state.toString()));
   }

   public void handleCookieResponse(final ServerboundCookieResponsePacket packet) {
      this.disconnect(ServerCommonPacketListenerImpl.DISCONNECT_UNEXPECTED_QUERY);
   }

   private static enum State {
      HELLO,
      KEY,
      AUTHENTICATING,
      APPLYING_ENCRYPTION,
      NEGOTIATING,
      VERIFYING,
      WAITING_FOR_DUPE_DISCONNECT,
      PROTOCOL_SWITCHING,
      ACCEPTED;

      private State() {
      }

      // $FF: synthetic method
      private static State[] $values() {
         return new State[]{HELLO, KEY, AUTHENTICATING, APPLYING_ENCRYPTION, NEGOTIATING, VERIFYING, WAITING_FOR_DUPE_DISCONNECT, PROTOCOL_SWITCHING, ACCEPTED};
      }
   }
}
