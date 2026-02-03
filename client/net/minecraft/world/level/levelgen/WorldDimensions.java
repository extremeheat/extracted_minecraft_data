package net.minecraft.world.level.levelgen;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.WritableRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterLists;
import net.minecraft.world.level.biome.TheEndBiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.storage.PrimaryLevelData;

public record WorldDimensions(Map<ResourceKey<LevelStem>, LevelStem> dimensions) {
   public static final MapCodec<WorldDimensions> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(Codec.unboundedMap(ResourceKey.codec(Registries.LEVEL_STEM), LevelStem.CODEC).fieldOf("dimensions").forGetter(WorldDimensions::dimensions)).apply(i, i.stable(WorldDimensions::new)));
   private static final Set<ResourceKey<LevelStem>> BUILTIN_ORDER;
   private static final int VANILLA_DIMENSION_COUNT;

   public WorldDimensions {
      super();
      LevelStem overworld = (LevelStem)dimensions.get(LevelStem.OVERWORLD);
      if (overworld == null) {
         throw new IllegalStateException("Overworld settings missing");
      }
   }

   public WorldDimensions(final Registry<LevelStem> registry) {
      this((Map)registry.listElements().collect(Collectors.toMap(Holder.Reference::key, Holder.Reference::value)));
   }

   public static Stream<ResourceKey<LevelStem>> keysInOrder(final Stream<ResourceKey<LevelStem>> knownKeys) {
      return Stream.concat(BUILTIN_ORDER.stream(), knownKeys.filter((k) -> !BUILTIN_ORDER.contains(k)));
   }

   public WorldDimensions replaceOverworldGenerator(final HolderLookup.Provider registries, final ChunkGenerator generator) {
      HolderLookup<DimensionType> dimensionTypes = registries.lookupOrThrow(Registries.DIMENSION_TYPE);
      Map<ResourceKey<LevelStem>, LevelStem> newDimensions = withOverworld(dimensionTypes, this.dimensions, generator);
      return new WorldDimensions(newDimensions);
   }

   public static Map<ResourceKey<LevelStem>, LevelStem> withOverworld(final HolderLookup<DimensionType> dimensionTypes, final Map<ResourceKey<LevelStem>, LevelStem> dimensions, final ChunkGenerator generator) {
      LevelStem stem = (LevelStem)dimensions.get(LevelStem.OVERWORLD);
      Holder<DimensionType> type = (Holder<DimensionType>)(stem == null ? dimensionTypes.getOrThrow(BuiltinDimensionTypes.OVERWORLD) : stem.type());
      return withOverworld(dimensions, type, generator);
   }

   public static Map<ResourceKey<LevelStem>, LevelStem> withOverworld(final Map<ResourceKey<LevelStem>, LevelStem> dimensions, final Holder<DimensionType> type, final ChunkGenerator generator) {
      ImmutableMap.Builder<ResourceKey<LevelStem>, LevelStem> builder = ImmutableMap.builder();
      builder.putAll(dimensions);
      builder.put(LevelStem.OVERWORLD, new LevelStem(type, generator));
      return builder.buildKeepingLast();
   }

   public ChunkGenerator overworld() {
      LevelStem stem = (LevelStem)this.dimensions.get(LevelStem.OVERWORLD);
      if (stem == null) {
         throw new IllegalStateException("Overworld settings missing");
      } else {
         return stem.generator();
      }
   }

   public Optional<LevelStem> get(final ResourceKey<LevelStem> key) {
      return Optional.ofNullable((LevelStem)this.dimensions.get(key));
   }

   public ImmutableSet<ResourceKey<Level>> levels() {
      return (ImmutableSet)this.dimensions().keySet().stream().map(Registries::levelStemToLevel).collect(ImmutableSet.toImmutableSet());
   }

   public boolean isDebug() {
      return this.overworld() instanceof DebugLevelSource;
   }

   private static PrimaryLevelData.SpecialWorldProperty specialWorldProperty(final Registry<LevelStem> registry) {
      return (PrimaryLevelData.SpecialWorldProperty)registry.getOptional(LevelStem.OVERWORLD).map((overworld) -> {
         ChunkGenerator generator = overworld.generator();
         if (generator instanceof DebugLevelSource) {
            return PrimaryLevelData.SpecialWorldProperty.DEBUG;
         } else {
            return generator instanceof FlatLevelSource ? PrimaryLevelData.SpecialWorldProperty.FLAT : PrimaryLevelData.SpecialWorldProperty.NONE;
         }
      }).orElse(PrimaryLevelData.SpecialWorldProperty.NONE);
   }

   private static Lifecycle checkStability(final ResourceKey<LevelStem> key, final LevelStem dimension) {
      return isVanillaLike(key, dimension) ? Lifecycle.stable() : Lifecycle.experimental();
   }

   private static boolean isVanillaLike(final ResourceKey<LevelStem> key, final LevelStem dimension) {
      if (key == LevelStem.OVERWORLD) {
         return isStableOverworld(dimension);
      } else if (key == LevelStem.NETHER) {
         return isStableNether(dimension);
      } else {
         return key == LevelStem.END ? isStableEnd(dimension) : false;
      }
   }

   private static boolean isStableOverworld(final LevelStem dimension) {
      Holder<DimensionType> dimensionType = dimension.type();
      if (!dimensionType.is(BuiltinDimensionTypes.OVERWORLD) && !dimensionType.is(BuiltinDimensionTypes.OVERWORLD_CAVES)) {
         return false;
      } else {
         BiomeSource var3 = dimension.generator().getBiomeSource();
         if (var3 instanceof MultiNoiseBiomeSource) {
            MultiNoiseBiomeSource biomeSource = (MultiNoiseBiomeSource)var3;
            if (!biomeSource.stable(MultiNoiseBiomeSourceParameterLists.OVERWORLD)) {
               return false;
            }
         }

         return true;
      }
   }

   private static boolean isStableNether(final LevelStem dimension) {
      boolean var10000;
      if (dimension.type().is(BuiltinDimensionTypes.NETHER)) {
         ChunkGenerator var3 = dimension.generator();
         if (var3 instanceof NoiseBasedChunkGenerator) {
            NoiseBasedChunkGenerator generator = (NoiseBasedChunkGenerator)var3;
            if (generator.stable(NoiseGeneratorSettings.NETHER)) {
               BiomeSource var4 = generator.getBiomeSource();
               if (var4 instanceof MultiNoiseBiomeSource) {
                  MultiNoiseBiomeSource biomeSource = (MultiNoiseBiomeSource)var4;
                  if (biomeSource.stable(MultiNoiseBiomeSourceParameterLists.NETHER)) {
                     var10000 = true;
                     return var10000;
                  }
               }
            }
         }
      }

      var10000 = false;
      return var10000;
   }

   private static boolean isStableEnd(final LevelStem dimension) {
      boolean var10000;
      if (dimension.type().is(BuiltinDimensionTypes.END)) {
         ChunkGenerator var2 = dimension.generator();
         if (var2 instanceof NoiseBasedChunkGenerator) {
            NoiseBasedChunkGenerator generator = (NoiseBasedChunkGenerator)var2;
            if (generator.stable(NoiseGeneratorSettings.END) && generator.getBiomeSource() instanceof TheEndBiomeSource) {
               var10000 = true;
               return var10000;
            }
         }
      }

      var10000 = false;
      return var10000;
   }

   public Complete bake(final Registry<LevelStem> baseDimensions) {
      Stream<ResourceKey<LevelStem>> knownDimensions = Stream.concat(baseDimensions.registryKeySet().stream(), this.dimensions.keySet().stream()).distinct();
      List<Entry> results = new ArrayList();
      keysInOrder(knownDimensions).forEach((key) -> baseDimensions.getOptional(key).or(() -> Optional.ofNullable((LevelStem)this.dimensions.get(key))).ifPresent((levelStem) -> {
            record Entry(ResourceKey<LevelStem> key, LevelStem value) {
               Entry {
                  super();
               }

               private RegistrationInfo registrationInfo() {
                  return new RegistrationInfo(Optional.empty(), WorldDimensions.checkStability(this.key, this.value));
               }
            }

            results.add(new Entry(key, levelStem));
         }));
      Lifecycle initialStability = results.size() == VANILLA_DIMENSION_COUNT ? Lifecycle.stable() : Lifecycle.experimental();
      WritableRegistry<LevelStem> writableDimensions = new MappedRegistry<LevelStem>(Registries.LEVEL_STEM, initialStability);
      results.forEach((entry) -> writableDimensions.register(entry.key, entry.value, entry.registrationInfo()));
      Registry<LevelStem> newDimensions = writableDimensions.freeze();
      PrimaryLevelData.SpecialWorldProperty specialWorldProperty = specialWorldProperty(newDimensions);
      return new Complete(newDimensions.freeze(), specialWorldProperty);
   }

   static {
      BUILTIN_ORDER = ImmutableSet.of(LevelStem.OVERWORLD, LevelStem.NETHER, LevelStem.END);
      VANILLA_DIMENSION_COUNT = BUILTIN_ORDER.size();
   }

   public static record Complete(Registry<LevelStem> dimensions, PrimaryLevelData.SpecialWorldProperty specialWorldProperty) {
      public Complete {
         super();
      }

      public Lifecycle lifecycle() {
         return this.dimensions.registryLifecycle();
      }

      public RegistryAccess.Frozen dimensionsRegistryAccess() {
         return (new RegistryAccess.ImmutableRegistryAccess(List.of(this.dimensions))).freeze();
      }
   }
}
