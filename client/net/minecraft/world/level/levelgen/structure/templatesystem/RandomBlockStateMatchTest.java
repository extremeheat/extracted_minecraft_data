package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

public record RandomBlockStateMatchTest(BlockState blockState, float probability) implements RuleTest {
   public static final MapCodec<RandomBlockStateMatchTest> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(BlockState.CODEC.fieldOf("block_state").forGetter((t) -> t.blockState), Codec.FLOAT.fieldOf("probability").forGetter((t) -> t.probability)).apply(i, RandomBlockStateMatchTest::new));

   public RandomBlockStateMatchTest {
      super();
   }

   public boolean test(final BlockState blockState, final BlockPos pos, final RandomSource random) {
      return blockState == this.blockState && random.nextFloat() < this.probability;
   }

   public MapCodec<RandomBlockStateMatchTest> codec() {
      return CODEC;
   }
}
