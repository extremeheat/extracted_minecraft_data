package net.minecraft.world.level.block.state.properties;

import net.minecraft.util.StringRepresentable;

public enum Tilt implements StringRepresentable {
   NONE("none", true),
   UNSTABLE("unstable", false),
   PARTIAL("partial", true),
   FULL("full", true);

   private final String name;
   private final boolean causesVibration;

   private Tilt(final String name, final boolean causesVibration) {
      this.name = name;
      this.causesVibration = causesVibration;
   }

   public String getSerializedName() {
      return this.name;
   }

   public boolean causesVibration() {
      return this.causesVibration;
   }

   // $FF: synthetic method
   private static Tilt[] $values() {
      return new Tilt[]{NONE, UNSTABLE, PARTIAL, FULL};
   }
}
