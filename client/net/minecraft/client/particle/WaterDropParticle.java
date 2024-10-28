package net.minecraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;

public class WaterDropParticle extends TextureSheetParticle {
   protected WaterDropParticle(ClientLevel var1, double var2, double var4, double var6) {
      super(var1, var2, var4, var6, 0.0, 0.0, 0.0);
      this.xd *= 0.30000001192092896;
      this.yd = Math.random() * 0.20000000298023224 + 0.10000000149011612;
      this.zd *= 0.30000001192092896;
      this.setSize(0.01F, 0.01F);
      this.gravity = 0.06F;
      this.lifetime = (int)(8.0 / (Math.random() * 0.8 + 0.2));
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
         this.yd -= (double)this.gravity;
         this.move(this.xd, this.yd, this.zd);
         this.xd *= 0.9800000190734863;
         this.yd *= 0.9800000190734863;
         this.zd *= 0.9800000190734863;
         if (this.onGround) {
            if (Math.random() < 0.5) {
               this.remove();
            }

            this.xd *= 0.699999988079071;
            this.zd *= 0.699999988079071;
         }

         BlockPos var1 = BlockPos.containing(this.x, this.y, this.z);
         double var2 = Math.max(this.level.getBlockState(var1).getCollisionShape(this.level, var1).max(Direction.Axis.Y, this.x - (double)var1.getX(), this.z - (double)var1.getZ()), (double)this.level.getFluidState(var1).getHeight(this.level, var1));
         if (var2 > 0.0 && this.y < (double)var1.getY() + var2) {
            this.remove();
         }

      }
   }

   public static class Provider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprite;

      public Provider(SpriteSet var1) {
         super();
         this.sprite = var1;
      }

      public Particle createParticle(SimpleParticleType var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         WaterDropParticle var15 = new WaterDropParticle(var2, var3, var5, var7);
         var15.pickSprite(this.sprite);
         return var15;
      }

      // $FF: synthetic method
      public Particle createParticle(ParticleOptions var1, ClientLevel var2, double var3, double var5, double var7, double var9, double var11, double var13) {
         return this.createParticle((SimpleParticleType)var1, var2, var3, var5, var7, var9, var11, var13);
      }
   }
}
