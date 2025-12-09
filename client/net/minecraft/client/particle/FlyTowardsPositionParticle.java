package net.minecraft.client.particle;

import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.state.QuadParticleRenderState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;

public class FlyTowardsPositionParticle extends SingleQuadParticle {
   private final double xStart;
   private final double yStart;
   private final double zStart;
   private final boolean isGlowing;
   private final Particle.LifetimeAlpha lifetimeAlpha;

   FlyTowardsPositionParticle(ClientLevel var1, double var2, double var4, double var6, double var8, double var10, double var12, TextureAtlasSprite var14) {
      this(var1, var2, var4, var6, var8, var10, var12, false, Particle.LifetimeAlpha.ALWAYS_OPAQUE, var14);
   }

   FlyTowardsPositionParticle(ClientLevel var1, double var2, double var4, double var6, double var8, double var10, double var12, boolean var14, Particle.LifetimeAlpha var15, TextureAtlasSprite var16) {
      super(var1, var2, var4, var6, var16);
      this.isGlowing = var14;
      this.lifetimeAlpha = var15;
      this.setAlpha(var15.startAlpha());
      this.xd = var8;
      this.yd = var10;
      this.zd = var12;
      this.xStart = var2;
      this.yStart = var4;
      this.zStart = var6;
      this.xo = var2 + var8;
      this.yo = var4 + var10;
      this.zo = var6 + var12;
      this.x = this.xo;
      this.y = this.yo;
      this.z = this.zo;
      this.quadSize = 0.1F * (this.random.nextFloat() * 0.5F + 0.2F);
      float var17 = this.random.nextFloat() * 0.6F + 0.4F;
      this.rCol = 0.9F * var17;
      this.gCol = 0.9F * var17;
      this.bCol = var17;
      this.hasPhysics = false;
      this.lifetime = (int)(this.random.nextFloat() * 10.0F) + 30;
   }

   public SingleQuadParticle.Layer getLayer() {
      return this.lifetimeAlpha.isOpaque() ? SingleQuadParticle.Layer.OPAQUE : SingleQuadParticle.Layer.TRANSLUCENT;
   }

   public void move(double var1, double var3, double var5) {
      this.setBoundingBox(this.getBoundingBox().move(var1, var3, var5));
      this.setLocationFromBoundingbox();
   }

   public int getLightColor(float var1) {
      if (this.isGlowing) {
         return 240;
      } else {
         int var2 = super.getLightColor(var1);
         float var3 = (float)this.age / (float)this.lifetime;
         var3 *= var3;
         var3 *= var3;
         int var4 = var2 & 255;
         int var5 = var2 >> 16 & 255;
         var5 += (int)(var3 * 15.0F * 16.0F);
         if (var5 > 240) {
            var5 = 240;
         }

         return var4 | var5 << 16;
      }
   }

   public void tick() {
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      if (this.age++ >= this.lifetime) {
         this.remove();
      } else {
         float var1 = (float)this.age / (float)this.lifetime;
         var1 = 1.0F - var1;
         float var2 = 1.0F - var1;
         var2 *= var2;
         var2 *= var2;
         this.x = this.xStart + this.xd * (double)var1;
         this.y = this.yStart + this.yd * (double)var1 - (double)(var2 * 1.2F);
         this.z = this.zStart + this.zd * (double)var1;
      }
   }

   public void extract(QuadParticleRenderState var1, Camera var2, float var3) {
      this.setAlpha(this.lifetimeAlpha.currentAlphaForAge(this.age, this.lifetime, var3));
      super.extract(var1, var2, var3);
   }

   public static class EnchantProvider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprite;

      public EnchantProvider(SpriteSet var1) {
         super();
         this.sprite = var1;
      }

      public Particle createParticle(SimpleParticleType var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13, RandomSource var15) {
         FlyTowardsPositionParticle var16 = new FlyTowardsPositionParticle(var2, var3, var5, var7, var9, var11, var13, this.sprite.get(var15));
         return var16;
      }
   }

   public static class NautilusProvider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprite;

      public NautilusProvider(SpriteSet var1) {
         super();
         this.sprite = var1;
      }

      public Particle createParticle(SimpleParticleType var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13, RandomSource var15) {
         FlyTowardsPositionParticle var16 = new FlyTowardsPositionParticle(var2, var3, var5, var7, var9, var11, var13, this.sprite.get(var15));
         return var16;
      }
   }

   public static class VaultConnectionProvider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprite;

      public VaultConnectionProvider(SpriteSet var1) {
         super();
         this.sprite = var1;
      }

      public Particle createParticle(SimpleParticleType var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13, RandomSource var15) {
         FlyTowardsPositionParticle var16 = new FlyTowardsPositionParticle(var2, var3, var5, var7, var9, var11, var13, true, new Particle.LifetimeAlpha(0.0F, 0.6F, 0.25F, 1.0F), this.sprite.get(var15));
         var16.scale(1.5F);
         return var16;
      }
   }
}
