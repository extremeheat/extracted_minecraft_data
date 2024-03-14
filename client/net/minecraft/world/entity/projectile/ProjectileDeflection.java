package net.minecraft.world.entity.projectile;

import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;

@FunctionalInterface
public interface ProjectileDeflection {
   ProjectileDeflection NONE = (var0, var1, var2) -> {
   };
   ProjectileDeflection REVERSE = (var0, var1, var2) -> {
      float var3 = 180.0F + var2.nextFloat() * 20.0F;
      var0.setDeltaMovement(var0.getDeltaMovement().scale(-0.25));
      var0.setYRot(var0.getYRot() + var3);
      var0.yRotO += var3;
   };

   void deflect(Projectile var1, Entity var2, RandomSource var3);
}
