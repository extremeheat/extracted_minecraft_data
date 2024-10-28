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

   public VertexConsumer addVertex(float var1, float var2, float var3) {
      return this.delegate.addVertex(var1, var2, var3);
   }

   public VertexConsumer setColor(int var1, int var2, int var3, int var4) {
      return this.delegate.setColor(var1, var2, var3, var4);
   }

   public VertexConsumer setUv(float var1, float var2) {
      return this.delegate.setUv(this.sprite.getU(var1), this.sprite.getV(var2));
   }

   public VertexConsumer setUv1(int var1, int var2) {
      return this.delegate.setUv1(var1, var2);
   }

   public VertexConsumer setUv2(int var1, int var2) {
      return this.delegate.setUv2(var1, var2);
   }

   public VertexConsumer setNormal(float var1, float var2, float var3) {
      return this.delegate.setNormal(var1, var2, var3);
   }

   public void addVertex(float var1, float var2, float var3, int var4, float var5, float var6, int var7, int var8, float var9, float var10, float var11) {
      this.delegate.addVertex(var1, var2, var3, var4, this.sprite.getU(var5), this.sprite.getV(var6), var7, var8, var9, var10, var11);
   }
}
