package net.minecraft.world.level.block.state.properties;

import net.minecraft.util.StringRepresentable;

public enum DoorHingeSide implements StringRepresentable {
   LEFT,
   RIGHT;

   private DoorHingeSide() {
   }

   public String toString() {
      return this.getSerializedName();
   }

   public String getSerializedName() {
      return this == LEFT ? "left" : "right";
   }

   // $FF: synthetic method
   private static DoorHingeSide[] $values() {
      return new DoorHingeSide[]{LEFT, RIGHT};
   }
}
