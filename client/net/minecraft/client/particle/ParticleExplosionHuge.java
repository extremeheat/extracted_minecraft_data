package net.minecraft.client.particle;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.init.Particles;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.world.World;

public class ParticleExplosionHuge extends Particle {
   private int field_70579_a;
   private final int field_70580_aq = 8;

   protected ParticleExplosionHuge(World var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      super(var1, var2, var4, var6, 0.0D, 0.0D, 0.0D);
   }

   public void func_180434_a(BufferBuilder var1, Entity var2, float var3, float var4, float var5, float var6, float var7, float var8) {
   }

   public void func_189213_a() {
      for(int var1 = 0; var1 < 6; ++var1) {
         double var2 = this.field_187126_f + (this.field_187136_p.nextDouble() - this.field_187136_p.nextDouble()) * 4.0D;
         double var4 = this.field_187127_g + (this.field_187136_p.nextDouble() - this.field_187136_p.nextDouble()) * 4.0D;
         double var6 = this.field_187128_h + (this.field_187136_p.nextDouble() - this.field_187136_p.nextDouble()) * 4.0D;
         this.field_187122_b.func_195594_a(Particles.field_197627_t, var2, var4, var6, (double)((float)this.field_70579_a / (float)this.field_70580_aq), 0.0D, 0.0D);
      }

      ++this.field_70579_a;
      if (this.field_70579_a == this.field_70580_aq) {
         this.func_187112_i();
      }

   }

   public int func_70537_b() {
      return 1;
   }

   public static class Factory implements IParticleFactory<BasicParticleType> {
      public Factory() {
         super();
      }

      public Particle func_199234_a(BasicParticleType var1, World var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         return new ParticleExplosionHuge(var2, var3, var5, var7, var9, var11, var13);
      }
   }
}
