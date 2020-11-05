package net.minecraft.world.level.levelgen;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Iterator;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.Properties;
import java.util.Random;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.WritableRegistry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.OverworldBiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorldGenSettings {
   public static final Codec<WorldGenSettings> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(Codec.LONG.fieldOf("seed").stable().forGetter(WorldGenSettings::seed), Codec.BOOL.fieldOf("generate_features").orElse(true).stable().forGetter(WorldGenSettings::generateFeatures), Codec.BOOL.fieldOf("bonus_chest").orElse(false).stable().forGetter(WorldGenSettings::generateBonusChest), MappedRegistry.dataPackCodec(Registry.LEVEL_STEM_REGISTRY, Lifecycle.stable(), LevelStem.CODEC).xmap(LevelStem::sortMap, Function.identity()).fieldOf("dimensions").forGetter(WorldGenSettings::dimensions), Codec.STRING.optionalFieldOf("legacy_custom_options").stable().forGetter((var0x) -> {
         return var0x.legacyCustomOptions;
      })).apply(var0, var0.stable(WorldGenSettings::new));
   }).comapFlatMap(WorldGenSettings::guardExperimental, Function.identity());
   private static final Logger LOGGER = LogManager.getLogger();
   private final long seed;
   private final boolean generateFeatures;
   private final boolean generateBonusChest;
   private final MappedRegistry<LevelStem> dimensions;
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
      return LevelStem.stable(this.seed, this.dimensions);
   }

   public WorldGenSettings(long var1, boolean var3, boolean var4, MappedRegistry<LevelStem> var5) {
      this(var1, var3, var4, var5, Optional.empty());
      LevelStem var6 = (LevelStem)var5.get(LevelStem.OVERWORLD);
      if (var6 == null) {
         throw new IllegalStateException("Overworld settings missing");
      }
   }

   private WorldGenSettings(long var1, boolean var3, boolean var4, MappedRegistry<LevelStem> var5, Optional<String> var6) {
      super();
      this.seed = var1;
      this.generateFeatures = var3;
      this.generateBonusChest = var4;
      this.dimensions = var5;
      this.legacyCustomOptions = var6;
   }

   public static WorldGenSettings demoSettings(RegistryAccess var0) {
      WritableRegistry var1 = var0.registryOrThrow(Registry.BIOME_REGISTRY);
      int var2 = "North Carolina".hashCode();
      WritableRegistry var3 = var0.registryOrThrow(Registry.DIMENSION_TYPE_REGISTRY);
      WritableRegistry var4 = var0.registryOrThrow(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY);
      return new WorldGenSettings((long)var2, true, true, withOverworld((Registry)var3, (MappedRegistry)DimensionType.defaultDimensions(var3, var1, var4, (long)var2), makeDefaultOverworld(var1, var4, (long)var2)));
   }

   public static WorldGenSettings makeDefault(Registry<DimensionType> var0, Registry<Biome> var1, Registry<NoiseGeneratorSettings> var2) {
      long var3 = (new Random()).nextLong();
      return new WorldGenSettings(var3, true, false, withOverworld((Registry)var0, (MappedRegistry)DimensionType.defaultDimensions(var0, var1, var2, var3), makeDefaultOverworld(var1, var2, var3)));
   }

   public static NoiseBasedChunkGenerator makeDefaultOverworld(Registry<Biome> var0, Registry<NoiseGeneratorSettings> var1, long var2) {
      return new NoiseBasedChunkGenerator(new OverworldBiomeSource(var2, false, false, var0), var2, () -> {
         return (NoiseGeneratorSettings)var1.getOrThrow(NoiseGeneratorSettings.OVERWORLD);
      });
   }

   public long seed() {
      return this.seed;
   }

   public boolean generateFeatures() {
      return this.generateFeatures;
   }

   public boolean generateBonusChest() {
      return this.generateBonusChest;
   }

   public static MappedRegistry<LevelStem> withOverworld(Registry<DimensionType> var0, MappedRegistry<LevelStem> var1, ChunkGenerator var2) {
      LevelStem var3 = (LevelStem)var1.get(LevelStem.OVERWORLD);
      Supplier var4 = () -> {
         return var3 == null ? (DimensionType)var0.getOrThrow(DimensionType.OVERWORLD_LOCATION) : var3.type();
      };
      return withOverworld(var1, var4, var2);
   }

   public static MappedRegistry<LevelStem> withOverworld(MappedRegistry<LevelStem> var0, Supplier<DimensionType> var1, ChunkGenerator var2) {
      MappedRegistry var3 = new MappedRegistry(Registry.LEVEL_STEM_REGISTRY, Lifecycle.experimental());
      var3.register(LevelStem.OVERWORLD, new LevelStem(var1, var2), Lifecycle.stable());
      Iterator var4 = var0.entrySet().iterator();

      while(var4.hasNext()) {
         Entry var5 = (Entry)var4.next();
         ResourceKey var6 = (ResourceKey)var5.getKey();
         if (var6 != LevelStem.OVERWORLD) {
            var3.register(var6, var5.getValue(), var0.lifecycle(var5.getValue()));
         }
      }

      return var3;
   }

   public MappedRegistry<LevelStem> dimensions() {
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
      return (ImmutableSet)this.dimensions().entrySet().stream().map((var0) -> {
         return ResourceKey.create(Registry.DIMENSION_REGISTRY, ((ResourceKey)var0.getKey()).location());
      }).collect(ImmutableSet.toImmutableSet());
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
      return new WorldGenSettings(this.seed, this.generateFeatures, true, this.dimensions, this.legacyCustomOptions);
   }

   public WorldGenSettings withFeaturesToggled() {
      return new WorldGenSettings(this.seed, !this.generateFeatures, this.generateBonusChest, this.dimensions);
   }

   public WorldGenSettings withBonusChestToggled() {
      return new WorldGenSettings(this.seed, this.generateFeatures, !this.generateBonusChest, this.dimensions);
   }

   public static WorldGenSettings create(RegistryAccess var0, Properties var1) {
      String var2 = (String)MoreObjects.firstNonNull((String)var1.get("generator-settings"), "");
      var1.put("generator-settings", var2);
      String var3 = (String)MoreObjects.firstNonNull((String)var1.get("level-seed"), "");
      var1.put("level-seed", var3);
      String var4 = (String)var1.get("generate-structures");
      boolean var5 = var4 == null || Boolean.parseBoolean(var4);
      var1.put("generate-structures", Objects.toString(var5));
      String var6 = (String)var1.get("level-type");
      String var7 = (String)Optional.ofNullable(var6).map((var0x) -> {
         return var0x.toLowerCase(Locale.ROOT);
      }).orElse("default");
      var1.put("level-type", var7);
      long var8 = (new Random()).nextLong();
      if (!var3.isEmpty()) {
         try {
            long var10 = Long.parseLong(var3);
            if (var10 != 0L) {
               var8 = var10;
            }
         } catch (NumberFormatException var18) {
            var8 = (long)var3.hashCode();
         }
      }

      WritableRegistry var19 = var0.registryOrThrow(Registry.DIMENSION_TYPE_REGISTRY);
      WritableRegistry var11 = var0.registryOrThrow(Registry.BIOME_REGISTRY);
      WritableRegistry var12 = var0.registryOrThrow(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY);
      MappedRegistry var13 = DimensionType.defaultDimensions(var19, var11, var12, var8);
      byte var15 = -1;
      switch(var7.hashCode()) {
      case -1100099890:
         if (var7.equals("largebiomes")) {
            var15 = 3;
         }
         break;
      case 3145593:
         if (var7.equals("flat")) {
            var15 = 0;
         }
         break;
      case 1045526590:
         if (var7.equals("debug_all_block_states")) {
            var15 = 1;
         }
         break;
      case 1271599715:
         if (var7.equals("amplified")) {
            var15 = 2;
         }
      }

      switch(var15) {
      case 0:
         JsonObject var16 = !var2.isEmpty() ? GsonHelper.parse(var2) : new JsonObject();
         Dynamic var17 = new Dynamic(JsonOps.INSTANCE, var16);
         DataResult var10009 = FlatLevelGeneratorSettings.CODEC.parse(var17);
         Logger var10010 = LOGGER;
         var10010.getClass();
         return new WorldGenSettings(var8, var5, false, withOverworld((Registry)var19, (MappedRegistry)var13, new FlatLevelSource((FlatLevelGeneratorSettings)var10009.resultOrPartial(var10010::error).orElseGet(() -> {
            return FlatLevelGeneratorSettings.getDefault(var11);
         }))));
      case 1:
         return new WorldGenSettings(var8, var5, false, withOverworld((Registry)var19, (MappedRegistry)var13, new DebugLevelSource(var11)));
      case 2:
         return new WorldGenSettings(var8, var5, false, withOverworld((Registry)var19, (MappedRegistry)var13, new NoiseBasedChunkGenerator(new OverworldBiomeSource(var8, false, false, var11), var8, () -> {
            return (NoiseGeneratorSettings)var12.getOrThrow(NoiseGeneratorSettings.AMPLIFIED);
         })));
      case 3:
         return new WorldGenSettings(var8, var5, false, withOverworld((Registry)var19, (MappedRegistry)var13, new NoiseBasedChunkGenerator(new OverworldBiomeSource(var8, false, true, var11), var8, () -> {
            return (NoiseGeneratorSettings)var12.getOrThrow(NoiseGeneratorSettings.OVERWORLD);
         })));
      default:
         return new WorldGenSettings(var8, var5, false, withOverworld((Registry)var19, (MappedRegistry)var13, makeDefaultOverworld(var11, var12, var8)));
      }
   }

   public WorldGenSettings withSeed(boolean var1, OptionalLong var2) {
      long var4 = var2.orElse(this.seed);
      MappedRegistry var6;
      if (var2.isPresent()) {
         var6 = new MappedRegistry(Registry.LEVEL_STEM_REGISTRY, Lifecycle.experimental());
         long var7 = var2.getAsLong();
         Iterator var9 = this.dimensions.entrySet().iterator();

         while(var9.hasNext()) {
            Entry var10 = (Entry)var9.next();
            ResourceKey var11 = (ResourceKey)var10.getKey();
            var6.register(var11, new LevelStem(((LevelStem)var10.getValue()).typeSupplier(), ((LevelStem)var10.getValue()).generator().withSeed(var7)), this.dimensions.lifecycle(var10.getValue()));
         }
      } else {
         var6 = this.dimensions;
      }

      WorldGenSettings var3;
      if (this.isDebug()) {
         var3 = new WorldGenSettings(var4, false, false, var6);
      } else {
         var3 = new WorldGenSettings(var4, this.generateFeatures(), this.generateBonusChest() && !var1, var6);
      }

      return var3;
   }
}
