package net.minecraft.client.particle;

import java.util.Random;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.world.World;

public class ParticleSpell extends Particle {
   private static final Random field_174848_a = new Random();
   private int field_70590_a = 128;

   protected ParticleSpell(World var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      super(var1, var2, var4, var6, 0.5D - field_174848_a.nextDouble(), var10, 0.5D - field_174848_a.nextDouble());
      this.field_187130_j *= 0.20000000298023224D;
      if (var8 == 0.0D && var12 == 0.0D) {
         this.field_187129_i *= 0.10000000149011612D;
         this.field_187131_k *= 0.10000000149011612D;
      }

      this.field_70544_f *= 0.75F;
      this.field_70547_e = (int)(8.0D / (Math.random() * 0.8D + 0.2D));
      this.field_190017_n = false;
   }

   public boolean func_187111_c() {
      return true;
   }

   public void func_189213_a() {
      this.field_187123_c = this.field_187126_f;
      this.field_187124_d = this.field_187127_g;
      this.field_187125_e = this.field_187128_h;
      if (this.field_70546_d++ >= this.field_70547_e) {
         this.func_187112_i();
      }

      this.func_70536_a(this.field_70590_a + 7 - this.field_70546_d * 8 / this.field_70547_e);
      this.field_187130_j += 0.004D;
      this.func_187110_a(this.field_187129_i, this.field_187130_j, this.field_187131_k);
      if (this.field_187127_g == this.field_187124_d) {
         this.field_187129_i *= 1.1D;
         this.field_187131_k *= 1.1D;
      }

      this.field_187129_i *= 0.9599999785423279D;
      this.field_187130_j *= 0.9599999785423279D;
      this.field_187131_k *= 0.9599999785423279D;
      if (this.field_187132_l) {
         this.field_187129_i *= 0.699999988079071D;
         this.field_187131_k *= 0.699999988079071D;
      }

   }

   public void func_70589_b(int var1) {
      this.field_70590_a = var1;
   }

   public static class InstantFactory implements IParticleFactory<BasicParticleType> {
      public InstantFactory() {
         super();
      }

      public Particle func_199234_a(BasicParticleType var1, World var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         ParticleSpell var15 = new ParticleSpell(var2, var3, var5, var7, var9, var11, var13);
         ((ParticleSpell)var15).func_70589_b(144);
         return var15;
      }
   }

   public static class WitchFactory implements IParticleFactory<BasicParticleType> {
      public WitchFactory() {
         super();
      }

      public Particle func_199234_a(BasicParticleType var1, World var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         ParticleSpell var15 = new ParticleSpell(var2, var3, var5, var7, var9, var11, var13);
         ((ParticleSpell)var15).func_70589_b(144);
         float var16 = var2.field_73012_v.nextFloat() * 0.5F + 0.35F;
         var15.func_70538_b(1.0F * var16, 0.0F * var16, 1.0F * var16);
         return var15;
      }
   }

   public static class AmbientMobFactory implements IParticleFactory<BasicParticleType> {
      public AmbientMobFactory() {
         super();
      }

      public Particle func_199234_a(BasicParticleType var1, World var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         ParticleSpell var15 = new ParticleSpell(var2, var3, var5, var7, var9, var11, var13);
         var15.func_82338_g(0.15F);
         var15.func_70538_b((float)var9, (float)var11, (float)var13);
         return var15;
      }
   }

   public static class MobFactory implements IParticleFactory<BasicParticleType> {
      public MobFactory() {
         super();
      }

      public Particle func_199234_a(BasicParticleType var1, World var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         ParticleSpell var15 = new ParticleSpell(var2, var3, var5, var7, var9, var11, var13);
         var15.func_70538_b((float)var9, (float)var11, (float)var13);
         return var15;
      }
   }

   public static class Factory implements IParticleFactory<BasicParticleType> {
      public Factory() {
         super();
      }

      public Particle func_199234_a(BasicParticleType var1, World var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         return new ParticleSpell(var2, var3, var5, var7, var9, var11, var13);
      }
   }
}
