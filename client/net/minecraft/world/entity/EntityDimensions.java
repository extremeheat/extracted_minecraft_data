package net.minecraft.world.entity;

import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class EntityDimensions {
   public final float width;
   public final float height;
   public final boolean fixed;

   public EntityDimensions(float var1, float var2, boolean var3) {
      super();
      this.width = var1;
      this.height = var2;
      this.fixed = var3;
   }

   public AABB makeBoundingBox(Vec3 var1) {
      return this.makeBoundingBox(var1.field_414, var1.field_415, var1.field_416);
   }

   public AABB makeBoundingBox(double var1, double var3, double var5) {
      float var7 = this.width / 2.0F;
      float var8 = this.height;
      return new AABB(var1 - (double)var7, var3, var5 - (double)var7, var1 + (double)var7, var3 + (double)var8, var5 + (double)var7);
   }

   public EntityDimensions scale(float var1) {
      return this.scale(var1, var1);
   }

   public EntityDimensions scale(float var1, float var2) {
      return !this.fixed && (var1 != 1.0F || var2 != 1.0F) ? scalable(this.width * var1, this.height * var2) : this;
   }

   public static EntityDimensions scalable(float var0, float var1) {
      return new EntityDimensions(var0, var1, false);
   }

   public static EntityDimensions fixed(float var0, float var1) {
      return new EntityDimensions(var0, var1, true);
   }

   public String toString() {
      return "EntityDimensions w=" + this.width + ", h=" + this.height + ", fixed=" + this.fixed;
   }
}
