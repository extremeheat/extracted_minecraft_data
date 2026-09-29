package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.serialization.MapCodec;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

public record AllOfRuleTest(List<RuleTest> rules) implements RuleTest {
   public static final MapCodec<AllOfRuleTest> CODEC;

   public AllOfRuleTest {
      super();
   }

   public boolean test(final BlockState blockState, final BlockPos pos, final RandomSource random) {
      for(RuleTest rule : this.rules) {
         if (!rule.test(blockState, pos, random)) {
            return false;
         }
      }

      return true;
   }

   public MapCodec<AllOfRuleTest> codec() {
      return CODEC;
   }

   static {
      CODEC = RuleTest.CODEC.listOf().fieldOf("rules").xmap(AllOfRuleTest::new, (t) -> t.rules);
   }
}
