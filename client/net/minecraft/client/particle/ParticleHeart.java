package net.minecraft.client.particle;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class ParticleHeart extends Particle {
   private final float field_70575_a;

   protected ParticleHeart(World var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      this(var1, var2, var4, var6, var8, var10, var12, 2.0F);
   }

   protected ParticleHeart(World var1, double var2, double var4, double var6, double var8, double var10, double var12, float var14) {
      super(var1, var2, var4, var6, 0.0D, 0.0D, 0.0D);
      this.field_187129_i *= 0.009999999776482582D;
      this.field_187130_j *= 0.009999999776482582D;
      this.field_187131_k *= 0.009999999776482582D;
      this.field_187130_j += 0.1D;
      this.field_70544_f *= 0.75F;
      this.field_70544_f *= var14;
      this.field_70575_a = this.field_70544_f;
      this.field_70547_e = 16;
      this.func_70536_a(80);
      this.field_190017_n = false;
   }

   public void func_180434_a(BufferBuilder var1, Entity var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      float var9 = ((float)this.field_70546_d + var3) / (float)this.field_70547_e * 32.0F;
      var9 = MathHelper.func_76131_a(var9, 0.0F, 1.0F);
      this.field_70544_f = this.field_70575_a * var9;
      super.func_180434_a(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   public void func_189213_a() {
      this.field_187123_c = this.field_187126_f;
      this.field_187124_d = this.field_187127_g;
      this.field_187125_e = this.field_187128_h;
      if (this.field_70546_d++ >= this.field_70547_e) {
         this.func_187112_i();
      }

      this.func_187110_a(this.field_187129_i, this.field_187130_j, this.field_187131_k);
      if (this.field_187127_g == this.field_187124_d) {
         this.field_187129_i *= 1.1D;
         this.field_187131_k *= 1.1D;
      }

      this.field_187129_i *= 0.8600000143051147D;
      this.field_187130_j *= 0.8600000143051147D;
      this.field_187131_k *= 0.8600000143051147D;
      if (this.field_187132_l) {
         this.field_187129_i *= 0.699999988079071D;
         this.field_187131_k *= 0.699999988079071D;
      }

   }

   public static class AngryVillagerFactory implements IParticleFactory<BasicParticleType> {
      public AngryVillagerFactory() {
         super();
      }

      public Particle func_199234_a(BasicParticleType var1, World var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         ParticleHeart var15 = new ParticleHeart(var2, var3, var5 + 0.5D, var7, var9, var11, var13);
         var15.func_70536_a(81);
         var15.func_70538_b(1.0F, 1.0F, 1.0F);
         return var15;
      }
   }

   public static class Factory implements IParticleFactory<BasicParticleType> {
      public Factory() {
         super();
      }

      public Particle func_199234_a(BasicParticleType var1, World var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         return new ParticleHeart(var2, var3, var5, var7, var9, var11, var13);
      }
   }
}
