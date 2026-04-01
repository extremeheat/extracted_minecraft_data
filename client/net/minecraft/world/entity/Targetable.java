package net.minecraft.world.entity;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.level.entity.UniquelyIdentifyable;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public interface Targetable extends UniquelyIdentifyable {
   boolean canBeSeenByAnyone();

   boolean canBeSeenAsEnemy();

   double getVisibilityPercent(@Nullable Entity observer);

   boolean canAttack(Entity target);

   AABB getHitbox();

   boolean isDeadOrDying();

   DamageSource getLastDamageSource();

   Vec3 position();

   default boolean isAlive() {
      return !this.isRemoved();
   }
}
