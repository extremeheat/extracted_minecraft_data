package net.minecraft.world.item;

public interface TooltipFlag {
   boolean isAdvanced();

   public static enum Default implements TooltipFlag {
      NORMAL(false),
      ADVANCED(true);

      private final boolean advanced;

      private Default(boolean var3) {
         this.advanced = var3;
      }

      @Override
      public boolean isAdvanced() {
         return this.advanced;
      }
   }
}
