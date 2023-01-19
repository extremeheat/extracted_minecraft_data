package net.minecraft.client.renderer;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public class SpriteCoordinateExpander implements VertexConsumer {
   private final VertexConsumer delegate;
   private final TextureAtlasSprite sprite;

   public SpriteCoordinateExpander(VertexConsumer var1, TextureAtlasSprite var2) {
      super();
      this.delegate = var1;
      this.sprite = var2;
   }

   @Override
   public VertexConsumer vertex(double var1, double var3, double var5) {
      return this.delegate.vertex(var1, var3, var5);
   }

   @Override
   public VertexConsumer color(int var1, int var2, int var3, int var4) {
      return this.delegate.color(var1, var2, var3, var4);
   }

   @Override
   public VertexConsumer uv(float var1, float var2) {
      return this.delegate.uv(this.sprite.getU((double)(var1 * 16.0F)), this.sprite.getV((double)(var2 * 16.0F)));
   }

   @Override
   public VertexConsumer overlayCoords(int var1, int var2) {
      return this.delegate.overlayCoords(var1, var2);
   }

   @Override
   public VertexConsumer uv2(int var1, int var2) {
      return this.delegate.uv2(var1, var2);
   }

   @Override
   public VertexConsumer normal(float var1, float var2, float var3) {
      return this.delegate.normal(var1, var2, var3);
   }

   @Override
   public void endVertex() {
      this.delegate.endVertex();
   }

   @Override
   public void defaultColor(int var1, int var2, int var3, int var4) {
      this.delegate.defaultColor(var1, var2, var3, var4);
   }

   @Override
   public void unsetDefaultColor() {
      this.delegate.unsetDefaultColor();
   }

   @Override
   public void vertex(
      float var1,
      float var2,
      float var3,
      float var4,
      float var5,
      float var6,
      float var7,
      float var8,
      float var9,
      int var10,
      int var11,
      float var12,
      float var13,
      float var14
   ) {
      this.delegate
         .vertex(
            var1,
            var2,
            var3,
            var4,
            var5,
            var6,
            var7,
            this.sprite.getU((double)(var8 * 16.0F)),
            this.sprite.getV((double)(var9 * 16.0F)),
            var10,
            var11,
            var12,
            var13,
            var14
         );
   }
}
