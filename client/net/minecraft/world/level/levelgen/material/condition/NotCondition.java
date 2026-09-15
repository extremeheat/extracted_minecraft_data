package net.minecraft.world.level.levelgen.material.condition;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.levelgen.material.MaterialRuleContext;

public record NotCondition(MaterialCondition target) implements MaterialCondition {
   public static final MapCodec<NotCondition> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(MaterialCondition.CODEC.fieldOf("invert").forGetter(NotCondition::target)).apply(i, NotCondition::new));

   public NotCondition {
      super();
   }

   public MapCodec<NotCondition> codec() {
      return CODEC;
   }

   public ConditionEvaluator compile(final MaterialRuleContext context) {
      ConditionEvaluator target = this.target.compile(context);
      return () -> !target.test();
   }
}
