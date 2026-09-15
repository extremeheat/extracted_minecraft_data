package net.minecraft.world.level.levelgen.material.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.material.MaterialRuleContext;

public record YCondition(VerticalAnchor anchor, int surfaceDepthMultiplier, boolean addStoneDepth) implements MaterialCondition {
   public static final MapCodec<YCondition> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(VerticalAnchor.CODEC.fieldOf("anchor").forGetter(YCondition::anchor), Codec.intRange(-20, 20).fieldOf("surface_depth_multiplier").forGetter(YCondition::surfaceDepthMultiplier), Codec.BOOL.fieldOf("add_stone_depth").forGetter(YCondition::addStoneDepth)).apply(i, YCondition::new));

   public YCondition {
      super();
   }

   public MapCodec<YCondition> codec() {
      return CODEC;
   }

   public ConditionEvaluator compile(final MaterialRuleContext ruleContext) {
      return new MaterialRuleContext.LazyYCondition(ruleContext) {
         {
            Objects.requireNonNull(YCondition.this);
         }

         protected boolean compute() {
            return this.context.blockY() + (YCondition.this.addStoneDepth ? this.context.stoneDepthAbove() : 0) >= this.context.resolveAnchorY(YCondition.this.anchor) + this.context.surfaceDepth() * YCondition.this.surfaceDepthMultiplier;
         }
      };
   }
}
