package net.minecraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;

public class ExplodeParticle extends SingleQuadParticle {
   private final SpriteSet sprites;

   protected ExplodeParticle(ClientLevel var1, double var2, double var4, double var6, double var8, double var10, double var12, SpriteSet var14) {
      super(var1, var2, var4, var6, var14.first());
      this.gravity = -0.1F;
      this.friction = 0.9F;
      this.sprites = var14;
      this.xd = var8 + (double)((this.random.nextFloat() * 2.0F - 1.0F) * 0.05F);
      this.yd = var10 + (double)((this.random.nextFloat() * 2.0F - 1.0F) * 0.05F);
      this.zd = var12 + (double)((this.random.nextFloat() * 2.0F - 1.0F) * 0.05F);
      float var15 = this.random.nextFloat() * 0.3F + 0.7F;
      this.rCol = var15;
      this.gCol = var15;
      this.bCol = var15;
      this.quadSize = 0.1F * (this.random.nextFloat() * this.random.nextFloat() * 6.0F + 1.0F);
      this.lifetime = (int)(16.0 / ((double)this.random.nextFloat() * 0.8 + 0.2)) + 2;
      this.setSpriteFromAge(var14);
   }

   public SingleQuadParticle.Layer getLayer() {
      return SingleQuadParticle.Layer.OPAQUE;
   }

   public void tick() {
      super.tick();
      this.setSpriteFromAge(this.sprites);
   }

   public static class Provider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprites;

      public Provider(SpriteSet var1) {
         super();
         this.sprites = var1;
      }

      public Particle createParticle(SimpleParticleType var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13, RandomSource var15) {
         return new ExplodeParticle(var2, var3, var5, var7, var9, var11, var13, this.sprites);
      }
   }
}
