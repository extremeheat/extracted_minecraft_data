package net.minecraft.state.properties;

import net.minecraft.util.IStringSerializable;

public enum ChestType implements IStringSerializable {
   SINGLE("single", 0),
   LEFT("left", 2),
   RIGHT("right", 1);

   public static final ChestType[] field_196020_d = values();
   private final String field_196021_e;
   private final int field_196022_f;

   private ChestType(String var3, int var4) {
      this.field_196021_e = var3;
      this.field_196022_f = var4;
   }

   public String func_176610_l() {
      return this.field_196021_e;
   }

   public ChestType func_208081_a() {
      return field_196020_d[this.field_196022_f];
   }
}
