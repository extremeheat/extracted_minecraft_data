package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;

public class PosAlwaysTrueTest extends PosRuleTest {
   public static final MapCodec<PosAlwaysTrueTest> CODEC = MapCodec.unit(() -> INSTANCE);
   public static final PosAlwaysTrueTest INSTANCE = new PosAlwaysTrueTest();

   private PosAlwaysTrueTest() {
      super();
   }

   public boolean test(final BlockPos inTemplatePos, final BlockPos worldPos, final BlockPos worldReference, final RandomSource random) {
      return true;
   }

   protected PosRuleTestType<?> getType() {
      return PosRuleTestType.ALWAYS_TRUE_TEST;
   }
}
