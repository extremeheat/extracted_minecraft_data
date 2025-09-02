package net.minecraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;

public class CampfireSmokeParticle extends SingleQuadParticle {
   CampfireSmokeParticle(ClientLevel var1, double var2, double var4, double var6, double var8, double var10, double var12, boolean var14, TextureAtlasSprite var15) {
      super(var1, var2, var4, var6, var15);
      this.scale(3.0F);
      this.setSize(0.25F, 0.25F);
      if (var14) {
         this.lifetime = this.random.nextInt(50) + 280;
      } else {
         this.lifetime = this.random.nextInt(50) + 80;
      }

      this.gravity = 3.0E-6F;
      this.xd = var8;
      this.yd = var10 + (double)(this.random.nextFloat() / 500.0F);
      this.zd = var12;
   }

   public void tick() {
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      if (this.age++ < this.lifetime && !(this.alpha <= 0.0F)) {
         this.xd += (double)(this.random.nextFloat() / 5000.0F * (float)(this.random.nextBoolean() ? 1 : -1));
         this.zd += (double)(this.random.nextFloat() / 5000.0F * (float)(this.random.nextBoolean() ? 1 : -1));
         this.yd -= (double)this.gravity;
         this.move(this.xd, this.yd, this.zd);
         if (this.age >= this.lifetime - 60 && this.alpha > 0.01F) {
            this.alpha -= 0.015F;
         }

      } else {
         this.remove();
      }
   }

   public SingleQuadParticle.Layer getLayer() {
      return SingleQuadParticle.Layer.TRANSLUCENT;
   }

   public static class CosyProvider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprites;

      public CosyProvider(SpriteSet var1) {
         super();
         this.sprites = var1;
      }

      public Particle createParticle(SimpleParticleType var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13, RandomSource var15) {
         CampfireSmokeParticle var16 = new CampfireSmokeParticle(var2, var3, var5, var7, var9, var11, var13, false, this.sprites.get(var15));
         var16.setAlpha(0.9F);
         return var16;
      }
   }

   public static class SignalProvider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprites;

      public SignalProvider(SpriteSet var1) {
         super();
         this.sprites = var1;
      }

      public Particle createParticle(SimpleParticleType var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13, RandomSource var15) {
         CampfireSmokeParticle var16 = new CampfireSmokeParticle(var2, var3, var5, var7, var9, var11, var13, true, this.sprites.get(var15));
         var16.setAlpha(0.95F);
         return var16;
      }
   }
}
