package net.minecraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;

public class WaterCurrentDownParticle extends TextureSheetParticle {
   private float angle;

   WaterCurrentDownParticle(ClientLevel var1, double var2, double var4, double var6) {
      super(var1, var2, var4, var6);
      this.lifetime = (int)(Math.random() * 60.0) + 30;
      this.hasPhysics = false;
      this.xd = 0.0;
      this.yd = -0.05;
      this.zd = 0.0;
      this.setSize(0.02F, 0.02F);
      this.quadSize = this.quadSize * (this.random.nextFloat() * 0.6F + 0.2F);
      this.gravity = 0.002F;
   }

   @Override
   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
   }

   @Override
   public void tick() {
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      if (this.age++ >= this.lifetime) {
         this.remove();
      } else {
         float var1 = 0.6F;
         this.xd = this.xd + (double)(0.6F * Mth.cos(this.angle));
         this.zd = this.zd + (double)(0.6F * Mth.sin(this.angle));
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

      public Particle createParticle(SimpleParticleType var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         WaterCurrentDownParticle var15 = new WaterCurrentDownParticle(var2, var3, var5, var7);
         var15.pickSprite(this.sprite);
         return var15;
      }
   }
}
