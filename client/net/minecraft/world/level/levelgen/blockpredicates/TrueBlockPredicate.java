package net.minecraft.world.level.levelgen.blockpredicates;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;

class TrueBlockPredicate implements BlockPredicate {
   public static TrueBlockPredicate INSTANCE = new TrueBlockPredicate();
   public static final MapCodec<TrueBlockPredicate> CODEC = MapCodec.unit(() -> INSTANCE);

   private TrueBlockPredicate() {
      super();
   }

   public boolean test(WorldGenLevel var1, BlockPos var2) {
      return true;
   }

   public BlockPredicateType<?> type() {
      return BlockPredicateType.TRUE;
   }

   // $FF: synthetic method
   public boolean test(final Object var1, final Object var2) {
      return this.test((WorldGenLevel)var1, (BlockPos)var2);
   }
}
