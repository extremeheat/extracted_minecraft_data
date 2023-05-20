package net.minecraft.data.registries;

import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.biome.BiomeData;
import net.minecraft.world.item.armortrim.TrimMaterials;
import net.minecraft.world.item.armortrim.TrimPatterns;
import net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterLists;

public class UpdateOneTwentyRegistries {
   private static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
      .add(Registries.TRIM_MATERIAL, TrimMaterials::nextUpdate)
      .add(Registries.TRIM_PATTERN, TrimPatterns::nextUpdate)
      .add(Registries.BIOME, BiomeData::nextUpdate)
      .add(Registries.MULTI_NOISE_BIOME_SOURCE_PARAMETER_LIST, MultiNoiseBiomeSourceParameterLists::nextUpdate);

   public UpdateOneTwentyRegistries() {
      super();
   }

   public static CompletableFuture<HolderLookup.Provider> createLookup(CompletableFuture<HolderLookup.Provider> var0) {
      return var0.thenApply(var0x -> {
         RegistryAccess.Frozen var1 = RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY);
         HolderLookup.Provider var2 = BUILDER.buildPatch(var1, var0x);
         VanillaRegistries.validateThatAllBiomeFeaturesHaveBiomeFilter(var0x.lookupOrThrow(Registries.PLACED_FEATURE), var2.lookupOrThrow(Registries.BIOME));
         return var2;
      });
   }
}
