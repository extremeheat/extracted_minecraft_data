package net.minecraft.world.flag;

public class FeatureFlag {
   final FeatureFlagUniverse universe;
   final long mask;

   FeatureFlag(FeatureFlagUniverse var1, int var2) {
      super();
      this.universe = var1;
      this.mask = 1L << var2;
   }
}
