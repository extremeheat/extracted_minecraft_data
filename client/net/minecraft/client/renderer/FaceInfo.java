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

   private static final Map<Direction, FaceInfo> BY_FACING = (Map)Util.make(new EnumMap(Direction.class), (map) -> {
      map.put(Direction.DOWN, DOWN);
      map.put(Direction.UP, UP);
      map.put(Direction.NORTH, NORTH);
      map.put(Direction.SOUTH, SOUTH);
      map.put(Direction.WEST, WEST);
      map.put(Direction.EAST, EAST);
   });
   private final VertexInfo[] infos;

   public static FaceInfo fromFacing(final Direction direction) {
      return (FaceInfo)BY_FACING.get(direction);
   }

   private FaceInfo(final VertexInfo... infos) {
      this.infos = infos;
   }

   public VertexInfo getVertexInfo(final int index) {
      return this.infos[index];
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

      public float select(final Vector3fc min, final Vector3fc max) {
         float var10000;
         switch (this.ordinal()) {
            case 0 -> var10000 = min.x();
            case 1 -> var10000 = min.y();
            case 2 -> var10000 = min.z();
            case 3 -> var10000 = max.x();
            case 4 -> var10000 = max.y();
            case 5 -> var10000 = max.z();
            default -> throw new MatchException((String)null, (Throwable)null);
         }

         return var10000;
      }

      public float select(final float minX, final float minY, final float minZ, final float maxX, final float maxY, final float maxZ) {
         float var10000;
         switch (this.ordinal()) {
            case 0 -> var10000 = minX;
            case 1 -> var10000 = minY;
            case 2 -> var10000 = minZ;
            case 3 -> var10000 = maxX;
            case 4 -> var10000 = maxY;
            case 5 -> var10000 = maxZ;
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
      public VertexInfo {
         super();
      }

      public Vector3f select(final Vector3fc min, final Vector3fc max) {
         return new Vector3f(this.xFace.select(min, max), this.yFace.select(min, max), this.zFace.select(min, max));
      }
   }
}
