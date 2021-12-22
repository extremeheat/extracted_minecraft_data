package net.minecraft.world.level.levelgen.blockpredicates;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;

class TrueBlockPredicate implements BlockPredicate {
   public static TrueBlockPredicate INSTANCE = new TrueBlockPredicate();
   public static final Codec<TrueBlockPredicate> CODEC = Codec.unit(() -> {
      return INSTANCE;
   });

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
   public boolean test(Object var1, Object var2) {
      return this.test((WorldGenLevel)var1, (BlockPos)var2);
   }
}
