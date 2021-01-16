package com.mojang.serialization;

import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

public abstract class MapCodec<A> extends CompressorHolder implements MapDecoder<A>, MapEncoder<A> {
   public MapCodec() {
      super();
   }

   public final <O> RecordCodecBuilder<O, A> forGetter(Function<O, A> var1) {
      return RecordCodecBuilder.of(var1, this);
   }

   public static <A> MapCodec<A> of(MapEncoder<A> var0, MapDecoder<A> var1) {
      return of(var0, var1, () -> {
         return "MapCodec[" + var0 + " " + var1 + "]";
      });
   }

   public static <A> MapCodec<A> of(final MapEncoder<A> var0, final MapDecoder<A> var1, final Supplier<String> var2) {
      return new MapCodec<A>() {
         public <T> Stream<T> keys(DynamicOps<T> var1x) {
            return Stream.concat(var0.keys(var1x), var1.keys(var1x));
         }

         public <T> DataResult<A> decode(DynamicOps<T> var1x, MapLike<T> var2x) {
            return var1.decode(var1x, var2x);
         }

         public <T> RecordBuilder<T> encode(A var1x, DynamicOps<T> var2x, RecordBuilder<T> var3) {
            return var0.encode(var1x, var2x, var3);
         }

         public String toString() {
            return (String)var2.get();
         }
      };
   }

   public MapCodec<A> fieldOf(String var1) {
      return this.codec().fieldOf(var1);
   }

   public MapCodec<A> withLifecycle(final Lifecycle var1) {
      return new MapCodec<A>() {
         public <T> Stream<T> keys(DynamicOps<T> var1x) {
            return MapCodec.this.keys(var1x);
         }

         public <T> DataResult<A> decode(DynamicOps<T> var1x, MapLike<T> var2) {
            return MapCodec.this.decode(var1x, var2).setLifecycle(var1);
         }

         public <T> RecordBuilder<T> encode(A var1x, DynamicOps<T> var2, RecordBuilder<T> var3) {
            return MapCodec.this.encode(var1x, var2, var3).setLifecycle(var1);
         }

         public String toString() {
            return MapCodec.this.toString();
         }
      };
   }

   public Codec<A> codec() {
      return new MapCodec.MapCodecCodec(this);
   }

   public MapCodec<A> stable() {
      return this.withLifecycle(Lifecycle.stable());
   }

   public MapCodec<A> deprecated(int var1) {
      return this.withLifecycle(Lifecycle.deprecated(var1));
   }

   public <S> MapCodec<S> xmap(Function<? super A, ? extends S> var1, Function<? super S, ? extends A> var2) {
      return of(this.comap(var2), this.map(var1), () -> {
         return this.toString() + "[xmapped]";
      });
   }

   public <S> MapCodec<S> flatXmap(Function<? super A, ? extends DataResult<? extends S>> var1, Function<? super S, ? extends DataResult<? extends A>> var2) {
      return Codec.of(this.flatComap(var2), this.flatMap(var1), () -> {
         return this.toString() + "[flatXmapped]";
      });
   }

   public <E> MapCodec<A> dependent(MapCodec<E> var1, Function<A, Pair<E, MapCodec<E>>> var2, BiFunction<A, E, A> var3) {
      return new MapCodec.Dependent(this, var1, var2, var3);
   }

   public abstract <T> Stream<T> keys(DynamicOps<T> var1);

   public MapCodec<A> mapResult(final MapCodec.ResultFunction<A> var1) {
      return new MapCodec<A>() {
         public <T> Stream<T> keys(DynamicOps<T> var1x) {
            return MapCodec.this.keys(var1x);
         }

         public <T> RecordBuilder<T> encode(A var1x, DynamicOps<T> var2, RecordBuilder<T> var3) {
            return var1.coApply(var2, var1x, MapCodec.this.encode(var1x, var2, var3));
         }

         public <T> DataResult<A> decode(DynamicOps<T> var1x, MapLike<T> var2) {
            return var1.apply(var1x, var2, MapCodec.this.decode(var1x, var2));
         }

         public String toString() {
            return MapCodec.this + "[mapResult " + var1 + "]";
         }
      };
   }

   public MapCodec<A> orElse(Consumer<String> var1, A var2) {
      return this.orElse(DataFixUtils.consumerToFunction(var1), var2);
   }

   public MapCodec<A> orElse(final UnaryOperator<String> var1, final A var2) {
      return this.mapResult(new MapCodec.ResultFunction<A>() {
         public <T> DataResult<A> apply(DynamicOps<T> var1x, MapLike<T> var2x, DataResult<A> var3) {
            return DataResult.success(var3.mapError(var1).result().orElse(var2));
         }

         public <T> RecordBuilder<T> coApply(DynamicOps<T> var1x, A var2x, RecordBuilder<T> var3) {
            return var3.mapError(var1);
         }

         public String toString() {
            return "OrElse[" + var1 + " " + var2 + "]";
         }
      });
   }

   public MapCodec<A> orElseGet(Consumer<String> var1, Supplier<? extends A> var2) {
      return this.orElseGet(DataFixUtils.consumerToFunction(var1), var2);
   }

   public MapCodec<A> orElseGet(final UnaryOperator<String> var1, final Supplier<? extends A> var2) {
      return this.mapResult(new MapCodec.ResultFunction<A>() {
         public <T> DataResult<A> apply(DynamicOps<T> var1x, MapLike<T> var2x, DataResult<A> var3) {
            return DataResult.success(var3.mapError(var1).result().orElseGet(var2));
         }

         public <T> RecordBuilder<T> coApply(DynamicOps<T> var1x, A var2x, RecordBuilder<T> var3) {
            return var3.mapError(var1);
         }

         public String toString() {
            return "OrElseGet[" + var1 + " " + var2.get() + "]";
         }
      });
   }

   public MapCodec<A> orElse(final A var1) {
      return this.mapResult(new MapCodec.ResultFunction<A>() {
         public <T> DataResult<A> apply(DynamicOps<T> var1x, MapLike<T> var2, DataResult<A> var3) {
            return DataResult.success(var3.result().orElse(var1));
         }

         public <T> RecordBuilder<T> coApply(DynamicOps<T> var1x, A var2, RecordBuilder<T> var3) {
            return var3;
         }

         public String toString() {
            return "OrElse[" + var1 + "]";
         }
      });
   }

   public MapCodec<A> orElseGet(final Supplier<? extends A> var1) {
      return this.mapResult(new MapCodec.ResultFunction<A>() {
         public <T> DataResult<A> apply(DynamicOps<T> var1x, MapLike<T> var2, DataResult<A> var3) {
            return DataResult.success(var3.result().orElseGet(var1));
         }

         public <T> RecordBuilder<T> coApply(DynamicOps<T> var1x, A var2, RecordBuilder<T> var3) {
            return var3;
         }

         public String toString() {
            return "OrElseGet[" + var1.get() + "]";
         }
      });
   }

   public MapCodec<A> setPartial(final Supplier<A> var1) {
      return this.mapResult(new MapCodec.ResultFunction<A>() {
         public <T> DataResult<A> apply(DynamicOps<T> var1x, MapLike<T> var2, DataResult<A> var3) {
            return var3.setPartial(var1);
         }

         public <T> RecordBuilder<T> coApply(DynamicOps<T> var1x, A var2, RecordBuilder<T> var3) {
            return var3;
         }

         public String toString() {
            return "SetPartial[" + var1 + "]";
         }
      });
   }

   public static <A> MapCodec<A> unit(A var0) {
      return unit(() -> {
         return var0;
      });
   }

   public static <A> MapCodec<A> unit(Supplier<A> var0) {
      return of(Encoder.empty(), Decoder.unit(var0));
   }

   public interface ResultFunction<A> {
      <T> DataResult<A> apply(DynamicOps<T> var1, MapLike<T> var2, DataResult<A> var3);

      <T> RecordBuilder<T> coApply(DynamicOps<T> var1, A var2, RecordBuilder<T> var3);
   }

   private static class Dependent<O, E> extends MapCodec<O> {
      private final MapCodec<E> initialInstance;
      private final Function<O, Pair<E, MapCodec<E>>> splitter;
      private final MapCodec<O> codec;
      private final BiFunction<O, E, O> combiner;

      public Dependent(MapCodec<O> var1, MapCodec<E> var2, Function<O, Pair<E, MapCodec<E>>> var3, BiFunction<O, E, O> var4) {
         super();
         this.initialInstance = var2;
         this.splitter = var3;
         this.codec = var1;
         this.combiner = var4;
      }

      public <T> Stream<T> keys(DynamicOps<T> var1) {
         return Stream.concat(this.codec.keys(var1), this.initialInstance.keys(var1));
      }

      public <T> DataResult<O> decode(DynamicOps<T> var1, MapLike<T> var2) {
         return this.codec.decode(var1, var2).flatMap((var3) -> {
            return ((MapCodec)((Pair)this.splitter.apply(var3)).getSecond()).decode(var1, var2).map((var2x) -> {
               return this.combiner.apply(var3, var2x);
            }).setLifecycle(Lifecycle.experimental());
         });
      }

      public <T> RecordBuilder<T> encode(O var1, DynamicOps<T> var2, RecordBuilder<T> var3) {
         this.codec.encode(var1, var2, var3);
         Pair var4 = (Pair)this.splitter.apply(var1);
         ((MapCodec)var4.getSecond()).encode(var4.getFirst(), var2, var3);
         return var3.setLifecycle(Lifecycle.experimental());
      }
   }

   public static final class MapCodecCodec<A> implements Codec<A> {
      private final MapCodec<A> codec;

      public MapCodecCodec(MapCodec<A> var1) {
         super();
         this.codec = var1;
      }

      public MapCodec<A> codec() {
         return this.codec;
      }

      public <T> DataResult<Pair<A, T>> decode(DynamicOps<T> var1, T var2) {
         return this.codec.compressedDecode(var1, var2).map((var1x) -> {
            return Pair.of(var1x, var2);
         });
      }

      public <T> DataResult<T> encode(A var1, DynamicOps<T> var2, T var3) {
         return this.codec.encode(var1, var2, this.codec.compressedBuilder(var2)).build(var3);
      }

      public String toString() {
         return this.codec.toString();
      }
   }
}
