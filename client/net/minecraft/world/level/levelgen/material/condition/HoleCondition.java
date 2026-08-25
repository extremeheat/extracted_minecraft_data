package net.minecraft.world.level.levelgen.material.condition;

import com.mojang.serialization.MapCodec;
import java.util.Objects;
import net.minecraft.world.level.levelgen.material.MaterialRuleContext;

public enum HoleCondition implements MaterialCondition {
   INSTANCE;

   public static final MapCodec<HoleCondition> CODEC = MapCodec.unit(INSTANCE);

   private HoleCondition() {
   }

   public MapCodec<HoleCondition> codec() {
      return CODEC;
   }

   public ConditionEvaluator compile(final MaterialRuleContext context) {
      return new MaterialRuleContext.LazyXZCondition(context) {
         {
            Objects.requireNonNull(HoleCondition.this);
         }

         protected boolean compute() {
            return this.context.surfaceDepth() <= 0;
         }
      };
   }

   // $FF: synthetic method
   private static HoleCondition[] $values() {
      return new HoleCondition[]{INSTANCE};
   }
}
