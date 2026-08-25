package net.minecraft.world.level.levelgen.material.rule;

import com.mojang.serialization.MapCodec;
import java.util.Objects;
import net.minecraft.world.level.levelgen.material.MaterialRuleContext;

public enum BandlandsRule implements MaterialRule {
   INSTANCE;

   public static final MapCodec<BandlandsRule> CODEC = MapCodec.unit(INSTANCE);

   private BandlandsRule() {
   }

   public MapCodec<BandlandsRule> codec() {
      return CODEC;
   }

   public RuleEvaluator compile(final MaterialRuleContext context) {
      Objects.requireNonNull(context);
      return context::getBand;
   }

   // $FF: synthetic method
   private static BandlandsRule[] $values() {
      return new BandlandsRule[]{INSTANCE};
   }
}
