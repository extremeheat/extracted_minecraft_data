package net.minecraft.client.gui.screens.advancements;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementNode;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientAdvancements;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundSeenAdvancementsPacket;
import net.minecraft.resources.ResourceLocation;

public class AdvancementsScreen extends Screen implements ClientAdvancements.Listener {
   private static final ResourceLocation WINDOW_LOCATION = new ResourceLocation("textures/gui/advancements/window.png");
   public static final int WINDOW_WIDTH = 252;
   public static final int WINDOW_HEIGHT = 140;
   private static final int WINDOW_INSIDE_X = 9;
   private static final int WINDOW_INSIDE_Y = 18;
   public static final int WINDOW_INSIDE_WIDTH = 234;
   public static final int WINDOW_INSIDE_HEIGHT = 113;
   private static final int WINDOW_TITLE_X = 8;
   private static final int WINDOW_TITLE_Y = 6;
   public static final int BACKGROUND_TILE_WIDTH = 16;
   public static final int BACKGROUND_TILE_HEIGHT = 16;
   public static final int BACKGROUND_TILE_COUNT_X = 14;
   public static final int BACKGROUND_TILE_COUNT_Y = 7;
   private static final double SCROLL_SPEED = 16.0;
   private static final Component VERY_SAD_LABEL = Component.translatable("advancements.sad_label");
   private static final Component NO_ADVANCEMENTS_LABEL = Component.translatable("advancements.empty");
   private static final Component TITLE = Component.translatable("gui.advancements");
   private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);
   @Nullable
   private final Screen lastScreen;
   private final ClientAdvancements advancements;
   private final Map<AdvancementHolder, AdvancementTab> tabs = Maps.newLinkedHashMap();
   @Nullable
   private AdvancementTab selectedTab;
   private boolean isScrolling;

   public AdvancementsScreen(ClientAdvancements var1) {
      this(var1, null);
   }

   public AdvancementsScreen(ClientAdvancements var1, @Nullable Screen var2) {
      super(TITLE);
      this.advancements = var1;
      this.lastScreen = var2;
   }

   @Override
   protected void init() {
      this.layout.addTitleHeader(TITLE, this.font);
      this.tabs.clear();
      this.selectedTab = null;
      this.advancements.setListener(this);
      if (this.selectedTab == null && !this.tabs.isEmpty()) {
         AdvancementTab var1 = this.tabs.values().iterator().next();
         this.advancements.setSelectedTab(var1.getRootNode().holder(), true);
      } else {
         this.advancements.setSelectedTab(this.selectedTab == null ? null : this.selectedTab.getRootNode().holder(), true);
      }

      this.layout.addToFooter(Button.builder(CommonComponents.GUI_DONE, var1x -> this.onClose()).width(200).build());
      this.layout.visitWidgets(var1x -> {
         AbstractWidget var10000 = this.addRenderableWidget(var1x);
      });
      this.repositionElements();
   }

   @Override
   protected void repositionElements() {
      this.layout.arrangeElements();
   }

   @Override
   public void onClose() {
      this.minecraft.setScreen(this.lastScreen);
   }

   @Override
   public void removed() {
      this.advancements.setListener(null);
      ClientPacketListener var1 = this.minecraft.getConnection();
      if (var1 != null) {
         var1.send(ServerboundSeenAdvancementsPacket.closedScreen());
      }
   }

   @Override
   public boolean mouseClicked(double var1, double var3, int var5) {
      if (var5 == 0) {
         int var6 = (this.width - 252) / 2;
         int var7 = (this.height - 140) / 2;

         for (AdvancementTab var9 : this.tabs.values()) {
            if (var9.isMouseOver(var6, var7, var1, var3)) {
               this.advancements.setSelectedTab(var9.getRootNode().holder(), true);
               break;
            }
         }
      }

      return super.mouseClicked(var1, var3, var5);
   }

   @Override
   public boolean keyPressed(int var1, int var2, int var3) {
      if (this.minecraft.options.keyAdvancements.matches(var1, var2)) {
         this.minecraft.setScreen(null);
         this.minecraft.mouseHandler.grabMouse();
         return true;
      } else {
         return super.keyPressed(var1, var2, var3);
      }
   }

   @Override
   public void render(GuiGraphics var1, int var2, int var3, float var4) {
      super.render(var1, var2, var3, var4);
      int var5 = (this.width - 252) / 2;
      int var6 = (this.height - 140) / 2;
      this.renderInside(var1, var2, var3, var5, var6);
      this.renderWindow(var1, var5, var6);
      this.renderTooltips(var1, var2, var3, var5, var6);
   }

   @Override
   public boolean mouseDragged(double var1, double var3, int var5, double var6, double var8) {
      if (var5 != 0) {
         this.isScrolling = false;
         return false;
      } else {
         if (!this.isScrolling) {
            this.isScrolling = true;
         } else if (this.selectedTab != null) {
            this.selectedTab.scroll(var6, var8);
         }

         return true;
      }
   }

   @Override
   public boolean mouseScrolled(double var1, double var3, double var5, double var7) {
      if (this.selectedTab != null) {
         this.selectedTab.scroll(var5 * 16.0, var7 * 16.0);
         return true;
      } else {
         return false;
      }
   }

   private void renderInside(GuiGraphics var1, int var2, int var3, int var4, int var5) {
      AdvancementTab var6 = this.selectedTab;
      if (var6 == null) {
         var1.fill(var4 + 9, var5 + 18, var4 + 9 + 234, var5 + 18 + 113, -16777216);
         int var7 = var4 + 9 + 117;
         var1.drawCenteredString(this.font, NO_ADVANCEMENTS_LABEL, var7, var5 + 18 + 56 - 9 / 2, -1);
         var1.drawCenteredString(this.font, VERY_SAD_LABEL, var7, var5 + 18 + 113 - 9, -1);
      } else {
         var6.drawContents(var1, var4 + 9, var5 + 18);
      }
   }

   public void renderWindow(GuiGraphics var1, int var2, int var3) {
      RenderSystem.enableBlend();
      var1.blit(WINDOW_LOCATION, var2, var3, 0, 0, 252, 140);
      if (this.tabs.size() > 1) {
         for (AdvancementTab var5 : this.tabs.values()) {
            var5.drawTab(var1, var2, var3, var5 == this.selectedTab);
         }

         for (AdvancementTab var7 : this.tabs.values()) {
            var7.drawIcon(var1, var2, var3);
         }
      }

      var1.drawString(this.font, this.selectedTab != null ? this.selectedTab.getTitle() : TITLE, var2 + 8, var3 + 6, 4210752, false);
   }

   private void renderTooltips(GuiGraphics var1, int var2, int var3, int var4, int var5) {
      if (this.selectedTab != null) {
         var1.pose().pushPose();
         var1.pose().translate((float)(var4 + 9), (float)(var5 + 18), 400.0F);
         RenderSystem.enableDepthTest();
         this.selectedTab.drawTooltips(var1, var2 - var4 - 9, var3 - var5 - 18, var4, var5);
         RenderSystem.disableDepthTest();
         var1.pose().popPose();
      }

      if (this.tabs.size() > 1) {
         for (AdvancementTab var7 : this.tabs.values()) {
            if (var7.isMouseOver(var4, var5, (double)var2, (double)var3)) {
               var1.renderTooltip(this.font, var7.getTitle(), var2, var3);
            }
         }
      }
   }

   @Override
   public void onAddAdvancementRoot(AdvancementNode var1) {
      AdvancementTab var2 = AdvancementTab.create(this.minecraft, this, this.tabs.size(), var1);
      if (var2 != null) {
         this.tabs.put(var1.holder(), var2);
      }
   }

   @Override
   public void onRemoveAdvancementRoot(AdvancementNode var1) {
   }

   @Override
   public void onAddAdvancementTask(AdvancementNode var1) {
      AdvancementTab var2 = this.getTab(var1);
      if (var2 != null) {
         var2.addAdvancement(var1);
      }
   }

   @Override
   public void onRemoveAdvancementTask(AdvancementNode var1) {
   }

   @Override
   public void onUpdateAdvancementProgress(AdvancementNode var1, AdvancementProgress var2) {
      AdvancementWidget var3 = this.getAdvancementWidget(var1);
      if (var3 != null) {
         var3.setProgress(var2);
      }
   }

   @Override
   public void onSelectedTabChanged(@Nullable AdvancementHolder var1) {
      this.selectedTab = this.tabs.get(var1);
   }

   @Override
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
      return this.tabs.get(var2.holder());
   }
}
