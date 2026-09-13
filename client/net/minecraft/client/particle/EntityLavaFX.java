package net.minecraft.client.particle;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.World;

public class EntityLavaFX extends EntityFX {
   private float field_70586_a;

   public EntityLavaFX(World var1, double var2, double var4, double var6) {
      super(var1, var2, var4, var6, 0.0, 0.0, 0.0);
      this.field_70159_w *= 0.800000011920929;
      this.field_70181_x *= 0.800000011920929;
      this.field_70179_y *= 0.800000011920929;
      this.field_70181_x = (double)(this.field_70146_Z.nextFloat() * 0.4F + 0.05F);
      this.field_70552_h = this.field_70553_i = this.field_70551_j = 1.0F;
      this.field_70544_f *= this.field_70146_Z.nextFloat() * 2.0F + 0.2F;
      this.field_70586_a = this.field_70544_f;
      this.field_70547_e = (int)(16.0 / (Math.random() * 0.8 + 0.2));
      this.field_70145_X = false;
      this.func_70536_a(49);
   }

   @Override
   public int func_70070_b(float var1) {
      float var2 = ((float)this.field_70546_d + var1) / (float)this.field_70547_e;
      if (var2 < 0.0F) {
         var2 = 0.0F;
      }

      if (var2 > 1.0F) {
         var2 = 1.0F;
      }

      int var3 = super.func_70070_b(var1);
      short var4 = 240;
      int var5 = var3 >> 16 & 0xFF;
      return var4 | var5 << 16;
   }

   @Override
   public float func_70013_c(float var1) {
      return 1.0F;
   }

   @Override
   public void func_70539_a(Tessellator var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      float var8 = ((float)this.field_70546_d + var2) / (float)this.field_70547_e;
      this.field_70544_f = this.field_70586_a * (1.0F - var8 * var8);
      super.func_70539_a(var1, var2, var3, var4, var5, var6, var7);
   }

   @Override
   public void func_70071_h_() {
      this.field_70169_q = this.field_70165_t;
      this.field_70167_r = this.field_70163_u;
      this.field_70166_s = this.field_70161_v;
      if (this.field_70546_d++ >= this.field_70547_e) {
         this.func_70106_y();
      }

      float var1 = (float)this.field_70546_d / (float)this.field_70547_e;
      if (this.field_70146_Z.nextFloat() > var1) {
         this.field_70170_p
            .func_72869_a("smoke", this.field_70165_t, this.field_70163_u, this.field_70161_v, this.field_70159_w, this.field_70181_x, this.field_70179_y);
      }

      this.field_70181_x -= 0.03;
      this.func_70091_d(this.field_70159_w, this.field_70181_x, this.field_70179_y);
      this.field_70159_w *= 0.9990000128746033;
      this.field_70181_x *= 0.9990000128746033;
      this.field_70179_y *= 0.9990000128746033;
      if (this.field_70122_E) {
         this.field_70159_w *= 0.699999988079071;
         this.field_70179_y *= 0.699999988079071;
      }
   }
}
