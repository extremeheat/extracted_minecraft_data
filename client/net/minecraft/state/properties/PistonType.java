package net.minecraft.state.properties;

import net.minecraft.util.IStringSerializable;

public enum PistonType implements IStringSerializable {
   DEFAULT("normal"),
   STICKY("sticky");

   private final String field_176714_c;

   private PistonType(String var3) {
      this.field_176714_c = var3;
   }

   public String toString() {
      return this.field_176714_c;
   }

   public String func_176610_l() {
      return this.field_176714_c;
   }
}
