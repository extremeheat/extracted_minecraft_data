package net.minecraft.client.particle;

import net.minecraft.world.World;

public class EntityEnchantmentTableParticleFX extends EntityFX {
   private float field_70565_a;
   private double field_70568_aq;
   private double field_70567_ar;
   private double field_70566_as;

   protected EntityEnchantmentTableParticleFX(World var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      super(var1, var2, var4, var6, var8, var10, var12);
      this.field_70159_w = var8;
      this.field_70181_x = var10;
      this.field_70179_y = var12;
      this.field_70568_aq = var2;
      this.field_70567_ar = var4;
      this.field_70566_as = var6;
      this.field_70165_t = this.field_70169_q = var2 + var8;
      this.field_70163_u = this.field_70167_r = var4 + var10;
      this.field_70161_v = this.field_70166_s = var6 + var12;
      float var14 = this.field_70146_Z.nextFloat() * 0.6F + 0.4F;
      this.field_70565_a = this.field_70544_f = this.field_70146_Z.nextFloat() * 0.5F + 0.2F;
      this.field_70552_h = this.field_70553_i = this.field_70551_j = 1.0F * var14;
      this.field_70553_i *= 0.9F;
      this.field_70552_h *= 0.9F;
      this.field_70547_e = (int)(Math.random() * 10.0D) + 30;
      this.field_70145_X = true;
      this.func_70536_a((int)(Math.random() * 26.0D + 1.0D + 224.0D));
   }

   public int func_70070_b(float var1) {
      int var2 = super.func_70070_b(var1);
      float var3 = (float)this.field_70546_d / (float)this.field_70547_e;
      var3 *= var3;
      var3 *= var3;
      int var4 = var2 & 255;
      int var5 = var2 >> 16 & 255;
      var5 += (int)(var3 * 15.0F * 16.0F);
      if (var5 > 240) {
         var5 = 240;
      }

      return var4 | var5 << 16;
   }

   public float func_70013_c(float var1) {
      float var2 = super.func_70013_c(var1);
      float var3 = (float)this.field_70546_d / (float)this.field_70547_e;
      var3 *= var3;
      var3 *= var3;
      return var2 * (1.0F - var3) + var3;
   }

   public void func_70071_h_() {
      this.field_70169_q = this.field_70165_t;
      this.field_70167_r = this.field_70163_u;
      this.field_70166_s = this.field_70161_v;
      float var1 = (float)this.field_70546_d / (float)this.field_70547_e;
      var1 = 1.0F - var1;
      float var2 = 1.0F - var1;
      var2 *= var2;
      var2 *= var2;
      this.field_70165_t = this.field_70568_aq + this.field_70159_w * (double)var1;
      this.field_70163_u = this.field_70567_ar + this.field_70181_x * (double)var1 - (double)(var2 * 1.2F);
      this.field_70161_v = this.field_70566_as + this.field_70179_y * (double)var1;
      if (this.field_70546_d++ >= this.field_70547_e) {
         this.func_70106_y();
      }

   }

   public static class EnchantmentTable implements IParticleFactory {
      public EnchantmentTable() {
         super();
      }

      public EntityFX func_178902_a(int var1, World var2, double var3, double var5, double var7, double var9, double var11, double var13, int... var15) {
         return new EntityEnchantmentTableParticleFX(var2, var3, var5, var7, var9, var11, var13);
      }
   }
}
