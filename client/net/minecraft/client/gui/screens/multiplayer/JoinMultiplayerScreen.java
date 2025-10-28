package net.minecraft.client.gui.screens.multiplayer;

import com.mojang.logging.LogUtils;
import java.util.List;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.gui.screens.DirectJoinServerScreen;
import net.minecraft.client.gui.screens.ManageServerScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.client.multiplayer.ServerStatusPinger;
import net.minecraft.client.multiplayer.TransferState;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.server.LanServerDetection;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class JoinMultiplayerScreen extends Screen {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int TOP_ROW_BUTTON_WIDTH = 100;
   private static final int LOWER_ROW_BUTTON_WIDTH = 74;
   private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this, 33, 60);
   private final ServerStatusPinger pinger = new ServerStatusPinger();
   private final Screen lastScreen;
   protected ServerSelectionList serverSelectionList;
   private ServerList servers;
   private Button editButton;
   private Button selectButton;
   private Button deleteButton;
   private ServerData editingServer;
   private LanServerDetection.LanServerList lanServerList;
   private LanServerDetection.@Nullable LanServerDetector lanServerDetector;

   public JoinMultiplayerScreen(Screen var1) {
      super(Component.translatable("multiplayer.title"));
      this.lastScreen = var1;
   }

   protected void init() {
      this.layout.addTitleHeader(this.title, this.font);
      this.servers = new ServerList(this.minecraft);
      this.servers.load();
      this.lanServerList = new LanServerDetection.LanServerList();

      try {
         this.lanServerDetector = new LanServerDetection.LanServerDetector(this.lanServerList);
         this.lanServerDetector.start();
      } catch (Exception var4) {
         LOGGER.warn("Unable to start LAN server detection: {}", var4.getMessage());
      }

      this.serverSelectionList = (ServerSelectionList)this.layout.addToContents(new ServerSelectionList(this, this.minecraft, this.width, this.layout.getContentHeight(), this.layout.getHeaderHeight(), 36));
      this.serverSelectionList.updateOnlineServers(this.servers);
      LinearLayout var1 = (LinearLayout)this.layout.addToFooter(LinearLayout.vertical().spacing(4));
      var1.defaultCellSetting().alignHorizontallyCenter();
      LinearLayout var2 = (LinearLayout)var1.addChild(LinearLayout.horizontal().spacing(4));
      LinearLayout var3 = (LinearLayout)var1.addChild(LinearLayout.horizontal().spacing(4));
      this.selectButton = (Button)var2.addChild(Button.builder(Component.translatable("selectServer.select"), (var1x) -> {
         ServerSelectionList.Entry var2 = (ServerSelectionList.Entry)this.serverSelectionList.getSelected();
         if (var2 != null) {
            var2.join();
         }

      }).width(100).build());
      var2.addChild(Button.builder(Component.translatable("selectServer.direct"), (var1x) -> {
         this.editingServer = new ServerData(I18n.get("selectServer.defaultName"), "", ServerData.Type.OTHER);
         this.minecraft.setScreen(new DirectJoinServerScreen(this, this::directJoinCallback, this.editingServer));
      }).width(100).build());
      var2.addChild(Button.builder(Component.translatable("selectServer.add"), (var1x) -> {
         this.editingServer = new ServerData("", "", ServerData.Type.OTHER);
         this.minecraft.setScreen(new ManageServerScreen(this, Component.translatable("manageServer.add.title"), this::addServerCallback, this.editingServer));
      }).width(100).build());
      this.editButton = (Button)var3.addChild(Button.builder(Component.translatable("selectServer.edit"), (var1x) -> {
         ServerSelectionList.Entry var2 = (ServerSelectionList.Entry)this.serverSelectionList.getSelected();
         if (var2 instanceof ServerSelectionList.OnlineServerEntry) {
            ServerData var3 = ((ServerSelectionList.OnlineServerEntry)var2).getServerData();
            this.editingServer = new ServerData(var3.name, var3.ip, ServerData.Type.OTHER);
            this.editingServer.copyFrom(var3);
            this.minecraft.setScreen(new ManageServerScreen(this, Component.translatable("manageServer.edit.title"), this::editServerCallback, this.editingServer));
         }

      }).width(74).build());
      this.deleteButton = (Button)var3.addChild(Button.builder(Component.translatable("selectServer.delete"), (var1x) -> {
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
      var3.addChild(Button.builder(Component.translatable("selectServer.refresh"), (var1x) -> this.refreshServerList()).width(74).build());
      var3.addChild(Button.builder(CommonComponents.GUI_BACK, (var1x) -> this.onClose()).width(74).build());
      this.layout.visitWidgets((var1x) -> {
         AbstractWidget var10000 = (AbstractWidget)this.addRenderableWidget(var1x);
      });
      this.repositionElements();
      this.onSelectedChange();
   }

   protected void repositionElements() {
      this.layout.arrangeElements();
      if (this.serverSelectionList != null) {
         this.serverSelectionList.updateSize(this.width, this.layout);
      }

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

   public boolean keyPressed(KeyEvent var1) {
      if (super.keyPressed(var1)) {
         return true;
      } else if (var1.key() == 294) {
         this.refreshServerList();
         return true;
      } else {
         return false;
      }
   }

   public void join(ServerData var1) {
      ConnectScreen.startConnecting(this, this.minecraft, ServerAddress.parseString(var1.ip), var1, false, (TransferState)null);
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
