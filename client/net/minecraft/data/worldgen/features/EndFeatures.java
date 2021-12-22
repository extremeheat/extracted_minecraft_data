package net.minecraft.data.worldgen.features;

import com.google.common.collect.ImmutableList;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.EndGatewayConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SpikeConfiguration;

public class EndFeatures {
   public static final ConfiguredFeature<?, ?> END_SPIKE;
   public static final ConfiguredFeature<?, ?> END_GATEWAY_RETURN;
   public static final ConfiguredFeature<?, ?> END_GATEWAY_DELAYED;
   public static final ConfiguredFeature<?, ?> CHORUS_PLANT;
   public static final ConfiguredFeature<?, ?> END_ISLAND;

   public EndFeatures() {
      super();
   }

   static {
      END_SPIKE = FeatureUtils.register("end_spike", Feature.END_SPIKE.configured(new SpikeConfiguration(false, ImmutableList.of(), (BlockPos)null)));
      END_GATEWAY_RETURN = FeatureUtils.register("end_gateway_return", Feature.END_GATEWAY.configured(EndGatewayConfiguration.knownExit(ServerLevel.END_SPAWN_POINT, true)));
      END_GATEWAY_DELAYED = FeatureUtils.register("end_gateway_delayed", Feature.END_GATEWAY.configured(EndGatewayConfiguration.delayedExitSearch()));
      CHORUS_PLANT = FeatureUtils.register("chorus_plant", Feature.CHORUS_PLANT.configured(FeatureConfiguration.NONE));
      END_ISLAND = FeatureUtils.register("end_island", Feature.END_ISLAND.configured(FeatureConfiguration.NONE));
   }
}
