package net.minecraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;

public class FireflyParticle extends TextureSheetParticle {
   private static final float PARTICLE_FADE_OUT_TIME = 0.5F;
   private static final float PARTICLE_FADE_IN_TIME = 0.3F;
   private static final int PARTICLE_MIN_LIFETIME = 36;
   private static final int PARTICLE_MAX_LIFETIME = 180;

   FireflyParticle(ClientLevel var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      super(var1, var2, var4, var6, var8, var10, var12);
      this.speedUpWhenYMotionIsBlocked = true;
      this.friction = 0.96F;
      this.quadSize *= 0.75F;
      this.yd *= 0.800000011920929;
      this.xd *= 0.800000011920929;
      this.zd *= 0.800000011920929;
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
   }

   public int getLightColor(float var1) {
      float var2 = Mth.clamp(((float)this.age + var1) / (float)this.lifetime, 0.0F, 1.0F);
      if (var2 > 0.5F) {
         float var4 = (1.0F - var2) / 0.5F;
         return (int)(255.0F * var4);
      } else if (var2 < 0.3F) {
         float var3 = var2 / 0.3F;
         return (int)(255.0F * var3);
      } else {
         return 255;
      }
   }

   public void tick() {
      super.tick();
      if (!this.level.getBlockState(BlockPos.containing(this.x, this.y, this.z)).isAir()) {
         this.remove();
      } else {
         if (Math.random() > 0.95 || this.age == 1) {
            this.setParticleSpeed(-0.05000000074505806 + 0.10000000149011612 * Math.random(), -0.05000000074505806 + 0.10000000149011612 * Math.random(), -0.05000000074505806 + 0.10000000149011612 * Math.random());
         }

      }
   }

   public static class FireflyProvider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprite;

      public FireflyProvider(SpriteSet var1) {
         super();
         this.sprite = var1;
      }

      public Particle createParticle(SimpleParticleType var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         FireflyParticle var15 = new FireflyParticle(var2, var3, var5, var7, 0.5 - var2.random.nextDouble(), var2.random.nextBoolean() ? var11 : -var11, 0.5 - var2.random.nextDouble());
         var15.setLifetime(var2.random.nextIntBetweenInclusive(36, 180));
         var15.scale(1.5F);
         var15.pickSprite(this.sprite);
         return var15;
      }

      // $FF: synthetic method
      public Particle createParticle(final ParticleOptions var1, final ClientLevel var2, final double var3, final double var5, final double var7, final double var9, final double var11, final double var13) {
         return this.createParticle((SimpleParticleType)var1, var2, var3, var5, var7, var9, var11, var13);
      }
   }
}
