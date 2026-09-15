package net.minecraft.core;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;

public record CompositeDirection(Set<Direction> directions, Vec3i step) implements Directional {
   public static final CompositeDirection NORTH;
   public static final CompositeDirection EAST;
   public static final CompositeDirection SOUTH;
   public static final CompositeDirection WEST;
   public static final CompositeDirection NORTH_EAST;
   public static final CompositeDirection SOUTH_EAST;
   public static final CompositeDirection SOUTH_WEST;
   public static final CompositeDirection NORTH_WEST;
   public static final CompositeDirection UP;
   public static final CompositeDirection NORTH_UP;
   public static final CompositeDirection EAST_UP;
   public static final CompositeDirection SOUTH_UP;
   public static final CompositeDirection WEST_UP;
   public static final CompositeDirection NORTH_EAST_UP;
   public static final CompositeDirection SOUTH_EAST_UP;
   public static final CompositeDirection SOUTH_WEST_UP;
   public static final CompositeDirection NORTH_WEST_UP;
   public static final CompositeDirection DOWN;
   public static final CompositeDirection NORTH_DOWN;
   public static final CompositeDirection EAST_DOWN;
   public static final CompositeDirection SOUTH_DOWN;
   public static final CompositeDirection WEST_DOWN;
   public static final CompositeDirection NORTH_EAST_DOWN;
   public static final CompositeDirection SOUTH_EAST_DOWN;
   public static final CompositeDirection SOUTH_WEST_DOWN;
   public static final CompositeDirection NORTH_WEST_DOWN;

   public CompositeDirection {
      super();
      if (directions.isEmpty()) {
         throw new IllegalArgumentException("Directions cannot be empty");
      }
   }

   @VisibleForTesting
   CompositeDirection(final Direction... directions) {
      Set<Direction> immutableDirections = Sets.immutableEnumSet(Arrays.asList(directions));
      int x = 0;
      int y = 0;
      int z = 0;

      for(Direction direction : immutableDirections) {
         x += direction.getStepX();
         y += direction.getStepY();
         z += direction.getStepZ();
      }

      this(immutableDirections, new Vec3i(x, y, z));
   }

   public CompositeDirection compose(final CompositeDirection other) {
      Set<Direction> newDirections = EnumSet.copyOf(this.directions);
      newDirections.addAll(other.directions);
      Vec3i step = new Vec3i(this.getStepX(), this.getStepY(), this.getStepZ());
      Vec3i additionalStep = other.getStep();
      step.setX(step.getX() + additionalStep.getX()).setY(step.getY() + additionalStep.getY()).setZ(step.getZ() + additionalStep.getZ());
      return new CompositeDirection(Sets.immutableEnumSet(newDirections), step);
   }

   public int getStepX() {
      return this.step.getX();
   }

   public int getStepY() {
      return this.step.getY();
   }

   public int getStepZ() {
      return this.step.getZ();
   }

   public Vec3i getStep() {
      return this.step;
   }

   static {
      NORTH = new CompositeDirection(new Direction[]{Direction.NORTH});
      EAST = new CompositeDirection(new Direction[]{Direction.EAST});
      SOUTH = new CompositeDirection(new Direction[]{Direction.SOUTH});
      WEST = new CompositeDirection(new Direction[]{Direction.WEST});
      NORTH_EAST = NORTH.compose(EAST);
      SOUTH_EAST = SOUTH.compose(EAST);
      SOUTH_WEST = SOUTH.compose(WEST);
      NORTH_WEST = NORTH.compose(WEST);
      UP = new CompositeDirection(new Direction[]{Direction.UP});
      NORTH_UP = NORTH.compose(UP);
      EAST_UP = EAST.compose(UP);
      SOUTH_UP = SOUTH.compose(UP);
      WEST_UP = WEST.compose(UP);
      NORTH_EAST_UP = NORTH_EAST.compose(UP);
      SOUTH_EAST_UP = SOUTH_EAST.compose(UP);
      SOUTH_WEST_UP = SOUTH_WEST.compose(UP);
      NORTH_WEST_UP = NORTH_WEST.compose(UP);
      DOWN = new CompositeDirection(new Direction[]{Direction.DOWN});
      NORTH_DOWN = NORTH.compose(DOWN);
      EAST_DOWN = EAST.compose(DOWN);
      SOUTH_DOWN = SOUTH.compose(DOWN);
      WEST_DOWN = WEST.compose(DOWN);
      NORTH_EAST_DOWN = NORTH_EAST.compose(DOWN);
      SOUTH_EAST_DOWN = SOUTH_EAST.compose(DOWN);
      SOUTH_WEST_DOWN = SOUTH_WEST.compose(DOWN);
      NORTH_WEST_DOWN = NORTH_WEST.compose(DOWN);
   }

   public static enum Direction8 implements Directional {
      NORTH(CompositeDirection.NORTH),
      NORTH_EAST(CompositeDirection.NORTH_EAST),
      EAST(CompositeDirection.EAST),
      SOUTH_EAST(CompositeDirection.SOUTH_EAST),
      SOUTH(CompositeDirection.SOUTH),
      SOUTH_WEST(CompositeDirection.SOUTH_WEST),
      WEST(CompositeDirection.WEST),
      NORTH_WEST(CompositeDirection.NORTH_WEST);

      private final CompositeDirection compositeDirection;

      private Direction8(final CompositeDirection compositeDirection) {
         this.compositeDirection = compositeDirection;
      }

      public Set<Direction> getDirections() {
         return this.compositeDirection.directions();
      }

      public int getStepX() {
         return this.compositeDirection.getStepX();
      }

      public int getStepY() {
         return this.compositeDirection.getStepY();
      }

      public int getStepZ() {
         return this.compositeDirection.getStepZ();
      }

      public Vec3i getStep() {
         return this.compositeDirection.getStep();
      }

      // $FF: synthetic method
      private static Direction8[] $values() {
         return new Direction8[]{NORTH, NORTH_EAST, EAST, SOUTH_EAST, SOUTH, SOUTH_WEST, WEST, NORTH_WEST};
      }
   }
}
