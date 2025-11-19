package net.minecraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;

public class BubbleParticle extends SingleQuadParticle {
   BubbleParticle(ClientLevel var1, double var2, double var4, double var6, double var8, double var10, double var12, TextureAtlasSprite var14) {
      super(var1, var2, var4, var6, var14);
      this.setSize(0.02F, 0.02F);
      this.quadSize *= this.random.nextFloat() * 0.6F + 0.2F;
      this.xd = var8 * 0.20000000298023224 + (double)((this.random.nextFloat() * 2.0F - 1.0F) * 0.02F);
      this.yd = var10 * 0.20000000298023224 + (double)((this.random.nextFloat() * 2.0F - 1.0F) * 0.02F);
      this.zd = var12 * 0.20000000298023224 + (double)((this.random.nextFloat() * 2.0F - 1.0F) * 0.02F);
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

      public Provider(SpriteSet var1) {
         super();
         this.sprite = var1;
      }

      public Particle createParticle(SimpleParticleType var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13, RandomSource var15) {
         BubbleParticle var16 = new BubbleParticle(var2, var3, var5, var7, var9, var11, var13, this.sprite.get(var15));
         return var16;
      }
   }
}
