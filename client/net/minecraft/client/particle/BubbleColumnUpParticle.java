package net.minecraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BubbleColumnUpParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;

public class BubbleColumnUpParticle extends SingleQuadParticle {
   private final float popChance;

   private BubbleColumnUpParticle(final ClientLevel level, final double x, final double y, final double z, final double xa, final double ya, final double za, final TextureAtlasSprite sprite, final float popChance) {
      super(level, x, y, z, sprite);
      this.gravity = -0.125F;
      this.friction = 0.85F;
      this.setSize(0.02F, 0.02F);
      this.quadSize *= this.random.nextFloat() * 0.6F + 0.2F;
      this.xd = xa * 0.20000000298023224 + (double)((this.random.nextFloat() * 2.0F - 1.0F) * 0.02F);
      this.yd = ya * 0.20000000298023224 + (double)((this.random.nextFloat() * 2.0F - 1.0F) * 0.02F);
      this.zd = za * 0.20000000298023224 + (double)((this.random.nextFloat() * 2.0F - 1.0F) * 0.02F);
      this.lifetime = (int)(40.0 / ((double)this.random.nextFloat() * 0.8 + 0.2));
      this.popChance = popChance;
   }

   public void tick() {
      super.tick();
      if (!this.removed && !this.level.getFluidState(BlockPos.containing(this.x, this.y, this.z)).is(FluidTags.WATER)) {
         this.remove();
         if (this.level.getRandom().nextFloat() < this.popChance) {
            this.level.addAlwaysVisibleParticle(ParticleTypes.BUBBLE_POP, this.x, this.y, this.z, 0.0, 0.0, 0.0);
            this.level.playLocalSound(this.x, this.y, this.z, SoundEvents.BUBBLE_COLUMN_BUBBLE_POP, SoundSource.BLOCKS, 1.0F, 1.0F, false);
         }
      }

   }

   public SingleQuadParticle.Layer getLayer() {
      return SingleQuadParticle.Layer.OPAQUE;
   }

   public static class Provider implements ParticleProvider<BubbleColumnUpParticleOptions> {
      private final SpriteSet sprite;

      public Provider(final SpriteSet sprite) {
         super();
         this.sprite = sprite;
      }

      public Particle createParticle(final BubbleColumnUpParticleOptions options, final ClientLevel level, final double x, final double y, final double z, final double xAux, final double yAux, final double zAux, final RandomSource random) {
         BubbleColumnUpParticle particle = new BubbleColumnUpParticle(level, x, y, z, xAux, yAux, zAux, this.sprite.get(random), options.popChance());
         return particle;
      }
   }
}
