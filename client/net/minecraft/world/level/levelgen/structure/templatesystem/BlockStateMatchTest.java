package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.serialization.MapCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

public class BlockStateMatchTest extends RuleTest {
   public static final MapCodec<BlockStateMatchTest> CODEC;
   private final BlockState blockState;

   public BlockStateMatchTest(final BlockState blockState) {
      super();
      this.blockState = blockState;
   }

   public boolean test(final BlockState blockState, final RandomSource random) {
      return blockState == this.blockState;
   }

   protected RuleTestType<?> getType() {
      return RuleTestType.BLOCKSTATE_TEST;
   }

   static {
      CODEC = BlockState.CODEC.fieldOf("block_state").xmap(BlockStateMatchTest::new, (t) -> t.blockState);
   }
}
