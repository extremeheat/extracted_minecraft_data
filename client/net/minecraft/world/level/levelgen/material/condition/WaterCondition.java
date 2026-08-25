package net.minecraft.world.level.levelgen.material.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import net.minecraft.world.level.levelgen.material.MaterialRuleContext;

public record WaterCondition(int offset, int surfaceDepthMultiplier, boolean addStoneDepth) implements MaterialCondition {
   public static final MapCodec<WaterCondition> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(Codec.INT.fieldOf("offset").forGetter(WaterCondition::offset), Codec.intRange(-20, 20).fieldOf("surface_depth_multiplier").forGetter(WaterCondition::surfaceDepthMultiplier), Codec.BOOL.fieldOf("add_stone_depth").forGetter(WaterCondition::addStoneDepth)).apply(i, WaterCondition::new));

   public WaterCondition {
      super();
   }

   public MapCodec<WaterCondition> codec() {
      return CODEC;
   }

   public ConditionEvaluator compile(final MaterialRuleContext ruleContext) {
      return new MaterialRuleContext.LazyYCondition(ruleContext) {
         {
            Objects.requireNonNull(WaterCondition.this);
         }

         protected boolean compute() {
            return this.context.waterHeight() == -2147483648 || this.context.blockY() + (WaterCondition.this.addStoneDepth ? this.context.stoneDepthAbove() : 0) >= this.context.waterHeight() + WaterCondition.this.offset + this.context.surfaceDepth() * WaterCondition.this.surfaceDepthMultiplier;
         }
      };
   }
}
