package net.minecraft.init;

import net.minecraft.dispenser.BehaviorProjectileDispense;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.world.World;

final class Bootstrap$4 extends BehaviorProjectileDispense {
   Bootstrap$4() {
      super();
   }

   @Override
   protected IProjectile func_82499_a(World var1, IPosition var2) {
      return new EntityExpBottle(var1, var2.func_82615_a(), var2.func_82617_b(), var2.func_82616_c());
   }

   @Override
   protected float func_82498_a() {
      return super.func_82498_a() * 0.5F;
   }

   @Override
   protected float func_82500_b() {
      return super.func_82500_b() * 1.25F;
   }
}
