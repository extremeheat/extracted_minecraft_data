package net.minecraft.client.multiplayer;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import com.mojang.authlib.exceptions.ForcedUsernameChangeException;
import com.mojang.authlib.exceptions.InsufficientPrivilegesException;
import com.mojang.authlib.exceptions.InvalidCredentialsException;
import com.mojang.authlib.exceptions.UserBannedException;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.logging.LogUtils;
import java.math.BigInteger;
import java.security.PublicKey;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.CrashReportDetail;
import net.minecraft.Util;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.gui.screens.DisconnectedScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.Connection;
import net.minecraft.network.DisconnectionDetails;
import net.minecraft.network.PacketSendListener;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.ServerboundClientInformationPacket;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.BrandPayload;
import net.minecraft.network.protocol.configuration.ConfigurationProtocols;
import net.minecraft.network.protocol.cookie.ClientboundCookieRequestPacket;
import net.minecraft.network.protocol.cookie.ServerboundCookieResponsePacket;
import net.minecraft.network.protocol.login.ClientLoginPacketListener;
import net.minecraft.network.protocol.login.ClientboundCustomQueryPacket;
import net.minecraft.network.protocol.login.ClientboundHelloPacket;
import net.minecraft.network.protocol.login.ClientboundLoginCompressionPacket;
import net.minecraft.network.protocol.login.ClientboundLoginDisconnectPacket;
import net.minecraft.network.protocol.login.ClientboundLoginFinishedPacket;
import net.minecraft.network.protocol.login.ServerboundCustomQueryAnswerPacket;
import net.minecraft.network.protocol.login.ServerboundKeyPacket;
import net.minecraft.network.protocol.login.ServerboundLoginAcknowledgedPacket;
import net.minecraft.network.protocol.login.custom.CustomQueryAnswerPayload;
import net.minecraft.realms.DisconnectedRealmsScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ServerLinks;
import net.minecraft.util.Crypt;
import net.minecraft.world.flag.FeatureFlags;
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
   private final boolean newWorld;
   @Nullable
   private final Duration worldLoadDuration;
   @Nullable
   private String minigameName;
   private final Map<ResourceLocation, byte[]> cookies;
   private final boolean wasTransferredTo;
   private final AtomicReference<State> state;

   public ClientHandshakePacketListenerImpl(Connection var1, Minecraft var2, @Nullable ServerData var3, @Nullable Screen var4, boolean var5, @Nullable Duration var6, Consumer<Component> var7, @Nullable TransferState var8) {
      super();
      this.state = new AtomicReference(ClientHandshakePacketListenerImpl.State.CONNECTING);
      this.connection = var1;
      this.minecraft = var2;
      this.serverData = var3;
      this.parent = var4;
      this.updateStatus = var7;
      this.newWorld = var5;
      this.worldLoadDuration = var6;
      this.cookies = var8 != null ? new HashMap(var8.cookies()) : new HashMap();
      this.wasTransferredTo = var8 != null;
   }

   private void switchState(State var1) {
      State var2 = (State)this.state.updateAndGet((var1x) -> {
         if (!var1.fromStates.contains(var1x)) {
            String var10002 = String.valueOf(var1);
            throw new IllegalStateException("Tried to switch to " + var10002 + " from " + String.valueOf(var1x) + ", but expected one of " + String.valueOf(var1.fromStates));
         } else {
            return var1;
         }
      });
      this.updateStatus.accept(var2.message);
   }

   public void handleHello(ClientboundHelloPacket var1) {
      this.switchState(ClientHandshakePacketListenerImpl.State.AUTHORIZING);

      Cipher var2;
      Cipher var3;
      String var4;
      ServerboundKeyPacket var5;
      try {
         SecretKey var6 = Crypt.generateSecretKey();
         PublicKey var7 = var1.getPublicKey();
         var4 = (new BigInteger(Crypt.digestData(var1.getServerId(), var7, var6))).toString(16);
         var2 = Crypt.getCipher(2, var6);
         var3 = Crypt.getCipher(1, var6);
         byte[] var8 = var1.getChallenge();
         var5 = new ServerboundKeyPacket(var6, var7, var8);
      } catch (Exception var9) {
         throw new IllegalStateException("Protocol error", var9);
      }

      if (var1.shouldAuthenticate()) {
         Util.ioPool().execute(() -> {
            Component var5x = this.authenticateServer(var4);
            if (var5x != null) {
               if (this.serverData == null || !this.serverData.isLan()) {
                  this.connection.disconnect(var5x);
                  return;
               }

               LOGGER.warn(var5x.getString());
            }

            this.setEncryption(var5, var2, var3);
         });
      } else {
         this.setEncryption(var5, var2, var3);
      }

   }

   private void setEncryption(ServerboundKeyPacket var1, Cipher var2, Cipher var3) {
      this.switchState(ClientHandshakePacketListenerImpl.State.ENCRYPTING);
      this.connection.send(var1, PacketSendListener.thenRun(() -> this.connection.setEncryptionKey(var2, var3)));
   }

   @Nullable
   private Component authenticateServer(String var1) {
      try {
         this.getMinecraftSessionService().joinServer(this.minecraft.getUser().getProfileId(), this.minecraft.getUser().getAccessToken(), var1);
         return null;
      } catch (AuthenticationUnavailableException var3) {
         return Component.translatable("disconnect.loginFailedInfo", Component.translatable("disconnect.loginFailedInfo.serversUnavailable"));
      } catch (InvalidCredentialsException var4) {
         return Component.translatable("disconnect.loginFailedInfo", Component.translatable("disconnect.loginFailedInfo.invalidSession"));
      } catch (InsufficientPrivilegesException var5) {
         return Component.translatable("disconnect.loginFailedInfo", Component.translatable("disconnect.loginFailedInfo.insufficientPrivileges"));
      } catch (ForcedUsernameChangeException | UserBannedException var6) {
         return Component.translatable("disconnect.loginFailedInfo", Component.translatable("disconnect.loginFailedInfo.userBanned"));
      } catch (AuthenticationException var7) {
         return Component.translatable("disconnect.loginFailedInfo", var7.getMessage());
      }
   }

   private MinecraftSessionService getMinecraftSessionService() {
      return this.minecraft.getMinecraftSessionService();
   }

   public void handleLoginFinished(ClientboundLoginFinishedPacket var1) {
      this.switchState(ClientHandshakePacketListenerImpl.State.JOINING);
      GameProfile var2 = var1.gameProfile();
      this.connection.setupInboundProtocol(ConfigurationProtocols.CLIENTBOUND, new ClientConfigurationPacketListenerImpl(this.minecraft, this.connection, new CommonListenerCookie(var2, this.minecraft.getTelemetryManager().createWorldSessionManager(this.newWorld, this.worldLoadDuration, this.minigameName), ClientRegistryLayer.createRegistryAccess().compositeAccess(), FeatureFlags.DEFAULT_FLAGS, (String)null, this.serverData, this.parent, this.cookies, (ChatComponent.State)null, Map.of(), ServerLinks.EMPTY)));
      this.connection.send(ServerboundLoginAcknowledgedPacket.INSTANCE);
      this.connection.setupOutboundProtocol(ConfigurationProtocols.SERVERBOUND);
      this.connection.send(new ServerboundCustomPayloadPacket(new BrandPayload(ClientBrandRetriever.getClientModName())));
      this.connection.send(new ServerboundClientInformationPacket(this.minecraft.options.buildPlayerInformation()));
   }

   public void onDisconnect(DisconnectionDetails var1) {
      Component var2 = this.wasTransferredTo ? CommonComponents.TRANSFER_CONNECT_FAILED : CommonComponents.CONNECT_FAILED;
      if (this.serverData != null && this.serverData.isRealm()) {
         this.minecraft.setScreen(new DisconnectedRealmsScreen(this.parent, var2, var1.reason()));
      } else {
         this.minecraft.setScreen(new DisconnectedScreen(this.parent, var2, var1));
      }

   }

   public boolean isAcceptingMessages() {
      return this.connection.isConnected();
   }

   public void handleDisconnect(ClientboundLoginDisconnectPacket var1) {
      this.connection.disconnect(var1.getReason());
   }

   public void handleCompression(ClientboundLoginCompressionPacket var1) {
      if (!this.connection.isMemoryConnection()) {
         this.connection.setupCompression(var1.getCompressionThreshold(), false);
      }

   }

   public void handleCustomQuery(ClientboundCustomQueryPacket var1) {
      this.updateStatus.accept(Component.translatable("connect.negotiating"));
      this.connection.send(new ServerboundCustomQueryAnswerPacket(var1.transactionId(), (CustomQueryAnswerPayload)null));
   }

   public void setMinigameName(@Nullable String var1) {
      this.minigameName = var1;
   }

   public void handleRequestCookie(ClientboundCookieRequestPacket var1) {
      this.connection.send(new ServerboundCookieResponsePacket(var1.key(), (byte[])this.cookies.get(var1.key())));
   }

   public void fillListenerSpecificCrashDetails(CrashReport var1, CrashReportCategory var2) {
      var2.setDetail("Server type", (CrashReportDetail)(() -> this.serverData != null ? this.serverData.type().toString() : "<unknown>"));
      var2.setDetail("Login phase", (CrashReportDetail)(() -> ((State)this.state.get()).toString()));
      var2.setDetail("Is Local", (CrashReportDetail)(() -> String.valueOf(this.connection.isMemoryConnection())));
   }

   static enum State {
      CONNECTING(Component.translatable("connect.connecting"), Set.of()),
      AUTHORIZING(Component.translatable("connect.authorizing"), Set.of(CONNECTING)),
      ENCRYPTING(Component.translatable("connect.encrypting"), Set.of(AUTHORIZING)),
      JOINING(Component.translatable("connect.joining"), Set.of(ENCRYPTING, CONNECTING));

      final Component message;
      final Set<State> fromStates;

      private State(final Component var3, final Set<State> var4) {
         this.message = var3;
         this.fromStates = var4;
      }

      // $FF: synthetic method
      private static State[] $values() {
         return new State[]{CONNECTING, AUTHORIZING, ENCRYPTING, JOINING};
      }
   }
}
