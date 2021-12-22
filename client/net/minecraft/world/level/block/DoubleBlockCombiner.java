package net.minecraft.world.level.block;

import java.util.function.BiPredicate;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DirectionProperty;

public class DoubleBlockCombiner {
   public DoubleBlockCombiner() {
      super();
   }

   public static <S extends BlockEntity> DoubleBlockCombiner.NeighborCombineResult<S> combineWithNeigbour(BlockEntityType<S> var0, Function<BlockState, DoubleBlockCombiner.BlockType> var1, Function<BlockState, Direction> var2, DirectionProperty var3, BlockState var4, LevelAccessor var5, BlockPos var6, BiPredicate<LevelAccessor, BlockPos> var7) {
      BlockEntity var8 = var0.getBlockEntity(var5, var6);
      if (var8 == null) {
         return DoubleBlockCombiner.Combiner::acceptNone;
      } else if (var7.test(var5, var6)) {
         return DoubleBlockCombiner.Combiner::acceptNone;
      } else {
         DoubleBlockCombiner.BlockType var9 = (DoubleBlockCombiner.BlockType)var1.apply(var4);
         boolean var10 = var9 == DoubleBlockCombiner.BlockType.SINGLE;
         boolean var11 = var9 == DoubleBlockCombiner.BlockType.FIRST;
         if (var10) {
            return new DoubleBlockCombiner.NeighborCombineResult.Single(var8);
         } else {
            BlockPos var12 = var6.relative((Direction)var2.apply(var4));
            BlockState var13 = var5.getBlockState(var12);
            if (var13.is(var4.getBlock())) {
               DoubleBlockCombiner.BlockType var14 = (DoubleBlockCombiner.BlockType)var1.apply(var13);
               if (var14 != DoubleBlockCombiner.BlockType.SINGLE && var9 != var14 && var13.getValue(var3) == var4.getValue(var3)) {
                  if (var7.test(var5, var12)) {
                     return DoubleBlockCombiner.Combiner::acceptNone;
                  }

                  BlockEntity var15 = var0.getBlockEntity(var5, var12);
                  if (var15 != null) {
                     BlockEntity var16 = var11 ? var8 : var15;
                     BlockEntity var17 = var11 ? var15 : var8;
                     return new DoubleBlockCombiner.NeighborCombineResult.Double(var16, var17);
                  }
               }
            }

            return new DoubleBlockCombiner.NeighborCombineResult.Single(var8);
         }
      }
   }

   public interface NeighborCombineResult<S> {
      <T> T apply(DoubleBlockCombiner.Combiner<? super S, T> var1);

      public static final class Single<S> implements DoubleBlockCombiner.NeighborCombineResult<S> {
         private final S single;

         public Single(S var1) {
            super();
            this.single = var1;
         }

         public <T> T apply(DoubleBlockCombiner.Combiner<? super S, T> var1) {
            return var1.acceptSingle(this.single);
         }
      }

      public static final class Double<S> implements DoubleBlockCombiner.NeighborCombineResult<S> {
         private final S first;
         private final S second;

         public Double(S var1, S var2) {
            super();
            this.first = var1;
            this.second = var2;
         }

         public <T> T apply(DoubleBlockCombiner.Combiner<? super S, T> var1) {
            return var1.acceptDouble(this.first, this.second);
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
      private static DoubleBlockCombiner.BlockType[] $values() {
         return new DoubleBlockCombiner.BlockType[]{SINGLE, FIRST, SECOND};
      }
   }

   public interface Combiner<S, T> {
      T acceptDouble(S var1, S var2);

      T acceptSingle(S var1);

      T acceptNone();
   }
}
