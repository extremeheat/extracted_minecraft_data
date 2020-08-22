package net.minecraft.core;

import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.Set;

public enum Direction8 {
   NORTH(new Direction[]{Direction.NORTH}),
   NORTH_EAST(new Direction[]{Direction.NORTH, Direction.EAST}),
   EAST(new Direction[]{Direction.EAST}),
   SOUTH_EAST(new Direction[]{Direction.SOUTH, Direction.EAST}),
   SOUTH(new Direction[]{Direction.SOUTH}),
   SOUTH_WEST(new Direction[]{Direction.SOUTH, Direction.WEST}),
   WEST(new Direction[]{Direction.WEST}),
   NORTH_WEST(new Direction[]{Direction.NORTH, Direction.WEST});

   private static final int NORTH_WEST_MASK = 1 << NORTH_WEST.ordinal();
   private static final int WEST_MASK = 1 << WEST.ordinal();
   private static final int SOUTH_WEST_MASK = 1 << SOUTH_WEST.ordinal();
   private static final int SOUTH_MASK = 1 << SOUTH.ordinal();
   private static final int SOUTH_EAST_MASK = 1 << SOUTH_EAST.ordinal();
   private static final int EAST_MASK = 1 << EAST.ordinal();
   private static final int NORTH_EAST_MASK = 1 << NORTH_EAST.ordinal();
   private static final int NORTH_MASK = 1 << NORTH.ordinal();
   private final Set directions;

   private Direction8(Direction... var3) {
      this.directions = Sets.immutableEnumSet(Arrays.asList(var3));
   }

   public Set getDirections() {
      return this.directions;
   }
}
