package com.mojang.realmsclient.gui.screens.configuration;

import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.client.RealmsError;
import com.mojang.realmsclient.dto.PreferredRegionsDto;
import com.mojang.realmsclient.dto.RealmsRegion;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsSlot;
import com.mojang.realmsclient.dto.RegionDataDto;
import com.mojang.realmsclient.dto.RegionSelectionPreference;
import com.mojang.realmsclient.dto.RegionSelectionPreferenceDto;
import com.mojang.realmsclient.dto.ServiceQuality;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.screens.RealmsGenericErrorScreen;
import com.mojang.realmsclient.gui.screens.RealmsLongRunningMcoTaskScreen;
import com.mojang.realmsclient.util.RealmsUtil;
import com.mojang.realmsclient.util.task.CloseServerTask;
import com.mojang.realmsclient.util.task.LongRunningTask;
import com.mojang.realmsclient.util.task.OpenServerTask;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.tabs.LoadingTab;
import net.minecraft.client.gui.components.tabs.Tab;
import net.minecraft.client.gui.components.tabs.TabManager;
import net.minecraft.client.gui.components.tabs.TabNavigationBar;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.util.StringUtil;
import org.slf4j.Logger;

public class RealmsConfigureWorldScreen extends RealmsScreen {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Component PLAY_TEXT = Component.translatable("mco.selectServer.play");
   private final RealmsMainScreen lastScreen;
   @Nullable
   private RealmsServer serverData;
   @Nullable
   private PreferredRegionsDto regions;
   private final Map<RealmsRegion, ServiceQuality> regionServiceQuality;
   private final long serverId;
   private boolean stateChanged;
   private final TabManager tabManager;
   @Nullable
   private Button playButton;
   @Nullable
   private TabNavigationBar tabNavigationBar;
   final HeaderAndFooterLayout layout;

   public RealmsConfigureWorldScreen(RealmsMainScreen var1, long var2, @Nullable RealmsServer var4, @Nullable PreferredRegionsDto var5) {
      super(Component.empty());
      this.regionServiceQuality = new LinkedHashMap();
      this.tabManager = new TabManager((var1x) -> {
         AbstractWidget var10000 = (AbstractWidget)this.addRenderableWidget(var1x);
      }, (var1x) -> this.removeWidget(var1x), this::onTabSelected, this::onTabDeselected);
      this.layout = new HeaderAndFooterLayout(this);
      this.lastScreen = var1;
      this.serverId = var2;
      this.serverData = var4;
      this.regions = var5;
   }

   public RealmsConfigureWorldScreen(RealmsMainScreen var1, long var2) {
      this(var1, var2, (RealmsServer)null, (PreferredRegionsDto)null);
   }

   public void init() {
      if (this.serverData == null) {
         this.fetchServerData(this.serverId);
      }

      if (this.regions == null) {
         this.fetchRegionData();
      }

      MutableComponent var1 = Component.translatable("mco.configure.world.loading");
      this.tabNavigationBar = TabNavigationBar.builder(this.tabManager, this.width).addTabs(new LoadingTab(this.getFont(), RealmsWorldsTab.TITLE, var1), new LoadingTab(this.getFont(), RealmsPlayersTab.TITLE, var1), new LoadingTab(this.getFont(), RealmsSubscriptionTab.TITLE, var1), new LoadingTab(this.getFont(), RealmsSettingsTab.TITLE, var1)).build();
      this.addRenderableWidget(this.tabNavigationBar);
      LinearLayout var2 = (LinearLayout)this.layout.addToFooter(LinearLayout.horizontal().spacing(8));
      this.playButton = (Button)var2.addChild(Button.builder(PLAY_TEXT, (var1x) -> {
         this.onClose();
         RealmsMainScreen.play(this.serverData, this);
      }).width(150).build());
      this.playButton.active = false;
      var2.addChild(Button.builder(CommonComponents.GUI_BACK, (var1x) -> this.onClose()).build());
      this.layout.visitWidgets((var1x) -> {
         var1x.setTabOrderGroup(1);
         this.addRenderableWidget(var1x);
      });
      this.tabNavigationBar.selectTab(0, false);
      this.repositionElements();
      if (this.serverData != null && this.regions != null) {
         this.onRealmsDataFetched();
      }

   }

   private void onTabSelected(Tab var1) {
      if (this.serverData != null && var1 instanceof RealmsConfigurationTab var2) {
         var2.onSelected(this.serverData);
      }

   }

   private void onTabDeselected(Tab var1) {
      if (this.serverData != null && var1 instanceof RealmsConfigurationTab var2) {
         var2.onDeselected(this.serverData);
      }

   }

   public int getContentHeight() {
      return this.layout.getContentHeight();
   }

   public int getHeaderHeight() {
      return this.layout.getHeaderHeight();
   }

   public Screen getLastScreen() {
      return this.lastScreen;
   }

   public Screen createErrorScreen(RealmsServiceException var1) {
      return new RealmsGenericErrorScreen(var1, this.lastScreen);
   }

   public void repositionElements() {
      if (this.tabNavigationBar != null) {
         this.tabNavigationBar.setWidth(this.width);
         this.tabNavigationBar.arrangeElements();
         int var1 = this.tabNavigationBar.getRectangle().bottom();
         ScreenRectangle var2 = new ScreenRectangle(0, var1, this.width, this.height - this.layout.getFooterHeight() - var1);
         this.tabManager.setTabArea(var2);
         this.layout.setHeaderHeight(var1);
         this.layout.arrangeElements();
      }
   }

   private void updateButtonStates() {
      if (this.serverData != null && this.playButton != null) {
         this.playButton.active = this.serverData.shouldPlayButtonBeActive();
         if (!this.playButton.active && this.serverData.state == RealmsServer.State.CLOSED) {
            this.playButton.setTooltip(Tooltip.create(RealmsServer.WORLD_CLOSED_COMPONENT));
         }
      }

   }

   public void render(GuiGraphics var1, int var2, int var3, float var4) {
      super.render(var1, var2, var3, var4);
      var1.blit(RenderPipelines.GUI_TEXTURED, Screen.FOOTER_SEPARATOR, 0, this.height - this.layout.getFooterHeight() - 2, 0.0F, 0.0F, this.width, 2, 32, 2);
   }

   public boolean keyPressed(KeyEvent var1) {
      return this.tabNavigationBar.keyPressed(var1) ? true : super.keyPressed(var1);
   }

   protected void renderMenuBackground(GuiGraphics var1) {
      var1.blit(RenderPipelines.GUI_TEXTURED, CreateWorldScreen.TAB_HEADER_BACKGROUND, 0, 0, 0.0F, 0.0F, this.width, this.layout.getHeaderHeight(), 16, 16);
      this.renderMenuBackground(var1, 0, this.layout.getHeaderHeight(), this.width, this.height);
   }

   public void onClose() {
      if (this.serverData != null) {
         Tab var2 = this.tabManager.getCurrentTab();
         if (var2 instanceof RealmsConfigurationTab) {
            RealmsConfigurationTab var1 = (RealmsConfigurationTab)var2;
            var1.onDeselected(this.serverData);
         }
      }

      this.minecraft.setScreen(this.lastScreen);
      if (this.stateChanged) {
         this.lastScreen.resetScreen();
      }

   }

   public void fetchRegionData() {
      RealmsUtil.supplyAsync(RealmsClient::getPreferredRegionSelections, RealmsUtil.openScreenAndLogOnFailure(this::createErrorScreen, "Couldn't get realms region data")).thenAcceptAsync((var1) -> {
         this.regions = var1;
         this.onRealmsDataFetched();
      }, this.minecraft);
   }

   public void fetchServerData(long var1) {
      RealmsUtil.supplyAsync((var2) -> var2.getOwnRealm(var1), RealmsUtil.openScreenAndLogOnFailure(this::createErrorScreen, "Couldn't get own world")).thenAcceptAsync((var1x) -> {
         this.serverData = var1x;
         this.onRealmsDataFetched();
      }, this.minecraft);
   }

   private void onRealmsDataFetched() {
      if (this.serverData != null && this.regions != null) {
         this.regionServiceQuality.clear();

         for(RegionDataDto var2 : this.regions.regionData()) {
            if (var2.region() != RealmsRegion.INVALID_REGION) {
               this.regionServiceQuality.put(var2.region(), var2.serviceQuality());
            }
         }

         int var3 = -1;
         if (this.tabNavigationBar != null) {
            var3 = this.tabNavigationBar.getTabs().indexOf(this.tabManager.getCurrentTab());
         }

         if (this.tabNavigationBar != null) {
            this.removeWidget(this.tabNavigationBar);
         }

         this.tabNavigationBar = TabNavigationBar.builder(this.tabManager, this.width).addTabs(new RealmsWorldsTab(this, (Minecraft)Objects.requireNonNull(this.minecraft), this.serverData), new RealmsPlayersTab(this, this.minecraft, this.serverData), new RealmsSubscriptionTab(this, this.minecraft, this.serverData), new RealmsSettingsTab(this, this.minecraft, this.serverData, this.regionServiceQuality)).build();
         this.setFocused(this.tabNavigationBar);
         if (var3 != -1) {
            this.tabNavigationBar.selectTab(var3, false);
         }

         this.addRenderableWidget(this.tabNavigationBar);
         this.tabNavigationBar.setTabActiveState(3, !this.serverData.expired);
         if (this.serverData.expired) {
            this.tabNavigationBar.setTabTooltip(3, Tooltip.create(Component.translatable("mco.configure.world.settings.expired")));
         } else {
            this.tabNavigationBar.setTabTooltip(3, (Tooltip)null);
         }

         this.updateButtonStates();
         this.repositionElements();
      }
   }

   public void saveSlotSettings(RealmsSlot var1) {
      RealmsSlot var2 = (RealmsSlot)this.serverData.slots.get(this.serverData.activeSlot);
      var1.options.templateId = var2.options.templateId;
      var1.options.templateImage = var2.options.templateImage;
      RealmsClient var3 = RealmsClient.getOrCreate();

      try {
         if (this.serverData.activeSlot != var1.slotId) {
            throw new RealmsServiceException(RealmsError.CustomError.configurationError());
         }

         var3.updateSlot(this.serverData.id, var1.slotId, var1.options, var1.settings);
         this.serverData.slots.put(this.serverData.activeSlot, var1);
         if (var1.options.gameMode != var2.options.gameMode || var1.isHardcore() != var2.isHardcore()) {
            RealmsMainScreen.refreshServerList();
         }

         this.stateChanged();
      } catch (RealmsServiceException var5) {
         LOGGER.error("Couldn't save slot settings", var5);
         this.minecraft.setScreen(new RealmsGenericErrorScreen(var5, this));
         return;
      }

      this.minecraft.setScreen(this);
   }

   public void saveSettings(String var1, String var2, RegionSelectionPreference var3, @Nullable RealmsRegion var4) {
      String var5 = StringUtil.isBlank(var2) ? "" : var2;
      String var6 = StringUtil.isBlank(var1) ? "" : var1;
      RealmsClient var7 = RealmsClient.getOrCreate();

      try {
         RealmsSlot var8 = (RealmsSlot)this.serverData.slots.get(this.serverData.activeSlot);
         RealmsRegion var9 = var3 == RegionSelectionPreference.MANUAL ? var4 : null;
         RegionSelectionPreferenceDto var10 = new RegionSelectionPreferenceDto(var3, var9);
         var7.updateConfiguration(this.serverData.id, var6, var5, var10, var8.slotId, var8.options, var8.settings);
         this.serverData.regionSelectionPreference = var10;
         this.serverData.name = var1;
         this.serverData.motd = var5;
         this.stateChanged();
      } catch (RealmsServiceException var11) {
         LOGGER.error("Couldn't save settings", var11);
         this.minecraft.setScreen(new RealmsGenericErrorScreen(var11, this));
         return;
      }

      this.minecraft.setScreen(this);
   }

   public void openTheWorld(boolean var1) {
      RealmsConfigureWorldScreen var2 = this.getNewScreenWithKnownData(this.serverData);
      this.minecraft.setScreen(new RealmsLongRunningMcoTaskScreen(this.getNewScreen(), new LongRunningTask[]{new OpenServerTask(this.serverData, var2, var1, this.minecraft)}));
   }

   public void closeTheWorld() {
      RealmsConfigureWorldScreen var1 = this.getNewScreenWithKnownData(this.serverData);
      this.minecraft.setScreen(new RealmsLongRunningMcoTaskScreen(this.getNewScreen(), new LongRunningTask[]{new CloseServerTask(this.serverData, var1)}));
   }

   public void stateChanged() {
      this.stateChanged = true;
      if (this.tabNavigationBar != null) {
         for(Tab var2 : this.tabNavigationBar.getTabs()) {
            if (var2 instanceof RealmsConfigurationTab) {
               RealmsConfigurationTab var3 = (RealmsConfigurationTab)var2;
               var3.updateData(this.serverData);
            }
         }
      }

   }

   public boolean invitePlayer(long var1, String var3) {
      RealmsClient var4 = RealmsClient.getOrCreate();

      try {
         List var5 = var4.invite(var1, var3);
         if (this.serverData != null) {
            this.serverData.players = var5;
         } else {
            this.serverData = var4.getOwnRealm(var1);
         }

         this.stateChanged();
         return true;
      } catch (RealmsServiceException var6) {
         LOGGER.error("Couldn't invite user", var6);
         return false;
      }
   }

   public RealmsConfigureWorldScreen getNewScreen() {
      RealmsConfigureWorldScreen var1 = new RealmsConfigureWorldScreen(this.lastScreen, this.serverId);
      var1.stateChanged = this.stateChanged;
      return var1;
   }

   public RealmsConfigureWorldScreen getNewScreenWithKnownData(RealmsServer var1) {
      RealmsConfigureWorldScreen var2 = new RealmsConfigureWorldScreen(this.lastScreen, this.serverId, var1, this.regions);
      var2.stateChanged = this.stateChanged;
      return var2;
   }
}
