package net.minecraft.world.level.block.state.properties;

import net.minecraft.util.StringRepresentable;

public enum CreakingHeartState implements StringRepresentable {
   UPROOTED("uprooted"),
   DORMANT("dormant"),
   AWAKE("awake");

   private final String name;

   private CreakingHeartState(final String name) {
      this.name = name;
   }

   public String toString() {
      return this.name;
   }

   public String getSerializedName() {
      return this.name;
   }

   // $FF: synthetic method
   private static CreakingHeartState[] $values() {
      return new CreakingHeartState[]{UPROOTED, DORMANT, AWAKE};
   }
}
