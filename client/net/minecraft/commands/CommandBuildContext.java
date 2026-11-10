package net.minecraft.commands;

import net.minecraft.world.flag.FeatureFlagSet;

public interface CommandBuildContext {
   static CommandBuildContext simple(final FeatureFlagSet enabledFeatures) {
      return new CommandBuildContext() {
         public FeatureFlagSet enabledFeatures() {
            return enabledFeatures;
         }
      };
   }

   FeatureFlagSet enabledFeatures();
}
