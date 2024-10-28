package net.minecraft.world.level.levelgen.blockpredicates;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.function.Function;

abstract class CombiningPredicate implements BlockPredicate {
   protected final List<BlockPredicate> predicates;

   protected CombiningPredicate(List<BlockPredicate> var1) {
      super();
      this.predicates = var1;
   }

   public static <T extends CombiningPredicate> MapCodec<T> codec(Function<List<BlockPredicate>, T> var0) {
      return RecordCodecBuilder.mapCodec((var1) -> {
         return var1.group(BlockPredicate.CODEC.listOf().fieldOf("predicates").forGetter((var0x) -> {
            return var0x.predicates;
         })).apply(var1, var0);
      });
   }
}
