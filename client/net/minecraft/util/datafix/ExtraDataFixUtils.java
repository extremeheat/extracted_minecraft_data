package net.minecraft.util.datafix;

import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.RewriteResult;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.View;
import com.mojang.datafixers.functions.PointFreeRule;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import java.util.BitSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;

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

   public static Type<?> patchSubType(Type<?> var0, Type<?> var1, Type<?> var2) {
      return var0.all(typePatcher(var1, var2), true, false).view().newType();
   }

   private static <A, B> TypeRewriteRule typePatcher(Type<A> var0, Type<B> var1) {
      RewriteResult var2 = RewriteResult.create(View.create("Patcher", var0, var1, (var0x) -> {
         return (var0) -> {
            throw new UnsupportedOperationException();
         };
      }), new BitSet());
      return TypeRewriteRule.everywhere(TypeRewriteRule.ifSame(var0, var2), PointFreeRule.nop(), true, true);
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

   public static Dynamic<?> blockState(String var0, Map<String, String> var1) {
      Dynamic var2 = new Dynamic(NbtOps.INSTANCE, new CompoundTag());
      Dynamic var3 = var2.set("Name", var2.createString(var0));
      if (!var1.isEmpty()) {
         var3 = var3.set("Properties", var2.createMap((Map)var1.entrySet().stream().collect(Collectors.toMap((var1x) -> {
            return var2.createString((String)var1x.getKey());
         }, (var1x) -> {
            return var2.createString((String)var1x.getValue());
         }))));
      }

      return var3;
   }

   public static Dynamic<?> blockState(String var0) {
      return blockState(var0, Map.of());
   }

   public static Dynamic<?> fixStringField(Dynamic<?> var0, String var1, UnaryOperator<String> var2) {
      return var0.update(var1, (var2x) -> {
         DataResult var10000 = var2x.asString().map(var2);
         Objects.requireNonNull(var0);
         return (Dynamic)DataFixUtils.orElse(var10000.map(var0::createString).result(), var2x);
      });
   }
}
