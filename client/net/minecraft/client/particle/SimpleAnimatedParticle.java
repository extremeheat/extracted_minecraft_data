package net.minecraft.client.particle;

import net.minecraft.world.level.Level;

public class SimpleAnimatedParticle extends TextureSheetParticle {
   protected final SpriteSet sprites;
   private final float baseGravity;
   private float baseAirFriction = 0.91F;
   private float fadeR;
   private float fadeG;
   private float fadeB;
   private boolean hasFade;

   protected SimpleAnimatedParticle(Level var1, double var2, double var4, double var6, SpriteSet var8, float var9) {
      super(var1, var2, var4, var6);
      this.sprites = var8;
      this.baseGravity = var9;
   }

   public void setColor(int var1) {
      float var2 = (float)((var1 & 16711680) >> 16) / 255.0F;
      float var3 = (float)((var1 & '\uff00') >> 8) / 255.0F;
      float var4 = (float)((var1 & 255) >> 0) / 255.0F;
      float var5 = 1.0F;
      this.setColor(var2 * 1.0F, var3 * 1.0F, var4 * 1.0F);
   }

   public void setFadeColor(int var1) {
      this.fadeR = (float)((var1 & 16711680) >> 16) / 255.0F;
      this.fadeG = (float)((var1 & '\uff00') >> 8) / 255.0F;
      this.fadeB = (float)((var1 & 255) >> 0) / 255.0F;
      this.hasFade = true;
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
   }

   public void tick() {
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      if (this.age++ >= this.lifetime) {
         this.remove();
      } else {
         this.setSpriteFromAge(this.sprites);
         if (this.age > this.lifetime / 2) {
            this.setAlpha(1.0F - ((float)this.age - (float)(this.lifetime / 2)) / (float)this.lifetime);
            if (this.hasFade) {
               this.rCol += (this.fadeR - this.rCol) * 0.2F;
               this.gCol += (this.fadeG - this.gCol) * 0.2F;
               this.bCol += (this.fadeB - this.bCol) * 0.2F;
            }
         }

         this.yd += (double)this.baseGravity;
         this.move(this.xd, this.yd, this.zd);
         this.xd *= (double)this.baseAirFriction;
         this.yd *= (double)this.baseAirFriction;
         this.zd *= (double)this.baseAirFriction;
         if (this.onGround) {
            this.xd *= 0.699999988079071D;
            this.zd *= 0.699999988079071D;
         }

      }
   }

   public int getLightColor(float var1) {
      return 15728880;
   }

   protected void setBaseAirFriction(float var1) {
      this.baseAirFriction = var1;
   }
}
