package net.minecraft.util.datafix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.TypeTemplate;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicLike;
import com.mojang.serialization.OptionalDynamic;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.UnaryOperator;
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

   public static <T> Dynamic<T> setFieldIfPresent(Dynamic<T> var0, String var1, Optional<? extends Dynamic<?>> var2) {
      return var2.isEmpty() ? var0 : var0.set(var1, (Dynamic)var2.get());
   }

   public static <T> Dynamic<T> renameField(Dynamic<T> var0, String var1, String var2) {
      return renameAndFixField(var0, var1, var2, UnaryOperator.identity());
   }

   public static <T> Dynamic<T> replaceField(Dynamic<T> var0, String var1, String var2, Optional<? extends Dynamic<?>> var3) {
      return setFieldIfPresent(var0.remove(var1), var2, var3);
   }

   public static <T> Dynamic<T> renameAndFixField(Dynamic<T> var0, String var1, String var2, UnaryOperator<Dynamic<?>> var3) {
      return setFieldIfPresent(var0.remove(var1), var2, var0.get(var1).result().map(var3));
   }

   public static Dynamic<?> copyField(Dynamic<?> var0, String var1, Dynamic<?> var2, String var3) {
      return copyAndFixField(var0, var1, var2, var3, UnaryOperator.identity());
   }

   public static <T> Dynamic<?> copyAndFixField(Dynamic<T> var0, String var1, Dynamic<?> var2, String var3, UnaryOperator<Dynamic<T>> var4) {
      Optional var5 = var0.get(var1).result();
      return var5.isPresent() ? var2.set(var3, (Dynamic)var4.apply((Dynamic)var5.get())) : var2;
   }

   @SafeVarargs
   public static TypeTemplate optionalFields(Pair<String, TypeTemplate>... var0) {
      List var1 = Arrays.stream((Object[])var0).map(var0x -> DSL.optional(DSL.field((String)var0x.getFirst(), (TypeTemplate)var0x.getSecond()))).toList();
      return DSL.allWithRemainder((TypeTemplate)var1.get(0), var1.subList(1, var1.size()).toArray(new TypeTemplate[0]));
   }

   private static <T> DataResult<Boolean> asBoolean(Dynamic<T> var0) {
      return var0.getOps().getBooleanValue(var0.getValue());
   }

   public static DataResult<Boolean> asBoolean(DynamicLike<?> var0) {
      if (var0 instanceof Dynamic var2) {
         return asBoolean((Dynamic)var2);
      } else {
         return var0 instanceof OptionalDynamic var1
            ? var1.get().flatMap(ExtraDataFixUtils::asBoolean)
            : DataResult.error(() -> "Unknown dynamic value: " + var0);
      }
   }

   public static boolean asBoolean(DynamicLike<?> var0, boolean var1) {
      return asBoolean(var0).result().orElse(var1);
   }

   public static <T, R> Typed<R> cast(Type<R> var0, Typed<T> var1) {
      return new Typed(var0, var1.getOps(), var1.getValue());
   }
}
