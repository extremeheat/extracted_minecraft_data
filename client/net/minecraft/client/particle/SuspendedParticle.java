package net.minecraft.client.particle;

import java.util.Optional;
import java.util.Random;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleGroup;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;

public class SuspendedParticle extends TextureSheetParticle {
   SuspendedParticle(ClientLevel var1, SpriteSet var2, double var3, double var5, double var7) {
      super(var1, var3, var5 - 0.125D, var7);
      this.setSize(0.01F, 0.01F);
      this.pickSprite(var2);
      this.quadSize *= this.random.nextFloat() * 0.6F + 0.2F;
      this.lifetime = (int)(16.0D / (Math.random() * 0.8D + 0.2D));
      this.hasPhysics = false;
      this.friction = 1.0F;
      this.gravity = 0.0F;
   }

   SuspendedParticle(ClientLevel var1, SpriteSet var2, double var3, double var5, double var7, double var9, double var11, double var13) {
      super(var1, var3, var5 - 0.125D, var7, var9, var11, var13);
      this.setSize(0.01F, 0.01F);
      this.pickSprite(var2);
      this.quadSize *= this.random.nextFloat() * 0.6F + 0.6F;
      this.lifetime = (int)(16.0D / (Math.random() * 0.8D + 0.2D));
      this.hasPhysics = false;
      this.friction = 1.0F;
      this.gravity = 0.0F;
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
   }

   public static class WarpedSporeProvider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprite;

      public WarpedSporeProvider(SpriteSet var1) {
         super();
         this.sprite = var1;
      }

      public Particle createParticle(SimpleParticleType var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         double var15 = (double)var2.random.nextFloat() * -1.9D * (double)var2.random.nextFloat() * 0.1D;
         SuspendedParticle var17 = new SuspendedParticle(var2, this.sprite, var3, var5, var7, 0.0D, var15, 0.0D);
         var17.setColor(0.1F, 0.1F, 0.3F);
         var17.setSize(0.001F, 0.001F);
         return var17;
      }
   }

   public static class CrimsonSporeProvider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprite;

      public CrimsonSporeProvider(SpriteSet var1) {
         super();
         this.sprite = var1;
      }

      public Particle createParticle(SimpleParticleType var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         Random var15 = var2.random;
         double var16 = var15.nextGaussian() * 9.999999974752427E-7D;
         double var18 = var15.nextGaussian() * 9.999999747378752E-5D;
         double var20 = var15.nextGaussian() * 9.999999974752427E-7D;
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
         SuspendedParticle var15 = new SuspendedParticle(var2, this.sprite, var3, var5, var7, 0.0D, -0.800000011920929D, 0.0D) {
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
}
