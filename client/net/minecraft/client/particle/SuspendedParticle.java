package net.minecraft.client.particle;

import java.util.Optional;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.ParticleLimit;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

public class SuspendedParticle extends SingleQuadParticle {
   SuspendedParticle(ClientLevel var1, double var2, double var4, double var6, TextureAtlasSprite var8) {
      super(var1, var2, var4 - 0.125, var6, var8);
      this.setSize(0.01F, 0.01F);
      this.quadSize *= this.random.nextFloat() * 0.6F + 0.2F;
      this.lifetime = (int)(16.0 / ((double)this.random.nextFloat() * 0.8 + 0.2));
      this.hasPhysics = false;
      this.friction = 1.0F;
      this.gravity = 0.0F;
   }

   SuspendedParticle(ClientLevel var1, double var2, double var4, double var6, double var8, double var10, double var12, TextureAtlasSprite var14) {
      super(var1, var2, var4 - 0.125, var6, var8, var10, var12, var14);
      this.setSize(0.01F, 0.01F);
      this.quadSize *= this.random.nextFloat() * 0.6F + 0.6F;
      this.lifetime = (int)(16.0 / ((double)this.random.nextFloat() * 0.8 + 0.2));
      this.hasPhysics = false;
      this.friction = 1.0F;
      this.gravity = 0.0F;
   }

   public SingleQuadParticle.Layer getLayer() {
      return SingleQuadParticle.Layer.OPAQUE;
   }

   public static class UnderwaterProvider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprite;

      public UnderwaterProvider(SpriteSet var1) {
         super();
         this.sprite = var1;
      }

      public Particle createParticle(SimpleParticleType var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13, RandomSource var15) {
         SuspendedParticle var16 = new SuspendedParticle(var2, var3, var5, var7, this.sprite.get(var15));
         var16.setColor(0.4F, 0.4F, 0.7F);
         return var16;
      }
   }

   public static class SporeBlossomAirProvider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprite;

      public SporeBlossomAirProvider(SpriteSet var1) {
         super();
         this.sprite = var1;
      }

      public Particle createParticle(SimpleParticleType var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13, RandomSource var15) {
         SuspendedParticle var16 = new SuspendedParticle(var2, var3, var5, var7, 0.0, -0.800000011920929, 0.0, this.sprite.get(var15)) {
            public Optional<ParticleLimit> getParticleLimit() {
               return Optional.of(ParticleLimit.SPORE_BLOSSOM);
            }
         };
         var16.lifetime = Mth.randomBetweenInclusive(var15, 500, 1000);
         var16.gravity = 0.01F;
         var16.setColor(0.32F, 0.5F, 0.22F);
         return var16;
      }
   }

   public static class CrimsonSporeProvider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprite;

      public CrimsonSporeProvider(SpriteSet var1) {
         super();
         this.sprite = var1;
      }

      public Particle createParticle(SimpleParticleType var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13, RandomSource var15) {
         double var16 = var15.nextGaussian() * 9.999999974752427E-7;
         double var18 = var15.nextGaussian() * 9.999999747378752E-5;
         double var20 = var15.nextGaussian() * 9.999999974752427E-7;
         SuspendedParticle var22 = new SuspendedParticle(var2, var3, var5, var7, var16, var18, var20, this.sprite.get(var15));
         var22.setColor(0.9F, 0.4F, 0.5F);
         return var22;
      }
   }

   public static class WarpedSporeProvider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprite;

      public WarpedSporeProvider(SpriteSet var1) {
         super();
         this.sprite = var1;
      }

      public Particle createParticle(SimpleParticleType var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13, RandomSource var15) {
         double var16 = (double)var15.nextFloat() * -1.9 * (double)var15.nextFloat() * 0.1;
         SuspendedParticle var18 = new SuspendedParticle(var2, var3, var5, var7, 0.0, var16, 0.0, this.sprite.get(var15));
         var18.setColor(0.1F, 0.1F, 0.3F);
         var18.setSize(0.001F, 0.001F);
         return var18;
      }
   }
}
