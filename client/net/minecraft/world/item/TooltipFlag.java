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

      public boolean isAdvanced() {
         return this.advanced;
      }

      // $FF: synthetic method
      private static TooltipFlag.Default[] $values() {
         return new TooltipFlag.Default[]{NORMAL, ADVANCED};
      }
   }
}
