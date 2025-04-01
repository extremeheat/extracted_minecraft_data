package net.minecraft.world.level.levelgen.presets;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterList;
import net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterLists;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import net.minecraft.world.level.levelgen.HubLevelSource;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.WorldDimensions;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.mines.MineSpawnStrategy;

public class WorldPresets {
   public static final ResourceKey<WorldPreset> NORMAL = register("normal");
   public static final ResourceKey<WorldPreset> FLAT = register("flat");
   public static final ResourceKey<WorldPreset> AMPLIFIED = register("amplified");
   public static final ResourceKey<WorldPreset> SINGLE_BIOME_SURFACE = register("single_biome_surface");
   public static final ResourceKey<WorldPreset> DEBUG = register("debug_all_block_states");

   public WorldPresets() {
      super();
   }

   public static void bootstrap(BootstrapContext<WorldPreset> var0) {
      (new Bootstrap(var0)).bootstrap();
   }

   private static ResourceKey<WorldPreset> register(String var0) {
      return ResourceKey.create(Registries.WORLD_PRESET, ResourceLocation.withDefaultNamespace(var0));
   }

   public static Optional<ResourceKey<WorldPreset>> fromSettings(WorldDimensions var0) {
      return var0.get(LevelStem.OVERWORLD).flatMap((var0x) -> {
         ChunkGenerator var10000 = (ChunkGenerator)var0x.generator().get();
         Objects.requireNonNull(var10000);
         ChunkGenerator var1 = var10000;
         byte var2 = 0;
         Optional var5;
         //$FF: var2->value
         //0->net/minecraft/world/level/levelgen/FlatLevelSource
         //1->net/minecraft/world/level/levelgen/NoiseBasedChunkGenerator
         switch (var1.typeSwitch<invokedynamic>(var1, var2)) {
            case 0:
               FlatLevelSource var3 = (FlatLevelSource)var1;
               var5 = Optional.of(FLAT);
               break;
            case 1:
               NoiseBasedChunkGenerator var4 = (NoiseBasedChunkGenerator)var1;
               var5 = Optional.of(NORMAL);
               break;
            default:
               var5 = Optional.empty();
         }

         return var5;
      });
   }

   public static WorldDimensions createNormalWorldDimensions(HolderLookup.Provider var0) {
      return ((WorldPreset)var0.lookupOrThrow(Registries.WORLD_PRESET).getOrThrow(NORMAL).value()).createWorldDimensions();
   }

   public static LevelStem getNormalOverworld(HolderLookup.Provider var0) {
      return (LevelStem)((WorldPreset)var0.lookupOrThrow(Registries.WORLD_PRESET).getOrThrow(NORMAL).value()).overworld().orElseThrow();
   }

   public static WorldDimensions createFlatWorldDimensions(HolderLookup.Provider var0) {
      return ((WorldPreset)var0.lookupOrThrow(Registries.WORLD_PRESET).getOrThrow(FLAT).value()).createWorldDimensions();
   }

   static class Bootstrap {
      private final BootstrapContext<WorldPreset> context;
      private final HolderGetter<NoiseGeneratorSettings> noiseSettings;
      private final HolderGetter<Biome> biomes;
      private final HolderGetter<PlacedFeature> placedFeatures;
      private final HolderGetter<StructureSet> structureSets;
      private final HolderGetter<MultiNoiseBiomeSourceParameterList> multiNoiseBiomeSourceParameterLists;
      private final Holder<DimensionType> overworldDimensionType;

      Bootstrap(BootstrapContext<WorldPreset> var1) {
         super();
         this.context = var1;
         HolderGetter var2 = var1.lookup(Registries.DIMENSION_TYPE);
         this.noiseSettings = var1.<NoiseGeneratorSettings>lookup(Registries.NOISE_SETTINGS);
         this.biomes = var1.<Biome>lookup(Registries.BIOME);
         this.placedFeatures = var1.<PlacedFeature>lookup(Registries.PLACED_FEATURE);
         this.structureSets = var1.<StructureSet>lookup(Registries.STRUCTURE_SET);
         this.multiNoiseBiomeSourceParameterLists = var1.<MultiNoiseBiomeSourceParameterList>lookup(Registries.MULTI_NOISE_BIOME_SOURCE_PARAMETER_LIST);
         this.overworldDimensionType = var2.getOrThrow(BuiltinDimensionTypes.OVERWORLD);
      }

      private LevelStem makeOverworld(ChunkGenerator var1) {
         return new LevelStem(this.overworldDimensionType, Optional.of(var1), List.of(), Optional.empty(), MineSpawnStrategy.SURFACE);
      }

      private WorldPreset createPresetWithCustomOverworld(LevelStem var1) {
         return new WorldPreset(Map.of(LevelStem.OVERWORLD, var1));
      }

      private void registerCustomOverworldPreset(ResourceKey<WorldPreset> var1, LevelStem var2) {
         this.context.register(var1, this.createPresetWithCustomOverworld(var2));
      }

      private LevelStem makeNoiseBasedOverworld(BiomeSource var1, Holder<NoiseGeneratorSettings> var2) {
         return this.makeOverworld(new NoiseBasedChunkGenerator(var1, var2));
      }

      public void bootstrap() {
         LevelStem var1 = this.makeOverworld(new HubLevelSource(this.biomes.getOrThrow(Biomes.HUB)));
         this.registerCustomOverworldPreset(WorldPresets.NORMAL, var1);
         this.registerCustomOverworldPreset(WorldPresets.FLAT, this.makeOverworld(new FlatLevelSource(FlatLevelGeneratorSettings.getDefault(this.biomes, this.structureSets, this.placedFeatures))));
         Holder.Reference var2 = this.noiseSettings.getOrThrow(NoiseGeneratorSettings.AMPLIFIED);
         Holder.Reference var3 = this.multiNoiseBiomeSourceParameterLists.getOrThrow(MultiNoiseBiomeSourceParameterLists.OVERWORLD);
         this.registerCustomOverworldPreset(WorldPresets.AMPLIFIED, this.makeNoiseBasedOverworld(MultiNoiseBiomeSource.createFromPreset(var3), var2));
      }
   }
}
