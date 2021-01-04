package net.minecraft.client.gui.screens.multiplayer;

import com.google.common.collect.Lists;
import com.google.common.hash.Hashing;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.NativeImage;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
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
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureObject;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.server.LanServer;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerSelectionList extends ObjectSelectionList<ServerSelectionList.Entry> {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final ThreadPoolExecutor THREAD_POOL;
   private static final ResourceLocation ICON_MISSING;
   private static final ResourceLocation ICON_OVERLAY_LOCATION;
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

   public void setSelected(ServerSelectionList.Entry var1) {
      super.setSelected(var1);
      if (this.getSelected() instanceof ServerSelectionList.OnlineServerEntry) {
         NarratorChatListener.INSTANCE.sayNow((new TranslatableComponent("narrator.select", new Object[]{((ServerSelectionList.OnlineServerEntry)this.getSelected()).serverData.name})).getString());
      }

   }

   protected void moveSelection(int var1) {
      int var2 = this.children().indexOf(this.getSelected());
      int var3 = Mth.clamp(var2 + var1, 0, this.getItemCount() - 1);
      ServerSelectionList.Entry var4 = (ServerSelectionList.Entry)this.children().get(var3);
      super.setSelected(var4);
      if (var4 instanceof ServerSelectionList.LANHeader) {
         if (var1 <= 0 || var3 != this.getItemCount() - 1) {
            if (var1 >= 0 || var3 != 0) {
               this.moveSelection(var1);
            }
         }
      } else {
         this.ensureVisible(var4);
         this.screen.onSelectedChange();
      }
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

   // $FF: synthetic method
   public void setSelected(AbstractSelectionList.Entry var1) {
      this.setSelected((ServerSelectionList.Entry)var1);
   }

   static {
      THREAD_POOL = new ScheduledThreadPoolExecutor(5, (new ThreadFactoryBuilder()).setNameFormat("Server Pinger #%d").setDaemon(true).setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(LOGGER)).build());
      ICON_MISSING = new ResourceLocation("textures/misc/unknown_server.png");
      ICON_OVERLAY_LOCATION = new ResourceLocation("textures/gui/server_selection.png");
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

      public void render(int var1, int var2, int var3, int var4, int var5, int var6, int var7, boolean var8, float var9) {
         if (!this.serverData.pinged) {
            this.serverData.pinged = true;
            this.serverData.ping = -2L;
            this.serverData.motd = "";
            this.serverData.status = "";
            ServerSelectionList.THREAD_POOL.submit(() -> {
               try {
                  this.screen.getPinger().pingServer(this.serverData);
               } catch (UnknownHostException var2) {
                  this.serverData.ping = -1L;
                  this.serverData.motd = ChatFormatting.DARK_RED + I18n.get("multiplayer.status.cannot_resolve");
               } catch (Exception var3) {
                  this.serverData.ping = -1L;
                  this.serverData.motd = ChatFormatting.DARK_RED + I18n.get("multiplayer.status.cannot_connect");
               }

            });
         }

         boolean var10 = this.serverData.protocol > SharedConstants.getCurrentVersion().getProtocolVersion();
         boolean var11 = this.serverData.protocol < SharedConstants.getCurrentVersion().getProtocolVersion();
         boolean var12 = var10 || var11;
         this.minecraft.font.draw(this.serverData.name, (float)(var3 + 32 + 3), (float)(var2 + 1), 16777215);
         List var13 = this.minecraft.font.split(this.serverData.motd, var4 - 32 - 2);

         for(int var14 = 0; var14 < Math.min(var13.size(), 2); ++var14) {
            Font var10000 = this.minecraft.font;
            String var10001 = (String)var13.get(var14);
            float var10002 = (float)(var3 + 32 + 3);
            int var10003 = var2 + 12;
            this.minecraft.font.getClass();
            var10000.draw(var10001, var10002, (float)(var10003 + 9 * var14), 8421504);
         }

         String var24 = var12 ? ChatFormatting.DARK_RED + this.serverData.version : this.serverData.status;
         int var15 = this.minecraft.font.width(var24);
         this.minecraft.font.draw(var24, (float)(var3 + var4 - var15 - 15 - 2), (float)(var2 + 1), 8421504);
         byte var16 = 0;
         String var18 = null;
         int var17;
         String var19;
         if (var12) {
            var17 = 5;
            var19 = I18n.get(var10 ? "multiplayer.status.client_out_of_date" : "multiplayer.status.server_out_of_date");
            var18 = this.serverData.playerList;
         } else if (this.serverData.pinged && this.serverData.ping != -2L) {
            if (this.serverData.ping < 0L) {
               var17 = 5;
            } else if (this.serverData.ping < 150L) {
               var17 = 0;
            } else if (this.serverData.ping < 300L) {
               var17 = 1;
            } else if (this.serverData.ping < 600L) {
               var17 = 2;
            } else if (this.serverData.ping < 1000L) {
               var17 = 3;
            } else {
               var17 = 4;
            }

            if (this.serverData.ping < 0L) {
               var19 = I18n.get("multiplayer.status.no_connection");
            } else {
               var19 = this.serverData.ping + "ms";
               var18 = this.serverData.playerList;
            }
         } else {
            var16 = 1;
            var17 = (int)(Util.getMillis() / 100L + (long)(var1 * 2) & 7L);
            if (var17 > 4) {
               var17 = 8 - var17;
            }

            var19 = I18n.get("multiplayer.status.pinging");
         }

         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         this.minecraft.getTextureManager().bind(GuiComponent.GUI_ICONS_LOCATION);
         GuiComponent.blit(var3 + var4 - 15, var2, (float)(var16 * 10), (float)(176 + var17 * 8), 10, 8, 256, 256);
         if (this.serverData.getIconB64() != null && !this.serverData.getIconB64().equals(this.lastIconB64)) {
            this.lastIconB64 = this.serverData.getIconB64();
            this.loadServerIcon();
            this.screen.getServers().save();
         }

         if (this.icon != null) {
            this.drawIcon(var3, var2, this.iconLocation);
         } else {
            this.drawIcon(var3, var2, ServerSelectionList.ICON_MISSING);
         }

         int var20 = var6 - var3;
         int var21 = var7 - var2;
         if (var20 >= var4 - 15 && var20 <= var4 - 5 && var21 >= 0 && var21 <= 8) {
            this.screen.setToolTip(var19);
         } else if (var20 >= var4 - var15 - 15 - 2 && var20 <= var4 - 15 - 2 && var21 >= 0 && var21 <= 8) {
            this.screen.setToolTip(var18);
         }

         if (this.minecraft.options.touchscreen || var8) {
            this.minecraft.getTextureManager().bind(ServerSelectionList.ICON_OVERLAY_LOCATION);
            GuiComponent.fill(var3, var2, var3 + 32, var2 + 32, -1601138544);
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            int var22 = var6 - var3;
            int var23 = var7 - var2;
            if (this.canJoin()) {
               if (var22 < 32 && var22 > 16) {
                  GuiComponent.blit(var3, var2, 0.0F, 32.0F, 32, 32, 256, 256);
               } else {
                  GuiComponent.blit(var3, var2, 0.0F, 0.0F, 32, 32, 256, 256);
               }
            }

            if (var1 > 0) {
               if (var22 < 16 && var23 < 16) {
                  GuiComponent.blit(var3, var2, 96.0F, 32.0F, 32, 32, 256, 256);
               } else {
                  GuiComponent.blit(var3, var2, 96.0F, 0.0F, 32, 32, 256, 256);
               }
            }

            if (var1 < this.screen.getServers().size() - 1) {
               if (var22 < 16 && var23 > 16) {
                  GuiComponent.blit(var3, var2, 64.0F, 32.0F, 32, 32, 256, 256);
               } else {
                  GuiComponent.blit(var3, var2, 64.0F, 0.0F, 32, 32, 256, 256);
               }
            }
         }

      }

      protected void drawIcon(int var1, int var2, ResourceLocation var3) {
         this.minecraft.getTextureManager().bind(var3);
         GlStateManager.enableBlend();
         GuiComponent.blit(var1, var2, 0.0F, 0.0F, 32, 32, 32, 32);
         GlStateManager.disableBlend();
      }

      private boolean canJoin() {
         return true;
      }

      private void loadServerIcon() {
         String var1 = this.serverData.getIconB64();
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

               this.minecraft.getTextureManager().register((ResourceLocation)this.iconLocation, (TextureObject)this.icon);
            } catch (Throwable var3) {
               ServerSelectionList.LOGGER.error("Invalid icon for server {} ({})", this.serverData.name, this.serverData.ip, var3);
               this.serverData.setIconB64((String)null);
            }
         }

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
               int var13 = Screen.hasShiftDown() ? 0 : var10 - 1;
               this.screen.getServers().swap(var10, var13);
               if (this.screen.serverSelectionList.getSelected() == this) {
                  this.screen.setSelected(this);
               }

               this.screen.serverSelectionList.updateOnlineServers(this.screen.getServers());
               return true;
            }

            if (var6 < 16.0D && var8 > 16.0D && var10 < this.screen.getServers().size() - 1) {
               ServerList var11 = this.screen.getServers();
               int var12 = Screen.hasShiftDown() ? var11.size() - 1 : var10 + 1;
               var11.swap(var10, var12);
               if (this.screen.serverSelectionList.getSelected() == this) {
                  this.screen.setSelected(this);
               }

               this.screen.serverSelectionList.updateOnlineServers(var11);
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

      public void render(int var1, int var2, int var3, int var4, int var5, int var6, int var7, boolean var8, float var9) {
         this.minecraft.font.draw(I18n.get("lanServer.title"), (float)(var3 + 32 + 3), (float)(var2 + 1), 16777215);
         this.minecraft.font.draw(this.serverData.getMotd(), (float)(var3 + 32 + 3), (float)(var2 + 12), 8421504);
         if (this.minecraft.options.hideServerAddress) {
            this.minecraft.font.draw(I18n.get("selectServer.hiddenAddress"), (float)(var3 + 32 + 3), (float)(var2 + 12 + 11), 3158064);
         } else {
            this.minecraft.font.draw(this.serverData.getAddress(), (float)(var3 + 32 + 3), (float)(var2 + 12 + 11), 3158064);
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

      public void render(int var1, int var2, int var3, int var4, int var5, int var6, int var7, boolean var8, float var9) {
         int var10000 = var2 + var5 / 2;
         this.minecraft.font.getClass();
         int var10 = var10000 - 9 / 2;
         this.minecraft.font.draw(I18n.get("lanServer.scanning"), (float)(this.minecraft.screen.width / 2 - this.minecraft.font.width(I18n.get("lanServer.scanning")) / 2), (float)var10, 16777215);
         String var11;
         switch((int)(Util.getMillis() / 300L % 4L)) {
         case 0:
         default:
            var11 = "O o o";
            break;
         case 1:
         case 3:
            var11 = "o O o";
            break;
         case 2:
            var11 = "o o O";
         }

         Font var12 = this.minecraft.font;
         float var10002 = (float)(this.minecraft.screen.width / 2 - this.minecraft.font.width(var11) / 2);
         this.minecraft.font.getClass();
         var12.draw(var11, var10002, (float)(var10 + 9), 8421504);
      }
   }

   public abstract static class Entry extends ObjectSelectionList.Entry<ServerSelectionList.Entry> {
      public Entry() {
         super();
      }
   }
}
