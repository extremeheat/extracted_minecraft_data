package net.minecraft.world.level.storage.loot.predicates;

import java.util.Iterator;
import java.util.function.Function;

public interface ConditionUserBuilder<T extends ConditionUserBuilder<T>> {
   T when(LootItemCondition.Builder var1);

   default <E> T when(Iterable<E> var1, Function<E, LootItemCondition.Builder> var2) {
      ConditionUserBuilder var3 = this.unwrap();

      Object var5;
      for(Iterator var4 = var1.iterator(); var4.hasNext(); var3 = var3.when((LootItemCondition.Builder)var2.apply(var5))) {
         var5 = var4.next();
      }

      return var3;
   }

   T unwrap();
}
