package net.minecraft.world.level.block.state.properties;

import net.minecraft.util.StringRepresentable;

public enum RailShape implements StringRepresentable {
   NORTH_SOUTH("north_south"),
   EAST_WEST("east_west"),
   ASCENDING_EAST("ascending_east"),
   ASCENDING_WEST("ascending_west"),
   ASCENDING_NORTH("ascending_north"),
   ASCENDING_SOUTH("ascending_south"),
   SOUTH_EAST("south_east"),
   SOUTH_WEST("south_west"),
   NORTH_WEST("north_west"),
   NORTH_EAST("north_east");

   private final String name;

   private RailShape(String var3) {
      this.name = var3;
   }

   public String getName() {
      return this.name;
   }

   public String toString() {
      return this.name;
   }

   public boolean isAscending() {
      return this == ASCENDING_NORTH || this == ASCENDING_EAST || this == ASCENDING_SOUTH || this == ASCENDING_WEST;
   }

   public String getSerializedName() {
      return this.name;
   }

   // $FF: synthetic method
   private static RailShape[] $values() {
      return new RailShape[]{NORTH_SOUTH, EAST_WEST, ASCENDING_EAST, ASCENDING_WEST, ASCENDING_NORTH, ASCENDING_SOUTH, SOUTH_EAST, SOUTH_WEST, NORTH_WEST, NORTH_EAST};
   }
}
