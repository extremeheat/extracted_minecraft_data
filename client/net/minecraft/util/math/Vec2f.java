package net.minecraft.util.math;

public class Vec2f {
   public static final Vec2f field_189974_a = new Vec2f(0.0F, 0.0F);
   public static final Vec2f field_189975_b = new Vec2f(1.0F, 1.0F);
   public static final Vec2f field_189976_c = new Vec2f(1.0F, 0.0F);
   public static final Vec2f field_189977_d = new Vec2f(-1.0F, 0.0F);
   public static final Vec2f field_189978_e = new Vec2f(0.0F, 1.0F);
   public static final Vec2f field_189979_f = new Vec2f(0.0F, -1.0F);
   public static final Vec2f field_189980_g = new Vec2f(3.4028235E38F, 3.4028235E38F);
   public static final Vec2f field_189981_h = new Vec2f(1.4E-45F, 1.4E-45F);
   public final float field_189982_i;
   public final float field_189983_j;

   public Vec2f(float var1, float var2) {
      super();
      this.field_189982_i = var1;
      this.field_189983_j = var2;
   }

   public boolean func_201069_c(Vec2f var1) {
      return this.field_189982_i == var1.field_189982_i && this.field_189983_j == var1.field_189983_j;
   }
}
