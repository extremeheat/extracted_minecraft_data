package net.minecraft.world.item;

public interface TooltipFlag {
   Default NORMAL = new Default(false, false);
   Default ADVANCED = new Default(true, false);

   boolean isAdvanced();

   boolean isCreative();

   public static record Default(boolean advanced, boolean creative) implements TooltipFlag {
      public Default(boolean advanced, boolean creative) {
         super();
         this.advanced = advanced;
         this.creative = creative;
      }

      public boolean isAdvanced() {
         return this.advanced;
      }

      public boolean isCreative() {
         return this.creative;
      }

      public Default asCreative() {
         return new Default(this.advanced, true);
      }

      public boolean advanced() {
         return this.advanced;
      }

      public boolean creative() {
         return this.creative;
      }
   }
}
