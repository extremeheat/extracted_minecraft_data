package net.minecraft.world.level.levelgen.placement;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.world.level.levelgen.feature.configurations.DecoratorConfiguration;

public class FrequencyWithExtraChanceDecoratorConfiguration implements DecoratorConfiguration {
   public final int count;
   public final float extraChance;
   public final int extraCount;

   public FrequencyWithExtraChanceDecoratorConfiguration(int var1, float var2, int var3) {
      this.count = var1;
      this.extraChance = var2;
      this.extraCount = var3;
   }

   public Dynamic serialize(DynamicOps var1) {
      return new Dynamic(var1, var1.createMap(ImmutableMap.of(var1.createString("count"), var1.createInt(this.count), var1.createString("extra_chance"), var1.createFloat(this.extraChance), var1.createString("extra_count"), var1.createInt(this.extraCount))));
   }

   public static FrequencyWithExtraChanceDecoratorConfiguration deserialize(Dynamic var0) {
      int var1 = var0.get("count").asInt(0);
      float var2 = var0.get("extra_chance").asFloat(0.0F);
      int var3 = var0.get("extra_count").asInt(0);
      return new FrequencyWithExtraChanceDecoratorConfiguration(var1, var2, var3);
   }
}
