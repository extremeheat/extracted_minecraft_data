package net.minecraft.world.level.levelgen.blockpredicates;

import com.mojang.serialization.Codec;
import java.util.Iterator;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;

class AllOfPredicate extends CombiningPredicate {
   public static final Codec<AllOfPredicate> CODEC = codec(AllOfPredicate::new);

   public AllOfPredicate(List<BlockPredicate> var1) {
      super(var1);
   }

   public boolean test(WorldGenLevel var1, BlockPos var2) {
      Iterator var3 = this.predicates.iterator();

      BlockPredicate var4;
      do {
         if (!var3.hasNext()) {
            return true;
         }

         var4 = (BlockPredicate)var3.next();
      } while(var4.test(var1, var2));

      return false;
   }

   public BlockPredicateType<?> type() {
      return BlockPredicateType.ALL_OF;
   }

   // $FF: synthetic method
   public boolean test(Object var1, Object var2) {
      return this.test((WorldGenLevel)var1, (BlockPos)var2);
   }
}
