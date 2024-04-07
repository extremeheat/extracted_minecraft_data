package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.serialization.MapCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

public class BlockStateMatchTest extends RuleTest {
   public static final MapCodec<BlockStateMatchTest> CODEC = BlockState.CODEC.fieldOf("block_state").xmap(BlockStateMatchTest::new, var0 -> var0.blockState);
   private final BlockState blockState;

   public BlockStateMatchTest(BlockState var1) {
      super();
      this.blockState = var1;
   }

   @Override
   public boolean test(BlockState var1, RandomSource var2) {
      return var1 == this.blockState;
   }

   @Override
   protected RuleTestType<?> getType() {
      return RuleTestType.BLOCKSTATE_TEST;
   }
}
