package net.minecraft.world.level.block.state.properties;

import net.minecraft.util.StringRepresentable;

public enum BedPart implements StringRepresentable {
   HEAD("head"),
   FOOT("foot");

   private final String name;

   private BedPart(String var3) {
      this.name = var3;
   }

   public String toString() {
      return this.name;
   }

   public String getSerializedName() {
      return this.name;
   }

   // $FF: synthetic method
   private static BedPart[] $values() {
      return new BedPart[]{HEAD, FOOT};
   }
}
