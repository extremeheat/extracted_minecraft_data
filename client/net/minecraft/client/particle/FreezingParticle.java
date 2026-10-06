package net.minecraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;

public class FreezingParticle extends SingleQuadParticle {
   private final float rotSpeed;
   private boolean landed;

   public FreezingParticle(final ClientLevel level, final double x, final double y, final double z, final double xAux, final double yAux, final double zAux, final TextureAtlasSprite sprite) {
      super(level, x, y, z, xAux, yAux, zAux, sprite);
      this.xd = (double)((this.random.nextFloat() * 2.0F - 1.0F) * 0.01F);
      this.yd = (double)((this.random.nextFloat() * 2.0F - 1.0F) * 0.01F * 0.25F);
      this.zd = (double)((this.random.nextFloat() * 2.0F - 1.0F) * 0.01F);
      float scale = 0.9F;
      this.gravity = 0.125F;
      this.friction = 1.0F;
      this.quadSize *= 0.80999994F;
      int baseLifetime = (int)(32.0 / ((double)this.random.nextFloat() * 0.8 + 0.2));
      this.lifetime = (int)Math.max((float)baseLifetime * 0.9F, 10.0F);
      this.rotSpeed = (this.random.nextFloat() - 0.5F) * 0.1F;
      this.roll = this.random.nextFloat() * 6.2831855F;
   }

   public SingleQuadParticle.Layer getLayer() {
      return SingleQuadParticle.Layer.OPAQUE;
   }

   public void tick() {
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      if (this.age++ >= this.lifetime) {
         this.remove();
      } else {
         this.oRoll = this.roll;
         this.roll += 3.1415927F * this.rotSpeed * 2.0F * (float)Math.exp((double)((float)this.age / -8.0F));
         if (this.onGround && !this.landed) {
            this.lifetime = this.age + 20;
            this.landed = true;
         }

         this.move(this.xd, this.yd, this.zd);
         this.yd -= 0.003000000026077032;
         this.yd = Math.max(this.yd, -0.14000000059604645);
      }
   }

   public static class Provider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprites;

      public Provider(final SpriteSet sprites) {
         super();
         this.sprites = sprites;
      }

      public Particle createParticle(final SimpleParticleType options, final ClientLevel level, final double x, final double y, final double z, final double xAux, final double yAux, final double zAux, final RandomSource random) {
         return new FreezingParticle(level, x, y, z, xAux, yAux, zAux, this.sprites.get(random));
      }
   }
}
