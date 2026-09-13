package net.minecraft.init;

import net.minecraft.dispenser.BehaviorProjectileDispense;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.world.World;

final class Bootstrap$3 extends BehaviorProjectileDispense {
   Bootstrap$3() {
      super();
   }

   @Override
   protected IProjectile func_82499_a(World var1, IPosition var2) {
      return new EntitySnowball(var1, var2.func_82615_a(), var2.func_82617_b(), var2.func_82616_c());
   }
}
