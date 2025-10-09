package net.minecraft.world.level;

import net.minecraft.util.StringRepresentable;

public enum MoonPhase implements StringRepresentable {
   FULL_MOON(0, "full_moon"),
   WANING_GIBBOUS(1, "waning_gibbous"),
   THIRD_QUARTER(2, "third_quarter"),
   WANING_CRESCENT(3, "waning_crescent"),
   NEW_MOON(4, "new_moon"),
   WAXING_CRESCENT(5, "waxing_crescent"),
   FIRST_QUARTER(6, "first_quarter"),
   WAXING_GIBBOUS(7, "waxing_gibbous");

   public static final int COUNT = values().length;
   private final int index;
   private final String name;

   private MoonPhase(final int var3, final String var4) {
      this.index = var3;
      this.name = var4;
   }

   public int index() {
      return this.index;
   }

   public String getSerializedName() {
      return this.name;
   }

   // $FF: synthetic method
   private static MoonPhase[] $values() {
      return new MoonPhase[]{FULL_MOON, WANING_GIBBOUS, THIRD_QUARTER, WANING_CRESCENT, NEW_MOON, WAXING_CRESCENT, FIRST_QUARTER, WAXING_GIBBOUS};
   }
}
