package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;

public interface RuleTest {
   Codec<RuleTest> CODEC = BuiltInRegistries.RULE_TEST_TYPE.byNameCodec().dispatch("predicate_type", RuleTest::codec, (c) -> c);

   default boolean testAgainstWorldState(final LevelReader level, final BlockPos pos, final RandomSource random) {
      return this.test(level.getBlockState(pos), pos, random);
   }

   boolean test(BlockState state, final BlockPos pos, RandomSource random);

   MapCodec<? extends RuleTest> codec();

   static RuleTest allOf(final List<RuleTest> predicates) {
      return new AllOfRuleTest(predicates);
   }

   static RuleTest allOf(final RuleTest... predicates) {
      return allOf(List.of(predicates));
   }

   static RuleTest anyOf(final List<RuleTest> predicates) {
      return new AnyOfRuleTest(predicates);
   }

   static RuleTest anyOf(final RuleTest... predicates) {
      return anyOf(List.of(predicates));
   }

   static RuleTest not(final RuleTest predicate) {
      return new NotRuleTest(predicate);
   }

   static RuleTest either(final RuleTest condition, final RuleTest ifTrue, final RuleTest ifFalse) {
      return anyOf(allOf(condition, ifTrue), allOf(not(condition), ifFalse));
   }
}
