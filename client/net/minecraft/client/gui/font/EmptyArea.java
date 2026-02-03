package net.minecraft.client.gui.font;

import net.minecraft.network.chat.Style;

public record EmptyArea(float x, float y, float advance, float ascent, float height, Style style) implements ActiveArea {
   public static final float DEFAULT_HEIGHT = 9.0F;
   public static final float DEFAULT_ASCENT = 7.0F;

   public EmptyArea {
      super();
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
