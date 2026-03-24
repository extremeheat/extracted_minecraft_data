package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.IntProviders;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.levelgen.GeodeBlockSettings;
import net.minecraft.world.level.levelgen.GeodeCrackSettings;
import net.minecraft.world.level.levelgen.GeodeLayerSettings;

public class GeodeConfiguration implements FeatureConfiguration {
   public static final Codec<Double> CHANCE_RANGE = Codec.doubleRange(0.0, 1.0);
   public static final Codec<GeodeConfiguration> CODEC = RecordCodecBuilder.create((i) -> i.group(GeodeBlockSettings.CODEC.fieldOf("blocks").forGetter((c) -> c.geodeBlockSettings), GeodeLayerSettings.CODEC.fieldOf("layers").forGetter((c) -> c.geodeLayerSettings), GeodeCrackSettings.CODEC.fieldOf("crack").forGetter((c) -> c.geodeCrackSettings), CHANCE_RANGE.fieldOf("use_potential_placements_chance").orElse(0.35).forGetter((c) -> c.usePotentialPlacementsChance), CHANCE_RANGE.fieldOf("use_alternate_layer0_chance").orElse(0.0).forGetter((c) -> c.useAlternateLayer0Chance), Codec.BOOL.fieldOf("placements_require_layer0_alternate").orElse(true).forGetter((c) -> c.placementsRequireLayer0Alternate), IntProviders.codec(1, 20).fieldOf("outer_wall_distance").orElse(UniformInt.of(4, 5)).forGetter((c) -> c.outerWallDistance), IntProviders.codec(1, 20).fieldOf("distribution_points").orElse(UniformInt.of(3, 4)).forGetter((c) -> c.distributionPoints), IntProviders.codec(0, 10).fieldOf("point_offset").orElse(UniformInt.of(1, 2)).forGetter((c) -> c.pointOffset), Codec.INT.fieldOf("min_gen_offset").orElse(-16).forGetter((c) -> c.minGenOffset), Codec.INT.fieldOf("max_gen_offset").orElse(16).forGetter((c) -> c.maxGenOffset), CHANCE_RANGE.fieldOf("noise_multiplier").orElse(0.05).forGetter((c) -> c.noiseMultiplier), Codec.INT.fieldOf("invalid_blocks_threshold").forGetter((c) -> c.invalidBlocksThreshold)).apply(i, GeodeConfiguration::new));
   public final GeodeBlockSettings geodeBlockSettings;
   public final GeodeLayerSettings geodeLayerSettings;
   public final GeodeCrackSettings geodeCrackSettings;
   public final double usePotentialPlacementsChance;
   public final double useAlternateLayer0Chance;
   public final boolean placementsRequireLayer0Alternate;
   public final IntProvider outerWallDistance;
   public final IntProvider distributionPoints;
   public final IntProvider pointOffset;
   public final int minGenOffset;
   public final int maxGenOffset;
   public final double noiseMultiplier;
   public final int invalidBlocksThreshold;

   public GeodeConfiguration(final GeodeBlockSettings geodeBlockSettings, final GeodeLayerSettings geodeLayerSettings, final GeodeCrackSettings geodeCrackSettings, final double usePotentialPlacementsChance, final double useAlternateLayer0Chance, final boolean placementsRequireLayer0Alternate, final IntProvider outerWallDistance, final IntProvider distributionPoints, final IntProvider pointOffset, final int minGenOffset, final int maxGenOffset, final double noiseMultiplier, final int invalidBlocksThreshold) {
      super();
      this.geodeBlockSettings = geodeBlockSettings;
      this.geodeLayerSettings = geodeLayerSettings;
      this.geodeCrackSettings = geodeCrackSettings;
      this.usePotentialPlacementsChance = usePotentialPlacementsChance;
      this.useAlternateLayer0Chance = useAlternateLayer0Chance;
      this.placementsRequireLayer0Alternate = placementsRequireLayer0Alternate;
      this.outerWallDistance = outerWallDistance;
      this.distributionPoints = distributionPoints;
      this.pointOffset = pointOffset;
      this.minGenOffset = minGenOffset;
      this.maxGenOffset = maxGenOffset;
      this.noiseMultiplier = noiseMultiplier;
      this.invalidBlocksThreshold = invalidBlocksThreshold;
   }
}
