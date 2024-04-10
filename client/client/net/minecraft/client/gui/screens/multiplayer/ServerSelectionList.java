package net.minecraft.client.gui.screens.multiplayer;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import java.net.UnknownHostException;
import java.util.Arrays;
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
   static final Component SCANNING_LABEL = Component.translatable("lanServer.scanning");
   static final Component CANT_RESOLVE_TEXT = Component.translatable("multiplayer.status.cannot_resolve").withColor(-65536);
   static final Component CANT_CONNECT_TEXT = Component.translatable("multiplayer.status.cannot_connect").withColor(-65536);
   static final Component INCOMPATIBLE_STATUS = Component.translatable("multiplayer.status.incompatible");
   static final Component NO_CONNECTION_STATUS = Component.translatable("multiplayer.status.no_connection");
   static final Component PINGING_STATUS = Component.translatable("multiplayer.status.pinging");
   static final Component ONLINE_STATUS = Component.translatable("multiplayer.status.online");
   private final JoinMultiplayerScreen screen;
   private final List<ServerSelectionList.OnlineServerEntry> onlineServers = Lists.newArrayList();
   private final ServerSelectionList.Entry lanHeader = new ServerSelectionList.LANHeader();
   private final List<ServerSelectionList.NetworkServerEntry> networkServers = Lists.newArrayList();

   public ServerSelectionList(JoinMultiplayerScreen var1, Minecraft var2, int var3, int var4, int var5, int var6) {
      super(var2, var3, var4, var5, var6);
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

      for (int var2 = 0; var2 < var1.size(); var2++) {
         this.onlineServers.add(new ServerSelectionList.OnlineServerEntry(this.screen, var1.get(var2)));
      }

      this.refreshEntries();
   }

   public void updateNetworkServers(List<LanServer> var1) {
      int var2 = var1.size() - this.networkServers.size();
      this.networkServers.clear();

      for (LanServer var4 : var1) {
         this.networkServers.add(new ServerSelectionList.NetworkServerEntry(this.screen, var4));
      }

      this.refreshEntries();

      for (int var8 = this.networkServers.size() - var2; var8 < this.networkServers.size(); var8++) {
         ServerSelectionList.NetworkServerEntry var9 = this.networkServers.get(var8);
         int var5 = var8 - this.networkServers.size() + this.children().size();
         int var6 = this.getRowTop(var5);
         int var7 = this.getRowBottom(var5);
         if (var7 >= this.getY() && var6 <= this.getBottom()) {
            this.minecraft.getNarrator().say(Component.translatable("multiplayer.lan.server_found", var9.getServerNarration()));
         }
      }
   }

   @Override
   public int getRowWidth() {
      return 305;
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
         return super.mouseClicked(var1, var3, var5);
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
      private static final int SPACING = 5;
      private static final int STATUS_ICON_WIDTH = 10;
      private static final int STATUS_ICON_HEIGHT = 8;
      private final JoinMultiplayerScreen screen;
      private final Minecraft minecraft;
      private final ServerData serverData;
      private final FaviconTexture icon;
      @Nullable
      private byte[] lastIconBytes;
      private long lastClickTime;
      @Nullable
      private List<Component> onlinePlayersTooltip;
      @Nullable
      private ResourceLocation statusIcon;
      @Nullable
      private Component statusIconTooltip;

      protected OnlineServerEntry(final JoinMultiplayerScreen param2, final ServerData param3) {
         super();
         this.screen = nullx;
         this.serverData = nullxx;
         this.minecraft = Minecraft.getInstance();
         this.icon = FaviconTexture.forServer(this.minecraft.getTextureManager(), nullxx.ip);
         this.refreshStatus();
      }

      @Override
      public void render(GuiGraphics var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9, float var10) {
         if (this.serverData.state() == ServerData.State.INITIAL) {
            this.serverData.setState(ServerData.State.PINGING);
            this.serverData.motd = CommonComponents.EMPTY;
            this.serverData.status = CommonComponents.EMPTY;
            ServerSelectionList.THREAD_POOL
               .submit(
                  () -> {
                     try {
                        this.screen
                           .getPinger()
                           .pingServer(
                              this.serverData,
                              () -> this.minecraft.execute(this::updateServerList),
                              () -> {
                                 this.serverData
                                    .setState(
                                       this.serverData.protocol == SharedConstants.getCurrentVersion().getProtocolVersion()
                                          ? ServerData.State.SUCCESSFUL
                                          : ServerData.State.INCOMPATIBLE
                                    );
                                 this.minecraft.execute(this::refreshStatus);
                              }
                           );
                     } catch (UnknownHostException var2x) {
                        this.serverData.setState(ServerData.State.UNREACHABLE);
                        this.serverData.motd = ServerSelectionList.CANT_RESOLVE_TEXT;
                        this.minecraft.execute(this::refreshStatus);
                     } catch (Exception var3x) {
                        this.serverData.setState(ServerData.State.UNREACHABLE);
                        this.serverData.motd = ServerSelectionList.CANT_CONNECT_TEXT;
                        this.minecraft.execute(this::refreshStatus);
                     }
                  }
               );
         }

         var1.drawString(this.minecraft.font, this.serverData.name, var4 + 32 + 3, var3 + 1, 16777215, false);
         List var11 = this.minecraft.font.split(this.serverData.motd, var5 - 32 - 2);

         for (int var12 = 0; var12 < Math.min(var11.size(), 2); var12++) {
            var1.drawString(this.minecraft.font, (FormattedCharSequence)var11.get(var12), var4 + 32 + 3, var3 + 12 + 9 * var12, -8355712, false);
         }

         this.drawIcon(var1, var4, var3, this.icon.textureLocation());
         if (this.serverData.state() == ServerData.State.PINGING) {
            int var19 = (int)(Util.getMillis() / 100L + (long)(var2 * 2) & 7L);
            if (var19 > 4) {
               var19 = 8 - var19;
            }
            this.statusIcon = switch (var19) {
               case 1 -> ServerSelectionList.PINGING_2_SPRITE;
               case 2 -> ServerSelectionList.PINGING_3_SPRITE;
               case 3 -> ServerSelectionList.PINGING_4_SPRITE;
               case 4 -> ServerSelectionList.PINGING_5_SPRITE;
               default -> ServerSelectionList.PINGING_1_SPRITE;
            };
         }

         int var20 = var4 + var5 - 10 - 5;
         if (this.statusIcon != null) {
            var1.blitSprite(this.statusIcon, var20, var3, 10, 8);
         }

         byte[] var13 = this.serverData.getIconBytes();
         if (!Arrays.equals(var13, this.lastIconBytes)) {
            if (this.uploadServerIcon(var13)) {
               this.lastIconBytes = var13;
            } else {
               this.serverData.setIconBytes(null);
               this.updateServerList();
            }
         }

         Object var14 = this.serverData.state() == ServerData.State.INCOMPATIBLE
            ? this.serverData.version.copy().withStyle(ChatFormatting.RED)
            : this.serverData.status;
         int var15 = this.minecraft.font.width((FormattedText)var14);
         int var16 = var20 - var15 - 5;
         var1.drawString(this.minecraft.font, (Component)var14, var16, var3 + 1, -8355712, false);
         if (this.statusIconTooltip != null && var7 >= var20 && var7 <= var20 + 10 && var8 >= var3 && var8 <= var3 + 8) {
            this.screen.setTooltipForNextRenderPass(this.statusIconTooltip);
         } else if (this.onlinePlayersTooltip != null && var7 >= var16 && var7 <= var16 + var15 && var8 >= var3 && var8 <= var3 - 1 + 9) {
            this.screen.setTooltipForNextRenderPass(Lists.transform(this.onlinePlayersTooltip, Component::getVisualOrderText));
         }

         if (this.minecraft.options.touchscreen().get() || var9) {
            var1.fill(var4, var3, var4 + 32, var3 + 32, -1601138544);
            int var17 = var7 - var4;
            int var18 = var8 - var3;
            if (this.canJoin()) {
               if (var17 < 32 && var17 > 16) {
                  var1.blitSprite(ServerSelectionList.JOIN_HIGHLIGHTED_SPRITE, var4, var3, 32, 32);
               } else {
                  var1.blitSprite(ServerSelectionList.JOIN_SPRITE, var4, var3, 32, 32);
               }
            }

            if (var2 > 0) {
               if (var17 < 16 && var18 < 16) {
                  var1.blitSprite(ServerSelectionList.MOVE_UP_HIGHLIGHTED_SPRITE, var4, var3, 32, 32);
               } else {
                  var1.blitSprite(ServerSelectionList.MOVE_UP_SPRITE, var4, var3, 32, 32);
               }
            }

            if (var2 < this.screen.getServers().size() - 1) {
               if (var17 < 16 && var18 > 16) {
                  var1.blitSprite(ServerSelectionList.MOVE_DOWN_HIGHLIGHTED_SPRITE, var4, var3, 32, 32);
               } else {
                  var1.blitSprite(ServerSelectionList.MOVE_DOWN_SPRITE, var4, var3, 32, 32);
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
         return super.mouseClicked(var1, var3, var5);
      }

      public ServerData getServerData() {
         return this.serverData;
      }

      @Override
      public Component getNarration() {
         MutableComponent var1 = Component.empty();
         var1.append(Component.translatable("narrator.select", this.serverData.name));
         var1.append(CommonComponents.NARRATION_SEPARATOR);
         switch (this.serverData.state()) {
            case PINGING:
               var1.append(ServerSelectionList.PINGING_STATUS);
               break;
            case INCOMPATIBLE:
               var1.append(ServerSelectionList.INCOMPATIBLE_STATUS);
               var1.append(CommonComponents.NARRATION_SEPARATOR);
               var1.append(Component.translatable("multiplayer.status.version.narration", this.serverData.version));
               var1.append(CommonComponents.NARRATION_SEPARATOR);
               var1.append(Component.translatable("multiplayer.status.motd.narration", this.serverData.motd));
               break;
            case UNREACHABLE:
               var1.append(ServerSelectionList.NO_CONNECTION_STATUS);
               break;
            default:
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
