package net.minecraft.world.phys;

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

   public boolean equals(Vec2 var1) {
      return this.x == var1.x && this.y == var1.y;
   }
}
