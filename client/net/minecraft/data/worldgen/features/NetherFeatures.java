package net.minecraft.data.worldgen.features;

import net.minecraft.core.HolderSet;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.random.WeightedList;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.BasaltColumnsFeature;
import net.minecraft.world.level.levelgen.feature.BasaltPillarFeature;
import net.minecraft.world.level.levelgen.feature.DeltaFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.GlowstoneFeature;
import net.minecraft.world.level.levelgen.feature.NetherForestVegetationFeature;
import net.minecraft.world.level.levelgen.feature.ReplaceBlobsFeature;
import net.minecraft.world.level.levelgen.feature.SimpleBlockFeature;
import net.minecraft.world.level.levelgen.feature.SpringFeature;
import net.minecraft.world.level.levelgen.feature.TwistingVinesFeature;
import net.minecraft.world.level.levelgen.feature.WeepingVinesFeature;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;
import net.minecraft.world.level.material.Fluids;

public class NetherFeatures {
   public static final ResourceKey<Feature> DELTA = FeatureUtils.createKey("delta");
   public static final ResourceKey<Feature> SMALL_BASALT_COLUMNS = FeatureUtils.createKey("small_basalt_columns");
   public static final ResourceKey<Feature> LARGE_BASALT_COLUMNS = FeatureUtils.createKey("large_basalt_columns");
   public static final ResourceKey<Feature> BASALT_BLOBS = FeatureUtils.createKey("basalt_blobs");
   public static final ResourceKey<Feature> BLACKSTONE_BLOBS = FeatureUtils.createKey("blackstone_blobs");
   public static final ResourceKey<Feature> GLOWSTONE_EXTRA = FeatureUtils.createKey("glowstone_extra");
   public static final ResourceKey<Feature> CRIMSON_FOREST_VEGETATION = FeatureUtils.createKey("crimson_forest_vegetation");
   public static final ResourceKey<Feature> CRIMSON_FOREST_VEGETATION_BONEMEAL = FeatureUtils.createKey("crimson_forest_vegetation_bonemeal");
   public static final ResourceKey<Feature> WARPED_FOREST_VEGETION = FeatureUtils.createKey("warped_forest_vegetation");
   public static final ResourceKey<Feature> WARPED_FOREST_VEGETATION_BONEMEAL = FeatureUtils.createKey("warped_forest_vegetation_bonemeal");
   public static final ResourceKey<Feature> NETHER_SPROUTS = FeatureUtils.createKey("nether_sprouts");
   public static final ResourceKey<Feature> NETHER_SPROUTS_BONEMEAL = FeatureUtils.createKey("nether_sprouts_bonemeal");
   public static final ResourceKey<Feature> TWISTING_VINES = FeatureUtils.createKey("twisting_vines");
   public static final ResourceKey<Feature> TWISTING_VINES_BONEMEAL = FeatureUtils.createKey("twisting_vines_bonemeal");
   public static final ResourceKey<Feature> WEEPING_VINES = FeatureUtils.createKey("weeping_vines");
   public static final ResourceKey<Feature> CRIMSON_ROOTS = FeatureUtils.createKey("crimson_roots");
   public static final ResourceKey<Feature> BASALT_PILLAR = FeatureUtils.createKey("basalt_pillar");
   public static final ResourceKey<Feature> SPRING_LAVA_NETHER = FeatureUtils.createKey("spring_lava_nether");
   public static final ResourceKey<Feature> SPRING_NETHER_CLOSED = FeatureUtils.createKey("spring_nether_closed");
   public static final ResourceKey<Feature> SPRING_NETHER_OPEN = FeatureUtils.createKey("spring_nether_open");
   public static final ResourceKey<Feature> FIRE = FeatureUtils.createKey("patch_fire");
   public static final ResourceKey<Feature> SOUL_FIRE = FeatureUtils.createKey("patch_soul_fire");

   public NetherFeatures() {
      super();
   }

   public static void bootstrap(final BootstrapContext<Feature> context) {
      context.register(DELTA, new DeltaFeature(Blocks.LAVA.defaultBlockState(), Blocks.MAGMA_BLOCK.defaultBlockState(), UniformInt.of(3, 7), UniformInt.of(0, 2)));
      context.register(SMALL_BASALT_COLUMNS, new BasaltColumnsFeature(ConstantInt.of(1), UniformInt.of(1, 4)));
      context.register(LARGE_BASALT_COLUMNS, new BasaltColumnsFeature(UniformInt.of(2, 3), UniformInt.of(5, 10)));
      context.register(BASALT_BLOBS, new ReplaceBlobsFeature(Blocks.NETHERRACK.defaultBlockState(), Blocks.BASALT.defaultBlockState(), UniformInt.of(3, 7)));
      context.register(BLACKSTONE_BLOBS, new ReplaceBlobsFeature(Blocks.NETHERRACK.defaultBlockState(), Blocks.BLACKSTONE.defaultBlockState(), UniformInt.of(3, 7)));
      context.register(GLOWSTONE_EXTRA, GlowstoneFeature.INSTANCE);
      WeightedStateProvider crimsonVegetationProvider = new WeightedStateProvider(WeightedList.builder().add(Blocks.CRIMSON_ROOTS.defaultBlockState(), 87).add(Blocks.CRIMSON_FUNGUS.defaultBlockState(), 11).add(Blocks.WARPED_FUNGUS.defaultBlockState(), 1));
      context.register(CRIMSON_FOREST_VEGETATION, new NetherForestVegetationFeature(crimsonVegetationProvider, 8, 4));
      context.register(CRIMSON_FOREST_VEGETATION_BONEMEAL, new NetherForestVegetationFeature(crimsonVegetationProvider, 3, 1));
      WeightedStateProvider warpedVegetationProvider = new WeightedStateProvider(WeightedList.builder().add(Blocks.WARPED_ROOTS.defaultBlockState(), 85).add(Blocks.CRIMSON_ROOTS.defaultBlockState(), 1).add(Blocks.WARPED_FUNGUS.defaultBlockState(), 13).add(Blocks.CRIMSON_FUNGUS.defaultBlockState(), 1));
      context.register(WARPED_FOREST_VEGETION, new NetherForestVegetationFeature(warpedVegetationProvider, 8, 4));
      context.register(WARPED_FOREST_VEGETATION_BONEMEAL, new NetherForestVegetationFeature(warpedVegetationProvider, 3, 1));
      context.register(NETHER_SPROUTS, new NetherForestVegetationFeature(BlockStateProvider.simple(Blocks.NETHER_SPROUTS), 8, 4));
      context.register(NETHER_SPROUTS_BONEMEAL, new NetherForestVegetationFeature(BlockStateProvider.simple(Blocks.NETHER_SPROUTS), 3, 1));
      context.register(TWISTING_VINES, new TwistingVinesFeature(8, 4, 8));
      context.register(TWISTING_VINES_BONEMEAL, new TwistingVinesFeature(3, 1, 2));
      context.register(WEEPING_VINES, WeepingVinesFeature.INSTANCE);
      context.register(CRIMSON_ROOTS, new SimpleBlockFeature(BlockStateProvider.simple(Blocks.CRIMSON_ROOTS)));
      context.register(BASALT_PILLAR, BasaltPillarFeature.INSTANCE);
      context.register(SPRING_LAVA_NETHER, new SpringFeature(Fluids.LAVA.defaultFluidState(), true, 4, 1, HolderSet.direct(Block::builtInRegistryHolder, Blocks.NETHERRACK, Blocks.SOUL_SAND, Blocks.GRAVEL, Blocks.MAGMA_BLOCK, Blocks.BLACKSTONE)));
      context.register(SPRING_NETHER_CLOSED, new SpringFeature(Fluids.LAVA.defaultFluidState(), false, 5, 0, HolderSet.direct(Block::builtInRegistryHolder, Blocks.NETHERRACK)));
      context.register(SPRING_NETHER_OPEN, new SpringFeature(Fluids.LAVA.defaultFluidState(), false, 4, 1, HolderSet.direct(Block::builtInRegistryHolder, Blocks.NETHERRACK)));
      context.register(FIRE, new SimpleBlockFeature(BlockStateProvider.simple(Blocks.FIRE)));
      context.register(SOUL_FIRE, new SimpleBlockFeature(BlockStateProvider.simple(Blocks.SOUL_FIRE)));
   }
}
