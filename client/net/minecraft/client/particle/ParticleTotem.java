package net.minecraft.client.particle;

import net.minecraft.particles.BasicParticleType;
import net.minecraft.world.World;

public class ParticleTotem extends ParticleSimpleAnimated {
   public ParticleTotem(World var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      super(var1, var2, var4, var6, 176, 8, -0.05F);
      this.field_187129_i = var8;
      this.field_187130_j = var10;
      this.field_187131_k = var12;
      this.field_70544_f *= 0.75F;
      this.field_70547_e = 60 + this.field_187136_p.nextInt(12);
      if (this.field_187136_p.nextInt(4) == 0) {
         this.func_70538_b(0.6F + this.field_187136_p.nextFloat() * 0.2F, 0.6F + this.field_187136_p.nextFloat() * 0.3F, this.field_187136_p.nextFloat() * 0.2F);
      } else {
         this.func_70538_b(0.1F + this.field_187136_p.nextFloat() * 0.2F, 0.4F + this.field_187136_p.nextFloat() * 0.3F, this.field_187136_p.nextFloat() * 0.2F);
      }

      this.func_191238_f(0.6F);
   }

   public static class Factory implements IParticleFactory<BasicParticleType> {
      public Factory() {
         super();
      }

      public Particle func_199234_a(BasicParticleType var1, World var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         return new ParticleTotem(var2, var3, var5, var7, var9, var11, var13);
      }
   }
}
