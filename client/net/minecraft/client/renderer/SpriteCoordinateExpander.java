package net.minecraft.client.renderer;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public class SpriteCoordinateExpander implements VertexConsumer {
   private final VertexConsumer delegate;
   private final TextureAtlasSprite sprite;

   public SpriteCoordinateExpander(final VertexConsumer delegate, final TextureAtlasSprite sprite) {
      super();
      this.delegate = delegate;
      this.sprite = sprite;
   }

   public VertexConsumer addVertex(final float x, final float y, final float z) {
      return this.delegate.addVertex(x, y, z);
   }

   public VertexConsumer setColor(final int r, final int g, final int b, final int a) {
      return this.delegate.setColor(r, g, b, a);
   }

   public VertexConsumer setColor(final int color) {
      return this.delegate.setColor(color);
   }

   public VertexConsumer setUv(final float u, final float v) {
      return this.delegate.setUv(this.sprite.getU(u), this.sprite.getV(v));
   }

   public VertexConsumer setUv1(final int u, final int v) {
      return this.delegate.setUv1(u, v);
   }

   public VertexConsumer setUv2(final int u, final int v) {
      return this.delegate.setUv2(u, v);
   }

   public VertexConsumer setNormal(final float x, final float y, final float z) {
      return this.delegate.setNormal(x, y, z);
   }

   public VertexConsumer setLineWidth(final float width) {
      this.delegate.setLineWidth(width);
      return this;
   }

   public void addVertex(final float x, final float y, final float z, final int color, final float u, final float v, final int overlayCoords, final int lightCoords, final float nx, final float ny, final float nz) {
      this.delegate.addVertex(x, y, z, color, this.sprite.getU(u), this.sprite.getV(v), overlayCoords, lightCoords, nx, ny, nz);
   }
}
