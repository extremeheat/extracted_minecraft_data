package net.minecraft.world.level.block.state.properties;

import net.minecraft.util.StringRepresentable;

public enum StructureMode implements StringRepresentable {
   SAVE("save"),
   LOAD("load"),
   CORNER("corner"),
   DATA("data");

   private final String name;

   private StructureMode(String var3) {
      this.name = var3;
   }

   public String getSerializedName() {
      return this.name;
   }
}
