package net.minecraft.world.level.block.state.properties;

import net.minecraft.util.StringRepresentable;

public enum PistonType implements StringRepresentable {
   DEFAULT("normal"),
   STICKY("sticky");

   private final String name;

   private PistonType(final String name) {
      this.name = name;
   }

   public String toString() {
      return this.name;
   }

   public String getSerializedName() {
      return this.name;
   }

   // $FF: synthetic method
   private static PistonType[] $values() {
      return new PistonType[]{DEFAULT, STICKY};
   }
}
