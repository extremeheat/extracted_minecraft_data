package net.minecraft.client.particle;

import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public class EntityPortalFX extends EntityFX {
   private float field_70571_a;
   private double field_70574_aq;
   private double field_70573_ar;
   private double field_70572_as;

   protected EntityPortalFX(World var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      super(var1, var2, var4, var6, var8, var10, var12);
      this.field_70159_w = var8;
      this.field_70181_x = var10;
      this.field_70179_y = var12;
      this.field_70574_aq = this.field_70165_t = var2;
      this.field_70573_ar = this.field_70163_u = var4;
      this.field_70572_as = this.field_70161_v = var6;
      float var14 = this.field_70146_Z.nextFloat() * 0.6F + 0.4F;
      this.field_70571_a = this.field_70544_f = this.field_70146_Z.nextFloat() * 0.2F + 0.5F;
      this.field_70552_h = this.field_70553_i = this.field_70551_j = 1.0F * var14;
      this.field_70553_i *= 0.3F;
      this.field_70552_h *= 0.9F;
      this.field_70547_e = (int)(Math.random() * 10.0D) + 40;
      this.field_70145_X = true;
      this.func_70536_a((int)(Math.random() * 8.0D));
   }

   public void func_180434_a(WorldRenderer var1, Entity var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      float var9 = ((float)this.field_70546_d + var3) / (float)this.field_70547_e;
      var9 = 1.0F - var9;
      var9 *= var9;
      var9 = 1.0F - var9;
      this.field_70544_f = this.field_70571_a * var9;
      super.func_180434_a(var1, var2, var3, var4, var5, var6, var7, var8);
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
      var3 = var3 * var3 * var3 * var3;
      return var2 * (1.0F - var3) + var3;
   }

   public void func_70071_h_() {
      this.field_70169_q = this.field_70165_t;
      this.field_70167_r = this.field_70163_u;
      this.field_70166_s = this.field_70161_v;
      float var1 = (float)this.field_70546_d / (float)this.field_70547_e;
      float var2 = var1;
      var1 = -var1 + var1 * var1 * 2.0F;
      var1 = 1.0F - var1;
      this.field_70165_t = this.field_70574_aq + this.field_70159_w * (double)var1;
      this.field_70163_u = this.field_70573_ar + this.field_70181_x * (double)var1 + (double)(1.0F - var2);
      this.field_70161_v = this.field_70572_as + this.field_70179_y * (double)var1;
      if (this.field_70546_d++ >= this.field_70547_e) {
         this.func_70106_y();
      }

   }

   public static class Factory implements IParticleFactory {
      public Factory() {
         super();
      }

      public EntityFX func_178902_a(int var1, World var2, double var3, double var5, double var7, double var9, double var11, double var13, int... var15) {
         return new EntityPortalFX(var2, var3, var5, var7, var9, var11, var13);
      }
   }
}
