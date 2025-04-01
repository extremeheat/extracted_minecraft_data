package net.minecraft.world.level.levelgen;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.HashMap;
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
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.mines.MineSpawnStrategy;
import net.minecraft.world.level.storage.PrimaryLevelData;

public record WorldDimensions(Map<ResourceKey<LevelStem>, LevelStem> dimensions) {
   public static final MapCodec<WorldDimensions> CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(Codec.unboundedMap(ResourceKey.codec(Registries.LEVEL_STEM), LevelStem.CODEC).fieldOf("dimensions").forGetter(WorldDimensions::dimensions)).apply(var0, var0.stable(WorldDimensions::new)));
   private static final Set<ResourceKey<LevelStem>> BUILTIN_ORDER;
   private static final int VANILLA_DIMENSION_COUNT;

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
      this((Map)var1.listElements().collect(Collectors.toMap(Holder.Reference::key, Holder.Reference::value)));
   }

   public static Stream<ResourceKey<LevelStem>> keysInOrder(Stream<ResourceKey<LevelStem>> var0) {
      return Stream.concat(BUILTIN_ORDER.stream(), var0.filter((var0x) -> !BUILTIN_ORDER.contains(var0x)));
   }

   public WorldDimensions replaceOverworldGenerator(HolderLookup.Provider var1, ChunkGenerator var2) {
      HolderLookup.RegistryLookup var3 = var1.lookupOrThrow(Registries.DIMENSION_TYPE);
      Map var4 = withOverworld(var3, this.dimensions, var2);
      return new WorldDimensions(var4);
   }

   public static Map<ResourceKey<LevelStem>, LevelStem> withOverworld(HolderLookup<DimensionType> var0, Map<ResourceKey<LevelStem>, LevelStem> var1, ChunkGenerator var2) {
      LevelStem var3 = (LevelStem)var1.get(LevelStem.OVERWORLD);
      Object var4 = var3 == null ? var0.getOrThrow(BuiltinDimensionTypes.OVERWORLD) : var3.type();
      return withOverworld(var1, (Holder)var4, var2);
   }

   public static Map<ResourceKey<LevelStem>, LevelStem> withOverworld(Map<ResourceKey<LevelStem>, LevelStem> var0, Holder<DimensionType> var1, ChunkGenerator var2) {
      ImmutableMap.Builder var3 = ImmutableMap.builder();
      var3.putAll(var0);
      var3.put(LevelStem.OVERWORLD, new LevelStem(var1, Optional.of(var2), List.of(), Optional.empty(), MineSpawnStrategy.SURFACE));
      return var3.buildKeepingLast();
   }

   public WorldDimensions stripGenerated() {
      HashMap var1 = new HashMap(this.dimensions);
      var1.entrySet().removeIf((var0) -> ((ResourceKey)var0.getKey()).location().getPath().startsWith("level") & ((LevelStem)var0.getValue()).generator().isEmpty());
      return new WorldDimensions(var1);
   }

   public ChunkGenerator overworld() {
      LevelStem var1 = (LevelStem)this.dimensions.get(LevelStem.OVERWORLD);
      if (var1 == null) {
         throw new IllegalStateException("Overworld settings missing");
      } else {
         return (ChunkGenerator)var1.generator().get();
      }
   }

   public Optional<LevelStem> get(ResourceKey<LevelStem> var1) {
      return Optional.ofNullable((LevelStem)this.dimensions.get(var1));
   }

   public ImmutableSet<ResourceKey<Level>> levels() {
      return (ImmutableSet)this.dimensions().keySet().stream().map(Registries::levelStemToLevel).collect(ImmutableSet.toImmutableSet());
   }

   public boolean isDebug() {
      return this.overworld() instanceof DebugLevelSource;
   }

   private static PrimaryLevelData.SpecialWorldProperty specialWorldProperty(Registry<LevelStem> var0) {
      return (PrimaryLevelData.SpecialWorldProperty)var0.getOptional(LevelStem.OVERWORLD).map((var0x) -> {
         ChunkGenerator var1 = (ChunkGenerator)var0x.generator().get();
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
      return true;
   }

   public Complete bake(Registry<LevelStem> var1) {
      Stream var2 = Stream.concat(var1.registryKeySet().stream(), this.dimensions.keySet().stream()).distinct();
      ArrayList var3 = new ArrayList();
      keysInOrder(var2).forEach((var3x) -> var1.getOptional(var3x).or(() -> Optional.ofNullable((LevelStem)this.dimensions.get(var3x))).ifPresent((var2) -> {
            record 1Entry(ResourceKey<LevelStem> key, LevelStem value) {
               final ResourceKey<LevelStem> key;
               final LevelStem value;

               _Entry/* $FF was: 1Entry*/(ResourceKey<LevelStem> var1, LevelStem var2) {
                  super();
                  this.key = var1;
                  this.value = var2;
               }

               RegistrationInfo registrationInfo() {
                  return new RegistrationInfo(Optional.empty(), WorldDimensions.checkStability(this.key, this.value));
               }
            }

            var3.add(new 1Entry(var3x, var2));
         }));
      Lifecycle var4 = var3.size() == VANILLA_DIMENSION_COUNT ? Lifecycle.stable() : Lifecycle.experimental();
      MappedRegistry var5 = new MappedRegistry(Registries.LEVEL_STEM, var4);
      var3.forEach((var1x) -> var5.register(var1x.key, var1x.value, var1x.registrationInfo()));
      Registry var6 = var5.freeze();
      PrimaryLevelData.SpecialWorldProperty var7 = specialWorldProperty(var6);
      return new Complete(var6.freeze(), var7);
   }

   static {
      BUILTIN_ORDER = ImmutableSet.of(LevelStem.OVERWORLD);
      VANILLA_DIMENSION_COUNT = BUILTIN_ORDER.size();
   }

   public static record Complete(Registry<LevelStem> dimensions, PrimaryLevelData.SpecialWorldProperty specialWorldProperty) {
      public Complete(Registry<LevelStem> var1, PrimaryLevelData.SpecialWorldProperty var2) {
         super();
         this.dimensions = var1;
         this.specialWorldProperty = var2;
      }

      public Lifecycle lifecycle() {
         return this.dimensions.registryLifecycle();
      }

      public RegistryAccess.Frozen dimensionsRegistryAccess() {
         return (new RegistryAccess.ImmutableRegistryAccess(List.of(this.dimensions))).freeze();
      }
   }
}
