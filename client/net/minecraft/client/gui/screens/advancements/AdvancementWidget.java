package net.minecraft.client.gui.screens.advancements;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.advancements.AdvancementNode;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.StringSplitter;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;

public class AdvancementWidget {
   private static final ResourceLocation TITLE_BOX_SPRITE = ResourceLocation.withDefaultNamespace("advancements/title_box");
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
   private static final int TITLE_PADDING_TOP = 9;
   private static final int TITLE_PADDING_BOTTOM = 8;
   private static final int TITLE_MAX_WIDTH = 163;
   private static final int TITLE_MIN_WIDTH = 80;
   private static final int[] TEST_SPLIT_OFFSETS = new int[]{0, 10, -10, 25, -25};
   private final AdvancementTab tab;
   private final AdvancementNode advancementNode;
   private final DisplayInfo display;
   private final List<FormattedCharSequence> titleLines;
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

   public AdvancementWidget(AdvancementTab var1, Minecraft var2, AdvancementNode var3, DisplayInfo var4) {
      super();
      this.tab = var1;
      this.advancementNode = var3;
      this.display = var4;
      this.minecraft = var2;
      this.titleLines = var2.font.split(var4.getTitle(), 163);
      this.x = Mth.floor(var4.getX() * 28.0F);
      this.y = Mth.floor(var4.getY() * 27.0F);
      Stream var10000 = this.titleLines.stream();
      Font var10001 = var2.font;
      Objects.requireNonNull(var10001);
      int var5 = Math.max(var10000.mapToInt(var10001::width).max().orElse(0), 80);
      int var6 = this.getMaxProgressWidth();
      int var7 = 29 + var5 + var6;
      this.description = Language.getInstance().getVisualOrder(this.findOptimalLines(ComponentUtils.mergeStyles(var4.getDescription().copy(), Style.EMPTY.withColor(var4.getType().getChatColor())), var7));

      for(FormattedCharSequence var9 : this.description) {
         var7 = Math.max(var7, var2.font.width(var9));
      }

      this.width = var7 + 3 + 5;
   }

   private int getMaxProgressWidth() {
      int var1 = this.advancementNode.advancement().requirements().size();
      if (var1 <= 1) {
         return 0;
      } else {
         boolean var2 = true;
         MutableComponent var3 = Component.translatable("advancements.progress", var1, var1);
         return this.minecraft.font.width((FormattedText)var3) + 8;
      }
   }

   private static float getMaxWidth(StringSplitter var0, List<FormattedText> var1) {
      Stream var10000 = var1.stream();
      Objects.requireNonNull(var0);
      return (float)var10000.mapToDouble(var0::stringWidth).max().orElse(0.0);
   }

   private List<FormattedText> findOptimalLines(Component var1, int var2) {
      StringSplitter var3 = this.minecraft.font.getSplitter();
      List var4 = null;
      float var5 = 3.4028235E38F;

      for(int var9 : TEST_SPLIT_OFFSETS) {
         List var10 = var3.splitLines((FormattedText)var1, var2 - var9, Style.EMPTY);
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
   private AdvancementWidget getFirstVisibleParent(AdvancementNode var1) {
      do {
         var1 = var1.parent();
      } while(var1 != null && var1.advancement().display().isEmpty());

      if (var1 != null && !var1.advancement().display().isEmpty()) {
         return this.tab.getWidget(var1.holder());
      } else {
         return null;
      }
   }

   public void drawConnectivity(GuiGraphics var1, int var2, int var3, boolean var4) {
      if (this.parent != null) {
         int var5 = var2 + this.parent.x + 13;
         int var6 = var2 + this.parent.x + 26 + 4;
         int var7 = var3 + this.parent.y + 13;
         int var8 = var2 + this.x + 13;
         int var9 = var3 + this.y + 13;
         int var10 = var4 ? -16777216 : -1;
         if (var4) {
            var1.hLine(var6, var5, var7 - 1, var10);
            var1.hLine(var6 + 1, var5, var7, var10);
            var1.hLine(var6, var5, var7 + 1, var10);
            var1.hLine(var8, var6 - 1, var9 - 1, var10);
            var1.hLine(var8, var6 - 1, var9, var10);
            var1.hLine(var8, var6 - 1, var9 + 1, var10);
            var1.vLine(var6 - 1, var9, var7, var10);
            var1.vLine(var6 + 1, var9, var7, var10);
         } else {
            var1.hLine(var6, var5, var7, var10);
            var1.hLine(var8, var6, var9, var10);
            var1.vLine(var6, var9, var7, var10);
         }
      }

      for(AdvancementWidget var12 : this.children) {
         var12.drawConnectivity(var1, var2, var3, var4);
      }

   }

   public void draw(GuiGraphics var1, int var2, int var3) {
      if (!this.display.isHidden() || this.progress != null && this.progress.isDone()) {
         float var4 = this.progress == null ? 0.0F : this.progress.getPercent();
         AdvancementWidgetType var5;
         if (var4 >= 1.0F) {
            var5 = AdvancementWidgetType.OBTAINED;
         } else {
            var5 = AdvancementWidgetType.UNOBTAINED;
         }

         var1.blitSprite(RenderType::guiTextured, (ResourceLocation)var5.frameSprite(this.display.getType()), var2 + this.x + 3, var3 + this.y, 26, 26);
         var1.renderFakeItem(this.display.getIcon(), var2 + this.x + 8, var3 + this.y + 5);
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

   public void drawHover(GuiGraphics var1, int var2, int var3, float var4, int var5, int var6) {
      Font var7 = this.minecraft.font;
      Objects.requireNonNull(var7);
      int var8 = 9 * this.titleLines.size() + 9 + 8;
      int var9 = var3 + this.y + (26 - var8) / 2;
      int var10 = var9 + var8;
      int var10000 = this.description.size();
      Objects.requireNonNull(var7);
      int var11 = var10000 * 9;
      int var12 = 6 + var11;
      boolean var13 = var5 + var2 + this.x + this.width + 26 >= this.tab.getScreen().width;
      Component var14 = this.progress == null ? null : this.progress.getProgressText();
      int var15 = var14 == null ? 0 : var7.width((FormattedText)var14);
      boolean var16 = var10 + var12 >= 113;
      float var17 = this.progress == null ? 0.0F : this.progress.getPercent();
      int var21 = Mth.floor(var17 * (float)this.width);
      AdvancementWidgetType var18;
      AdvancementWidgetType var19;
      AdvancementWidgetType var20;
      if (var17 >= 1.0F) {
         var21 = this.width / 2;
         var18 = AdvancementWidgetType.OBTAINED;
         var19 = AdvancementWidgetType.OBTAINED;
         var20 = AdvancementWidgetType.OBTAINED;
      } else if (var21 < 2) {
         var21 = this.width / 2;
         var18 = AdvancementWidgetType.UNOBTAINED;
         var19 = AdvancementWidgetType.UNOBTAINED;
         var20 = AdvancementWidgetType.UNOBTAINED;
      } else if (var21 > this.width - 2) {
         var21 = this.width / 2;
         var18 = AdvancementWidgetType.OBTAINED;
         var19 = AdvancementWidgetType.OBTAINED;
         var20 = AdvancementWidgetType.UNOBTAINED;
      } else {
         var18 = AdvancementWidgetType.OBTAINED;
         var19 = AdvancementWidgetType.UNOBTAINED;
         var20 = AdvancementWidgetType.UNOBTAINED;
      }

      int var22 = this.width - var21;
      int var23;
      if (var13) {
         var23 = var2 + this.x - this.width + 26 + 6;
      } else {
         var23 = var2 + this.x;
      }

      int var24 = var8 + var12;
      if (!this.description.isEmpty()) {
         if (var16) {
            var1.blitSprite(RenderType::guiTextured, TITLE_BOX_SPRITE, var23, var10 - var24, this.width, var24);
         } else {
            var1.blitSprite(RenderType::guiTextured, TITLE_BOX_SPRITE, var23, var9, this.width, var24);
         }
      }

      if (var18 != var19) {
         var1.blitSprite(RenderType::guiTextured, var18.boxSprite(), 200, var8, 0, 0, var23, var9, var21, var8);
         var1.blitSprite(RenderType::guiTextured, var19.boxSprite(), 200, var8, 200 - var22, 0, var23 + var21, var9, var22, var8);
      } else {
         var1.blitSprite(RenderType::guiTextured, var18.boxSprite(), var23, var9, this.width, var8);
      }

      var1.blitSprite(RenderType::guiTextured, (ResourceLocation)var20.frameSprite(this.display.getType()), var2 + this.x + 3, var3 + this.y, 26, 26);
      int var25 = var23 + 5;
      if (var13) {
         this.drawMultilineText(var1, this.titleLines, var25, var9 + 9, -1);
         if (var14 != null) {
            var1.drawString(var7, (Component)var14, var2 + this.x - var15, var9 + 9, -1);
         }
      } else {
         this.drawMultilineText(var1, this.titleLines, var2 + this.x + 32, var9 + 9, -1);
         if (var14 != null) {
            var1.drawString(var7, (Component)var14, var2 + this.x + this.width - var15 - 5, var9 + 9, -1);
         }
      }

      if (var16) {
         this.drawMultilineText(var1, this.description, var25, var9 - var11 + 1, -16711936);
      } else {
         this.drawMultilineText(var1, this.description, var25, var10, -16711936);
      }

      var1.renderFakeItem(this.display.getIcon(), var2 + this.x + 8, var3 + this.y + 5);
   }

   private void drawMultilineText(GuiGraphics var1, List<FormattedCharSequence> var2, int var3, int var4, int var5) {
      Font var6 = this.minecraft.font;

      for(int var7 = 0; var7 < var2.size(); ++var7) {
         FormattedCharSequence var10002 = (FormattedCharSequence)var2.get(var7);
         Objects.requireNonNull(var6);
         var1.drawString(var6, var10002, var3, var4 + var7 * 9, var5);
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
      if (this.parent == null && this.advancementNode.parent() != null) {
         this.parent = this.getFirstVisibleParent(this.advancementNode);
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
