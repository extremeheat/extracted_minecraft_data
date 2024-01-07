package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.levelgen.GeodeBlockSettings;
import net.minecraft.world.level.levelgen.GeodeCrackSettings;
import net.minecraft.world.level.levelgen.GeodeLayerSettings;

public class GeodeConfiguration implements FeatureConfiguration {
   public static final Codec<Double> CHANCE_RANGE = Codec.doubleRange(0.0, 1.0);
   public static final Codec<GeodeConfiguration> CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(
               GeodeBlockSettings.CODEC.fieldOf("blocks").forGetter(var0x -> var0x.geodeBlockSettings),
               GeodeLayerSettings.CODEC.fieldOf("layers").forGetter(var0x -> var0x.geodeLayerSettings),
               GeodeCrackSettings.CODEC.fieldOf("crack").forGetter(var0x -> var0x.geodeCrackSettings),
               CHANCE_RANGE.fieldOf("use_potential_placements_chance").orElse(0.35).forGetter(var0x -> var0x.usePotentialPlacementsChance),
               CHANCE_RANGE.fieldOf("use_alternate_layer0_chance").orElse(0.0).forGetter(var0x -> var0x.useAlternateLayer0Chance),
               Codec.BOOL.fieldOf("placements_require_layer0_alternate").orElse(true).forGetter(var0x -> var0x.placementsRequireLayer0Alternate),
               IntProvider.codec(1, 20).fieldOf("outer_wall_distance").orElse(UniformInt.of(4, 5)).forGetter(var0x -> var0x.outerWallDistance),
               IntProvider.codec(1, 20).fieldOf("distribution_points").orElse(UniformInt.of(3, 4)).forGetter(var0x -> var0x.distributionPoints),
               IntProvider.codec(0, 10).fieldOf("point_offset").orElse(UniformInt.of(1, 2)).forGetter(var0x -> var0x.pointOffset),
               Codec.INT.fieldOf("min_gen_offset").orElse(-16).forGetter(var0x -> var0x.minGenOffset),
               Codec.INT.fieldOf("max_gen_offset").orElse(16).forGetter(var0x -> var0x.maxGenOffset),
               CHANCE_RANGE.fieldOf("noise_multiplier").orElse(0.05).forGetter(var0x -> var0x.noiseMultiplier),
               Codec.INT.fieldOf("invalid_blocks_threshold").forGetter(var0x -> var0x.invalidBlocksThreshold)
            )
            .apply(var0, GeodeConfiguration::new)
   );
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

   public GeodeConfiguration(
      GeodeBlockSettings var1,
      GeodeLayerSettings var2,
      GeodeCrackSettings var3,
      double var4,
      double var6,
      boolean var8,
      IntProvider var9,
      IntProvider var10,
      IntProvider var11,
      int var12,
      int var13,
      double var14,
      int var16
   ) {
      super();
      this.geodeBlockSettings = var1;
      this.geodeLayerSettings = var2;
      this.geodeCrackSettings = var3;
      this.usePotentialPlacementsChance = var4;
      this.useAlternateLayer0Chance = var6;
      this.placementsRequireLayer0Alternate = var8;
      this.outerWallDistance = var9;
      this.distributionPoints = var10;
      this.pointOffset = var11;
      this.minGenOffset = var12;
      this.maxGenOffset = var13;
      this.noiseMultiplier = var14;
      this.invalidBlocksThreshold = var16;
   }
}
