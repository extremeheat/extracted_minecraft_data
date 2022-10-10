package net.minecraft.state.properties;

import net.minecraft.util.IStringSerializable;

public enum DoorHingeSide implements IStringSerializable {
   LEFT,
   RIGHT;

   private DoorHingeSide() {
   }

   public String toString() {
      return this.func_176610_l();
   }

   public String func_176610_l() {
      return this == LEFT ? "left" : "right";
   }
}
