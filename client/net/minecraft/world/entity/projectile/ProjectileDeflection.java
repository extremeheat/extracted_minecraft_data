package net.minecraft.world.entity.projectile;

import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

@FunctionalInterface
public interface ProjectileDeflection {
   ProjectileDeflection NONE = (var0, var1, var2, var3) -> {
   };
   ProjectileDeflection REVERSE = (projectile, var1, random, power) -> {
      float rotation = 170.0F + random.nextFloat() * 20.0F;
      projectile.setDeltaMovement(projectile.getDeltaMovement().multiply(-power.x * 0.5, -power.y * 0.5, -power.z * 0.5));
      projectile.setYRot(projectile.getYRot() + rotation);
      projectile.yRotO += rotation;
      projectile.needsSync = true;
   };
   ProjectileDeflection AIM_DEFLECT = (projectile, entity, var2, power) -> {
      if (entity != null) {
         Vec3 lookAngle = entity.getLookAngle();
         projectile.setDeltaMovement(lookAngle.multiply(power));
         projectile.needsSync = true;
      }

   };
   ProjectileDeflection MOMENTUM_DEFLECT = (projectile, entity, var2, power) -> {
      if (entity != null) {
         Vec3 movement = entity.getKnownSpeed().normalize();
         projectile.setDeltaMovement(movement.multiply(power));
         projectile.needsSync = true;
      }

   };

   void deflect(Projectile projectile, @Nullable Entity entity, RandomSource random, Vec3 power);
}
