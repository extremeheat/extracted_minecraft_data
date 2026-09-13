package net.minecraft.client.particle;

import net.minecraft.world.World;

public class EntityAuraFX extends EntityFX {
   public EntityAuraFX(World var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      super(var1, var2, var4, var6, var8, var10, var12);
      float var14 = this.field_70146_Z.nextFloat() * 0.1F + 0.2F;
      this.field_70552_h = var14;
      this.field_70553_i = var14;
      this.field_70551_j = var14;
      this.func_70536_a(0);
      this.func_70105_a(0.02F, 0.02F);
      this.field_70544_f *= this.field_70146_Z.nextFloat() * 0.6F + 0.5F;
      this.field_70159_w *= 0.019999999552965164;
      this.field_70181_x *= 0.019999999552965164;
      this.field_70179_y *= 0.019999999552965164;
      this.field_70547_e = (int)(20.0 / (Math.random() * 0.8 + 0.2));
      this.field_70145_X = true;
   }

   @Override
   public void func_70071_h_() {
      this.field_70169_q = this.field_70165_t;
      this.field_70167_r = this.field_70163_u;
      this.field_70166_s = this.field_70161_v;
      this.func_70091_d(this.field_70159_w, this.field_70181_x, this.field_70179_y);
      this.field_70159_w *= 0.99;
      this.field_70181_x *= 0.99;
      this.field_70179_y *= 0.99;
      if (this.field_70547_e-- <= 0) {
         this.func_70106_y();
      }
   }
}
