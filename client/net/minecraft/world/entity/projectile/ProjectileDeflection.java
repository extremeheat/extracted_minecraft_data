package net.minecraft.world.entity.projectile;

import javax.annotation.Nullable;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

@FunctionalInterface
public interface ProjectileDeflection {
   ProjectileDeflection NONE = (var0, var1, var2) -> {
   };
   ProjectileDeflection REVERSE = (var0, var1, var2) -> {
      float var3 = 170.0F + var2.nextFloat() * 20.0F;
      var0.setDeltaMovement(var0.getDeltaMovement().scale(-0.5));
      var0.setYRot(var0.getYRot() + var3);
      var0.yRotO += var3;
      var0.hasImpulse = true;
   };
   ProjectileDeflection AIM_DEFLECT = (var0, var1, var2) -> {
      if (var1 != null) {
         Vec3 var3 = var1.getLookAngle().normalize();
         var0.setDeltaMovement(var3);
         var0.hasImpulse = true;
      }

   };
   ProjectileDeflection MOMENTUM_DEFLECT = (var0, var1, var2) -> {
      if (var1 != null) {
         Vec3 var3 = var1.getDeltaMovement().normalize();
         var0.setDeltaMovement(var3);
         var0.hasImpulse = true;
      }

   };

   void deflect(Projectile var1, @Nullable Entity var2, RandomSource var3);
}
