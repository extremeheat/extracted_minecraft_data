package net.minecraft.world.level.storage.loot.functions;

import java.util.Arrays;
import java.util.function.Function;

public interface FunctionUserBuilder<T extends FunctionUserBuilder<T>> {
   T apply(LootItemFunction.Builder var1);

   default <E> T apply(Iterable<E> var1, Function<E, LootItemFunction.Builder> var2) {
      FunctionUserBuilder var3 = this.unwrap();

      for(Object var5 : var1) {
         var3 = var3.apply((LootItemFunction.Builder)var2.apply(var5));
      }

      return (T)var3;
   }

   default <E> T apply(E[] var1, Function<E, LootItemFunction.Builder> var2) {
      return this.apply(Arrays.asList(var1), var2);
   }

   T unwrap();
}
