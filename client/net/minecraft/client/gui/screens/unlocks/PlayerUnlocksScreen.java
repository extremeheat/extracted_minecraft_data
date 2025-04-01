package net.minecraft.client.gui.screens.unlocks;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientPlayerUnlocks;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.players.PlayerUnlock;

public class PlayerUnlocksScreen extends Screen implements ClientPlayerUnlocks.Listener {
   private static final ResourceLocation WINDOW_LOCATION = ResourceLocation.withDefaultNamespace("widget/window");
   private static final int WINDOW_TITLE_X = 8;
   private static final int WINDOW_TITLE_Y = 6;
   private static final int WINDOW_MARGIN_MIN = 80;
   private static final int TOP_PADDING = 18;
   private static final int BOTTOM_PADDING = 9;
   private static final int LEFT_PADDING = 9;
   private static final int RIGHT_PADDING = 9;
   private static final double SCROLL_SPEED = 16.0;
   private static final Component VERY_SAD_LABEL = Component.translatable("advancements.sad_label");
   private static final Component NO_ADVANCEMENTS_LABEL = Component.translatable("advancements.empty");
   public static final Component TITLE = Component.translatable("gui.unlocks");
   private final HeaderAndFooterLayout layout;
   @Nullable
   private final Screen lastScreen;
   private final ClientPlayerUnlocks playerUnlocks;
   private final Map<Holder<PlayerUnlock>, PlayerUnlocksTab> tabs;
   @Nullable
   private PlayerUnlocksTab selectedTab;
   private boolean isScrolling;
   private int windowWidth;
   private int windowHeight;

   public PlayerUnlocksScreen(ClientPlayerUnlocks var1) {
      this(var1, (Screen)null);
   }

   public PlayerUnlocksScreen(ClientPlayerUnlocks var1, @Nullable Screen var2) {
      super(TITLE);
      this.layout = new HeaderAndFooterLayout(this);
      this.tabs = Maps.newLinkedHashMap();
      this.lastScreen = var2;
      this.playerUnlocks = var1;
   }

   protected void init() {
      int var1 = Math.max(this.width / 4, 80);
      int var2 = Math.max(this.height / 4, 80);
      this.windowWidth = this.width - var1;
      this.windowHeight = this.height - var2;
      this.layout.addTitleHeader(TITLE, this.font);
      this.layout.addToFooter(Button.builder(CommonComponents.GUI_DONE, (var1x) -> this.onClose()).width(200).build());
      this.layout.visitWidgets((var1x) -> {
         AbstractWidget var10000 = (AbstractWidget)this.addRenderableWidget(var1x);
      });
      this.repositionElements();
   }

   public boolean isUnlocked(Holder<PlayerUnlock> var1) {
      return this.playerUnlocks.isUnlocked(var1);
   }

   public PlayerUnlock.UnlockVisibility getVisibility(Holder<PlayerUnlock> var1) {
      return this.playerUnlocks.getVisibility(var1);
   }

   public boolean isActiveExclusive(Holder<PlayerUnlock> var1) {
      return this.playerUnlocks.isActiveExclusive(var1);
   }

   protected void repositionElements() {
      this.resetTabs();
      this.layout.arrangeElements();
   }

   private void resetTabs() {
      this.tabs.clear();
      this.selectedTab = null;
      this.playerUnlocks.setListener(this);
      if (this.selectedTab == null && !this.tabs.isEmpty()) {
         PlayerUnlocksTab var1 = (PlayerUnlocksTab)this.tabs.values().iterator().next();
         this.playerUnlocks.setSelectedTab(var1.getRoot());
      } else {
         this.playerUnlocks.setSelectedTab(this.selectedTab == null ? null : this.selectedTab.getRoot());
      }

   }

   public void onClose() {
      this.minecraft.setScreen(this.lastScreen);
   }

   public void renderBackground(GuiGraphics var1, int var2, int var3, float var4) {
      this.renderTransparentBackground(var1);
   }

   public void removed() {
      this.playerUnlocks.setListener((ClientPlayerUnlocks.Listener)null);
   }

   public boolean mouseClicked(double var1, double var3, int var5) {
      int var6 = (this.width - this.getWindowWidth()) / 2;
      int var7 = (this.height - this.getWindowHeight()) / 2;
      if (var5 == 0) {
         for(PlayerUnlocksTab var9 : this.tabs.values()) {
            if (var9.isMouseOver(var6, var7, var1, var3)) {
               this.playerUnlocks.setSelectedTab(var9.getRoot());
               break;
            }
         }
      }

      if (this.selectedTab != null) {
         double var12 = var1 - (double)var6 - 9.0;
         double var10 = var3 - (double)var7 - 18.0;
         if (var12 > 0.0 && var12 < (double)this.getInsideWidth() && var10 > 0.0 && var10 < (double)this.getInsideHeight()) {
            this.selectedTab.mouseClicked(var12, var10, var5);
         }
      }

      return super.mouseClicked(var1, var3, var5);
   }

   public boolean isPauseScreen() {
      return false;
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      if (this.minecraft.options.keyUnlocks.matches(var1, var2)) {
         this.minecraft.setScreen((Screen)null);
         this.minecraft.mouseHandler.grabMouse();
         return true;
      } else {
         return super.keyPressed(var1, var2, var3);
      }
   }

   public void render(GuiGraphics var1, int var2, int var3, float var4) {
      super.render(var1, var2, var3, var4);
      int var5 = this.getWindowWidth();
      int var6 = this.getWindowHeight();
      int var7 = (this.width - var5) / 2;
      int var8 = (this.height - var6) / 2;
      this.renderInside(var1, var2, var3, var7, var8, var5, var6);
      this.renderWindow(var1, var7, var8, var5, var6);
      this.renderTooltips(var1, var2, var3, var7, var8, var5, var6);
   }

   public boolean mouseDragged(double var1, double var3, int var5, double var6, double var8) {
      if (var5 != 0) {
         this.isScrolling = false;
         return false;
      } else {
         if (!this.isScrolling) {
            this.isScrolling = true;
         } else if (this.selectedTab != null) {
            this.selectedTab.scroll(var6, var8, this.getInsideWidth(), this.getInsideHeight());
         }

         return true;
      }
   }

   public boolean mouseScrolled(double var1, double var3, double var5, double var7) {
      if (this.selectedTab != null) {
         this.selectedTab.scroll(var5 * 16.0, var7 * 16.0, this.getInsideWidth(), this.getInsideHeight());
         return true;
      } else {
         return false;
      }
   }

   private void renderInside(GuiGraphics var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      PlayerUnlocksTab var8 = this.selectedTab;
      int var9 = var4 + 9;
      int var10 = var5 + 18;
      int var11 = var4 + var6 - 9;
      int var12 = var5 + var7 - 9;
      if (var8 == null) {
         var1.fill(var9, var10, var11, var12, -16777216);
         int var13 = (var9 + var11) / 2;
         int var14 = (var10 + var12) / 2;
         Font var10001 = this.font;
         Component var10002 = NO_ADVANCEMENTS_LABEL;
         Objects.requireNonNull(this.font);
         var1.drawCenteredString(var10001, (Component)var10002, var13, var14 - 9 / 2, -1);
         var10001 = this.font;
         var10002 = VERY_SAD_LABEL;
         Objects.requireNonNull(this.font);
         var1.drawCenteredString(var10001, (Component)var10002, var13, var14 + 9, -1);
      } else {
         var8.drawContents(var1, var9, var10, var11 - var9, var12 - var10);
      }
   }

   public void renderWindow(GuiGraphics var1, int var2, int var3, int var4, int var5) {
      var1.blitSprite(RenderType::guiTextured, WINDOW_LOCATION, var2, var3, var4, var5);
      if (this.tabs.size() > 1) {
         for(PlayerUnlocksTab var7 : this.tabs.values()) {
            var7.drawTab(var1, var2, var3, var7 == this.selectedTab);
         }

         for(PlayerUnlocksTab var10 : this.tabs.values()) {
            var10.drawIcon(var1, var2, var3);
         }
      }

      var1.drawString(this.font, this.selectedTab != null ? this.selectedTab.getTitle() : TITLE, var2 + 8, var3 + 6, 4210752, false);
      int var9 = this.minecraft.player.experienceLevel;
      var1.drawString(this.font, (Component)Component.translatable("unlocks.screen.points", var9), var2 + 8 + this.getWindowWidth() - (82 + this.font.width("" + var9)), var3 + 6, 47872, false);
   }

   private void renderTooltips(GuiGraphics var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      if (this.selectedTab != null) {
         int var8 = var4 + 9;
         int var9 = var5 + 18;
         var1.pose().pushPose();
         var1.pose().translate((float)var8, (float)var9, 400.0F);
         this.selectedTab.drawTooltips(var1, var2 - var8, var3 - var9, var4, var5, var6 - 9 - 9, var7 - 18 - 9);
         var1.pose().popPose();
      }

      if (this.tabs.size() > 1) {
         for(PlayerUnlocksTab var11 : this.tabs.values()) {
            if (var11.isMouseOver(var4, var5, (double)var2, (double)var3)) {
               var1.renderTooltip(this.font, var11.getTitle(), var2, var3);
            }
         }
      }

   }

   public int getWindowWidth() {
      return this.windowWidth;
   }

   public int getWindowHeight() {
      return this.windowHeight;
   }

   private int getInsideWidth() {
      return this.getWindowWidth() - 9 - 9;
   }

   private int getInsideHeight() {
      return this.getWindowHeight() - 18 - 9;
   }

   public PlayerUnlocksTree getTree() {
      return this.playerUnlocks.getTree();
   }

   public void onAddUnlocksRoot(Holder<PlayerUnlock> var1) {
      PlayerUnlocksTab var2 = PlayerUnlocksTab.create(this.minecraft, this, this.playerUnlocks, this.tabs.size(), var1);
      if (var2 != null) {
         this.tabs.put(var1, var2);
      }
   }

   public void onRemoveUnlocksRoot(Holder<PlayerUnlock> var1) {
   }

   public void onAddUnlock(Holder<PlayerUnlock> var1) {
      PlayerUnlocksTab var2 = this.getTab(var1);
      if (var2 != null) {
         var2.addUnlock(var1);
      }

   }

   public void onRemoveUnlock(Holder<PlayerUnlock> var1) {
   }

   public void onUnlockStatusChange(Holder<PlayerUnlock> var1, boolean var2) {
      PlayerUnlockWidget var3 = this.getWidget(var1);
      if (var3 != null) {
         var3.unlock(var2);
      }

   }

   public void onVisibilityStatusChange(Holder<PlayerUnlock> var1, PlayerUnlock.UnlockVisibility var2) {
      PlayerUnlockWidget var3 = this.getWidget(var1);
      if (var3 != null) {
         var3.visibility(var2);
      }

      if (this.selectedTab != null) {
         this.selectedTab.updateLayout();
      }

   }

   public void onActiveExclusiveStatusChange(Holder<PlayerUnlock> var1, boolean var2) {
      PlayerUnlockWidget var3 = this.getWidget(var1);
      if (var3 != null) {
         var3.isActiveExclusive(var2);
      }

   }

   public void onSelectedTabChanged(@Nullable Holder<PlayerUnlock> var1) {
      this.selectedTab = (PlayerUnlocksTab)this.tabs.get(var1);
   }

   public void onUnlocksCleared() {
      this.tabs.clear();
      this.selectedTab = null;
   }

   @Nullable
   public PlayerUnlockWidget getWidget(Holder<PlayerUnlock> var1) {
      PlayerUnlocksTab var2 = this.getTab(var1);
      return var2 == null ? null : var2.getWidget(var1);
   }

   @Nullable
   private PlayerUnlocksTab getTab(Holder<PlayerUnlock> var1) {
      Holder var2 = PlayerUnlock.getRoot(var1);
      return (PlayerUnlocksTab)this.tabs.get(var2);
   }
}
