package net.minecraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

public class FireflyParticle extends SingleQuadParticle {
   private static final float PARTICLE_FADE_OUT_LIGHT_TIME = 0.3F;
   private static final float PARTICLE_FADE_IN_LIGHT_TIME = 0.1F;
   private static final float PARTICLE_FADE_OUT_ALPHA_TIME = 0.5F;
   private static final float PARTICLE_FADE_IN_ALPHA_TIME = 0.3F;
   private static final int PARTICLE_MIN_LIFETIME = 200;
   private static final int PARTICLE_MAX_LIFETIME = 300;

   FireflyParticle(ClientLevel var1, double var2, double var4, double var6, double var8, double var10, double var12, TextureAtlasSprite var14) {
      super(var1, var2, var4, var6, var8, var10, var12, var14);
      this.speedUpWhenYMotionIsBlocked = true;
      this.friction = 0.96F;
      this.quadSize *= 0.75F;
      this.yd *= 0.800000011920929;
      this.xd *= 0.800000011920929;
      this.zd *= 0.800000011920929;
   }

   public SingleQuadParticle.Layer getLayer() {
      return SingleQuadParticle.Layer.TRANSLUCENT;
   }

   public int getLightColor(float var1) {
      return (int)(255.0F * getFadeAmount(this.getLifetimeProgress((float)this.age + var1), 0.1F, 0.3F));
   }

   public void tick() {
      super.tick();
      if (!this.level.getBlockState(BlockPos.containing(this.x, this.y, this.z)).isAir()) {
         this.remove();
      } else {
         this.setAlpha(getFadeAmount(this.getLifetimeProgress((float)this.age), 0.3F, 0.5F));
         if (Math.random() > 0.95 || this.age == 1) {
            this.setParticleSpeed(-0.05000000074505806 + 0.10000000149011612 * Math.random(), -0.05000000074505806 + 0.10000000149011612 * Math.random(), -0.05000000074505806 + 0.10000000149011612 * Math.random());
         }

      }
   }

   private float getLifetimeProgress(float var1) {
      return Mth.clamp(var1 / (float)this.lifetime, 0.0F, 1.0F);
   }

   private static float getFadeAmount(float var0, float var1, float var2) {
      if (var0 >= 1.0F - var1) {
         return (1.0F - var0) / var1;
      } else {
         return var0 <= var2 ? var0 / var2 : 1.0F;
      }
   }

   public static class FireflyProvider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprite;

      public FireflyProvider(SpriteSet var1) {
         super();
         this.sprite = var1;
      }

      public Particle createParticle(SimpleParticleType var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13, RandomSource var15) {
         FireflyParticle var16 = new FireflyParticle(var2, var3, var5, var7, 0.5 - var15.nextDouble(), var15.nextBoolean() ? var11 : -var11, 0.5 - var15.nextDouble(), this.sprite.get(var15));
         var16.setLifetime(var15.nextIntBetweenInclusive(200, 300));
         var16.scale(1.5F);
         var16.setAlpha(0.0F);
         return var16;
      }
   }
}
