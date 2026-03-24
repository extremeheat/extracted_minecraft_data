package net.minecraft.world.level.block.state.properties;

import net.minecraft.util.StringRepresentable;

public enum BellAttachType implements StringRepresentable {
   FLOOR("floor"),
   CEILING("ceiling"),
   SINGLE_WALL("single_wall"),
   DOUBLE_WALL("double_wall");

   private final String name;

   private BellAttachType(final String name) {
      this.name = name;
   }

   public String getSerializedName() {
      return this.name;
   }

   // $FF: synthetic method
   private static BellAttachType[] $values() {
      return new BellAttachType[]{FLOOR, CEILING, SINGLE_WALL, DOUBLE_WALL};
   }
}
