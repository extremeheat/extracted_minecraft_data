package net.minecraft.world.phys;

import net.minecraft.world.entity.Entity;

public abstract class HitResult {
   protected final Vec3 location;

   protected HitResult(Vec3 var1) {
      super();
      this.location = var1;
   }

   public double distanceTo(Entity var1) {
      double var2 = this.location.x - var1.getX();
      double var4 = this.location.y - var1.getY();
      double var6 = this.location.z - var1.getZ();
      return var2 * var2 + var4 * var4 + var6 * var6;
   }

   public abstract HitResult.Type getType();

   public Vec3 getLocation() {
      return this.location;
   }

   public static enum Type {
      MISS,
      BLOCK,
      ENTITY;

      private Type() {
      }
   }
}
