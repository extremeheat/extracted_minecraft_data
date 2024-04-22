package net.minecraft.util.datafix;

import com.mojang.datafixers.Typed;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import java.util.Optional;
import java.util.stream.IntStream;

public class ExtraDataFixUtils {
   public ExtraDataFixUtils() {
      super();
   }

   public static Dynamic<?> fixBlockPos(Dynamic<?> var0) {
      Optional var1 = var0.get("X").asNumber().result();
      Optional var2 = var0.get("Y").asNumber().result();
      Optional var3 = var0.get("Z").asNumber().result();
      return !var1.isEmpty() && !var2.isEmpty() && !var3.isEmpty()
         ? var0.createIntList(IntStream.of(((Number)var1.get()).intValue(), ((Number)var2.get()).intValue(), ((Number)var3.get()).intValue()))
         : var0;
   }

   public static <T, R> Typed<R> cast(Type<R> var0, Typed<T> var1) {
      return new Typed(var0, var1.getOps(), var1.getValue());
   }
}