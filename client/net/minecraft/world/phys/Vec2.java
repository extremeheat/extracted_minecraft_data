package net.minecraft.world.phys;

import com.mojang.serialization.Codec;
import java.util.List;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;

public class Vec2 {
   public static final Vec2 ZERO = new Vec2(0.0F, 0.0F);
   public static final Vec2 ONE = new Vec2(1.0F, 1.0F);
   public static final Vec2 UNIT_X = new Vec2(1.0F, 0.0F);
   public static final Vec2 NEG_UNIT_X = new Vec2(-1.0F, 0.0F);
   public static final Vec2 UNIT_Y = new Vec2(0.0F, 1.0F);
   public static final Vec2 NEG_UNIT_Y = new Vec2(0.0F, -1.0F);
   public static final Vec2 MAX = new Vec2(3.4028235E38F, 3.4028235E38F);
   public static final Vec2 MIN = new Vec2(1.4E-45F, 1.4E-45F);
   public static final Codec<Vec2> CODEC;
   public final float x;
   public final float y;

   public Vec2(final float x, final float y) {
      super();
      this.x = x;
      this.y = y;
   }

   public Vec2 scale(final float s) {
      return new Vec2(this.x * s, this.y * s);
   }

   public float dot(final Vec2 v) {
      return this.x * v.x + this.y * v.y;
   }

   public Vec2 add(final Vec2 rhs) {
      return new Vec2(this.x + rhs.x, this.y + rhs.y);
   }

   public Vec2 add(final float v) {
      return new Vec2(this.x + v, this.y + v);
   }

   public boolean equals(final Vec2 rhs) {
      return this.x == rhs.x && this.y == rhs.y;
   }

   public Vec2 normalized() {
      float dist = Mth.sqrt(this.x * this.x + this.y * this.y);
      return dist < 1.0E-4F ? ZERO : new Vec2(this.x / dist, this.y / dist);
   }

   public float length() {
      return Mth.sqrt(this.x * this.x + this.y * this.y);
   }

   public float lengthSquared() {
      return this.x * this.x + this.y * this.y;
   }

   public float distanceToSqr(final Vec2 p) {
      float xd = p.x - this.x;
      float yd = p.y - this.y;
      return xd * xd + yd * yd;
   }

   public Vec2 negated() {
      return new Vec2(-this.x, -this.y);
   }

   static {
      CODEC = Codec.FLOAT.listOf().comapFlatMap((input) -> Util.fixedSize((List)input, 2).map((floats) -> new Vec2((Float)floats.get(0), (Float)floats.get(1))), (vec) -> List.of(vec.x, vec.y));
   }
}
