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

   @Override
   public BlockPredicateType<?> type() {
      return BlockPredicateType.TRUE;
   }
}
