package net.minecraft.client.gui.screens.worldselection;

import java.util.Map;
import java.util.Optional;
import net.minecraft.client.gui.screens.CreateBuffetWorldScreen;
import net.minecraft.client.gui.screens.CreateFlatWorldScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.FixedBiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import net.minecraft.world.level.levelgen.presets.WorldPresets;

public interface PresetEditor {
   Map<Optional<ResourceKey<WorldPreset>>, PresetEditor> EDITORS = Map.of(Optional.of(WorldPresets.FLAT), (var0, var1) -> {
      ChunkGenerator var2 = var1.selectedDimensions().overworld();
      RegistryAccess.Frozen var3 = var1.worldgenLoadContext();
      HolderLookup.RegistryLookup var4 = var3.lookupOrThrow(Registries.BIOME);
      HolderLookup.RegistryLookup var5 = var3.lookupOrThrow(Registries.STRUCTURE_SET);
      HolderLookup.RegistryLookup var6 = var3.lookupOrThrow(Registries.PLACED_FEATURE);
      return new CreateFlatWorldScreen(var0, (var1x) -> {
         var0.getUiState().updateDimensions(flatWorldConfigurator(var1x));
      }, var2 instanceof FlatLevelSource ? ((FlatLevelSource)var2).settings() : FlatLevelGeneratorSettings.getDefault(var4, var5, var6));
   }, Optional.of(WorldPresets.SINGLE_BIOME_SURFACE), (var0, var1) -> {
      return new CreateBuffetWorldScreen(var0, var1, (var1x) -> {
         var0.getUiState().updateDimensions(fixedBiomeConfigurator(var1x));
      });
   });

   Screen createEditScreen(CreateWorldScreen var1, WorldCreationContext var2);

   private static WorldCreationContext.DimensionsUpdater flatWorldConfigurator(FlatLevelGeneratorSettings var0) {
      return (var1, var2) -> {
         FlatLevelSource var3 = new FlatLevelSource(var0);
         return var2.replaceOverworldGenerator(var1, var3);
      };
   }

   private static WorldCreationContext.DimensionsUpdater fixedBiomeConfigurator(Holder<Biome> var0) {
      return (var1, var2) -> {
         Registry var3 = var1.registryOrThrow(Registries.NOISE_SETTINGS);
         Holder.Reference var4 = var3.getHolderOrThrow(NoiseGeneratorSettings.OVERWORLD);
         FixedBiomeSource var5 = new FixedBiomeSource(var0);
         NoiseBasedChunkGenerator var6 = new NoiseBasedChunkGenerator(var5, var4);
         return var2.replaceOverworldGenerator(var1, var6);
      };
   }
}
