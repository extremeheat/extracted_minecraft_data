package net.minecraft.client.particle;

import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityFlameFX extends EntityFX {
   private float field_70562_a;

   protected EntityFlameFX(World var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      super(var1, var2, var4, var6, var8, var10, var12);
      this.field_70159_w = this.field_70159_w * 0.009999999776482582D + var8;
      this.field_70181_x = this.field_70181_x * 0.009999999776482582D + var10;
      this.field_70179_y = this.field_70179_y * 0.009999999776482582D + var12;
      this.field_70165_t += (double)((this.field_70146_Z.nextFloat() - this.field_70146_Z.nextFloat()) * 0.05F);
      this.field_70163_u += (double)((this.field_70146_Z.nextFloat() - this.field_70146_Z.nextFloat()) * 0.05F);
      this.field_70161_v += (double)((this.field_70146_Z.nextFloat() - this.field_70146_Z.nextFloat()) * 0.05F);
      this.field_70562_a = this.field_70544_f;
      this.field_70552_h = this.field_70553_i = this.field_70551_j = 1.0F;
      this.field_70547_e = (int)(8.0D / (Math.random() * 0.8D + 0.2D)) + 4;
      this.field_70145_X = true;
      this.func_70536_a(48);
   }

   public void func_180434_a(WorldRenderer var1, Entity var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      float var9 = ((float)this.field_70546_d + var3) / (float)this.field_70547_e;
      this.field_70544_f = this.field_70562_a * (1.0F - var9 * var9 * 0.5F);
      super.func_180434_a(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   public int func_70070_b(float var1) {
      float var2 = ((float)this.field_70546_d + var1) / (float)this.field_70547_e;
      var2 = MathHelper.func_76131_a(var2, 0.0F, 1.0F);
      int var3 = super.func_70070_b(var1);
      int var4 = var3 & 255;
      int var5 = var3 >> 16 & 255;
      var4 += (int)(var2 * 15.0F * 16.0F);
      if (var4 > 240) {
         var4 = 240;
      }

      return var4 | var5 << 16;
   }

   public float func_70013_c(float var1) {
      float var2 = ((float)this.field_70546_d + var1) / (float)this.field_70547_e;
      var2 = MathHelper.func_76131_a(var2, 0.0F, 1.0F);
      float var3 = super.func_70013_c(var1);
      return var3 * var2 + (1.0F - var2);
   }

   public void func_70071_h_() {
      this.field_70169_q = this.field_70165_t;
      this.field_70167_r = this.field_70163_u;
      this.field_70166_s = this.field_70161_v;
      if (this.field_70546_d++ >= this.field_70547_e) {
         this.func_70106_y();
      }

      this.func_70091_d(this.field_70159_w, this.field_70181_x, this.field_70179_y);
      this.field_70159_w *= 0.9599999785423279D;
      this.field_70181_x *= 0.9599999785423279D;
      this.field_70179_y *= 0.9599999785423279D;
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
         return new EntityFlameFX(var2, var3, var5, var7, var9, var11, var13);
      }
   }
}
