package net.minecraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;

public abstract class SimpleAnimatedParticle extends SingleQuadParticle {
   protected final SpriteSet sprites;
   private float fadeR;
   private float fadeG;
   private float fadeB;
   private boolean hasFade;

   protected SimpleAnimatedParticle(final ClientLevel level, final double x, final double y, final double z, final SpriteSet sprites, final float gravity) {
      super(level, x, y, z, sprites.first());
      this.friction = 0.91F;
      this.gravity = gravity;
      this.sprites = sprites;
   }

   public void setColor(final int rgb) {
      float r = (float)((rgb & 16711680) >> 16) / 255.0F;
      float g = (float)((rgb & '\uff00') >> 8) / 255.0F;
      float b = (float)((rgb & 255) >> 0) / 255.0F;
      float scale = 1.0F;
      this.setColor(r * 1.0F, g * 1.0F, b * 1.0F);
   }

   public void setFadeColor(final int rgb) {
      this.fadeR = (float)((rgb & 16711680) >> 16) / 255.0F;
      this.fadeG = (float)((rgb & '\uff00') >> 8) / 255.0F;
      this.fadeB = (float)((rgb & 255) >> 0) / 255.0F;
      this.hasFade = true;
   }

   public SingleQuadParticle.Layer getLayer() {
      return SingleQuadParticle.Layer.TRANSLUCENT;
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

   public int getLightCoords(final float a) {
      return 15728880;
   }
}
