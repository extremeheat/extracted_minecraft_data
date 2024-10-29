package net.minecraft.world.level.levelgen.presets;

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
import net.minecraft.world.level.biome.FixedBiomeSource;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterList;
import net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterLists;
import net.minecraft.world.level.biome.TheEndBiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.DebugLevelSource;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.WorldDimensions;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.StructureSet;

public class WorldPresets {
   public static final ResourceKey<WorldPreset> NORMAL = register("normal");
   public static final ResourceKey<WorldPreset> FLAT = register("flat");
   public static final ResourceKey<WorldPreset> LARGE_BIOMES = register("large_biomes");
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
         ChunkGenerator var10000 = var0x.generator();
         Objects.requireNonNull(var10000);
         ChunkGenerator var1 = var10000;
         byte var2 = 0;
         Optional var6;
         //$FF: var2->value
         //0->net/minecraft/world/level/levelgen/FlatLevelSource
         //1->net/minecraft/world/level/levelgen/DebugLevelSource
         //2->net/minecraft/world/level/levelgen/NoiseBasedChunkGenerator
         switch (var1.typeSwitch<invokedynamic>(var1, var2)) {
            case 0:
               FlatLevelSource var3 = (FlatLevelSource)var1;
               var6 = Optional.of(FLAT);
               break;
            case 1:
               DebugLevelSource var4 = (DebugLevelSource)var1;
               var6 = Optional.of(DEBUG);
               break;
            case 2:
               NoiseBasedChunkGenerator var5 = (NoiseBasedChunkGenerator)var1;
               var6 = Optional.of(NORMAL);
               break;
            default:
               var6 = Optional.empty();
         }

         return var6;
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

   private static class Bootstrap {
      private final BootstrapContext<WorldPreset> context;
      private final HolderGetter<NoiseGeneratorSettings> noiseSettings;
      private final HolderGetter<Biome> biomes;
      private final HolderGetter<PlacedFeature> placedFeatures;
      private final HolderGetter<StructureSet> structureSets;
      private final HolderGetter<MultiNoiseBiomeSourceParameterList> multiNoiseBiomeSourceParameterLists;
      private final Holder<DimensionType> overworldDimensionType;
      private final LevelStem netherStem;
      private final LevelStem endStem;

      Bootstrap(BootstrapContext<WorldPreset> var1) {
         super();
         this.context = var1;
         HolderGetter var2 = var1.lookup(Registries.DIMENSION_TYPE);
         this.noiseSettings = var1.lookup(Registries.NOISE_SETTINGS);
         this.biomes = var1.lookup(Registries.BIOME);
         this.placedFeatures = var1.lookup(Registries.PLACED_FEATURE);
         this.structureSets = var1.lookup(Registries.STRUCTURE_SET);
         this.multiNoiseBiomeSourceParameterLists = var1.lookup(Registries.MULTI_NOISE_BIOME_SOURCE_PARAMETER_LIST);
         this.overworldDimensionType = var2.getOrThrow(BuiltinDimensionTypes.OVERWORLD);
         Holder.Reference var3 = var2.getOrThrow(BuiltinDimensionTypes.NETHER);
         Holder.Reference var4 = this.noiseSettings.getOrThrow(NoiseGeneratorSettings.NETHER);
         Holder.Reference var5 = this.multiNoiseBiomeSourceParameterLists.getOrThrow(MultiNoiseBiomeSourceParameterLists.NETHER);
         this.netherStem = new LevelStem(var3, new NoiseBasedChunkGenerator(MultiNoiseBiomeSource.createFromPreset(var5), var4));
         Holder.Reference var6 = var2.getOrThrow(BuiltinDimensionTypes.END);
         Holder.Reference var7 = this.noiseSettings.getOrThrow(NoiseGeneratorSettings.END);
         this.endStem = new LevelStem(var6, new NoiseBasedChunkGenerator(TheEndBiomeSource.create(this.biomes), var7));
      }

      private LevelStem makeOverworld(ChunkGenerator var1) {
         return new LevelStem(this.overworldDimensionType, var1);
      }

      private LevelStem makeNoiseBasedOverworld(BiomeSource var1, Holder<NoiseGeneratorSettings> var2) {
         return this.makeOverworld(new NoiseBasedChunkGenerator(var1, var2));
      }

      private WorldPreset createPresetWithCustomOverworld(LevelStem var1) {
         return new WorldPreset(Map.of(LevelStem.OVERWORLD, var1, LevelStem.NETHER, this.netherStem, LevelStem.END, this.endStem));
      }

      private void registerCustomOverworldPreset(ResourceKey<WorldPreset> var1, LevelStem var2) {
         this.context.register(var1, this.createPresetWithCustomOverworld(var2));
      }

      private void registerOverworlds(BiomeSource var1) {
         Holder.Reference var2 = this.noiseSettings.getOrThrow(NoiseGeneratorSettings.OVERWORLD);
         this.registerCustomOverworldPreset(WorldPresets.NORMAL, this.makeNoiseBasedOverworld(var1, var2));
         Holder.Reference var3 = this.noiseSettings.getOrThrow(NoiseGeneratorSettings.LARGE_BIOMES);
         this.registerCustomOverworldPreset(WorldPresets.LARGE_BIOMES, this.makeNoiseBasedOverworld(var1, var3));
         Holder.Reference var4 = this.noiseSettings.getOrThrow(NoiseGeneratorSettings.AMPLIFIED);
         this.registerCustomOverworldPreset(WorldPresets.AMPLIFIED, this.makeNoiseBasedOverworld(var1, var4));
      }

      public void bootstrap() {
         Holder.Reference var1 = this.multiNoiseBiomeSourceParameterLists.getOrThrow(MultiNoiseBiomeSourceParameterLists.OVERWORLD);
         this.registerOverworlds(MultiNoiseBiomeSource.createFromPreset(var1));
         Holder.Reference var2 = this.noiseSettings.getOrThrow(NoiseGeneratorSettings.OVERWORLD);
         Holder.Reference var3 = this.biomes.getOrThrow(Biomes.PLAINS);
         this.registerCustomOverworldPreset(WorldPresets.SINGLE_BIOME_SURFACE, this.makeNoiseBasedOverworld(new FixedBiomeSource(var3), var2));
         this.registerCustomOverworldPreset(WorldPresets.FLAT, this.makeOverworld(new FlatLevelSource(FlatLevelGeneratorSettings.getDefault(this.biomes, this.structureSets, this.placedFeatures))));
         this.registerCustomOverworldPreset(WorldPresets.DEBUG, this.makeOverworld(new DebugLevelSource(var3)));
      }
   }
}
