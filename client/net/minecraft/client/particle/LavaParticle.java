package net.minecraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.RandomSource;

public class LavaParticle extends SingleQuadParticle {
   private LavaParticle(final ClientLevel level, final double x, final double y, final double z, final TextureAtlasSprite sprite) {
      super(level, x, y, z, 0.0, 0.0, 0.0, sprite);
      this.gravity = 0.75F;
      this.friction = 0.999F;
      this.xd *= 0.800000011920929;
      this.yd *= 0.800000011920929;
      this.zd *= 0.800000011920929;
      this.yd = (double)(this.random.nextFloat() * 0.4F + 0.05F);
      this.quadSize *= this.random.nextFloat() * 2.0F + 0.2F;
      this.lifetime = (int)(16.0 / ((double)this.random.nextFloat() * 0.8 + 0.2));
   }

   public SingleQuadParticle.Layer getLayer() {
      return SingleQuadParticle.Layer.OPAQUE;
   }

   public int getLightCoords(final float a) {
      return LightCoordsUtil.withBlock(super.getLightCoords(a), 15);
   }

   public float getQuadSize(final float a) {
      float s = ((float)this.age + a) / (float)this.lifetime;
      return this.quadSize * (1.0F - s * s);
   }

   public void tick() {
      super.tick();
      if (!this.removed) {
         float odds = (float)this.age / (float)this.lifetime;
         if (this.random.nextFloat() > odds) {
            this.level.addParticle(ParticleTypes.SMOKE, this.x, this.y, this.z, this.xd, this.yd, this.zd);
         }
      }

   }

   public static class Provider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprite;

      public Provider(final SpriteSet sprite) {
         super();
         this.sprite = sprite;
      }

      public Particle createParticle(final SimpleParticleType options, final ClientLevel level, final double x, final double y, final double z, final double xAux, final double yAux, final double zAux, final RandomSource random) {
         LavaParticle particle = new LavaParticle(level, x, y, z, this.sprite.get(random));
         return particle;
      }
   }
}
