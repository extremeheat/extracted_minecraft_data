package net.minecraft.world.level.levelgen.blockpredicates;

import com.mojang.serialization.MapCodec;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;

class AllOfPredicate extends CombiningPredicate {
   public static final MapCodec<AllOfPredicate> CODEC = codec(AllOfPredicate::new);

   public AllOfPredicate(List<BlockPredicate> var1) {
      super(var1);
   }

   public boolean test(WorldGenLevel var1, BlockPos var2) {
      for(BlockPredicate var4 : this.predicates) {
         if (!var4.test(var1, var2)) {
            return false;
         }
      }

      return true;
   }

   public BlockPredicateType<?> type() {
      return BlockPredicateType.ALL_OF;
   }

   // $FF: synthetic method
   public boolean test(final Object var1, final Object var2) {
      return this.test((WorldGenLevel)var1, (BlockPos)var2);
   }
}
