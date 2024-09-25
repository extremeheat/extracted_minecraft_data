package net.minecraft.client.gui.components;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;

public class StringWidget extends AbstractStringWidget {
   private float alignX = 0.5F;

   public StringWidget(Component var1, Font var2) {
      this(0, 0, var2.width(var1.getVisualOrderText()), 9, var1, var2);
   }

   public StringWidget(int var1, int var2, Component var3, Font var4) {
      this(0, 0, var1, var2, var3, var4);
   }

   public StringWidget(int var1, int var2, int var3, int var4, Component var5, Font var6) {
      super(var1, var2, var3, var4, var5, var6);
      this.active = false;
   }

   public StringWidget setColor(int var1) {
      super.setColor(var1);
      return this;
   }

   private StringWidget horizontalAlignment(float var1) {
      this.alignX = var1;
      return this;
   }

   public StringWidget alignLeft() {
      return this.horizontalAlignment(0.0F);
   }

   public StringWidget alignCenter() {
      return this.horizontalAlignment(0.5F);
   }

   public StringWidget alignRight() {
      return this.horizontalAlignment(1.0F);
   }

   @Override
   public void renderWidget(GuiGraphics var1, int var2, int var3, float var4) {
      Component var5 = this.getMessage();
      Font var6 = this.getFont();
      int var7 = this.getWidth();
      int var8 = var6.width(var5);
      int var9 = this.getX() + Math.round(this.alignX * (float)(var7 - var8));
      int var10 = this.getY() + (this.getHeight() - 9) / 2;
      FormattedCharSequence var11 = var8 > var7 ? this.clipText(var5, var7) : var5.getVisualOrderText();
      var1.drawString(var6, var11, var9, var10, this.getColor());
   }

   private FormattedCharSequence clipText(Component var1, int var2) {
      Font var3 = this.getFont();
      FormattedText var4 = var3.substrByWidth(var1, var2 - var3.width(CommonComponents.ELLIPSIS));
      return Language.getInstance().getVisualOrder(FormattedText.composite(var4, CommonComponents.ELLIPSIS));
   }
}