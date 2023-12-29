package net.minecraft.server.network;

import com.google.common.primitives.Ints;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import com.mojang.authlib.yggdrasil.ProfileResult;
import com.mojang.logging.LogUtils;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.security.PrivateKey;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nullable;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import net.minecraft.CrashReportCategory;
import net.minecraft.DefaultUncaughtExceptionHandler;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketSendListener;
import net.minecraft.network.TickablePacketListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.login.ClientboundGameProfilePacket;
import net.minecraft.network.protocol.login.ClientboundHelloPacket;
import net.minecraft.network.protocol.login.ClientboundLoginCompressionPacket;
import net.minecraft.network.protocol.login.ClientboundLoginDisconnectPacket;
import net.minecraft.network.protocol.login.ServerLoginPacketListener;
import net.minecraft.network.protocol.login.ServerboundCustomQueryAnswerPacket;
import net.minecraft.network.protocol.login.ServerboundHelloPacket;
import net.minecraft.network.protocol.login.ServerboundKeyPacket;
import net.minecraft.network.protocol.login.ServerboundLoginAcknowledgedPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.util.Crypt;
import net.minecraft.util.CryptException;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;

public class ServerLoginPacketListenerImpl implements ServerLoginPacketListener, TickablePacketListener {
   private static final AtomicInteger UNIQUE_THREAD_ID = new AtomicInteger(0);
   static final Logger LOGGER = LogUtils.getLogger();
   private static final int MAX_TICKS_BEFORE_LOGIN = 600;
   private static final Component DISCONNECT_UNEXPECTED_QUERY = Component.translatable("multiplayer.disconnect.unexpected_query_response");
   private final byte[] challenge;
   final MinecraftServer server;
   final Connection connection;
   private volatile ServerLoginPacketListenerImpl.State state = ServerLoginPacketListenerImpl.State.HELLO;
   private int tick;
   @Nullable
   String requestedUsername;
   @Nullable
   private GameProfile authenticatedProfile;
   private final String serverId = "";

   public ServerLoginPacketListenerImpl(MinecraftServer var1, Connection var2) {
      super();
      this.server = var1;
      this.connection = var2;
      this.challenge = Ints.toByteArray(RandomSource.create().nextInt());
   }

   @Override
   public void tick() {
      if (this.state == ServerLoginPacketListenerImpl.State.VERIFYING) {
         this.verifyLoginAndFinishConnectionSetup(Objects.requireNonNull(this.authenticatedProfile));
      }

      if (this.state == ServerLoginPacketListenerImpl.State.WAITING_FOR_DUPE_DISCONNECT
         && !this.isPlayerAlreadyInWorld(Objects.requireNonNull(this.authenticatedProfile))) {
         this.finishLoginAndWaitForClient(this.authenticatedProfile);
      }

      if (this.tick++ == 600) {
         this.disconnect(Component.translatable("multiplayer.disconnect.slow_login"));
      }
   }

   @Override
   public boolean isAcceptingMessages() {
      return this.connection.isConnected();
   }

   public void disconnect(Component var1) {
      try {
         LOGGER.info("Disconnecting {}: {}", this.getUserName(), var1.getString());
         this.connection.send(new ClientboundLoginDisconnectPacket(var1));
         this.connection.disconnect(var1);
      } catch (Exception var3) {
         LOGGER.error("Error whilst disconnecting player", var3);
      }
   }

   private boolean isPlayerAlreadyInWorld(GameProfile var1) {
      return this.server.getPlayerList().getPlayer(var1.getId()) != null;
   }

   @Override
   public void onDisconnect(Component var1) {
      LOGGER.info("{} lost connection: {}", this.getUserName(), var1.getString());
   }

   public String getUserName() {
      String var1 = this.connection.getLoggableAddress(this.server.logIPs());
      return this.requestedUsername != null ? this.requestedUsername + " (" + var1 + ")" : var1;
   }

   @Override
   public void handleHello(ServerboundHelloPacket var1) {
      Validate.validState(this.state == ServerLoginPacketListenerImpl.State.HELLO, "Unexpected hello packet", new Object[0]);
      Validate.validState(Player.isValidUsername(var1.name()), "Invalid characters in username", new Object[0]);
      this.requestedUsername = var1.name();
      GameProfile var2 = this.server.getSingleplayerProfile();
      if (var2 != null && this.requestedUsername.equalsIgnoreCase(var2.getName())) {
         this.startClientVerification(var2);
      } else {
         if (this.server.usesAuthentication() && !this.connection.isMemoryConnection()) {
            this.state = ServerLoginPacketListenerImpl.State.KEY;
            this.connection.send(new ClientboundHelloPacket("", this.server.getKeyPair().getPublic().getEncoded(), this.challenge));
         } else {
            this.startClientVerification(UUIDUtil.createOfflineProfile(this.requestedUsername));
         }
      }
   }

   void startClientVerification(GameProfile var1) {
      this.authenticatedProfile = var1;
      this.state = ServerLoginPacketListenerImpl.State.VERIFYING;
   }

   private void verifyLoginAndFinishConnectionSetup(GameProfile var1) {
      PlayerList var2 = this.server.getPlayerList();
      Component var3 = var2.canPlayerLogin(this.connection.getRemoteAddress(), var1);
      if (var3 != null) {
         this.disconnect(var3);
      } else {
         if (this.server.getCompressionThreshold() >= 0 && !this.connection.isMemoryConnection()) {
            this.connection
               .send(
                  new ClientboundLoginCompressionPacket(this.server.getCompressionThreshold()),
                  PacketSendListener.thenRun(() -> this.connection.setupCompression(this.server.getCompressionThreshold(), true))
               );
         }

         boolean var4 = var2.disconnectAllPlayersWithProfile(var1);
         if (var4) {
            this.state = ServerLoginPacketListenerImpl.State.WAITING_FOR_DUPE_DISCONNECT;
         } else {
            this.finishLoginAndWaitForClient(var1);
         }
      }
   }

   private void finishLoginAndWaitForClient(GameProfile var1) {
      this.state = ServerLoginPacketListenerImpl.State.PROTOCOL_SWITCHING;
      this.connection.send(new ClientboundGameProfilePacket(var1));
   }

   @Override
   public void handleKey(ServerboundKeyPacket var1) {
      Validate.validState(this.state == ServerLoginPacketListenerImpl.State.KEY, "Unexpected key packet", new Object[0]);

      final String var2;
      try {
         PrivateKey var3 = this.server.getKeyPair().getPrivate();
         if (!var1.isChallengeValid(this.challenge, var3)) {
            throw new IllegalStateException("Protocol error");
         }

         SecretKey var4 = var1.getSecretKey(var3);
         Cipher var5 = Crypt.getCipher(2, var4);
         Cipher var6 = Crypt.getCipher(1, var4);
         var2 = new BigInteger(Crypt.digestData("", this.server.getKeyPair().getPublic(), var4)).toString(16);
         this.state = ServerLoginPacketListenerImpl.State.AUTHENTICATING;
         this.connection.setEncryptionKey(var5, var6);
      } catch (CryptException var7) {
         throw new IllegalStateException("Protocol error", var7);
      }

      Thread var8 = new Thread("User Authenticator #" + UNIQUE_THREAD_ID.incrementAndGet()) {
         @Override
         public void run() {
            String var1 = Objects.requireNonNull(ServerLoginPacketListenerImpl.this.requestedUsername, "Player name not initialized");

            try {
               ProfileResult var2x = ServerLoginPacketListenerImpl.this.server.getSessionService().hasJoinedServer(var1, var2, this.getAddress());
               if (var2x != null) {
                  GameProfile var3 = var2x.profile();
                  ServerLoginPacketListenerImpl.LOGGER.info("UUID of player {} is {}", var3.getName(), var3.getId());
                  ServerLoginPacketListenerImpl.this.startClientVerification(var3);
               } else if (ServerLoginPacketListenerImpl.this.server.isSingleplayer()) {
                  ServerLoginPacketListenerImpl.LOGGER.warn("Failed to verify username but will let them in anyway!");
                  ServerLoginPacketListenerImpl.this.startClientVerification(UUIDUtil.createOfflineProfile(var1));
               } else {
                  ServerLoginPacketListenerImpl.this.disconnect(Component.translatable("multiplayer.disconnect.unverified_username"));
                  ServerLoginPacketListenerImpl.LOGGER.error("Username '{}' tried to join with an invalid session", var1);
               }
            } catch (AuthenticationUnavailableException var4) {
               if (ServerLoginPacketListenerImpl.this.server.isSingleplayer()) {
                  ServerLoginPacketListenerImpl.LOGGER.warn("Authentication servers are down but will let them in anyway!");
                  ServerLoginPacketListenerImpl.this.startClientVerification(UUIDUtil.createOfflineProfile(var1));
               } else {
                  ServerLoginPacketListenerImpl.this.disconnect(Component.translatable("multiplayer.disconnect.authservers_down"));
                  ServerLoginPacketListenerImpl.LOGGER.error("Couldn't verify username because servers are unavailable");
               }
            }
         }

         @Nullable
         private InetAddress getAddress() {
            SocketAddress var1 = ServerLoginPacketListenerImpl.this.connection.getRemoteAddress();
            return ServerLoginPacketListenerImpl.this.server.getPreventProxyConnections() && var1 instanceof InetSocketAddress
               ? ((InetSocketAddress)var1).getAddress()
               : null;
         }
      };
      var8.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(LOGGER));
      var8.start();
   }

   @Override
   public void handleCustomQueryPacket(ServerboundCustomQueryAnswerPacket var1) {
      this.disconnect(DISCONNECT_UNEXPECTED_QUERY);
   }

   @Override
   public void handleLoginAcknowledgement(ServerboundLoginAcknowledgedPacket var1) {
      Validate.validState(this.state == ServerLoginPacketListenerImpl.State.PROTOCOL_SWITCHING, "Unexpected login acknowledgement packet", new Object[0]);
      CommonListenerCookie var2 = CommonListenerCookie.createInitial(Objects.requireNonNull(this.authenticatedProfile));
      ServerConfigurationPacketListenerImpl var3 = new ServerConfigurationPacketListenerImpl(this.server, this.connection, var2);
      this.connection.setListener(var3);
      var3.startConfiguration();
      this.state = ServerLoginPacketListenerImpl.State.ACCEPTED;
   }

   @Override
   public void fillListenerSpecificCrashDetails(CrashReportCategory var1) {
      var1.setDetail("Login phase", () -> this.state.toString());
   }

   static enum State {
      HELLO,
      KEY,
      AUTHENTICATING,
      NEGOTIATING,
      VERIFYING,
      WAITING_FOR_DUPE_DISCONNECT,
      PROTOCOL_SWITCHING,
      ACCEPTED;

      private State() {
      }
   }
}
