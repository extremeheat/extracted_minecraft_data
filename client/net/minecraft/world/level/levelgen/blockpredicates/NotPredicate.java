package net.minecraft.world.level.levelgen.blockpredicates;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;

class NotPredicate implements BlockPredicate {
   public static final MapCodec<NotPredicate> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return var0.group(BlockPredicate.CODEC.fieldOf("predicate").forGetter((var0x) -> {
         return var0x.predicate;
      })).apply(var0, NotPredicate::new);
   });
   private final BlockPredicate predicate;

   public NotPredicate(BlockPredicate var1) {
      super();
      this.predicate = var1;
   }

   public boolean test(WorldGenLevel var1, BlockPos var2) {
      return !this.predicate.test(var1, var2);
   }

   public BlockPredicateType<?> type() {
      return BlockPredicateType.NOT;
   }

   // $FF: synthetic method
   public boolean test(final Object var1, final Object var2) {
      return this.test((WorldGenLevel)var1, (BlockPos)var2);
   }
}
