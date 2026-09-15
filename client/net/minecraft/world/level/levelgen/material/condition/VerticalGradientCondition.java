package net.minecraft.world.level.levelgen.material.condition;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.material.MaterialRuleContext;

public record VerticalGradientCondition(Identifier randomName, VerticalAnchor trueAtAndBelow, VerticalAnchor falseAtAndAbove) implements MaterialCondition {
   public static final MapCodec<VerticalGradientCondition> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(Identifier.CODEC.fieldOf("random_name").forGetter(VerticalGradientCondition::randomName), VerticalAnchor.CODEC.fieldOf("true_at_and_below").forGetter(VerticalGradientCondition::trueAtAndBelow), VerticalAnchor.CODEC.fieldOf("false_at_and_above").forGetter(VerticalGradientCondition::falseAtAndAbove)).apply(i, VerticalGradientCondition::new));

   public VerticalGradientCondition {
      super();
   }

   public MapCodec<VerticalGradientCondition> codec() {
      return CODEC;
   }

   public ConditionEvaluator compile(final MaterialRuleContext ruleContext) {
      final int trueAtAndBelow = ruleContext.resolveAnchorY(this.trueAtAndBelow);
      final int falseAtAndAbove = ruleContext.resolveAnchorY(this.falseAtAndAbove);
      final PositionalRandomFactory randomFactory = ruleContext.getOrCreateRandomFactory(this.randomName);
      return new MaterialRuleContext.LazyYCondition(ruleContext) {
         {
            Objects.requireNonNull(VerticalGradientCondition.this);
         }

         protected boolean compute() {
            int blockY = this.context.blockY();
            if (blockY <= trueAtAndBelow) {
               return true;
            } else if (blockY >= falseAtAndAbove) {
               return false;
            } else {
               double probability = Mth.map((double)blockY, (double)trueAtAndBelow, (double)falseAtAndAbove, 1.0, 0.0);
               RandomSource random = randomFactory.at(this.context.blockX(), blockY, this.context.blockZ());
               return (double)random.nextFloat() < probability;
            }
         }
      };
   }
}
