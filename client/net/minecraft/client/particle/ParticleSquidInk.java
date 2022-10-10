package net.minecraft.client.particle;

import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ParticleSquidInk extends ParticleSimpleAnimated {
   protected ParticleSquidInk(World var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      super(var1, var2, var4, var6, 0, 8, 0.0F);
      this.field_70544_f = 5.0F;
      this.func_82338_g(1.0F);
      this.func_70538_b(0.0F, 0.0F, 0.0F);
      this.func_70536_a(0);
      this.field_70547_e = (int)((double)(this.field_70544_f * 12.0F) / (Math.random() * 0.800000011920929D + 0.20000000298023224D));
      this.field_190017_n = false;
      this.field_187129_i = var8;
      this.field_187130_j = var10;
      this.field_187131_k = var12;
      this.func_191238_f(0.0F);
   }

   public void func_189213_a() {
      this.field_187123_c = this.field_187126_f;
      this.field_187124_d = this.field_187127_g;
      this.field_187125_e = this.field_187128_h;
      if (this.field_70546_d++ >= this.field_70547_e) {
         this.func_187112_i();
      }

      if (this.field_70546_d > this.field_70547_e / 2) {
         this.func_82338_g(1.0F - ((float)this.field_70546_d - (float)(this.field_70547_e / 2)) / (float)this.field_70547_e);
      }

      this.func_70536_a(this.field_187147_a + this.field_187148_G - 1 - this.field_70546_d * this.field_187148_G / this.field_70547_e);
      this.func_187110_a(this.field_187129_i, this.field_187130_j, this.field_187131_k);
      if (this.field_187122_b.func_180495_p(new BlockPos(this.field_187126_f, this.field_187127_g, this.field_187128_h)).func_196958_f()) {
         this.field_187130_j -= 0.00800000037997961D;
      }

      this.field_187129_i *= 0.9200000166893005D;
      this.field_187130_j *= 0.9200000166893005D;
      this.field_187131_k *= 0.9200000166893005D;
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
         return new ParticleSquidInk(var2, var3, var5, var7, var9, var11, var13);
      }
   }
}
