package net.minecraft.entity.passive;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;

final class EntityHorse$1 implements IEntitySelector {
   EntityHorse$1() {
      super();
   }

   @Override
   public boolean func_82704_a(Entity var1) {
      return var1 instanceof EntityHorse && ((EntityHorse)var1).func_110205_ce();
   }
}
