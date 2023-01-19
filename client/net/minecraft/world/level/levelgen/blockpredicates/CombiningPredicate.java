package net.minecraft.world.level.levelgen.blockpredicates;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.function.Function;

abstract class CombiningPredicate implements BlockPredicate {
   protected final List<BlockPredicate> predicates;

   protected CombiningPredicate(List<BlockPredicate> var1) {
      super();
      this.predicates = var1;
   }

   public static <T extends CombiningPredicate> Codec<T> codec(Function<List<BlockPredicate>, T> var0) {
      return RecordCodecBuilder.create(
         var1 -> var1.group(BlockPredicate.CODEC.listOf().fieldOf("predicates").forGetter(var0xx -> var0xx.predicates)).apply(var1, var0)
      );
   }
}