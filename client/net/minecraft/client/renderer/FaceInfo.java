package net.minecraft.client.renderer;

import net.minecraft.Util;
import net.minecraft.core.Direction;

public enum FaceInfo {
   DOWN(new VertexInfo[]{new VertexInfo(FaceInfo.Constants.MIN_X, FaceInfo.Constants.MIN_Y, FaceInfo.Constants.MAX_Z), new VertexInfo(FaceInfo.Constants.MIN_X, FaceInfo.Constants.MIN_Y, FaceInfo.Constants.MIN_Z), new VertexInfo(FaceInfo.Constants.MAX_X, FaceInfo.Constants.MIN_Y, FaceInfo.Constants.MIN_Z), new VertexInfo(FaceInfo.Constants.MAX_X, FaceInfo.Constants.MIN_Y, FaceInfo.Constants.MAX_Z)}),
   UP(new VertexInfo[]{new VertexInfo(FaceInfo.Constants.MIN_X, FaceInfo.Constants.MAX_Y, FaceInfo.Constants.MIN_Z), new VertexInfo(FaceInfo.Constants.MIN_X, FaceInfo.Constants.MAX_Y, FaceInfo.Constants.MAX_Z), new VertexInfo(FaceInfo.Constants.MAX_X, FaceInfo.Constants.MAX_Y, FaceInfo.Constants.MAX_Z), new VertexInfo(FaceInfo.Constants.MAX_X, FaceInfo.Constants.MAX_Y, FaceInfo.Constants.MIN_Z)}),
   NORTH(new VertexInfo[]{new VertexInfo(FaceInfo.Constants.MAX_X, FaceInfo.Constants.MAX_Y, FaceInfo.Constants.MIN_Z), new VertexInfo(FaceInfo.Constants.MAX_X, FaceInfo.Constants.MIN_Y, FaceInfo.Constants.MIN_Z), new VertexInfo(FaceInfo.Constants.MIN_X, FaceInfo.Constants.MIN_Y, FaceInfo.Constants.MIN_Z), new VertexInfo(FaceInfo.Constants.MIN_X, FaceInfo.Constants.MAX_Y, FaceInfo.Constants.MIN_Z)}),
   SOUTH(new VertexInfo[]{new VertexInfo(FaceInfo.Constants.MIN_X, FaceInfo.Constants.MAX_Y, FaceInfo.Constants.MAX_Z), new VertexInfo(FaceInfo.Constants.MIN_X, FaceInfo.Constants.MIN_Y, FaceInfo.Constants.MAX_Z), new VertexInfo(FaceInfo.Constants.MAX_X, FaceInfo.Constants.MIN_Y, FaceInfo.Constants.MAX_Z), new VertexInfo(FaceInfo.Constants.MAX_X, FaceInfo.Constants.MAX_Y, FaceInfo.Constants.MAX_Z)}),
   WEST(new VertexInfo[]{new VertexInfo(FaceInfo.Constants.MIN_X, FaceInfo.Constants.MAX_Y, FaceInfo.Constants.MIN_Z), new VertexInfo(FaceInfo.Constants.MIN_X, FaceInfo.Constants.MIN_Y, FaceInfo.Constants.MIN_Z), new VertexInfo(FaceInfo.Constants.MIN_X, FaceInfo.Constants.MIN_Y, FaceInfo.Constants.MAX_Z), new VertexInfo(FaceInfo.Constants.MIN_X, FaceInfo.Constants.MAX_Y, FaceInfo.Constants.MAX_Z)}),
   EAST(new VertexInfo[]{new VertexInfo(FaceInfo.Constants.MAX_X, FaceInfo.Constants.MAX_Y, FaceInfo.Constants.MAX_Z), new VertexInfo(FaceInfo.Constants.MAX_X, FaceInfo.Constants.MIN_Y, FaceInfo.Constants.MAX_Z), new VertexInfo(FaceInfo.Constants.MAX_X, FaceInfo.Constants.MIN_Y, FaceInfo.Constants.MIN_Z), new VertexInfo(FaceInfo.Constants.MAX_X, FaceInfo.Constants.MAX_Y, FaceInfo.Constants.MIN_Z)});

   private static final FaceInfo[] BY_FACING = (FaceInfo[])Util.make(new FaceInfo[6], (var0) -> {
      var0[FaceInfo.Constants.MIN_Y] = DOWN;
      var0[FaceInfo.Constants.MAX_Y] = UP;
      var0[FaceInfo.Constants.MIN_Z] = NORTH;
      var0[FaceInfo.Constants.MAX_Z] = SOUTH;
      var0[FaceInfo.Constants.MIN_X] = WEST;
      var0[FaceInfo.Constants.MAX_X] = EAST;
   });
   private final VertexInfo[] infos;

   public static FaceInfo fromFacing(Direction var0) {
      return BY_FACING[var0.get3DDataValue()];
   }

   private FaceInfo(VertexInfo... var3) {
      this.infos = var3;
   }

   public VertexInfo getVertexInfo(int var1) {
      return this.infos[var1];
   }

   // $FF: synthetic method
   private static FaceInfo[] $values() {
      return new FaceInfo[]{DOWN, UP, NORTH, SOUTH, WEST, EAST};
   }

   public static class VertexInfo {
      public final int xFace;
      public final int yFace;
      public final int zFace;

      VertexInfo(int var1, int var2, int var3) {
         super();
         this.xFace = var1;
         this.yFace = var2;
         this.zFace = var3;
      }
   }

   public static final class Constants {
      public static final int MAX_Z;
      public static final int MAX_Y;
      public static final int MAX_X;
      public static final int MIN_Z;
      public static final int MIN_Y;
      public static final int MIN_X;

      public Constants() {
         super();
      }

      static {
         MAX_Z = Direction.SOUTH.get3DDataValue();
         MAX_Y = Direction.UP.get3DDataValue();
         MAX_X = Direction.EAST.get3DDataValue();
         MIN_Z = Direction.NORTH.get3DDataValue();
         MIN_Y = Direction.DOWN.get3DDataValue();
         MIN_X = Direction.WEST.get3DDataValue();
      }
   }
}
