package net.minecraft.client.multiplayer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.BooleanSupplier;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.DisconnectedScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.telemetry.WorldSessionTelemetryManager;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
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
import net.minecraft.network.protocol.common.ClientboundResourcePackPacket;
import net.minecraft.network.protocol.common.ClientboundUpdateTagsPacket;
import net.minecraft.network.protocol.common.ServerboundKeepAlivePacket;
import net.minecraft.network.protocol.common.ServerboundPongPacket;
import net.minecraft.network.protocol.common.ServerboundResourcePackPacket;
import net.minecraft.network.protocol.common.custom.BrandPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.DiscardedPayload;
import net.minecraft.realms.DisconnectedRealmsScreen;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagNetworkSerialization;
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
   private final List<ClientCommonPacketListenerImpl.DeferredPacket> deferredPackets = new ArrayList<>();

   protected ClientCommonPacketListenerImpl(Minecraft var1, Connection var2, CommonListenerCookie var3) {
      super();
      this.minecraft = var1;
      this.connection = var2;
      this.serverData = var3.serverData();
      this.serverBrand = var3.serverBrand();
      this.telemetryManager = var3.telemetryManager();
      this.postDisconnectScreen = var3.postDisconnectScreen();
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

   // $QF: Could not properly define all variable types!
   // Please report this to the Quiltflower issue tracker, at https://github.com/QuiltMC/quiltflower/issues with a copy of the class file (if you have the rights to distribute it!)
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

   protected abstract RegistryAccess.Frozen registryAccess();

   @Override
   public void handleResourcePack(ClientboundResourcePackPacket var1) {
      URL var2 = parseResourcePackUrl(var1.getUrl());
      if (var2 == null) {
         this.send(ServerboundResourcePackPacket.Action.FAILED_DOWNLOAD);
      } else {
         String var3 = var1.getHash();
         boolean var4 = var1.isRequired();
         if (this.serverData != null && this.serverData.getResourcePackStatus() == ServerData.ServerPackStatus.ENABLED) {
            this.send(ServerboundResourcePackPacket.Action.ACCEPTED);
            this.packApplicationCallback(this.minecraft.getDownloadedPackSource().downloadAndSelectResourcePack(var2, var3, true));
         } else if (this.serverData != null
            && this.serverData.getResourcePackStatus() != ServerData.ServerPackStatus.PROMPT
            && (!var4 || this.serverData.getResourcePackStatus() != ServerData.ServerPackStatus.DISABLED)) {
            this.send(ServerboundResourcePackPacket.Action.DECLINED);
            if (var4) {
               this.connection.disconnect(Component.translatable("multiplayer.requiredTexturePrompt.disconnect"));
            }
         } else {
            this.minecraft.execute(() -> this.showServerPackPrompt(var2, var3, var4, var1.getPrompt()));
         }
      }
   }

   private void showServerPackPrompt(URL var1, String var2, boolean var3, @Nullable Component var4) {
      Screen var5 = this.minecraft.screen;
      this.minecraft
         .setScreen(
            new ConfirmScreen(
               var5x -> {
                  this.minecraft.setScreen(var5);
                  if (var5x) {
                     if (this.serverData != null) {
                        this.serverData.setResourcePackStatus(ServerData.ServerPackStatus.ENABLED);
                     }
         
                     this.send(ServerboundResourcePackPacket.Action.ACCEPTED);
                     this.packApplicationCallback(this.minecraft.getDownloadedPackSource().downloadAndSelectResourcePack(var1, var2, true));
                  } else {
                     this.send(ServerboundResourcePackPacket.Action.DECLINED);
                     if (var3) {
                        this.connection.disconnect(Component.translatable("multiplayer.requiredTexturePrompt.disconnect"));
                     } else if (this.serverData != null) {
                        this.serverData.setResourcePackStatus(ServerData.ServerPackStatus.DISABLED);
                     }
                  }
         
                  if (this.serverData != null) {
                     ServerList.saveSingleServer(this.serverData);
                  }
               },
               var3 ? Component.translatable("multiplayer.requiredTexturePrompt.line1") : Component.translatable("multiplayer.texturePrompt.line1"),
               preparePackPrompt(
                  var3
                     ? Component.translatable("multiplayer.requiredTexturePrompt.line2").withStyle(ChatFormatting.YELLOW, ChatFormatting.BOLD)
                     : Component.translatable("multiplayer.texturePrompt.line2"),
                  var4
               ),
               var3 ? CommonComponents.GUI_PROCEED : CommonComponents.GUI_YES,
               (Component)(var3 ? Component.translatable("menu.disconnect") : CommonComponents.GUI_NO)
            )
         );
   }

   private static Component preparePackPrompt(Component var0, @Nullable Component var1) {
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

   private void packApplicationCallback(CompletableFuture<?> var1) {
      var1.thenRun(() -> this.send(ServerboundResourcePackPacket.Action.SUCCESSFULLY_LOADED)).exceptionally(var1x -> {
         this.send(ServerboundResourcePackPacket.Action.FAILED_DOWNLOAD);
         return null;
      });
   }

   @Override
   public void handleUpdateTags(ClientboundUpdateTagsPacket var1) {
      PacketUtils.ensureRunningOnSameThread(var1, this, this.minecraft);
      var1.getTags().forEach(this::updateTagsForRegistry);
   }

   private <T> void updateTagsForRegistry(ResourceKey<? extends Registry<? extends T>> var1, TagNetworkSerialization.NetworkPayload var2) {
      if (!var2.isEmpty()) {
         Registry var3 = (Registry)this.registryAccess().registry(var1).orElseThrow(() -> new IllegalStateException("Unknown registry " + var1));
         HashMap var5 = new HashMap();
         TagNetworkSerialization.deserializeTagsFromNetwork(var1, var3, var2, var5::put);
         var3.bindTags(var5);
      }
   }

   private void send(ServerboundResourcePackPacket.Action var1) {
      this.connection.send(new ServerboundResourcePackPacket(var1));
   }

   @Override
   public void handleDisconnect(ClientboundDisconnectPacket var1) {
      this.connection.disconnect(var1.getReason());
   }

   protected void sendDeferredPackets() {
      Iterator var1 = this.deferredPackets.iterator();

      while(var1.hasNext()) {
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
      this.minecraft.disconnect(this.createDisconnectScreen(var1));
      LOGGER.warn("Client disconnected with reason: {}", var1.getString());
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

   static record DeferredPacket(Packet<? extends ServerboundPacketListener> a, BooleanSupplier b, long c) {
      final Packet<? extends ServerboundPacketListener> packet;
      private final BooleanSupplier sendCondition;
      private final long expirationTime;

      DeferredPacket(Packet<? extends ServerboundPacketListener> var1, BooleanSupplier var2, long var3) {
         super();
         this.packet = var1;
         this.sendCondition = var2;
         this.expirationTime = var3;
      }
   }
}
