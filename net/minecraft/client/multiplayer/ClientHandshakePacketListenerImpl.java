package net.minecraft.client.multiplayer;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import com.mojang.authlib.exceptions.InvalidCredentialsException;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import java.math.BigInteger;
import java.security.PublicKey;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import javax.crypto.SecretKey;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.DisconnectedScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.Connection;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.login.ClientLoginPacketListener;
import net.minecraft.network.protocol.login.ClientboundCustomQueryPacket;
import net.minecraft.network.protocol.login.ClientboundGameProfilePacket;
import net.minecraft.network.protocol.login.ClientboundHelloPacket;
import net.minecraft.network.protocol.login.ClientboundLoginCompressionPacket;
import net.minecraft.network.protocol.login.ClientboundLoginDisconnectPacket;
import net.minecraft.network.protocol.login.ServerboundCustomQueryPacket;
import net.minecraft.network.protocol.login.ServerboundKeyPacket;
import net.minecraft.realms.DisconnectedRealmsScreen;
import net.minecraft.realms.RealmsScreenProxy;
import net.minecraft.util.Crypt;
import net.minecraft.util.HttpUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ClientHandshakePacketListenerImpl implements ClientLoginPacketListener {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Minecraft minecraft;
   @Nullable
   private final Screen parent;
   private final Consumer updateStatus;
   private final Connection connection;
   private GameProfile localGameProfile;

   public ClientHandshakePacketListenerImpl(Connection var1, Minecraft var2, @Nullable Screen var3, Consumer var4) {
      this.connection = var1;
      this.minecraft = var2;
      this.parent = var3;
      this.updateStatus = var4;
   }

   public void handleHello(ClientboundHelloPacket var1) {
      SecretKey var2 = Crypt.generateSecretKey();
      PublicKey var3 = var1.getPublicKey();
      String var4 = (new BigInteger(Crypt.digestData(var1.getServerId(), var3, var2))).toString(16);
      ServerboundKeyPacket var5 = new ServerboundKeyPacket(var2, var3, var1.getNonce());
      this.updateStatus.accept(new TranslatableComponent("connect.authorizing", new Object[0]));
      HttpUtil.DOWNLOAD_EXECUTOR.submit(() -> {
         Component var4x = this.authenticateServer(var4);
         if (var4x != null) {
            if (this.minecraft.getCurrentServer() == null || !this.minecraft.getCurrentServer().isLan()) {
               this.connection.disconnect(var4x);
               return;
            }

            LOGGER.warn(var4x.getString());
         }

         this.updateStatus.accept(new TranslatableComponent("connect.encrypting", new Object[0]));
         this.connection.send(var5, (var2x) -> {
            this.connection.setEncryptionKey(var2);
         });
      });
   }

   @Nullable
   private Component authenticateServer(String var1) {
      try {
         this.getMinecraftSessionService().joinServer(this.minecraft.getUser().getGameProfile(), this.minecraft.getUser().getAccessToken(), var1);
         return null;
      } catch (AuthenticationUnavailableException var3) {
         return new TranslatableComponent("disconnect.loginFailedInfo", new Object[]{new TranslatableComponent("disconnect.loginFailedInfo.serversUnavailable", new Object[0])});
      } catch (InvalidCredentialsException var4) {
         return new TranslatableComponent("disconnect.loginFailedInfo", new Object[]{new TranslatableComponent("disconnect.loginFailedInfo.invalidSession", new Object[0])});
      } catch (AuthenticationException var5) {
         return new TranslatableComponent("disconnect.loginFailedInfo", new Object[]{var5.getMessage()});
      }
   }

   private MinecraftSessionService getMinecraftSessionService() {
      return this.minecraft.getMinecraftSessionService();
   }

   public void handleGameProfile(ClientboundGameProfilePacket var1) {
      this.updateStatus.accept(new TranslatableComponent("connect.joining", new Object[0]));
      this.localGameProfile = var1.getGameProfile();
      this.connection.setProtocol(ConnectionProtocol.PLAY);
      this.connection.setListener(new ClientPacketListener(this.minecraft, this.parent, this.connection, this.localGameProfile));
   }

   public void onDisconnect(Component var1) {
      if (this.parent != null && this.parent instanceof RealmsScreenProxy) {
         this.minecraft.setScreen((new DisconnectedRealmsScreen(((RealmsScreenProxy)this.parent).getScreen(), "connect.failed", var1)).getProxy());
      } else {
         this.minecraft.setScreen(new DisconnectedScreen(this.parent, "connect.failed", var1));
      }

   }

   public Connection getConnection() {
      return this.connection;
   }

   public void handleDisconnect(ClientboundLoginDisconnectPacket var1) {
      this.connection.disconnect(var1.getReason());
   }

   public void handleCompression(ClientboundLoginCompressionPacket var1) {
      if (!this.connection.isMemoryConnection()) {
         this.connection.setupCompression(var1.getCompressionThreshold());
      }

   }

   public void handleCustomQuery(ClientboundCustomQueryPacket var1) {
      this.updateStatus.accept(new TranslatableComponent("connect.negotiating", new Object[0]));
      this.connection.send(new ServerboundCustomQueryPacket(var1.getTransactionId(), (FriendlyByteBuf)null));
   }
}
