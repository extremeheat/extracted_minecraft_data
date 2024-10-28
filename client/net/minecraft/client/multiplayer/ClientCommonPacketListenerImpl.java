package net.minecraft.client.multiplayer;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BooleanSupplier;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportType;
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
import net.minecraft.network.DisconnectionDetails;
import net.minecraft.network.ServerboundPacketListener;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketUtils;
import net.minecraft.network.protocol.common.ClientCommonPacketListener;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.ClientboundCustomReportDetailsPacket;
import net.minecraft.network.protocol.common.ClientboundDisconnectPacket;
import net.minecraft.network.protocol.common.ClientboundKeepAlivePacket;
import net.minecraft.network.protocol.common.ClientboundPingPacket;
import net.minecraft.network.protocol.common.ClientboundResourcePackPopPacket;
import net.minecraft.network.protocol.common.ClientboundResourcePackPushPacket;
import net.minecraft.network.protocol.common.ClientboundServerLinksPacket;
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
import net.minecraft.server.ServerLinks;
import net.minecraft.util.thread.BlockableEventLoop;
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
   /** @deprecated */
   @Deprecated(
      forRemoval = true
   )
   protected final boolean strictErrorHandling;
   private final List<DeferredPacket> deferredPackets = new ArrayList();
   protected final Map<ResourceLocation, byte[]> serverCookies;
   protected Map<String, String> customReportDetails;
   protected ServerLinks serverLinks;

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
      this.customReportDetails = var3.customReportDetails();
      this.serverLinks = var3.serverLinks();
   }

   public void onPacketError(Packet var1, Exception var2) {
      LOGGER.error("Failed to handle packet {}", var1, var2);
      ClientCommonPacketListener.super.onPacketError(var1, var2);
      Optional var3 = this.storeDisconnectionReport(var1, var2);
      Optional var4 = this.serverLinks.findKnownType(ServerLinks.KnownLinkType.BUG_REPORT).map(ServerLinks.Entry::url);
      if (this.strictErrorHandling) {
         this.connection.disconnect(new DisconnectionDetails(Component.translatable("disconnect.packetError"), var3, var4));
      }

   }

   public DisconnectionDetails createDisconnectionInfo(Component var1, Throwable var2) {
      Optional var3 = this.storeDisconnectionReport((Packet)null, var2);
      Optional var4 = this.serverLinks.findKnownType(ServerLinks.KnownLinkType.BUG_REPORT).map(ServerLinks.Entry::url);
      return new DisconnectionDetails(var1, var3, var4);
   }

   private Optional<Path> storeDisconnectionReport(@Nullable Packet var1, Throwable var2) {
      CrashReport var3 = CrashReport.forThrowable(var2, "Packet handling error");
      PacketUtils.fillCrashReport(var3, this, var1);
      Path var4 = this.minecraft.gameDirectory.toPath().resolve("debug");
      Path var5 = var4.resolve("disconnect-" + Util.getFilenameFormattedDateTime() + "-client.txt");
      Optional var6 = this.serverLinks.findKnownType(ServerLinks.KnownLinkType.BUG_REPORT);
      List var7 = (List)var6.map((var0) -> {
         return List.of("Server bug reporting link: " + var0.url());
      }).orElse(List.of());
      return var3.saveToFile(var5, ReportType.NETWORK_PROTOCOL_ERROR, var7) ? Optional.of(var5) : Optional.empty();
   }

   public boolean shouldHandleMessage(Packet<?> var1) {
      if (ClientCommonPacketListener.super.shouldHandleMessage(var1)) {
         return true;
      } else {
         return this.isTransferring && (var1 instanceof ClientboundStoreCookiePacket || var1 instanceof ClientboundTransferPacket);
      }
   }

   public void handleKeepAlive(ClientboundKeepAlivePacket var1) {
      this.sendWhen(new ServerboundKeepAlivePacket(var1.getId()), () -> {
         return !RenderSystem.isFrozenAtPollEvents();
      }, Duration.ofMinutes(1L));
   }

   public void handlePing(ClientboundPingPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (BlockableEventLoop)this.minecraft);
      this.send(new ServerboundPongPacket(var1.getId()));
   }

   public void handleCustomPayload(ClientboundCustomPayloadPacket var1) {
      CustomPacketPayload var2 = var1.payload();
      if (!(var2 instanceof DiscardedPayload)) {
         PacketUtils.ensureRunningOnSameThread(var1, this, (BlockableEventLoop)this.minecraft);
         if (var2 instanceof BrandPayload) {
            BrandPayload var3 = (BrandPayload)var2;
            this.serverBrand = var3.brand();
            this.telemetryManager.onServerBrandReceived(var3.brand());
         } else {
            this.handleCustomPayload(var2);
         }

      }
   }

   protected abstract void handleCustomPayload(CustomPacketPayload var1);

   public void handleResourcePackPush(ClientboundResourcePackPushPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (BlockableEventLoop)this.minecraft);
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
            this.minecraft.setScreen(this.addOrUpdatePackPrompt(var2, var3, var4, var5, (Component)var1.prompt().orElse((Object)null)));
         }

      }
   }

   public void handleResourcePackPop(ClientboundResourcePackPopPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (BlockableEventLoop)this.minecraft);
      var1.id().ifPresentOrElse((var1x) -> {
         this.minecraft.getDownloadedPackSource().popPack(var1x);
      }, () -> {
         this.minecraft.getDownloadedPackSource().popAll();
      });
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

   public void handleRequestCookie(ClientboundCookieRequestPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (BlockableEventLoop)this.minecraft);
      this.connection.send(new ServerboundCookieResponsePacket(var1.key(), (byte[])this.serverCookies.get(var1.key())));
   }

   public void handleStoreCookie(ClientboundStoreCookiePacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (BlockableEventLoop)this.minecraft);
      this.serverCookies.put(var1.key(), var1.payload());
   }

   public void handleCustomReportDetails(ClientboundCustomReportDetailsPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (BlockableEventLoop)this.minecraft);
      this.customReportDetails = var1.details();
   }

   public void handleServerLinks(ClientboundServerLinksPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, (BlockableEventLoop)this.minecraft);
      this.serverLinks = var1.links();
   }

   public void handleTransfer(ClientboundTransferPacket var1) {
      this.isTransferring = true;
      PacketUtils.ensureRunningOnSameThread(var1, this, (BlockableEventLoop)this.minecraft);
      if (this.serverData == null) {
         throw new IllegalStateException("Cannot transfer to server from singleplayer");
      } else {
         this.connection.disconnect((Component)Component.translatable("disconnect.transfer"));
         this.connection.setReadOnly();
         this.connection.handleDisconnection();
         ServerAddress var2 = new ServerAddress(var1.host(), var1.port());
         ConnectScreen.startConnecting((Screen)Objects.requireNonNullElseGet(this.postDisconnectScreen, TitleScreen::new), this.minecraft, var2, this.serverData, false, new TransferState(this.serverCookies));
      }
   }

   public void handleDisconnect(ClientboundDisconnectPacket var1) {
      this.connection.disconnect(var1.reason());
   }

   protected void sendDeferredPackets() {
      Iterator var1 = this.deferredPackets.iterator();

      while(var1.hasNext()) {
         DeferredPacket var2 = (DeferredPacket)var1.next();
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

   public void onDisconnect(DisconnectionDetails var1) {
      this.telemetryManager.onDisconnect();
      this.minecraft.disconnect(this.createDisconnectScreen(var1), this.isTransferring);
      LOGGER.warn("Client disconnected with reason: {}", var1.reason().getString());
   }

   public void fillListenerSpecificCrashDetails(CrashReport var1, CrashReportCategory var2) {
      var2.setDetail("Server type", () -> {
         return this.serverData != null ? this.serverData.type().toString() : "<none>";
      });
      var2.setDetail("Server brand", () -> {
         return this.serverBrand;
      });
      if (!this.customReportDetails.isEmpty()) {
         CrashReportCategory var3 = var1.addCategory("Custom Server Details");
         Map var10000 = this.customReportDetails;
         Objects.requireNonNull(var3);
         var10000.forEach(var3::setDetail);
      }

   }

   protected Screen createDisconnectScreen(DisconnectionDetails var1) {
      Screen var2 = (Screen)Objects.requireNonNullElseGet(this.postDisconnectScreen, () -> {
         return new JoinMultiplayerScreen(new TitleScreen());
      });
      return (Screen)(this.serverData != null && this.serverData.isRealm() ? new DisconnectedRealmsScreen(var2, GENERIC_DISCONNECT_MESSAGE, var1.reason()) : new DisconnectedScreen(var2, GENERIC_DISCONNECT_MESSAGE, var1));
   }

   @Nullable
   public String serverBrand() {
      return this.serverBrand;
   }

   private void sendWhen(Packet<? extends ServerboundPacketListener> var1, BooleanSupplier var2, Duration var3) {
      if (var2.getAsBoolean()) {
         this.send(var1);
      } else {
         this.deferredPackets.add(new DeferredPacket(var1, var2, Util.getMillis() + var3.toMillis()));
      }

   }

   private Screen addOrUpdatePackPrompt(UUID var1, URL var2, String var3, boolean var4, @Nullable Component var5) {
      Screen var6 = this.minecraft.screen;
      if (var6 instanceof PackConfirmScreen var7) {
         return var7.update(this.minecraft, var1, var2, var3, var4, var5);
      } else {
         return new PackConfirmScreen(this.minecraft, var6, List.of(new PackConfirmScreen.PendingRequest(var1, var2, var3)), var4, var5);
      }
   }

   private static record DeferredPacket(Packet<? extends ServerboundPacketListener> packet, BooleanSupplier sendCondition, long expirationTime) {
      final Packet<? extends ServerboundPacketListener> packet;

      DeferredPacket(Packet<? extends ServerboundPacketListener> var1, BooleanSupplier var2, long var3) {
         super();
         this.packet = var1;
         this.sendCondition = var2;
         this.expirationTime = var3;
      }

      public Packet<? extends ServerboundPacketListener> packet() {
         return this.packet;
      }

      public BooleanSupplier sendCondition() {
         return this.sendCondition;
      }

      public long expirationTime() {
         return this.expirationTime;
      }
   }

   private class PackConfirmScreen extends ConfirmScreen {
      private final List<PendingRequest> requests;
      @Nullable
      private final Screen parentScreen;

      PackConfirmScreen(final Minecraft var2, @Nullable final Screen var3, final List<PendingRequest> var4, final boolean var5, @Nullable final Component var6) {
         super((var5x) -> {
            var2.setScreen(var3);
            DownloadedPackSource var6 = var2.getDownloadedPackSource();
            if (var5x) {
               if (ClientCommonPacketListenerImpl.this.serverData != null) {
                  ClientCommonPacketListenerImpl.this.serverData.setResourcePackStatus(ServerData.ServerPackStatus.ENABLED);
               }

               var6.allowServerPacks();
            } else {
               var6.rejectServerPacks();
               if (var5) {
                  ClientCommonPacketListenerImpl.this.connection.disconnect((Component)Component.translatable("multiplayer.requiredTexturePrompt.disconnect"));
               } else if (ClientCommonPacketListenerImpl.this.serverData != null) {
                  ClientCommonPacketListenerImpl.this.serverData.setResourcePackStatus(ServerData.ServerPackStatus.DISABLED);
               }
            }

            Iterator var7 = var4.iterator();

            while(var7.hasNext()) {
               PendingRequest var8 = (PendingRequest)var7.next();
               var6.pushPack(var8.id, var8.url, var8.hash);
            }

            if (ClientCommonPacketListenerImpl.this.serverData != null) {
               ServerList.saveSingleServer(ClientCommonPacketListenerImpl.this.serverData);
            }

         }, var5 ? Component.translatable("multiplayer.requiredTexturePrompt.line1") : Component.translatable("multiplayer.texturePrompt.line1"), ClientCommonPacketListenerImpl.preparePackPrompt(var5 ? Component.translatable("multiplayer.requiredTexturePrompt.line2").withStyle(ChatFormatting.YELLOW, ChatFormatting.BOLD) : Component.translatable("multiplayer.texturePrompt.line2"), var6), var5 ? CommonComponents.GUI_PROCEED : CommonComponents.GUI_YES, var5 ? CommonComponents.GUI_DISCONNECT : CommonComponents.GUI_NO);
         this.requests = var4;
         this.parentScreen = var3;
      }

      public PackConfirmScreen update(Minecraft var1, UUID var2, URL var3, String var4, boolean var5, @Nullable Component var6) {
         ImmutableList var7 = ImmutableList.builderWithExpectedSize(this.requests.size() + 1).addAll(this.requests).add(new PendingRequest(var2, var3, var4)).build();
         return ClientCommonPacketListenerImpl.this.new PackConfirmScreen(var1, this.parentScreen, var7, var5, var6);
      }

      private static record PendingRequest(UUID id, URL url, String hash) {
         final UUID id;
         final URL url;
         final String hash;

         PendingRequest(UUID var1, URL var2, String var3) {
            super();
            this.id = var1;
            this.url = var2;
            this.hash = var3;
         }

         public UUID id() {
            return this.id;
         }

         public URL url() {
            return this.url;
         }

         public String hash() {
            return this.hash;
         }
      }
   }
}
