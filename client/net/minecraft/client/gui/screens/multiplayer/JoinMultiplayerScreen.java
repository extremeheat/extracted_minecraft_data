package net.minecraft.client.gui.screens.multiplayer;

import com.mojang.logging.LogUtils;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.EqualSpacingLayout;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.layouts.SpacerElement;
import net.minecraft.client.gui.navigation.CommonInputs;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.gui.screens.DirectJoinServerScreen;
import net.minecraft.client.gui.screens.EditServerScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.client.multiplayer.ServerStatusPinger;
import net.minecraft.client.multiplayer.TransferState;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.server.LanServer;
import net.minecraft.client.server.LanServerDetection;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.slf4j.Logger;

public class JoinMultiplayerScreen extends Screen {
   public static final int BUTTON_ROW_WIDTH = 308;
   public static final int TOP_ROW_BUTTON_WIDTH = 100;
   public static final int LOWER_ROW_BUTTON_WIDTH = 74;
   public static final int FOOTER_HEIGHT = 64;
   private static final Logger LOGGER = LogUtils.getLogger();
   private final ServerStatusPinger pinger = new ServerStatusPinger();
   private final Screen lastScreen;
   protected ServerSelectionList serverSelectionList;
   private ServerList servers;
   private Button editButton;
   private Button selectButton;
   private Button deleteButton;
   private ServerData editingServer;
   private LanServerDetection.LanServerList lanServerList;
   @Nullable
   private LanServerDetection.LanServerDetector lanServerDetector;
   private boolean initedOnce;

   public JoinMultiplayerScreen(Screen var1) {
      super(Component.translatable("multiplayer.title"));
      this.lastScreen = var1;
   }

   protected void init() {
      if (this.initedOnce) {
         this.serverSelectionList.setRectangle(this.width, this.height - 64 - 32, 0, 32);
      } else {
         this.initedOnce = true;
         this.servers = new ServerList(this.minecraft);
         this.servers.load();
         this.lanServerList = new LanServerDetection.LanServerList();

         try {
            this.lanServerDetector = new LanServerDetection.LanServerDetector(this.lanServerList);
            this.lanServerDetector.start();
         } catch (Exception var8) {
            LOGGER.warn("Unable to start LAN server detection: {}", var8.getMessage());
         }

         this.serverSelectionList = new ServerSelectionList(this, this.minecraft, this.width, this.height - 64 - 32, 32, 36);
         this.serverSelectionList.updateOnlineServers(this.servers);
      }

      this.addRenderableWidget(this.serverSelectionList);
      this.selectButton = (Button)this.addRenderableWidget(Button.builder(Component.translatable("selectServer.select"), (var1x) -> {
         this.joinSelectedServer();
      }).width(100).build());
      Button var1 = (Button)this.addRenderableWidget(Button.builder(Component.translatable("selectServer.direct"), (var1x) -> {
         this.editingServer = new ServerData(I18n.get("selectServer.defaultName"), "", ServerData.Type.OTHER);
         this.minecraft.setScreen(new DirectJoinServerScreen(this, this::directJoinCallback, this.editingServer));
      }).width(100).build());
      Button var2 = (Button)this.addRenderableWidget(Button.builder(Component.translatable("selectServer.add"), (var1x) -> {
         this.editingServer = new ServerData(I18n.get("selectServer.defaultName"), "", ServerData.Type.OTHER);
         this.minecraft.setScreen(new EditServerScreen(this, this::addServerCallback, this.editingServer));
      }).width(100).build());
      this.editButton = (Button)this.addRenderableWidget(Button.builder(Component.translatable("selectServer.edit"), (var1x) -> {
         ServerSelectionList.Entry var2 = (ServerSelectionList.Entry)this.serverSelectionList.getSelected();
         if (var2 instanceof ServerSelectionList.OnlineServerEntry) {
            ServerData var3 = ((ServerSelectionList.OnlineServerEntry)var2).getServerData();
            this.editingServer = new ServerData(var3.name, var3.ip, ServerData.Type.OTHER);
            this.editingServer.copyFrom(var3);
            this.minecraft.setScreen(new EditServerScreen(this, this::editServerCallback, this.editingServer));
         }

      }).width(74).build());
      this.deleteButton = (Button)this.addRenderableWidget(Button.builder(Component.translatable("selectServer.delete"), (var1x) -> {
         ServerSelectionList.Entry var2 = (ServerSelectionList.Entry)this.serverSelectionList.getSelected();
         if (var2 instanceof ServerSelectionList.OnlineServerEntry) {
            String var3 = ((ServerSelectionList.OnlineServerEntry)var2).getServerData().name;
            if (var3 != null) {
               MutableComponent var4 = Component.translatable("selectServer.deleteQuestion");
               MutableComponent var5 = Component.translatable("selectServer.deleteWarning", var3);
               MutableComponent var6 = Component.translatable("selectServer.deleteButton");
               Component var7 = CommonComponents.GUI_CANCEL;
               this.minecraft.setScreen(new ConfirmScreen(this::deleteCallback, var4, var5, var6, var7));
            }
         }

      }).width(74).build());
      Button var3 = (Button)this.addRenderableWidget(Button.builder(Component.translatable("selectServer.refresh"), (var1x) -> {
         this.refreshServerList();
      }).width(74).build());
      Button var4 = (Button)this.addRenderableWidget(Button.builder(CommonComponents.GUI_BACK, (var1x) -> {
         this.onClose();
      }).width(74).build());
      LinearLayout var5 = LinearLayout.vertical();
      EqualSpacingLayout var6 = (EqualSpacingLayout)var5.addChild(new EqualSpacingLayout(308, 20, EqualSpacingLayout.Orientation.HORIZONTAL));
      var6.addChild(this.selectButton);
      var6.addChild(var1);
      var6.addChild(var2);
      var5.addChild(SpacerElement.height(4));
      EqualSpacingLayout var7 = (EqualSpacingLayout)var5.addChild(new EqualSpacingLayout(308, 20, EqualSpacingLayout.Orientation.HORIZONTAL));
      var7.addChild(this.editButton);
      var7.addChild(this.deleteButton);
      var7.addChild(var3);
      var7.addChild(var4);
      var5.arrangeElements();
      FrameLayout.centerInRectangle(var5, 0, this.height - 64, this.width, 64);
      this.onSelectedChange();
   }

   public void onClose() {
      this.minecraft.setScreen(this.lastScreen);
   }

   public void tick() {
      super.tick();
      List var1 = this.lanServerList.takeDirtyServers();
      if (var1 != null) {
         this.serverSelectionList.updateNetworkServers(var1);
      }

      this.pinger.tick();
   }

   public void removed() {
      if (this.lanServerDetector != null) {
         this.lanServerDetector.interrupt();
         this.lanServerDetector = null;
      }

      this.pinger.removeAll();
      this.serverSelectionList.removed();
   }

   private void refreshServerList() {
      this.minecraft.setScreen(new JoinMultiplayerScreen(this.lastScreen));
   }

   private void deleteCallback(boolean var1) {
      ServerSelectionList.Entry var2 = (ServerSelectionList.Entry)this.serverSelectionList.getSelected();
      if (var1 && var2 instanceof ServerSelectionList.OnlineServerEntry) {
         this.servers.remove(((ServerSelectionList.OnlineServerEntry)var2).getServerData());
         this.servers.save();
         this.serverSelectionList.setSelected((ServerSelectionList.Entry)null);
         this.serverSelectionList.updateOnlineServers(this.servers);
      }

      this.minecraft.setScreen(this);
   }

   private void editServerCallback(boolean var1) {
      ServerSelectionList.Entry var2 = (ServerSelectionList.Entry)this.serverSelectionList.getSelected();
      if (var1 && var2 instanceof ServerSelectionList.OnlineServerEntry) {
         ServerData var3 = ((ServerSelectionList.OnlineServerEntry)var2).getServerData();
         var3.name = this.editingServer.name;
         var3.ip = this.editingServer.ip;
         var3.copyFrom(this.editingServer);
         this.servers.save();
         this.serverSelectionList.updateOnlineServers(this.servers);
      }

      this.minecraft.setScreen(this);
   }

   private void addServerCallback(boolean var1) {
      if (var1) {
         ServerData var2 = this.servers.unhide(this.editingServer.ip);
         if (var2 != null) {
            var2.copyNameIconFrom(this.editingServer);
            this.servers.save();
         } else {
            this.servers.add(this.editingServer, false);
            this.servers.save();
         }

         this.serverSelectionList.setSelected((ServerSelectionList.Entry)null);
         this.serverSelectionList.updateOnlineServers(this.servers);
      }

      this.minecraft.setScreen(this);
   }

   private void directJoinCallback(boolean var1) {
      if (var1) {
         ServerData var2 = this.servers.get(this.editingServer.ip);
         if (var2 == null) {
            this.servers.add(this.editingServer, true);
            this.servers.save();
            this.join(this.editingServer);
         } else {
            this.join(var2);
         }
      } else {
         this.minecraft.setScreen(this);
      }

   }

   public boolean keyPressed(int var1, int var2, int var3) {
      if (super.keyPressed(var1, var2, var3)) {
         return true;
      } else if (var1 == 294) {
         this.refreshServerList();
         return true;
      } else if (this.serverSelectionList.getSelected() != null) {
         if (CommonInputs.selected(var1)) {
            this.joinSelectedServer();
            return true;
         } else {
            return this.serverSelectionList.keyPressed(var1, var2, var3);
         }
      } else {
         return false;
      }
   }

   public void render(GuiGraphics var1, int var2, int var3, float var4) {
      super.render(var1, var2, var3, var4);
      var1.drawCenteredString(this.font, (Component)this.title, this.width / 2, 20, 16777215);
   }

   public void joinSelectedServer() {
      ServerSelectionList.Entry var1 = (ServerSelectionList.Entry)this.serverSelectionList.getSelected();
      if (var1 instanceof ServerSelectionList.OnlineServerEntry) {
         this.join(((ServerSelectionList.OnlineServerEntry)var1).getServerData());
      } else if (var1 instanceof ServerSelectionList.NetworkServerEntry) {
         LanServer var2 = ((ServerSelectionList.NetworkServerEntry)var1).getServerData();
         this.join(new ServerData(var2.getMotd(), var2.getAddress(), ServerData.Type.LAN));
      }

   }

   private void join(ServerData var1) {
      ConnectScreen.startConnecting(this, this.minecraft, ServerAddress.parseString(var1.ip), var1, false, (TransferState)null);
   }

   public void setSelected(ServerSelectionList.Entry var1) {
      this.serverSelectionList.setSelected(var1);
      this.onSelectedChange();
   }

   protected void onSelectedChange() {
      this.selectButton.active = false;
      this.editButton.active = false;
      this.deleteButton.active = false;
      ServerSelectionList.Entry var1 = (ServerSelectionList.Entry)this.serverSelectionList.getSelected();
      if (var1 != null && !(var1 instanceof ServerSelectionList.LANHeader)) {
         this.selectButton.active = true;
         if (var1 instanceof ServerSelectionList.OnlineServerEntry) {
            this.editButton.active = true;
            this.deleteButton.active = true;
         }
      }

   }

   public ServerStatusPinger getPinger() {
      return this.pinger;
   }

   public ServerList getServers() {
      return this.servers;
   }
}
