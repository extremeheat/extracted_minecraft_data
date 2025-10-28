package net.minecraft.client.gui.screens.multiplayer;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.logging.LogUtils;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import net.minecraft.ChatFormatting;
import net.minecraft.DefaultUncaughtExceptionHandler;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.LoadingDotsWidget;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.SelectableEntry;
import net.minecraft.client.gui.screens.FaviconTexture;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.server.LanServer;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.network.EventLoopGroupHolder;
import net.minecraft.util.FormattedCharSequence;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class ServerSelectionList extends ObjectSelectionList<Entry> {
   static final ResourceLocation INCOMPATIBLE_SPRITE = ResourceLocation.withDefaultNamespace("server_list/incompatible");
   static final ResourceLocation UNREACHABLE_SPRITE = ResourceLocation.withDefaultNamespace("server_list/unreachable");
   static final ResourceLocation PING_1_SPRITE = ResourceLocation.withDefaultNamespace("server_list/ping_1");
   static final ResourceLocation PING_2_SPRITE = ResourceLocation.withDefaultNamespace("server_list/ping_2");
   static final ResourceLocation PING_3_SPRITE = ResourceLocation.withDefaultNamespace("server_list/ping_3");
   static final ResourceLocation PING_4_SPRITE = ResourceLocation.withDefaultNamespace("server_list/ping_4");
   static final ResourceLocation PING_5_SPRITE = ResourceLocation.withDefaultNamespace("server_list/ping_5");
   static final ResourceLocation PINGING_1_SPRITE = ResourceLocation.withDefaultNamespace("server_list/pinging_1");
   static final ResourceLocation PINGING_2_SPRITE = ResourceLocation.withDefaultNamespace("server_list/pinging_2");
   static final ResourceLocation PINGING_3_SPRITE = ResourceLocation.withDefaultNamespace("server_list/pinging_3");
   static final ResourceLocation PINGING_4_SPRITE = ResourceLocation.withDefaultNamespace("server_list/pinging_4");
   static final ResourceLocation PINGING_5_SPRITE = ResourceLocation.withDefaultNamespace("server_list/pinging_5");
   static final ResourceLocation JOIN_HIGHLIGHTED_SPRITE = ResourceLocation.withDefaultNamespace("server_list/join_highlighted");
   static final ResourceLocation JOIN_SPRITE = ResourceLocation.withDefaultNamespace("server_list/join");
   static final ResourceLocation MOVE_UP_HIGHLIGHTED_SPRITE = ResourceLocation.withDefaultNamespace("server_list/move_up_highlighted");
   static final ResourceLocation MOVE_UP_SPRITE = ResourceLocation.withDefaultNamespace("server_list/move_up");
   static final ResourceLocation MOVE_DOWN_HIGHLIGHTED_SPRITE = ResourceLocation.withDefaultNamespace("server_list/move_down_highlighted");
   static final ResourceLocation MOVE_DOWN_SPRITE = ResourceLocation.withDefaultNamespace("server_list/move_down");
   static final Logger LOGGER = LogUtils.getLogger();
   static final ThreadPoolExecutor THREAD_POOL;
   static final Component SCANNING_LABEL;
   static final Component CANT_RESOLVE_TEXT;
   static final Component CANT_CONNECT_TEXT;
   static final Component INCOMPATIBLE_STATUS;
   static final Component NO_CONNECTION_STATUS;
   static final Component PINGING_STATUS;
   static final Component ONLINE_STATUS;
   private final JoinMultiplayerScreen screen;
   private final List<OnlineServerEntry> onlineServers = Lists.newArrayList();
   private final Entry lanHeader = new LANHeader();
   private final List<NetworkServerEntry> networkServers = Lists.newArrayList();

   public ServerSelectionList(JoinMultiplayerScreen var1, Minecraft var2, int var3, int var4, int var5, int var6) {
      super(var2, var3, var4, var5, var6);
      this.screen = var1;
   }

   private void refreshEntries() {
      Entry var1 = (Entry)this.getSelected();
      ArrayList var2 = new ArrayList(this.onlineServers);
      var2.add(this.lanHeader);
      var2.addAll(this.networkServers);
      this.replaceEntries(var2);
      if (var1 != null) {
         for(Entry var4 : var2) {
            if (var4.matches(var1)) {
               this.setSelected(var4);
               break;
            }
         }
      }

   }

   public void setSelected(@Nullable Entry var1) {
      super.setSelected(var1);
      this.screen.onSelectedChange();
   }

   public void updateOnlineServers(ServerList var1) {
      this.onlineServers.clear();

      for(int var2 = 0; var2 < var1.size(); ++var2) {
         this.onlineServers.add(new OnlineServerEntry(this.screen, var1.get(var2)));
      }

      this.refreshEntries();
   }

   public void updateNetworkServers(List<LanServer> var1) {
      int var2 = var1.size() - this.networkServers.size();
      this.networkServers.clear();

      for(LanServer var4 : var1) {
         this.networkServers.add(new NetworkServerEntry(this.screen, var4));
      }

      this.refreshEntries();

      for(int var8 = this.networkServers.size() - var2; var8 < this.networkServers.size(); ++var8) {
         NetworkServerEntry var9 = (NetworkServerEntry)this.networkServers.get(var8);
         int var5 = var8 - this.networkServers.size() + this.children().size();
         int var6 = this.getRowTop(var5);
         int var7 = this.getRowBottom(var5);
         if (var7 >= this.getY() && var6 <= this.getBottom()) {
            this.minecraft.getNarrator().saySystemQueued(Component.translatable("multiplayer.lan.server_found", var9.getServerNarration()));
         }
      }

   }

   public int getRowWidth() {
      return 305;
   }

   public void removed() {
   }

   static {
      THREAD_POOL = new ScheduledThreadPoolExecutor(5, (new ThreadFactoryBuilder()).setNameFormat("Server Pinger #%d").setDaemon(true).setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(LOGGER)).build());
      SCANNING_LABEL = Component.translatable("lanServer.scanning");
      CANT_RESOLVE_TEXT = Component.translatable("multiplayer.status.cannot_resolve").withColor(-65536);
      CANT_CONNECT_TEXT = Component.translatable("multiplayer.status.cannot_connect").withColor(-65536);
      INCOMPATIBLE_STATUS = Component.translatable("multiplayer.status.incompatible");
      NO_CONNECTION_STATUS = Component.translatable("multiplayer.status.no_connection");
      PINGING_STATUS = Component.translatable("multiplayer.status.pinging");
      ONLINE_STATUS = Component.translatable("multiplayer.status.online");
   }

   public abstract static class Entry extends ObjectSelectionList.Entry<Entry> implements AutoCloseable {
      public Entry() {
         super();
      }

      public void close() {
      }

      abstract boolean matches(Entry var1);

      public abstract void join();
   }

   public static class LANHeader extends Entry {
      private final Minecraft minecraft = Minecraft.getInstance();
      private final LoadingDotsWidget loadingDotsWidget;

      public LANHeader() {
         super();
         this.loadingDotsWidget = new LoadingDotsWidget(this.minecraft.font, ServerSelectionList.SCANNING_LABEL);
      }

      public void renderContent(GuiGraphics var1, int var2, int var3, boolean var4, float var5) {
         this.loadingDotsWidget.setPosition(this.getContentXMiddle() - this.minecraft.font.width((FormattedText)ServerSelectionList.SCANNING_LABEL) / 2, this.getContentY());
         this.loadingDotsWidget.render(var1, var2, var3, var5);
      }

      public Component getNarration() {
         return ServerSelectionList.SCANNING_LABEL;
      }

      boolean matches(Entry var1) {
         return var1 instanceof LANHeader;
      }

      public void join() {
      }
   }

   public static class NetworkServerEntry extends Entry {
      private static final int ICON_WIDTH = 32;
      private static final Component LAN_SERVER_HEADER = Component.translatable("lanServer.title");
      private static final Component HIDDEN_ADDRESS_TEXT = Component.translatable("selectServer.hiddenAddress");
      private final JoinMultiplayerScreen screen;
      protected final Minecraft minecraft;
      protected final LanServer serverData;

      protected NetworkServerEntry(JoinMultiplayerScreen var1, LanServer var2) {
         super();
         this.screen = var1;
         this.serverData = var2;
         this.minecraft = Minecraft.getInstance();
      }

      public void renderContent(GuiGraphics var1, int var2, int var3, boolean var4, float var5) {
         var1.drawString(this.minecraft.font, (Component)LAN_SERVER_HEADER, this.getContentX() + 32 + 3, this.getContentY() + 1, -1);
         var1.drawString(this.minecraft.font, this.serverData.getMotd(), this.getContentX() + 32 + 3, this.getContentY() + 12, -8355712);
         if (this.minecraft.options.hideServerAddress) {
            var1.drawString(this.minecraft.font, HIDDEN_ADDRESS_TEXT, this.getContentX() + 32 + 3, this.getContentY() + 12 + 11, -8355712);
         } else {
            var1.drawString(this.minecraft.font, this.serverData.getAddress(), this.getContentX() + 32 + 3, this.getContentY() + 12 + 11, -8355712);
         }

      }

      public boolean mouseClicked(MouseButtonEvent var1, boolean var2) {
         if (var2) {
            this.join();
         }

         return super.mouseClicked(var1, var2);
      }

      public boolean keyPressed(KeyEvent var1) {
         if (var1.isSelection()) {
            this.join();
            return true;
         } else {
            return super.keyPressed(var1);
         }
      }

      public void join() {
         this.screen.join(new ServerData(this.serverData.getMotd(), this.serverData.getAddress(), ServerData.Type.LAN));
      }

      public Component getNarration() {
         return Component.translatable("narrator.select", this.getServerNarration());
      }

      public Component getServerNarration() {
         return Component.empty().append(LAN_SERVER_HEADER).append(CommonComponents.SPACE).append(this.serverData.getMotd());
      }

      boolean matches(Entry var1) {
         boolean var10000;
         if (var1 instanceof NetworkServerEntry var2) {
            if (var2.serverData == this.serverData) {
               var10000 = true;
               return var10000;
            }
         }

         var10000 = false;
         return var10000;
      }
   }

   public class OnlineServerEntry extends Entry implements SelectableEntry {
      private static final int ICON_SIZE = 32;
      private static final int SPACING = 5;
      private static final int STATUS_ICON_WIDTH = 10;
      private static final int STATUS_ICON_HEIGHT = 8;
      private final JoinMultiplayerScreen screen;
      private final Minecraft minecraft;
      private final ServerData serverData;
      private final FaviconTexture icon;
      private byte @Nullable [] lastIconBytes;
      private @Nullable List<Component> onlinePlayersTooltip;
      private @Nullable ResourceLocation statusIcon;
      private @Nullable Component statusIconTooltip;

      protected OnlineServerEntry(final JoinMultiplayerScreen var2, final ServerData var3) {
         super();
         this.screen = var2;
         this.serverData = var3;
         this.minecraft = Minecraft.getInstance();
         this.icon = FaviconTexture.forServer(this.minecraft.getTextureManager(), var3.ip);
         this.refreshStatus();
      }

      public void renderContent(GuiGraphics var1, int var2, int var3, boolean var4, float var5) {
         if (this.serverData.state() == ServerData.State.INITIAL) {
            this.serverData.setState(ServerData.State.PINGING);
            this.serverData.motd = CommonComponents.EMPTY;
            this.serverData.status = CommonComponents.EMPTY;
            ServerSelectionList.THREAD_POOL.submit(() -> {
               try {
                  this.screen.getPinger().pingServer(this.serverData, () -> this.minecraft.execute(this::updateServerList), () -> {
                     this.serverData.setState(this.serverData.protocol == SharedConstants.getCurrentVersion().protocolVersion() ? ServerData.State.SUCCESSFUL : ServerData.State.INCOMPATIBLE);
                     this.minecraft.execute(this::refreshStatus);
                  }, EventLoopGroupHolder.remote(this.minecraft.options.useNativeTransport()));
               } catch (UnknownHostException var2) {
                  this.serverData.setState(ServerData.State.UNREACHABLE);
                  this.serverData.motd = ServerSelectionList.CANT_RESOLVE_TEXT;
                  this.minecraft.execute(this::refreshStatus);
               } catch (Exception var3) {
                  this.serverData.setState(ServerData.State.UNREACHABLE);
                  this.serverData.motd = ServerSelectionList.CANT_CONNECT_TEXT;
                  this.minecraft.execute(this::refreshStatus);
               }

            });
         }

         var1.drawString(this.minecraft.font, (String)this.serverData.name, this.getContentX() + 32 + 3, this.getContentY() + 1, -1);
         List var6 = this.minecraft.font.split(this.serverData.motd, this.getContentWidth() - 32 - 2);

         for(int var7 = 0; var7 < Math.min(var6.size(), 2); ++var7) {
            Font var10001 = this.minecraft.font;
            FormattedCharSequence var10002 = (FormattedCharSequence)var6.get(var7);
            int var10003 = this.getContentX() + 32 + 3;
            int var10004 = this.getContentY() + 12;
            Objects.requireNonNull(this.minecraft.font);
            var1.drawString(var10001, var10002, var10003, var10004 + 9 * var7, -8355712);
         }

         this.drawIcon(var1, this.getContentX(), this.getContentY(), this.icon.textureLocation());
         int var15 = ServerSelectionList.this.children().indexOf(this);
         if (this.serverData.state() == ServerData.State.PINGING) {
            int var8 = (int)(Util.getMillis() / 100L + (long)(var15 * 2) & 7L);
            if (var8 > 4) {
               var8 = 8 - var8;
            }

            ResourceLocation var17;
            switch (var8) {
               case 1 -> var17 = ServerSelectionList.PINGING_2_SPRITE;
               case 2 -> var17 = ServerSelectionList.PINGING_3_SPRITE;
               case 3 -> var17 = ServerSelectionList.PINGING_4_SPRITE;
               case 4 -> var17 = ServerSelectionList.PINGING_5_SPRITE;
               default -> var17 = ServerSelectionList.PINGING_1_SPRITE;
            }

            this.statusIcon = var17;
         }

         int var16 = this.getContentRight() - 10 - 5;
         if (this.statusIcon != null) {
            var1.blitSprite(RenderPipelines.GUI_TEXTURED, (ResourceLocation)this.statusIcon, var16, this.getContentY(), 10, 8);
         }

         byte[] var9 = this.serverData.getIconBytes();
         if (!Arrays.equals(var9, this.lastIconBytes)) {
            if (this.uploadServerIcon(var9)) {
               this.lastIconBytes = var9;
            } else {
               this.serverData.setIconBytes((byte[])null);
               this.updateServerList();
            }
         }

         Object var10 = this.serverData.state() == ServerData.State.INCOMPATIBLE ? this.serverData.version.copy().withStyle(ChatFormatting.RED) : this.serverData.status;
         int var11 = this.minecraft.font.width((FormattedText)var10);
         int var12 = var16 - var11 - 5;
         var1.drawString(this.minecraft.font, (Component)var10, var12, this.getContentY() + 1, -8355712);
         if (this.statusIconTooltip != null && var2 >= var16 && var2 <= var16 + 10 && var3 >= this.getContentY() && var3 <= this.getContentY() + 8) {
            var1.setTooltipForNextFrame(this.statusIconTooltip, var2, var3);
         } else if (this.onlinePlayersTooltip != null && var2 >= var12 && var2 <= var12 + var11 && var3 >= this.getContentY()) {
            int var18 = this.getContentY() - 1;
            Objects.requireNonNull(this.minecraft.font);
            if (var3 <= var18 + 9) {
               var1.setTooltipForNextFrame(Lists.transform(this.onlinePlayersTooltip, Component::getVisualOrderText), var2, var3);
            }
         }

         if ((Boolean)this.minecraft.options.touchscreen().get() || var4) {
            var1.fill(this.getContentX(), this.getContentY(), this.getContentX() + 32, this.getContentY() + 32, -1601138544);
            int var13 = var2 - this.getContentX();
            int var14 = var3 - this.getContentY();
            if (this.mouseOverRightHalf(var13, var14, 32)) {
               var1.blitSprite(RenderPipelines.GUI_TEXTURED, (ResourceLocation)ServerSelectionList.JOIN_HIGHLIGHTED_SPRITE, this.getContentX(), this.getContentY(), 32, 32);
               ServerSelectionList.this.handleCursor(var1);
            } else {
               var1.blitSprite(RenderPipelines.GUI_TEXTURED, (ResourceLocation)ServerSelectionList.JOIN_SPRITE, this.getContentX(), this.getContentY(), 32, 32);
            }

            if (var15 > 0) {
               if (this.mouseOverTopLeftQuarter(var13, var14, 32)) {
                  var1.blitSprite(RenderPipelines.GUI_TEXTURED, (ResourceLocation)ServerSelectionList.MOVE_UP_HIGHLIGHTED_SPRITE, this.getContentX(), this.getContentY(), 32, 32);
                  ServerSelectionList.this.handleCursor(var1);
               } else {
                  var1.blitSprite(RenderPipelines.GUI_TEXTURED, (ResourceLocation)ServerSelectionList.MOVE_UP_SPRITE, this.getContentX(), this.getContentY(), 32, 32);
               }
            }

            if (var15 < this.screen.getServers().size() - 1) {
               if (this.mouseOverBottomLeftQuarter(var13, var14, 32)) {
                  var1.blitSprite(RenderPipelines.GUI_TEXTURED, (ResourceLocation)ServerSelectionList.MOVE_DOWN_HIGHLIGHTED_SPRITE, this.getContentX(), this.getContentY(), 32, 32);
                  ServerSelectionList.this.handleCursor(var1);
               } else {
                  var1.blitSprite(RenderPipelines.GUI_TEXTURED, (ResourceLocation)ServerSelectionList.MOVE_DOWN_SPRITE, this.getContentX(), this.getContentY(), 32, 32);
               }
            }
         }

      }

      private void refreshStatus() {
         this.onlinePlayersTooltip = null;
         switch (this.serverData.state()) {
            case INITIAL:
            case PINGING:
               this.statusIcon = ServerSelectionList.PING_1_SPRITE;
               this.statusIconTooltip = ServerSelectionList.PINGING_STATUS;
               break;
            case INCOMPATIBLE:
               this.statusIcon = ServerSelectionList.INCOMPATIBLE_SPRITE;
               this.statusIconTooltip = ServerSelectionList.INCOMPATIBLE_STATUS;
               this.onlinePlayersTooltip = this.serverData.playerList;
               break;
            case UNREACHABLE:
               this.statusIcon = ServerSelectionList.UNREACHABLE_SPRITE;
               this.statusIconTooltip = ServerSelectionList.NO_CONNECTION_STATUS;
               break;
            case SUCCESSFUL:
               if (this.serverData.ping < 150L) {
                  this.statusIcon = ServerSelectionList.PING_5_SPRITE;
               } else if (this.serverData.ping < 300L) {
                  this.statusIcon = ServerSelectionList.PING_4_SPRITE;
               } else if (this.serverData.ping < 600L) {
                  this.statusIcon = ServerSelectionList.PING_3_SPRITE;
               } else if (this.serverData.ping < 1000L) {
                  this.statusIcon = ServerSelectionList.PING_2_SPRITE;
               } else {
                  this.statusIcon = ServerSelectionList.PING_1_SPRITE;
               }

               this.statusIconTooltip = Component.translatable("multiplayer.status.ping", this.serverData.ping);
               this.onlinePlayersTooltip = this.serverData.playerList;
         }

      }

      public void updateServerList() {
         this.screen.getServers().save();
      }

      protected void drawIcon(GuiGraphics var1, int var2, int var3, ResourceLocation var4) {
         var1.blit(RenderPipelines.GUI_TEXTURED, var4, var2, var3, 0.0F, 0.0F, 32, 32, 32, 32);
      }

      private boolean uploadServerIcon(byte @Nullable [] var1) {
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

      public boolean keyPressed(KeyEvent var1) {
         if (var1.isSelection()) {
            this.join();
            return true;
         } else {
            if (var1.hasShiftDown()) {
               ServerSelectionList var2 = this.screen.serverSelectionList;
               int var3 = var2.children().indexOf(this);
               if (var3 == -1) {
                  return true;
               }

               if (var1.isDown() && var3 < this.screen.getServers().size() - 1 || var1.isUp() && var3 > 0) {
                  this.swap(var3, var1.isDown() ? var3 + 1 : var3 - 1);
                  return true;
               }
            }

            return super.keyPressed(var1);
         }
      }

      public void join() {
         this.screen.join(this.serverData);
      }

      private void swap(int var1, int var2) {
         this.screen.getServers().swap(var1, var2);
         this.screen.serverSelectionList.swap(var1, var2);
      }

      public boolean mouseClicked(MouseButtonEvent var1, boolean var2) {
         int var3 = (int)var1.x() - this.getContentX();
         int var4 = (int)var1.y() - this.getContentY();
         if (this.mouseOverRightHalf(var3, var4, 32)) {
            this.join();
            return true;
         } else {
            int var5 = this.screen.serverSelectionList.children().indexOf(this);
            if (var5 > 0 && this.mouseOverTopLeftQuarter(var3, var4, 32)) {
               this.swap(var5, var5 - 1);
               return true;
            } else if (var5 < this.screen.getServers().size() - 1 && this.mouseOverBottomLeftQuarter(var3, var4, 32)) {
               this.swap(var5, var5 + 1);
               return true;
            } else {
               if (var2) {
                  this.join();
               }

               return super.mouseClicked(var1, var2);
            }
         }
      }

      public ServerData getServerData() {
         return this.serverData;
      }

      public Component getNarration() {
         MutableComponent var1 = Component.empty();
         var1.append((Component)Component.translatable("narrator.select", this.serverData.name));
         var1.append(CommonComponents.NARRATION_SEPARATOR);
         switch (this.serverData.state()) {
            case PINGING:
               var1.append(ServerSelectionList.PINGING_STATUS);
               break;
            case INCOMPATIBLE:
               var1.append(ServerSelectionList.INCOMPATIBLE_STATUS);
               var1.append(CommonComponents.NARRATION_SEPARATOR);
               var1.append((Component)Component.translatable("multiplayer.status.version.narration", this.serverData.version));
               var1.append(CommonComponents.NARRATION_SEPARATOR);
               var1.append((Component)Component.translatable("multiplayer.status.motd.narration", this.serverData.motd));
               break;
            case UNREACHABLE:
               var1.append(ServerSelectionList.NO_CONNECTION_STATUS);
               break;
            default:
               var1.append(ServerSelectionList.ONLINE_STATUS);
               var1.append(CommonComponents.NARRATION_SEPARATOR);
               var1.append((Component)Component.translatable("multiplayer.status.ping.narration", this.serverData.ping));
               var1.append(CommonComponents.NARRATION_SEPARATOR);
               var1.append((Component)Component.translatable("multiplayer.status.motd.narration", this.serverData.motd));
               if (this.serverData.players != null) {
                  var1.append(CommonComponents.NARRATION_SEPARATOR);
                  var1.append((Component)Component.translatable("multiplayer.status.player_count.narration", this.serverData.players.online(), this.serverData.players.max()));
                  var1.append(CommonComponents.NARRATION_SEPARATOR);
                  var1.append(ComponentUtils.formatList(this.serverData.playerList, Component.literal(", ")));
               }
         }

         return var1;
      }

      public void close() {
         this.icon.close();
      }

      boolean matches(Entry var1) {
         boolean var10000;
         if (var1 instanceof OnlineServerEntry var2) {
            if (var2.serverData == this.serverData) {
               var10000 = true;
               return var10000;
            }
         }

         var10000 = false;
         return var10000;
      }
   }
}
