package net.minecraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;

public class SimpleAnimatedParticle extends TextureSheetParticle {
   protected final SpriteSet sprites;
   private float fadeR;
   private float fadeG;
   private float fadeB;
   private boolean hasFade;

   protected SimpleAnimatedParticle(ClientLevel var1, double var2, double var4, double var6, SpriteSet var8, float var9) {
      super(var1, var2, var4, var6);
      this.friction = 0.91F;
      this.gravity = var9;
      this.sprites = var8;
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
      super.tick();
      this.setSpriteFromAge(this.sprites);
      if (this.age > this.lifetime / 2) {
         this.setAlpha(1.0F - ((float)this.age - (float)(this.lifetime / 2)) / (float)this.lifetime);
         if (this.hasFade) {
            this.rCol += (this.fadeR - this.rCol) * 0.2F;
            this.gCol += (this.fadeG - this.gCol) * 0.2F;
            this.bCol += (this.fadeB - this.bCol) * 0.2F;
         }
      }

   }

   public int getLightColor(float var1) {
      return 15728880;
   }
}
