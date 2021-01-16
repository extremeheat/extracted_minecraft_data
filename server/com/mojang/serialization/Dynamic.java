package com.mojang.serialization;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.util.Pair;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import javax.annotation.Nullable;

public class Dynamic<T> extends DynamicLike<T> {
   private final T value;

   public Dynamic(DynamicOps<T> var1) {
      this(var1, var1.empty());
   }

   public Dynamic(DynamicOps<T> var1, @Nullable T var2) {
      super(var1);
      this.value = var2 == null ? var1.empty() : var2;
   }

   public T getValue() {
      return this.value;
   }

   public Dynamic<T> map(Function<? super T, ? extends T> var1) {
      return new Dynamic(this.ops, var1.apply(this.value));
   }

   public <U> Dynamic<U> castTyped(DynamicOps<U> var1) {
      if (!Objects.equals(this.ops, var1)) {
         throw new IllegalStateException("Dynamic type doesn't match");
      } else {
         return this;
      }
   }

   public <U> U cast(DynamicOps<U> var1) {
      return this.castTyped(var1).getValue();
   }

   public OptionalDynamic<T> merge(Dynamic<?> var1) {
      DataResult var2 = this.ops.mergeToList(this.value, var1.cast(this.ops));
      return new OptionalDynamic(this.ops, var2.map((var1x) -> {
         return new Dynamic(this.ops, var1x);
      }));
   }

   public OptionalDynamic<T> merge(Dynamic<?> var1, Dynamic<?> var2) {
      DataResult var3 = this.ops.mergeToMap(this.value, var1.cast(this.ops), var2.cast(this.ops));
      return new OptionalDynamic(this.ops, var3.map((var1x) -> {
         return new Dynamic(this.ops, var1x);
      }));
   }

   public DataResult<Map<Dynamic<T>, Dynamic<T>>> getMapValues() {
      return this.ops.getMapValues(this.value).map((var1) -> {
         ImmutableMap.Builder var2 = ImmutableMap.builder();
         var1.forEach((var2x) -> {
            var2.put(new Dynamic(this.ops, var2x.getFirst()), new Dynamic(this.ops, var2x.getSecond()));
         });
         return var2.build();
      });
   }

   public Dynamic<T> updateMapValues(Function<Pair<Dynamic<?>, Dynamic<?>>, Pair<Dynamic<?>, Dynamic<?>>> var1) {
      return (Dynamic)DataFixUtils.orElse(this.getMapValues().map((var2) -> {
         return (Map)var2.entrySet().stream().map((var2x) -> {
            Pair var3 = (Pair)var1.apply(Pair.of(var2x.getKey(), var2x.getValue()));
            return Pair.of(((Dynamic)var3.getFirst()).castTyped(this.ops), ((Dynamic)var3.getSecond()).castTyped(this.ops));
         }).collect(Pair.toMap());
      }).map(this::createMap).result(), this);
   }

   public DataResult<Number> asNumber() {
      return this.ops.getNumberValue(this.value);
   }

   public DataResult<String> asString() {
      return this.ops.getStringValue(this.value);
   }

   public DataResult<Stream<Dynamic<T>>> asStreamOpt() {
      return this.ops.getStream(this.value).map((var1) -> {
         return var1.map((var1x) -> {
            return new Dynamic(this.ops, var1x);
         });
      });
   }

   public DataResult<Stream<Pair<Dynamic<T>, Dynamic<T>>>> asMapOpt() {
      return this.ops.getMapValues(this.value).map((var1) -> {
         return var1.map((var1x) -> {
            return Pair.of(new Dynamic(this.ops, var1x.getFirst()), new Dynamic(this.ops, var1x.getSecond()));
         });
      });
   }

   public DataResult<ByteBuffer> asByteBufferOpt() {
      return this.ops.getByteBuffer(this.value);
   }

   public DataResult<IntStream> asIntStreamOpt() {
      return this.ops.getIntStream(this.value);
   }

   public DataResult<LongStream> asLongStreamOpt() {
      return this.ops.getLongStream(this.value);
   }

   public OptionalDynamic<T> get(String var1) {
      return new OptionalDynamic(this.ops, this.ops.getMap(this.value).flatMap((var2) -> {
         Object var3 = var2.get(var1);
         return var3 == null ? DataResult.error("key missing: " + var1 + " in " + this.value) : DataResult.success(new Dynamic(this.ops, var3));
      }));
   }

   public DataResult<T> getGeneric(T var1) {
      return this.ops.getGeneric(this.value, var1);
   }

   public Dynamic<T> remove(String var1) {
      return this.map((var2) -> {
         return this.ops.remove(var2, var1);
      });
   }

   public Dynamic<T> set(String var1, Dynamic<?> var2) {
      return this.map((var3) -> {
         return this.ops.set(var3, var1, var2.cast(this.ops));
      });
   }

   public Dynamic<T> update(String var1, Function<Dynamic<?>, Dynamic<?>> var2) {
      return this.map((var3) -> {
         return this.ops.update(var3, var1, (var2x) -> {
            return ((Dynamic)var2.apply(new Dynamic(this.ops, var2x))).cast(this.ops);
         });
      });
   }

   public Dynamic<T> updateGeneric(T var1, Function<T, T> var2) {
      return this.map((var3) -> {
         return this.ops.updateGeneric(var3, var1, var2);
      });
   }

   public DataResult<T> getElement(String var1) {
      return this.getElementGeneric(this.ops.createString(var1));
   }

   public DataResult<T> getElementGeneric(T var1) {
      return this.ops.getGeneric(this.value, var1);
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         Dynamic var2 = (Dynamic)var1;
         return Objects.equals(this.ops, var2.ops) && Objects.equals(this.value, var2.value);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.ops, this.value});
   }

   public String toString() {
      return String.format("%s[%s]", this.ops, this.value);
   }

   public <R> Dynamic<R> convert(DynamicOps<R> var1) {
      return new Dynamic(var1, convert(this.ops, var1, this.value));
   }

   public <V> V into(Function<? super Dynamic<T>, ? extends V> var1) {
      return var1.apply(this);
   }

   public <A> DataResult<Pair<A, T>> decode(Decoder<? extends A> var1) {
      return var1.decode(this.ops, this.value).map((var0) -> {
         return var0.mapFirst(Function.identity());
      });
   }

   public static <S, T> T convert(DynamicOps<S> var0, DynamicOps<T> var1, S var2) {
      return Objects.equals(var0, var1) ? var2 : var0.convertTo(var1, var2);
   }
}
