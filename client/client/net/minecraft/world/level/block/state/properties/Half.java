package net.minecraft.world.level.block.state.properties;

import net.minecraft.util.StringRepresentable;

public enum Half implements StringRepresentable {
   TOP("top"),
   BOTTOM("bottom");

   private final String name;

   private Half(final String param3) {
      this.name = nullxx;
   }

   @Override
   public String toString() {
      return this.name;
   }

   @Override
   public String getSerializedName() {
      return this.name;
   }
}
