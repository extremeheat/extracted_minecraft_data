package net.minecraft.world.level.levelgen.material.condition;

import com.mojang.serialization.MapCodec;
import java.util.Objects;
import net.minecraft.world.level.levelgen.material.MaterialRuleContext;

public enum SteepCondition implements MaterialCondition {
   INSTANCE;

   public static final MapCodec<SteepCondition> CODEC = MapCodec.unit(INSTANCE);

   private SteepCondition() {
   }

   public MapCodec<SteepCondition> codec() {
      return CODEC;
   }

   public ConditionEvaluator compile(final MaterialRuleContext context) {
      return new MaterialRuleContext.LazyXZCondition(context) {
         {
            Objects.requireNonNull(SteepCondition.this);
         }

         protected boolean compute() {
            return this.context.surfaceGradientX() <= -4 || this.context.surfaceGradientZ() >= 4;
         }
      };
   }

   // $FF: synthetic method
   private static SteepCondition[] $values() {
      return new SteepCondition[]{INSTANCE};
   }
}
