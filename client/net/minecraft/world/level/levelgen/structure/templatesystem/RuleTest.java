package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

public abstract class RuleTest {
   public static final Codec<RuleTest> CODEC;

   public RuleTest() {
      super();
   }

   public abstract boolean test(BlockState var1, RandomSource var2);

   protected abstract RuleTestType<?> getType();

   static {
      CODEC = Registry.RULE_TEST.byNameCodec().dispatch("predicate_type", RuleTest::getType, RuleTestType::codec);
   }
}
