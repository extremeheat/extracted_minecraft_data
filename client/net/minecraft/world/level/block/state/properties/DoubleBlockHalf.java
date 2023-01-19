package net.minecraft.world.level.block.state.properties;

import net.minecraft.util.StringRepresentable;

public enum DoubleBlockHalf implements StringRepresentable {
   UPPER,
   LOWER;

   private DoubleBlockHalf() {
   }

   @Override
   public String toString() {
      return this.getSerializedName();
   }

   @Override
   public String getSerializedName() {
      return this == UPPER ? "upper" : "lower";
   }
}