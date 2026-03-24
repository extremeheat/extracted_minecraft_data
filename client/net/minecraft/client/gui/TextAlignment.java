package net.minecraft.client.gui;

import net.minecraft.util.FormattedCharSequence;

public enum TextAlignment {
   LEFT {
      public int calculateLeft(final int anchor, final int width) {
         return anchor;
      }

      public int calculateLeft(final int anchor, final Font font, final FormattedCharSequence text) {
         return anchor;
      }
   },
   CENTER {
      public int calculateLeft(final int anchor, final int width) {
         return anchor - width / 2;
      }
   },
   RIGHT {
      public int calculateLeft(final int anchor, final int width) {
         return anchor - width;
      }
   };

   private TextAlignment() {
   }

   public abstract int calculateLeft(int anchor, int width);

   public int calculateLeft(final int anchor, final Font font, final FormattedCharSequence text) {
      return this.calculateLeft(anchor, font.width(text));
   }

   // $FF: synthetic method
   private static TextAlignment[] $values() {
      return new TextAlignment[]{LEFT, CENTER, RIGHT};
   }
}
