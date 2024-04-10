package net.minecraft.advancements.critereon;

import com.google.common.collect.Iterables;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.function.Predicate;

public record CollectionPredicate<T, P extends Predicate<T>>(
   Optional<CollectionContentsPredicate<T, P>> contains, Optional<CollectionCountsPredicate<T, P>> counts, Optional<MinMaxBounds.Ints> size
) implements Predicate<Iterable<T>> {
   public CollectionPredicate(
      Optional<CollectionContentsPredicate<T, P>> contains, Optional<CollectionCountsPredicate<T, P>> counts, Optional<MinMaxBounds.Ints> size
   ) {
      super();
      this.contains = contains;
      this.counts = counts;
      this.size = size;
   }

   public static <T, P extends Predicate<T>> Codec<CollectionPredicate<T, P>> codec(Codec<P> var0) {
      return RecordCodecBuilder.create(
         var1 -> var1.group(
                  CollectionContentsPredicate.codec(var0).optionalFieldOf("contains").forGetter(CollectionPredicate::contains),
                  CollectionCountsPredicate.codec(var0).optionalFieldOf("count").forGetter(CollectionPredicate::counts),
                  MinMaxBounds.Ints.CODEC.optionalFieldOf("size").forGetter(CollectionPredicate::size)
               )
               .apply(var1, CollectionPredicate::new)
      );
   }

   public boolean test(Iterable<T> var1) {
      if (this.contains.isPresent() && !this.contains.get().test(var1)) {
         return false;
      } else {
         return this.counts.isPresent() && !this.counts.get().test(var1) ? false : !this.size.isPresent() || this.size.get().matches(Iterables.size(var1));
      }
   }
}
