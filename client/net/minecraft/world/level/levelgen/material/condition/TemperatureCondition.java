package net.minecraft.world.level.levelgen.material.condition;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.material.MaterialRuleContext;

public enum TemperatureCondition implements MaterialCondition {
   INSTANCE;

   public static final MapCodec<TemperatureCondition> CODEC = MapCodec.unit(INSTANCE);

   private TemperatureCondition() {
   }

   public MapCodec<TemperatureCondition> codec() {
      return CODEC;
   }

   public ConditionEvaluator compile(final MaterialRuleContext context) {
      return () -> ((Biome)context.getBiome().value()).coldEnoughToSnow(context.blockPos(), context.getSeaLevel());
   }

   // $FF: synthetic method
   private static TemperatureCondition[] $values() {
      return new TemperatureCondition[]{INSTANCE};
   }
}
