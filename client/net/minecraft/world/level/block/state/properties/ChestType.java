package net.minecraft.world.level.block.state.properties;

import net.minecraft.util.StringRepresentable;

public enum ChestType implements StringRepresentable {
   SINGLE("single"),
   LEFT("left"),
   RIGHT("right");

   private final String name;

   private ChestType(String var3) {
      this.name = var3;
   }

   @Override
   public String getSerializedName() {
      return this.name;
   }

   public ChestType getOpposite() {
      return switch (this) {
         case SINGLE -> SINGLE;
         case LEFT -> RIGHT;
         case RIGHT -> LEFT;
      };
   }
}
