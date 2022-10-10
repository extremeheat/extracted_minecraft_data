package net.minecraft.state.properties;

import net.minecraft.util.IStringSerializable;

public enum SlabType implements IStringSerializable {
   TOP("top"),
   BOTTOM("bottom"),
   DOUBLE("double");

   private final String field_196049_d;

   private SlabType(String var3) {
      this.field_196049_d = var3;
   }

   public String toString() {
      return this.field_196049_d;
   }

   public String func_176610_l() {
      return this.field_196049_d;
   }
}
