package net.minecraft.client.gui.components;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;

public class FocusableTextWidget extends MultiLineTextWidget {
   public static final int DEFAULT_PADDING = 4;
   private final boolean alwaysShowBorder;
   private final boolean fillBackground;
   private final int padding;

   public FocusableTextWidget(int var1, Component var2, Font var3) {
      this(var1, var2, var3, 4);
   }

   public FocusableTextWidget(int var1, Component var2, Font var3, int var4) {
      this(var1, var2, var3, true, true, var4);
   }

   public FocusableTextWidget(int var1, Component var2, Font var3, boolean var4, boolean var5, int var6) {
      super(var2, var3);
      this.setMaxWidth(var1);
      this.setCentered(true);
      this.active = true;
      this.alwaysShowBorder = var4;
      this.fillBackground = var5;
      this.padding = var6;
   }

   public void containWithin(int var1) {
      this.setMaxWidth(var1 - this.padding * 4);
   }

   protected void updateWidgetNarration(NarrationElementOutput var1) {
      var1.add(NarratedElementType.TITLE, this.getMessage());
   }

   public void renderWidget(GuiGraphics var1, int var2, int var3, float var4) {
      int var5 = this.getX() - this.padding;
      int var6 = this.getY() - this.padding;
      int var7 = this.getWidth() + this.padding * 2;
      int var8 = this.getHeight() + this.padding * 2;
      int var9 = ARGB.color(this.alpha, this.alwaysShowBorder ? (this.isFocused() ? -1 : -6250336) : -1);
      if (this.fillBackground) {
         var1.fill(var5 + 1, var6, var5 + var7, var6 + var8, ARGB.color(this.alpha, -16777216));
      }

      if (this.isFocused() || this.alwaysShowBorder) {
         var1.renderOutline(var5, var6, var7, var8, var9);
      }

      super.renderWidget(var1, var2, var3, var4);
   }

   public void playDownSound(SoundManager var1) {
   }
}
