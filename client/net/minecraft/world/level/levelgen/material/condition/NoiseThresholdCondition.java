package net.minecraft.world.level.levelgen.material.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.DoubleSupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.material.MaterialRuleContext;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

public record NoiseThresholdCondition(ResourceKey<NormalNoise> noise, double minThreshold, double maxThreshold, boolean is3d) implements MaterialCondition {
   public static final MapCodec<NoiseThresholdCondition> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(ResourceKey.codec(Registries.NOISE).fieldOf("noise").forGetter(NoiseThresholdCondition::noise), Codec.DOUBLE.fieldOf("min_threshold").forGetter(NoiseThresholdCondition::minThreshold), Codec.DOUBLE.fieldOf("max_threshold").forGetter(NoiseThresholdCondition::maxThreshold), Codec.BOOL.optionalFieldOf("is_3d", false).forGetter(NoiseThresholdCondition::is3d)).apply(i, NoiseThresholdCondition::new));

   public NoiseThresholdCondition {
      super();
   }

   public MapCodec<NoiseThresholdCondition> codec() {
      return CODEC;
   }

   public ConditionEvaluator compile(final MaterialRuleContext ruleContext) {
      DoubleSupplier noise = ruleContext.getNoiseSampler(this.noise, this.is3d);
      return () -> {
         double value = noise.getAsDouble();
         return value >= this.minThreshold && value <= this.maxThreshold;
      };
   }
}
