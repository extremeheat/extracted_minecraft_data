package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public record BlockMatchTest(Block block) implements RuleTest {
   public static final MapCodec<BlockMatchTest> CODEC;

   public BlockMatchTest {
      super();
   }

   public boolean test(final BlockState blockState, final BlockPos pos, final RandomSource random) {
      return blockState.is(this.block);
   }

   public MapCodec<BlockMatchTest> codec() {
      return CODEC;
   }

   static {
      CODEC = BuiltInRegistries.BLOCK.byNameCodec().fieldOf("block").xmap(BlockMatchTest::new, (t) -> t.block);
   }
}
