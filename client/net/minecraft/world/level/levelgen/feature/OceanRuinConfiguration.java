package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.world.level.levelgen.structure.OceanRuinFeature;

public class OceanRuinConfiguration implements FeatureConfiguration {
   public final OceanRuinFeature.Type biomeTemp;
   public final float largeProbability;
   public final float clusterProbability;

   public OceanRuinConfiguration(OceanRuinFeature.Type var1, float var2, float var3) {
      super();
      this.biomeTemp = var1;
      this.largeProbability = var2;
      this.clusterProbability = var3;
   }

   public <T> Dynamic<T> serialize(DynamicOps<T> var1) {
      return new Dynamic(var1, var1.createMap(ImmutableMap.of(var1.createString("biome_temp"), var1.createString(this.biomeTemp.getName()), var1.createString("large_probability"), var1.createFloat(this.largeProbability), var1.createString("cluster_probability"), var1.createFloat(this.clusterProbability))));
   }

   public static <T> OceanRuinConfiguration deserialize(Dynamic<T> var0) {
      OceanRuinFeature.Type var1 = OceanRuinFeature.Type.byName(var0.get("biome_temp").asString(""));
      float var2 = var0.get("large_probability").asFloat(0.0F);
      float var3 = var0.get("cluster_probability").asFloat(0.0F);
      return new OceanRuinConfiguration(var1, var2, var3);
   }
}
