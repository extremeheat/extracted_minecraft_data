package net.minecraft.client.gui.screens.advancements;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementNode;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.multiplayer.ClientAdvancements;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundSeenAdvancementsPacket;
import net.minecraft.resources.ResourceLocation;

public class AdvancementsScreen extends Screen implements ClientAdvancements.Listener {
   private static final ResourceLocation WINDOW_LOCATION = ResourceLocation.withDefaultNamespace("textures/gui/advancements/window.png");
   public static final int WINDOW_WIDTH = 252;
   public static final int WINDOW_HEIGHT = 140;
   private static final int WINDOW_INSIDE_X = 9;
   private static final int WINDOW_INSIDE_Y = 18;
   public static final int WINDOW_INSIDE_WIDTH = 234;
   public static final int WINDOW_INSIDE_HEIGHT = 113;
   private static final int WINDOW_TITLE_X = 8;
   private static final int WINDOW_TITLE_Y = 6;
   private static final int BACKGROUND_TEXTURE_WIDTH = 256;
   private static final int BACKGROUND_TEXTURE_HEIGHT = 256;
   public static final int BACKGROUND_TILE_WIDTH = 16;
   public static final int BACKGROUND_TILE_HEIGHT = 16;
   public static final int BACKGROUND_TILE_COUNT_X = 14;
   public static final int BACKGROUND_TILE_COUNT_Y = 7;
   private static final double SCROLL_SPEED = 16.0;
   private static final Component VERY_SAD_LABEL = Component.translatable("advancements.sad_label");
   private static final Component NO_ADVANCEMENTS_LABEL = Component.translatable("advancements.empty");
   private static final Component TITLE = Component.translatable("gui.advancements");
   private final HeaderAndFooterLayout layout;
   @Nullable
   private final Screen lastScreen;
   private final ClientAdvancements advancements;
   private final Map<AdvancementHolder, AdvancementTab> tabs;
   @Nullable
   private AdvancementTab selectedTab;
   private boolean isScrolling;

   public AdvancementsScreen(ClientAdvancements var1) {
      this(var1, (Screen)null);
   }

   public AdvancementsScreen(ClientAdvancements var1, @Nullable Screen var2) {
      super(TITLE);
      this.layout = new HeaderAndFooterLayout(this);
      this.tabs = Maps.newLinkedHashMap();
      this.advancements = var1;
      this.lastScreen = var2;
   }

   protected void init() {
      this.layout.addTitleHeader(TITLE, this.font);
      this.tabs.clear();
      this.selectedTab = null;
      this.advancements.setListener(this);
      if (this.selectedTab == null && !this.tabs.isEmpty()) {
         AdvancementTab var1 = (AdvancementTab)this.tabs.values().iterator().next();
         this.advancements.setSelectedTab(var1.getRootNode().holder(), true);
      } else {
         this.advancements.setSelectedTab(this.selectedTab == null ? null : this.selectedTab.getRootNode().holder(), true);
      }

      this.layout.addToFooter(Button.builder(CommonComponents.GUI_DONE, (var1x) -> this.onClose()).width(200).build());
      this.layout.visitWidgets((var1x) -> {
         AbstractWidget var10000 = (AbstractWidget)this.addRenderableWidget(var1x);
      });
      this.repositionElements();
   }

   protected void repositionElements() {
      this.layout.arrangeElements();
   }

   public void onClose() {
      this.minecraft.setScreen(this.lastScreen);
   }

   public void removed() {
      this.advancements.setListener((ClientAdvancements.Listener)null);
      ClientPacketListener var1 = this.minecraft.getConnection();
      if (var1 != null) {
         var1.send(ServerboundSeenAdvancementsPacket.closedScreen());
      }

   }

   public boolean mouseClicked(MouseButtonEvent var1, boolean var2) {
      if (var1.button() == 0) {
         int var3 = (this.width - 252) / 2;
         int var4 = (this.height - 140) / 2;

         for(AdvancementTab var6 : this.tabs.values()) {
            if (var6.isMouseOver(var3, var4, var1.x(), var1.y())) {
               this.advancements.setSelectedTab(var6.getRootNode().holder(), true);
               break;
            }
         }
      }

      return super.mouseClicked(var1, var2);
   }

   public boolean keyPressed(KeyEvent var1) {
      if (this.minecraft.options.keyAdvancements.matches(var1)) {
         this.minecraft.setScreen((Screen)null);
         this.minecraft.mouseHandler.grabMouse();
         return true;
      } else {
         return super.keyPressed(var1);
      }
   }

   public void render(GuiGraphics var1, int var2, int var3, float var4) {
      super.render(var1, var2, var3, var4);
      int var5 = (this.width - 252) / 2;
      int var6 = (this.height - 140) / 2;
      var1.nextStratum();
      this.renderInside(var1, var5, var6);
      var1.nextStratum();
      this.renderWindow(var1, var5, var6);
      this.renderTooltips(var1, var2, var3, var5, var6);
   }

   public boolean mouseDragged(MouseButtonEvent var1, double var2, double var4) {
      if (var1.button() != 0) {
         this.isScrolling = false;
         return false;
      } else {
         if (!this.isScrolling) {
            this.isScrolling = true;
         } else if (this.selectedTab != null) {
            this.selectedTab.scroll(var2, var4);
         }

         return true;
      }
   }

   public boolean mouseScrolled(double var1, double var3, double var5, double var7) {
      if (this.selectedTab != null) {
         this.selectedTab.scroll(var5 * 16.0, var7 * 16.0);
         return true;
      } else {
         return false;
      }
   }

   private void renderInside(GuiGraphics var1, int var2, int var3) {
      AdvancementTab var4 = this.selectedTab;
      if (var4 == null) {
         var1.fill(var2 + 9, var3 + 18, var2 + 9 + 234, var3 + 18 + 113, -16777216);
         int var5 = var2 + 9 + 117;
         Font var10001 = this.font;
         Component var10002 = NO_ADVANCEMENTS_LABEL;
         int var10004 = var3 + 18 + 56;
         Objects.requireNonNull(this.font);
         var1.drawCenteredString(var10001, (Component)var10002, var5, var10004 - 9 / 2, -1);
         var10001 = this.font;
         var10002 = VERY_SAD_LABEL;
         var10004 = var3 + 18 + 113;
         Objects.requireNonNull(this.font);
         var1.drawCenteredString(var10001, (Component)var10002, var5, var10004 - 9, -1);
      } else {
         var4.drawContents(var1, var2 + 9, var3 + 18);
      }
   }

   public void renderWindow(GuiGraphics var1, int var2, int var3) {
      var1.blit(RenderPipelines.GUI_TEXTURED, WINDOW_LOCATION, var2, var3, 0.0F, 0.0F, 252, 140, 256, 256);
      if (this.tabs.size() > 1) {
         for(AdvancementTab var5 : this.tabs.values()) {
            var5.drawTab(var1, var2, var3, var5 == this.selectedTab);
         }

         for(AdvancementTab var7 : this.tabs.values()) {
            var7.drawIcon(var1, var2, var3);
         }
      }

      var1.drawString(this.font, this.selectedTab != null ? this.selectedTab.getTitle() : TITLE, var2 + 8, var3 + 6, -12566464, false);
   }

   private void renderTooltips(GuiGraphics var1, int var2, int var3, int var4, int var5) {
      if (this.selectedTab != null) {
         var1.pose().pushMatrix();
         var1.pose().translate((float)(var4 + 9), (float)(var5 + 18));
         var1.nextStratum();
         this.selectedTab.drawTooltips(var1, var2 - var4 - 9, var3 - var5 - 18, var4, var5);
         var1.pose().popMatrix();
      }

      if (this.tabs.size() > 1) {
         for(AdvancementTab var7 : this.tabs.values()) {
            if (var7.isMouseOver(var4, var5, (double)var2, (double)var3)) {
               var1.setTooltipForNextFrame(this.font, var7.getTitle(), var2, var3);
            }
         }
      }

   }

   public void onAddAdvancementRoot(AdvancementNode var1) {
      AdvancementTab var2 = AdvancementTab.create(this.minecraft, this, this.tabs.size(), var1);
      if (var2 != null) {
         this.tabs.put(var1.holder(), var2);
      }
   }

   public void onRemoveAdvancementRoot(AdvancementNode var1) {
   }

   public void onAddAdvancementTask(AdvancementNode var1) {
      AdvancementTab var2 = this.getTab(var1);
      if (var2 != null) {
         var2.addAdvancement(var1);
      }

   }

   public void onRemoveAdvancementTask(AdvancementNode var1) {
   }

   public void onUpdateAdvancementProgress(AdvancementNode var1, AdvancementProgress var2) {
      AdvancementWidget var3 = this.getAdvancementWidget(var1);
      if (var3 != null) {
         var3.setProgress(var2);
      }

   }

   public void onSelectedTabChanged(@Nullable AdvancementHolder var1) {
      this.selectedTab = (AdvancementTab)this.tabs.get(var1);
   }

   public void onAdvancementsCleared() {
      this.tabs.clear();
      this.selectedTab = null;
   }

   @Nullable
   public AdvancementWidget getAdvancementWidget(AdvancementNode var1) {
      AdvancementTab var2 = this.getTab(var1);
      return var2 == null ? null : var2.getWidget(var1.holder());
   }

   @Nullable
   private AdvancementTab getTab(AdvancementNode var1) {
      AdvancementNode var2 = var1.root();
      return (AdvancementTab)this.tabs.get(var2.holder());
   }
}
