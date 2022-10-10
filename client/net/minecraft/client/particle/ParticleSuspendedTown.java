package net.minecraft.client.particle;

import net.minecraft.particles.BasicParticleType;
import net.minecraft.world.World;

public class ParticleSuspendedTown extends Particle {
   protected ParticleSuspendedTown(World var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      super(var1, var2, var4, var6, var8, var10, var12);
      float var14 = this.field_187136_p.nextFloat() * 0.1F + 0.2F;
      this.field_70552_h = var14;
      this.field_70553_i = var14;
      this.field_70551_j = var14;
      this.func_70536_a(0);
      this.func_187115_a(0.02F, 0.02F);
      this.field_70544_f *= this.field_187136_p.nextFloat() * 0.6F + 0.5F;
      this.field_187129_i *= 0.019999999552965164D;
      this.field_187130_j *= 0.019999999552965164D;
      this.field_187131_k *= 0.019999999552965164D;
      this.field_70547_e = (int)(20.0D / (Math.random() * 0.8D + 0.2D));
   }

   public void func_187110_a(double var1, double var3, double var5) {
      this.func_187108_a(this.func_187116_l().func_72317_d(var1, var3, var5));
      this.func_187118_j();
   }

   public void func_189213_a() {
      this.field_187123_c = this.field_187126_f;
      this.field_187124_d = this.field_187127_g;
      this.field_187125_e = this.field_187128_h;
      this.func_187110_a(this.field_187129_i, this.field_187130_j, this.field_187131_k);
      this.field_187129_i *= 0.99D;
      this.field_187130_j *= 0.99D;
      this.field_187131_k *= 0.99D;
      if (this.field_70547_e-- <= 0) {
         this.func_187112_i();
      }

   }

   public static class DolphinSpeedFactory implements IParticleFactory<BasicParticleType> {
      public DolphinSpeedFactory() {
         super();
      }

      public Particle func_199234_a(BasicParticleType var1, World var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         ParticleSuspendedTown var15 = new ParticleSuspendedTown(var2, var3, var5, var7, var9, var11, var13);
         var15.func_70538_b(0.3F, 0.5F, 1.0F);
         var15.func_82338_g(1.0F - var2.field_73012_v.nextFloat() * 0.7F);
         var15.func_187114_a(var15.func_206254_h() / 2);
         return var15;
      }
   }

   public static class HappyVillagerFactory implements IParticleFactory<BasicParticleType> {
      public HappyVillagerFactory() {
         super();
      }

      public Particle func_199234_a(BasicParticleType var1, World var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         ParticleSuspendedTown var15 = new ParticleSuspendedTown(var2, var3, var5, var7, var9, var11, var13);
         var15.func_70536_a(82);
         var15.func_70538_b(1.0F, 1.0F, 1.0F);
         return var15;
      }
   }

   public static class Factory implements IParticleFactory<BasicParticleType> {
      public Factory() {
         super();
      }

      public Particle func_199234_a(BasicParticleType var1, World var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         return new ParticleSuspendedTown(var2, var3, var5, var7, var9, var11, var13);
      }
   }
}
