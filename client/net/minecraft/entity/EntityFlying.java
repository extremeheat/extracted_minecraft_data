package net.minecraft.entity;

import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public abstract class EntityFlying extends EntityLiving {
   public EntityFlying(World var1) {
      super(var1);
   }

   @Override
   protected void func_70069_a(float var1) {
   }

   @Override
   protected void func_70064_a(double var1, boolean var3) {
   }

   @Override
   public void func_70612_e(float var1, float var2) {
      if (this.func_70090_H()) {
         this.func_70060_a(var1, var2, 0.02F);
         this.func_70091_d(this.field_70159_w, this.field_70181_x, this.field_70179_y);
         this.field_70159_w *= 0.800000011920929;
         this.field_70181_x *= 0.800000011920929;
         this.field_70179_y *= 0.800000011920929;
      } else if (this.func_70058_J()) {
         this.func_70060_a(var1, var2, 0.02F);
         this.func_70091_d(this.field_70159_w, this.field_70181_x, this.field_70179_y);
         this.field_70159_w *= 0.5;
         this.field_70181_x *= 0.5;
         this.field_70179_y *= 0.5;
      } else {
         float var3 = 0.91F;
         if (this.field_70122_E) {
            var3 = this.field_70170_p
                  .func_147439_a(
                     MathHelper.func_76128_c(this.field_70165_t),
                     MathHelper.func_76128_c(this.field_70121_D.field_72338_b) - 1,
                     MathHelper.func_76128_c(this.field_70161_v)
                  )
                  .field_149765_K
               * 0.91F;
         }

         float var4 = 0.16277136F / (var3 * var3 * var3);
         this.func_70060_a(var1, var2, this.field_70122_E ? 0.1F * var4 : 0.02F);
         var3 = 0.91F;
         if (this.field_70122_E) {
            var3 = this.field_70170_p
                  .func_147439_a(
                     MathHelper.func_76128_c(this.field_70165_t),
                     MathHelper.func_76128_c(this.field_70121_D.field_72338_b) - 1,
                     MathHelper.func_76128_c(this.field_70161_v)
                  )
                  .field_149765_K
               * 0.91F;
         }

         this.func_70091_d(this.field_70159_w, this.field_70181_x, this.field_70179_y);
         this.field_70159_w *= (double)var3;
         this.field_70181_x *= (double)var3;
         this.field_70179_y *= (double)var3;
      }

      this.field_70722_aY = this.field_70721_aZ;
      double var9 = this.field_70165_t - this.field_70169_q;
      double var5 = this.field_70161_v - this.field_70166_s;
      float var7 = MathHelper.func_76133_a(var9 * var9 + var5 * var5) * 4.0F;
      if (var7 > 1.0F) {
         var7 = 1.0F;
      }

      this.field_70721_aZ += (var7 - this.field_70721_aZ) * 0.4F;
      this.field_70754_ba += this.field_70721_aZ;
   }

   @Override
   public boolean func_70617_f_() {
      return false;
   }
}
