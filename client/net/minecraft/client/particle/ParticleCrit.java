package net.minecraft.client.particle;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class ParticleCrit extends Particle {
   private final float field_174839_a;

   protected ParticleCrit(World var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      this(var1, var2, var4, var6, var8, var10, var12, 1.0F);
   }

   protected ParticleCrit(World var1, double var2, double var4, double var6, double var8, double var10, double var12, float var14) {
      super(var1, var2, var4, var6, 0.0D, 0.0D, 0.0D);
      this.field_187129_i *= 0.10000000149011612D;
      this.field_187130_j *= 0.10000000149011612D;
      this.field_187131_k *= 0.10000000149011612D;
      this.field_187129_i += var8 * 0.4D;
      this.field_187130_j += var10 * 0.4D;
      this.field_187131_k += var12 * 0.4D;
      float var15 = (float)(Math.random() * 0.30000001192092896D + 0.6000000238418579D);
      this.field_70552_h = var15;
      this.field_70553_i = var15;
      this.field_70551_j = var15;
      this.field_70544_f *= 0.75F;
      this.field_70544_f *= var14;
      this.field_174839_a = this.field_70544_f;
      this.field_70547_e = (int)(6.0D / (Math.random() * 0.8D + 0.6D));
      this.field_70547_e = (int)((float)this.field_70547_e * var14);
      this.field_70547_e = Math.max(this.field_70547_e, 1);
      this.field_190017_n = false;
      this.func_70536_a(65);
      this.func_189213_a();
   }

   public void func_180434_a(BufferBuilder var1, Entity var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      float var9 = ((float)this.field_70546_d + var3) / (float)this.field_70547_e * 32.0F;
      var9 = MathHelper.func_76131_a(var9, 0.0F, 1.0F);
      this.field_70544_f = this.field_174839_a * var9;
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
      this.field_70553_i = (float)((double)this.field_70553_i * 0.96D);
      this.field_70551_j = (float)((double)this.field_70551_j * 0.9D);
      this.field_187129_i *= 0.699999988079071D;
      this.field_187130_j *= 0.699999988079071D;
      this.field_187131_k *= 0.699999988079071D;
      this.field_187130_j -= 0.019999999552965164D;
      if (this.field_187132_l) {
         this.field_187129_i *= 0.699999988079071D;
         this.field_187131_k *= 0.699999988079071D;
      }

   }

   public static class DamageIndicatorFactory implements IParticleFactory<BasicParticleType> {
      public DamageIndicatorFactory() {
         super();
      }

      public Particle func_199234_a(BasicParticleType var1, World var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         ParticleCrit var15 = new ParticleCrit(var2, var3, var5, var7, var9, var11 + 1.0D, var13, 1.0F);
         var15.func_187114_a(20);
         var15.func_70536_a(67);
         return var15;
      }
   }

   public static class MagicFactory implements IParticleFactory<BasicParticleType> {
      public MagicFactory() {
         super();
      }

      public Particle func_199234_a(BasicParticleType var1, World var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         ParticleCrit var15 = new ParticleCrit(var2, var3, var5, var7, var9, var11, var13);
         var15.func_70538_b(var15.func_70534_d() * 0.3F, var15.func_70542_f() * 0.8F, var15.func_70535_g());
         var15.func_94053_h();
         return var15;
      }
   }

   public static class Factory implements IParticleFactory<BasicParticleType> {
      public Factory() {
         super();
      }

      public Particle func_199234_a(BasicParticleType var1, World var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         return new ParticleCrit(var2, var3, var5, var7, var9, var11, var13);
      }
   }
}
