package net.minecraft.world.level.levelgen.placement;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.configurations.DecoratorConfiguration;

public class CarvingMaskDecoratorConfiguration implements DecoratorConfiguration {
   protected final GenerationStep.Carving step;
   protected final float probability;

   public CarvingMaskDecoratorConfiguration(GenerationStep.Carving var1, float var2) {
      this.step = var1;
      this.probability = var2;
   }

   public Dynamic serialize(DynamicOps var1) {
      return new Dynamic(var1, var1.createMap(ImmutableMap.of(var1.createString("step"), var1.createString(this.step.toString()), var1.createString("probability"), var1.createFloat(this.probability))));
   }

   public static CarvingMaskDecoratorConfiguration deserialize(Dynamic var0) {
      GenerationStep.Carving var1 = GenerationStep.Carving.valueOf(var0.get("step").asString(""));
      float var2 = var0.get("probability").asFloat(0.0F);
      return new CarvingMaskDecoratorConfiguration(var1, var2);
   }
}
