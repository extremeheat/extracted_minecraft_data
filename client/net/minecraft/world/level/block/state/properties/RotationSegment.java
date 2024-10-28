package net.minecraft.world.level.block.state.properties;

import java.util.Optional;
import net.minecraft.core.Direction;
import net.minecraft.util.SegmentedAnglePrecision;

public class RotationSegment {
   private static final SegmentedAnglePrecision SEGMENTED_ANGLE16 = new SegmentedAnglePrecision(4);
   private static final int MAX_SEGMENT_INDEX;
   private static final int NORTH_0 = 0;
   private static final int EAST_90 = 4;
   private static final int SOUTH_180 = 8;
   private static final int WEST_270 = 12;

   public RotationSegment() {
      super();
   }

   public static int getMaxSegmentIndex() {
      return MAX_SEGMENT_INDEX;
   }

   public static int convertToSegment(Direction var0) {
      return SEGMENTED_ANGLE16.fromDirection(var0);
   }

   public static int convertToSegment(float var0) {
      return SEGMENTED_ANGLE16.fromDegrees(var0);
   }

   public static Optional<Direction> convertToDirection(int var0) {
      Direction var10000;
      switch (var0) {
         case 0 -> var10000 = Direction.NORTH;
         case 4 -> var10000 = Direction.EAST;
         case 8 -> var10000 = Direction.SOUTH;
         case 12 -> var10000 = Direction.WEST;
         default -> var10000 = null;
      }

      Direction var1 = var10000;
      return Optional.ofNullable(var1);
   }

   public static float convertToDegrees(int var0) {
      return SEGMENTED_ANGLE16.toDegrees(var0);
   }

   static {
      MAX_SEGMENT_INDEX = SEGMENTED_ANGLE16.getMask();
   }
}
