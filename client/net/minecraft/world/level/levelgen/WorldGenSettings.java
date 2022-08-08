package net.minecraft.world.level.levelgen;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.function.Function;
import net.minecraft.core.Holder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import org.apache.commons.lang3.StringUtils;

public class WorldGenSettings {
   public static final Codec<WorldGenSettings> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(Codec.LONG.fieldOf("seed").stable().forGetter(WorldGenSettings::seed), Codec.BOOL.fieldOf("generate_features").orElse(true).stable().forGetter(WorldGenSettings::generateStructures), Codec.BOOL.fieldOf("bonus_chest").orElse(false).stable().forGetter(WorldGenSettings::generateBonusChest), RegistryCodecs.dataPackAwareCodec(Registry.LEVEL_STEM_REGISTRY, Lifecycle.stable(), LevelStem.CODEC).xmap(LevelStem::sortMap, Function.identity()).fieldOf("dimensions").forGetter(WorldGenSettings::dimensions), Codec.STRING.optionalFieldOf("legacy_custom_options").stable().forGetter((var0x) -> {
         return var0x.legacyCustomOptions;
      })).apply(var0, var0.stable(WorldGenSettings::new));
   }).comapFlatMap(WorldGenSettings::guardExperimental, Function.identity());
   private final long seed;
   private final boolean generateStructures;
   private final boolean generateBonusChest;
   private final Registry<LevelStem> dimensions;
   private final Optional<String> legacyCustomOptions;

   private DataResult<WorldGenSettings> guardExperimental() {
      LevelStem var1 = (LevelStem)this.dimensions.get(LevelStem.OVERWORLD);
      if (var1 == null) {
         return DataResult.error("Overworld settings missing");
      } else {
         return this.stable() ? DataResult.success(this, Lifecycle.stable()) : DataResult.success(this);
      }
   }

   private boolean stable() {
      return LevelStem.stable(this.dimensions);
   }

   public WorldGenSettings(long var1, boolean var3, boolean var4, Registry<LevelStem> var5) {
      this(var1, var3, var4, var5, Optional.empty());
      LevelStem var6 = (LevelStem)var5.get(LevelStem.OVERWORLD);
      if (var6 == null) {
         throw new IllegalStateException("Overworld settings missing");
      }
   }

   private WorldGenSettings(long var1, boolean var3, boolean var4, Registry<LevelStem> var5, Optional<String> var6) {
      super();
      this.seed = var1;
      this.generateStructures = var3;
      this.generateBonusChest = var4;
      this.dimensions = var5;
      this.legacyCustomOptions = var6;
   }

   public long seed() {
      return this.seed;
   }

   public boolean generateStructures() {
      return this.generateStructures;
   }

   public boolean generateBonusChest() {
      return this.generateBonusChest;
   }

   public static WorldGenSettings replaceOverworldGenerator(RegistryAccess var0, WorldGenSettings var1, ChunkGenerator var2) {
      Registry var3 = var0.registryOrThrow(Registry.DIMENSION_TYPE_REGISTRY);
      Registry var4 = withOverworld(var3, var1.dimensions(), var2);
      return new WorldGenSettings(var1.seed(), var1.generateStructures(), var1.generateBonusChest(), var4);
   }

   public static Registry<LevelStem> withOverworld(Registry<DimensionType> var0, Registry<LevelStem> var1, ChunkGenerator var2) {
      LevelStem var3 = (LevelStem)var1.get(LevelStem.OVERWORLD);
      Holder var4 = var3 == null ? var0.getOrCreateHolderOrThrow(BuiltinDimensionTypes.OVERWORLD) : var3.typeHolder();
      return withOverworld(var1, var4, var2);
   }

   public static Registry<LevelStem> withOverworld(Registry<LevelStem> var0, Holder<DimensionType> var1, ChunkGenerator var2) {
      MappedRegistry var3 = new MappedRegistry(Registry.LEVEL_STEM_REGISTRY, Lifecycle.experimental(), (Function)null);
      var3.register(LevelStem.OVERWORLD, new LevelStem(var1, var2), Lifecycle.stable());
      Iterator var4 = var0.entrySet().iterator();

      while(var4.hasNext()) {
         Map.Entry var5 = (Map.Entry)var4.next();
         ResourceKey var6 = (ResourceKey)var5.getKey();
         if (var6 != LevelStem.OVERWORLD) {
            var3.register(var6, (LevelStem)var5.getValue(), var0.lifecycle((LevelStem)var5.getValue()));
         }
      }

      return var3;
   }

   public Registry<LevelStem> dimensions() {
      return this.dimensions;
   }

   public ChunkGenerator overworld() {
      LevelStem var1 = (LevelStem)this.dimensions.get(LevelStem.OVERWORLD);
      if (var1 == null) {
         throw new IllegalStateException("Overworld settings missing");
      } else {
         return var1.generator();
      }
   }

   public ImmutableSet<ResourceKey<Level>> levels() {
      return (ImmutableSet)this.dimensions().entrySet().stream().map(Map.Entry::getKey).map(WorldGenSettings::levelStemToLevel).collect(ImmutableSet.toImmutableSet());
   }

   public static ResourceKey<Level> levelStemToLevel(ResourceKey<LevelStem> var0) {
      return ResourceKey.create(Registry.DIMENSION_REGISTRY, var0.location());
   }

   public static ResourceKey<LevelStem> levelToLevelStem(ResourceKey<Level> var0) {
      return ResourceKey.create(Registry.LEVEL_STEM_REGISTRY, var0.location());
   }

   public boolean isDebug() {
      return this.overworld() instanceof DebugLevelSource;
   }

   public boolean isFlatWorld() {
      return this.overworld() instanceof FlatLevelSource;
   }

   public boolean isOldCustomizedWorld() {
      return this.legacyCustomOptions.isPresent();
   }

   public WorldGenSettings withBonusChest() {
      return new WorldGenSettings(this.seed, this.generateStructures, true, this.dimensions, this.legacyCustomOptions);
   }

   public WorldGenSettings withStructuresToggled() {
      return new WorldGenSettings(this.seed, !this.generateStructures, this.generateBonusChest, this.dimensions);
   }

   public WorldGenSettings withBonusChestToggled() {
      return new WorldGenSettings(this.seed, this.generateStructures, !this.generateBonusChest, this.dimensions);
   }

   public WorldGenSettings withSeed(boolean var1, OptionalLong var2) {
      long var4 = var2.orElse(this.seed);
      Object var6;
      if (var2.isPresent()) {
         MappedRegistry var7 = new MappedRegistry(Registry.LEVEL_STEM_REGISTRY, Lifecycle.experimental(), (Function)null);
         Iterator var8 = this.dimensions.entrySet().iterator();

         while(var8.hasNext()) {
            Map.Entry var9 = (Map.Entry)var8.next();
            ResourceKey var10 = (ResourceKey)var9.getKey();
            var7.register(var10, new LevelStem(((LevelStem)var9.getValue()).typeHolder(), ((LevelStem)var9.getValue()).generator()), this.dimensions.lifecycle((LevelStem)var9.getValue()));
         }

         var6 = var7;
      } else {
         var6 = this.dimensions;
      }

      WorldGenSettings var3;
      if (this.isDebug()) {
         var3 = new WorldGenSettings(var4, false, false, (Registry)var6);
      } else {
         var3 = new WorldGenSettings(var4, this.generateStructures(), this.generateBonusChest() && !var1, (Registry)var6);
      }

      return var3;
   }

   public static OptionalLong parseSeed(String var0) {
      var0 = var0.trim();
      if (StringUtils.isEmpty(var0)) {
         return OptionalLong.empty();
      } else {
         try {
            return OptionalLong.of(Long.parseLong(var0));
         } catch (NumberFormatException var2) {
            return OptionalLong.of((long)var0.hashCode());
         }
      }
   }
}
