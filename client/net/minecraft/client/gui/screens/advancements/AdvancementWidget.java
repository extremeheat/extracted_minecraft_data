package net.minecraft.client.gui.screens.advancements;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.StringSplitter;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;

public class AdvancementWidget extends GuiComponent {
   private static final ResourceLocation WIDGETS_LOCATION = new ResourceLocation("textures/gui/advancements/widgets.png");
   private static final int HEIGHT = 26;
   private static final int BOX_X = 0;
   private static final int BOX_WIDTH = 200;
   private static final int FRAME_WIDTH = 26;
   private static final int ICON_X = 8;
   private static final int ICON_Y = 5;
   private static final int ICON_WIDTH = 26;
   private static final int TITLE_PADDING_LEFT = 3;
   private static final int TITLE_PADDING_RIGHT = 5;
   private static final int TITLE_X = 32;
   private static final int TITLE_Y = 9;
   private static final int TITLE_MAX_WIDTH = 163;
   private static final int[] TEST_SPLIT_OFFSETS = new int[]{0, 10, -10, 25, -25};
   private final AdvancementTab tab;
   private final Advancement advancement;
   private final DisplayInfo display;
   private final FormattedCharSequence title;
   private final int width;
   private final List<FormattedCharSequence> description;
   private final Minecraft minecraft;
   @Nullable
   private AdvancementWidget parent;
   private final List<AdvancementWidget> children = Lists.newArrayList();
   @Nullable
   private AdvancementProgress progress;
   private final int x;
   private final int y;

   public AdvancementWidget(AdvancementTab var1, Minecraft var2, Advancement var3, DisplayInfo var4) {
      super();
      this.tab = var1;
      this.advancement = var3;
      this.display = var4;
      this.minecraft = var2;
      this.title = Language.getInstance().getVisualOrder(var2.font.substrByWidth(var4.getTitle(), 163));
      this.x = Mth.floor(var4.getX() * 28.0F);
      this.y = Mth.floor(var4.getY() * 27.0F);
      int var5 = var3.getMaxCriteraRequired();
      int var6 = String.valueOf(var5).length();
      int var7 = var5 > 1 ? var2.font.width("  ") + var2.font.width("0") * var6 * 2 + var2.font.width("/") : 0;
      int var8 = 29 + var2.font.width(this.title) + var7;
      this.description = Language.getInstance()
         .getVisualOrder(
            this.findOptimalLines(ComponentUtils.mergeStyles(var4.getDescription().copy(), Style.EMPTY.withColor(var4.getFrame().getChatColor())), var8)
         );

      for(FormattedCharSequence var10 : this.description) {
         var8 = Math.max(var8, var2.font.width(var10));
      }

      this.width = var8 + 3 + 5;
   }

   private static float getMaxWidth(StringSplitter var0, List<FormattedText> var1) {
      return (float)var1.stream().mapToDouble(var0::stringWidth).max().orElse(0.0);
   }

   private List<FormattedText> findOptimalLines(Component var1, int var2) {
      StringSplitter var3 = this.minecraft.font.getSplitter();
      List var4 = null;
      float var5 = 3.4028235E38F;

      for(int var9 : TEST_SPLIT_OFFSETS) {
         List var10 = var3.splitLines(var1, var2 - var9, Style.EMPTY);
         float var11 = Math.abs(getMaxWidth(var3, var10) - (float)var2);
         if (var11 <= 10.0F) {
            return var10;
         }

         if (var11 < var5) {
            var5 = var11;
            var4 = var10;
         }
      }

      return var4;
   }

   @Nullable
   private AdvancementWidget getFirstVisibleParent(Advancement var1) {
      do {
         var1 = var1.getParent();
      } while(var1 != null && var1.getDisplay() == null);

      return var1 != null && var1.getDisplay() != null ? this.tab.getWidget(var1) : null;
   }

   public void drawConnectivity(PoseStack var1, int var2, int var3, boolean var4) {
      if (this.parent != null) {
         int var5 = var2 + this.parent.x + 13;
         int var6 = var2 + this.parent.x + 26 + 4;
         int var7 = var3 + this.parent.y + 13;
         int var8 = var2 + this.x + 13;
         int var9 = var3 + this.y + 13;
         int var10 = var4 ? -16777216 : -1;
         if (var4) {
            this.hLine(var1, var6, var5, var7 - 1, var10);
            this.hLine(var1, var6 + 1, var5, var7, var10);
            this.hLine(var1, var6, var5, var7 + 1, var10);
            this.hLine(var1, var8, var6 - 1, var9 - 1, var10);
            this.hLine(var1, var8, var6 - 1, var9, var10);
            this.hLine(var1, var8, var6 - 1, var9 + 1, var10);
            this.vLine(var1, var6 - 1, var9, var7, var10);
            this.vLine(var1, var6 + 1, var9, var7, var10);
         } else {
            this.hLine(var1, var6, var5, var7, var10);
            this.hLine(var1, var8, var6, var9, var10);
            this.vLine(var1, var6, var9, var7, var10);
         }
      }

      for(AdvancementWidget var12 : this.children) {
         var12.drawConnectivity(var1, var2, var3, var4);
      }
   }

   public void draw(PoseStack var1, int var2, int var3) {
      if (!this.display.isHidden() || this.progress != null && this.progress.isDone()) {
         float var4 = this.progress == null ? 0.0F : this.progress.getPercent();
         AdvancementWidgetType var5;
         if (var4 >= 1.0F) {
            var5 = AdvancementWidgetType.OBTAINED;
         } else {
            var5 = AdvancementWidgetType.UNOBTAINED;
         }

         RenderSystem.setShader(GameRenderer::getPositionTexShader);
         RenderSystem.setShaderTexture(0, WIDGETS_LOCATION);
         this.blit(var1, var2 + this.x + 3, var3 + this.y, this.display.getFrame().getTexture(), 128 + var5.getIndex() * 26, 26, 26);
         this.minecraft.getItemRenderer().renderAndDecorateFakeItem(this.display.getIcon(), var2 + this.x + 8, var3 + this.y + 5);
      }

      for(AdvancementWidget var7 : this.children) {
         var7.draw(var1, var2, var3);
      }
   }

   public int getWidth() {
      return this.width;
   }

   public void setProgress(AdvancementProgress var1) {
      this.progress = var1;
   }

   public void addChild(AdvancementWidget var1) {
      this.children.add(var1);
   }

   public void drawHover(PoseStack var1, int var2, int var3, float var4, int var5, int var6) {
      boolean var7 = var5 + var2 + this.x + this.width + 26 >= this.tab.getScreen().width;
      String var8 = this.progress == null ? null : this.progress.getProgressText();
      int var9 = var8 == null ? 0 : this.minecraft.font.width(var8);
      boolean var10 = 113 - var3 - this.y - 26 <= 6 + this.description.size() * 9;
      float var11 = this.progress == null ? 0.0F : this.progress.getPercent();
      int var15 = Mth.floor(var11 * (float)this.width);
      AdvancementWidgetType var12;
      AdvancementWidgetType var13;
      AdvancementWidgetType var14;
      if (var11 >= 1.0F) {
         var15 = this.width / 2;
         var12 = AdvancementWidgetType.OBTAINED;
         var13 = AdvancementWidgetType.OBTAINED;
         var14 = AdvancementWidgetType.OBTAINED;
      } else if (var15 < 2) {
         var15 = this.width / 2;
         var12 = AdvancementWidgetType.UNOBTAINED;
         var13 = AdvancementWidgetType.UNOBTAINED;
         var14 = AdvancementWidgetType.UNOBTAINED;
      } else if (var15 > this.width - 2) {
         var15 = this.width / 2;
         var12 = AdvancementWidgetType.OBTAINED;
         var13 = AdvancementWidgetType.OBTAINED;
         var14 = AdvancementWidgetType.UNOBTAINED;
      } else {
         var12 = AdvancementWidgetType.OBTAINED;
         var13 = AdvancementWidgetType.UNOBTAINED;
         var14 = AdvancementWidgetType.UNOBTAINED;
      }

      int var16 = this.width - var15;
      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      RenderSystem.setShaderTexture(0, WIDGETS_LOCATION);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.enableBlend();
      int var17 = var3 + this.y;
      int var18;
      if (var7) {
         var18 = var2 + this.x - this.width + 26 + 6;
      } else {
         var18 = var2 + this.x;
      }

      int var19 = 32 + this.description.size() * 9;
      if (!this.description.isEmpty()) {
         if (var10) {
            this.render9Sprite(var1, var18, var17 + 26 - var19, this.width, var19, 10, 200, 26, 0, 52);
         } else {
            this.render9Sprite(var1, var18, var17, this.width, var19, 10, 200, 26, 0, 52);
         }
      }

      this.blit(var1, var18, var17, 0, var12.getIndex() * 26, var15, 26);
      this.blit(var1, var18 + var15, var17, 200 - var16, var13.getIndex() * 26, var16, 26);
      this.blit(var1, var2 + this.x + 3, var3 + this.y, this.display.getFrame().getTexture(), 128 + var14.getIndex() * 26, 26, 26);
      if (var7) {
         this.minecraft.font.drawShadow(var1, this.title, (float)(var18 + 5), (float)(var3 + this.y + 9), -1);
         if (var8 != null) {
            this.minecraft.font.drawShadow(var1, var8, (float)(var2 + this.x - var9), (float)(var3 + this.y + 9), -1);
         }
      } else {
         this.minecraft.font.drawShadow(var1, this.title, (float)(var2 + this.x + 32), (float)(var3 + this.y + 9), -1);
         if (var8 != null) {
            this.minecraft.font.drawShadow(var1, var8, (float)(var2 + this.x + this.width - var9 - 5), (float)(var3 + this.y + 9), -1);
         }
      }

      if (var10) {
         for(int var20 = 0; var20 < this.description.size(); ++var20) {
            this.minecraft.font.draw(var1, this.description.get(var20), (float)(var18 + 5), (float)(var17 + 26 - var19 + 7 + var20 * 9), -5592406);
         }
      } else {
         for(int var21 = 0; var21 < this.description.size(); ++var21) {
            this.minecraft.font.draw(var1, this.description.get(var21), (float)(var18 + 5), (float)(var3 + this.y + 9 + 17 + var21 * 9), -5592406);
         }
      }

      this.minecraft.getItemRenderer().renderAndDecorateFakeItem(this.display.getIcon(), var2 + this.x + 8, var3 + this.y + 5);
   }

   protected void render9Sprite(PoseStack var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10) {
      this.blit(var1, var2, var3, var9, var10, var6, var6);
      this.renderRepeating(var1, var2 + var6, var3, var4 - var6 - var6, var6, var9 + var6, var10, var7 - var6 - var6, var8);
      this.blit(var1, var2 + var4 - var6, var3, var9 + var7 - var6, var10, var6, var6);
      this.blit(var1, var2, var3 + var5 - var6, var9, var10 + var8 - var6, var6, var6);
      this.renderRepeating(var1, var2 + var6, var3 + var5 - var6, var4 - var6 - var6, var6, var9 + var6, var10 + var8 - var6, var7 - var6 - var6, var8);
      this.blit(var1, var2 + var4 - var6, var3 + var5 - var6, var9 + var7 - var6, var10 + var8 - var6, var6, var6);
      this.renderRepeating(var1, var2, var3 + var6, var6, var5 - var6 - var6, var9, var10 + var6, var7, var8 - var6 - var6);
      this.renderRepeating(
         var1, var2 + var6, var3 + var6, var4 - var6 - var6, var5 - var6 - var6, var9 + var6, var10 + var6, var7 - var6 - var6, var8 - var6 - var6
      );
      this.renderRepeating(var1, var2 + var4 - var6, var3 + var6, var6, var5 - var6 - var6, var9 + var7 - var6, var10 + var6, var7, var8 - var6 - var6);
   }

   protected void renderRepeating(PoseStack var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9) {
      for(int var10 = 0; var10 < var4; var10 += var8) {
         int var11 = var2 + var10;
         int var12 = Math.min(var8, var4 - var10);

         for(int var13 = 0; var13 < var5; var13 += var9) {
            int var14 = var3 + var13;
            int var15 = Math.min(var9, var5 - var13);
            this.blit(var1, var11, var14, var6, var7, var12, var15);
         }
      }
   }

   public boolean isMouseOver(int var1, int var2, int var3, int var4) {
      if (!this.display.isHidden() || this.progress != null && this.progress.isDone()) {
         int var5 = var1 + this.x;
         int var6 = var5 + 26;
         int var7 = var2 + this.y;
         int var8 = var7 + 26;
         return var3 >= var5 && var3 <= var6 && var4 >= var7 && var4 <= var8;
      } else {
         return false;
      }
   }

   public void attachToParent() {
      if (this.parent == null && this.advancement.getParent() != null) {
         this.parent = this.getFirstVisibleParent(this.advancement);
         if (this.parent != null) {
            this.parent.addChild(this);
         }
      }
   }

   public int getY() {
      return this.y;
   }

   public int getX() {
      return this.x;
   }
}