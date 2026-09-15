package net.minecraft.world.level.blockscan;

import java.util.function.BiConsumer;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;

public abstract class BlockMatcher {
   private static final Predicate<BlockState> NO_PREDICATE = (var0) -> true;
   protected final LevelReader level;
   protected Predicate<BlockState> statePredicate;

   public BlockMatcher(final LevelReader levelReader) {
      super();
      this.statePredicate = NO_PREDICATE;
      this.level = levelReader;
   }

   public BlockMatcher filterState(final Predicate<BlockState> predicate) {
      this.statePredicate = this.statePredicate == NO_PREDICATE ? predicate : this.statePredicate.and(predicate);
      return this;
   }

   public abstract boolean atLeastMatched(final int n);

   public boolean atMostMatched(final int n) {
      return !this.atLeastMatched(n + 1);
   }

   public boolean anyMatched() {
      return this.anyMatched(this.statePredicate);
   }

   protected abstract boolean anyMatched(final Predicate<BlockState> statePredicate);

   public boolean noneMatched() {
      return !this.anyMatched();
   }

   public boolean allMatched() {
      return !this.anyMatched((state) -> !this.statePredicate.test(state));
   }

   public abstract void forEach(final BiConsumer<BlockPos, BlockState> consumer);

   public abstract boolean forEachUntil(final BlockStateConsumer consumer);
}
