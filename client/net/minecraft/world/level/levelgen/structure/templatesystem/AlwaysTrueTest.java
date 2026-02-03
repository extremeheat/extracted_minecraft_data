package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.serialization.MapCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

public class AlwaysTrueTest extends RuleTest {
   public static final MapCodec<AlwaysTrueTest> CODEC = MapCodec.unit(() -> INSTANCE);
   public static final AlwaysTrueTest INSTANCE = new AlwaysTrueTest();

   private AlwaysTrueTest() {
      super();
   }

   public boolean test(final BlockState blockState, final RandomSource random) {
      return true;
   }

   protected RuleTestType<?> getType() {
      return RuleTestType.ALWAYS_TRUE_TEST;
   }
}
