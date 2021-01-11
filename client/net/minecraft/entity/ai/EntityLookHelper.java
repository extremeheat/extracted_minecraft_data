package net.minecraft.entity.ai;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;

public class EntityLookHelper {
   private EntityLiving field_75659_a;
   private float field_75657_b;
   private float field_75658_c;
   private boolean field_75655_d;
   private double field_75656_e;
   private double field_75653_f;
   private double field_75654_g;

   public EntityLookHelper(EntityLiving var1) {
      super();
      this.field_75659_a = var1;
   }

   public void func_75651_a(Entity var1, float var2, float var3) {
      this.field_75656_e = var1.field_70165_t;
      if (var1 instanceof EntityLivingBase) {
         this.field_75653_f = var1.field_70163_u + (double)var1.func_70047_e();
      } else {
         this.field_75653_f = (var1.func_174813_aQ().field_72338_b + var1.func_174813_aQ().field_72337_e) / 2.0D;
      }

      this.field_75654_g = var1.field_70161_v;
      this.field_75657_b = var2;
      this.field_75658_c = var3;
      this.field_75655_d = true;
   }

   public void func_75650_a(double var1, double var3, double var5, float var7, float var8) {
      this.field_75656_e = var1;
      this.field_75653_f = var3;
      this.field_75654_g = var5;
      this.field_75657_b = var7;
      this.field_75658_c = var8;
      this.field_75655_d = true;
   }

   public void func_75649_a() {
      this.field_75659_a.field_70125_A = 0.0F;
      if (this.field_75655_d) {
         this.field_75655_d = false;
         double var1 = this.field_75656_e - this.field_75659_a.field_70165_t;
         double var3 = this.field_75653_f - (this.field_75659_a.field_70163_u + (double)this.field_75659_a.func_70047_e());
         double var5 = this.field_75654_g - this.field_75659_a.field_70161_v;
         double var7 = (double)MathHelper.func_76133_a(var1 * var1 + var5 * var5);
         float var9 = (float)(MathHelper.func_181159_b(var5, var1) * 180.0D / 3.1415927410125732D) - 90.0F;
         float var10 = (float)(-(MathHelper.func_181159_b(var3, var7) * 180.0D / 3.1415927410125732D));
         this.field_75659_a.field_70125_A = this.func_75652_a(this.field_75659_a.field_70125_A, var10, this.field_75658_c);
         this.field_75659_a.field_70759_as = this.func_75652_a(this.field_75659_a.field_70759_as, var9, this.field_75657_b);
      } else {
         this.field_75659_a.field_70759_as = this.func_75652_a(this.field_75659_a.field_70759_as, this.field_75659_a.field_70761_aq, 10.0F);
      }

      float var11 = MathHelper.func_76142_g(this.field_75659_a.field_70759_as - this.field_75659_a.field_70761_aq);
      if (!this.field_75659_a.func_70661_as().func_75500_f()) {
         if (var11 < -75.0F) {
            this.field_75659_a.field_70759_as = this.field_75659_a.field_70761_aq - 75.0F;
         }

         if (var11 > 75.0F) {
            this.field_75659_a.field_70759_as = this.field_75659_a.field_70761_aq + 75.0F;
         }
      }

   }

   private float func_75652_a(float var1, float var2, float var3) {
      float var4 = MathHelper.func_76142_g(var2 - var1);
      if (var4 > var3) {
         var4 = var3;
      }

      if (var4 < -var3) {
         var4 = -var3;
      }

      return var1 + var4;
   }

   public boolean func_180424_b() {
      return this.field_75655_d;
   }

   public double func_180423_e() {
      return this.field_75656_e;
   }

   public double func_180422_f() {
      return this.field_75653_f;
   }

   public double func_180421_g() {
      return this.field_75654_g;
   }
}
