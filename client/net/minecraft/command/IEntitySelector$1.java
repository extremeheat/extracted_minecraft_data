package net.minecraft.command;

import net.minecraft.entity.Entity;

final class IEntitySelector$1 implements IEntitySelector {
   IEntitySelector$1() {
      super();
   }

   @Override
   public boolean func_82704_a(Entity var1) {
      return var1.func_70089_S();
   }
}
