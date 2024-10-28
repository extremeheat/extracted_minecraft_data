package net.minecraft.util;

public enum TriState {
   TRUE,
   FALSE,
   DEFAULT;

   private TriState() {
   }

   public boolean toBoolean(boolean var1) {
      boolean var10000;
      switch (this.ordinal()) {
         case 0 -> var10000 = true;
         case 1 -> var10000 = false;
         default -> var10000 = var1;
      }

      return var10000;
   }

   // $FF: synthetic method
   private static TriState[] $values() {
      return new TriState[]{TRUE, FALSE, DEFAULT};
   }
}
