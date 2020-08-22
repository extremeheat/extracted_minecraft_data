package net.minecraft.world.level.levelgen.placement;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.ChanceRangeDecoratorConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.CountRangeDecoratorConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.DecoratorConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoiseDependantDecoratorConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneDecoratorConfiguration;
import net.minecraft.world.level.levelgen.placement.nether.ChanceRangeDecorator;
import net.minecraft.world.level.levelgen.placement.nether.CountRangeDecorator;
import net.minecraft.world.level.levelgen.placement.nether.HellFireDecorator;
import net.minecraft.world.level.levelgen.placement.nether.LightGemChanceDecorator;
import net.minecraft.world.level.levelgen.placement.nether.MagmaDecorator;
import net.minecraft.world.level.levelgen.placement.nether.RandomCountRangeDecorator;

public abstract class FeatureDecorator {
   public static final FeatureDecorator NOPE = register("nope", new NopePlacementDecorator(NoneDecoratorConfiguration::deserialize));
   public static final FeatureDecorator COUNT_HEIGHTMAP = register("count_heightmap", new CountHeightmapDecorator(FrequencyDecoratorConfiguration::deserialize));
   public static final FeatureDecorator COUNT_TOP_SOLID = register("count_top_solid", new CountTopSolidDecorator(FrequencyDecoratorConfiguration::deserialize));
   public static final FeatureDecorator COUNT_HEIGHTMAP_32 = register("count_heightmap_32", new CountHeightmap32Decorator(FrequencyDecoratorConfiguration::deserialize));
   public static final FeatureDecorator COUNT_HEIGHTMAP_DOUBLE = register("count_heightmap_double", new CountHeighmapDoubleDecorator(FrequencyDecoratorConfiguration::deserialize));
   public static final FeatureDecorator COUNT_HEIGHT_64 = register("count_height_64", new CountHeight64Decorator(FrequencyDecoratorConfiguration::deserialize));
   public static final FeatureDecorator NOISE_HEIGHTMAP_32 = register("noise_heightmap_32", new NoiseHeightmap32Decorator(NoiseDependantDecoratorConfiguration::deserialize));
   public static final FeatureDecorator NOISE_HEIGHTMAP_DOUBLE = register("noise_heightmap_double", new NoiseHeightmapDoubleDecorator(NoiseDependantDecoratorConfiguration::deserialize));
   public static final FeatureDecorator CHANCE_HEIGHTMAP = register("chance_heightmap", new ChanceHeightmapDecorator(ChanceDecoratorConfiguration::deserialize));
   public static final FeatureDecorator CHANCE_HEIGHTMAP_DOUBLE = register("chance_heightmap_double", new ChanceHeightmapDoubleDecorator(ChanceDecoratorConfiguration::deserialize));
   public static final FeatureDecorator CHANCE_PASSTHROUGH = register("chance_passthrough", new ChancePassthroughDecorator(ChanceDecoratorConfiguration::deserialize));
   public static final FeatureDecorator CHANCE_TOP_SOLID_HEIGHTMAP = register("chance_top_solid_heightmap", new ChanceTopSolidHeightmapDecorator(ChanceDecoratorConfiguration::deserialize));
   public static final FeatureDecorator COUNT_EXTRA_HEIGHTMAP = register("count_extra_heightmap", new CountWithExtraChanceHeightmapDecorator(FrequencyWithExtraChanceDecoratorConfiguration::deserialize));
   public static final FeatureDecorator COUNT_RANGE = register("count_range", new CountRangeDecorator(CountRangeDecoratorConfiguration::deserialize));
   public static final FeatureDecorator COUNT_BIASED_RANGE = register("count_biased_range", new CountBiasedRangeDecorator(CountRangeDecoratorConfiguration::deserialize));
   public static final FeatureDecorator COUNT_VERY_BIASED_RANGE = register("count_very_biased_range", new CountVeryBiasedRangeDecorator(CountRangeDecoratorConfiguration::deserialize));
   public static final FeatureDecorator RANDOM_COUNT_RANGE = register("random_count_range", new RandomCountRangeDecorator(CountRangeDecoratorConfiguration::deserialize));
   public static final FeatureDecorator CHANCE_RANGE = register("chance_range", new ChanceRangeDecorator(ChanceRangeDecoratorConfiguration::deserialize));
   public static final FeatureDecorator COUNT_CHANCE_HEIGHTMAP = register("count_chance_heightmap", new CountChanceHeightmapDecorator(FrequencyChanceDecoratorConfiguration::deserialize));
   public static final FeatureDecorator COUNT_CHANCE_HEIGHTMAP_DOUBLE = register("count_chance_heightmap_double", new CountChanceHeightmapDoubleDecorator(FrequencyChanceDecoratorConfiguration::deserialize));
   public static final FeatureDecorator COUNT_DEPTH_AVERAGE = register("count_depth_average", new CountDepthAverageDecorator(DepthAverageConfigation::deserialize));
   public static final FeatureDecorator TOP_SOLID_HEIGHTMAP = register("top_solid_heightmap", new TopSolidHeightMapDecorator(NoneDecoratorConfiguration::deserialize));
   public static final FeatureDecorator TOP_SOLID_HEIGHTMAP_RANGE = register("top_solid_heightmap_range", new TopSolidHeightMapRangeDecorator(RangeDecoratorConfiguration::deserialize));
   public static final FeatureDecorator TOP_SOLID_HEIGHTMAP_NOISE_BIASED = register("top_solid_heightmap_noise_biased", new TopSolidHeightMapNoiseBasedDecorator(NoiseCountFactorDecoratorConfiguration::deserialize));
   public static final FeatureDecorator CARVING_MASK = register("carving_mask", new CarvingMaskDecorator(CarvingMaskDecoratorConfiguration::deserialize));
   public static final FeatureDecorator FOREST_ROCK = register("forest_rock", new ForestRockPlacementDecorator(FrequencyDecoratorConfiguration::deserialize));
   public static final FeatureDecorator HELL_FIRE = register("hell_fire", new HellFireDecorator(FrequencyDecoratorConfiguration::deserialize));
   public static final FeatureDecorator MAGMA = register("magma", new MagmaDecorator(FrequencyDecoratorConfiguration::deserialize));
   public static final FeatureDecorator EMERALD_ORE = register("emerald_ore", new EmeraldPlacementDecorator(NoneDecoratorConfiguration::deserialize));
   public static final FeatureDecorator LAVA_LAKE = register("lava_lake", new LakeLavaPlacementDecorator(ChanceDecoratorConfiguration::deserialize));
   public static final FeatureDecorator WATER_LAKE = register("water_lake", new LakeWaterPlacementDecorator(ChanceDecoratorConfiguration::deserialize));
   public static final FeatureDecorator DUNGEONS = register("dungeons", new MonsterRoomPlacementDecorator(ChanceDecoratorConfiguration::deserialize));
   public static final FeatureDecorator DARK_OAK_TREE = register("dark_oak_tree", new DarkOakTreePlacementDecorator(NoneDecoratorConfiguration::deserialize));
   public static final FeatureDecorator ICEBERG = register("iceberg", new IcebergPlacementDecorator(ChanceDecoratorConfiguration::deserialize));
   public static final FeatureDecorator LIGHT_GEM_CHANCE = register("light_gem_chance", new LightGemChanceDecorator(FrequencyDecoratorConfiguration::deserialize));
   public static final FeatureDecorator END_ISLAND = register("end_island", new EndIslandPlacementDecorator(NoneDecoratorConfiguration::deserialize));
   public static final FeatureDecorator CHORUS_PLANT = register("chorus_plant", new ChorusPlantPlacementDecorator(NoneDecoratorConfiguration::deserialize));
   public static final FeatureDecorator END_GATEWAY = register("end_gateway", new EndGatewayPlacementDecorator(NoneDecoratorConfiguration::deserialize));
   private final Function configurationFactory;

   private static FeatureDecorator register(String var0, FeatureDecorator var1) {
      return (FeatureDecorator)Registry.register(Registry.DECORATOR, (String)var0, var1);
   }

   public FeatureDecorator(Function var1) {
      this.configurationFactory = var1;
   }

   public DecoratorConfiguration createSettings(Dynamic var1) {
      return (DecoratorConfiguration)this.configurationFactory.apply(var1);
   }

   public ConfiguredDecorator configured(DecoratorConfiguration var1) {
      return new ConfiguredDecorator(this, var1);
   }

   protected boolean placeFeature(LevelAccessor var1, ChunkGenerator var2, Random var3, BlockPos var4, DecoratorConfiguration var5, ConfiguredFeature var6) {
      AtomicBoolean var7 = new AtomicBoolean(false);
      this.getPositions(var1, var2, var3, var5, var4).forEach((var5x) -> {
         boolean var6x = var6.place(var1, var2, var3, var5x);
         var7.set(var7.get() || var6x);
      });
      return var7.get();
   }

   public abstract Stream getPositions(LevelAccessor var1, ChunkGenerator var2, Random var3, DecoratorConfiguration var4, BlockPos var5);

   public String toString() {
      return this.getClass().getSimpleName() + "@" + Integer.toHexString(this.hashCode());
   }
}
