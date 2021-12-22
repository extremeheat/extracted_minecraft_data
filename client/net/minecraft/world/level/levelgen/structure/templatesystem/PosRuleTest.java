package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;

public abstract class PosRuleTest {
   public static final Codec<PosRuleTest> CODEC;

   public PosRuleTest() {
      super();
   }

   public abstract boolean test(BlockPos var1, BlockPos var2, BlockPos var3, Random var4);

   protected abstract PosRuleTestType<?> getType();

   static {
      CODEC = Registry.POS_RULE_TEST.byNameCodec().dispatch("predicate_type", PosRuleTest::getType, PosRuleTestType::codec);
   }
}
