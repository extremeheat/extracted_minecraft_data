package net.minecraft.client.gui.components;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;

public class FocusableTextWidget extends MultiLineTextWidget {
   private static final int BACKGROUND_COLOR = 1426063360;
   private static final int PADDING = 4;
   private final boolean alwaysShowBorder;

   public FocusableTextWidget(int var1, Component var2, Font var3) {
      this(var1, var2, var3, true);
   }

   public FocusableTextWidget(int var1, Component var2, Font var3, boolean var4) {
      super(var2, var3);
      this.setMaxWidth(var1);
      this.setCentered(true);
      this.active = true;
      this.alwaysShowBorder = var4;
   }

   @Override
   protected void updateWidgetNarration(NarrationElementOutput var1) {
      var1.add(NarratedElementType.TITLE, this.getMessage());
   }

   @Override
   public void renderWidget(GuiGraphics var1, int var2, int var3, float var4) {
      if (this.isFocused() || this.alwaysShowBorder) {
         int var5 = this.getX() - 4;
         int var6 = this.getY() - 4;
         int var7 = this.getWidth() + 8;
         int var8 = this.getHeight() + 8;
         int var9 = this.alwaysShowBorder ? (this.isFocused() ? -1 : -6250336) : -1;
         var1.fill(var5 + 1, var6, var5 + var7, var6 + var8, 1426063360);
         var1.renderOutline(var5, var6, var7, var8, var9);
      }

      super.renderWidget(var1, var2, var3, var4);
   }

   @Override
   public void playDownSound(SoundManager var1) {
   }
}
