package net.minecraft.server.network;

import com.google.common.primitives.Ints;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import com.mojang.authlib.minecraft.InsecurePublicKeyException;
import com.mojang.logging.LogUtils;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.security.PrivateKey;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nullable;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import net.minecraft.DefaultUncaughtExceptionHandler;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.Connection;
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

public class ServerLoginPacketListenerImpl implements ServerLoginPacketListener {
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
   State state;
   private int tick;
   @Nullable
   GameProfile gameProfile;
   private final String serverId;
   @Nullable
   private ServerPlayer delayedAcceptPlayer;
   @Nullable
   private ProfilePublicKey playerProfilePublicKey;

   public ServerLoginPacketListenerImpl(MinecraftServer var1, Connection var2) {
      super();
      this.state = ServerLoginPacketListenerImpl.State.HELLO;
      this.serverId = "";
      this.server = var1;
      this.connection = var2;
      this.nonce = Ints.toByteArray(RANDOM.nextInt());
   }

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
      if (!this.gameProfile.isComplete()) {
         this.gameProfile = this.createFakeProfile(this.gameProfile);
      }

      Component var1 = this.server.getPlayerList().canPlayerLogin(this.connection.getRemoteAddress(), this.gameProfile);
      if (var1 != null) {
         this.disconnect(var1);
      } else {
         this.state = ServerLoginPacketListenerImpl.State.ACCEPTED;
         if (this.server.getCompressionThreshold() >= 0 && !this.connection.isMemoryConnection()) {
            this.connection.send(new ClientboundLoginCompressionPacket(this.server.getCompressionThreshold()), (var1x) -> {
               this.connection.setupCompression(this.server.getCompressionThreshold(), true);
            });
         }

         this.connection.send(new ClientboundGameProfilePacket(this.gameProfile));
         ServerPlayer var2 = this.server.getPlayerList().getPlayer(this.gameProfile.getId());

         try {
            ServerPlayer var3 = this.server.getPlayerList().getPlayerForLogin(this.gameProfile, this.playerProfilePublicKey);
            if (var2 != null) {
               this.state = ServerLoginPacketListenerImpl.State.DELAY_ACCEPT;
               this.delayedAcceptPlayer = var3;
            } else {
               this.placeNewPlayer(var3);
            }
         } catch (Exception var5) {
            LOGGER.error("Couldn't place player in world", var5);
            MutableComponent var4 = Component.translatable("multiplayer.disconnect.invalid_player_data");
            this.connection.send(new ClientboundDisconnectPacket(var4));
            this.connection.disconnect(var4);
         }
      }

   }

   private void placeNewPlayer(ServerPlayer var1) {
      this.server.getPlayerList().placeNewPlayer(this.connection, var1);
   }

   public void onDisconnect(Component var1) {
      LOGGER.info("{} lost connection: {}", this.getUserName(), var1.getString());
   }

   public String getUserName() {
      if (this.gameProfile != null) {
         GameProfile var10000 = this.gameProfile;
         return "" + var10000 + " (" + this.connection.getRemoteAddress() + ")";
      } else {
         return String.valueOf(this.connection.getRemoteAddress());
      }
   }

   @Nullable
   private static ProfilePublicKey validatePublicKey(ServerboundHelloPacket var0, SignatureValidator var1, boolean var2) throws PublicKeyParseException {
      try {
         Optional var3 = var0.publicKey();
         if (var3.isEmpty()) {
            if (var2) {
               throw new PublicKeyParseException(MISSING_PROFILE_PUBLIC_KEY);
            } else {
               return null;
            }
         } else {
            return ProfilePublicKey.createValidated(var1, (ProfilePublicKey.Data)var3.get());
         }
      } catch (InsecurePublicKeyException.MissingException var4) {
         if (var2) {
            throw new PublicKeyParseException(INVALID_SIGNATURE, var4);
         } else {
            return null;
         }
      } catch (CryptException var5) {
         throw new PublicKeyParseException(INVALID_PUBLIC_KEY, var5);
      } catch (Exception var6) {
         throw new PublicKeyParseException(INVALID_SIGNATURE, var6);
      }
   }

   public void handleHello(ServerboundHelloPacket var1) {
      Validate.validState(this.state == ServerLoginPacketListenerImpl.State.HELLO, "Unexpected hello packet", new Object[0]);
      Validate.validState(isValidUsername(var1.name()), "Invalid characters in username", new Object[0]);

      try {
         this.playerProfilePublicKey = validatePublicKey(var1, this.server.getServiceSignatureValidator(), this.server.enforceSecureProfile());
      } catch (PublicKeyParseException var3) {
         LOGGER.error(var3.getMessage(), var3.getCause());
         if (!this.connection.isMemoryConnection()) {
            this.disconnect(var3.getComponent());
            return;
         }
      }

      GameProfile var2 = this.server.getSingleplayerProfile();
      if (var2 != null && var1.name().equalsIgnoreCase(var2.getName())) {
         this.gameProfile = var2;
         this.state = ServerLoginPacketListenerImpl.State.READY_TO_ACCEPT;
      } else {
         this.gameProfile = new GameProfile((UUID)null, var1.name());
         if (this.server.usesAuthentication() && !this.connection.isMemoryConnection()) {
            this.state = ServerLoginPacketListenerImpl.State.KEY;
            this.connection.send(new ClientboundHelloPacket("", this.server.getKeyPair().getPublic().getEncoded(), this.nonce));
         } else {
            this.state = ServerLoginPacketListenerImpl.State.READY_TO_ACCEPT;
         }

      }
   }

   public static boolean isValidUsername(String var0) {
      return var0.chars().filter((var0x) -> {
         return var0x <= 32 || var0x >= 127;
      }).findAny().isEmpty();
   }

   public void handleKey(ServerboundKeyPacket var1) {
      Validate.validState(this.state == ServerLoginPacketListenerImpl.State.KEY, "Unexpected key packet", new Object[0]);

      final String var2;
      try {
         PrivateKey var3 = this.server.getKeyPair().getPrivate();
         if (this.playerProfilePublicKey != null) {
            if (!var1.isChallengeSignatureValid(this.nonce, this.playerProfilePublicKey)) {
               throw new IllegalStateException("Protocol error");
            }
         } else if (!var1.isNonceValid(this.nonce, var3)) {
            throw new IllegalStateException("Protocol error");
         }

         SecretKey var4 = var1.getSecretKey(var3);
         Cipher var5 = Crypt.getCipher(2, var4);
         Cipher var6 = Crypt.getCipher(1, var4);
         var2 = (new BigInteger(Crypt.digestData("", this.server.getKeyPair().getPublic(), var4))).toString(16);
         this.state = ServerLoginPacketListenerImpl.State.AUTHENTICATING;
         this.connection.setEncryptionKey(var5, var6);
      } catch (CryptException var7) {
         throw new IllegalStateException("Protocol error", var7);
      }

      Thread var8 = new Thread("User Authenticator #" + UNIQUE_THREAD_ID.incrementAndGet()) {
         public void run() {
            GameProfile var1 = ServerLoginPacketListenerImpl.this.gameProfile;

            try {
               ServerLoginPacketListenerImpl.this.gameProfile = ServerLoginPacketListenerImpl.this.server.getSessionService().hasJoinedServer(new GameProfile((UUID)null, var1.getName()), var2, this.getAddress());
               if (ServerLoginPacketListenerImpl.this.gameProfile != null) {
                  ServerLoginPacketListenerImpl.LOGGER.info("UUID of player {} is {}", ServerLoginPacketListenerImpl.this.gameProfile.getName(), ServerLoginPacketListenerImpl.this.gameProfile.getId());
                  ServerLoginPacketListenerImpl.this.state = ServerLoginPacketListenerImpl.State.READY_TO_ACCEPT;
               } else if (ServerLoginPacketListenerImpl.this.server.isSingleplayer()) {
                  ServerLoginPacketListenerImpl.LOGGER.warn("Failed to verify username but will let them in anyway!");
                  ServerLoginPacketListenerImpl.this.gameProfile = ServerLoginPacketListenerImpl.this.createFakeProfile(var1);
                  ServerLoginPacketListenerImpl.this.state = ServerLoginPacketListenerImpl.State.READY_TO_ACCEPT;
               } else {
                  ServerLoginPacketListenerImpl.this.disconnect(Component.translatable("multiplayer.disconnect.unverified_username"));
                  ServerLoginPacketListenerImpl.LOGGER.error("Username '{}' tried to join with an invalid session", var1.getName());
               }
            } catch (AuthenticationUnavailableException var3) {
               if (ServerLoginPacketListenerImpl.this.server.isSingleplayer()) {
                  ServerLoginPacketListenerImpl.LOGGER.warn("Authentication servers are down but will let them in anyway!");
                  ServerLoginPacketListenerImpl.this.gameProfile = ServerLoginPacketListenerImpl.this.createFakeProfile(var1);
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
            return ServerLoginPacketListenerImpl.this.server.getPreventProxyConnections() && var1 instanceof InetSocketAddress ? ((InetSocketAddress)var1).getAddress() : null;
         }
      };
      var8.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(LOGGER));
      var8.start();
   }

   public void handleCustomQueryPacket(ServerboundCustomQueryPacket var1) {
      this.disconnect(Component.translatable("multiplayer.disconnect.unexpected_query_response"));
   }

   protected GameProfile createFakeProfile(GameProfile var1) {
      UUID var2 = UUIDUtil.createOfflinePlayerUUID(var1.getName());
      return new GameProfile(var2, var1.getName());
   }

   private static enum State {
      HELLO,
      KEY,
      AUTHENTICATING,
      NEGOTIATING,
      READY_TO_ACCEPT,
      DELAY_ACCEPT,
      ACCEPTED;

      private State() {
      }

      // $FF: synthetic method
      private static State[] $values() {
         return new State[]{HELLO, KEY, AUTHENTICATING, NEGOTIATING, READY_TO_ACCEPT, DELAY_ACCEPT, ACCEPTED};
      }
   }

   static class PublicKeyParseException extends ThrowingComponent {
      public PublicKeyParseException(Component var1) {
         super(var1);
      }

      public PublicKeyParseException(Component var1, Throwable var2) {
         super(var1, var2);
      }
   }
}
