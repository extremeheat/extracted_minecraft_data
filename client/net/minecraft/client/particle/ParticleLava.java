package net.minecraft.client.particle;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.init.Particles;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.world.World;

public class ParticleLava extends Particle {
   private final float field_70586_a;

   protected ParticleLava(World var1, double var2, double var4, double var6) {
      super(var1, var2, var4, var6, 0.0D, 0.0D, 0.0D);
      this.field_187129_i *= 0.800000011920929D;
      this.field_187130_j *= 0.800000011920929D;
      this.field_187131_k *= 0.800000011920929D;
      this.field_187130_j = (double)(this.field_187136_p.nextFloat() * 0.4F + 0.05F);
      this.field_70552_h = 1.0F;
      this.field_70553_i = 1.0F;
      this.field_70551_j = 1.0F;
      this.field_70544_f *= this.field_187136_p.nextFloat() * 2.0F + 0.2F;
      this.field_70586_a = this.field_70544_f;
      this.field_70547_e = (int)(16.0D / (Math.random() * 0.8D + 0.2D));
      this.func_70536_a(49);
   }

   public int func_189214_a(float var1) {
      int var2 = super.func_189214_a(var1);
      boolean var3 = true;
      int var4 = var2 >> 16 & 255;
      return 240 | var4 << 16;
   }

   public void func_180434_a(BufferBuilder var1, Entity var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      float var9 = ((float)this.field_70546_d + var3) / (float)this.field_70547_e;
      this.field_70544_f = this.field_70586_a * (1.0F - var9 * var9);
      super.func_180434_a(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   public void func_189213_a() {
      this.field_187123_c = this.field_187126_f;
      this.field_187124_d = this.field_187127_g;
      this.field_187125_e = this.field_187128_h;
      if (this.field_70546_d++ >= this.field_70547_e) {
         this.func_187112_i();
      }

      float var1 = (float)this.field_70546_d / (float)this.field_70547_e;
      if (this.field_187136_p.nextFloat() > var1) {
         this.field_187122_b.func_195594_a(Particles.field_197601_L, this.field_187126_f, this.field_187127_g, this.field_187128_h, this.field_187129_i, this.field_187130_j, this.field_187131_k);
      }

      this.field_187130_j -= 0.03D;
      this.func_187110_a(this.field_187129_i, this.field_187130_j, this.field_187131_k);
      this.field_187129_i *= 0.9990000128746033D;
      this.field_187130_j *= 0.9990000128746033D;
      this.field_187131_k *= 0.9990000128746033D;
      if (this.field_187132_l) {
         this.field_187129_i *= 0.699999988079071D;
         this.field_187131_k *= 0.699999988079071D;
      }

   }

   public static class Factory implements IParticleFactory<BasicParticleType> {
      public Factory() {
         super();
      }

      public Particle func_199234_a(BasicParticleType var1, World var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         return new ParticleLava(var2, var3, var5, var7);
      }
   }
}
