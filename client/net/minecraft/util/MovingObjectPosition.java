package net.minecraft.util;

import net.minecraft.entity.Entity;

public class MovingObjectPosition {
   public MovingObjectPosition$MovingObjectType field_72313_a;
   public int field_72311_b;
   public int field_72312_c;
   public int field_72309_d;
   public int field_72310_e;
   public Vec3 field_72307_f;
   public Entity field_72308_g;

   public MovingObjectPosition(int var1, int var2, int var3, int var4, Vec3 var5) {
      this(var1, var2, var3, var4, var5, true);
   }

   public MovingObjectPosition(int var1, int var2, int var3, int var4, Vec3 var5, boolean var6) {
      super();
      this.field_72313_a = var6 ? MovingObjectPosition$MovingObjectType.BLOCK : MovingObjectPosition$MovingObjectType.MISS;
      this.field_72311_b = var1;
      this.field_72312_c = var2;
      this.field_72309_d = var3;
      this.field_72310_e = var4;
      this.field_72307_f = Vec3.func_72443_a(var5.field_72450_a, var5.field_72448_b, var5.field_72449_c);
   }

   public MovingObjectPosition(Entity var1) {
      this(var1, Vec3.func_72443_a(var1.field_70165_t, var1.field_70163_u, var1.field_70161_v));
   }

   public MovingObjectPosition(Entity var1, Vec3 var2) {
      super();
      this.field_72313_a = MovingObjectPosition$MovingObjectType.ENTITY;
      this.field_72308_g = var1;
      this.field_72307_f = var2;
   }

   @Override
   public String toString() {
      return "HitResult{type="
         + this.field_72313_a
         + ", x="
         + this.field_72311_b
         + ", y="
         + this.field_72312_c
         + ", z="
         + this.field_72309_d
         + ", f="
         + this.field_72310_e
         + ", pos="
         + this.field_72307_f
         + ", entity="
         + this.field_72308_g
         + '}';
   }
}
