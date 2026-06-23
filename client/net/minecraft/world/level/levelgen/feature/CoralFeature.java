package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.MapCodec;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.BaseCoralWallFanBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SeaPickleBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;

public interface CoralFeature extends Feature {
   MapCodec<? extends CoralFeature> codec();

   default boolean place(final WorldGenLevel level, final ChunkGenerator chunkGenerator, final RandomSource random, final BlockPos origin) {
      Optional<Block> coral = BuiltInRegistries.BLOCK.getRandomElementOf(BlockTags.CORAL_BLOCKS, random).map(Holder::value);
      return coral.isEmpty() ? false : this.placeFeature(level, random, origin, ((Block)coral.get()).defaultBlockState());
   }

   boolean placeFeature(final LevelAccessor level, final RandomSource random, final BlockPos origin, final BlockState state);

   default boolean placeCoralBlock(final LevelAccessor level, final RandomSource random, final BlockPos pos, final BlockState state) {
      BlockPos above = pos.above();
      BlockState targetBlockState = level.getBlockState(pos);
      if ((targetBlockState.is(Blocks.WATER) || targetBlockState.is(BlockTags.CORALS)) && level.getBlockState(above).is(Blocks.WATER)) {
         level.setBlockAndUpdate(pos, state);
         if (random.nextFloat() < 0.25F) {
            BuiltInRegistries.BLOCK.getRandomElementOf(BlockTags.CORALS, random).map(Holder::value).ifPresent((block) -> level.setBlock(above, block.defaultBlockState(), 2));
         } else if (random.nextFloat() < 0.05F) {
            level.setBlock(above, (BlockState)Blocks.SEA_PICKLE.defaultBlockState().setValue(SeaPickleBlock.PICKLES, random.nextInt(4) + 1), 2);
         }

         for(Direction direction : Direction.Plane.HORIZONTAL) {
            if (random.nextFloat() < 0.2F) {
               BlockPos relativePos = pos.relative(direction);
               if (level.getBlockState(relativePos).is(Blocks.WATER)) {
                  BuiltInRegistries.BLOCK.getRandomElementOf(BlockTags.WALL_CORALS, random).map(Holder::value).ifPresent((coral) -> {
                     BlockState coralFanState = coral.defaultBlockState();
                     if (coralFanState.hasProperty(BaseCoralWallFanBlock.FACING)) {
                        coralFanState = (BlockState)coralFanState.setValue(BaseCoralWallFanBlock.FACING, direction);
                     }

                     level.setBlock(relativePos, coralFanState, 2);
                  });
               }
            }
         }

         return true;
      } else {
         return false;
      }
   }
}
