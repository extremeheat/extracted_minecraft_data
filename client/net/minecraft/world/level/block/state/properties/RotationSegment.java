package net.minecraft.world.level.block.state.properties;

import java.util.Optional;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;

public class RotationSegment {
   private static final int MAX_SEGMENT_INDEX = 15;
   private static final int NORTH_0 = 0;
   private static final int EAST_90 = 4;
   private static final int SOUTH_180 = 8;
   private static final int WEST_270 = 12;

   public RotationSegment() {
      super();
   }

   public static int getMaxSegmentIndex() {
      return 15;
   }

   public static int convertToSegment(Direction var0) {
      return var0.getAxis().isVertical() ? 0 : var0.getOpposite().get2DDataValue() * 4;
   }

   public static int convertToSegment(float var0) {
      return Mth.floor((double)((180.0F + var0) * 16.0F / 360.0F) + 0.5) & 15;
   }

   public static Optional<Direction> convertToDirection(int var0) {
      Direction var1 = switch(var0) {
         case 0 -> Direction.NORTH;
         case 4 -> Direction.EAST;
         case 8 -> Direction.SOUTH;
         case 12 -> Direction.WEST;
         default -> null;
      };
      return Optional.ofNullable(var1);
   }

   public static float convertToDegrees(int var0) {
      return (float)var0 * 22.5F;
   }
}
