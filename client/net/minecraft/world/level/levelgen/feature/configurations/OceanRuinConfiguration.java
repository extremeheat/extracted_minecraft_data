package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.levelgen.structure.OceanRuinFeature;

public class OceanRuinConfiguration implements FeatureConfiguration {
   public static final Codec<OceanRuinConfiguration> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(OceanRuinFeature.Type.CODEC.fieldOf("biome_temp").forGetter((var0x) -> {
         return var0x.biomeTemp;
      }), Codec.floatRange(0.0F, 1.0F).fieldOf("large_probability").forGetter((var0x) -> {
         return var0x.largeProbability;
      }), Codec.floatRange(0.0F, 1.0F).fieldOf("cluster_probability").forGetter((var0x) -> {
         return var0x.clusterProbability;
      })).apply(var0, OceanRuinConfiguration::new);
   });
   public final OceanRuinFeature.Type biomeTemp;
   public final float largeProbability;
   public final float clusterProbability;

   public OceanRuinConfiguration(OceanRuinFeature.Type var1, float var2, float var3) {
      super();
      this.biomeTemp = var1;
      this.largeProbability = var2;
      this.clusterProbability = var3;
   }
}
