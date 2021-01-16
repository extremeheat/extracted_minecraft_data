package net.minecraft.core.dispenser;

import net.minecraft.core.BlockSource;

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
      var1.getLevel().levelEvent(this.isSuccess() ? 1000 : 1001, var1.getPos(), 0);
   }
}
