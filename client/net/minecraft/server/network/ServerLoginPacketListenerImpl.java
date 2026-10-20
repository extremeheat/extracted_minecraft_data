package net.minecraft.server.network;

import com.google.common.primitives.Ints;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import com.mojang.authlib.services.ProfileResult;
import com.mojang.logging.LogUtils;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.security.PrivateKey;
import java.util.Arrays;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.concurrent.atomic.AtomicInteger;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.CrashReportDetail;
import net.minecraft.DefaultUncaughtExceptionHandler;
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
import net.minecraft.util.Crypt;
import net.minecraft.util.CryptException;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringUtil;
import org.apache.commons.lang3.Validate;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class ServerLoginPacketListenerImpl implements ServerLoginPacketListener, TickablePacketListener {
   private static final AtomicInteger UNIQUE_THREAD_ID = new AtomicInteger(0);
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int MAX_TICKS_BEFORE_LOGIN = 600;
   private static final String PROTOCOL_ERROR_MESSAGE = "Protocol error";
   private final byte[] challenge;
   private final MinecraftServer server;
   private final Connection connection;
   private final ServerActivityMonitor serverActivityMonitor;
   private volatile State state;
   private int tick;
   private @Nullable String requestedUsername;
   private @Nullable GameProfile resolvedUserProfile;
   private final String serverId;
   private final boolean transferred;
   private final OptionalInt requestedCompressionThreshold;
   private @Nullable SecretKey pendingSecretKey;

   public ServerLoginPacketListenerImpl(final MinecraftServer minecraftserver, final Connection connection, final boolean transferred) {
      super();
      this.state = ServerLoginPacketListenerImpl.State.HELLO;
      this.serverId = "";
      this.server = minecraftserver;
      this.connection = connection;
      this.serverActivityMonitor = this.server.getServerActivityMonitor();
      this.challenge = Ints.toByteArray(RandomSource.create().nextInt());
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
            this.state = ServerLoginPacketListenerImpl.State.KEY;
            this.connection.send(new ClientboundKeyPacket("", this.server.getKeyPair().getPublic().getEncoded(), this.challenge, true));
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

      final String digest;
      final SecretKey secretKey;
      try {
         PrivateKey serverPrivateKey = this.server.getKeyPair().getPrivate();
         byte[] receivedChallenge = Crypt.decryptUsingKey(serverPrivateKey, packet.encryptedChallenge());
         secretKey = Crypt.decryptByteToSecretKey(serverPrivateKey, packet.encryptedSecretKey());
         if (!Arrays.equals(this.challenge, receivedChallenge)) {
            throw new IllegalStateException("Protocol error");
         }

         digest = (new BigInteger(Crypt.digestData("", this.server.getKeyPair().getPublic(), secretKey))).toString(16);
         this.state = ServerLoginPacketListenerImpl.State.AUTHENTICATING;
      } catch (CryptException e) {
         throw new IllegalStateException("Protocol error", e);
      }

      Thread thread = new Thread("User Authenticator #" + UNIQUE_THREAD_ID.incrementAndGet()) {
         {
            Objects.requireNonNull(ServerLoginPacketListenerImpl.this);
         }

         public void run() {
            String name = (String)Objects.requireNonNull(ServerLoginPacketListenerImpl.this.requestedUsername, "Player name not initialized");

            try {
               ProfileResult result = ServerLoginPacketListenerImpl.this.server.services().sessionService().hasJoinedServer(name, digest, this.getAddress());
               if (result != null) {
                  GameProfile profile = result.profile();
                  ServerLoginPacketListenerImpl.LOGGER.info("UUID of player {} is {}", profile.name(), profile.id());
                  ServerLoginPacketListenerImpl.this.serverActivityMonitor.reportLoginActivity();
                  ServerLoginPacketListenerImpl.this.applyConnectionEncryption(profile, secretKey);
               } else if (ServerLoginPacketListenerImpl.this.server.isSingleplayer()) {
                  ServerLoginPacketListenerImpl.LOGGER.warn("Failed to verify username but will let them in anyway!");
                  ServerLoginPacketListenerImpl.this.applyConnectionEncryption(UUIDUtil.createOfflineProfile(name), secretKey);
               } else {
                  ServerLoginPacketListenerImpl.this.disconnect(Component.translatable("multiplayer.disconnect.unverified_username"));
                  ServerLoginPacketListenerImpl.LOGGER.error("Username '{}' tried to join with an invalid session", name);
               }
            } catch (AuthenticationUnavailableException var4) {
               if (ServerLoginPacketListenerImpl.this.server.isSingleplayer()) {
                  ServerLoginPacketListenerImpl.LOGGER.warn("Authentication servers are down but will let them in anyway!");
                  ServerLoginPacketListenerImpl.this.applyConnectionEncryption(UUIDUtil.createOfflineProfile(name), secretKey);
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

   private void applyConnectionEncryption(final GameProfile authenticatedGameProfile, final SecretKey secretKey) {
      this.resolvedUserProfile = authenticatedGameProfile;
      this.pendingSecretKey = secretKey;
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
         SecretKey secretKey = (SecretKey)Objects.requireNonNull(this.pendingSecretKey, "Key not initialized");
         this.pendingSecretKey = null;

         try {
            Cipher decryptCipher = Crypt.getCipher(2, secretKey);
            Cipher encryptCipher = Crypt.getCipher(1, secretKey);
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
