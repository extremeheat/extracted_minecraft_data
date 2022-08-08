package net.minecraft.client.gui.screens.worldselection;

import java.util.Map;
import java.util.Optional;
import net.minecraft.client.gui.screens.CreateBuffetWorldScreen;
import net.minecraft.client.gui.screens.CreateFlatWorldScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.FixedBiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import net.minecraft.world.level.levelgen.presets.WorldPresets;

public interface PresetEditor {
   Map<Optional<ResourceKey<WorldPreset>>, PresetEditor> EDITORS = Map.of(Optional.of(WorldPresets.FLAT), (var0, var1) -> {
      ChunkGenerator var2 = var1.worldGenSettings().overworld();
      RegistryAccess.Frozen var3 = var1.registryAccess();
      Registry var4 = var3.registryOrThrow(Registry.BIOME_REGISTRY);
      Registry var5 = var3.registryOrThrow(Registry.STRUCTURE_SET_REGISTRY);
      return new CreateFlatWorldScreen(var0, (var1x) -> {
         var0.worldGenSettingsComponent.updateSettings(flatWorldConfigurator(var1x));
      }, var2 instanceof FlatLevelSource ? ((FlatLevelSource)var2).settings() : FlatLevelGeneratorSettings.getDefault(var4, var5));
   }, Optional.of(WorldPresets.SINGLE_BIOME_SURFACE), (var0, var1) -> {
      return new CreateBuffetWorldScreen(var0, var1, (var1x) -> {
         var0.worldGenSettingsComponent.updateSettings(fixedBiomeConfigurator(var1x));
      });
   });

   Screen createEditScreen(CreateWorldScreen var1, WorldCreationContext var2);

   private static WorldCreationContext.Updater flatWorldConfigurator(FlatLevelGeneratorSettings var0) {
      return (var1, var2) -> {
         Registry var3 = var1.registryOrThrow(Registry.STRUCTURE_SET_REGISTRY);
         FlatLevelSource var4 = new FlatLevelSource(var3, var0);
         return WorldGenSettings.replaceOverworldGenerator(var1, var2, var4);
      };
   }

   private static WorldCreationContext.Updater fixedBiomeConfigurator(Holder<Biome> var0) {
      return (var1, var2) -> {
         Registry var3 = var1.registryOrThrow(Registry.STRUCTURE_SET_REGISTRY);
         Registry var4 = var1.registryOrThrow(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY);
         Registry var5 = var1.registryOrThrow(Registry.NOISE_REGISTRY);
         Holder var6 = var4.getOrCreateHolderOrThrow(NoiseGeneratorSettings.OVERWORLD);
         FixedBiomeSource var7 = new FixedBiomeSource(var0);
         NoiseBasedChunkGenerator var8 = new NoiseBasedChunkGenerator(var3, var5, var7, var6);
         return WorldGenSettings.replaceOverworldGenerator(var1, var2, var8);
      };
   }
}
