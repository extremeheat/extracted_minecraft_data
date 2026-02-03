package net.minecraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;

public class BubbleParticle extends SingleQuadParticle {
   private BubbleParticle(final ClientLevel level, final double x, final double y, final double z, final double xa, final double ya, final double za, final TextureAtlasSprite sprite) {
      super(level, x, y, z, sprite);
      this.setSize(0.02F, 0.02F);
      this.quadSize *= this.random.nextFloat() * 0.6F + 0.2F;
      this.xd = xa * 0.20000000298023224 + (double)((this.random.nextFloat() * 2.0F - 1.0F) * 0.02F);
      this.yd = ya * 0.20000000298023224 + (double)((this.random.nextFloat() * 2.0F - 1.0F) * 0.02F);
      this.zd = za * 0.20000000298023224 + (double)((this.random.nextFloat() * 2.0F - 1.0F) * 0.02F);
      this.lifetime = (int)(8.0 / ((double)this.random.nextFloat() * 0.8 + 0.2));
   }

   public void tick() {
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      if (this.lifetime-- <= 0) {
         this.remove();
      } else {
         this.yd += 0.002;
         this.move(this.xd, this.yd, this.zd);
         this.xd *= 0.8500000238418579;
         this.yd *= 0.8500000238418579;
         this.zd *= 0.8500000238418579;
         if (!this.level.getFluidState(BlockPos.containing(this.x, this.y, this.z)).is(FluidTags.WATER)) {
            this.remove();
         }

      }
   }

   public SingleQuadParticle.Layer getLayer() {
      return SingleQuadParticle.Layer.OPAQUE;
   }

   public static class Provider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprite;

      public Provider(final SpriteSet sprite) {
         super();
         this.sprite = sprite;
      }

      public Particle createParticle(final SimpleParticleType options, final ClientLevel level, final double x, final double y, final double z, final double xAux, final double yAux, final double zAux, final RandomSource random) {
         BubbleParticle particle = new BubbleParticle(level, x, y, z, xAux, yAux, zAux, this.sprite.get(random));
         return particle;
      }
   }
}
