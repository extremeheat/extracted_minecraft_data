package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.Registry;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class BlockMatchTest extends RuleTest {
   public static final Codec<BlockMatchTest> CODEC;
   private final Block block;

   public BlockMatchTest(Block var1) {
      super();
      this.block = var1;
   }

   public boolean test(BlockState var1, Random var2) {
      return var1.is(this.block);
   }

   protected RuleTestType<?> getType() {
      return RuleTestType.BLOCK_TEST;
   }

   static {
      CODEC = Registry.BLOCK.byNameCodec().fieldOf("block").xmap(BlockMatchTest::new, (var0) -> {
         return var0.block;
      }).codec();
   }
}
