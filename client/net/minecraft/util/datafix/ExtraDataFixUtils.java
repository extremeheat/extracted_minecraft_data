package net.minecraft.util.datafix;

import com.mojang.datafixers.Typed;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.IntStream;

public class ExtraDataFixUtils {
   public ExtraDataFixUtils() {
      super();
   }

   public static Dynamic<?> fixBlockPos(Dynamic<?> var0) {
      Optional var1 = var0.get("X").asNumber().result();
      Optional var2 = var0.get("Y").asNumber().result();
      Optional var3 = var0.get("Z").asNumber().result();
      return !var1.isEmpty() && !var2.isEmpty() && !var3.isEmpty() ? var0.createIntList(IntStream.of(new int[]{((Number)var1.get()).intValue(), ((Number)var2.get()).intValue(), ((Number)var3.get()).intValue()})) : var0;
   }

   public static <T, R> Typed<R> cast(Type<R> var0, Typed<T> var1) {
      return new Typed(var0, var1.getOps(), var1.getValue());
   }

   @SafeVarargs
   public static <T> Function<Typed<?>, Typed<?>> chainAllFilters(Function<Typed<?>, Typed<?>>... var0) {
      return (var1) -> {
         Function[] var2 = var0;
         int var3 = var0.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            Function var5 = var2[var4];
            var1 = (Typed)var5.apply(var1);
         }

         return var1;
      };
   }
}
