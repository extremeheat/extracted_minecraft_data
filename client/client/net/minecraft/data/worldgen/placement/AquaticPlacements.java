package net.minecraft.data.worldgen.placement;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.features.AquaticFeatures;
import net.minecraft.resources.ResourceKey;
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
   public static final ResourceKey<PlacedFeature> SEAGRASS_WARM = PlacementUtils.createKey("seagrass_warm");
   public static final ResourceKey<PlacedFeature> SEAGRASS_NORMAL = PlacementUtils.createKey("seagrass_normal");
   public static final ResourceKey<PlacedFeature> SEAGRASS_COLD = PlacementUtils.createKey("seagrass_cold");
   public static final ResourceKey<PlacedFeature> SEAGRASS_RIVER = PlacementUtils.createKey("seagrass_river");
   public static final ResourceKey<PlacedFeature> SEAGRASS_SWAMP = PlacementUtils.createKey("seagrass_swamp");
   public static final ResourceKey<PlacedFeature> SEAGRASS_DEEP_WARM = PlacementUtils.createKey("seagrass_deep_warm");
   public static final ResourceKey<PlacedFeature> SEAGRASS_DEEP = PlacementUtils.createKey("seagrass_deep");
   public static final ResourceKey<PlacedFeature> SEAGRASS_DEEP_COLD = PlacementUtils.createKey("seagrass_deep_cold");
   public static final ResourceKey<PlacedFeature> SEAGRASS_SIMPLE = PlacementUtils.createKey("seagrass_simple");
   public static final ResourceKey<PlacedFeature> SEA_PICKLE = PlacementUtils.createKey("sea_pickle");
   public static final ResourceKey<PlacedFeature> KELP_COLD = PlacementUtils.createKey("kelp_cold");
   public static final ResourceKey<PlacedFeature> KELP_WARM = PlacementUtils.createKey("kelp_warm");
   public static final ResourceKey<PlacedFeature> WARM_OCEAN_VEGETATION = PlacementUtils.createKey("warm_ocean_vegetation");

   public AquaticPlacements() {
      super();
   }

   private static List<PlacementModifier> seagrassPlacement(int var0) {
      return List.of(InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_TOP_SOLID, CountPlacement.of(var0), BiomeFilter.biome());
   }

   public static void bootstrap(BootstrapContext<PlacedFeature> var0) {
      HolderGetter var1 = var0.lookup(Registries.CONFIGURED_FEATURE);
      Holder.Reference var2 = var1.getOrThrow(AquaticFeatures.SEAGRASS_SHORT);
      Holder.Reference var3 = var1.getOrThrow(AquaticFeatures.SEAGRASS_SLIGHTLY_LESS_SHORT);
      Holder.Reference var4 = var1.getOrThrow(AquaticFeatures.SEAGRASS_MID);
      Holder.Reference var5 = var1.getOrThrow(AquaticFeatures.SEAGRASS_TALL);
      Holder.Reference var6 = var1.getOrThrow(AquaticFeatures.SEAGRASS_SIMPLE);
      Holder.Reference var7 = var1.getOrThrow(AquaticFeatures.SEA_PICKLE);
      Holder.Reference var8 = var1.getOrThrow(AquaticFeatures.KELP);
      Holder.Reference var9 = var1.getOrThrow(AquaticFeatures.WARM_OCEAN_VEGETATION);
      PlacementUtils.register(var0, SEAGRASS_WARM, var2, seagrassPlacement(80));
      PlacementUtils.register(var0, SEAGRASS_NORMAL, var2, seagrassPlacement(48));
      PlacementUtils.register(var0, SEAGRASS_COLD, var2, seagrassPlacement(32));
      PlacementUtils.register(var0, SEAGRASS_RIVER, var3, seagrassPlacement(48));
      PlacementUtils.register(var0, SEAGRASS_SWAMP, var4, seagrassPlacement(64));
      PlacementUtils.register(var0, SEAGRASS_DEEP_WARM, var5, seagrassPlacement(80));
      PlacementUtils.register(var0, SEAGRASS_DEEP, var5, seagrassPlacement(48));
      PlacementUtils.register(var0, SEAGRASS_DEEP_COLD, var5, seagrassPlacement(40));
      PlacementUtils.register(
         var0,
         SEAGRASS_SIMPLE,
         var6,
         CarvingMaskPlacement.forStep(GenerationStep.Carving.LIQUID),
         RarityFilter.onAverageOnceEvery(10),
         BlockPredicateFilter.forPredicate(
            BlockPredicate.allOf(
               BlockPredicate.matchesBlocks(Direction.DOWN.getNormal(), Blocks.STONE),
               BlockPredicate.matchesBlocks(BlockPos.ZERO, Blocks.WATER),
               BlockPredicate.matchesBlocks(Direction.UP.getNormal(), Blocks.WATER)
            )
         ),
         BiomeFilter.biome()
      );
      PlacementUtils.register(
         var0, SEA_PICKLE, var7, RarityFilter.onAverageOnceEvery(16), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_TOP_SOLID, BiomeFilter.biome()
      );
      PlacementUtils.register(
         var0,
         KELP_COLD,
         var8,
         NoiseBasedCountPlacement.of(120, 80.0, 0.0),
         InSquarePlacement.spread(),
         PlacementUtils.HEIGHTMAP_TOP_SOLID,
         BiomeFilter.biome()
      );
      PlacementUtils.register(
         var0, KELP_WARM, var8, NoiseBasedCountPlacement.of(80, 80.0, 0.0), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_TOP_SOLID, BiomeFilter.biome()
      );
      PlacementUtils.register(
         var0,
         WARM_OCEAN_VEGETATION,
         var9,
         NoiseBasedCountPlacement.of(20, 400.0, 0.0),
         InSquarePlacement.spread(),
         PlacementUtils.HEIGHTMAP_TOP_SOLID,
         BiomeFilter.biome()
      );
   }
}
