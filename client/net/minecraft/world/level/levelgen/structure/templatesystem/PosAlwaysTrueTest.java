package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPos;

public class PosAlwaysTrueTest extends PosRuleTest {
   public static final Codec<PosAlwaysTrueTest> CODEC = Codec.unit(() -> {
      return INSTANCE;
   });
   public static final PosAlwaysTrueTest INSTANCE = new PosAlwaysTrueTest();

   private PosAlwaysTrueTest() {
      super();
   }

   public boolean test(BlockPos var1, BlockPos var2, BlockPos var3, Random var4) {
      return true;
   }

   protected PosRuleTestType<?> getType() {
      return PosRuleTestType.ALWAYS_TRUE_TEST;
   }
}
