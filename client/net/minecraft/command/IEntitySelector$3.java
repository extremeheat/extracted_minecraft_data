package net.minecraft.command;

import net.minecraft.entity.Entity;
import net.minecraft.inventory.IInventory;

final class IEntitySelector$3 implements IEntitySelector {
   IEntitySelector$3() {
      super();
   }

   @Override
   public boolean func_82704_a(Entity var1) {
      return var1 instanceof IInventory && var1.func_70089_S();
   }
}
