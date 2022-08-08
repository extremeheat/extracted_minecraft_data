package net.minecraft.world.level.levelgen.blockpredicates;

import com.mojang.serialization.Codec;
import java.util.Iterator;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;

class AnyOfPredicate extends CombiningPredicate {
   public static final Codec<AnyOfPredicate> CODEC = codec(AnyOfPredicate::new);

   public AnyOfPredicate(List<BlockPredicate> var1) {
      super(var1);
   }

   public boolean test(WorldGenLevel var1, BlockPos var2) {
      Iterator var3 = this.predicates.iterator();

      BlockPredicate var4;
      do {
         if (!var3.hasNext()) {
            return false;
         }

         var4 = (BlockPredicate)var3.next();
      } while(!var4.test(var1, var2));

      return true;
   }

   public BlockPredicateType<?> type() {
      return BlockPredicateType.ANY_OF;
   }

   // $FF: synthetic method
   public boolean test(Object var1, Object var2) {
      return this.test((WorldGenLevel)var1, (BlockPos)var2);
   }
}
