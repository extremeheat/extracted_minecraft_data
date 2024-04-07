package net.minecraft.client.particle;

import java.util.Optional;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleGroup;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

public class SuspendedParticle extends TextureSheetParticle {
   SuspendedParticle(ClientLevel var1, SpriteSet var2, double var3, double var5, double var7) {
      super(var1, var3, var5 - 0.125, var7);
      this.setSize(0.01F, 0.01F);
      this.pickSprite(var2);
      this.quadSize = this.quadSize * (this.random.nextFloat() * 0.6F + 0.2F);
      this.lifetime = (int)(16.0 / (Math.random() * 0.8 + 0.2));
      this.hasPhysics = false;
      this.friction = 1.0F;
      this.gravity = 0.0F;
   }

   SuspendedParticle(ClientLevel var1, SpriteSet var2, double var3, double var5, double var7, double var9, double var11, double var13) {
      super(var1, var3, var5 - 0.125, var7, var9, var11, var13);
      this.setSize(0.01F, 0.01F);
      this.pickSprite(var2);
      this.quadSize = this.quadSize * (this.random.nextFloat() * 0.6F + 0.6F);
      this.lifetime = (int)(16.0 / (Math.random() * 0.8 + 0.2));
      this.hasPhysics = false;
      this.friction = 1.0F;
      this.gravity = 0.0F;
   }

   @Override
   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
   }

   public static class CrimsonSporeProvider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprite;

      public CrimsonSporeProvider(SpriteSet var1) {
         super();
         this.sprite = var1;
      }

      public Particle createParticle(SimpleParticleType var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         RandomSource var15 = var2.random;
         double var16 = var15.nextGaussian() * 9.999999974752427E-7;
         double var18 = var15.nextGaussian() * 9.999999747378752E-5;
         double var20 = var15.nextGaussian() * 9.999999974752427E-7;
         SuspendedParticle var22 = new SuspendedParticle(var2, this.sprite, var3, var5, var7, var16, var18, var20);
         var22.setColor(0.9F, 0.4F, 0.5F);
         return var22;
      }
   }

   public static class SporeBlossomAirProvider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprite;

      public SporeBlossomAirProvider(SpriteSet var1) {
         super();
         this.sprite = var1;
      }

      public Particle createParticle(SimpleParticleType var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         SuspendedParticle var15 = new SuspendedParticle(var2, this.sprite, var3, var5, var7, 0.0, -0.800000011920929, 0.0) {
            @Override
            public Optional<ParticleGroup> getParticleGroup() {
               return Optional.of(ParticleGroup.SPORE_BLOSSOM);
            }
         };
         var15.lifetime = Mth.randomBetweenInclusive(var2.random, 500, 1000);
         var15.gravity = 0.01F;
         var15.setColor(0.32F, 0.5F, 0.22F);
         return var15;
      }
   }

   public static class UnderwaterProvider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprite;

      public UnderwaterProvider(SpriteSet var1) {
         super();
         this.sprite = var1;
      }

      public Particle createParticle(SimpleParticleType var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         SuspendedParticle var15 = new SuspendedParticle(var2, this.sprite, var3, var5, var7);
         var15.setColor(0.4F, 0.4F, 0.7F);
         return var15;
      }
   }

   public static class WarpedSporeProvider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprite;

      public WarpedSporeProvider(SpriteSet var1) {
         super();
         this.sprite = var1;
      }

      public Particle createParticle(SimpleParticleType var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         double var15 = (double)var2.random.nextFloat() * -1.9 * (double)var2.random.nextFloat() * 0.1;
         SuspendedParticle var17 = new SuspendedParticle(var2, this.sprite, var3, var5, var7, 0.0, var15, 0.0);
         var17.setColor(0.1F, 0.1F, 0.3F);
         var17.setSize(0.001F, 0.001F);
         return var17;
      }
   }
}
