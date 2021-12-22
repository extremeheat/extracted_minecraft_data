package net.minecraft.world.phys;

import net.minecraft.world.entity.Entity;

public abstract class HitResult {
   protected final Vec3 location;

   protected HitResult(Vec3 var1) {
      super();
      this.location = var1;
   }

   public double distanceTo(Entity var1) {
      double var2 = this.location.field_414 - var1.getX();
      double var4 = this.location.field_415 - var1.getY();
      double var6 = this.location.field_416 - var1.getZ();
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

      // $FF: synthetic method
      private static HitResult.Type[] $values() {
         return new HitResult.Type[]{MISS, BLOCK, ENTITY};
      }
   }
}
