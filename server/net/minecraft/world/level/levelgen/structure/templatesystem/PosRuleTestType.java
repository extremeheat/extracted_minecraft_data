package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;

public interface PosRuleTestType<P extends PosRuleTest> {
   PosRuleTestType<PosAlwaysTrueTest> ALWAYS_TRUE_TEST = register("always_true", PosAlwaysTrueTest.CODEC);
   PosRuleTestType<LinearPosTest> LINEAR_POS_TEST = register("linear_pos", LinearPosTest.CODEC);
   PosRuleTestType<AxisAlignedLinearPosTest> AXIS_ALIGNED_LINEAR_POS_TEST = register("axis_aligned_linear_pos", AxisAlignedLinearPosTest.CODEC);

   Codec<P> codec();

   static <P extends PosRuleTest> PosRuleTestType<P> register(String var0, Codec<P> var1) {
      return (PosRuleTestType)Registry.register(Registry.POS_RULE_TEST, (String)var0, () -> {
         return var1;
      });
   }
}
