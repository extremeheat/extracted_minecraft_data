package net.minecraft.client.particle;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class ParticleDragonBreath extends Particle {
   private final float field_187143_a;
   private boolean field_187144_G;

   protected ParticleDragonBreath(World var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      super(var1, var2, var4, var6, var8, var10, var12);
      this.field_187129_i = var8;
      this.field_187130_j = var10;
      this.field_187131_k = var12;
      this.field_70552_h = MathHelper.func_151240_a(this.field_187136_p, 0.7176471F, 0.8745098F);
      this.field_70553_i = MathHelper.func_151240_a(this.field_187136_p, 0.0F, 0.0F);
      this.field_70551_j = MathHelper.func_151240_a(this.field_187136_p, 0.8235294F, 0.9764706F);
      this.field_70544_f *= 0.75F;
      this.field_187143_a = this.field_70544_f;
      this.field_70547_e = (int)(20.0D / ((double)this.field_187136_p.nextFloat() * 0.8D + 0.2D));
      this.field_187144_G = false;
      this.field_190017_n = false;
   }

   public void func_189213_a() {
      this.field_187123_c = this.field_187126_f;
      this.field_187124_d = this.field_187127_g;
      this.field_187125_e = this.field_187128_h;
      if (this.field_70546_d++ >= this.field_70547_e) {
         this.func_187112_i();
      } else {
         this.func_70536_a(3 * this.field_70546_d / this.field_70547_e + 5);
         if (this.field_187132_l) {
            this.field_187130_j = 0.0D;
            this.field_187144_G = true;
         }

         if (this.field_187144_G) {
            this.field_187130_j += 0.002D;
         }

         this.func_187110_a(this.field_187129_i, this.field_187130_j, this.field_187131_k);
         if (this.field_187127_g == this.field_187124_d) {
            this.field_187129_i *= 1.1D;
            this.field_187131_k *= 1.1D;
         }

         this.field_187129_i *= 0.9599999785423279D;
         this.field_187131_k *= 0.9599999785423279D;
         if (this.field_187144_G) {
            this.field_187130_j *= 0.9599999785423279D;
         }

      }
   }

   public void func_180434_a(BufferBuilder var1, Entity var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      this.field_70544_f = this.field_187143_a * MathHelper.func_76131_a(((float)this.field_70546_d + var3) / (float)this.field_70547_e * 32.0F, 0.0F, 1.0F);
      super.func_180434_a(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   public static class Factory implements IParticleFactory<BasicParticleType> {
      public Factory() {
         super();
      }

      public Particle func_199234_a(BasicParticleType var1, World var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         return new ParticleDragonBreath(var2, var3, var5, var7, var9, var11, var13);
      }
   }
}
