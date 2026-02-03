package net.minecraft.world.level.block.state.properties;

import net.minecraft.util.StringRepresentable;

public enum StairsShape implements StringRepresentable {
   STRAIGHT("straight"),
   INNER_LEFT("inner_left"),
   INNER_RIGHT("inner_right"),
   OUTER_LEFT("outer_left"),
   OUTER_RIGHT("outer_right");

   private final String name;

   private StairsShape(final String name) {
      this.name = name;
   }

   public String toString() {
      return this.name;
   }

   public String getSerializedName() {
      return this.name;
   }

   // $FF: synthetic method
   private static StairsShape[] $values() {
      return new StairsShape[]{STRAIGHT, INNER_LEFT, INNER_RIGHT, OUTER_LEFT, OUTER_RIGHT};
   }
}
