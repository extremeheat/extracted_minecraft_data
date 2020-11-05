package net.minecraft.client.gui.screens.worldselection;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.client.gui.screens.CreateBuffetWorldScreen;
import net.minecraft.client.gui.screens.CreateFlatWorldScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.WritableRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.FixedBiomeSource;
import net.minecraft.world.level.biome.OverworldBiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.DebugLevelSource;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;

public abstract class WorldPreset {
   public static final WorldPreset NORMAL = new WorldPreset("default") {
      protected ChunkGenerator generator(Registry<Biome> var1, Registry<NoiseGeneratorSettings> var2, long var3) {
         return new NoiseBasedChunkGenerator(new OverworldBiomeSource(var3, false, false, var1), var3, () -> {
            return (NoiseGeneratorSettings)var2.getOrThrow(NoiseGeneratorSettings.OVERWORLD);
         });
      }
   };
   private static final WorldPreset FLAT = new WorldPreset("flat") {
      protected ChunkGenerator generator(Registry<Biome> var1, Registry<NoiseGeneratorSettings> var2, long var3) {
         return new FlatLevelSource(FlatLevelGeneratorSettings.getDefault(var1));
      }
   };
   private static final WorldPreset LARGE_BIOMES = new WorldPreset("large_biomes") {
      protected ChunkGenerator generator(Registry<Biome> var1, Registry<NoiseGeneratorSettings> var2, long var3) {
         return new NoiseBasedChunkGenerator(new OverworldBiomeSource(var3, false, true, var1), var3, () -> {
            return (NoiseGeneratorSettings)var2.getOrThrow(NoiseGeneratorSettings.OVERWORLD);
         });
      }
   };
   public static final WorldPreset AMPLIFIED = new WorldPreset("amplified") {
      protected ChunkGenerator generator(Registry<Biome> var1, Registry<NoiseGeneratorSettings> var2, long var3) {
         return new NoiseBasedChunkGenerator(new OverworldBiomeSource(var3, false, false, var1), var3, () -> {
            return (NoiseGeneratorSettings)var2.getOrThrow(NoiseGeneratorSettings.AMPLIFIED);
         });
      }
   };
   private static final WorldPreset SINGLE_BIOME_SURFACE = new WorldPreset("single_biome_surface") {
      protected ChunkGenerator generator(Registry<Biome> var1, Registry<NoiseGeneratorSettings> var2, long var3) {
         return new NoiseBasedChunkGenerator(new FixedBiomeSource((Biome)var1.getOrThrow(Biomes.PLAINS)), var3, () -> {
            return (NoiseGeneratorSettings)var2.getOrThrow(NoiseGeneratorSettings.OVERWORLD);
         });
      }
   };
   private static final WorldPreset SINGLE_BIOME_CAVES = new WorldPreset("single_biome_caves") {
      public WorldGenSettings create(RegistryAccess.RegistryHolder var1, long var2, boolean var4, boolean var5) {
         WritableRegistry var6 = var1.registryOrThrow(Registry.BIOME_REGISTRY);
         WritableRegistry var7 = var1.registryOrThrow(Registry.DIMENSION_TYPE_REGISTRY);
         WritableRegistry var8 = var1.registryOrThrow(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY);
         return new WorldGenSettings(var2, var4, var5, WorldGenSettings.withOverworld(DimensionType.defaultDimensions(var7, var6, var8, var2), () -> {
            return (DimensionType)var7.getOrThrow(DimensionType.OVERWORLD_CAVES_LOCATION);
         }, this.generator(var6, var8, var2)));
      }

      protected ChunkGenerator generator(Registry<Biome> var1, Registry<NoiseGeneratorSettings> var2, long var3) {
         return new NoiseBasedChunkGenerator(new FixedBiomeSource((Biome)var1.getOrThrow(Biomes.PLAINS)), var3, () -> {
            return (NoiseGeneratorSettings)var2.getOrThrow(NoiseGeneratorSettings.CAVES);
         });
      }
   };
   private static final WorldPreset SINGLE_BIOME_FLOATING_ISLANDS = new WorldPreset("single_biome_floating_islands") {
      protected ChunkGenerator generator(Registry<Biome> var1, Registry<NoiseGeneratorSettings> var2, long var3) {
         return new NoiseBasedChunkGenerator(new FixedBiomeSource((Biome)var1.getOrThrow(Biomes.PLAINS)), var3, () -> {
            return (NoiseGeneratorSettings)var2.getOrThrow(NoiseGeneratorSettings.FLOATING_ISLANDS);
         });
      }
   };
   private static final WorldPreset DEBUG = new WorldPreset("debug_all_block_states") {
      protected ChunkGenerator generator(Registry<Biome> var1, Registry<NoiseGeneratorSettings> var2, long var3) {
         return new DebugLevelSource(var1);
      }
   };
   protected static final List<WorldPreset> PRESETS;
   protected static final Map<Optional<WorldPreset>, WorldPreset.PresetEditor> EDITORS;
   private final Component description;

   private WorldPreset(String var1) {
      super();
      this.description = new TranslatableComponent("generator." + var1);
   }

   private static WorldGenSettings fromBuffetSettings(RegistryAccess var0, WorldGenSettings var1, WorldPreset var2, Biome var3) {
      FixedBiomeSource var4 = new FixedBiomeSource(var3);
      WritableRegistry var6 = var0.registryOrThrow(Registry.DIMENSION_TYPE_REGISTRY);
      WritableRegistry var7 = var0.registryOrThrow(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY);
      Supplier var5;
      if (var2 == SINGLE_BIOME_CAVES) {
         var5 = () -> {
            return (NoiseGeneratorSettings)var7.getOrThrow(NoiseGeneratorSettings.CAVES);
         };
      } else if (var2 == SINGLE_BIOME_FLOATING_ISLANDS) {
         var5 = () -> {
            return (NoiseGeneratorSettings)var7.getOrThrow(NoiseGeneratorSettings.FLOATING_ISLANDS);
         };
      } else {
         var5 = () -> {
            return (NoiseGeneratorSettings)var7.getOrThrow(NoiseGeneratorSettings.OVERWORLD);
         };
      }

      return new WorldGenSettings(var1.seed(), var1.generateFeatures(), var1.generateBonusChest(), WorldGenSettings.withOverworld((Registry)var6, (MappedRegistry)var1.dimensions(), new NoiseBasedChunkGenerator(var4, var1.seed(), var5)));
   }

   private static Biome parseBuffetSettings(RegistryAccess var0, WorldGenSettings var1) {
      return (Biome)var1.overworld().getBiomeSource().possibleBiomes().stream().findFirst().orElse(var0.registryOrThrow(Registry.BIOME_REGISTRY).getOrThrow(Biomes.PLAINS));
   }

   public static Optional<WorldPreset> of(WorldGenSettings var0) {
      ChunkGenerator var1 = var0.overworld();
      if (var1 instanceof FlatLevelSource) {
         return Optional.of(FLAT);
      } else {
         return var1 instanceof DebugLevelSource ? Optional.of(DEBUG) : Optional.empty();
      }
   }

   public Component description() {
      return this.description;
   }

   public WorldGenSettings create(RegistryAccess.RegistryHolder var1, long var2, boolean var4, boolean var5) {
      WritableRegistry var6 = var1.registryOrThrow(Registry.BIOME_REGISTRY);
      WritableRegistry var7 = var1.registryOrThrow(Registry.DIMENSION_TYPE_REGISTRY);
      WritableRegistry var8 = var1.registryOrThrow(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY);
      return new WorldGenSettings(var2, var4, var5, WorldGenSettings.withOverworld((Registry)var7, (MappedRegistry)DimensionType.defaultDimensions(var7, var6, var8, var2), this.generator(var6, var8, var2)));
   }

   protected abstract ChunkGenerator generator(Registry<Biome> var1, Registry<NoiseGeneratorSettings> var2, long var3);

   // $FF: synthetic method
   WorldPreset(String var1, Object var2) {
      this(var1);
   }

   static {
      PRESETS = Lists.newArrayList(new WorldPreset[]{NORMAL, FLAT, LARGE_BIOMES, AMPLIFIED, SINGLE_BIOME_SURFACE, SINGLE_BIOME_CAVES, SINGLE_BIOME_FLOATING_ISLANDS, DEBUG});
      EDITORS = ImmutableMap.of(Optional.of(FLAT), (var0, var1) -> {
         ChunkGenerator var2 = var1.overworld();
         return new CreateFlatWorldScreen(var0, (var2x) -> {
            var0.worldGenSettingsComponent.updateSettings(new WorldGenSettings(var1.seed(), var1.generateFeatures(), var1.generateBonusChest(), WorldGenSettings.withOverworld((Registry)var0.worldGenSettingsComponent.registryHolder().registryOrThrow(Registry.DIMENSION_TYPE_REGISTRY), (MappedRegistry)var1.dimensions(), new FlatLevelSource(var2x))));
         }, var2 instanceof FlatLevelSource ? ((FlatLevelSource)var2).settings() : FlatLevelGeneratorSettings.getDefault(var0.worldGenSettingsComponent.registryHolder().registryOrThrow(Registry.BIOME_REGISTRY)));
      }, Optional.of(SINGLE_BIOME_SURFACE), (var0, var1) -> {
         return new CreateBuffetWorldScreen(var0, var0.worldGenSettingsComponent.registryHolder(), (var2) -> {
            var0.worldGenSettingsComponent.updateSettings(fromBuffetSettings(var0.worldGenSettingsComponent.registryHolder(), var1, SINGLE_BIOME_SURFACE, var2));
         }, parseBuffetSettings(var0.worldGenSettingsComponent.registryHolder(), var1));
      }, Optional.of(SINGLE_BIOME_CAVES), (var0, var1) -> {
         return new CreateBuffetWorldScreen(var0, var0.worldGenSettingsComponent.registryHolder(), (var2) -> {
            var0.worldGenSettingsComponent.updateSettings(fromBuffetSettings(var0.worldGenSettingsComponent.registryHolder(), var1, SINGLE_BIOME_CAVES, var2));
         }, parseBuffetSettings(var0.worldGenSettingsComponent.registryHolder(), var1));
      }, Optional.of(SINGLE_BIOME_FLOATING_ISLANDS), (var0, var1) -> {
         return new CreateBuffetWorldScreen(var0, var0.worldGenSettingsComponent.registryHolder(), (var2) -> {
            var0.worldGenSettingsComponent.updateSettings(fromBuffetSettings(var0.worldGenSettingsComponent.registryHolder(), var1, SINGLE_BIOME_FLOATING_ISLANDS, var2));
         }, parseBuffetSettings(var0.worldGenSettingsComponent.registryHolder(), var1));
      });
   }

   public interface PresetEditor {
      Screen createEditScreen(CreateWorldScreen var1, WorldGenSettings var2);
   }
}
