package net.minecraft.client.particle;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.World;

public class EntityHeartFX extends EntityFX {
   float field_70575_a;

   public EntityHeartFX(World var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      this(var1, var2, var4, var6, var8, var10, var12, 2.0F);
   }

   public EntityHeartFX(World var1, double var2, double var4, double var6, double var8, double var10, double var12, float var14) {
      super(var1, var2, var4, var6, 0.0, 0.0, 0.0);
      this.field_70159_w *= 0.009999999776482582;
      this.field_70181_x *= 0.009999999776482582;
      this.field_70179_y *= 0.009999999776482582;
      this.field_70181_x += 0.1;
      this.field_70544_f *= 0.75F;
      this.field_70544_f *= var14;
      this.field_70575_a = this.field_70544_f;
      this.field_70547_e = 16;
      this.field_70145_X = false;
      this.func_70536_a(80);
   }

   @Override
   public void func_70539_a(Tessellator var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      float var8 = ((float)this.field_70546_d + var2) / (float)this.field_70547_e * 32.0F;
      if (var8 < 0.0F) {
         var8 = 0.0F;
      }

      if (var8 > 1.0F) {
         var8 = 1.0F;
      }

      this.field_70544_f = this.field_70575_a * var8;
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

      this.func_70091_d(this.field_70159_w, this.field_70181_x, this.field_70179_y);
      if (this.field_70163_u == this.field_70167_r) {
         this.field_70159_w *= 1.1;
         this.field_70179_y *= 1.1;
      }

      this.field_70159_w *= 0.8600000143051147;
      this.field_70181_x *= 0.8600000143051147;
      this.field_70179_y *= 0.8600000143051147;
      if (this.field_70122_E) {
         this.field_70159_w *= 0.699999988079071;
         this.field_70179_y *= 0.699999988079071;
      }
   }
}
