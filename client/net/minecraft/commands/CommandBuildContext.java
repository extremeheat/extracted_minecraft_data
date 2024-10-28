package net.minecraft.commands;

import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.flag.FeatureFlagSet;

public interface CommandBuildContext extends HolderLookup.Provider {
   static CommandBuildContext simple(final HolderLookup.Provider var0, final FeatureFlagSet var1) {
      return new CommandBuildContext() {
         public Stream<ResourceKey<? extends Registry<?>>> listRegistries() {
            return var0.listRegistries();
         }

         public <T> Optional<HolderLookup.RegistryLookup<T>> lookup(ResourceKey<? extends Registry<? extends T>> var1x) {
            return var0.lookup(var1x).map((var1xx) -> {
               return var1xx.filterFeatures(var1);
            });
         }
      };
   }
}
