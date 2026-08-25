package net.minecraft.world.level.levelgen.material.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.material.MaterialRuleContext;
import net.minecraft.world.level.levelgen.placement.CaveSurface;

public record StoneDepthCondition(int offset, boolean addSurfaceDepth, int secondaryDepthRange, CaveSurface surfaceType) implements MaterialCondition {
   public static final MapCodec<StoneDepthCondition> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(Codec.INT.fieldOf("offset").forGetter(StoneDepthCondition::offset), Codec.BOOL.fieldOf("add_surface_depth").forGetter(StoneDepthCondition::addSurfaceDepth), Codec.INT.fieldOf("secondary_depth_range").forGetter(StoneDepthCondition::secondaryDepthRange), CaveSurface.CODEC.fieldOf("surface_type").forGetter(StoneDepthCondition::surfaceType)).apply(i, StoneDepthCondition::new));

   public StoneDepthCondition {
      super();
   }

   public MapCodec<StoneDepthCondition> codec() {
      return CODEC;
   }

   public ConditionEvaluator compile(final MaterialRuleContext ruleContext) {
      final boolean ceiling = this.surfaceType == CaveSurface.CEILING;
      return new MaterialRuleContext.LazyYCondition(ruleContext) {
         {
            Objects.requireNonNull(StoneDepthCondition.this);
         }

         protected boolean compute() {
            int stoneDepth = ceiling ? this.context.stoneDepthBelow() : this.context.stoneDepthAbove();
            int surfaceDepth = StoneDepthCondition.this.addSurfaceDepth ? this.context.surfaceDepth() : 0;
            int secondarySurfaceDepth = StoneDepthCondition.this.secondaryDepthRange == 0 ? 0 : (int)Mth.map(this.context.getSurfaceSecondary(), -1.0, 1.0, 0.0, (double)StoneDepthCondition.this.secondaryDepthRange);
            return stoneDepth <= 1 + StoneDepthCondition.this.offset + surfaceDepth + secondarySurfaceDepth;
         }
      };
   }
}
