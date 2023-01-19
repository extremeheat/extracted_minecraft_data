package net.minecraft.server.network;

import com.google.common.primitives.Ints;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import com.mojang.authlib.minecraft.InsecurePublicKeyException.MissingException;
import com.mojang.logging.LogUtils;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.security.PrivateKey;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nullable;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import net.minecraft.DefaultUncaughtExceptionHandler;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketSendListener;
import net.minecraft.network.TickablePacketListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.ThrowingComponent;
import net.minecraft.network.protocol.game.ClientboundDisconnectPacket;
import net.minecraft.network.protocol.login.ClientboundGameProfilePacket;
import net.minecraft.network.protocol.login.ClientboundHelloPacket;
import net.minecraft.network.protocol.login.ClientboundLoginCompressionPacket;
import net.minecraft.network.protocol.login.ClientboundLoginDisconnectPacket;
import net.minecraft.network.protocol.login.ServerLoginPacketListener;
import net.minecraft.network.protocol.login.ServerboundCustomQueryPacket;
import net.minecraft.network.protocol.login.ServerboundHelloPacket;
import net.minecraft.network.protocol.login.ServerboundKeyPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Crypt;
import net.minecraft.util.CryptException;
import net.minecraft.util.RandomSource;
import net.minecraft.util.SignatureValidator;
import net.minecraft.world.entity.player.ProfilePublicKey;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;

public class ServerLoginPacketListenerImpl implements TickablePacketListener, ServerLoginPacketListener {
   private static final AtomicInteger UNIQUE_THREAD_ID = new AtomicInteger(0);
   static final Logger LOGGER = LogUtils.getLogger();
   private static final int MAX_TICKS_BEFORE_LOGIN = 600;
   private static final RandomSource RANDOM = RandomSource.create();
   private static final Component MISSING_PROFILE_PUBLIC_KEY = Component.translatable("multiplayer.disconnect.missing_public_key");
   private static final Component INVALID_SIGNATURE = Component.translatable("multiplayer.disconnect.invalid_public_key_signature");
   private static final Component INVALID_PUBLIC_KEY = Component.translatable("multiplayer.disconnect.invalid_public_key");
   private final byte[] nonce;
   final MinecraftServer server;
   public final Connection connection;
   ServerLoginPacketListenerImpl.State state = ServerLoginPacketListenerImpl.State.HELLO;
   private int tick;
   @Nullable
   GameProfile gameProfile;
   private final String serverId = "";
   @Nullable
   private ServerPlayer delayedAcceptPlayer;
   @Nullable
   private ProfilePublicKey.Data profilePublicKeyData;

   public ServerLoginPacketListenerImpl(MinecraftServer var1, Connection var2) {
      super();
      this.server = var1;
      this.connection = var2;
      this.nonce = Ints.toByteArray(RANDOM.nextInt());
   }

   @Override
   public void tick() {
      if (this.state == ServerLoginPacketListenerImpl.State.READY_TO_ACCEPT) {
         this.handleAcceptedLogin();
      } else if (this.state == ServerLoginPacketListenerImpl.State.DELAY_ACCEPT) {
         ServerPlayer var1 = this.server.getPlayerList().getPlayer(this.gameProfile.getId());
         if (var1 == null) {
            this.state = ServerLoginPacketListenerImpl.State.READY_TO_ACCEPT;
            this.placeNewPlayer(this.delayedAcceptPlayer);
            this.delayedAcceptPlayer = null;
         }
      }

      if (this.tick++ == 600) {
         this.disconnect(Component.translatable("multiplayer.disconnect.slow_login"));
      }
   }

   @Override
   public Connection getConnection() {
      return this.connection;
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

   public void handleAcceptedLogin() {
      ProfilePublicKey var1 = null;
      if (!this.gameProfile.isComplete()) {
         this.gameProfile = this.createFakeProfile(this.gameProfile);
      } else {
         try {
            SignatureValidator var2 = this.server.getServiceSignatureValidator();
            var1 = validatePublicKey(this.profilePublicKeyData, this.gameProfile.getId(), var2, this.server.enforceSecureProfile());
         } catch (ServerLoginPacketListenerImpl.PublicKeyValidationException var7) {
            LOGGER.error(var7.getMessage(), var7.getCause());
            if (!this.connection.isMemoryConnection()) {
               this.disconnect(var7.getComponent());
               return;
            }
         }
      }

      Component var8 = this.server.getPlayerList().canPlayerLogin(this.connection.getRemoteAddress(), this.gameProfile);
      if (var8 != null) {
         this.disconnect(var8);
      } else {
         this.state = ServerLoginPacketListenerImpl.State.ACCEPTED;
         if (this.server.getCompressionThreshold() >= 0 && !this.connection.isMemoryConnection()) {
            this.connection
               .send(
                  new ClientboundLoginCompressionPacket(this.server.getCompressionThreshold()),
                  PacketSendListener.thenRun(() -> this.connection.setupCompression(this.server.getCompressionThreshold(), true))
               );
         }

         this.connection.send(new ClientboundGameProfilePacket(this.gameProfile));
         ServerPlayer var3 = this.server.getPlayerList().getPlayer(this.gameProfile.getId());

         try {
            ServerPlayer var4 = this.server.getPlayerList().getPlayerForLogin(this.gameProfile, var1);
            if (var3 != null) {
               this.state = ServerLoginPacketListenerImpl.State.DELAY_ACCEPT;
               this.delayedAcceptPlayer = var4;
            } else {
               this.placeNewPlayer(var4);
            }
         } catch (Exception var6) {
            LOGGER.error("Couldn't place player in world", var6);
            MutableComponent var5 = Component.translatable("multiplayer.disconnect.invalid_player_data");
            this.connection.send(new ClientboundDisconnectPacket(var5));
            this.connection.disconnect(var5);
         }
      }
   }

   private void placeNewPlayer(ServerPlayer var1) {
      this.server.getPlayerList().placeNewPlayer(this.connection, var1);
   }

   @Override
   public void onDisconnect(Component var1) {
      LOGGER.info("{} lost connection: {}", this.getUserName(), var1.getString());
   }

   public String getUserName() {
      return this.gameProfile != null
         ? this.gameProfile + " (" + this.connection.getRemoteAddress() + ")"
         : String.valueOf(this.connection.getRemoteAddress());
   }

   @Nullable
   private static ProfilePublicKey validatePublicKey(@Nullable ProfilePublicKey.Data var0, UUID var1, SignatureValidator var2, boolean var3) throws ServerLoginPacketListenerImpl.PublicKeyValidationException {
      try {
         if (var0 == null) {
            if (var3) {
               throw new ServerLoginPacketListenerImpl.PublicKeyValidationException(MISSING_PROFILE_PUBLIC_KEY);
            } else {
               return null;
            }
         } else {
            return ProfilePublicKey.createValidated(var2, var1, var0);
         }
      } catch (MissingException var5) {
         if (var3) {
            throw new ServerLoginPacketListenerImpl.PublicKeyValidationException(INVALID_SIGNATURE, var5);
         } else {
            return null;
         }
      } catch (CryptException var6) {
         throw new ServerLoginPacketListenerImpl.PublicKeyValidationException(INVALID_PUBLIC_KEY, var6);
      } catch (Exception var7) {
         throw new ServerLoginPacketListenerImpl.PublicKeyValidationException(INVALID_SIGNATURE, var7);
      }
   }

   @Override
   public void handleHello(ServerboundHelloPacket var1) {
      Validate.validState(this.state == ServerLoginPacketListenerImpl.State.HELLO, "Unexpected hello packet", new Object[0]);
      Validate.validState(isValidUsername(var1.name()), "Invalid characters in username", new Object[0]);
      this.profilePublicKeyData = var1.publicKey().orElse(null);
      GameProfile var2 = this.server.getSingleplayerProfile();
      if (var2 != null && var1.name().equalsIgnoreCase(var2.getName())) {
         this.gameProfile = var2;
         this.state = ServerLoginPacketListenerImpl.State.READY_TO_ACCEPT;
      } else {
         this.gameProfile = new GameProfile(null, var1.name());
         if (this.server.usesAuthentication() && !this.connection.isMemoryConnection()) {
            this.state = ServerLoginPacketListenerImpl.State.KEY;
            this.connection.send(new ClientboundHelloPacket("", this.server.getKeyPair().getPublic().getEncoded(), this.nonce));
         } else {
            this.state = ServerLoginPacketListenerImpl.State.READY_TO_ACCEPT;
         }
      }
   }

   public static boolean isValidUsername(String var0) {
      return var0.chars().filter(var0x -> var0x <= 32 || var0x >= 127).findAny().isEmpty();
   }

   @Override
   public void handleKey(ServerboundKeyPacket var1) {
      Validate.validState(this.state == ServerLoginPacketListenerImpl.State.KEY, "Unexpected key packet", new Object[0]);

      final String var2;
      try {
         PrivateKey var3 = this.server.getKeyPair().getPrivate();
         if (this.profilePublicKeyData != null) {
            ProfilePublicKey var4 = ProfilePublicKey.createTrusted(this.profilePublicKeyData);
            if (!var1.isChallengeSignatureValid(this.nonce, var4)) {
               throw new IllegalStateException("Protocol error");
            }
         } else if (!var1.isNonceValid(this.nonce, var3)) {
            throw new IllegalStateException("Protocol error");
         }

         SecretKey var9 = var1.getSecretKey(var3);
         Cipher var5 = Crypt.getCipher(2, var9);
         Cipher var6 = Crypt.getCipher(1, var9);
         var2 = new BigInteger(Crypt.digestData("", this.server.getKeyPair().getPublic(), var9)).toString(16);
         this.state = ServerLoginPacketListenerImpl.State.AUTHENTICATING;
         this.connection.setEncryptionKey(var5, var6);
      } catch (CryptException var7) {
         throw new IllegalStateException("Protocol error", var7);
      }

      Thread var8 = new Thread("User Authenticator #" + UNIQUE_THREAD_ID.incrementAndGet()) {
         @Override
         public void run() {
            GameProfile var1 = ServerLoginPacketListenerImpl.this.gameProfile;

            try {
               ServerLoginPacketListenerImpl.this.gameProfile = ServerLoginPacketListenerImpl.this.server
                  .getSessionService()
                  .hasJoinedServer(new GameProfile(null, var1.getName()), var2, this.getAddress());
               if (ServerLoginPacketListenerImpl.this.gameProfile != null) {
                  ServerLoginPacketListenerImpl.LOGGER
                     .info(
                        "UUID of player {} is {}",
                        ServerLoginPacketListenerImpl.this.gameProfile.getName(),
                        ServerLoginPacketListenerImpl.this.gameProfile.getId()
                     );
                  ServerLoginPacketListenerImpl.this.state = ServerLoginPacketListenerImpl.State.READY_TO_ACCEPT;
               } else if (ServerLoginPacketListenerImpl.this.server.isSingleplayer()) {
                  ServerLoginPacketListenerImpl.LOGGER.warn("Failed to verify username but will let them in anyway!");
                  ServerLoginPacketListenerImpl.this.gameProfile = var1;
                  ServerLoginPacketListenerImpl.this.state = ServerLoginPacketListenerImpl.State.READY_TO_ACCEPT;
               } else {
                  ServerLoginPacketListenerImpl.this.disconnect(Component.translatable("multiplayer.disconnect.unverified_username"));
                  ServerLoginPacketListenerImpl.LOGGER.error("Username '{}' tried to join with an invalid session", var1.getName());
               }
            } catch (AuthenticationUnavailableException var3) {
               if (ServerLoginPacketListenerImpl.this.server.isSingleplayer()) {
                  ServerLoginPacketListenerImpl.LOGGER.warn("Authentication servers are down but will let them in anyway!");
                  ServerLoginPacketListenerImpl.this.gameProfile = var1;
                  ServerLoginPacketListenerImpl.this.state = ServerLoginPacketListenerImpl.State.READY_TO_ACCEPT;
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
   public void handleCustomQueryPacket(ServerboundCustomQueryPacket var1) {
      this.disconnect(Component.translatable("multiplayer.disconnect.unexpected_query_response"));
   }

   protected GameProfile createFakeProfile(GameProfile var1) {
      UUID var2 = UUIDUtil.createOfflinePlayerUUID(var1.getName());
      return new GameProfile(var2, var1.getName());
   }

   static class PublicKeyValidationException extends ThrowingComponent {
      public PublicKeyValidationException(Component var1) {
         super(var1);
      }

      public PublicKeyValidationException(Component var1, Throwable var2) {
         super(var1, var2);
      }
   }

   static enum State {
      HELLO,
      KEY,
      AUTHENTICATING,
      NEGOTIATING,
      READY_TO_ACCEPT,
      DELAY_ACCEPT,
      ACCEPTED;

      private State() {
      }
   }
}