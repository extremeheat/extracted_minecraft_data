package net.minecraft.core;

import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.Set;

public enum Direction8 {
   NORTH(Direction.NORTH),
   NORTH_EAST(Direction.NORTH, Direction.EAST),
   EAST(Direction.EAST),
   SOUTH_EAST(Direction.SOUTH, Direction.EAST),
   SOUTH(Direction.SOUTH),
   SOUTH_WEST(Direction.SOUTH, Direction.WEST),
   WEST(Direction.WEST),
   NORTH_WEST(Direction.NORTH, Direction.WEST);

   private final Set<Direction> directions;
   private final Vec3i step;

   private Direction8(Direction... var3) {
      this.directions = Sets.immutableEnumSet(Arrays.asList(var3));
      this.step = new Vec3i(0, 0, 0);

      for(Direction var7 : var3) {
         this.step.setX(this.step.getX() + var7.getStepX()).setY(this.step.getY() + var7.getStepY()).setZ(this.step.getZ() + var7.getStepZ());
      }
   }

   public Set<Direction> getDirections() {
      return this.directions;
   }

   public int getStepX() {
      return this.step.getX();
   }

   public int getStepZ() {
      return this.step.getZ();
   }
}
