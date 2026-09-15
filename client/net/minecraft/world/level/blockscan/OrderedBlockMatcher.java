package net.minecraft.world.level.blockscan;

import com.google.common.collect.Iterables;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Continuation;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.mutable.MutableObject;

public class OrderedBlockMatcher extends BlockMatcher {
   private Iterable<BlockPos> positions;

   public OrderedBlockMatcher(final LevelReader level, final Iterable<BlockPos> positions) {
      super(level);
      this.positions = positions;
   }

   public OrderedBlockMatcher filterPos(final Predicate<? super BlockPos> predicate) {
      Iterable var10001 = this.positions;
      Objects.requireNonNull(predicate);
      this.positions = Iterables.filter(var10001, predicate::test);
      return this;
   }

   public OrderedBlockMatcher filterState(final Predicate<BlockState> predicate) {
      super.filterState(predicate);
      return this;
   }

   public boolean atLeastMatched(final int n) {
      return BlockScanUtils.findBlocksWithCache(this.level, this.positions, this.statePredicate, new BlockStateConsumer() {
         private int count;

         {
            Objects.requireNonNull(OrderedBlockMatcher.this);
            this.count = 0;
         }

         public Continuation apply(final BlockPos pos, final BlockState state) {
            return Continuation.abortIf(++this.count >= n);
         }
      });
   }

   protected boolean anyMatched(final Predicate<BlockState> statePredicate) {
      return BlockScanUtils.findBlocksWithCache(this.level, this.positions, statePredicate, (var0, var1) -> Continuation.ABORT);
   }

   public void forEach(final BiConsumer<BlockPos, BlockState> consumer) {
      BlockScanUtils.findBlocksWithCache(this.level, this.positions, this.statePredicate, (pos, state) -> {
         consumer.accept(pos, state);
         return Continuation.CONTINUE;
      });
   }

   public boolean forEachUntil(final BlockStateConsumer consumer) {
      return BlockScanUtils.findBlocksWithCache(this.level, this.positions, this.statePredicate, consumer);
   }

   public Optional<BlockPos> findFirst() {
      MutableObject<Optional<BlockPos>> result = new MutableObject(Optional.empty());
      BlockScanUtils.findBlocksWithCache(this.level, this.positions, this.statePredicate, (pos, var2) -> {
         result.setValue(Optional.of(pos.immutable()));
         return Continuation.ABORT;
      });
      return (Optional)result.get();
   }

   public Optional<BlockPos> findFirst(final BiPredicate<BlockPos, BlockState> predicate) {
      MutableObject<Optional<BlockPos>> result = new MutableObject(Optional.empty());
      BlockScanUtils.findBlocksWithCache(this.level, this.positions, this.statePredicate, (pos, state) -> {
         if (predicate.test(pos, state)) {
            result.setValue(Optional.of(pos.immutable()));
            return Continuation.ABORT;
         } else {
            return Continuation.CONTINUE;
         }
      });
      return (Optional)result.get();
   }
}
