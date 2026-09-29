package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

public record BlockStateMatchTest(BlockState blockState) implements RuleTest {
   public static final MapCodec<BlockStateMatchTest> CODEC;

   public BlockStateMatchTest {
      super();
   }

   public boolean test(final BlockState blockState, final BlockPos pos, final RandomSource random) {
      return blockState == this.blockState;
   }

   public MapCodec<BlockStateMatchTest> codec() {
      return CODEC;
   }

   static {
      CODEC = BlockState.CODEC.fieldOf("block_state").xmap(BlockStateMatchTest::new, (t) -> t.blockState);
   }
}
