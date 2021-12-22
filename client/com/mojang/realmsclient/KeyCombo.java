package com.mojang.realmsclient;

import java.util.Arrays;

public class KeyCombo {
   private final char[] chars;
   private int matchIndex;
   private final Runnable onCompletion;

   public KeyCombo(char[] var1, Runnable var2) {
      super();
      this.onCompletion = var2;
      if (var1.length < 1) {
         throw new IllegalArgumentException("Must have at least one char");
      } else {
         this.chars = var1;
      }
   }

   public KeyCombo(char[] var1) {
      this(var1, () -> {
      });
   }

   public boolean keyPressed(char var1) {
      if (var1 == this.chars[this.matchIndex++]) {
         if (this.matchIndex == this.chars.length) {
            this.reset();
            this.onCompletion.run();
            return true;
         }
      } else {
         this.reset();
      }

      return false;
   }

   public void reset() {
      this.matchIndex = 0;
   }

   public String toString() {
      String var10000 = Arrays.toString(this.chars);
      return "KeyCombo{chars=" + var10000 + ", matchIndex=" + this.matchIndex + "}";
   }
}
