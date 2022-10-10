package net.minecraft.state.properties;

import net.minecraft.util.IStringSerializable;

public enum DoubleBlockHalf implements IStringSerializable {
   UPPER,
   LOWER;

   private DoubleBlockHalf() {
   }

   public String toString() {
      return this.func_176610_l();
   }

   public String func_176610_l() {
      return this == UPPER ? "upper" : "lower";
   }
}
