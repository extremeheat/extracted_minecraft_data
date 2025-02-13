package net.minecraft.client.renderer.block.model;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;

public record BakedQuad(int[] vertices, int tintIndex, Direction direction, TextureAtlasSprite sprite, boolean shade, int lightEmission) {
   public BakedQuad(int[] var1, int var2, Direction var3, TextureAtlasSprite var4, boolean var5, int var6) {
      super();
      this.vertices = var1;
      this.tintIndex = var2;
      this.direction = var3;
      this.sprite = var4;
      this.shade = var5;
      this.lightEmission = var6;
   }

   public boolean isTinted() {
      return this.tintIndex != -1;
   }
}
