package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.serialization.Codec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

public abstract class RuleTest {
   public static final Codec<RuleTest> CODEC;

   public RuleTest() {
      super();
   }

   public abstract boolean test(BlockState state, RandomSource random);

   protected abstract RuleTestType<?> getType();

   static {
      CODEC = BuiltInRegistries.RULE_TEST.byNameCodec().dispatch("predicate_type", RuleTest::getType, RuleTestType::codec);
   }
}
