package net.minecraft.state.properties;

import net.minecraft.util.IStringSerializable;

public enum RedstoneSide implements IStringSerializable {
   UP("up"),
   SIDE("side"),
   NONE("none");

   private final String field_176820_d;

   private RedstoneSide(String var3) {
      this.field_176820_d = var3;
   }

   public String toString() {
      return this.func_176610_l();
   }

   public String func_176610_l() {
      return this.field_176820_d;
   }
}
