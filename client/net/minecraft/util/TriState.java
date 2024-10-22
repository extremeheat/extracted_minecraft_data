package net.minecraft.util;

public enum TriState {
   TRUE,
   FALSE,
   DEFAULT;

   private TriState() {
   }

   public boolean toBoolean(boolean var1) {
      return switch (this) {
         case TRUE -> true;
         case FALSE -> false;
         default -> var1;
      };
   }
}
