package net.minecraft.state.properties;

import net.minecraft.util.IStringSerializable;

public enum RailShape implements IStringSerializable {
   NORTH_SOUTH(0, "north_south"),
   EAST_WEST(1, "east_west"),
   ASCENDING_EAST(2, "ascending_east"),
   ASCENDING_WEST(3, "ascending_west"),
   ASCENDING_NORTH(4, "ascending_north"),
   ASCENDING_SOUTH(5, "ascending_south"),
   SOUTH_EAST(6, "south_east"),
   SOUTH_WEST(7, "south_west"),
   NORTH_WEST(8, "north_west"),
   NORTH_EAST(9, "north_east");

   private final int field_177027_l;
   private final String field_177028_m;

   private RailShape(int var3, String var4) {
      this.field_177027_l = var3;
      this.field_177028_m = var4;
   }

   public int func_208091_a() {
      return this.field_177027_l;
   }

   public String toString() {
      return this.field_177028_m;
   }

   public boolean func_208092_c() {
      return this == ASCENDING_NORTH || this == ASCENDING_EAST || this == ASCENDING_SOUTH || this == ASCENDING_WEST;
   }

   public String func_176610_l() {
      return this.field_177028_m;
   }
}
