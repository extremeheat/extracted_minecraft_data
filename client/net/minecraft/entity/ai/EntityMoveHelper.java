package net.minecraft.entity.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.util.MathHelper;

public class EntityMoveHelper {
   private EntityLiving field_75648_a;
   private double field_75646_b;
   private double field_75647_c;
   private double field_75644_d;
   private double field_75645_e;
   private boolean field_75643_f;

   public EntityMoveHelper(EntityLiving var1) {
      super();
      this.field_75648_a = var1;
      this.field_75646_b = var1.field_70165_t;
      this.field_75647_c = var1.field_70163_u;
      this.field_75644_d = var1.field_70161_v;
   }

   public boolean func_75640_a() {
      return this.field_75643_f;
   }

   public double func_75638_b() {
      return this.field_75645_e;
   }

   public void func_75642_a(double var1, double var3, double var5, double var7) {
      this.field_75646_b = var1;
      this.field_75647_c = var3;
      this.field_75644_d = var5;
      this.field_75645_e = var7;
      this.field_75643_f = true;
   }

   public void func_75641_c() {
      this.field_75648_a.func_70657_f(0.0F);
      if (this.field_75643_f) {
         this.field_75643_f = false;
         int var1 = MathHelper.func_76128_c(this.field_75648_a.field_70121_D.field_72338_b + 0.5);
         double var2 = this.field_75646_b - this.field_75648_a.field_70165_t;
         double var4 = this.field_75644_d - this.field_75648_a.field_70161_v;
         double var6 = this.field_75647_c - (double)var1;
         double var8 = var2 * var2 + var6 * var6 + var4 * var4;
         if (!(var8 < 2.500000277905201E-7)) {
            float var10 = (float)(Math.atan2(var4, var2) * 180.0 / 3.1415927410125732) - 90.0F;
            this.field_75648_a.field_70177_z = this.func_75639_a(this.field_75648_a.field_70177_z, var10, 30.0F);
            this.field_75648_a
               .func_70659_e((float)(this.field_75645_e * this.field_75648_a.func_110148_a(SharedMonsterAttributes.field_111263_d).func_111126_e()));
            if (var6 > 0.0 && var2 * var2 + var4 * var4 < 1.0) {
               this.field_75648_a.func_70683_ar().func_75660_a();
            }
         }
      }
   }

   private float func_75639_a(float var1, float var2, float var3) {
      float var4 = MathHelper.func_76142_g(var2 - var1);
      if (var4 > var3) {
         var4 = var3;
      }

      if (var4 < -var3) {
         var4 = -var3;
      }

      return var1 + var4;
   }
}
