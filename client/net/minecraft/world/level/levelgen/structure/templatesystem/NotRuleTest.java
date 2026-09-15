package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

public class NotRuleTest extends RuleTest {
   public static final MapCodec<NotRuleTest> CODEC;
   private final RuleTest rule;

   public NotRuleTest(final RuleTest rule) {
      super();
      this.rule = rule;
   }

   public boolean test(final BlockState blockState, final BlockPos pos, final RandomSource random) {
      return !this.rule.test(blockState, pos, random);
   }

   protected RuleTestType<?> getType() {
      return RuleTestType.NOT_TEST;
   }

   static {
      CODEC = RuleTest.CODEC.fieldOf("rule").xmap(NotRuleTest::new, (t) -> t.rule);
   }
}
