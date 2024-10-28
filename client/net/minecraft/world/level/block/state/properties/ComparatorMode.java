package net.minecraft.world.level.block.state.properties;

import net.minecraft.util.StringRepresentable;

public enum ComparatorMode implements StringRepresentable {
   COMPARE("compare"),
   SUBTRACT("subtract");

   private final String name;

   private ComparatorMode(String var3) {
      this.name = var3;
   }

   public String toString() {
      return this.name;
   }

   public String getSerializedName() {
      return this.name;
   }

   // $FF: synthetic method
   private static ComparatorMode[] $values() {
      return new ComparatorMode[]{COMPARE, SUBTRACT};
   }
}
