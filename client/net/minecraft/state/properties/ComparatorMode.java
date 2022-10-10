package net.minecraft.state.properties;

import net.minecraft.util.IStringSerializable;

public enum ComparatorMode implements IStringSerializable {
   COMPARE("compare"),
   SUBTRACT("subtract");

   private final String field_177041_c;

   private ComparatorMode(String var3) {
      this.field_177041_c = var3;
   }

   public String toString() {
      return this.field_177041_c;
   }

   public String func_176610_l() {
      return this.field_177041_c;
   }
}
