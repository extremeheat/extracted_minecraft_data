package net.minecraft.world.level.levelgen.presets;

import java.util.Map;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.FixedBiomeSource;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import net.minecraft.world.level.biome.TheEndBiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.DebugLevelSource;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

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

   public static Holder<WorldPreset> bootstrap(Registry<WorldPreset> var0) {
      return (new Bootstrap(var0)).run();
   }

   private static ResourceKey<WorldPreset> register(String var0) {
      return ResourceKey.create(Registry.WORLD_PRESET_REGISTRY, new ResourceLocation(var0));
   }

   public static Optional<ResourceKey<WorldPreset>> fromSettings(WorldGenSettings var0) {
      ChunkGenerator var1 = var0.overworld();
      if (var1 instanceof FlatLevelSource) {
         return Optional.of(FLAT);
      } else {
         return var1 instanceof DebugLevelSource ? Optional.of(DEBUG) : Optional.empty();
      }
   }

   public static WorldGenSettings createNormalWorldFromPreset(RegistryAccess var0, long var1, boolean var3, boolean var4) {
      return ((WorldPreset)var0.registryOrThrow(Registry.WORLD_PRESET_REGISTRY).getHolderOrThrow(NORMAL).value()).createWorldGenSettings(var1, var3, var4);
   }

   public static WorldGenSettings createNormalWorldFromPreset(RegistryAccess var0, long var1) {
      return createNormalWorldFromPreset(var0, var1, true, false);
   }

   public static WorldGenSettings createNormalWorldFromPreset(RegistryAccess var0) {
      return createNormalWorldFromPreset(var0, RandomSource.create().nextLong());
   }

   public static WorldGenSettings demoSettings(RegistryAccess var0) {
      return createNormalWorldFromPreset(var0, (long)"North Carolina".hashCode(), true, true);
   }

   public static LevelStem getNormalOverworld(RegistryAccess var0) {
      return ((WorldPreset)var0.registryOrThrow(Registry.WORLD_PRESET_REGISTRY).getHolderOrThrow(NORMAL).value()).overworldOrThrow();
   }

   private static class Bootstrap {
      private final Registry<WorldPreset> presets;
      private final Registry<DimensionType> dimensionTypes;
      private final Registry<Biome> biomes;
      private final Registry<StructureSet> structureSets;
      private final Registry<NoiseGeneratorSettings> noiseSettings;
      private final Registry<NormalNoise.NoiseParameters> noises;
      private final Holder<DimensionType> overworldDimensionType;
      private final Holder<DimensionType> netherDimensionType;
      private final Holder<NoiseGeneratorSettings> netherNoiseSettings;
      private final LevelStem netherStem;
      private final Holder<DimensionType> endDimensionType;
      private final Holder<NoiseGeneratorSettings> endNoiseSettings;
      private final LevelStem endStem;

      Bootstrap(Registry<WorldPreset> var1) {
         super();
         this.dimensionTypes = BuiltinRegistries.DIMENSION_TYPE;
         this.biomes = BuiltinRegistries.BIOME;
         this.structureSets = BuiltinRegistries.STRUCTURE_SETS;
         this.noiseSettings = BuiltinRegistries.NOISE_GENERATOR_SETTINGS;
         this.noises = BuiltinRegistries.NOISE;
         this.overworldDimensionType = this.dimensionTypes.getOrCreateHolderOrThrow(BuiltinDimensionTypes.OVERWORLD);
         this.netherDimensionType = this.dimensionTypes.getOrCreateHolderOrThrow(BuiltinDimensionTypes.NETHER);
         this.netherNoiseSettings = this.noiseSettings.getOrCreateHolderOrThrow(NoiseGeneratorSettings.NETHER);
         this.netherStem = new LevelStem(this.netherDimensionType, new NoiseBasedChunkGenerator(this.structureSets, this.noises, MultiNoiseBiomeSource.Preset.NETHER.biomeSource(this.biomes), this.netherNoiseSettings));
         this.endDimensionType = this.dimensionTypes.getOrCreateHolderOrThrow(BuiltinDimensionTypes.END);
         this.endNoiseSettings = this.noiseSettings.getOrCreateHolderOrThrow(NoiseGeneratorSettings.END);
         this.endStem = new LevelStem(this.endDimensionType, new NoiseBasedChunkGenerator(this.structureSets, this.noises, new TheEndBiomeSource(this.biomes), this.endNoiseSettings));
         this.presets = var1;
      }

      private LevelStem makeOverworld(ChunkGenerator var1) {
         return new LevelStem(this.overworldDimensionType, var1);
      }

      private LevelStem makeNoiseBasedOverworld(BiomeSource var1, Holder<NoiseGeneratorSettings> var2) {
         return this.makeOverworld(new NoiseBasedChunkGenerator(this.structureSets, this.noises, var1, var2));
      }

      private WorldPreset createPresetWithCustomOverworld(LevelStem var1) {
         return new WorldPreset(Map.of(LevelStem.OVERWORLD, var1, LevelStem.NETHER, this.netherStem, LevelStem.END, this.endStem));
      }

      private Holder<WorldPreset> registerCustomOverworldPreset(ResourceKey<WorldPreset> var1, LevelStem var2) {
         return BuiltinRegistries.register(this.presets, (ResourceKey)var1, this.createPresetWithCustomOverworld(var2));
      }

      public Holder<WorldPreset> run() {
         MultiNoiseBiomeSource var1 = MultiNoiseBiomeSource.Preset.OVERWORLD.biomeSource(this.biomes);
         Holder var2 = this.noiseSettings.getOrCreateHolderOrThrow(NoiseGeneratorSettings.OVERWORLD);
         this.registerCustomOverworldPreset(WorldPresets.NORMAL, this.makeNoiseBasedOverworld(var1, var2));
         Holder var3 = this.noiseSettings.getOrCreateHolderOrThrow(NoiseGeneratorSettings.LARGE_BIOMES);
         this.registerCustomOverworldPreset(WorldPresets.LARGE_BIOMES, this.makeNoiseBasedOverworld(var1, var3));
         Holder var4 = this.noiseSettings.getOrCreateHolderOrThrow(NoiseGeneratorSettings.AMPLIFIED);
         this.registerCustomOverworldPreset(WorldPresets.AMPLIFIED, this.makeNoiseBasedOverworld(var1, var4));
         this.registerCustomOverworldPreset(WorldPresets.SINGLE_BIOME_SURFACE, this.makeNoiseBasedOverworld(new FixedBiomeSource(this.biomes.getOrCreateHolderOrThrow(Biomes.PLAINS)), var2));
         this.registerCustomOverworldPreset(WorldPresets.FLAT, this.makeOverworld(new FlatLevelSource(this.structureSets, FlatLevelGeneratorSettings.getDefault(this.biomes, this.structureSets))));
         return this.registerCustomOverworldPreset(WorldPresets.DEBUG, this.makeOverworld(new DebugLevelSource(this.structureSets, this.biomes)));
      }
   }
}
