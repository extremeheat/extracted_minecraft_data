package net.minecraft.client.gui.screens.advancements;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientAdvancements;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundSeenAdvancementsPacket;
import net.minecraft.resources.ResourceLocation;

public class AdvancementsScreen extends Screen implements ClientAdvancements.Listener {
   private static final ResourceLocation WINDOW_LOCATION = new ResourceLocation("textures/gui/advancements/window.png");
   public static final ResourceLocation TABS_LOCATION = new ResourceLocation("textures/gui/advancements/tabs.png");
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
   private static final Component VERY_SAD_LABEL = Component.translatable("advancements.sad_label");
   private static final Component NO_ADVANCEMENTS_LABEL = Component.translatable("advancements.empty");
   private static final Component TITLE = Component.translatable("gui.advancements");
   private final ClientAdvancements advancements;
   private final Map<Advancement, AdvancementTab> tabs = Maps.newLinkedHashMap();
   @Nullable
   private AdvancementTab selectedTab;
   private boolean isScrolling;

   public AdvancementsScreen(ClientAdvancements var1) {
      super(GameNarrator.NO_TITLE);
      this.advancements = var1;
   }

   @Override
   protected void init() {
      this.tabs.clear();
      this.selectedTab = null;
      this.advancements.setListener(this);
      if (this.selectedTab == null && !this.tabs.isEmpty()) {
         this.advancements.setSelectedTab(this.tabs.values().iterator().next().getAdvancement(), true);
      } else {
         this.advancements.setSelectedTab(this.selectedTab == null ? null : this.selectedTab.getAdvancement(), true);
      }
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

         for(AdvancementTab var9 : this.tabs.values()) {
            if (var9.isMouseOver(var6, var7, var1, var3)) {
               this.advancements.setSelectedTab(var9.getAdvancement(), true);
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
      int var5 = (this.width - 252) / 2;
      int var6 = (this.height - 140) / 2;
      this.renderBackground(var1);
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
         for(AdvancementTab var5 : this.tabs.values()) {
            var5.drawTab(var1, var2, var3, var5 == this.selectedTab);
         }

         for(AdvancementTab var7 : this.tabs.values()) {
            var7.drawIcon(var1, var2, var3);
         }
      }

      var1.drawString(this.font, TITLE, var2 + 8, var3 + 6, 4210752, false);
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
         for(AdvancementTab var7 : this.tabs.values()) {
            if (var7.isMouseOver(var4, var5, (double)var2, (double)var3)) {
               var1.renderTooltip(this.font, var7.getTitle(), var2, var3);
            }
         }
      }
   }

   @Override
   public void onAddAdvancementRoot(Advancement var1) {
      AdvancementTab var2 = AdvancementTab.create(this.minecraft, this, this.tabs.size(), var1);
      if (var2 != null) {
         this.tabs.put(var1, var2);
      }
   }

   @Override
   public void onRemoveAdvancementRoot(Advancement var1) {
   }

   @Override
   public void onAddAdvancementTask(Advancement var1) {
      AdvancementTab var2 = this.getTab(var1);
      if (var2 != null) {
         var2.addAdvancement(var1);
      }
   }

   @Override
   public void onRemoveAdvancementTask(Advancement var1) {
   }

   @Override
   public void onUpdateAdvancementProgress(Advancement var1, AdvancementProgress var2) {
      AdvancementWidget var3 = this.getAdvancementWidget(var1);
      if (var3 != null) {
         var3.setProgress(var2);
      }
   }

   @Override
   public void onSelectedTabChanged(@Nullable Advancement var1) {
      this.selectedTab = this.tabs.get(var1);
   }

   @Override
   public void onAdvancementsCleared() {
      this.tabs.clear();
      this.selectedTab = null;
   }

   @Nullable
   public AdvancementWidget getAdvancementWidget(Advancement var1) {
      AdvancementTab var2 = this.getTab(var1);
      return var2 == null ? null : var2.getWidget(var1);
   }

   @Nullable
   private AdvancementTab getTab(Advancement var1) {
      while(var1.getParent() != null) {
         var1 = var1.getParent();
      }

      return this.tabs.get(var1);
   }
}
