package net.minecraft.state.properties;

import net.minecraft.util.IStringSerializable;

public enum StructureMode implements IStringSerializable {
   SAVE("save"),
   LOAD("load"),
   CORNER("corner"),
   DATA("data");

   private final String field_185116_f;

   private StructureMode(String var3) {
      this.field_185116_f = var3;
   }

   public String func_176610_l() {
      return this.field_185116_f;
   }
}
