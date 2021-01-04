package net.minecraft.core.dispenser;

import net.minecraft.core.BlockSource;

public abstract class OptionalDispenseItemBehavior extends DefaultDispenseItemBehavior {
   protected boolean success = true;

   public OptionalDispenseItemBehavior() {
      super();
   }

   protected void playSound(BlockSource var1) {
      var1.getLevel().levelEvent(this.success ? 1000 : 1001, var1.getPos(), 0);
   }
}
