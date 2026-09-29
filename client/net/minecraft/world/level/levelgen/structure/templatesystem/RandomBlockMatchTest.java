package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public record RandomBlockMatchTest(Block block, float probability) implements RuleTest {
   public static final MapCodec<RandomBlockMatchTest> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(BuiltInRegistries.BLOCK.byNameCodec().fieldOf("block").forGetter((t) -> t.block), Codec.FLOAT.fieldOf("probability").forGetter((t) -> t.probability)).apply(i, RandomBlockMatchTest::new));

   public RandomBlockMatchTest {
      super();
   }

   public boolean test(final BlockState blockState, final BlockPos pos, final RandomSource random) {
      return blockState.is(this.block) && random.nextFloat() < this.probability;
   }

   public MapCodec<RandomBlockMatchTest> codec() {
      return CODEC;
   }
}
