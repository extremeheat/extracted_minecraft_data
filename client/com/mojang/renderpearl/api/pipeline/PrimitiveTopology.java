package com.mojang.renderpearl.api.pipeline;

public enum PrimitiveTopology {
   LINES(2, 2, false),
   DEBUG_LINES(2, 2, false),
   DEBUG_LINE_STRIP(2, 1, true),
   POINTS(1, 1, false),
   TRIANGLES(3, 3, false),
   TRIANGLE_STRIP(3, 1, true),
   TRIANGLE_FAN(3, 1, true),
   QUADS(4, 4, false);

   public final int primitiveLength;
   public final int primitiveStride;
   public final boolean connectedPrimitives;

   private PrimitiveTopology(final int primitiveLength, final int primitiveStride, final boolean connectedPrimitives) {
      this.primitiveLength = primitiveLength;
      this.primitiveStride = primitiveStride;
      this.connectedPrimitives = connectedPrimitives;
   }

   public int indexCount(final int vertexCount) {
      int var10000;
      switch (this.ordinal()) {
         case 0:
         case 7:
            var10000 = vertexCount / 4 * 6;
            break;
         case 1:
         case 2:
         case 3:
         case 4:
         case 5:
         case 6:
            var10000 = vertexCount;
            break;
         default:
            var10000 = 0;
      }

      int indexCount = var10000;
      return indexCount;
   }

   // $FF: synthetic method
   private static PrimitiveTopology[] $values() {
      return new PrimitiveTopology[]{LINES, DEBUG_LINES, DEBUG_LINE_STRIP, POINTS, TRIANGLES, TRIANGLE_STRIP, TRIANGLE_FAN, QUADS};
   }
}
