package net.minecraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public abstract class TextureSheetParticle extends SingleQuadParticle {
   protected TextureAtlasSprite sprite;

   protected TextureSheetParticle(ClientLevel var1, double var2, double var4, double var6) {
      super(var1, var2, var4, var6);
   }

   protected TextureSheetParticle(ClientLevel var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      super(var1, var2, var4, var6, var8, var10, var12);
   }

   protected void setSprite(TextureAtlasSprite var1) {
      this.sprite = var1;
   }

   protected float getU0() {
      return this.sprite.getU0();
   }

   protected float getU1() {
      return this.sprite.getU1();
   }

   protected float getV0() {
      return this.sprite.getV0();
   }

   protected float getV1() {
      return this.sprite.getV1();
   }

   public void pickSprite(SpriteSet var1) {
      this.setSprite(var1.get(this.random));
   }

   public void setSpriteFromAge(SpriteSet var1) {
      if (!this.removed) {
         this.setSprite(var1.get(this.age, this.lifetime));
      }

   }
}
