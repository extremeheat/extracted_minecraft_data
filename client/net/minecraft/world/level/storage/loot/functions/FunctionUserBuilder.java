package net.minecraft.world.level.storage.loot.functions;

import java.util.Arrays;
import java.util.Iterator;
import java.util.function.Function;

public interface FunctionUserBuilder<T extends FunctionUserBuilder<T>> {
   T apply(LootItemFunction.Builder var1);

   default <E> T apply(Iterable<E> var1, Function<E, LootItemFunction.Builder> var2) {
      FunctionUserBuilder var3 = this.unwrap();

      Object var5;
      for(Iterator var4 = var1.iterator(); var4.hasNext(); var3 = var3.apply((LootItemFunction.Builder)var2.apply(var5))) {
         var5 = var4.next();
      }

      return var3;
   }

   default <E> T apply(E[] var1, Function<E, LootItemFunction.Builder> var2) {
      return this.apply((Iterable)Arrays.asList(var1), var2);
   }

   T unwrap();
}
