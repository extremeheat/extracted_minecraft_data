package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.IntProviders;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SeaPickleBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;

public record SeaPickleFeature(IntProvider count) implements Feature {
   public static final MapCodec<SeaPickleFeature> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(IntProviders.codec(0, 256).fieldOf("count").forGetter(SeaPickleFeature::count)).apply(i, SeaPickleFeature::new));

   public SeaPickleFeature(final int count) {
      this(ConstantInt.of(count));
   }

   public SeaPickleFeature {
      super();
   }

   public MapCodec<SeaPickleFeature> codec() {
      return CODEC;
   }

   public boolean place(final WorldGenLevel level, final ChunkGenerator chunkGenerator, final RandomSource random, final BlockPos origin) {
      int placed = 0;
      int count = this.count().sample(random);

      for(int i = 0; i < count; ++i) {
         int x = random.nextInt(8) - random.nextInt(8);
         int z = random.nextInt(8) - random.nextInt(8);
         int y = level.getHeight(Heightmap.Types.OCEAN_FLOOR, origin.getX() + x, origin.getZ() + z);
         BlockPos picklePos = new BlockPos(origin.getX() + x, y, origin.getZ() + z);
         BlockState pickleState = (BlockState)Blocks.SEA_PICKLE.defaultBlockState().setValue(SeaPickleBlock.PICKLES, random.nextInt(4) + 1);
         if (level.getBlockState(picklePos).is(Blocks.WATER) && pickleState.canSurvive(level, picklePos)) {
            level.setBlock(picklePos, pickleState, 2);
            ++placed;
         }
      }

      return placed > 0;
   }
}
