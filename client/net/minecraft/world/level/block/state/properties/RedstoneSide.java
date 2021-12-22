package net.minecraft.world.level.block.state.properties;

import net.minecraft.util.StringRepresentable;

public enum RedstoneSide implements StringRepresentable {
   // $FF: renamed from: UP net.minecraft.world.level.block.state.properties.RedstoneSide
   field_318("up"),
   SIDE("side"),
   NONE("none");

   private final String name;

   private RedstoneSide(String var3) {
      this.name = var3;
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
      return new RedstoneSide[]{field_318, SIDE, NONE};
   }
}
