package net.minecraft.entity.monster;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;

final class IMob$1 implements IEntitySelector {
   IMob$1() {
      super();
   }

   @Override
   public boolean func_82704_a(Entity var1) {
      return var1 instanceof IMob;
   }
}
