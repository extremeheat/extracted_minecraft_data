package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.DimensionType;

public record HeightMatchTest(int minInclusive, int maxInclusive) implements RuleTest {
   public static final MapCodec<HeightMatchTest> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(Codec.INT.fieldOf("min_inclusive").forGetter((c) -> c.minInclusive), Codec.INT.fieldOf("max_inclusive").forGetter((c) -> c.maxInclusive)).apply(i, HeightMatchTest::new));

   public HeightMatchTest {
      super();
   }

   public static RuleTest min(final int minInclusive) {
      return new HeightMatchTest(minInclusive, DimensionType.MAX_Y);
   }

   public static RuleTest max(final int maxInclusive) {
      return new HeightMatchTest(DimensionType.MIN_Y, maxInclusive);
   }

   public boolean test(final BlockState blockState, final BlockPos pos, final RandomSource random) {
      return this.minInclusive <= pos.getY() && pos.getY() <= this.maxInclusive;
   }

   public MapCodec<HeightMatchTest> codec() {
      return CODEC;
   }
}
