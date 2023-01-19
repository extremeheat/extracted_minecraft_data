package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class BlockMatchTest extends RuleTest {
   public static final Codec<BlockMatchTest> CODEC = Registry.BLOCK.byNameCodec().fieldOf("block").xmap(BlockMatchTest::new, var0 -> var0.block).codec();
   private final Block block;

   public BlockMatchTest(Block var1) {
      super();
      this.block = var1;
   }

   @Override
   public boolean test(BlockState var1, RandomSource var2) {
      return var1.is(this.block);
   }

   @Override
   protected RuleTestType<?> getType() {
      return RuleTestType.BLOCK_TEST;
   }
}
