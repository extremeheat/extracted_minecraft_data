package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.serialization.MapCodec;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

public class AllOfRuleTest extends RuleTest {
   public static final MapCodec<AllOfRuleTest> CODEC;
   private final List<RuleTest> rules;

   public AllOfRuleTest(final List<RuleTest> rules) {
      super();
      this.rules = rules;
   }

   public boolean test(final BlockState blockState, final BlockPos pos, final RandomSource random) {
      for(RuleTest rule : this.rules) {
         if (!rule.test(blockState, pos, random)) {
            return false;
         }
      }

      return true;
   }

   protected RuleTestType<?> getType() {
      return RuleTestType.ALL_OF_TEST;
   }

   static {
      CODEC = RuleTest.CODEC.listOf().fieldOf("rules").xmap(AllOfRuleTest::new, (t) -> t.rules);
   }
}
