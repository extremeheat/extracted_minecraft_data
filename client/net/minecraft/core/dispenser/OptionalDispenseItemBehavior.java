package net.minecraft.core.dispenser;

public abstract class OptionalDispenseItemBehavior extends DefaultDispenseItemBehavior {
   private boolean success = true;

   public OptionalDispenseItemBehavior() {
      super();
   }

   public boolean isSuccess() {
      return this.success;
   }

   public void setSuccess(boolean var1) {
      this.success = var1;
   }

   protected void playSound(BlockSource var1) {
      var1.level().levelEvent(this.isSuccess() ? 1000 : 1001, var1.pos(), 0);
   }
}
