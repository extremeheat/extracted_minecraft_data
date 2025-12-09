package net.minecraft.client.renderer;

import java.util.EnumMap;
import java.util.Map;
import net.minecraft.core.Direction;
import net.minecraft.util.Util;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public enum FaceInfo {
   DOWN(new VertexInfo[]{new VertexInfo(FaceInfo.Extent.MIN_X, FaceInfo.Extent.MIN_Y, FaceInfo.Extent.MAX_Z), new VertexInfo(FaceInfo.Extent.MIN_X, FaceInfo.Extent.MIN_Y, FaceInfo.Extent.MIN_Z), new VertexInfo(FaceInfo.Extent.MAX_X, FaceInfo.Extent.MIN_Y, FaceInfo.Extent.MIN_Z), new VertexInfo(FaceInfo.Extent.MAX_X, FaceInfo.Extent.MIN_Y, FaceInfo.Extent.MAX_Z)}),
   UP(new VertexInfo[]{new VertexInfo(FaceInfo.Extent.MIN_X, FaceInfo.Extent.MAX_Y, FaceInfo.Extent.MIN_Z), new VertexInfo(FaceInfo.Extent.MIN_X, FaceInfo.Extent.MAX_Y, FaceInfo.Extent.MAX_Z), new VertexInfo(FaceInfo.Extent.MAX_X, FaceInfo.Extent.MAX_Y, FaceInfo.Extent.MAX_Z), new VertexInfo(FaceInfo.Extent.MAX_X, FaceInfo.Extent.MAX_Y, FaceInfo.Extent.MIN_Z)}),
   NORTH(new VertexInfo[]{new VertexInfo(FaceInfo.Extent.MAX_X, FaceInfo.Extent.MAX_Y, FaceInfo.Extent.MIN_Z), new VertexInfo(FaceInfo.Extent.MAX_X, FaceInfo.Extent.MIN_Y, FaceInfo.Extent.MIN_Z), new VertexInfo(FaceInfo.Extent.MIN_X, FaceInfo.Extent.MIN_Y, FaceInfo.Extent.MIN_Z), new VertexInfo(FaceInfo.Extent.MIN_X, FaceInfo.Extent.MAX_Y, FaceInfo.Extent.MIN_Z)}),
   SOUTH(new VertexInfo[]{new VertexInfo(FaceInfo.Extent.MIN_X, FaceInfo.Extent.MAX_Y, FaceInfo.Extent.MAX_Z), new VertexInfo(FaceInfo.Extent.MIN_X, FaceInfo.Extent.MIN_Y, FaceInfo.Extent.MAX_Z), new VertexInfo(FaceInfo.Extent.MAX_X, FaceInfo.Extent.MIN_Y, FaceInfo.Extent.MAX_Z), new VertexInfo(FaceInfo.Extent.MAX_X, FaceInfo.Extent.MAX_Y, FaceInfo.Extent.MAX_Z)}),
   WEST(new VertexInfo[]{new VertexInfo(FaceInfo.Extent.MIN_X, FaceInfo.Extent.MAX_Y, FaceInfo.Extent.MIN_Z), new VertexInfo(FaceInfo.Extent.MIN_X, FaceInfo.Extent.MIN_Y, FaceInfo.Extent.MIN_Z), new VertexInfo(FaceInfo.Extent.MIN_X, FaceInfo.Extent.MIN_Y, FaceInfo.Extent.MAX_Z), new VertexInfo(FaceInfo.Extent.MIN_X, FaceInfo.Extent.MAX_Y, FaceInfo.Extent.MAX_Z)}),
   EAST(new VertexInfo[]{new VertexInfo(FaceInfo.Extent.MAX_X, FaceInfo.Extent.MAX_Y, FaceInfo.Extent.MAX_Z), new VertexInfo(FaceInfo.Extent.MAX_X, FaceInfo.Extent.MIN_Y, FaceInfo.Extent.MAX_Z), new VertexInfo(FaceInfo.Extent.MAX_X, FaceInfo.Extent.MIN_Y, FaceInfo.Extent.MIN_Z), new VertexInfo(FaceInfo.Extent.MAX_X, FaceInfo.Extent.MAX_Y, FaceInfo.Extent.MIN_Z)});

   private static final Map<Direction, FaceInfo> BY_FACING = (Map)Util.make(new EnumMap(Direction.class), (var0) -> {
      var0.put(Direction.DOWN, DOWN);
      var0.put(Direction.UP, UP);
      var0.put(Direction.NORTH, NORTH);
      var0.put(Direction.SOUTH, SOUTH);
      var0.put(Direction.WEST, WEST);
      var0.put(Direction.EAST, EAST);
   });
   private final VertexInfo[] infos;

   public static FaceInfo fromFacing(Direction var0) {
      return (FaceInfo)BY_FACING.get(var0);
   }

   private FaceInfo(final VertexInfo... var3) {
      this.infos = var3;
   }

   public VertexInfo getVertexInfo(int var1) {
      return this.infos[var1];
   }

   // $FF: synthetic method
   private static FaceInfo[] $values() {
      return new FaceInfo[]{DOWN, UP, NORTH, SOUTH, WEST, EAST};
   }

   public static enum Extent {
      MIN_X,
      MIN_Y,
      MIN_Z,
      MAX_X,
      MAX_Y,
      MAX_Z;

      private Extent() {
      }

      public float select(Vector3fc var1, Vector3fc var2) {
         float var10000;
         switch (this.ordinal()) {
            case 0 -> var10000 = var1.x();
            case 1 -> var10000 = var1.y();
            case 2 -> var10000 = var1.z();
            case 3 -> var10000 = var2.x();
            case 4 -> var10000 = var2.y();
            case 5 -> var10000 = var2.z();
            default -> throw new MatchException((String)null, (Throwable)null);
         }

         return var10000;
      }

      public float select(float var1, float var2, float var3, float var4, float var5, float var6) {
         float var10000;
         switch (this.ordinal()) {
            case 0 -> var10000 = var1;
            case 1 -> var10000 = var2;
            case 2 -> var10000 = var3;
            case 3 -> var10000 = var4;
            case 4 -> var10000 = var5;
            case 5 -> var10000 = var6;
            default -> throw new MatchException((String)null, (Throwable)null);
         }

         return var10000;
      }

      // $FF: synthetic method
      private static Extent[] $values() {
         return new Extent[]{MIN_X, MIN_Y, MIN_Z, MAX_X, MAX_Y, MAX_Z};
      }
   }

   public static record VertexInfo(Extent xFace, Extent yFace, Extent zFace) {
      public VertexInfo(Extent var1, Extent var2, Extent var3) {
         super();
         this.xFace = var1;
         this.yFace = var2;
         this.zFace = var3;
      }

      public Vector3f select(Vector3fc var1, Vector3fc var2) {
         return new Vector3f(this.xFace.select(var1, var2), this.yFace.select(var1, var2), this.zFace.select(var1, var2));
      }
   }
}
