package net.minecraft.client.renderer.block.model;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import org.joml.Vector3fc;

public record BakedQuad(Vector3fc position0, Vector3fc position1, Vector3fc position2, Vector3fc position3, long packedUV0, long packedUV1, long packedUV2, long packedUV3, int tintIndex, Direction direction, TextureAtlasSprite sprite, boolean shade, int lightEmission) {
   public static final int VERTEX_COUNT = 4;

   public BakedQuad {
      super();
   }

   public boolean isTinted() {
      return this.tintIndex != -1;
   }

   public Vector3fc position(final int vertex) {
      Vector3fc var10000;
      switch (vertex) {
         case 0 -> var10000 = this.position0;
         case 1 -> var10000 = this.position1;
         case 2 -> var10000 = this.position2;
         case 3 -> var10000 = this.position3;
         default -> throw new IndexOutOfBoundsException(vertex);
      }

      return var10000;
   }

   public long packedUV(final int vertex) {
      long var10000;
      switch (vertex) {
         case 0 -> var10000 = this.packedUV0;
         case 1 -> var10000 = this.packedUV1;
         case 2 -> var10000 = this.packedUV2;
         case 3 -> var10000 = this.packedUV3;
         default -> throw new IndexOutOfBoundsException(vertex);
      }

      return var10000;
   }
}
