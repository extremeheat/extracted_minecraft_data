package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.serialization.MapCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

public class BlockStateMatchTest extends RuleTest {
   public static final MapCodec<BlockStateMatchTest> CODEC;
   private final BlockState blockState;

   public BlockStateMatchTest(BlockState var1) {
      super();
      this.blockState = var1;
   }

   public boolean test(BlockState var1, RandomSource var2) {
      return var1 == this.blockState;
   }

   protected RuleTestType<?> getType() {
      return RuleTestType.BLOCKSTATE_TEST;
   }

   static {
      CODEC = BlockState.CODEC.fieldOf("block_state").xmap(BlockStateMatchTest::new, (var0) -> {
         return var0.blockState;
      });
   }
}
