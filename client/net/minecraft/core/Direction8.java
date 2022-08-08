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

   private final Set<Direction> directions;
   private final Vec3i step;

   private Direction8(Direction... var3) {
      this.directions = Sets.immutableEnumSet(Arrays.asList(var3));
      this.step = new Vec3i(0, 0, 0);
      Direction[] var4 = var3;
      int var5 = var3.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         Direction var7 = var4[var6];
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

   // $FF: synthetic method
   private static Direction8[] $values() {
      return new Direction8[]{NORTH, NORTH_EAST, EAST, SOUTH_EAST, SOUTH, SOUTH_WEST, WEST, NORTH_WEST};
   }
}
