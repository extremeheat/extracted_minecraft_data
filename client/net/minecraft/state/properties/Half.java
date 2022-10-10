package net.minecraft.state.properties;

import net.minecraft.util.IStringSerializable;

public enum Half implements IStringSerializable {
   TOP("top"),
   BOTTOM("bottom");

   private final String field_212249_f;

   private Half(String var3) {
      this.field_212249_f = var3;
   }

   public String toString() {
      return this.field_212249_f;
   }

   public String func_176610_l() {
      return this.field_212249_f;
   }
}
