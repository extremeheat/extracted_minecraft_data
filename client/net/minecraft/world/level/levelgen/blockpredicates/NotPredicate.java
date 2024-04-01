package net.minecraft.world.level.levelgen.blockpredicates;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;

class NotPredicate implements BlockPredicate {
   public static final Codec<NotPredicate> CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(BlockPredicate.CODEC.fieldOf("predicate").forGetter(var0x -> var0x.predicate)).apply(var0, NotPredicate::new)
   );
   private final BlockPredicate predicate;

   public NotPredicate(BlockPredicate var1) {
      super();
      this.predicate = var1;
   }

   public boolean test(WorldGenLevel var1, BlockPos var2) {
      return !this.predicate.test(var1, var2);
   }

   @Override
   public BlockPredicateType<?> type() {
      return BlockPredicateType.NOT;
   }
}
