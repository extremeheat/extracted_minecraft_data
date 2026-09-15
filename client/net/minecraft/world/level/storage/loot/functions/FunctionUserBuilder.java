package net.minecraft.world.level.storage.loot.functions;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.core.Holder;

public interface FunctionUserBuilder<T extends FunctionUserBuilder<T>> {
   default T apply(final LootItemFunction.Builder builder) {
      return (T)this.apply(Holder.direct(builder.build()));
   }

   T apply(Holder<LootItemFunction> function);

   default <E> T apply(final Iterable<E> collection, final Function<E, LootItemFunction.Builder> functionProvider) {
      T result = this.unwrap();

      for(E value : collection) {
         result = result.apply((LootItemFunction.Builder)functionProvider.apply(value));
      }

      return result;
   }

   default <E> T apply(final E[] collection, final Function<E, LootItemFunction.Builder> functionProvider) {
      return (T)this.apply(Arrays.asList(collection), functionProvider);
   }

   T unwrap();

   static Optional<Holder<LootItemFunction>> buildFunction(final List<Holder<LootItemFunction>> conditions) {
      if (conditions.isEmpty()) {
         return Optional.empty();
      } else {
         return conditions.size() == 1 ? Optional.of((Holder)conditions.getFirst()) : Optional.of(Holder.direct(SequenceFunction.of(conditions)));
      }
   }
}
