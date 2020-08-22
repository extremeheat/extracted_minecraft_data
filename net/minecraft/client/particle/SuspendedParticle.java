package net.minecraft.client.particle;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.Level;

public class SuspendedParticle extends TextureSheetParticle {
   private SuspendedParticle(Level var1, double var2, double var4, double var6) {
      super(var1, var2, var4 - 0.125D, var6);
      this.rCol = 0.4F;
      this.gCol = 0.4F;
      this.bCol = 0.7F;
      this.setSize(0.01F, 0.01F);
      this.quadSize *= this.random.nextFloat() * 0.6F + 0.2F;
      this.lifetime = (int)(16.0D / (Math.random() * 0.8D + 0.2D));
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
   }

   public void tick() {
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      if (this.lifetime-- <= 0) {
         this.remove();
      } else {
         this.move(this.xd, this.yd, this.zd);
         if (!this.level.getFluidState(new BlockPos(this.x, this.y, this.z)).is(FluidTags.WATER)) {
            this.remove();
         }

      }
   }

   // $FF: synthetic method
   SuspendedParticle(Level var1, double var2, double var4, double var6, Object var8) {
      this(var1, var2, var4, var6);
   }

   public static class Provider implements ParticleProvider {
      private final SpriteSet sprite;

      public Provider(SpriteSet var1) {
         this.sprite = var1;
      }

      public Particle createParticle(SimpleParticleType var1, Level var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         SuspendedParticle var15 = new SuspendedParticle(var2, var3, var5, var7);
         var15.pickSprite(this.sprite);
         return var15;
      }
   }
}
