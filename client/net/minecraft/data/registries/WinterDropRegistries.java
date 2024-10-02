package net.minecraft.data.registries;

import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.WinterDropBiomes;
import net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterLists;

public class WinterDropRegistries {
   private static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
      .add(Registries.BIOME, WinterDropBiomes::bootstrap)
      .add(Registries.MULTI_NOISE_BIOME_SOURCE_PARAMETER_LIST, MultiNoiseBiomeSourceParameterLists::winterDrop);

   public WinterDropRegistries() {
      super();
   }

   public static CompletableFuture<RegistrySetBuilder.PatchedRegistries> createLookup(CompletableFuture<HolderLookup.Provider> var0) {
      return RegistryPatchGenerator.createLookup(var0, BUILDER)
         .thenApply(
            var0x -> {
               VanillaRegistries.validateThatAllBiomeFeaturesHaveBiomeFilter(
                  var0x.full().lookupOrThrow(Registries.PLACED_FEATURE), var0x.full().lookupOrThrow(Registries.BIOME)
               );
               return (RegistrySetBuilder.PatchedRegistries)var0x;
            }
         );
   }
}
