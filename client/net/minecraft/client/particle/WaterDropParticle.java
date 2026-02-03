package net.minecraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;

public class WaterDropParticle extends SingleQuadParticle {
   protected WaterDropParticle(final ClientLevel level, final double x, final double y, final double z, final TextureAtlasSprite sprite) {
      super(level, x, y, z, 0.0, 0.0, 0.0, sprite);
      this.xd *= 0.30000001192092896;
      this.yd = (double)(this.random.nextFloat() * 0.2F + 0.1F);
      this.zd *= 0.30000001192092896;
      this.setSize(0.01F, 0.01F);
      this.gravity = 0.06F;
      this.lifetime = (int)(8.0 / ((double)this.random.nextFloat() * 0.8 + 0.2));
   }

   public SingleQuadParticle.Layer getLayer() {
      return SingleQuadParticle.Layer.OPAQUE;
   }

   public void tick() {
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      if (this.lifetime-- <= 0) {
         this.remove();
      } else {
         this.yd -= (double)this.gravity;
         this.move(this.xd, this.yd, this.zd);
         this.xd *= 0.9800000190734863;
         this.yd *= 0.9800000190734863;
         this.zd *= 0.9800000190734863;
         if (this.onGround) {
            if (this.random.nextFloat() < 0.5F) {
               this.remove();
            }

            this.xd *= 0.699999988079071;
            this.zd *= 0.699999988079071;
         }

         BlockPos pos = BlockPos.containing(this.x, this.y, this.z);
         double offset = Math.max(this.level.getBlockState(pos).getCollisionShape(this.level, pos).max(Direction.Axis.Y, this.x - (double)pos.getX(), this.z - (double)pos.getZ()), (double)this.level.getFluidState(pos).getHeight(this.level, pos));
         if (offset > 0.0 && this.y < (double)pos.getY() + offset) {
            this.remove();
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
         return new WaterDropParticle(level, x, y, z, this.sprite.get(random));
      }
   }
}
