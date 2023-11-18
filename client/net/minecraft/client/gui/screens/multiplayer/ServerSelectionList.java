package net.minecraft.client.gui.screens.multiplayer;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.DefaultUncaughtExceptionHandler;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.FaviconTexture;
import net.minecraft.client.gui.screens.LoadingDotsText;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.client.server.LanServer;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import org.slf4j.Logger;

public class ServerSelectionList extends ObjectSelectionList<ServerSelectionList.Entry> {
   static final ResourceLocation INCOMPATIBLE_SPRITE = new ResourceLocation("server_list/incompatible");
   static final ResourceLocation UNREACHABLE_SPRITE = new ResourceLocation("server_list/unreachable");
   static final ResourceLocation PING_1_SPRITE = new ResourceLocation("server_list/ping_1");
   static final ResourceLocation PING_2_SPRITE = new ResourceLocation("server_list/ping_2");
   static final ResourceLocation PING_3_SPRITE = new ResourceLocation("server_list/ping_3");
   static final ResourceLocation PING_4_SPRITE = new ResourceLocation("server_list/ping_4");
   static final ResourceLocation PING_5_SPRITE = new ResourceLocation("server_list/ping_5");
   static final ResourceLocation PINGING_1_SPRITE = new ResourceLocation("server_list/pinging_1");
   static final ResourceLocation PINGING_2_SPRITE = new ResourceLocation("server_list/pinging_2");
   static final ResourceLocation PINGING_3_SPRITE = new ResourceLocation("server_list/pinging_3");
   static final ResourceLocation PINGING_4_SPRITE = new ResourceLocation("server_list/pinging_4");
   static final ResourceLocation PINGING_5_SPRITE = new ResourceLocation("server_list/pinging_5");
   static final ResourceLocation JOIN_HIGHLIGHTED_SPRITE = new ResourceLocation("server_list/join_highlighted");
   static final ResourceLocation JOIN_SPRITE = new ResourceLocation("server_list/join");
   static final ResourceLocation MOVE_UP_HIGHLIGHTED_SPRITE = new ResourceLocation("server_list/move_up_highlighted");
   static final ResourceLocation MOVE_UP_SPRITE = new ResourceLocation("server_list/move_up");
   static final ResourceLocation MOVE_DOWN_HIGHLIGHTED_SPRITE = new ResourceLocation("server_list/move_down_highlighted");
   static final ResourceLocation MOVE_DOWN_SPRITE = new ResourceLocation("server_list/move_down");
   static final Logger LOGGER = LogUtils.getLogger();
   static final ThreadPoolExecutor THREAD_POOL = new ScheduledThreadPoolExecutor(
      5,
      new ThreadFactoryBuilder()
         .setNameFormat("Server Pinger #%d")
         .setDaemon(true)
         .setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(LOGGER))
         .build()
   );
   private static final ResourceLocation ICON_MISSING = new ResourceLocation("textures/misc/unknown_server.png");
   static final Component SCANNING_LABEL = Component.translatable("lanServer.scanning");
   static final Component CANT_RESOLVE_TEXT = Component.translatable("multiplayer.status.cannot_resolve").withStyle(var0 -> var0.withColor(-65536));
   static final Component CANT_CONNECT_TEXT = Component.translatable("multiplayer.status.cannot_connect").withStyle(var0 -> var0.withColor(-65536));
   static final Component INCOMPATIBLE_STATUS = Component.translatable("multiplayer.status.incompatible");
   static final Component NO_CONNECTION_STATUS = Component.translatable("multiplayer.status.no_connection");
   static final Component PINGING_STATUS = Component.translatable("multiplayer.status.pinging");
   static final Component ONLINE_STATUS = Component.translatable("multiplayer.status.online");
   private final JoinMultiplayerScreen screen;
   private final List<ServerSelectionList.OnlineServerEntry> onlineServers = Lists.newArrayList();
   private final ServerSelectionList.Entry lanHeader = new ServerSelectionList.LANHeader();
   private final List<ServerSelectionList.NetworkServerEntry> networkServers = Lists.newArrayList();

   public ServerSelectionList(JoinMultiplayerScreen var1, Minecraft var2, int var3, int var4, int var5, int var6, int var7) {
      super(var2, var3, var4, var5, var6, var7);
      this.screen = var1;
   }

   private void refreshEntries() {
      this.clearEntries();
      this.onlineServers.forEach(var1 -> this.addEntry(var1));
      this.addEntry(this.lanHeader);
      this.networkServers.forEach(var1 -> this.addEntry(var1));
   }

   public void setSelected(@Nullable ServerSelectionList.Entry var1) {
      super.setSelected(var1);
      this.screen.onSelectedChange();
   }

   @Override
   public boolean keyPressed(int var1, int var2, int var3) {
      ServerSelectionList.Entry var4 = this.getSelected();
      return var4 != null && var4.keyPressed(var1, var2, var3) || super.keyPressed(var1, var2, var3);
   }

   public void updateOnlineServers(ServerList var1) {
      this.onlineServers.clear();

      for(int var2 = 0; var2 < var1.size(); ++var2) {
         this.onlineServers.add(new ServerSelectionList.OnlineServerEntry(this.screen, var1.get(var2)));
      }

      this.refreshEntries();
   }

   public void updateNetworkServers(List<LanServer> var1) {
      int var2 = var1.size() - this.networkServers.size();
      this.networkServers.clear();

      for(LanServer var4 : var1) {
         this.networkServers.add(new ServerSelectionList.NetworkServerEntry(this.screen, var4));
      }

      this.refreshEntries();

      for(int var8 = this.networkServers.size() - var2; var8 < this.networkServers.size(); ++var8) {
         ServerSelectionList.NetworkServerEntry var9 = this.networkServers.get(var8);
         int var5 = var8 - this.networkServers.size() + this.children().size();
         int var6 = this.getRowTop(var5);
         int var7 = this.getRowBottom(var5);
         if (var7 >= this.y0 && var6 <= this.y1) {
            this.minecraft.getNarrator().say(Component.translatable("multiplayer.lan.server_found", var9.getServerNarration()));
         }
      }
   }

   @Override
   protected int getScrollbarPosition() {
      return super.getScrollbarPosition() + 30;
   }

   @Override
   public int getRowWidth() {
      return super.getRowWidth() + 85;
   }

   public void removed() {
   }

   public abstract static class Entry extends ObjectSelectionList.Entry<ServerSelectionList.Entry> implements AutoCloseable {
      public Entry() {
         super();
      }

      @Override
      public void close() {
      }
   }

   public static class LANHeader extends ServerSelectionList.Entry {
      private final Minecraft minecraft = Minecraft.getInstance();

      public LANHeader() {
         super();
      }

      @Override
      public void render(GuiGraphics var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9, float var10) {
         int var11 = var3 + var6 / 2 - 9 / 2;
         var1.drawString(
            this.minecraft.font,
            ServerSelectionList.SCANNING_LABEL,
            this.minecraft.screen.width / 2 - this.minecraft.font.width(ServerSelectionList.SCANNING_LABEL) / 2,
            var11,
            16777215,
            false
         );
         String var12 = LoadingDotsText.get(Util.getMillis());
         var1.drawString(this.minecraft.font, var12, this.minecraft.screen.width / 2 - this.minecraft.font.width(var12) / 2, var11 + 9, -8355712, false);
      }

      @Override
      public Component getNarration() {
         return ServerSelectionList.SCANNING_LABEL;
      }
   }

   public static class NetworkServerEntry extends ServerSelectionList.Entry {
      private static final int ICON_WIDTH = 32;
      private static final Component LAN_SERVER_HEADER = Component.translatable("lanServer.title");
      private static final Component HIDDEN_ADDRESS_TEXT = Component.translatable("selectServer.hiddenAddress");
      private final JoinMultiplayerScreen screen;
      protected final Minecraft minecraft;
      protected final LanServer serverData;
      private long lastClickTime;

      protected NetworkServerEntry(JoinMultiplayerScreen var1, LanServer var2) {
         super();
         this.screen = var1;
         this.serverData = var2;
         this.minecraft = Minecraft.getInstance();
      }

      @Override
      public void render(GuiGraphics var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9, float var10) {
         var1.drawString(this.minecraft.font, LAN_SERVER_HEADER, var4 + 32 + 3, var3 + 1, 16777215, false);
         var1.drawString(this.minecraft.font, this.serverData.getMotd(), var4 + 32 + 3, var3 + 12, -8355712, false);
         if (this.minecraft.options.hideServerAddress) {
            var1.drawString(this.minecraft.font, HIDDEN_ADDRESS_TEXT, var4 + 32 + 3, var3 + 12 + 11, 3158064, false);
         } else {
            var1.drawString(this.minecraft.font, this.serverData.getAddress(), var4 + 32 + 3, var3 + 12 + 11, 3158064, false);
         }
      }

      @Override
      public boolean mouseClicked(double var1, double var3, int var5) {
         this.screen.setSelected(this);
         if (Util.getMillis() - this.lastClickTime < 250L) {
            this.screen.joinSelectedServer();
         }

         this.lastClickTime = Util.getMillis();
         return false;
      }

      public LanServer getServerData() {
         return this.serverData;
      }

      @Override
      public Component getNarration() {
         return Component.translatable("narrator.select", this.getServerNarration());
      }

      public Component getServerNarration() {
         return Component.empty().append(LAN_SERVER_HEADER).append(CommonComponents.SPACE).append(this.serverData.getMotd());
      }
   }

   public class OnlineServerEntry extends ServerSelectionList.Entry {
      private static final int ICON_WIDTH = 32;
      private static final int ICON_HEIGHT = 32;
      private static final int ICON_OVERLAY_X_MOVE_LEFT = 32;
      private final JoinMultiplayerScreen screen;
      private final Minecraft minecraft;
      private final ServerData serverData;
      private final FaviconTexture icon;
      @Nullable
      private byte[] lastIconBytes;
      private long lastClickTime;

      protected OnlineServerEntry(JoinMultiplayerScreen var2, ServerData var3) {
         super();
         this.screen = var2;
         this.serverData = var3;
         this.minecraft = Minecraft.getInstance();
         this.icon = FaviconTexture.forServer(this.minecraft.getTextureManager(), var3.ip);
      }

      @Override
      public void render(GuiGraphics var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9, float var10) {
         if (!this.serverData.pinged) {
            this.serverData.pinged = true;
            this.serverData.ping = -2L;
            this.serverData.motd = CommonComponents.EMPTY;
            this.serverData.status = CommonComponents.EMPTY;
            ServerSelectionList.THREAD_POOL.submit(() -> {
               try {
                  this.screen.getPinger().pingServer(this.serverData, () -> this.minecraft.execute(this::updateServerList));
               } catch (UnknownHostException var2x) {
                  this.serverData.ping = -1L;
                  this.serverData.motd = ServerSelectionList.CANT_RESOLVE_TEXT;
               } catch (Exception var3x) {
                  this.serverData.ping = -1L;
                  this.serverData.motd = ServerSelectionList.CANT_CONNECT_TEXT;
               }
            });
         }

         boolean var11 = !this.isCompatible();
         var1.drawString(this.minecraft.font, this.serverData.name, var4 + 32 + 3, var3 + 1, 16777215, false);
         List var12 = this.minecraft.font.split(this.serverData.motd, var5 - 32 - 2);

         for(int var13 = 0; var13 < Math.min(var12.size(), 2); ++var13) {
            var1.drawString(this.minecraft.font, (FormattedCharSequence)var12.get(var13), var4 + 32 + 3, var3 + 12 + 9 * var13, -8355712, false);
         }

         Object var23 = var11 ? this.serverData.version.copy().withStyle(ChatFormatting.RED) : this.serverData.status;
         int var14 = this.minecraft.font.width((FormattedText)var23);
         var1.drawString(this.minecraft.font, (Component)var23, var4 + var5 - var14 - 15 - 2, var3 + 1, -8355712, false);
         ResourceLocation var15;
         List var16;
         Object var17;
         if (var11) {
            var15 = ServerSelectionList.INCOMPATIBLE_SPRITE;
            var17 = ServerSelectionList.INCOMPATIBLE_STATUS;
            var16 = this.serverData.playerList;
         } else if (this.pingCompleted()) {
            if (this.serverData.ping < 0L) {
               var15 = ServerSelectionList.UNREACHABLE_SPRITE;
            } else if (this.serverData.ping < 150L) {
               var15 = ServerSelectionList.PING_5_SPRITE;
            } else if (this.serverData.ping < 300L) {
               var15 = ServerSelectionList.PING_4_SPRITE;
            } else if (this.serverData.ping < 600L) {
               var15 = ServerSelectionList.PING_3_SPRITE;
            } else if (this.serverData.ping < 1000L) {
               var15 = ServerSelectionList.PING_2_SPRITE;
            } else {
               var15 = ServerSelectionList.PING_1_SPRITE;
            }

            if (this.serverData.ping < 0L) {
               var17 = ServerSelectionList.NO_CONNECTION_STATUS;
               var16 = Collections.emptyList();
            } else {
               var17 = Component.translatable("multiplayer.status.ping", this.serverData.ping);
               var16 = this.serverData.playerList;
            }
         } else {
            int var18 = (int)(Util.getMillis() / 100L + (long)(var2 * 2) & 7L);
            if (var18 > 4) {
               var18 = 8 - var18;
            }
            var15 = switch(var18) {
               case 1 -> ServerSelectionList.PINGING_2_SPRITE;
               case 2 -> ServerSelectionList.PINGING_3_SPRITE;
               case 3 -> ServerSelectionList.PINGING_4_SPRITE;
               case 4 -> ServerSelectionList.PINGING_5_SPRITE;
               default -> ServerSelectionList.PINGING_1_SPRITE;
            };
            var17 = ServerSelectionList.PINGING_STATUS;
            var16 = Collections.emptyList();
         }

         var1.blitSprite(var15, var4 + var5 - 15, var3, 10, 8);
         byte[] var24 = this.serverData.getIconBytes();
         if (!Arrays.equals(var24, this.lastIconBytes)) {
            if (this.uploadServerIcon(var24)) {
               this.lastIconBytes = var24;
            } else {
               this.serverData.setIconBytes(null);
               this.updateServerList();
            }
         }

         this.drawIcon(var1, var4, var3, this.icon.textureLocation());
         int var19 = var7 - var4;
         int var20 = var8 - var3;
         if (var19 >= var5 - 15 && var19 <= var5 - 5 && var20 >= 0 && var20 <= 8) {
            this.screen.setToolTip(Collections.singletonList((Component)var17));
         } else if (var19 >= var5 - var14 - 15 - 2 && var19 <= var5 - 15 - 2 && var20 >= 0 && var20 <= 8) {
            this.screen.setToolTip(var16);
         }

         if (this.minecraft.options.touchscreen().get() || var9) {
            var1.fill(var4, var3, var4 + 32, var3 + 32, -1601138544);
            int var21 = var7 - var4;
            int var22 = var8 - var3;
            if (this.canJoin()) {
               if (var21 < 32 && var21 > 16) {
                  var1.blitSprite(ServerSelectionList.JOIN_HIGHLIGHTED_SPRITE, var4, var3, 32, 32);
               } else {
                  var1.blitSprite(ServerSelectionList.JOIN_SPRITE, var4, var3, 32, 32);
               }
            }

            if (var2 > 0) {
               if (var21 < 16 && var22 < 16) {
                  var1.blitSprite(ServerSelectionList.MOVE_UP_HIGHLIGHTED_SPRITE, var4, var3, 32, 32);
               } else {
                  var1.blitSprite(ServerSelectionList.MOVE_UP_SPRITE, var4, var3, 32, 32);
               }
            }

            if (var2 < this.screen.getServers().size() - 1) {
               if (var21 < 16 && var22 > 16) {
                  var1.blitSprite(ServerSelectionList.MOVE_DOWN_HIGHLIGHTED_SPRITE, var4, var3, 32, 32);
               } else {
                  var1.blitSprite(ServerSelectionList.MOVE_DOWN_SPRITE, var4, var3, 32, 32);
               }
            }
         }
      }

      private boolean pingCompleted() {
         return this.serverData.pinged && this.serverData.ping != -2L;
      }

      private boolean isCompatible() {
         return this.serverData.protocol == SharedConstants.getCurrentVersion().getProtocolVersion();
      }

      public void updateServerList() {
         this.screen.getServers().save();
      }

      protected void drawIcon(GuiGraphics var1, int var2, int var3, ResourceLocation var4) {
         RenderSystem.enableBlend();
         var1.blit(var4, var2, var3, 0.0F, 0.0F, 32, 32, 32, 32);
         RenderSystem.disableBlend();
      }

      private boolean canJoin() {
         return true;
      }

      private boolean uploadServerIcon(@Nullable byte[] var1) {
         if (var1 == null) {
            this.icon.clear();
         } else {
            try {
               this.icon.upload(NativeImage.read(var1));
            } catch (Throwable var3) {
               ServerSelectionList.LOGGER.error("Invalid icon for server {} ({})", new Object[]{this.serverData.name, this.serverData.ip, var3});
               return false;
            }
         }

         return true;
      }

      @Override
      public boolean keyPressed(int var1, int var2, int var3) {
         if (Screen.hasShiftDown()) {
            ServerSelectionList var4 = this.screen.serverSelectionList;
            int var5 = var4.children().indexOf(this);
            if (var5 == -1) {
               return true;
            }

            if (var1 == 264 && var5 < this.screen.getServers().size() - 1 || var1 == 265 && var5 > 0) {
               this.swap(var5, var1 == 264 ? var5 + 1 : var5 - 1);
               return true;
            }
         }

         return super.keyPressed(var1, var2, var3);
      }

      private void swap(int var1, int var2) {
         this.screen.getServers().swap(var1, var2);
         this.screen.serverSelectionList.updateOnlineServers(this.screen.getServers());
         ServerSelectionList.Entry var3 = this.screen.serverSelectionList.children().get(var2);
         this.screen.serverSelectionList.setSelected(var3);
         ServerSelectionList.this.ensureVisible(var3);
      }

      @Override
      public boolean mouseClicked(double var1, double var3, int var5) {
         double var6 = var1 - (double)ServerSelectionList.this.getRowLeft();
         double var8 = var3 - (double)ServerSelectionList.this.getRowTop(ServerSelectionList.this.children().indexOf(this));
         if (var6 <= 32.0) {
            if (var6 < 32.0 && var6 > 16.0 && this.canJoin()) {
               this.screen.setSelected(this);
               this.screen.joinSelectedServer();
               return true;
            }

            int var10 = this.screen.serverSelectionList.children().indexOf(this);
            if (var6 < 16.0 && var8 < 16.0 && var10 > 0) {
               this.swap(var10, var10 - 1);
               return true;
            }

            if (var6 < 16.0 && var8 > 16.0 && var10 < this.screen.getServers().size() - 1) {
               this.swap(var10, var10 + 1);
               return true;
            }
         }

         this.screen.setSelected(this);
         if (Util.getMillis() - this.lastClickTime < 250L) {
            this.screen.joinSelectedServer();
         }

         this.lastClickTime = Util.getMillis();
         return true;
      }

      public ServerData getServerData() {
         return this.serverData;
      }

      @Override
      public Component getNarration() {
         MutableComponent var1 = Component.empty();
         var1.append(Component.translatable("narrator.select", this.serverData.name));
         var1.append(CommonComponents.NARRATION_SEPARATOR);
         if (!this.isCompatible()) {
            var1.append(ServerSelectionList.INCOMPATIBLE_STATUS);
            var1.append(CommonComponents.NARRATION_SEPARATOR);
            var1.append(Component.translatable("multiplayer.status.version.narration", this.serverData.version));
            var1.append(CommonComponents.NARRATION_SEPARATOR);
            var1.append(Component.translatable("multiplayer.status.motd.narration", this.serverData.motd));
         } else if (this.serverData.ping < 0L) {
            var1.append(ServerSelectionList.NO_CONNECTION_STATUS);
         } else if (!this.pingCompleted()) {
            var1.append(ServerSelectionList.PINGING_STATUS);
         } else {
            var1.append(ServerSelectionList.ONLINE_STATUS);
            var1.append(CommonComponents.NARRATION_SEPARATOR);
            var1.append(Component.translatable("multiplayer.status.ping.narration", this.serverData.ping));
            var1.append(CommonComponents.NARRATION_SEPARATOR);
            var1.append(Component.translatable("multiplayer.status.motd.narration", this.serverData.motd));
            if (this.serverData.players != null) {
               var1.append(CommonComponents.NARRATION_SEPARATOR);
               var1.append(
                  Component.translatable("multiplayer.status.player_count.narration", this.serverData.players.online(), this.serverData.players.max())
               );
               var1.append(CommonComponents.NARRATION_SEPARATOR);
               var1.append(ComponentUtils.formatList(this.serverData.playerList, Component.literal(", ")));
            }
         }

         return var1;
      }

      @Override
      public void close() {
         this.icon.close();
      }
   }
}
