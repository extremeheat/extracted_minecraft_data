package net.minecraft.client.particle;

import net.minecraft.world.World;

public class EntityAuraFX extends EntityFX {
   protected EntityAuraFX(World var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      super(var1, var2, var4, var6, var8, var10, var12);
      float var14 = this.field_70146_Z.nextFloat() * 0.1F + 0.2F;
      this.field_70552_h = var14;
      this.field_70553_i = var14;
      this.field_70551_j = var14;
      this.func_70536_a(0);
      this.func_70105_a(0.02F, 0.02F);
      this.field_70544_f *= this.field_70146_Z.nextFloat() * 0.6F + 0.5F;
      this.field_70159_w *= 0.019999999552965164D;
      this.field_70181_x *= 0.019999999552965164D;
      this.field_70179_y *= 0.019999999552965164D;
      this.field_70547_e = (int)(20.0D / (Math.random() * 0.8D + 0.2D));
      this.field_70145_X = true;
   }

   public void func_70071_h_() {
      this.field_70169_q = this.field_70165_t;
      this.field_70167_r = this.field_70163_u;
      this.field_70166_s = this.field_70161_v;
      this.func_70091_d(this.field_70159_w, this.field_70181_x, this.field_70179_y);
      this.field_70159_w *= 0.99D;
      this.field_70181_x *= 0.99D;
      this.field_70179_y *= 0.99D;
      if (this.field_70547_e-- <= 0) {
         this.func_70106_y();
      }

   }

   public static class HappyVillagerFactory implements IParticleFactory {
      public HappyVillagerFactory() {
         super();
      }

      public EntityFX func_178902_a(int var1, World var2, double var3, double var5, double var7, double var9, double var11, double var13, int... var15) {
         EntityAuraFX var16 = new EntityAuraFX(var2, var3, var5, var7, var9, var11, var13);
         var16.func_70536_a(82);
         var16.func_70538_b(1.0F, 1.0F, 1.0F);
         return var16;
      }
   }

   public static class Factory implements IParticleFactory {
      public Factory() {
         super();
      }

      public EntityFX func_178902_a(int var1, World var2, double var3, double var5, double var7, double var9, double var11, double var13, int... var15) {
         return new EntityAuraFX(var2, var3, var5, var7, var9, var11, var13);
      }
   }
}
