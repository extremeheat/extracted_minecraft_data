package net.minecraft.world.level.storage.loot.predicates;

import java.util.function.Function;

public interface ConditionUserBuilder<T extends ConditionUserBuilder<T>> {
   T when(LootItemCondition.Builder var1);

   default <E> T when(Iterable<E> var1, Function<E, LootItemCondition.Builder> var2) {
      ConditionUserBuilder var3 = this.unwrap();

      for (Object var5 : var1) {
         var3 = var3.when((LootItemCondition.Builder)var2.apply(var5));
      }

      return (T)var3;
   }

   T unwrap();
}
