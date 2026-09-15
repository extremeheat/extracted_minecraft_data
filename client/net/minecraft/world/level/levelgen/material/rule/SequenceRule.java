package net.minecraft.world.level.levelgen.material.rule;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.material.MaterialRuleContext;

public record SequenceRule(List<MaterialRule> sequence) implements MaterialRule {
   public static final MapCodec<SequenceRule> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(MaterialRule.CODEC.listOf().fieldOf("sequence").forGetter(SequenceRule::sequence)).apply(i, SequenceRule::new));

   public SequenceRule {
      super();
   }

   public MapCodec<SequenceRule> codec() {
      return CODEC;
   }

   public RuleEvaluator compile(final MaterialRuleContext context) {
      if (this.sequence.size() == 1) {
         return ((MaterialRule)this.sequence.getFirst()).compile(context);
      } else {
         RuleEvaluator[] sequence = new RuleEvaluator[this.sequence.size()];

         for(int i = 0; i < sequence.length; ++i) {
            sequence[i] = ((MaterialRule)this.sequence.get(i)).compile(context);
         }

         return (blockX, blockY, blockZ) -> {
            for(RuleEvaluator rule : sequence) {
               BlockState state = rule.tryApply(blockX, blockY, blockZ);
               if (state != null) {
                  return state;
               }
            }

            return null;
         };
      }
   }
}
