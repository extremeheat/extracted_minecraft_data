package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.TallSeagrassBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;

public record SeagrassFeature(float probability) implements Feature {
   public static final MapCodec<SeagrassFeature> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(Codec.floatRange(0.0F, 1.0F).fieldOf("probability").forGetter(SeagrassFeature::probability)).apply(i, SeagrassFeature::new));

   public SeagrassFeature {
      super();
   }

   public MapCodec<SeagrassFeature> codec() {
      return CODEC;
   }

   public boolean place(final WorldGenLevel level, final ChunkGenerator chunkGenerator, final RandomSource random, final BlockPos origin) {
      boolean placedAny = false;
      int x = random.nextInt(8) - random.nextInt(8);
      int z = random.nextInt(8) - random.nextInt(8);
      int y = level.getHeight(Heightmap.Types.OCEAN_FLOOR, origin.getX() + x, origin.getZ() + z);
      BlockPos grassPos = new BlockPos(origin.getX() + x, y, origin.getZ() + z);
      if (level.getBlockState(grassPos).is(Blocks.WATER)) {
         boolean isTall = random.nextDouble() < (double)this.probability;
         BlockState state = isTall ? Blocks.TALL_SEAGRASS.defaultBlockState() : Blocks.SEAGRASS.defaultBlockState();
         if (state.canSurvive(level, grassPos)) {
            if (isTall) {
               BlockState upperState = (BlockState)state.setValue(TallSeagrassBlock.HALF, DoubleBlockHalf.UPPER);
               BlockPos above = grassPos.above();
               if (level.getBlockState(above).is(Blocks.WATER)) {
                  level.setBlock(grassPos, state, 2);
                  level.setBlock(above, upperState, 2);
               }
            } else {
               level.setBlock(grassPos, state, 2);
            }

            placedAny = true;
         }
      }

      return placedAny;
   }
}
