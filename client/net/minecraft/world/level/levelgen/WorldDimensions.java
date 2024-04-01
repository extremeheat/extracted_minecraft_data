package net.minecraft.world.level.levelgen;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
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

public record WorldDimensions(Map<ResourceKey<LevelStem>, LevelStem> b) {
   private final Map<ResourceKey<LevelStem>, LevelStem> dimensions;
   public static final MapCodec<WorldDimensions> CODEC = RecordCodecBuilder.mapCodec(
      var0 -> var0.group(
               Codec.unboundedMap(ResourceKey.codec(Registries.LEVEL_STEM), LevelStem.CODEC).fieldOf("dimensions").forGetter(WorldDimensions::dimensions)
            )
            .apply(var0, var0.stable(WorldDimensions::new))
   );
   private static final Set<ResourceKey<LevelStem>> BUILTIN_ORDER = ImmutableSet.of(LevelStem.OVERWORLD, LevelStem.NETHER, LevelStem.END, LevelStem.POTATO);
   private static final int VANILLA_DIMENSION_COUNT = BUILTIN_ORDER.size();

   public WorldDimensions(Map<ResourceKey<LevelStem>, LevelStem> var1) {
      super();
      LevelStem var2 = (LevelStem)var1.get(LevelStem.OVERWORLD);
      if (var2 == null) {
         throw new IllegalStateException("Overworld settings missing");
      } else {
         this.dimensions = var1;
      }
   }

   public WorldDimensions(Registry<LevelStem> var1) {
      this(var1.holders().collect(Collectors.toMap(Holder.Reference::key, Holder.Reference::value)));
   }

   public static Stream<ResourceKey<LevelStem>> keysInOrder(Stream<ResourceKey<LevelStem>> var0) {
      return Stream.concat(BUILTIN_ORDER.stream(), var0.filter(var0x -> !BUILTIN_ORDER.contains(var0x)));
   }

   public WorldDimensions replaceOverworldGenerator(RegistryAccess var1, ChunkGenerator var2) {
      Registry var3 = var1.registryOrThrow(Registries.DIMENSION_TYPE);
      Map var4 = withOverworld(var3, this.dimensions, var2);
      return new WorldDimensions(var4);
   }

   public static Map<ResourceKey<LevelStem>, LevelStem> withOverworld(
      Registry<DimensionType> var0, Map<ResourceKey<LevelStem>, LevelStem> var1, ChunkGenerator var2
   ) {
      LevelStem var3 = (LevelStem)var1.get(LevelStem.OVERWORLD);
      Object var4 = var3 == null ? var0.getHolderOrThrow(BuiltinDimensionTypes.OVERWORLD) : var3.type();
      return withOverworld(var1, (Holder<DimensionType>)var4, var2);
   }

   public static Map<ResourceKey<LevelStem>, LevelStem> withOverworld(
      Map<ResourceKey<LevelStem>, LevelStem> var0, Holder<DimensionType> var1, ChunkGenerator var2
   ) {
      Builder var3 = ImmutableMap.builder();
      var3.putAll(var0);
      var3.put(LevelStem.OVERWORLD, new LevelStem(var1, var2));
      return var3.buildKeepingLast();
   }

   public ChunkGenerator overworld() {
      LevelStem var1 = (LevelStem)this.dimensions.get(LevelStem.OVERWORLD);
      if (var1 == null) {
         throw new IllegalStateException("Overworld settings missing");
      } else {
         return var1.generator();
      }
   }

   public Optional<LevelStem> get(ResourceKey<LevelStem> var1) {
      return Optional.ofNullable((LevelStem)this.dimensions.get(var1));
   }

   public ImmutableSet<ResourceKey<Level>> levels() {
      return this.dimensions().keySet().stream().map(Registries::levelStemToLevel).collect(ImmutableSet.toImmutableSet());
   }

   public boolean isDebug() {
      return this.overworld() instanceof DebugLevelSource;
   }

   private static PrimaryLevelData.SpecialWorldProperty specialWorldProperty(Registry<LevelStem> var0) {
      return var0.getOptional(LevelStem.OVERWORLD).map(var0x -> {
         ChunkGenerator var1 = var0x.generator();
         if (var1 instanceof DebugLevelSource) {
            return PrimaryLevelData.SpecialWorldProperty.DEBUG;
         } else {
            return var1 instanceof FlatLevelSource ? PrimaryLevelData.SpecialWorldProperty.FLAT : PrimaryLevelData.SpecialWorldProperty.NONE;
         }
      }).orElse(PrimaryLevelData.SpecialWorldProperty.NONE);
   }

   static Lifecycle checkStability(ResourceKey<LevelStem> var0, LevelStem var1) {
      return isVanillaLike(var0, var1) ? Lifecycle.stable() : Lifecycle.experimental();
   }

   private static boolean isVanillaLike(ResourceKey<LevelStem> var0, LevelStem var1) {
      if (var0 == LevelStem.OVERWORLD) {
         return isStableOverworld(var1);
      } else if (var0 == LevelStem.NETHER) {
         return isStableNether(var1);
      } else if (var0 == LevelStem.END) {
         return isStableEnd(var1);
      } else {
         return var0 == LevelStem.POTATO ? isStablePotato(var1) : false;
      }
   }

   private static boolean isStableOverworld(LevelStem var0) {
      Holder var1 = var0.type();
      if (!var1.is(BuiltinDimensionTypes.OVERWORLD) && !var1.is(BuiltinDimensionTypes.OVERWORLD_CAVES)) {
         return false;
      } else {
         BiomeSource var3 = var0.generator().getBiomeSource();
         if (var3 instanceof MultiNoiseBiomeSource var2 && !var2.stable(MultiNoiseBiomeSourceParameterLists.OVERWORLD)) {
            return false;
         }

         return true;
      }
   }

   // $VF: Could not properly define all variable types!
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private static boolean isStableNether(LevelStem var0) {
      if (var0.type().is(BuiltinDimensionTypes.NETHER)) {
         ChunkGenerator var3 = var0.generator();
         if (var3 instanceof NoiseBasedChunkGenerator var2 && var2.stable(NoiseGeneratorSettings.NETHER)) {
            BiomeSource var4 = var2.getBiomeSource();
            if (var4 instanceof MultiNoiseBiomeSource var1 && var1.stable(MultiNoiseBiomeSourceParameterLists.NETHER)) {
               return true;
            }
         }
      }

      return false;
   }

   private static boolean isStableEnd(LevelStem var0) {
      if (var0.type().is(BuiltinDimensionTypes.END)) {
         ChunkGenerator var2 = var0.generator();
         if (var2 instanceof NoiseBasedChunkGenerator var1 && var1.stable(NoiseGeneratorSettings.END) && var1.getBiomeSource() instanceof TheEndBiomeSource) {
            return true;
         }
      }

      return false;
   }

   // $VF: Could not properly define all variable types!
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private static boolean isStablePotato(LevelStem var0) {
      if (var0.type().is(BuiltinDimensionTypes.POTATO)) {
         ChunkGenerator var3 = var0.generator();
         if (var3 instanceof NoiseBasedChunkGenerator var2 && var2.stable(NoiseGeneratorSettings.POTATO)) {
            BiomeSource var4 = var2.getBiomeSource();
            if (var4 instanceof MultiNoiseBiomeSource var1 && var1.stable(MultiNoiseBiomeSourceParameterLists.POTATO)) {
               return true;
            }
         }
      }

      return false;
   }

   public WorldDimensions.Complete bake(Registry<LevelStem> var1) {
      Stream var2 = Stream.concat(var1.registryKeySet().stream(), this.dimensions.keySet().stream()).distinct();
      ArrayList var3 = new ArrayList();
      keysInOrder(var2)
         .forEach(
            var3x -> var1.getOptional(var3x)
                  .or(() -> Optional.ofNullable(this.dimensions.get(var3x)))
                  .ifPresent(var2xx -> var3.add(new 1Entry(var3x, (LevelStem)var2xx)))
         );
      Lifecycle var4 = var3.size() == VANILLA_DIMENSION_COUNT ? Lifecycle.stable() : Lifecycle.experimental();
      MappedRegistry var5 = new MappedRegistry(Registries.LEVEL_STEM, var4);
      var3.forEach(var1x -> var5.register(var1x.key, var1x.value, var1x.registrationInfo()));
      Registry var6 = var5.freeze();
      PrimaryLevelData.SpecialWorldProperty var7 = specialWorldProperty(var6);
      return new WorldDimensions.Complete(var6.freeze(), var7);

      record 1Entry(ResourceKey<LevelStem> a, LevelStem b) {
         final ResourceKey<LevelStem> key;
         final LevelStem value;

         _Entry/* $VF was: 1Entry*/(ResourceKey<LevelStem> var1, LevelStem var2) {
            super();
            this.key = var1;
            this.value = var2;
         }

         RegistrationInfo registrationInfo() {
            return new RegistrationInfo(Optional.empty(), WorldDimensions.checkStability(this.key, this.value));
         }
      }

   }

   public static record Complete(Registry<LevelStem> a, PrimaryLevelData.SpecialWorldProperty b) {
      private final Registry<LevelStem> dimensions;
      private final PrimaryLevelData.SpecialWorldProperty specialWorldProperty;

      public Complete(Registry<LevelStem> var1, PrimaryLevelData.SpecialWorldProperty var2) {
         super();
         this.dimensions = var1;
         this.specialWorldProperty = var2;
      }

      public Lifecycle lifecycle() {
         return this.dimensions.registryLifecycle();
      }

      public RegistryAccess.Frozen dimensionsRegistryAccess() {
         return new RegistryAccess.ImmutableRegistryAccess(List.of(this.dimensions)).freeze();
      }
   }
}
