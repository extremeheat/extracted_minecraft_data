package net.minecraft.world.level.block.state.properties;

import net.minecraft.util.StringRepresentable;

public enum BambooLeaves implements StringRepresentable {
   NONE("none"),
   SMALL("small"),
   LARGE("large");

   private final String name;

   private BambooLeaves(String var3) {
      this.name = var3;
   }

   public String toString() {
      return this.name;
   }

   public String getSerializedName() {
      return this.name;
   }

   // $FF: synthetic method
   private static BambooLeaves[] $values() {
      return new BambooLeaves[]{NONE, SMALL, LARGE};
   }
}
