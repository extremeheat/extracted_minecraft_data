package net.minecraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.Mth;

public class BaseAshSmokeParticle extends TextureSheetParticle {
   private final SpriteSet sprites;
   private final double fallSpeed;

   protected BaseAshSmokeParticle(ClientLevel var1, double var2, double var4, double var6, float var8, float var9, float var10, double var11, double var13, double var15, float var17, SpriteSet var18, float var19, int var20, double var21, boolean var23) {
      super(var1, var2, var4, var6, 0.0D, 0.0D, 0.0D);
      this.fallSpeed = var21;
      this.sprites = var18;
      this.xd *= (double)var8;
      this.yd *= (double)var9;
      this.zd *= (double)var10;
      this.xd += var11;
      this.yd += var13;
      this.zd += var15;
      float var24 = var1.random.nextFloat() * var19;
      this.rCol = var24;
      this.gCol = var24;
      this.bCol = var24;
      this.quadSize *= 0.75F * var17;
      this.lifetime = (int)((double)var20 / ((double)var1.random.nextFloat() * 0.8D + 0.2D));
      this.lifetime = (int)((float)this.lifetime * var17);
      this.lifetime = Math.max(this.lifetime, 1);
      this.setSpriteFromAge(var18);
      this.hasPhysics = var23;
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
   }

   public float getQuadSize(float var1) {
      return this.quadSize * Mth.clamp(((float)this.age + var1) / (float)this.lifetime * 32.0F, 0.0F, 1.0F);
   }

   public void tick() {
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      if (this.age++ >= this.lifetime) {
         this.remove();
      } else {
         this.setSpriteFromAge(this.sprites);
         this.yd += this.fallSpeed;
         this.move(this.xd, this.yd, this.zd);
         if (this.y == this.yo) {
            this.xd *= 1.1D;
            this.zd *= 1.1D;
         }

         this.xd *= 0.9599999785423279D;
         this.yd *= 0.9599999785423279D;
         this.zd *= 0.9599999785423279D;
         if (this.onGround) {
            this.xd *= 0.699999988079071D;
            this.zd *= 0.699999988079071D;
         }

      }
   }
}
