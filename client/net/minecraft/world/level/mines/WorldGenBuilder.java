package net.minecraft.world.level.mines;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.random.Weighted;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeBuilder;
import net.minecraft.world.level.biome.BiomeModificationBuilder;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterList;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import org.apache.commons.lang3.function.TriFunction;

public class WorldGenBuilder {
   private final Set<ResourceKey<Biome>> enabledBiomes = new HashSet();
   private final Map<MobCategory, List<Weighted<MobSpawnSettings.SpawnerData>>> enabledMobs = new HashMap();
   private final List<Processor<DimensionType>> dimensionTypeProcessors = new ArrayList();
   private final HolderLookup.Provider registries;
   private final List<BiomeModificationBuilder> changedBiomes = new ArrayList();
   private ResourceKey<NoiseGeneratorSettings> baseGeneratorSettings;
   private Optional<ChunkGeneratorGenerator> chunkGeneratorOverride;
   private final List<Consumer<NoiseGeneratorSettings.Builder>> noiseModifiers;
   private MineSpawnStrategy spawnStrategy;

   public WorldGenBuilder(HolderLookup.Provider var1) {
      super();
      this.baseGeneratorSettings = NoiseGeneratorSettings.OVERWORLD;
      this.chunkGeneratorOverride = Optional.empty();
      this.noiseModifiers = new ArrayList();
      this.spawnStrategy = MineSpawnStrategy.SURFACE;
      this.registries = var1;
   }

   public HolderLookup.Provider registries() {
      return this.registries;
   }

   public <T> Holder<T> getOrThrow(ResourceKey<T> var1) {
      return this.registries.getOrThrow(var1);
   }

   public WorldGenBuilder addBiome(ResourceKey<Biome> var1) {
      this.enabledBiomes.add(var1);
      return this;
   }

   public WorldGenBuilder addMobSpawn(MobCategory var1, int var2, MobSpawnSettings.SpawnerData var3) {
      ((List)this.enabledMobs.computeIfAbsent(var1, (var0) -> new ArrayList())).add(new Weighted(var3, var2));
      return this;
   }

   public WorldGenBuilder changeDimensionType(Processor<DimensionType> var1) {
      this.dimensionTypeProcessors.add(var1);
      return this;
   }

   public WorldGenBuilder changeBiome(ResourceKey<Biome> var1, Consumer<BiomeModificationBuilder> var2) {
      return this.changeBiomes((var2x) -> var2.accept(var2x.thatMatch((var1x) -> var1x.key() == var1)));
   }

   public WorldGenBuilder changeBiomes(Consumer<BiomeModificationBuilder> var1) {
      BiomeModificationBuilder var2 = new BiomeModificationBuilder();
      var1.accept(var2);
      this.changedBiomes.add(var2);
      return this;
   }

   public WorldGenBuilder setNoiseGenerationBase(ResourceKey<NoiseGeneratorSettings> var1) {
      this.baseGeneratorSettings = var1;
      return this;
   }

   public WorldGenBuilder changeNoiseGeneration(Consumer<NoiseGeneratorSettings.Builder> var1) {
      this.noiseModifiers.add(var1);
      return this;
   }

   public WorldGenBuilder withSpawnStrategy(MineSpawnStrategy var1) {
      this.spawnStrategy = var1;
      return this;
   }

   public WorldGenBuilder withCustomChunkGenerator(ChunkGeneratorGenerator var1) {
      this.chunkGeneratorOverride = Optional.of(var1);
      return this;
   }

   public ChunkGenerator createChunkGenerator(String var1) {
      HolderLookup.RegistryLookup var2 = this.registries.lookupOrThrow(Registries.BIOME);
      Stream var3 = this.enabledBiomes.isEmpty() ? var2.listElementIds() : this.enabledBiomes.stream();
      List var4 = this.createModifiedBiomes(var2, var1);
      Map var5 = (Map)var4.stream().collect(Collectors.toMap(ModifiedBiome::original, ModifiedBiome::modified));
      Map var6 = (Map)var5.entrySet().stream().collect(Collectors.toMap((var1x) -> var2.getOrThrow((ResourceKey)var1x.getKey()), (var1x) -> var2.getOrThrow((ResourceKey)var1x.getValue())));
      List var7 = var3.map((var2x) -> (Holder)var6.getOrDefault(var2.getOrThrow(var2x), var2.getOrThrow(var2x))).toList();
      MultiNoiseBiomeSourceParameterList var8 = new MultiNoiseBiomeSourceParameterList(var7, var6, var2);
      MultiNoiseBiomeSource var9 = MultiNoiseBiomeSource.createFromPreset(Holder.direct(var8));
      HolderLookup.RegistryLookup var10 = this.registries.lookupOrThrow(Registries.NOISE_SETTINGS);
      NoiseGeneratorSettings.Builder var11 = ((NoiseGeneratorSettings)var10.getOrThrow(this.baseGeneratorSettings).value()).asBuilder();
      this.noiseModifiers.forEach((var1x) -> var1x.accept(var11));
      var11.modifySurfaceRule((var1x) -> var1x.mapBiomes(var5)).salt((long)var1.hashCode());
      Holder var12 = Holder.direct(var11.create());
      return (ChunkGenerator)(this.chunkGeneratorOverride.isPresent() ? (ChunkGenerator)((ChunkGeneratorGenerator)this.chunkGeneratorOverride.get()).apply(this.registries, var9, var12) : new NoiseBasedChunkGenerator(var9, var12));
   }

   public Optional<DimensionType> createDimensionType(DimensionType var1) {
      if (this.dimensionTypeProcessors.isEmpty()) {
         return Optional.empty();
      } else {
         DimensionType var2 = var1;

         for(Processor var4 : this.dimensionTypeProcessors) {
            var2 = (DimensionType)var4.apply(var2);
         }

         return var2.equals(var1) ? Optional.empty() : Optional.of(var2);
      }
   }

   public List<ModifiedBiome> createModifiedBiomes(HolderLookup<Biome> var1, String var2) {
      Stream var3;
      if (this.enabledBiomes.isEmpty()) {
         var3 = var1.listElements();
      } else {
         Stream var10000 = this.enabledBiomes.stream();
         Objects.requireNonNull(var1);
         var3 = var10000.map(var1::getOrThrow);
      }

      return var3.filter((var0) -> !var0.key().location().getPath().startsWith("level")).map((var2x) -> {
         BiomeBuilder var3 = ((Biome)var2x.value()).asBuilder();

         for(Map.Entry var5 : this.enabledMobs.entrySet()) {
            MobCategory var6 = (MobCategory)var5.getKey();
            List var7 = (List)var5.getValue();
            var3.mobSpawnSettings().clearSpawns(var6);

            for(Weighted var9 : var7) {
               var3.mobSpawnSettings().addSpawn(var6, var9.weight(), (MobSpawnSettings.SpawnerData)var9.value());
            }
         }

         this.changedBiomes.forEach((var1) -> var1.apply(var3));
         Biome var10 = var3.build();
         ResourceKey var11 = var10.equals(var2x.value()) ? var2x.key() : ResourceKey.create(Registries.BIOME, var2x.key().location().withPrefix(var2 + "/"));
         return new ModifiedBiome(var2x.key(), var11, var10);
      }).filter((var0) -> var0.original() != var0.modified()).toList();
   }

   public MineSpawnStrategy spawnStrategy() {
      return this.spawnStrategy;
   }

   public static record ModifiedBiome(ResourceKey<Biome> original, ResourceKey<Biome> modified, Biome biome) {
      public ModifiedBiome(ResourceKey<Biome> var1, ResourceKey<Biome> var2, Biome var3) {
         super();
         this.original = var1;
         this.modified = var2;
         this.biome = var3;
      }
   }

   public interface ChunkGeneratorGenerator extends TriFunction<HolderLookup.Provider, BiomeSource, Holder<NoiseGeneratorSettings>, ChunkGenerator> {
   }

   public interface Processor<T> extends Function<T, T> {
   }
}
