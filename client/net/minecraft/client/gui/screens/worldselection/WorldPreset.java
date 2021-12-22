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
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.FixedBiomeSource;
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
      protected ChunkGenerator generator(RegistryAccess var1, long var2) {
         return WorldGenSettings.makeDefaultOverworld(var1, var2);
      }
   };
   private static final WorldPreset FLAT = new WorldPreset("flat") {
      protected ChunkGenerator generator(RegistryAccess var1, long var2) {
         return new FlatLevelSource(FlatLevelGeneratorSettings.getDefault(var1.registryOrThrow(Registry.BIOME_REGISTRY)));
      }
   };
   public static final WorldPreset LARGE_BIOMES = new WorldPreset("large_biomes") {
      protected ChunkGenerator generator(RegistryAccess var1, long var2) {
         return WorldGenSettings.makeOverworld(var1, var2, NoiseGeneratorSettings.LARGE_BIOMES);
      }
   };
   public static final WorldPreset AMPLIFIED = new WorldPreset("amplified") {
      protected ChunkGenerator generator(RegistryAccess var1, long var2) {
         return WorldGenSettings.makeOverworld(var1, var2, NoiseGeneratorSettings.AMPLIFIED);
      }
   };
   private static final WorldPreset SINGLE_BIOME_SURFACE = new WorldPreset("single_biome_surface") {
      protected ChunkGenerator generator(RegistryAccess var1, long var2) {
         return WorldPreset.fixedBiomeGenerator(var1, var2, NoiseGeneratorSettings.OVERWORLD);
      }
   };
   private static final WorldPreset DEBUG = new WorldPreset("debug_all_block_states") {
      protected ChunkGenerator generator(RegistryAccess var1, long var2) {
         return new DebugLevelSource(var1.registryOrThrow(Registry.BIOME_REGISTRY));
      }
   };
   protected static final List<WorldPreset> PRESETS;
   protected static final Map<Optional<WorldPreset>, WorldPreset.PresetEditor> EDITORS;
   private final Component description;

   static NoiseBasedChunkGenerator fixedBiomeGenerator(RegistryAccess var0, long var1, ResourceKey<NoiseGeneratorSettings> var3) {
      return new NoiseBasedChunkGenerator(var0.registryOrThrow(Registry.NOISE_REGISTRY), new FixedBiomeSource((Biome)var0.registryOrThrow(Registry.BIOME_REGISTRY).getOrThrow(Biomes.PLAINS)), var1, () -> {
         return (NoiseGeneratorSettings)var0.registryOrThrow(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY).getOrThrow(var3);
      });
   }

   WorldPreset(String var1) {
      super();
      this.description = new TranslatableComponent("generator." + var1);
   }

   private static WorldGenSettings fromBuffetSettings(RegistryAccess var0, WorldGenSettings var1, WorldPreset var2, Biome var3) {
      FixedBiomeSource var4 = new FixedBiomeSource(var3);
      Registry var6 = var0.registryOrThrow(Registry.DIMENSION_TYPE_REGISTRY);
      Registry var7 = var0.registryOrThrow(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY);
      Supplier var5 = () -> {
         return (NoiseGeneratorSettings)var7.getOrThrow(NoiseGeneratorSettings.OVERWORLD);
      };
      return new WorldGenSettings(var1.seed(), var1.generateFeatures(), var1.generateBonusChest(), WorldGenSettings.withOverworld((Registry)var6, (MappedRegistry)var1.dimensions(), new NoiseBasedChunkGenerator(var0.registryOrThrow(Registry.NOISE_REGISTRY), var4, var1.seed(), var5)));
   }

   private static Biome parseBuffetSettings(RegistryAccess var0, WorldGenSettings var1) {
      return (Biome)var1.overworld().getBiomeSource().possibleBiomes().stream().findFirst().orElse((Biome)var0.registryOrThrow(Registry.BIOME_REGISTRY).getOrThrow(Biomes.PLAINS));
   }

   // $FF: renamed from: of (net.minecraft.world.level.levelgen.WorldGenSettings) java.util.Optional
   public static Optional<WorldPreset> method_114(WorldGenSettings var0) {
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
      return new WorldGenSettings(var2, var4, var5, WorldGenSettings.withOverworld(var1.registryOrThrow(Registry.DIMENSION_TYPE_REGISTRY), DimensionType.defaultDimensions(var1, var2), this.generator(var1, var2)));
   }

   protected abstract ChunkGenerator generator(RegistryAccess var1, long var2);

   public static boolean isVisibleByDefault(WorldPreset var0) {
      return var0 != DEBUG;
   }

   static {
      PRESETS = Lists.newArrayList(new WorldPreset[]{NORMAL, FLAT, LARGE_BIOMES, AMPLIFIED, SINGLE_BIOME_SURFACE, DEBUG});
      EDITORS = ImmutableMap.of(Optional.of(FLAT), (var0, var1) -> {
         ChunkGenerator var2 = var1.overworld();
         return new CreateFlatWorldScreen(var0, (var2x) -> {
            var0.worldGenSettingsComponent.updateSettings(new WorldGenSettings(var1.seed(), var1.generateFeatures(), var1.generateBonusChest(), WorldGenSettings.withOverworld((Registry)var0.worldGenSettingsComponent.registryHolder().registryOrThrow(Registry.DIMENSION_TYPE_REGISTRY), (MappedRegistry)var1.dimensions(), new FlatLevelSource(var2x))));
         }, var2 instanceof FlatLevelSource ? ((FlatLevelSource)var2).settings() : FlatLevelGeneratorSettings.getDefault(var0.worldGenSettingsComponent.registryHolder().registryOrThrow(Registry.BIOME_REGISTRY)));
      }, Optional.of(SINGLE_BIOME_SURFACE), (var0, var1) -> {
         return new CreateBuffetWorldScreen(var0, var0.worldGenSettingsComponent.registryHolder(), (var2) -> {
            var0.worldGenSettingsComponent.updateSettings(fromBuffetSettings(var0.worldGenSettingsComponent.registryHolder(), var1, SINGLE_BIOME_SURFACE, var2));
         }, parseBuffetSettings(var0.worldGenSettingsComponent.registryHolder(), var1));
      });
   }

   public interface PresetEditor {
      Screen createEditScreen(CreateWorldScreen var1, WorldGenSettings var2);
   }
}
