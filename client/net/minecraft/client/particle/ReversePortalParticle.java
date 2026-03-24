package net.minecraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;

public class ReversePortalParticle extends PortalParticle {
   private ReversePortalParticle(final ClientLevel level, final double x, final double y, final double z, final double xd, final double yd, final double zd, final TextureAtlasSprite sprite) {
      super(level, x, y, z, xd, yd, zd, sprite);
      this.quadSize *= 1.5F;
      this.lifetime = (int)(this.random.nextFloat() * 2.0F) + 60;
   }

   public float getQuadSize(final float a) {
      float s = 1.0F - ((float)this.age + a) / ((float)this.lifetime * 1.5F);
      return this.quadSize * s;
   }

   public void tick() {
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      if (this.age++ >= this.lifetime) {
         this.remove();
      } else {
         float speedMultiplier = (float)this.age / (float)this.lifetime;
         this.x += this.xd * (double)speedMultiplier;
         this.y += this.yd * (double)speedMultiplier;
         this.z += this.zd * (double)speedMultiplier;
      }
   }

   public static class ReversePortalProvider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprite;

      public ReversePortalProvider(final SpriteSet sprite) {
         super();
         this.sprite = sprite;
      }

      public Particle createParticle(final SimpleParticleType options, final ClientLevel level, final double x, final double y, final double z, final double xAux, final double yAux, final double zAux, final RandomSource random) {
         ReversePortalParticle particle = new ReversePortalParticle(level, x, y, z, xAux, yAux, zAux, this.sprite.get(random));
         return particle;
      }
   }
}
