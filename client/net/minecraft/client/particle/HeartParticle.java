package net.minecraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

public class HeartParticle extends SingleQuadParticle {
   HeartParticle(ClientLevel var1, double var2, double var4, double var6, TextureAtlasSprite var8) {
      super(var1, var2, var4, var6, 0.0, 0.0, 0.0, var8);
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

   public SingleQuadParticle.Layer getLayer() {
      return SingleQuadParticle.Layer.OPAQUE;
   }

   public float getQuadSize(float var1) {
      return this.quadSize * Mth.clamp(((float)this.age + var1) / (float)this.lifetime * 32.0F, 0.0F, 1.0F);
   }

   public static class Provider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprite;

      public Provider(SpriteSet var1) {
         super();
         this.sprite = var1;
      }

      public Particle createParticle(SimpleParticleType var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13, RandomSource var15) {
         HeartParticle var16 = new HeartParticle(var2, var3, var5, var7, this.sprite.get(var15));
         return var16;
      }
   }

   public static class AngryVillagerProvider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprite;

      public AngryVillagerProvider(SpriteSet var1) {
         super();
         this.sprite = var1;
      }

      public Particle createParticle(SimpleParticleType var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13, RandomSource var15) {
         HeartParticle var16 = new HeartParticle(var2, var3, var5 + 0.5, var7, this.sprite.get(var15));
         var16.setColor(1.0F, 1.0F, 1.0F);
         return var16;
      }
   }
}
