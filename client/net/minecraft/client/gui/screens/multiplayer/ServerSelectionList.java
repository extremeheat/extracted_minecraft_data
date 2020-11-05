package net.minecraft.client.gui.screens.multiplayer;

import com.google.common.collect.Lists;
import com.google.common.hash.Hashing;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.DefaultUncaughtExceptionHandler;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.server.LanServer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerSelectionList extends ObjectSelectionList<ServerSelectionList.Entry> {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final ThreadPoolExecutor THREAD_POOL;
   private static final ResourceLocation ICON_MISSING;
   private static final ResourceLocation ICON_OVERLAY_LOCATION;
   private static final Component SCANNING_LABEL;
   private static final Component CANT_RESOLVE_TEXT;
   private static final Component CANT_CONNECT_TEXT;
   private static final Component INCOMPATIBLE_TOOLTIP;
   private static final Component NO_CONNECTION_TOOLTIP;
   private static final Component PINGING_TOOLTIP;
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
      this.onlineServers.forEach(this::addEntry);
      this.addEntry(this.lanHeader);
      this.networkServers.forEach(this::addEntry);
   }

   public void setSelected(@Nullable ServerSelectionList.Entry var1) {
      super.setSelected(var1);
      if (this.getSelected() instanceof ServerSelectionList.OnlineServerEntry) {
         NarratorChatListener.INSTANCE.sayNow((new TranslatableComponent("narrator.select", new Object[]{((ServerSelectionList.OnlineServerEntry)this.getSelected()).serverData.name})).getString());
      }

      this.screen.onSelectedChange();
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      ServerSelectionList.Entry var4 = (ServerSelectionList.Entry)this.getSelected();
      return var4 != null && var4.keyPressed(var1, var2, var3) || super.keyPressed(var1, var2, var3);
   }

   protected void moveSelection(AbstractSelectionList.SelectionDirection var1) {
      this.moveSelection(var1, (var0) -> {
         return !(var0 instanceof ServerSelectionList.LANHeader);
      });
   }

   public void updateOnlineServers(ServerList var1) {
      this.onlineServers.clear();

      for(int var2 = 0; var2 < var1.size(); ++var2) {
         this.onlineServers.add(new ServerSelectionList.OnlineServerEntry(this.screen, var1.get(var2)));
      }

      this.refreshEntries();
   }

   public void updateNetworkServers(List<LanServer> var1) {
      this.networkServers.clear();
      Iterator var2 = var1.iterator();

      while(var2.hasNext()) {
         LanServer var3 = (LanServer)var2.next();
         this.networkServers.add(new ServerSelectionList.NetworkServerEntry(this.screen, var3));
      }

      this.refreshEntries();
   }

   protected int getScrollbarPosition() {
      return super.getScrollbarPosition() + 30;
   }

   public int getRowWidth() {
      return super.getRowWidth() + 85;
   }

   protected boolean isFocused() {
      return this.screen.getFocused() == this;
   }

   static {
      THREAD_POOL = new ScheduledThreadPoolExecutor(5, (new ThreadFactoryBuilder()).setNameFormat("Server Pinger #%d").setDaemon(true).setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(LOGGER)).build());
      ICON_MISSING = new ResourceLocation("textures/misc/unknown_server.png");
      ICON_OVERLAY_LOCATION = new ResourceLocation("textures/gui/server_selection.png");
      SCANNING_LABEL = new TranslatableComponent("lanServer.scanning");
      CANT_RESOLVE_TEXT = (new TranslatableComponent("multiplayer.status.cannot_resolve")).withStyle(ChatFormatting.DARK_RED);
      CANT_CONNECT_TEXT = (new TranslatableComponent("multiplayer.status.cannot_connect")).withStyle(ChatFormatting.DARK_RED);
      INCOMPATIBLE_TOOLTIP = new TranslatableComponent("multiplayer.status.incompatible");
      NO_CONNECTION_TOOLTIP = new TranslatableComponent("multiplayer.status.no_connection");
      PINGING_TOOLTIP = new TranslatableComponent("multiplayer.status.pinging");
   }

   public class OnlineServerEntry extends ServerSelectionList.Entry {
      private final JoinMultiplayerScreen screen;
      private final Minecraft minecraft;
      private final ServerData serverData;
      private final ResourceLocation iconLocation;
      private String lastIconB64;
      private DynamicTexture icon;
      private long lastClickTime;

      protected OnlineServerEntry(JoinMultiplayerScreen var2, ServerData var3) {
         super();
         this.screen = var2;
         this.serverData = var3;
         this.minecraft = Minecraft.getInstance();
         this.iconLocation = new ResourceLocation("servers/" + Hashing.sha1().hashUnencodedChars(var3.ip) + "/icon");
         this.icon = (DynamicTexture)this.minecraft.getTextureManager().getTexture(this.iconLocation);
      }

      public void render(PoseStack var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9, float var10) {
         if (!this.serverData.pinged) {
            this.serverData.pinged = true;
            this.serverData.ping = -2L;
            this.serverData.motd = TextComponent.EMPTY;
            this.serverData.status = TextComponent.EMPTY;
            ServerSelectionList.THREAD_POOL.submit(() -> {
               try {
                  this.screen.getPinger().pingServer(this.serverData, () -> {
                     this.minecraft.execute(this::updateServerList);
                  });
               } catch (UnknownHostException var2) {
                  this.serverData.ping = -1L;
                  this.serverData.motd = ServerSelectionList.CANT_RESOLVE_TEXT;
               } catch (Exception var3) {
                  this.serverData.ping = -1L;
                  this.serverData.motd = ServerSelectionList.CANT_CONNECT_TEXT;
               }

            });
         }

         boolean var11 = this.serverData.protocol != SharedConstants.getCurrentVersion().getProtocolVersion();
         this.minecraft.font.draw(var1, this.serverData.name, (float)(var4 + 32 + 3), (float)(var3 + 1), 16777215);
         List var12 = this.minecraft.font.split(this.serverData.motd, var5 - 32 - 2);

         for(int var13 = 0; var13 < Math.min(var12.size(), 2); ++var13) {
            Font var10000 = this.minecraft.font;
            FormattedCharSequence var10002 = (FormattedCharSequence)var12.get(var13);
            float var10003 = (float)(var4 + 32 + 3);
            int var10004 = var3 + 12;
            this.minecraft.font.getClass();
            var10000.draw(var1, var10002, var10003, (float)(var10004 + 9 * var13), 8421504);
         }

         Object var24 = var11 ? this.serverData.version.copy().withStyle(ChatFormatting.RED) : this.serverData.status;
         int var14 = this.minecraft.font.width((FormattedText)var24);
         this.minecraft.font.draw(var1, (Component)var24, (float)(var4 + var5 - var14 - 15 - 2), (float)(var3 + 1), 8421504);
         byte var15 = 0;
         int var16;
         List var17;
         Object var18;
         if (var11) {
            var16 = 5;
            var18 = ServerSelectionList.INCOMPATIBLE_TOOLTIP;
            var17 = this.serverData.playerList;
         } else if (this.serverData.pinged && this.serverData.ping != -2L) {
            if (this.serverData.ping < 0L) {
               var16 = 5;
            } else if (this.serverData.ping < 150L) {
               var16 = 0;
            } else if (this.serverData.ping < 300L) {
               var16 = 1;
            } else if (this.serverData.ping < 600L) {
               var16 = 2;
            } else if (this.serverData.ping < 1000L) {
               var16 = 3;
            } else {
               var16 = 4;
            }

            if (this.serverData.ping < 0L) {
               var18 = ServerSelectionList.NO_CONNECTION_TOOLTIP;
               var17 = Collections.emptyList();
            } else {
               var18 = new TranslatableComponent("multiplayer.status.ping", new Object[]{this.serverData.ping});
               var17 = this.serverData.playerList;
            }
         } else {
            var15 = 1;
            var16 = (int)(Util.getMillis() / 100L + (long)(var2 * 2) & 7L);
            if (var16 > 4) {
               var16 = 8 - var16;
            }

            var18 = ServerSelectionList.PINGING_TOOLTIP;
            var17 = Collections.emptyList();
         }

         RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         this.minecraft.getTextureManager().bind(GuiComponent.GUI_ICONS_LOCATION);
         GuiComponent.blit(var1, var4 + var5 - 15, var3, (float)(var15 * 10), (float)(176 + var16 * 8), 10, 8, 256, 256);
         String var19 = this.serverData.getIconB64();
         if (!Objects.equals(var19, this.lastIconB64)) {
            if (this.uploadServerIcon(var19)) {
               this.lastIconB64 = var19;
            } else {
               this.serverData.setIconB64((String)null);
               this.updateServerList();
            }
         }

         if (this.icon != null) {
            this.drawIcon(var1, var4, var3, this.iconLocation);
         } else {
            this.drawIcon(var1, var4, var3, ServerSelectionList.ICON_MISSING);
         }

         int var20 = var7 - var4;
         int var21 = var8 - var3;
         if (var20 >= var5 - 15 && var20 <= var5 - 5 && var21 >= 0 && var21 <= 8) {
            this.screen.setToolTip(Collections.singletonList(var18));
         } else if (var20 >= var5 - var14 - 15 - 2 && var20 <= var5 - 15 - 2 && var21 >= 0 && var21 <= 8) {
            this.screen.setToolTip(var17);
         }

         if (this.minecraft.options.touchscreen || var9) {
            this.minecraft.getTextureManager().bind(ServerSelectionList.ICON_OVERLAY_LOCATION);
            GuiComponent.fill(var1, var4, var3, var4 + 32, var3 + 32, -1601138544);
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            int var22 = var7 - var4;
            int var23 = var8 - var3;
            if (this.canJoin()) {
               if (var22 < 32 && var22 > 16) {
                  GuiComponent.blit(var1, var4, var3, 0.0F, 32.0F, 32, 32, 256, 256);
               } else {
                  GuiComponent.blit(var1, var4, var3, 0.0F, 0.0F, 32, 32, 256, 256);
               }
            }

            if (var2 > 0) {
               if (var22 < 16 && var23 < 16) {
                  GuiComponent.blit(var1, var4, var3, 96.0F, 32.0F, 32, 32, 256, 256);
               } else {
                  GuiComponent.blit(var1, var4, var3, 96.0F, 0.0F, 32, 32, 256, 256);
               }
            }

            if (var2 < this.screen.getServers().size() - 1) {
               if (var22 < 16 && var23 > 16) {
                  GuiComponent.blit(var1, var4, var3, 64.0F, 32.0F, 32, 32, 256, 256);
               } else {
                  GuiComponent.blit(var1, var4, var3, 64.0F, 0.0F, 32, 32, 256, 256);
               }
            }
         }

      }

      public void updateServerList() {
         this.screen.getServers().save();
      }

      protected void drawIcon(PoseStack var1, int var2, int var3, ResourceLocation var4) {
         this.minecraft.getTextureManager().bind(var4);
         RenderSystem.enableBlend();
         GuiComponent.blit(var1, var2, var3, 0.0F, 0.0F, 32, 32, 32, 32);
         RenderSystem.disableBlend();
      }

      private boolean canJoin() {
         return true;
      }

      private boolean uploadServerIcon(@Nullable String var1) {
         if (var1 == null) {
            this.minecraft.getTextureManager().release(this.iconLocation);
            if (this.icon != null && this.icon.getPixels() != null) {
               this.icon.getPixels().close();
            }

            this.icon = null;
         } else {
            try {
               NativeImage var2 = NativeImage.fromBase64(var1);
               Validate.validState(var2.getWidth() == 64, "Must be 64 pixels wide", new Object[0]);
               Validate.validState(var2.getHeight() == 64, "Must be 64 pixels high", new Object[0]);
               if (this.icon == null) {
                  this.icon = new DynamicTexture(var2);
               } else {
                  this.icon.setPixels(var2);
                  this.icon.upload();
               }

               this.minecraft.getTextureManager().register((ResourceLocation)this.iconLocation, (AbstractTexture)this.icon);
            } catch (Throwable var3) {
               ServerSelectionList.LOGGER.error("Invalid icon for server {} ({})", this.serverData.name, this.serverData.ip, var3);
               return false;
            }
         }

         return true;
      }

      public boolean keyPressed(int var1, int var2, int var3) {
         if (Screen.hasShiftDown()) {
            ServerSelectionList var4 = this.screen.serverSelectionList;
            int var5 = var4.children().indexOf(this);
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
         ServerSelectionList.Entry var3 = (ServerSelectionList.Entry)this.screen.serverSelectionList.children().get(var2);
         this.screen.serverSelectionList.setSelected(var3);
         ServerSelectionList.this.ensureVisible(var3);
      }

      public boolean mouseClicked(double var1, double var3, int var5) {
         double var6 = var1 - (double)ServerSelectionList.this.getRowLeft();
         double var8 = var3 - (double)ServerSelectionList.this.getRowTop(ServerSelectionList.this.children().indexOf(this));
         if (var6 <= 32.0D) {
            if (var6 < 32.0D && var6 > 16.0D && this.canJoin()) {
               this.screen.setSelected(this);
               this.screen.joinSelectedServer();
               return true;
            }

            int var10 = this.screen.serverSelectionList.children().indexOf(this);
            if (var6 < 16.0D && var8 < 16.0D && var10 > 0) {
               this.swap(var10, var10 - 1);
               return true;
            }

            if (var6 < 16.0D && var8 > 16.0D && var10 < this.screen.getServers().size() - 1) {
               this.swap(var10, var10 + 1);
               return true;
            }
         }

         this.screen.setSelected(this);
         if (Util.getMillis() - this.lastClickTime < 250L) {
            this.screen.joinSelectedServer();
         }

         this.lastClickTime = Util.getMillis();
         return false;
      }

      public ServerData getServerData() {
         return this.serverData;
      }
   }

   public static class NetworkServerEntry extends ServerSelectionList.Entry {
      private static final Component LAN_SERVER_HEADER = new TranslatableComponent("lanServer.title");
      private static final Component HIDDEN_ADDRESS_TEXT = new TranslatableComponent("selectServer.hiddenAddress");
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

      public void render(PoseStack var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9, float var10) {
         this.minecraft.font.draw(var1, LAN_SERVER_HEADER, (float)(var4 + 32 + 3), (float)(var3 + 1), 16777215);
         this.minecraft.font.draw(var1, this.serverData.getMotd(), (float)(var4 + 32 + 3), (float)(var3 + 12), 8421504);
         if (this.minecraft.options.hideServerAddress) {
            this.minecraft.font.draw(var1, HIDDEN_ADDRESS_TEXT, (float)(var4 + 32 + 3), (float)(var3 + 12 + 11), 3158064);
         } else {
            this.minecraft.font.draw(var1, this.serverData.getAddress(), (float)(var4 + 32 + 3), (float)(var3 + 12 + 11), 3158064);
         }

      }

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
   }

   public static class LANHeader extends ServerSelectionList.Entry {
      private final Minecraft minecraft = Minecraft.getInstance();

      public LANHeader() {
         super();
      }

      public void render(PoseStack var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9, float var10) {
         int var10000 = var3 + var6 / 2;
         this.minecraft.font.getClass();
         int var11 = var10000 - 9 / 2;
         this.minecraft.font.draw(var1, ServerSelectionList.SCANNING_LABEL, (float)(this.minecraft.screen.width / 2 - this.minecraft.font.width((FormattedText)ServerSelectionList.SCANNING_LABEL) / 2), (float)var11, 16777215);
         String var12;
         switch((int)(Util.getMillis() / 300L % 4L)) {
         case 0:
         default:
            var12 = "O o o";
            break;
         case 1:
         case 3:
            var12 = "o O o";
            break;
         case 2:
            var12 = "o o O";
         }

         Font var13 = this.minecraft.font;
         float var10003 = (float)(this.minecraft.screen.width / 2 - this.minecraft.font.width(var12) / 2);
         this.minecraft.font.getClass();
         var13.draw(var1, var12, var10003, (float)(var11 + 9), 8421504);
      }
   }

   public abstract static class Entry extends ObjectSelectionList.Entry<ServerSelectionList.Entry> {
      public Entry() {
         super();
      }
   }
}
