package net.minecraft.state.properties;

import net.minecraft.util.IStringSerializable;

public enum AttachFace implements IStringSerializable {
   FLOOR("floor"),
   WALL("wall"),
   CEILING("ceiling");

   private final String field_196027_d;

   private AttachFace(String var3) {
      this.field_196027_d = var3;
   }

   public String func_176610_l() {
      return this.field_196027_d;
   }
}
