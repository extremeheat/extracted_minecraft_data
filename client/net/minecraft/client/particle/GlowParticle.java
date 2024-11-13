package net.minecraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

public class GlowParticle extends TextureSheetParticle {
   static final RandomSource RANDOM = RandomSource.create();
   private final SpriteSet sprites;

   GlowParticle(ClientLevel var1, double var2, double var4, double var6, double var8, double var10, double var12, SpriteSet var14) {
      super(var1, var2, var4, var6, var8, var10, var12);
      this.friction = 0.96F;
      this.speedUpWhenYMotionIsBlocked = true;
      this.sprites = var14;
      this.quadSize *= 0.75F;
      this.hasPhysics = false;
      this.setSpriteFromAge(var14);
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
   }

   public int getLightColor(float var1) {
      float var2 = ((float)this.age + var1) / (float)this.lifetime;
      var2 = Mth.clamp(var2, 0.0F, 1.0F);
      int var3 = super.getLightColor(var1);
      int var4 = var3 & 255;
      int var5 = var3 >> 16 & 255;
      var4 += (int)(var2 * 15.0F * 16.0F);
      if (var4 > 240) {
         var4 = 240;
      }

      return var4 | var5 << 16;
   }

   public void tick() {
      super.tick();
      this.setSpriteFromAge(this.sprites);
   }

   public static class GlowSquidProvider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprite;

      public GlowSquidProvider(SpriteSet var1) {
         super();
         this.sprite = var1;
      }

      public Particle createParticle(SimpleParticleType var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         GlowParticle var15 = new GlowParticle(var2, var3, var5, var7, 0.5 - GlowParticle.RANDOM.nextDouble(), var11, 0.5 - GlowParticle.RANDOM.nextDouble(), this.sprite);
         if (var2.random.nextBoolean()) {
            var15.setColor(0.6F, 1.0F, 0.8F);
         } else {
            var15.setColor(0.08F, 0.4F, 0.4F);
         }

         var15.yd *= 0.20000000298023224;
         if (var9 == 0.0 && var13 == 0.0) {
            var15.xd *= 0.10000000149011612;
            var15.zd *= 0.10000000149011612;
         }

         var15.setLifetime((int)(8.0 / (var2.random.nextDouble() * 0.8 + 0.2)));
         return var15;
      }

      // $FF: synthetic method
      public Particle createParticle(final ParticleOptions var1, final ClientLevel var2, final double var3, final double var5, final double var7, final double var9, final double var11, final double var13) {
         return this.createParticle((SimpleParticleType)var1, var2, var3, var5, var7, var9, var11, var13);
      }
   }

   public static class WaxOnProvider implements ParticleProvider<SimpleParticleType> {
      private final double SPEED_FACTOR = 0.01;
      private final SpriteSet sprite;

      public WaxOnProvider(SpriteSet var1) {
         super();
         this.sprite = var1;
      }

      public Particle createParticle(SimpleParticleType var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         GlowParticle var15 = new GlowParticle(var2, var3, var5, var7, 0.0, 0.0, 0.0, this.sprite);
         var15.setColor(0.91F, 0.55F, 0.08F);
         var15.setParticleSpeed(var9 * 0.01 / 2.0, var11 * 0.01, var13 * 0.01 / 2.0);
         boolean var16 = true;
         boolean var17 = true;
         var15.setLifetime(var2.random.nextInt(30) + 10);
         return var15;
      }

      // $FF: synthetic method
      public Particle createParticle(final ParticleOptions var1, final ClientLevel var2, final double var3, final double var5, final double var7, final double var9, final double var11, final double var13) {
         return this.createParticle((SimpleParticleType)var1, var2, var3, var5, var7, var9, var11, var13);
      }
   }

   public static class WaxOffProvider implements ParticleProvider<SimpleParticleType> {
      private final double SPEED_FACTOR = 0.01;
      private final SpriteSet sprite;

      public WaxOffProvider(SpriteSet var1) {
         super();
         this.sprite = var1;
      }

      public Particle createParticle(SimpleParticleType var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         GlowParticle var15 = new GlowParticle(var2, var3, var5, var7, 0.0, 0.0, 0.0, this.sprite);
         var15.setColor(1.0F, 0.9F, 1.0F);
         var15.setParticleSpeed(var9 * 0.01 / 2.0, var11 * 0.01, var13 * 0.01 / 2.0);
         boolean var16 = true;
         boolean var17 = true;
         var15.setLifetime(var2.random.nextInt(30) + 10);
         return var15;
      }

      // $FF: synthetic method
      public Particle createParticle(final ParticleOptions var1, final ClientLevel var2, final double var3, final double var5, final double var7, final double var9, final double var11, final double var13) {
         return this.createParticle((SimpleParticleType)var1, var2, var3, var5, var7, var9, var11, var13);
      }
   }

   public static class ElectricSparkProvider implements ParticleProvider<SimpleParticleType> {
      private final double SPEED_FACTOR = 0.25;
      private final SpriteSet sprite;

      public ElectricSparkProvider(SpriteSet var1) {
         super();
         this.sprite = var1;
      }

      public Particle createParticle(SimpleParticleType var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         GlowParticle var15 = new GlowParticle(var2, var3, var5, var7, 0.0, 0.0, 0.0, this.sprite);
         var15.setColor(1.0F, 0.9F, 1.0F);
         var15.setParticleSpeed(var9 * 0.25, var11 * 0.25, var13 * 0.25);
         boolean var16 = true;
         boolean var17 = true;
         var15.setLifetime(var2.random.nextInt(2) + 2);
         return var15;
      }

      // $FF: synthetic method
      public Particle createParticle(final ParticleOptions var1, final ClientLevel var2, final double var3, final double var5, final double var7, final double var9, final double var11, final double var13) {
         return this.createParticle((SimpleParticleType)var1, var2, var3, var5, var7, var9, var11, var13);
      }
   }

   public static class ScrapeProvider implements ParticleProvider<SimpleParticleType> {
      private final double SPEED_FACTOR = 0.01;
      private final SpriteSet sprite;

      public ScrapeProvider(SpriteSet var1) {
         super();
         this.sprite = var1;
      }

      public Particle createParticle(SimpleParticleType var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         GlowParticle var15 = new GlowParticle(var2, var3, var5, var7, 0.0, 0.0, 0.0, this.sprite);
         if (var2.random.nextBoolean()) {
            var15.setColor(0.29F, 0.58F, 0.51F);
         } else {
            var15.setColor(0.43F, 0.77F, 0.62F);
         }

         var15.setParticleSpeed(var9 * 0.01, var11 * 0.01, var13 * 0.01);
         boolean var16 = true;
         boolean var17 = true;
         var15.setLifetime(var2.random.nextInt(30) + 10);
         return var15;
      }

      // $FF: synthetic method
      public Particle createParticle(final ParticleOptions var1, final ClientLevel var2, final double var3, final double var5, final double var7, final double var9, final double var11, final double var13) {
         return this.createParticle((SimpleParticleType)var1, var2, var3, var5, var7, var9, var11, var13);
      }
   }
}
