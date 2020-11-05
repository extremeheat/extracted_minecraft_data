package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.levelgen.GeodeBlockSettings;
import net.minecraft.world.level.levelgen.GeodeCrackSettings;
import net.minecraft.world.level.levelgen.GeodeLayerSettings;

public class GeodeConfiguration implements FeatureConfiguration {
   public static final Codec<Double> CHANCE_RANGE = Codec.doubleRange(0.0D, 1.0D);
   public static final Codec<GeodeConfiguration> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(GeodeBlockSettings.CODEC.fieldOf("blocks").forGetter((var0x) -> {
         return var0x.geodeBlockSettings;
      }), GeodeLayerSettings.CODEC.fieldOf("layers").forGetter((var0x) -> {
         return var0x.geodeLayerSettings;
      }), GeodeCrackSettings.CODEC.fieldOf("crack").forGetter((var0x) -> {
         return var0x.geodeCrackSettings;
      }), CHANCE_RANGE.fieldOf("use_potential_placements_chance").orElse(0.35D).forGetter((var0x) -> {
         return var0x.usePotentialPlacementsChance;
      }), CHANCE_RANGE.fieldOf("use_alternate_layer0_chance").orElse(0.0D).forGetter((var0x) -> {
         return var0x.useAlternateLayer0Chance;
      }), Codec.BOOL.fieldOf("placements_require_layer0_alternate").orElse(true).forGetter((var0x) -> {
         return var0x.placementsRequireLayer0Alternate;
      }), Codec.intRange(1, 10).fieldOf("min_outer_wall_distance").orElse(4).forGetter((var0x) -> {
         return var0x.minOuterWallDistance;
      }), Codec.intRange(1, 20).fieldOf("max_outer_wall_distance").orElse(6).forGetter((var0x) -> {
         return var0x.maxOuterWallDistance;
      }), Codec.intRange(1, 10).fieldOf("min_distribution_points").orElse(3).forGetter((var0x) -> {
         return var0x.minDistributionPoints;
      }), Codec.intRange(1, 20).fieldOf("max_distribution_points").orElse(5).forGetter((var0x) -> {
         return var0x.maxDistributionPoints;
      }), Codec.intRange(0, 10).fieldOf("min_point_offset").orElse(1).forGetter((var0x) -> {
         return var0x.minPointOffset;
      }), Codec.intRange(0, 10).fieldOf("max_point_offset").orElse(3).forGetter((var0x) -> {
         return var0x.maxPointOffset;
      }), Codec.INT.fieldOf("min_gen_offset").orElse(-16).forGetter((var0x) -> {
         return var0x.minGenOffset;
      }), Codec.INT.fieldOf("max_gen_offset").orElse(16).forGetter((var0x) -> {
         return var0x.maxGenOffset;
      }), CHANCE_RANGE.fieldOf("noise_multiplier").orElse(0.05D).forGetter((var0x) -> {
         return var0x.noiseMultiplier;
      })).apply(var0, GeodeConfiguration::new);
   });
   public final GeodeBlockSettings geodeBlockSettings;
   public final GeodeLayerSettings geodeLayerSettings;
   public final GeodeCrackSettings geodeCrackSettings;
   public final double usePotentialPlacementsChance;
   public final double useAlternateLayer0Chance;
   public final boolean placementsRequireLayer0Alternate;
   public final int minOuterWallDistance;
   public final int maxOuterWallDistance;
   public final int minDistributionPoints;
   public final int maxDistributionPoints;
   public final int minPointOffset;
   public final int maxPointOffset;
   public final int minGenOffset;
   public final int maxGenOffset;
   public final double noiseMultiplier;

   public GeodeConfiguration(GeodeBlockSettings var1, GeodeLayerSettings var2, GeodeCrackSettings var3, double var4, double var6, boolean var8, int var9, int var10, int var11, int var12, int var13, int var14, int var15, int var16, double var17) {
      super();
      this.geodeBlockSettings = var1;
      this.geodeLayerSettings = var2;
      this.geodeCrackSettings = var3;
      this.usePotentialPlacementsChance = var4;
      this.useAlternateLayer0Chance = var6;
      this.placementsRequireLayer0Alternate = var8;
      this.minOuterWallDistance = var9;
      this.maxOuterWallDistance = var10;
      this.minDistributionPoints = var11;
      this.maxDistributionPoints = var12;
      this.minPointOffset = var13;
      this.maxPointOffset = var14;
      this.minGenOffset = var15;
      this.maxGenOffset = var16;
      this.noiseMultiplier = var17;
   }
}
