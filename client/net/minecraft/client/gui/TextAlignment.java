package net.minecraft.client.gui;

import net.minecraft.util.FormattedCharSequence;

public enum TextAlignment {
   LEFT {
      public int calculateLeft(int var1, int var2) {
         return var1;
      }

      public int calculateLeft(int var1, Font var2, FormattedCharSequence var3) {
         return var1;
      }
   },
   CENTER {
      public int calculateLeft(int var1, int var2) {
         return var1 - var2 / 2;
      }
   },
   RIGHT {
      public int calculateLeft(int var1, int var2) {
         return var1 - var2;
      }
   };

   TextAlignment() {
   }

   public abstract int calculateLeft(int var1, int var2);

   public int calculateLeft(int var1, Font var2, FormattedCharSequence var3) {
      return this.calculateLeft(var1, var2.width(var3));
   }

   // $FF: synthetic method
   private static TextAlignment[] $values() {
      return new TextAlignment[]{LEFT, CENTER, RIGHT};
   }
}
