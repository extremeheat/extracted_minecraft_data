package net.minecraft.client.particle;

import net.minecraft.particles.BasicParticleType;
import net.minecraft.world.World;

public class ParticleWaterWake extends Particle {
   protected ParticleWaterWake(World var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      super(var1, var2, var4, var6, 0.0D, 0.0D, 0.0D);
      this.field_187129_i *= 0.30000001192092896D;
      this.field_187130_j = Math.random() * 0.20000000298023224D + 0.10000000149011612D;
      this.field_187131_k *= 0.30000001192092896D;
      this.field_70552_h = 1.0F;
      this.field_70553_i = 1.0F;
      this.field_70551_j = 1.0F;
      this.func_70536_a(19);
      this.func_187115_a(0.01F, 0.01F);
      this.field_70547_e = (int)(8.0D / (Math.random() * 0.8D + 0.2D));
      this.field_70545_g = 0.0F;
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
      this.field_187129_i *= 0.9800000190734863D;
      this.field_187130_j *= 0.9800000190734863D;
      this.field_187131_k *= 0.9800000190734863D;
      int var1 = 60 - this.field_70547_e;
      float var2 = (float)var1 * 0.001F;
      this.func_187115_a(var2, var2);
      this.func_70536_a(19 + var1 % 4);
      if (this.field_70547_e-- <= 0) {
         this.func_187112_i();
      }

   }

   public static class Factory implements IParticleFactory<BasicParticleType> {
      public Factory() {
         super();
      }

      public Particle func_199234_a(BasicParticleType var1, World var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         return new ParticleWaterWake(var2, var3, var5, var7, var9, var11, var13);
      }
   }
}
