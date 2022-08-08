package net.minecraft.data.worldgen.placement;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.features.AquaticFeatures;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.BlockPredicateFilter;
import net.minecraft.world.level.levelgen.placement.CarvingMaskPlacement;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.NoiseBasedCountPlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.RarityFilter;

public class AquaticPlacements {
   public static final Holder<PlacedFeature> SEAGRASS_WARM;
   public static final Holder<PlacedFeature> SEAGRASS_NORMAL;
   public static final Holder<PlacedFeature> SEAGRASS_COLD;
   public static final Holder<PlacedFeature> SEAGRASS_RIVER;
   public static final Holder<PlacedFeature> SEAGRASS_SWAMP;
   public static final Holder<PlacedFeature> SEAGRASS_DEEP_WARM;
   public static final Holder<PlacedFeature> SEAGRASS_DEEP;
   public static final Holder<PlacedFeature> SEAGRASS_DEEP_COLD;
   public static final Holder<PlacedFeature> SEAGRASS_SIMPLE;
   public static final Holder<PlacedFeature> SEA_PICKLE;
   public static final Holder<PlacedFeature> KELP_COLD;
   public static final Holder<PlacedFeature> KELP_WARM;
   public static final Holder<PlacedFeature> WARM_OCEAN_VEGETATION;

   public AquaticPlacements() {
      super();
   }

   public static List<PlacementModifier> seagrassPlacement(int var0) {
      return List.of(InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_TOP_SOLID, CountPlacement.of(var0), BiomeFilter.biome());
   }

   static {
      SEAGRASS_WARM = PlacementUtils.register("seagrass_warm", AquaticFeatures.SEAGRASS_SHORT, seagrassPlacement(80));
      SEAGRASS_NORMAL = PlacementUtils.register("seagrass_normal", AquaticFeatures.SEAGRASS_SHORT, seagrassPlacement(48));
      SEAGRASS_COLD = PlacementUtils.register("seagrass_cold", AquaticFeatures.SEAGRASS_SHORT, seagrassPlacement(32));
      SEAGRASS_RIVER = PlacementUtils.register("seagrass_river", AquaticFeatures.SEAGRASS_SLIGHTLY_LESS_SHORT, seagrassPlacement(48));
      SEAGRASS_SWAMP = PlacementUtils.register("seagrass_swamp", AquaticFeatures.SEAGRASS_MID, seagrassPlacement(64));
      SEAGRASS_DEEP_WARM = PlacementUtils.register("seagrass_deep_warm", AquaticFeatures.SEAGRASS_TALL, seagrassPlacement(80));
      SEAGRASS_DEEP = PlacementUtils.register("seagrass_deep", AquaticFeatures.SEAGRASS_TALL, seagrassPlacement(48));
      SEAGRASS_DEEP_COLD = PlacementUtils.register("seagrass_deep_cold", AquaticFeatures.SEAGRASS_TALL, seagrassPlacement(40));
      SEAGRASS_SIMPLE = PlacementUtils.register("seagrass_simple", AquaticFeatures.SEAGRASS_SIMPLE, CarvingMaskPlacement.forStep(GenerationStep.Carving.LIQUID), RarityFilter.onAverageOnceEvery(10), BlockPredicateFilter.forPredicate(BlockPredicate.allOf(BlockPredicate.matchesBlocks(Direction.DOWN.getNormal(), Blocks.STONE), BlockPredicate.matchesBlocks(BlockPos.ZERO, (Block[])(Blocks.WATER)), BlockPredicate.matchesBlocks(Direction.UP.getNormal(), Blocks.WATER))), BiomeFilter.biome());
      SEA_PICKLE = PlacementUtils.register("sea_pickle", AquaticFeatures.SEA_PICKLE, RarityFilter.onAverageOnceEvery(16), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_TOP_SOLID, BiomeFilter.biome());
      KELP_COLD = PlacementUtils.register("kelp_cold", AquaticFeatures.KELP, NoiseBasedCountPlacement.of(120, 80.0, 0.0), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_TOP_SOLID, BiomeFilter.biome());
      KELP_WARM = PlacementUtils.register("kelp_warm", AquaticFeatures.KELP, NoiseBasedCountPlacement.of(80, 80.0, 0.0), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_TOP_SOLID, BiomeFilter.biome());
      WARM_OCEAN_VEGETATION = PlacementUtils.register("warm_ocean_vegetation", AquaticFeatures.WARM_OCEAN_VEGETATION, NoiseBasedCountPlacement.of(20, 400.0, 0.0), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_TOP_SOLID, BiomeFilter.biome());
   }
}
