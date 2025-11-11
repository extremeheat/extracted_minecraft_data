package net.minecraft.client.renderer.block.model;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import org.joml.Vector3fc;

public record BakedQuad(Vector3fc position0, Vector3fc position1, Vector3fc position2, Vector3fc position3, long packedUV0, long packedUV1, long packedUV2, long packedUV3, int tintIndex, Direction direction, TextureAtlasSprite sprite, boolean shade, int lightEmission) {
   public static final int VERTEX_COUNT = 4;

   public BakedQuad(Vector3fc var1, Vector3fc var2, Vector3fc var3, Vector3fc var4, long var5, long var7, long var9, long var11, int var13, Direction var14, TextureAtlasSprite var15, boolean var16, int var17) {
      super();
      this.position0 = var1;
      this.position1 = var2;
      this.position2 = var3;
      this.position3 = var4;
      this.packedUV0 = var5;
      this.packedUV1 = var7;
      this.packedUV2 = var9;
      this.packedUV3 = var11;
      this.tintIndex = var13;
      this.direction = var14;
      this.sprite = var15;
      this.shade = var16;
      this.lightEmission = var17;
   }

   public boolean isTinted() {
      return this.tintIndex != -1;
   }

   public Vector3fc position(int var1) {
      Vector3fc var10000;
      switch (var1) {
         case 0 -> var10000 = this.position0;
         case 1 -> var10000 = this.position1;
         case 2 -> var10000 = this.position2;
         case 3 -> var10000 = this.position3;
         default -> throw new IndexOutOfBoundsException(var1);
      }

      return var10000;
   }

   public long packedUV(int var1) {
      long var10000;
      switch (var1) {
         case 0 -> var10000 = this.packedUV0;
         case 1 -> var10000 = this.packedUV1;
         case 2 -> var10000 = this.packedUV2;
         case 3 -> var10000 = this.packedUV3;
         default -> throw new IndexOutOfBoundsException(var1);
      }

      return var10000;
   }
}
