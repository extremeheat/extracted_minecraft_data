package net.minecraft.data.worldgen.features;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.OptionalInt;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HugeMushroomBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.HugeFungusConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.HugeMushroomFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.featuresize.ThreeLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.AcaciaFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.BlobFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.BushFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.DarkOakFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FancyFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.MegaJungleFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.MegaPineFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.PineFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.RandomSpreadFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.SpruceFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;
import net.minecraft.world.level.levelgen.feature.treedecorators.AlterGroundDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.BeehiveDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.CocoaDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.LeaveVineDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TrunkVineDecorator;
import net.minecraft.world.level.levelgen.feature.trunkplacers.BendingTrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.DarkOakTrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.FancyTrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.ForkingTrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.GiantTrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.MegaJungleTrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer;

public class TreeFeatures {
   public static final ConfiguredFeature<HugeFungusConfiguration, ?> CRIMSON_FUNGUS;
   public static final ConfiguredFeature<HugeFungusConfiguration, ?> CRIMSON_FUNGUS_PLANTED;
   public static final ConfiguredFeature<HugeFungusConfiguration, ?> WARPED_FUNGUS;
   public static final ConfiguredFeature<HugeFungusConfiguration, ?> WARPED_FUNGUS_PLANTED;
   public static final ConfiguredFeature<?, ?> HUGE_BROWN_MUSHROOM;
   public static final ConfiguredFeature<?, ?> HUGE_RED_MUSHROOM;
   private static final BeehiveDecorator BEEHIVE_0002;
   private static final BeehiveDecorator BEEHIVE_002;
   private static final BeehiveDecorator BEEHIVE_005;
   private static final BeehiveDecorator BEEHIVE;
   public static final ConfiguredFeature<TreeConfiguration, ?> OAK;
   public static final ConfiguredFeature<TreeConfiguration, ?> DARK_OAK;
   public static final ConfiguredFeature<TreeConfiguration, ?> BIRCH;
   public static final ConfiguredFeature<TreeConfiguration, ?> ACACIA;
   public static final ConfiguredFeature<TreeConfiguration, ?> SPRUCE;
   public static final ConfiguredFeature<TreeConfiguration, ?> PINE;
   public static final ConfiguredFeature<TreeConfiguration, ?> JUNGLE_TREE;
   public static final ConfiguredFeature<TreeConfiguration, ?> FANCY_OAK;
   public static final ConfiguredFeature<?, ?> JUNGLE_TREE_NO_VINE;
   public static final ConfiguredFeature<TreeConfiguration, ?> MEGA_JUNGLE_TREE;
   public static final ConfiguredFeature<TreeConfiguration, ?> MEGA_SPRUCE;
   public static final ConfiguredFeature<TreeConfiguration, ?> MEGA_PINE;
   public static final ConfiguredFeature<TreeConfiguration, ?> SUPER_BIRCH_BEES_0002;
   public static final ConfiguredFeature<TreeConfiguration, ?> SUPER_BIRCH_BEES;
   public static final ConfiguredFeature<TreeConfiguration, ?> SWAMP_OAK;
   public static final ConfiguredFeature<TreeConfiguration, ?> JUNGLE_BUSH;
   public static final ConfiguredFeature<?, ?> AZALEA_TREE;
   public static final ConfiguredFeature<TreeConfiguration, ?> OAK_BEES_0002;
   public static final ConfiguredFeature<TreeConfiguration, ?> OAK_BEES_002;
   public static final ConfiguredFeature<TreeConfiguration, ?> OAK_BEES_005;
   public static final ConfiguredFeature<TreeConfiguration, ?> BIRCH_BEES_0002;
   public static final ConfiguredFeature<TreeConfiguration, ?> BIRCH_BEES_002;
   public static final ConfiguredFeature<TreeConfiguration, ?> BIRCH_BEES_005;
   public static final ConfiguredFeature<TreeConfiguration, ?> FANCY_OAK_BEES_0002;
   public static final ConfiguredFeature<TreeConfiguration, ?> FANCY_OAK_BEES_002;
   public static final ConfiguredFeature<TreeConfiguration, ?> FANCY_OAK_BEES_005;
   public static final ConfiguredFeature<TreeConfiguration, ?> FANCY_OAK_BEES;

   public TreeFeatures() {
      super();
   }

   private static TreeConfiguration.TreeConfigurationBuilder createStraightBlobTree(Block var0, Block var1, int var2, int var3, int var4, int var5) {
      return new TreeConfiguration.TreeConfigurationBuilder(BlockStateProvider.simple(var0), new StraightTrunkPlacer(var2, var3, var4), BlockStateProvider.simple(var1), new BlobFoliagePlacer(ConstantInt.method_49(var5), ConstantInt.method_49(0), 3), new TwoLayersFeatureSize(1, 0, 1));
   }

   private static TreeConfiguration.TreeConfigurationBuilder createOak() {
      return createStraightBlobTree(Blocks.OAK_LOG, Blocks.OAK_LEAVES, 4, 2, 0, 2).ignoreVines();
   }

   private static TreeConfiguration.TreeConfigurationBuilder createBirch() {
      return createStraightBlobTree(Blocks.BIRCH_LOG, Blocks.BIRCH_LEAVES, 5, 2, 0, 2).ignoreVines();
   }

   private static TreeConfiguration.TreeConfigurationBuilder createSuperBirch() {
      return createStraightBlobTree(Blocks.BIRCH_LOG, Blocks.BIRCH_LEAVES, 5, 2, 6, 2).ignoreVines();
   }

   private static TreeConfiguration.TreeConfigurationBuilder createJungleTree() {
      return createStraightBlobTree(Blocks.JUNGLE_LOG, Blocks.JUNGLE_LEAVES, 4, 8, 0, 2);
   }

   private static TreeConfiguration.TreeConfigurationBuilder createFancyOak() {
      return (new TreeConfiguration.TreeConfigurationBuilder(BlockStateProvider.simple(Blocks.OAK_LOG), new FancyTrunkPlacer(3, 11, 0), BlockStateProvider.simple(Blocks.OAK_LEAVES), new FancyFoliagePlacer(ConstantInt.method_49(2), ConstantInt.method_49(4), 4), new TwoLayersFeatureSize(0, 0, 0, OptionalInt.of(4)))).ignoreVines();
   }

   static {
      CRIMSON_FUNGUS = FeatureUtils.register("crimson_fungus", Feature.HUGE_FUNGUS.configured(new HugeFungusConfiguration(Blocks.CRIMSON_NYLIUM.defaultBlockState(), Blocks.CRIMSON_STEM.defaultBlockState(), Blocks.NETHER_WART_BLOCK.defaultBlockState(), Blocks.SHROOMLIGHT.defaultBlockState(), false)));
      CRIMSON_FUNGUS_PLANTED = FeatureUtils.register("crimson_fungus_planted", Feature.HUGE_FUNGUS.configured(new HugeFungusConfiguration(Blocks.CRIMSON_NYLIUM.defaultBlockState(), Blocks.CRIMSON_STEM.defaultBlockState(), Blocks.NETHER_WART_BLOCK.defaultBlockState(), Blocks.SHROOMLIGHT.defaultBlockState(), true)));
      WARPED_FUNGUS = FeatureUtils.register("warped_fungus", Feature.HUGE_FUNGUS.configured(new HugeFungusConfiguration(Blocks.WARPED_NYLIUM.defaultBlockState(), Blocks.WARPED_STEM.defaultBlockState(), Blocks.WARPED_WART_BLOCK.defaultBlockState(), Blocks.SHROOMLIGHT.defaultBlockState(), false)));
      WARPED_FUNGUS_PLANTED = FeatureUtils.register("warped_fungus_planted", Feature.HUGE_FUNGUS.configured(new HugeFungusConfiguration(Blocks.WARPED_NYLIUM.defaultBlockState(), Blocks.WARPED_STEM.defaultBlockState(), Blocks.WARPED_WART_BLOCK.defaultBlockState(), Blocks.SHROOMLIGHT.defaultBlockState(), true)));
      HUGE_BROWN_MUSHROOM = FeatureUtils.register("huge_brown_mushroom", Feature.HUGE_BROWN_MUSHROOM.configured(new HugeMushroomFeatureConfiguration(BlockStateProvider.simple((BlockState)((BlockState)Blocks.BROWN_MUSHROOM_BLOCK.defaultBlockState().setValue(HugeMushroomBlock.field_54, true)).setValue(HugeMushroomBlock.DOWN, false)), BlockStateProvider.simple((BlockState)((BlockState)Blocks.MUSHROOM_STEM.defaultBlockState().setValue(HugeMushroomBlock.field_54, false)).setValue(HugeMushroomBlock.DOWN, false)), 3)));
      HUGE_RED_MUSHROOM = FeatureUtils.register("huge_red_mushroom", Feature.HUGE_RED_MUSHROOM.configured(new HugeMushroomFeatureConfiguration(BlockStateProvider.simple((BlockState)Blocks.RED_MUSHROOM_BLOCK.defaultBlockState().setValue(HugeMushroomBlock.DOWN, false)), BlockStateProvider.simple((BlockState)((BlockState)Blocks.MUSHROOM_STEM.defaultBlockState().setValue(HugeMushroomBlock.field_54, false)).setValue(HugeMushroomBlock.DOWN, false)), 2)));
      BEEHIVE_0002 = new BeehiveDecorator(0.002F);
      BEEHIVE_002 = new BeehiveDecorator(0.02F);
      BEEHIVE_005 = new BeehiveDecorator(0.05F);
      BEEHIVE = new BeehiveDecorator(1.0F);
      OAK = FeatureUtils.register("oak", Feature.TREE.configured(createOak().build()));
      DARK_OAK = FeatureUtils.register("dark_oak", Feature.TREE.configured((new TreeConfiguration.TreeConfigurationBuilder(BlockStateProvider.simple(Blocks.DARK_OAK_LOG), new DarkOakTrunkPlacer(6, 2, 1), BlockStateProvider.simple(Blocks.DARK_OAK_LEAVES), new DarkOakFoliagePlacer(ConstantInt.method_49(0), ConstantInt.method_49(0)), new ThreeLayersFeatureSize(1, 1, 0, 1, 2, OptionalInt.empty()))).ignoreVines().build()));
      BIRCH = FeatureUtils.register("birch", Feature.TREE.configured(createBirch().build()));
      ACACIA = FeatureUtils.register("acacia", Feature.TREE.configured((new TreeConfiguration.TreeConfigurationBuilder(BlockStateProvider.simple(Blocks.ACACIA_LOG), new ForkingTrunkPlacer(5, 2, 2), BlockStateProvider.simple(Blocks.ACACIA_LEAVES), new AcaciaFoliagePlacer(ConstantInt.method_49(2), ConstantInt.method_49(0)), new TwoLayersFeatureSize(1, 0, 2))).ignoreVines().build()));
      SPRUCE = FeatureUtils.register("spruce", Feature.TREE.configured((new TreeConfiguration.TreeConfigurationBuilder(BlockStateProvider.simple(Blocks.SPRUCE_LOG), new StraightTrunkPlacer(5, 2, 1), BlockStateProvider.simple(Blocks.SPRUCE_LEAVES), new SpruceFoliagePlacer(UniformInt.method_45(2, 3), UniformInt.method_45(0, 2), UniformInt.method_45(1, 2)), new TwoLayersFeatureSize(2, 0, 2))).ignoreVines().build()));
      PINE = FeatureUtils.register("pine", Feature.TREE.configured((new TreeConfiguration.TreeConfigurationBuilder(BlockStateProvider.simple(Blocks.SPRUCE_LOG), new StraightTrunkPlacer(6, 4, 0), BlockStateProvider.simple(Blocks.SPRUCE_LEAVES), new PineFoliagePlacer(ConstantInt.method_49(1), ConstantInt.method_49(1), UniformInt.method_45(3, 4)), new TwoLayersFeatureSize(2, 0, 2))).ignoreVines().build()));
      JUNGLE_TREE = FeatureUtils.register("jungle_tree", Feature.TREE.configured(createJungleTree().decorators(ImmutableList.of(new CocoaDecorator(0.2F), TrunkVineDecorator.INSTANCE, LeaveVineDecorator.INSTANCE)).ignoreVines().build()));
      FANCY_OAK = FeatureUtils.register("fancy_oak", Feature.TREE.configured(createFancyOak().build()));
      JUNGLE_TREE_NO_VINE = FeatureUtils.register("jungle_tree_no_vine", Feature.TREE.configured(createJungleTree().ignoreVines().build()));
      MEGA_JUNGLE_TREE = FeatureUtils.register("mega_jungle_tree", Feature.TREE.configured((new TreeConfiguration.TreeConfigurationBuilder(BlockStateProvider.simple(Blocks.JUNGLE_LOG), new MegaJungleTrunkPlacer(10, 2, 19), BlockStateProvider.simple(Blocks.JUNGLE_LEAVES), new MegaJungleFoliagePlacer(ConstantInt.method_49(2), ConstantInt.method_49(0), 2), new TwoLayersFeatureSize(1, 1, 2))).decorators(ImmutableList.of(TrunkVineDecorator.INSTANCE, LeaveVineDecorator.INSTANCE)).build()));
      MEGA_SPRUCE = FeatureUtils.register("mega_spruce", Feature.TREE.configured((new TreeConfiguration.TreeConfigurationBuilder(BlockStateProvider.simple(Blocks.SPRUCE_LOG), new GiantTrunkPlacer(13, 2, 14), BlockStateProvider.simple(Blocks.SPRUCE_LEAVES), new MegaPineFoliagePlacer(ConstantInt.method_49(0), ConstantInt.method_49(0), UniformInt.method_45(13, 17)), new TwoLayersFeatureSize(1, 1, 2))).decorators(ImmutableList.of(new AlterGroundDecorator(BlockStateProvider.simple(Blocks.PODZOL)))).build()));
      MEGA_PINE = FeatureUtils.register("mega_pine", Feature.TREE.configured((new TreeConfiguration.TreeConfigurationBuilder(BlockStateProvider.simple(Blocks.SPRUCE_LOG), new GiantTrunkPlacer(13, 2, 14), BlockStateProvider.simple(Blocks.SPRUCE_LEAVES), new MegaPineFoliagePlacer(ConstantInt.method_49(0), ConstantInt.method_49(0), UniformInt.method_45(3, 7)), new TwoLayersFeatureSize(1, 1, 2))).decorators(ImmutableList.of(new AlterGroundDecorator(BlockStateProvider.simple(Blocks.PODZOL)))).build()));
      SUPER_BIRCH_BEES_0002 = FeatureUtils.register("super_birch_bees_0002", Feature.TREE.configured(createSuperBirch().decorators(ImmutableList.of(BEEHIVE_0002)).build()));
      SUPER_BIRCH_BEES = FeatureUtils.register("super_birch_bees", Feature.TREE.configured(createSuperBirch().decorators(ImmutableList.of(BEEHIVE)).build()));
      SWAMP_OAK = FeatureUtils.register("swamp_oak", Feature.TREE.configured(createStraightBlobTree(Blocks.OAK_LOG, Blocks.OAK_LEAVES, 5, 3, 0, 3).decorators(ImmutableList.of(LeaveVineDecorator.INSTANCE)).build()));
      JUNGLE_BUSH = FeatureUtils.register("jungle_bush", Feature.TREE.configured((new TreeConfiguration.TreeConfigurationBuilder(BlockStateProvider.simple(Blocks.JUNGLE_LOG), new StraightTrunkPlacer(1, 0, 0), BlockStateProvider.simple(Blocks.OAK_LEAVES), new BushFoliagePlacer(ConstantInt.method_49(2), ConstantInt.method_49(1), 2), new TwoLayersFeatureSize(0, 0, 0))).build()));
      AZALEA_TREE = FeatureUtils.register("azalea_tree", Feature.TREE.configured((new TreeConfiguration.TreeConfigurationBuilder(BlockStateProvider.simple(Blocks.OAK_LOG), new BendingTrunkPlacer(4, 2, 0, 3, UniformInt.method_45(1, 2)), new WeightedStateProvider(SimpleWeightedRandomList.builder().add(Blocks.AZALEA_LEAVES.defaultBlockState(), 3).add(Blocks.FLOWERING_AZALEA_LEAVES.defaultBlockState(), 1)), new RandomSpreadFoliagePlacer(ConstantInt.method_49(3), ConstantInt.method_49(0), ConstantInt.method_49(2), 50), new TwoLayersFeatureSize(1, 0, 1))).dirt(BlockStateProvider.simple(Blocks.ROOTED_DIRT)).forceDirt().build()));
      OAK_BEES_0002 = FeatureUtils.register("oak_bees_0002", Feature.TREE.configured(createOak().decorators(List.of(BEEHIVE_0002)).build()));
      OAK_BEES_002 = FeatureUtils.register("oak_bees_002", Feature.TREE.configured(createOak().decorators(List.of(BEEHIVE_002)).build()));
      OAK_BEES_005 = FeatureUtils.register("oak_bees_005", Feature.TREE.configured(createOak().decorators(List.of(BEEHIVE_005)).build()));
      BIRCH_BEES_0002 = FeatureUtils.register("birch_bees_0002", Feature.TREE.configured(createBirch().decorators(List.of(BEEHIVE_0002)).build()));
      BIRCH_BEES_002 = FeatureUtils.register("birch_bees_002", Feature.TREE.configured(createBirch().decorators(List.of(BEEHIVE_002)).build()));
      BIRCH_BEES_005 = FeatureUtils.register("birch_bees_005", Feature.TREE.configured(createBirch().decorators(List.of(BEEHIVE_005)).build()));
      FANCY_OAK_BEES_0002 = FeatureUtils.register("fancy_oak_bees_0002", Feature.TREE.configured(createFancyOak().decorators(List.of(BEEHIVE_0002)).build()));
      FANCY_OAK_BEES_002 = FeatureUtils.register("fancy_oak_bees_002", Feature.TREE.configured(createFancyOak().decorators(List.of(BEEHIVE_002)).build()));
      FANCY_OAK_BEES_005 = FeatureUtils.register("fancy_oak_bees_005", Feature.TREE.configured(createFancyOak().decorators(List.of(BEEHIVE_005)).build()));
      FANCY_OAK_BEES = FeatureUtils.register("fancy_oak_bees", Feature.TREE.configured(createFancyOak().decorators(List.of(BEEHIVE)).build()));
   }
}
