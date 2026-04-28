package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.IntProviders;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.levelgen.GeodeBlockSettings;
import net.minecraft.world.level.levelgen.GeodeCrackSettings;
import net.minecraft.world.level.levelgen.GeodeLayerSettings;

public record GeodeConfiguration(GeodeBlockSettings geodeBlockSettings, GeodeLayerSettings geodeLayerSettings, GeodeCrackSettings geodeCrackSettings, double usePotentialPlacementsChance, double useAlternateLayer0Chance, boolean placementsRequireLayer0Alternate, IntProvider outerWallDistance, IntProvider distributionPoints, IntProvider pointOffset, int minGenOffset, int maxGenOffset, double noiseMultiplier, int invalidBlocksThreshold) implements FeatureConfiguration {
   public static final Codec<Double> CHANCE_RANGE = Codec.doubleRange(0.0, 1.0);
   public static final Codec<GeodeConfiguration> CODEC = RecordCodecBuilder.create((i) -> i.group(GeodeBlockSettings.CODEC.fieldOf("blocks").forGetter(GeodeConfiguration::geodeBlockSettings), GeodeLayerSettings.CODEC.fieldOf("layers").forGetter(GeodeConfiguration::geodeLayerSettings), GeodeCrackSettings.CODEC.fieldOf("crack").forGetter(GeodeConfiguration::geodeCrackSettings), CHANCE_RANGE.optionalFieldOf("use_potential_placements_chance", 0.35).forGetter(GeodeConfiguration::usePotentialPlacementsChance), CHANCE_RANGE.optionalFieldOf("use_alternate_layer0_chance", 0.0).forGetter(GeodeConfiguration::useAlternateLayer0Chance), Codec.BOOL.optionalFieldOf("placements_require_layer0_alternate", true).forGetter(GeodeConfiguration::placementsRequireLayer0Alternate), IntProviders.codec(1, 20).optionalFieldOf("outer_wall_distance", UniformInt.of(4, 5)).forGetter(GeodeConfiguration::outerWallDistance), IntProviders.codec(1, 20).optionalFieldOf("distribution_points", UniformInt.of(3, 4)).forGetter(GeodeConfiguration::distributionPoints), IntProviders.codec(0, 10).optionalFieldOf("point_offset", UniformInt.of(1, 2)).forGetter(GeodeConfiguration::pointOffset), Codec.INT.optionalFieldOf("min_gen_offset", -16).forGetter(GeodeConfiguration::minGenOffset), Codec.INT.optionalFieldOf("max_gen_offset", 16).forGetter(GeodeConfiguration::maxGenOffset), CHANCE_RANGE.optionalFieldOf("noise_multiplier", 0.05).forGetter(GeodeConfiguration::noiseMultiplier), Codec.INT.fieldOf("invalid_blocks_threshold").forGetter(GeodeConfiguration::invalidBlocksThreshold)).apply(i, GeodeConfiguration::new));

   public GeodeConfiguration {
      super();
   }
}
