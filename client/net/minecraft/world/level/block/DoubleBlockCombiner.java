package net.minecraft.world.level.block;

import java.util.function.BiPredicate;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public class DoubleBlockCombiner {
   public DoubleBlockCombiner() {
      super();
   }

   public static <S extends BlockEntity> NeighborCombineResult<S> combineWithNeigbour(final BlockEntityType<S> entityType, final Function<BlockState, BlockType> typeResolver, final Function<BlockState, Direction> connectionResolver, final Property<Direction> facingProperty, final BlockState state, final LevelAccessor level, final BlockPos pos, final BiPredicate<LevelAccessor, BlockPos> blockedChecker) {
      S blockEntity = entityType.getBlockEntity(level, pos);
      if (blockEntity == null) {
         return Combiner::acceptNone;
      } else if (blockedChecker.test(level, pos)) {
         return Combiner::acceptNone;
      } else {
         BlockType type = (BlockType)typeResolver.apply(state);
         boolean single = type == DoubleBlockCombiner.BlockType.SINGLE;
         boolean isFirst = type == DoubleBlockCombiner.BlockType.FIRST;
         if (single) {
            return new NeighborCombineResult.Single<S>(blockEntity);
         } else {
            BlockPos neighborPos = pos.relative((Direction)connectionResolver.apply(state));
            BlockState neighbourState = level.getBlockState(neighborPos);
            if (neighbourState.is(state.getBlock())) {
               BlockType neighbourType = (BlockType)typeResolver.apply(neighbourState);
               if (neighbourType != DoubleBlockCombiner.BlockType.SINGLE && type != neighbourType && neighbourState.getValue(facingProperty) == state.getValue(facingProperty)) {
                  if (blockedChecker.test(level, neighborPos)) {
                     return Combiner::acceptNone;
                  }

                  S neighbour = entityType.getBlockEntity(level, neighborPos);
                  if (neighbour != null) {
                     S first = isFirst ? blockEntity : neighbour;
                     S second = isFirst ? neighbour : blockEntity;
                     return new NeighborCombineResult.Double<S>(first, second);
                  }
               }
            }

            return new NeighborCombineResult.Single<S>(blockEntity);
         }
      }
   }

   public static enum BlockType {
      SINGLE,
      FIRST,
      SECOND;

      private BlockType() {
      }

      // $FF: synthetic method
      private static BlockType[] $values() {
         return new BlockType[]{SINGLE, FIRST, SECOND};
      }
   }

   public interface Combiner<S, T> {
      T acceptDouble(S first, S second);

      T acceptSingle(S single);

      T acceptNone();
   }

   public interface NeighborCombineResult<S> {
      <T> T apply(Combiner<? super S, T> callback);

      public static final class Double<S> implements NeighborCombineResult<S> {
         private final S first;
         private final S second;

         public Double(final S first, final S second) {
            super();
            this.first = first;
            this.second = second;
         }

         public <T> T apply(final Combiner<? super S, T> callback) {
            return callback.acceptDouble(this.first, this.second);
         }
      }

      public static final class Single<S> implements NeighborCombineResult<S> {
         private final S single;

         public Single(final S single) {
            super();
            this.single = single;
         }

         public <T> T apply(final Combiner<? super S, T> callback) {
            return callback.acceptSingle(this.single);
         }
      }
   }
}
