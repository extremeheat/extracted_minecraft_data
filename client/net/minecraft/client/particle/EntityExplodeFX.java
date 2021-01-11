package net.minecraft.client.particle;

import net.minecraft.world.World;

public class EntityExplodeFX extends EntityFX {
   protected EntityExplodeFX(World var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      super(var1, var2, var4, var6, var8, var10, var12);
      this.field_70159_w = var8 + (Math.random() * 2.0D - 1.0D) * 0.05000000074505806D;
      this.field_70181_x = var10 + (Math.random() * 2.0D - 1.0D) * 0.05000000074505806D;
      this.field_70179_y = var12 + (Math.random() * 2.0D - 1.0D) * 0.05000000074505806D;
      this.field_70552_h = this.field_70553_i = this.field_70551_j = this.field_70146_Z.nextFloat() * 0.3F + 0.7F;
      this.field_70544_f = this.field_70146_Z.nextFloat() * this.field_70146_Z.nextFloat() * 6.0F + 1.0F;
      this.field_70547_e = (int)(16.0D / ((double)this.field_70146_Z.nextFloat() * 0.8D + 0.2D)) + 2;
   }

   public void func_70071_h_() {
      this.field_70169_q = this.field_70165_t;
      this.field_70167_r = this.field_70163_u;
      this.field_70166_s = this.field_70161_v;
      if (this.field_70546_d++ >= this.field_70547_e) {
         this.func_70106_y();
      }

      this.func_70536_a(7 - this.field_70546_d * 8 / this.field_70547_e);
      this.field_70181_x += 0.004D;
      this.func_70091_d(this.field_70159_w, this.field_70181_x, this.field_70179_y);
      this.field_70159_w *= 0.8999999761581421D;
      this.field_70181_x *= 0.8999999761581421D;
      this.field_70179_y *= 0.8999999761581421D;
      if (this.field_70122_E) {
         this.field_70159_w *= 0.699999988079071D;
         this.field_70179_y *= 0.699999988079071D;
      }

   }

   public static class Factory implements IParticleFactory {
      public Factory() {
         super();
      }

      public EntityFX func_178902_a(int var1, World var2, double var3, double var5, double var7, double var9, double var11, double var13, int... var15) {
         return new EntityExplodeFX(var2, var3, var5, var7, var9, var11, var13);
      }
   }
}
