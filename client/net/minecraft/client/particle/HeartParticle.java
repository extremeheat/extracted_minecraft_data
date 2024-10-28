package net.minecraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;

public class HeartParticle extends TextureSheetParticle {
   HeartParticle(ClientLevel var1, double var2, double var4, double var6) {
      super(var1, var2, var4, var6, 0.0, 0.0, 0.0);
      this.speedUpWhenYMotionIsBlocked = true;
      this.friction = 0.86F;
      this.xd *= 0.009999999776482582;
      this.yd *= 0.009999999776482582;
      this.zd *= 0.009999999776482582;
      this.yd += 0.1;
      this.quadSize *= 1.5F;
      this.lifetime = 16;
      this.hasPhysics = false;
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
   }

   public float getQuadSize(float var1) {
      return this.quadSize * Mth.clamp(((float)this.age + var1) / (float)this.lifetime * 32.0F, 0.0F, 1.0F);
   }

   public static class AngryVillagerProvider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprite;

      public AngryVillagerProvider(SpriteSet var1) {
         super();
         this.sprite = var1;
      }

      public Particle createParticle(SimpleParticleType var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         HeartParticle var15 = new HeartParticle(var2, var3, var5 + 0.5, var7);
         var15.pickSprite(this.sprite);
         var15.setColor(1.0F, 1.0F, 1.0F);
         return var15;
      }

      // $FF: synthetic method
      public Particle createParticle(final ParticleOptions var1, final ClientLevel var2, final double var3, final double var5, final double var7, final double var9, final double var11, final double var13) {
         return this.createParticle((SimpleParticleType)var1, var2, var3, var5, var7, var9, var11, var13);
      }
   }

   public static class Provider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprite;

      public Provider(SpriteSet var1) {
         super();
         this.sprite = var1;
      }

      public Particle createParticle(SimpleParticleType var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         HeartParticle var15 = new HeartParticle(var2, var3, var5, var7);
         var15.pickSprite(this.sprite);
         return var15;
      }

      // $FF: synthetic method
      public Particle createParticle(final ParticleOptions var1, final ClientLevel var2, final double var3, final double var5, final double var7, final double var9, final double var11, final double var13) {
         return this.createParticle((SimpleParticleType)var1, var2, var3, var5, var7, var9, var11, var13);
      }
   }
}
