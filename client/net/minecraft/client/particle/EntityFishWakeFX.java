package net.minecraft.client.particle;

import net.minecraft.world.World;

public class EntityFishWakeFX extends EntityFX {
   protected EntityFishWakeFX(World var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      super(var1, var2, var4, var6, 0.0D, 0.0D, 0.0D);
      this.field_70159_w *= 0.30000001192092896D;
      this.field_70181_x = Math.random() * 0.20000000298023224D + 0.10000000149011612D;
      this.field_70179_y *= 0.30000001192092896D;
      this.field_70552_h = 1.0F;
      this.field_70553_i = 1.0F;
      this.field_70551_j = 1.0F;
      this.func_70536_a(19);
      this.func_70105_a(0.01F, 0.01F);
      this.field_70547_e = (int)(8.0D / (Math.random() * 0.8D + 0.2D));
      this.field_70545_g = 0.0F;
      this.field_70159_w = var8;
      this.field_70181_x = var10;
      this.field_70179_y = var12;
   }

   public void func_70071_h_() {
      this.field_70169_q = this.field_70165_t;
      this.field_70167_r = this.field_70163_u;
      this.field_70166_s = this.field_70161_v;
      this.field_70181_x -= (double)this.field_70545_g;
      this.func_70091_d(this.field_70159_w, this.field_70181_x, this.field_70179_y);
      this.field_70159_w *= 0.9800000190734863D;
      this.field_70181_x *= 0.9800000190734863D;
      this.field_70179_y *= 0.9800000190734863D;
      int var1 = 60 - this.field_70547_e;
      float var2 = (float)var1 * 0.001F;
      this.func_70105_a(var2, var2);
      this.func_70536_a(19 + var1 % 4);
      if (this.field_70547_e-- <= 0) {
         this.func_70106_y();
      }

   }

   public static class Factory implements IParticleFactory {
      public Factory() {
         super();
      }

      public EntityFX func_178902_a(int var1, World var2, double var3, double var5, double var7, double var9, double var11, double var13, int... var15) {
         return new EntityFishWakeFX(var2, var3, var5, var7, var9, var11, var13);
      }
   }
}
