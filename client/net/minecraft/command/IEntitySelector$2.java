package net.minecraft.command;

import net.minecraft.entity.Entity;

final class IEntitySelector$2 implements IEntitySelector {
   IEntitySelector$2() {
      super();
   }

   @Override
   public boolean func_82704_a(Entity var1) {
      return var1.func_70089_S() && var1.field_70153_n == null && var1.field_70154_o == null;
   }
}
