package net.minecraft.client.particle;

import javax.annotation.Nullable;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ParticleBubblePop extends Particle {
   protected ParticleBubblePop(World var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      super(var1, var2, var4, var6, 0.0D, 0.0D, 0.0D);
      this.field_70552_h = 1.0F;
      this.field_70553_i = 1.0F;
      this.field_70551_j = 1.0F;
      this.func_70536_a(256);
      this.field_70547_e = 4;
      this.field_70545_g = 0.008F;
      this.field_187129_i = var8;
      this.field_187130_j = var10;
      this.field_187131_k = var12;
   }

   public void func_189213_a() {
      this.field_187123_c = this.field_187126_f;
      this.field_187124_d = this.field_187127_g;
      this.field_187125_e = this.field_187128_h;
      this.field_187130_j -= (double)this.field_70545_g;
      this.func_187110_a(this.field_187129_i, this.field_187130_j, this.field_187131_k);
      if (this.field_70546_d++ >= this.field_70547_e) {
         this.func_187112_i();
      } else {
         int var1 = this.field_70546_d * 5 / this.field_70547_e;
         if (var1 <= 4) {
            this.func_70536_a(256 + var1);
         }
      }
   }

   public void func_180434_a(BufferBuilder var1, Entity var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      float var9 = (float)this.field_94054_b / 32.0F;
      float var10 = var9 + 0.0624375F;
      float var11 = (float)this.field_94055_c / 32.0F;
      float var12 = var11 + 0.0624375F;
      float var13 = 0.1F * this.field_70544_f;
      if (this.field_187119_C != null) {
         var9 = this.field_187119_C.func_94209_e();
         var10 = this.field_187119_C.func_94212_f();
         var11 = this.field_187119_C.func_94206_g();
         var12 = this.field_187119_C.func_94210_h();
      }

      float var14 = (float)(this.field_187123_c + (this.field_187126_f - this.field_187123_c) * (double)var3 - field_70556_an);
      float var15 = (float)(this.field_187124_d + (this.field_187127_g - this.field_187124_d) * (double)var3 - field_70554_ao);
      float var16 = (float)(this.field_187125_e + (this.field_187128_h - this.field_187125_e) * (double)var3 - field_70555_ap);
      int var17 = this.func_189214_a(var3);
      int var18 = var17 >> 16 & '\uffff';
      int var19 = var17 & '\uffff';
      Vec3d[] var20 = new Vec3d[]{new Vec3d((double)(-var4 * var13 - var7 * var13), (double)(-var5 * var13), (double)(-var6 * var13 - var8 * var13)), new Vec3d((double)(-var4 * var13 + var7 * var13), (double)(var5 * var13), (double)(-var6 * var13 + var8 * var13)), new Vec3d((double)(var4 * var13 + var7 * var13), (double)(var5 * var13), (double)(var6 * var13 + var8 * var13)), new Vec3d((double)(var4 * var13 - var7 * var13), (double)(-var5 * var13), (double)(var6 * var13 - var8 * var13))};
      var1.func_181662_b((double)var14 + var20[0].field_72450_a, (double)var15 + var20[0].field_72448_b, (double)var16 + var20[0].field_72449_c).func_187315_a((double)var10, (double)var12).func_181666_a(this.field_70552_h, this.field_70553_i, this.field_70551_j, this.field_82339_as).func_187314_a(var18, var19).func_181675_d();
      var1.func_181662_b((double)var14 + var20[1].field_72450_a, (double)var15 + var20[1].field_72448_b, (double)var16 + var20[1].field_72449_c).func_187315_a((double)var10, (double)var11).func_181666_a(this.field_70552_h, this.field_70553_i, this.field_70551_j, this.field_82339_as).func_187314_a(var18, var19).func_181675_d();
      var1.func_181662_b((double)var14 + var20[2].field_72450_a, (double)var15 + var20[2].field_72448_b, (double)var16 + var20[2].field_72449_c).func_187315_a((double)var9, (double)var11).func_181666_a(this.field_70552_h, this.field_70553_i, this.field_70551_j, this.field_82339_as).func_187314_a(var18, var19).func_181675_d();
      var1.func_181662_b((double)var14 + var20[3].field_72450_a, (double)var15 + var20[3].field_72448_b, (double)var16 + var20[3].field_72449_c).func_187315_a((double)var9, (double)var12).func_181666_a(this.field_70552_h, this.field_70553_i, this.field_70551_j, this.field_82339_as).func_187314_a(var18, var19).func_181675_d();
   }

   public void func_70536_a(int var1) {
      if (this.func_70537_b() != 0) {
         throw new RuntimeException("Invalid call to Particle.setMiscTex");
      } else {
         this.field_94054_b = 2 * var1 % 16;
         this.field_94055_c = var1 / 16;
      }
   }

   public static class Factory implements IParticleFactory<BasicParticleType> {
      public Factory() {
         super();
      }

      @Nullable
      public Particle func_199234_a(BasicParticleType var1, World var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         return new ParticleBubblePop(var2, var3, var5, var7, var9, var11, var13);
      }
   }
}
