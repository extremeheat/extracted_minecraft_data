package net.minecraft.world.level.storage.loot.predicates;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;

public interface ConditionUserBuilder<T extends ConditionUserBuilder<T>> {
   default T when(final LootItemCondition.Builder builder) {
      return (T)this.when(Holder.direct(builder.build()));
   }

   T when(Holder<LootItemCondition> condition);

   default <E> T when(final Iterable<E> collection, final Function<E, LootItemCondition.Builder> conditionProvider) {
      T result = this.unwrap();

      for(E value : collection) {
         result = result.when((LootItemCondition.Builder)conditionProvider.apply(value));
      }

      return result;
   }

   T unwrap();

   static Optional<Holder<LootItemCondition>> buildCondition(final List<Holder<LootItemCondition>> conditions) {
      if (conditions.isEmpty()) {
         return Optional.empty();
      } else {
         return conditions.size() == 1 ? Optional.of((Holder)conditions.getFirst()) : Optional.of(Holder.direct(AllOfCondition.allOf(HolderSet.direct(conditions))));
      }
   }
}
