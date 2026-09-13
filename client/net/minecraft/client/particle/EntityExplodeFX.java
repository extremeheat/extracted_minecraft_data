package net.minecraft.client.particle;

import net.minecraft.world.World;

public class EntityExplodeFX extends EntityFX {
   public EntityExplodeFX(World var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      super(var1, var2, var4, var6, var8, var10, var12);
      this.field_70159_w = var8 + (double)((float)(Math.random() * 2.0 - 1.0) * 0.05F);
      this.field_70181_x = var10 + (double)((float)(Math.random() * 2.0 - 1.0) * 0.05F);
      this.field_70179_y = var12 + (double)((float)(Math.random() * 2.0 - 1.0) * 0.05F);
      this.field_70552_h = this.field_70553_i = this.field_70551_j = this.field_70146_Z.nextFloat() * 0.3F + 0.7F;
      this.field_70544_f = this.field_70146_Z.nextFloat() * this.field_70146_Z.nextFloat() * 6.0F + 1.0F;
      this.field_70547_e = (int)(16.0 / ((double)this.field_70146_Z.nextFloat() * 0.8 + 0.2)) + 2;
   }

   @Override
   public void func_70071_h_() {
      this.field_70169_q = this.field_70165_t;
      this.field_70167_r = this.field_70163_u;
      this.field_70166_s = this.field_70161_v;
      if (this.field_70546_d++ >= this.field_70547_e) {
         this.func_70106_y();
      }

      this.func_70536_a(7 - this.field_70546_d * 8 / this.field_70547_e);
      this.field_70181_x += 0.004;
      this.func_70091_d(this.field_70159_w, this.field_70181_x, this.field_70179_y);
      this.field_70159_w *= 0.8999999761581421;
      this.field_70181_x *= 0.8999999761581421;
      this.field_70179_y *= 0.8999999761581421;
      if (this.field_70122_E) {
         this.field_70159_w *= 0.699999988079071;
         this.field_70179_y *= 0.699999988079071;
      }
   }
}
