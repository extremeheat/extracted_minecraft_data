package net.minecraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

public class WaterCurrentDownParticle extends SingleQuadParticle {
   private float angle;

   WaterCurrentDownParticle(ClientLevel var1, double var2, double var4, double var6, TextureAtlasSprite var8) {
      super(var1, var2, var4, var6, var8);
      this.lifetime = (int)(this.random.nextFloat() * 60.0F) + 30;
      this.hasPhysics = false;
      this.xd = 0.0;
      this.yd = -0.05;
      this.zd = 0.0;
      this.setSize(0.02F, 0.02F);
      this.quadSize *= this.random.nextFloat() * 0.6F + 0.2F;
      this.gravity = 0.002F;
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
         float var1 = 0.6F;
         this.xd += (double)(0.6F * Mth.cos((double)this.angle));
         this.zd += (double)(0.6F * Mth.sin((double)this.angle));
         this.xd *= 0.07;
         this.zd *= 0.07;
         this.move(this.xd, this.yd, this.zd);
         if (!this.level.getFluidState(BlockPos.containing(this.x, this.y, this.z)).is(FluidTags.WATER) || this.onGround) {
            this.remove();
         }

         this.angle += 0.08F;
      }
   }

   public static class Provider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprite;

      public Provider(SpriteSet var1) {
         super();
         this.sprite = var1;
      }

      public Particle createParticle(SimpleParticleType var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13, RandomSource var15) {
         return new WaterCurrentDownParticle(var2, var3, var5, var7, this.sprite.get(var15));
      }
   }
}
