package net.minecraft.world.phys;

import net.minecraft.world.entity.Entity;

public abstract class HitResult {
   protected final Vec3 location;

   protected HitResult(final Vec3 location) {
      super();
      this.location = location;
   }

   public double distanceTo(final Entity entity) {
      double xd = this.location.x - entity.getX();
      double yd = this.location.y - entity.getY();
      double zd = this.location.z - entity.getZ();
      return xd * xd + yd * yd + zd * zd;
   }

   public abstract Type getType();

   public Vec3 getLocation() {
      return this.location;
   }

   public static enum Type {
      MISS,
      BLOCK,
      ENTITY;

      private Type() {
      }

      // $FF: synthetic method
      private static Type[] $values() {
         return new Type[]{MISS, BLOCK, ENTITY};
      }
   }
}
