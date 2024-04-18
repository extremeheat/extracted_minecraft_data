package net.minecraft.client.multiplayer;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.BooleanSupplier;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.CrashReportCategory;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.gui.screens.DisconnectedScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.client.resources.server.DownloadedPackSource;
import net.minecraft.client.telemetry.WorldSessionTelemetryManager;
import net.minecraft.network.Connection;
import net.minecraft.network.ServerboundPacketListener;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketUtils;
import net.minecraft.network.protocol.common.ClientCommonPacketListener;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.ClientboundDisconnectPacket;
import net.minecraft.network.protocol.common.ClientboundKeepAlivePacket;
import net.minecraft.network.protocol.common.ClientboundPingPacket;
import net.minecraft.network.protocol.common.ClientboundResourcePackPopPacket;
import net.minecraft.network.protocol.common.ClientboundResourcePackPushPacket;
import net.minecraft.network.protocol.common.ClientboundStoreCookiePacket;
import net.minecraft.network.protocol.common.ClientboundTransferPacket;
import net.minecraft.network.protocol.common.ServerboundKeepAlivePacket;
import net.minecraft.network.protocol.common.ServerboundPongPacket;
import net.minecraft.network.protocol.common.ServerboundResourcePackPacket;
import net.minecraft.network.protocol.common.custom.BrandPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.DiscardedPayload;
import net.minecraft.network.protocol.cookie.ClientboundCookieRequestPacket;
import net.minecraft.network.protocol.cookie.ServerboundCookieResponsePacket;
import net.minecraft.realms.DisconnectedRealmsScreen;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

public abstract class ClientCommonPacketListenerImpl implements ClientCommonPacketListener {
   private static final Component GENERIC_DISCONNECT_MESSAGE = Component.translatable("disconnect.lost");
   private static final Logger LOGGER = LogUtils.getLogger();
   protected final Minecraft minecraft;
   protected final Connection connection;
   @Nullable
   protected final ServerData serverData;
   @Nullable
   protected String serverBrand;
   protected final WorldSessionTelemetryManager telemetryManager;
   @Nullable
   protected final Screen postDisconnectScreen;
   protected boolean isTransferring;
   @Deprecated(
      forRemoval = true
   )
   protected final boolean strictErrorHandling;
   private final List<ClientCommonPacketListenerImpl.DeferredPacket> deferredPackets = new ArrayList<>();
   protected final Map<ResourceLocation, byte[]> serverCookies;

   protected ClientCommonPacketListenerImpl(Minecraft var1, Connection var2, CommonListenerCookie var3) {
      super();
      this.minecraft = var1;
      this.connection = var2;
      this.serverData = var3.serverData();
      this.serverBrand = var3.serverBrand();
      this.telemetryManager = var3.telemetryManager();
      this.postDisconnectScreen = var3.postDisconnectScreen();
      this.serverCookies = var3.serverCookies();
      this.strictErrorHandling = var3.strictErrorHandling();
   }

   @Override
   public void onPacketError(Packet var1, Exception var2) {
      LOGGER.error("Failed to handle packet {}", var1, var2);
      if (this.strictErrorHandling) {
         this.connection.disconnect(Component.translatable("disconnect.packetError"));
      }
   }

   @Override
   public boolean shouldHandleMessage(Packet<?> var1) {
      return ClientCommonPacketListener.super.shouldHandleMessage(var1)
         ? true
         : this.isTransferring && (var1 instanceof ClientboundStoreCookiePacket || var1 instanceof ClientboundTransferPacket);
   }

   @Override
   public void handleKeepAlive(ClientboundKeepAlivePacket var1) {
      this.sendWhen(new ServerboundKeepAlivePacket(var1.getId()), () -> !RenderSystem.isFrozenAtPollEvents(), Duration.ofMinutes(1L));
   }

   @Override
   public void handlePing(ClientboundPingPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      this.send(new ServerboundPongPacket(var1.getId()));
   }

   @Override
   public void handleCustomPayload(ClientboundCustomPayloadPacket var1) {
      CustomPacketPayload var2 = var1.payload();
      if (!(var2 instanceof DiscardedPayload)) {
         PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
         if (var2 instanceof BrandPayload var3) {
            this.serverBrand = var3.brand();
            this.telemetryManager.onServerBrandReceived(var3.brand());
         } else {
            this.handleCustomPayload(var2);
         }
      }
   }

   protected abstract void handleCustomPayload(CustomPacketPayload var1);

   @Override
   public void handleResourcePackPush(ClientboundResourcePackPushPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      UUID var2 = var1.id();
      URL var3 = parseResourcePackUrl(var1.url());
      if (var3 == null) {
         this.connection.send(new ServerboundResourcePackPacket(var2, ServerboundResourcePackPacket.Action.INVALID_URL));
      } else {
         String var4 = var1.hash();
         boolean var5 = var1.required();
         ServerData.ServerPackStatus var6 = this.serverData != null ? this.serverData.getResourcePackStatus() : ServerData.ServerPackStatus.PROMPT;
         if (var6 != ServerData.ServerPackStatus.PROMPT && (!var5 || var6 != ServerData.ServerPackStatus.DISABLED)) {
            this.minecraft.getDownloadedPackSource().pushPack(var2, var3, var4);
         } else {
            this.minecraft.setScreen(this.addOrUpdatePackPrompt(var2, var3, var4, var5, var1.prompt().orElse(null)));
         }
      }
   }

   @Override
   public void handleResourcePackPop(ClientboundResourcePackPopPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      var1.id().ifPresentOrElse(var1x -> this.minecraft.getDownloadedPackSource().popPack(var1x), () -> this.minecraft.getDownloadedPackSource().popAll());
   }

   static Component preparePackPrompt(Component var0, @Nullable Component var1) {
      return (Component)(var1 == null ? var0 : Component.translatable("multiplayer.texturePrompt.serverPrompt", var0, var1));
   }

   @Nullable
   private static URL parseResourcePackUrl(String var0) {
      try {
         URL var1 = new URL(var0);
         String var2 = var1.getProtocol();
         return !"http".equals(var2) && !"https".equals(var2) ? null : var1;
      } catch (MalformedURLException var3) {
         return null;
      }
   }

   @Override
   public void handleRequestCookie(ClientboundCookieRequestPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      this.connection.send(new ServerboundCookieResponsePacket(var1.key(), this.serverCookies.get(var1.key())));
   }

   @Override
   public void handleStoreCookie(ClientboundStoreCookiePacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      this.serverCookies.put(var1.key(), var1.payload());
   }

   @Override
   public void handleTransfer(ClientboundTransferPacket var1) {
      this.isTransferring = true;
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      if (this.serverData == null) {
         throw new IllegalStateException("Cannot transfer to server from singleplayer");
      } else {
         this.connection.disconnect(Component.translatable("disconnect.transfer"));
         this.connection.setReadOnly();
         this.connection.handleDisconnection();
         ServerAddress var2 = new ServerAddress(var1.host(), var1.port());
         ConnectScreen.startConnecting(
            Objects.requireNonNullElseGet(this.postDisconnectScreen, TitleScreen::new),
            this.minecraft,
            var2,
            this.serverData,
            false,
            new TransferState(this.serverCookies)
         );
      }
   }

   @Override
   public void handleDisconnect(ClientboundDisconnectPacket var1) {
      this.connection.disconnect(var1.reason());
   }

   protected void sendDeferredPackets() {
      Iterator var1 = this.deferredPackets.iterator();

      while (var1.hasNext()) {
         ClientCommonPacketListenerImpl.DeferredPacket var2 = (ClientCommonPacketListenerImpl.DeferredPacket)var1.next();
         if (var2.sendCondition().getAsBoolean()) {
            this.send(var2.packet);
            var1.remove();
         } else if (var2.expirationTime() <= Util.getMillis()) {
            var1.remove();
         }
      }
   }

   public void send(Packet<?> var1) {
      this.connection.send(var1);
   }

   @Override
   public void onDisconnect(Component var1) {
      this.telemetryManager.onDisconnect();
      this.minecraft.disconnect(this.createDisconnectScreen(var1), this.isTransferring);
      LOGGER.warn("Client disconnected with reason: {}", var1.getString());
   }

   @Override
   public void fillListenerSpecificCrashDetails(CrashReportCategory var1) {
      var1.setDetail("Server type", () -> this.serverData != null ? this.serverData.type().toString() : "<none>");
      var1.setDetail("Server brand", () -> this.serverBrand);
   }

   protected Screen createDisconnectScreen(Component var1) {
      Screen var2 = Objects.requireNonNullElseGet(this.postDisconnectScreen, () -> new JoinMultiplayerScreen(new TitleScreen()));
      return (Screen)(this.serverData != null && this.serverData.isRealm()
         ? new DisconnectedRealmsScreen(var2, GENERIC_DISCONNECT_MESSAGE, var1)
         : new DisconnectedScreen(var2, GENERIC_DISCONNECT_MESSAGE, var1));
   }

   @Nullable
   public String serverBrand() {
      return this.serverBrand;
   }

   private void sendWhen(Packet<? extends ServerboundPacketListener> var1, BooleanSupplier var2, Duration var3) {
      if (var2.getAsBoolean()) {
         this.send(var1);
      } else {
         this.deferredPackets.add(new ClientCommonPacketListenerImpl.DeferredPacket(var1, var2, Util.getMillis() + var3.toMillis()));
      }
   }

   private Screen addOrUpdatePackPrompt(UUID var1, URL var2, String var3, boolean var4, @Nullable Component var5) {
      Screen var6 = this.minecraft.screen;
      return var6 instanceof ClientCommonPacketListenerImpl.PackConfirmScreen var7
         ? var7.update(this.minecraft, var1, var2, var3, var4, var5)
         : new ClientCommonPacketListenerImpl.PackConfirmScreen(
            this.minecraft, var6, List.of(new ClientCommonPacketListenerImpl.PackConfirmScreen.PendingRequest(var1, var2, var3)), var4, var5
         );
   }

   static record DeferredPacket(Packet<? extends ServerboundPacketListener> packet, BooleanSupplier sendCondition, long expirationTime) {

      DeferredPacket(Packet<? extends ServerboundPacketListener> packet, BooleanSupplier sendCondition, long expirationTime) {
         super();
         this.packet = packet;
         this.sendCondition = sendCondition;
         this.expirationTime = expirationTime;
      }
   }

   class PackConfirmScreen extends ConfirmScreen {
      private final List<ClientCommonPacketListenerImpl.PackConfirmScreen.PendingRequest> requests;
      @Nullable
      private final Screen parentScreen;

      PackConfirmScreen(
         final Minecraft nullx,
         @Nullable final Screen nullxx,
         final List<ClientCommonPacketListenerImpl.PackConfirmScreen.PendingRequest> nullxxx,
         final boolean nullxxxx,
         @Nullable final Component nullxxxxx
      ) {
         super(
            var5 -> {
               nullx.setScreen(nullxx);
               DownloadedPackSource var6 = nullx.getDownloadedPackSource();
               if (var5) {
                  if (ClientCommonPacketListenerImpl.this.serverData != null) {
                     ClientCommonPacketListenerImpl.this.serverData.setResourcePackStatus(ServerData.ServerPackStatus.ENABLED);
                  }

                  var6.allowServerPacks();
               } else {
                  var6.rejectServerPacks();
                  if (nullxxxx) {
                     ClientCommonPacketListenerImpl.this.connection.disconnect(Component.translatable("multiplayer.requiredTexturePrompt.disconnect"));
                  } else if (ClientCommonPacketListenerImpl.this.serverData != null) {
                     ClientCommonPacketListenerImpl.this.serverData.setResourcePackStatus(ServerData.ServerPackStatus.DISABLED);
                  }
               }

               for (ClientCommonPacketListenerImpl.PackConfirmScreen.PendingRequest var8 : nullxxx) {
                  var6.pushPack(var8.id, var8.url, var8.hash);
               }

               if (ClientCommonPacketListenerImpl.this.serverData != null) {
                  ServerList.saveSingleServer(ClientCommonPacketListenerImpl.this.serverData);
               }
            },
            nullxxxx ? Component.translatable("multiplayer.requiredTexturePrompt.line1") : Component.translatable("multiplayer.texturePrompt.line1"),
            ClientCommonPacketListenerImpl.preparePackPrompt(
               nullxxxx
                  ? Component.translatable("multiplayer.requiredTexturePrompt.line2").withStyle(ChatFormatting.YELLOW, ChatFormatting.BOLD)
                  : Component.translatable("multiplayer.texturePrompt.line2"),
               nullxxxxx
            ),
            nullxxxx ? CommonComponents.GUI_PROCEED : CommonComponents.GUI_YES,
            nullxxxx ? CommonComponents.GUI_DISCONNECT : CommonComponents.GUI_NO
         );
         this.requests = nullxxx;
         this.parentScreen = nullxx;
      }

      public ClientCommonPacketListenerImpl.PackConfirmScreen update(Minecraft var1, UUID var2, URL var3, String var4, boolean var5, @Nullable Component var6) {
         ImmutableList var7 = ImmutableList.builderWithExpectedSize(this.requests.size() + 1)
            .addAll(this.requests)
            .add(new ClientCommonPacketListenerImpl.PackConfirmScreen.PendingRequest(var2, var3, var4))
            .build();
         return ClientCommonPacketListenerImpl.this.new PackConfirmScreen(var1, this.parentScreen, var7, var5, var6);
      }

      static record PendingRequest(UUID id, URL url, String hash) {

         PendingRequest(UUID id, URL url, String hash) {
            super();
            this.id = id;
            this.url = url;
            this.hash = hash;
         }
      }
   }
}
