package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;

public class PosAlwaysTrueTest extends PosRuleTest {
   public static final MapCodec<PosAlwaysTrueTest> CODEC = MapCodec.unit(() -> PosAlwaysTrueTest.INSTANCE);
   public static final PosAlwaysTrueTest INSTANCE = new PosAlwaysTrueTest();

   private PosAlwaysTrueTest() {
      super();
   }

   @Override
   public boolean test(BlockPos var1, BlockPos var2, BlockPos var3, RandomSource var4) {
      return true;
   }

   @Override
   protected PosRuleTestType<?> getType() {
      return PosRuleTestType.ALWAYS_TRUE_TEST;
   }
}
