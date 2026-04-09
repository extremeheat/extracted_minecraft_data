package net.minecraft.world.level.block.state.properties;

import net.minecraft.util.StringRepresentable;

public enum SpeleothemThickness implements StringRepresentable {
   TIP_MERGE("tip_merge"),
   TIP("tip"),
   FRUSTUM("frustum"),
   MIDDLE("middle"),
   BASE("base");

   private final String name;

   private SpeleothemThickness(final String name) {
      this.name = name;
   }

   public String toString() {
      return this.name;
   }

   public String getSerializedName() {
      return this.name;
   }

   // $FF: synthetic method
   private static SpeleothemThickness[] $values() {
      return new SpeleothemThickness[]{TIP_MERGE, TIP, FRUSTUM, MIDDLE, BASE};
   }
}
