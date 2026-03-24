package net.minecraft.world.level.block.state.properties;

import net.minecraft.util.StringRepresentable;

public enum RedstoneSide implements StringRepresentable {
   UP("up"),
   SIDE("side"),
   NONE("none");

   private final String name;

   private RedstoneSide(final String name) {
      this.name = name;
   }

   public String toString() {
      return this.getSerializedName();
   }

   public String getSerializedName() {
      return this.name;
   }

   public boolean isConnected() {
      return this != NONE;
   }

   // $FF: synthetic method
   private static RedstoneSide[] $values() {
      return new RedstoneSide[]{UP, SIDE, NONE};
   }
}
