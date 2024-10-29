package net.minecraft.client.renderer;

import com.mojang.blaze3d.shaders.FogShape;

public record FogParameters(float start, float end, FogShape shape, float red, float green, float blue, float alpha) {
   public static final FogParameters NO_FOG;

   public FogParameters(float var1, float var2, FogShape var3, float var4, float var5, float var6, float var7) {
      super();
      this.start = var1;
      this.end = var2;
      this.shape = var3;
      this.red = var4;
      this.green = var5;
      this.blue = var6;
      this.alpha = var7;
   }

   public float start() {
      return this.start;
   }

   public float end() {
      return this.end;
   }

   public FogShape shape() {
      return this.shape;
   }

   public float red() {
      return this.red;
   }

   public float green() {
      return this.green;
   }

   public float blue() {
      return this.blue;
   }

   public float alpha() {
      return this.alpha;
   }

   static {
      NO_FOG = new FogParameters(3.4028235E38F, 0.0F, FogShape.SPHERE, 0.0F, 0.0F, 0.0F, 0.0F);
   }
}
