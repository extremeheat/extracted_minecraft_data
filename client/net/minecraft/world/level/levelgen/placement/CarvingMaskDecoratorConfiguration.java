package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.configurations.DecoratorConfiguration;

public class CarvingMaskDecoratorConfiguration implements DecoratorConfiguration {
   public static final Codec<CarvingMaskDecoratorConfiguration> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(GenerationStep.Carving.CODEC.fieldOf("step").forGetter((var0x) -> {
         return var0x.step;
      }), Codec.FLOAT.fieldOf("probability").forGetter((var0x) -> {
         return var0x.probability;
      })).apply(var0, CarvingMaskDecoratorConfiguration::new);
   });
   protected final GenerationStep.Carving step;
   protected final float probability;

   public CarvingMaskDecoratorConfiguration(GenerationStep.Carving var1, float var2) {
      super();
      this.step = var1;
      this.probability = var2;
   }
}
