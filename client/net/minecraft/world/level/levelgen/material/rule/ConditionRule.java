package net.minecraft.world.level.levelgen.material.rule;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.levelgen.material.MaterialRuleContext;
import net.minecraft.world.level.levelgen.material.condition.ConditionEvaluator;
import net.minecraft.world.level.levelgen.material.condition.MaterialCondition;

public record ConditionRule(MaterialCondition ifTrue, MaterialRule thenRun) implements MaterialRule {
   public static final MapCodec<ConditionRule> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(MaterialCondition.CODEC.fieldOf("if_true").forGetter(ConditionRule::ifTrue), MaterialRule.CODEC.fieldOf("then_run").forGetter(ConditionRule::thenRun)).apply(i, ConditionRule::new));

   public ConditionRule {
      super();
   }

   public MapCodec<ConditionRule> codec() {
      return CODEC;
   }

   public RuleEvaluator compile(final MaterialRuleContext context) {
      ConditionEvaluator ifTrue = this.ifTrue.compile(context);
      RuleEvaluator thenRun = this.thenRun.compile(context);
      return (blockX, blockY, blockZ) -> !ifTrue.test() ? null : thenRun.tryApply(blockX, blockY, blockZ);
   }
}
