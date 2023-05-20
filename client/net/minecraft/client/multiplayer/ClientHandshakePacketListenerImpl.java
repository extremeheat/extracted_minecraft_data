package net.minecraft.client.multiplayer;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import com.mojang.authlib.exceptions.InsufficientPrivilegesException;
import com.mojang.authlib.exceptions.InvalidCredentialsException;
import com.mojang.authlib.exceptions.UserBannedException;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.logging.LogUtils;
import java.math.BigInteger;
import java.security.PublicKey;
import java.time.Duration;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.DisconnectedScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.Connection;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.PacketSendListener;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.login.ClientLoginPacketListener;
import net.minecraft.network.protocol.login.ClientboundCustomQueryPacket;
import net.minecraft.network.protocol.login.ClientboundGameProfilePacket;
import net.minecraft.network.protocol.login.ClientboundHelloPacket;
import net.minecraft.network.protocol.login.ClientboundLoginCompressionPacket;
import net.minecraft.network.protocol.login.ClientboundLoginDisconnectPacket;
import net.minecraft.network.protocol.login.ServerboundCustomQueryPacket;
import net.minecraft.network.protocol.login.ServerboundKeyPacket;
import net.minecraft.realms.DisconnectedRealmsScreen;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.util.Crypt;
import net.minecraft.util.HttpUtil;
import org.slf4j.Logger;

public class ClientHandshakePacketListenerImpl implements ClientLoginPacketListener {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final Minecraft minecraft;
   @Nullable
   private final ServerData serverData;
   @Nullable
   private final Screen parent;
   private final Consumer<Component> updateStatus;
   private final Connection connection;
   private GameProfile localGameProfile;
   private final boolean newWorld;
   @Nullable
   private final Duration worldLoadDuration;

   public ClientHandshakePacketListenerImpl(
      Connection var1, Minecraft var2, @Nullable ServerData var3, @Nullable Screen var4, boolean var5, @Nullable Duration var6, Consumer<Component> var7
   ) {
      super();
      this.connection = var1;
      this.minecraft = var2;
      this.serverData = var3;
      this.parent = var4;
      this.updateStatus = var7;
      this.newWorld = var5;
      this.worldLoadDuration = var6;
   }

   @Override
   public void handleHello(ClientboundHelloPacket var1) {
      Cipher var2;
      Cipher var3;
      String var4;
      ServerboundKeyPacket var5;
      try {
         SecretKey var6 = Crypt.generateSecretKey();
         PublicKey var7 = var1.getPublicKey();
         var4 = new BigInteger(Crypt.digestData(var1.getServerId(), var7, var6)).toString(16);
         var2 = Crypt.getCipher(2, var6);
         var3 = Crypt.getCipher(1, var6);
         byte[] var8 = var1.getChallenge();
         var5 = new ServerboundKeyPacket(var6, var7, var8);
      } catch (Exception var9) {
         throw new IllegalStateException("Protocol error", var9);
      }

      this.updateStatus.accept(Component.translatable("connect.authorizing"));
      HttpUtil.DOWNLOAD_EXECUTOR.submit(() -> {
         Component var5x = this.authenticateServer(var4);
         if (var5x != null) {
            if (this.serverData == null || !this.serverData.isLan()) {
               this.connection.disconnect(var5x);
               return;
            }

            LOGGER.warn(var5x.getString());
         }

         this.updateStatus.accept(Component.translatable("connect.encrypting"));
         this.connection.send(var5, PacketSendListener.thenRun(() -> this.connection.setEncryptionKey(var2, var3)));
      });
   }

   @Nullable
   private Component authenticateServer(String var1) {
      try {
         this.getMinecraftSessionService().joinServer(this.minecraft.getUser().getGameProfile(), this.minecraft.getUser().getAccessToken(), var1);
         return null;
      } catch (AuthenticationUnavailableException var3) {
         return Component.translatable("disconnect.loginFailedInfo", Component.translatable("disconnect.loginFailedInfo.serversUnavailable"));
      } catch (InvalidCredentialsException var4) {
         return Component.translatable("disconnect.loginFailedInfo", Component.translatable("disconnect.loginFailedInfo.invalidSession"));
      } catch (InsufficientPrivilegesException var5) {
         return Component.translatable("disconnect.loginFailedInfo", Component.translatable("disconnect.loginFailedInfo.insufficientPrivileges"));
      } catch (UserBannedException var6) {
         return Component.translatable("disconnect.loginFailedInfo", Component.translatable("disconnect.loginFailedInfo.userBanned"));
      } catch (AuthenticationException var7) {
         return Component.translatable("disconnect.loginFailedInfo", var7.getMessage());
      }
   }

   private MinecraftSessionService getMinecraftSessionService() {
      return this.minecraft.getMinecraftSessionService();
   }

   @Override
   public void handleGameProfile(ClientboundGameProfilePacket var1) {
      this.updateStatus.accept(Component.translatable("connect.joining"));
      this.localGameProfile = var1.getGameProfile();
      this.connection.setProtocol(ConnectionProtocol.PLAY);
      this.connection
         .setListener(
            new ClientPacketListener(
               this.minecraft,
               this.parent,
               this.connection,
               this.serverData,
               this.localGameProfile,
               this.minecraft.getTelemetryManager().createWorldSessionManager(this.newWorld, this.worldLoadDuration)
            )
         );
   }

   @Override
   public void onDisconnect(Component var1) {
      if (this.parent != null && this.parent instanceof RealmsScreen) {
         this.minecraft.setScreen(new DisconnectedRealmsScreen(this.parent, CommonComponents.CONNECT_FAILED, var1));
      } else {
         this.minecraft.setScreen(new DisconnectedScreen(this.parent, CommonComponents.CONNECT_FAILED, var1));
      }
   }

   @Override
   public boolean isAcceptingMessages() {
      return this.connection.isConnected();
   }

   @Override
   public void handleDisconnect(ClientboundLoginDisconnectPacket var1) {
      this.connection.disconnect(var1.getReason());
   }

   @Override
   public void handleCompression(ClientboundLoginCompressionPacket var1) {
      if (!this.connection.isMemoryConnection()) {
         this.connection.setupCompression(var1.getCompressionThreshold(), false);
      }
   }

   @Override
   public void handleCustomQuery(ClientboundCustomQueryPacket var1) {
      this.updateStatus.accept(Component.translatable("connect.negotiating"));
      this.connection.send(new ServerboundCustomQueryPacket(var1.getTransactionId(), null));
   }
}
