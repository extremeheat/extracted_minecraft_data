package net.minecraft.world.level.block.state.properties;

import net.minecraft.util.StringRepresentable;

public enum RedstoneSide implements StringRepresentable {
   UP("up"),
   SIDE("side"),
   NONE("none");

   private final String name;

   private RedstoneSide(final String nullxx) {
      this.name = nullxx;
   }

   @Override
   public String toString() {
      return this.getSerializedName();
   }

   @Override
   public String getSerializedName() {
      return this.name;
   }

   public boolean isConnected() {
      return this != NONE;
   }
}
