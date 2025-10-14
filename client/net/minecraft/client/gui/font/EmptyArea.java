package net.minecraft.client.gui.font;

import net.minecraft.network.chat.Style;

public record EmptyArea(float x, float y, float advance, float ascent, float height, Style style) implements ActiveArea {
   public static final float DEFAULT_HEIGHT = 9.0F;
   public static final float DEFAULT_ASCENT = 7.0F;

   public EmptyArea(float var1, float var2, float var3, float var4, float var5, Style var6) {
      super();
      this.x = var1;
      this.y = var2;
      this.advance = var3;
      this.ascent = var4;
      this.height = var5;
      this.style = var6;
   }

   public float activeLeft() {
      return this.x;
   }

   public float activeTop() {
      return this.y + 7.0F - this.ascent;
   }

   public float activeRight() {
      return this.x + this.advance;
   }

   public float activeBottom() {
      return this.activeTop() + this.height;
   }
}
