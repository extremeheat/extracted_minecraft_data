package net.minecraft.world.phys;

import net.minecraft.util.Mth;

public class Vec2 {
   public static final Vec2 ZERO = new Vec2(0.0F, 0.0F);
   public static final Vec2 ONE = new Vec2(1.0F, 1.0F);
   public static final Vec2 UNIT_X = new Vec2(1.0F, 0.0F);
   public static final Vec2 NEG_UNIT_X = new Vec2(-1.0F, 0.0F);
   public static final Vec2 UNIT_Y = new Vec2(0.0F, 1.0F);
   public static final Vec2 NEG_UNIT_Y = new Vec2(0.0F, -1.0F);
   public static final Vec2 MAX = new Vec2(3.4028235E38F, 3.4028235E38F);
   public static final Vec2 MIN = new Vec2(1.4E-45F, 1.4E-45F);
   public final float x;
   public final float y;

   public Vec2(float var1, float var2) {
      super();
      this.x = var1;
      this.y = var2;
   }

   public Vec2 scale(float var1) {
      return new Vec2(this.x * var1, this.y * var1);
   }

   public float dot(Vec2 var1) {
      return this.x * var1.x + this.y * var1.y;
   }

   public Vec2 add(Vec2 var1) {
      return new Vec2(this.x + var1.x, this.y + var1.y);
   }

   public Vec2 add(float var1) {
      return new Vec2(this.x + var1, this.y + var1);
   }

   public boolean equals(Vec2 var1) {
      return this.x == var1.x && this.y == var1.y;
   }

   public Vec2 normalized() {
      float var1 = Mth.sqrt(this.x * this.x + this.y * this.y);
      return var1 < 1.0E-4F ? ZERO : new Vec2(this.x / var1, this.y / var1);
   }

   public float length() {
      return Mth.sqrt(this.x * this.x + this.y * this.y);
   }

   public float lengthSquared() {
      return this.x * this.x + this.y * this.y;
   }

   public float distanceToSqr(Vec2 var1) {
      float var2 = var1.x - this.x;
      float var3 = var1.y - this.y;
      return var2 * var2 + var3 * var3;
   }

   public Vec2 negated() {
      return new Vec2(-this.x, -this.y);
   }
}
