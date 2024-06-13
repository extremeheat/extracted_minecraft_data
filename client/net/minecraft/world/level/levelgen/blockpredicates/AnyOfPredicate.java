package net.minecraft.world.level.levelgen.blockpredicates;

import com.mojang.serialization.MapCodec;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;

class AnyOfPredicate extends CombiningPredicate {
   public static final MapCodec<AnyOfPredicate> CODEC = codec(AnyOfPredicate::new);

   public AnyOfPredicate(List<BlockPredicate> var1) {
      super(var1);
   }

   public boolean test(WorldGenLevel var1, BlockPos var2) {
      for (BlockPredicate var4 : this.predicates) {
         if (var4.test(var1, var2)) {
            return true;
         }
      }

      return false;
   }

   @Override
   public BlockPredicateType<?> type() {
      return BlockPredicateType.ANY_OF;
   }
}
