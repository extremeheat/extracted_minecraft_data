package net.minecraft.client.particle;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.World;

public class EntityCritFX extends EntityFX {
   float field_70561_a;

   public EntityCritFX(World var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      this(var1, var2, var4, var6, var8, var10, var12, 1.0F);
   }

   public EntityCritFX(World var1, double var2, double var4, double var6, double var8, double var10, double var12, float var14) {
      super(var1, var2, var4, var6, 0.0, 0.0, 0.0);
      this.field_70159_w *= 0.10000000149011612;
      this.field_70181_x *= 0.10000000149011612;
      this.field_70179_y *= 0.10000000149011612;
      this.field_70159_w += var8 * 0.4;
      this.field_70181_x += var10 * 0.4;
      this.field_70179_y += var12 * 0.4;
      this.field_70552_h = this.field_70553_i = this.field_70551_j = (float)(Math.random() * 0.30000001192092896 + 0.6000000238418579);
      this.field_70544_f *= 0.75F;
      this.field_70544_f *= var14;
      this.field_70561_a = this.field_70544_f;
      this.field_70547_e = (int)(6.0 / (Math.random() * 0.8 + 0.6));
      this.field_70547_e = (int)((float)this.field_70547_e * var14);
      this.field_70145_X = false;
      this.func_70536_a(65);
      this.func_70071_h_();
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

      this.field_70544_f = this.field_70561_a * var8;
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
      this.field_70553_i = (float)((double)this.field_70553_i * 0.96);
      this.field_70551_j = (float)((double)this.field_70551_j * 0.9);
      this.field_70159_w *= 0.699999988079071;
      this.field_70181_x *= 0.699999988079071;
      this.field_70179_y *= 0.699999988079071;
      this.field_70181_x -= 0.019999999552965164;
      if (this.field_70122_E) {
         this.field_70159_w *= 0.699999988079071;
         this.field_70179_y *= 0.699999988079071;
      }
   }
}
