package net.minecraft.data.worldgen.features;

import net.minecraft.core.HolderSet;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.CoralClawFeature;
import net.minecraft.world.level.levelgen.feature.CoralMushroomFeature;
import net.minecraft.world.level.levelgen.feature.CoralTreeFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.KelpFeature;
import net.minecraft.world.level.levelgen.feature.SeaPickleFeature;
import net.minecraft.world.level.levelgen.feature.SeagrassFeature;
import net.minecraft.world.level.levelgen.feature.SimpleRandomSelectorFeature;

public class AquaticFeatures {
   public static final ResourceKey<Feature> SEAGRASS_SHORT = FeatureUtils.createKey("seagrass_short");
   public static final ResourceKey<Feature> SEAGRASS_SLIGHTLY_LESS_SHORT = FeatureUtils.createKey("seagrass_slightly_less_short");
   public static final ResourceKey<Feature> SEAGRASS_MID = FeatureUtils.createKey("seagrass_mid");
   public static final ResourceKey<Feature> SEAGRASS_TALL = FeatureUtils.createKey("seagrass_tall");
   public static final ResourceKey<Feature> SEA_PICKLE = FeatureUtils.createKey("sea_pickle");
   public static final ResourceKey<Feature> KELP = FeatureUtils.createKey("kelp");
   public static final ResourceKey<Feature> WARM_OCEAN_VEGETATION = FeatureUtils.createKey("warm_ocean_vegetation");

   public AquaticFeatures() {
      super();
   }

   public static void bootstrap(final BootstrapContext<Feature> context) {
      context.register(SEAGRASS_SHORT, new SeagrassFeature(0.3F));
      context.register(SEAGRASS_SLIGHTLY_LESS_SHORT, new SeagrassFeature(0.4F));
      context.register(SEAGRASS_MID, new SeagrassFeature(0.6F));
      context.register(SEAGRASS_TALL, new SeagrassFeature(0.8F));
      context.register(SEA_PICKLE, new SeaPickleFeature(20));
      context.register(KELP, KelpFeature.INSTANCE);
      context.register(WARM_OCEAN_VEGETATION, new SimpleRandomSelectorFeature(HolderSet.direct(PlacementUtils.inlinePlaced(CoralTreeFeature.INSTANCE), PlacementUtils.inlinePlaced(CoralClawFeature.INSTANCE), PlacementUtils.inlinePlaced(CoralMushroomFeature.INSTANCE))));
   }
}
