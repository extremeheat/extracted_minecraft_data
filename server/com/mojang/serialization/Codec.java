package com.mojang.serialization;

import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.codecs.CompoundListCodec;
import com.mojang.serialization.codecs.EitherCodec;
import com.mojang.serialization.codecs.EitherMapCodec;
import com.mojang.serialization.codecs.KeyDispatchCodec;
import com.mojang.serialization.codecs.ListCodec;
import com.mojang.serialization.codecs.OptionalFieldCodec;
import com.mojang.serialization.codecs.PairCodec;
import com.mojang.serialization.codecs.PairMapCodec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.SimpleMapCodec;
import com.mojang.serialization.codecs.UnboundedMapCodec;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public interface Codec<A> extends Encoder<A>, Decoder<A> {
   PrimitiveCodec<Boolean> BOOL = new PrimitiveCodec<Boolean>() {
      public <T> DataResult<Boolean> read(DynamicOps<T> var1, T var2) {
         return var1.getBooleanValue(var2);
      }

      public <T> T write(DynamicOps<T> var1, Boolean var2) {
         return var1.createBoolean(var2);
      }

      public String toString() {
         return "Bool";
      }
   };
   PrimitiveCodec<Byte> BYTE = new PrimitiveCodec<Byte>() {
      public <T> DataResult<Byte> read(DynamicOps<T> var1, T var2) {
         return var1.getNumberValue(var2).map(Number::byteValue);
      }

      public <T> T write(DynamicOps<T> var1, Byte var2) {
         return var1.createByte(var2);
      }

      public String toString() {
         return "Byte";
      }
   };
   PrimitiveCodec<Short> SHORT = new PrimitiveCodec<Short>() {
      public <T> DataResult<Short> read(DynamicOps<T> var1, T var2) {
         return var1.getNumberValue(var2).map(Number::shortValue);
      }

      public <T> T write(DynamicOps<T> var1, Short var2) {
         return var1.createShort(var2);
      }

      public String toString() {
         return "Short";
      }
   };
   PrimitiveCodec<Integer> INT = new PrimitiveCodec<Integer>() {
      public <T> DataResult<Integer> read(DynamicOps<T> var1, T var2) {
         return var1.getNumberValue(var2).map(Number::intValue);
      }

      public <T> T write(DynamicOps<T> var1, Integer var2) {
         return var1.createInt(var2);
      }

      public String toString() {
         return "Int";
      }
   };
   PrimitiveCodec<Long> LONG = new PrimitiveCodec<Long>() {
      public <T> DataResult<Long> read(DynamicOps<T> var1, T var2) {
         return var1.getNumberValue(var2).map(Number::longValue);
      }

      public <T> T write(DynamicOps<T> var1, Long var2) {
         return var1.createLong(var2);
      }

      public String toString() {
         return "Long";
      }
   };
   PrimitiveCodec<Float> FLOAT = new PrimitiveCodec<Float>() {
      public <T> DataResult<Float> read(DynamicOps<T> var1, T var2) {
         return var1.getNumberValue(var2).map(Number::floatValue);
      }

      public <T> T write(DynamicOps<T> var1, Float var2) {
         return var1.createFloat(var2);
      }

      public String toString() {
         return "Float";
      }
   };
   PrimitiveCodec<Double> DOUBLE = new PrimitiveCodec<Double>() {
      public <T> DataResult<Double> read(DynamicOps<T> var1, T var2) {
         return var1.getNumberValue(var2).map(Number::doubleValue);
      }

      public <T> T write(DynamicOps<T> var1, Double var2) {
         return var1.createDouble(var2);
      }

      public String toString() {
         return "Double";
      }
   };
   PrimitiveCodec<String> STRING = new PrimitiveCodec<String>() {
      public <T> DataResult<String> read(DynamicOps<T> var1, T var2) {
         return var1.getStringValue(var2);
      }

      public <T> T write(DynamicOps<T> var1, String var2) {
         return var1.createString(var2);
      }

      public String toString() {
         return "String";
      }
   };
   PrimitiveCodec<ByteBuffer> BYTE_BUFFER = new PrimitiveCodec<ByteBuffer>() {
      public <T> DataResult<ByteBuffer> read(DynamicOps<T> var1, T var2) {
         return var1.getByteBuffer(var2);
      }

      public <T> T write(DynamicOps<T> var1, ByteBuffer var2) {
         return var1.createByteList(var2);
      }

      public String toString() {
         return "ByteBuffer";
      }
   };
   PrimitiveCodec<IntStream> INT_STREAM = new PrimitiveCodec<IntStream>() {
      public <T> DataResult<IntStream> read(DynamicOps<T> var1, T var2) {
         return var1.getIntStream(var2);
      }

      public <T> T write(DynamicOps<T> var1, IntStream var2) {
         return var1.createIntList(var2);
      }

      public String toString() {
         return "IntStream";
      }
   };
   PrimitiveCodec<LongStream> LONG_STREAM = new PrimitiveCodec<LongStream>() {
      public <T> DataResult<LongStream> read(DynamicOps<T> var1, T var2) {
         return var1.getLongStream(var2);
      }

      public <T> T write(DynamicOps<T> var1, LongStream var2) {
         return var1.createLongList(var2);
      }

      public String toString() {
         return "LongStream";
      }
   };
   Codec<Dynamic<?>> PASSTHROUGH = new Codec<Dynamic<?>>() {
      public <T> DataResult<Pair<Dynamic<?>, T>> decode(DynamicOps<T> var1, T var2) {
         return DataResult.success(Pair.of(new Dynamic(var1, var2), var1.empty()));
      }

      public <T> DataResult<T> encode(Dynamic<?> var1, DynamicOps<T> var2, T var3) {
         if (var1.getValue() == var1.getOps().empty()) {
            return DataResult.success(var3, Lifecycle.experimental());
         } else {
            Object var4 = var1.convert(var2).getValue();
            if (var3 == var2.empty()) {
               return DataResult.success(var4, Lifecycle.experimental());
            } else {
               DataResult var5 = var2.getMap(var4).flatMap((var2x) -> {
                  return var2.mergeToMap(var3, var2x);
               });
               return (DataResult)var5.result().map(DataResult::success).orElseGet(() -> {
                  DataResult var3x = var2.getStream(var4).flatMap((var2x) -> {
                     return var2.mergeToList(var3, (List)var2x.collect(Collectors.toList()));
                  });
                  return (DataResult)var3x.result().map(DataResult::success).orElseGet(() -> {
                     return DataResult.error("Don't know how to merge " + var3 + " and " + var4, var3, Lifecycle.experimental());
                  });
               });
            }
         }
      }

      public String toString() {
         return "passthrough";
      }
   };
   MapCodec<Unit> EMPTY = MapCodec.of(Encoder.empty(), Decoder.unit((Object)Unit.INSTANCE));

   default Codec<A> withLifecycle(final Lifecycle var1) {
      return new Codec<A>() {
         public <T> DataResult<T> encode(A var1x, DynamicOps<T> var2, T var3) {
            return Codec.this.encode(var1x, var2, var3).setLifecycle(var1);
         }

         public <T> DataResult<Pair<A, T>> decode(DynamicOps<T> var1x, T var2) {
            return Codec.this.decode(var1x, var2).setLifecycle(var1);
         }

         public String toString() {
            return Codec.this.toString();
         }
      };
   }

   default Codec<A> stable() {
      return this.withLifecycle(Lifecycle.stable());
   }

   default Codec<A> deprecated(int var1) {
      return this.withLifecycle(Lifecycle.deprecated(var1));
   }

   static <A> Codec<A> of(Encoder<A> var0, Decoder<A> var1) {
      return of(var0, var1, "Codec[" + var0 + " " + var1 + "]");
   }

   static <A> Codec<A> of(final Encoder<A> var0, final Decoder<A> var1, final String var2) {
      return new Codec<A>() {
         public <T> DataResult<Pair<A, T>> decode(DynamicOps<T> var1x, T var2x) {
            return var1.decode(var1x, var2x);
         }

         public <T> DataResult<T> encode(A var1x, DynamicOps<T> var2x, T var3) {
            return var0.encode(var1x, var2x, var3);
         }

         public String toString() {
            return var2;
         }
      };
   }

   static <A> MapCodec<A> of(MapEncoder<A> var0, MapDecoder<A> var1) {
      return of(var0, var1, () -> {
         return "MapCodec[" + var0 + " " + var1 + "]";
      });
   }

   static <A> MapCodec<A> of(final MapEncoder<A> var0, final MapDecoder<A> var1, final Supplier<String> var2) {
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

   static <F, S> Codec<Pair<F, S>> pair(Codec<F> var0, Codec<S> var1) {
      return new PairCodec(var0, var1);
   }

   static <F, S> Codec<Either<F, S>> either(Codec<F> var0, Codec<S> var1) {
      return new EitherCodec(var0, var1);
   }

   static <F, S> MapCodec<Pair<F, S>> mapPair(MapCodec<F> var0, MapCodec<S> var1) {
      return new PairMapCodec(var0, var1);
   }

   static <F, S> MapCodec<Either<F, S>> mapEither(MapCodec<F> var0, MapCodec<S> var1) {
      return new EitherMapCodec(var0, var1);
   }

   static <E> Codec<List<E>> list(Codec<E> var0) {
      return new ListCodec(var0);
   }

   static <K, V> Codec<List<Pair<K, V>>> compoundList(Codec<K> var0, Codec<V> var1) {
      return new CompoundListCodec(var0, var1);
   }

   static <K, V> SimpleMapCodec<K, V> simpleMap(Codec<K> var0, Codec<V> var1, Keyable var2) {
      return new SimpleMapCodec(var0, var1, var2);
   }

   static <K, V> UnboundedMapCodec<K, V> unboundedMap(Codec<K> var0, Codec<V> var1) {
      return new UnboundedMapCodec(var0, var1);
   }

   static <F> MapCodec<Optional<F>> optionalField(String var0, Codec<F> var1) {
      return new OptionalFieldCodec(var0, var1);
   }

   default Codec<List<A>> listOf() {
      return list(this);
   }

   default <S> Codec<S> xmap(Function<? super A, ? extends S> var1, Function<? super S, ? extends A> var2) {
      return of(this.comap(var2), this.map(var1), this.toString() + "[xmapped]");
   }

   default <S> Codec<S> comapFlatMap(Function<? super A, ? extends DataResult<? extends S>> var1, Function<? super S, ? extends A> var2) {
      return of(this.comap(var2), this.flatMap(var1), this.toString() + "[comapFlatMapped]");
   }

   default <S> Codec<S> flatComapMap(Function<? super A, ? extends S> var1, Function<? super S, ? extends DataResult<? extends A>> var2) {
      return of(this.flatComap(var2), this.map(var1), this.toString() + "[flatComapMapped]");
   }

   default <S> Codec<S> flatXmap(Function<? super A, ? extends DataResult<? extends S>> var1, Function<? super S, ? extends DataResult<? extends A>> var2) {
      return of(this.flatComap(var2), this.flatMap(var1), this.toString() + "[flatXmapped]");
   }

   default MapCodec<A> fieldOf(String var1) {
      return MapCodec.of(Encoder.super.fieldOf(var1), Decoder.super.fieldOf(var1), () -> {
         return "Field[" + var1 + ": " + this.toString() + "]";
      });
   }

   default MapCodec<Optional<A>> optionalFieldOf(String var1) {
      return optionalField(var1, this);
   }

   default MapCodec<A> optionalFieldOf(String var1, A var2) {
      return optionalField(var1, this).xmap((var1x) -> {
         return var1x.orElse(var2);
      }, (var1x) -> {
         return Objects.equals(var1x, var2) ? Optional.empty() : Optional.of(var1x);
      });
   }

   default MapCodec<A> optionalFieldOf(String var1, A var2, Lifecycle var3) {
      return this.optionalFieldOf(var1, Lifecycle.experimental(), var2, var3);
   }

   default MapCodec<A> optionalFieldOf(String var1, Lifecycle var2, A var3, Lifecycle var4) {
      return optionalField(var1, this).stable().flatXmap((var3x) -> {
         return (DataResult)var3x.map((var1) -> {
            return DataResult.success(var1, var2);
         }).orElse(DataResult.success(var3, var4));
      }, (var3x) -> {
         return Objects.equals(var3x, var3) ? DataResult.success(Optional.empty(), var4) : DataResult.success(Optional.of(var3x), var2);
      });
   }

   default Codec<A> mapResult(final Codec.ResultFunction<A> var1) {
      return new Codec<A>() {
         public <T> DataResult<T> encode(A var1x, DynamicOps<T> var2, T var3) {
            return var1.coApply(var2, var1x, Codec.this.encode(var1x, var2, var3));
         }

         public <T> DataResult<Pair<A, T>> decode(DynamicOps<T> var1x, T var2) {
            return var1.apply(var1x, var2, Codec.this.decode(var1x, var2));
         }

         public String toString() {
            return Codec.this + "[mapResult " + var1 + "]";
         }
      };
   }

   default Codec<A> orElse(Consumer<String> var1, A var2) {
      return this.orElse(DataFixUtils.consumerToFunction(var1), var2);
   }

   default Codec<A> orElse(final UnaryOperator<String> var1, final A var2) {
      return this.mapResult(new Codec.ResultFunction<A>() {
         public <T> DataResult<Pair<A, T>> apply(DynamicOps<T> var1x, T var2x, DataResult<Pair<A, T>> var3) {
            return DataResult.success(var3.mapError(var1).result().orElseGet(() -> {
               return Pair.of(var2, var2x);
            }));
         }

         public <T> DataResult<T> coApply(DynamicOps<T> var1x, A var2x, DataResult<T> var3) {
            return var3.mapError(var1);
         }

         public String toString() {
            return "OrElse[" + var1 + " " + var2 + "]";
         }
      });
   }

   default Codec<A> orElseGet(Consumer<String> var1, Supplier<? extends A> var2) {
      return this.orElseGet(DataFixUtils.consumerToFunction(var1), var2);
   }

   default Codec<A> orElseGet(final UnaryOperator<String> var1, final Supplier<? extends A> var2) {
      return this.mapResult(new Codec.ResultFunction<A>() {
         public <T> DataResult<Pair<A, T>> apply(DynamicOps<T> var1x, T var2x, DataResult<Pair<A, T>> var3) {
            return DataResult.success(var3.mapError(var1).result().orElseGet(() -> {
               return Pair.of(var2.get(), var2x);
            }));
         }

         public <T> DataResult<T> coApply(DynamicOps<T> var1x, A var2x, DataResult<T> var3) {
            return var3.mapError(var1);
         }

         public String toString() {
            return "OrElseGet[" + var1 + " " + var2.get() + "]";
         }
      });
   }

   default Codec<A> orElse(final A var1) {
      return this.mapResult(new Codec.ResultFunction<A>() {
         public <T> DataResult<Pair<A, T>> apply(DynamicOps<T> var1x, T var2, DataResult<Pair<A, T>> var3) {
            return DataResult.success(var3.result().orElseGet(() -> {
               return Pair.of(var1, var2);
            }));
         }

         public <T> DataResult<T> coApply(DynamicOps<T> var1x, A var2, DataResult<T> var3) {
            return var3;
         }

         public String toString() {
            return "OrElse[" + var1 + "]";
         }
      });
   }

   default Codec<A> orElseGet(final Supplier<? extends A> var1) {
      return this.mapResult(new Codec.ResultFunction<A>() {
         public <T> DataResult<Pair<A, T>> apply(DynamicOps<T> var1x, T var2, DataResult<Pair<A, T>> var3) {
            return DataResult.success(var3.result().orElseGet(() -> {
               return Pair.of(var1.get(), var2);
            }));
         }

         public <T> DataResult<T> coApply(DynamicOps<T> var1x, A var2, DataResult<T> var3) {
            return var3;
         }

         public String toString() {
            return "OrElseGet[" + var1.get() + "]";
         }
      });
   }

   default Codec<A> promotePartial(Consumer<String> var1) {
      return of((Encoder)this, (Decoder)Decoder.super.promotePartial(var1));
   }

   static <A> Codec<A> unit(A var0) {
      return unit(() -> {
         return var0;
      });
   }

   static <A> Codec<A> unit(Supplier<A> var0) {
      return MapCodec.unit(var0).codec();
   }

   default <E> Codec<E> dispatch(Function<? super E, ? extends A> var1, Function<? super A, ? extends Codec<? extends E>> var2) {
      return this.dispatch("type", var1, var2);
   }

   default <E> Codec<E> dispatch(String var1, Function<? super E, ? extends A> var2, Function<? super A, ? extends Codec<? extends E>> var3) {
      return this.partialDispatch(var1, var2.andThen(DataResult::success), var3.andThen(DataResult::success));
   }

   default <E> Codec<E> dispatchStable(Function<? super E, ? extends A> var1, Function<? super A, ? extends Codec<? extends E>> var2) {
      return this.partialDispatch("type", (var1x) -> {
         return DataResult.success(var1.apply(var1x), Lifecycle.stable());
      }, (var1x) -> {
         return DataResult.success(var2.apply(var1x), Lifecycle.stable());
      });
   }

   default <E> Codec<E> partialDispatch(String var1, Function<? super E, ? extends DataResult<? extends A>> var2, Function<? super A, ? extends DataResult<? extends Codec<? extends E>>> var3) {
      return (new KeyDispatchCodec(var1, this, var2, var3)).codec();
   }

   default <E> MapCodec<E> dispatchMap(Function<? super E, ? extends A> var1, Function<? super A, ? extends Codec<? extends E>> var2) {
      return this.dispatchMap("type", var1, var2);
   }

   default <E> MapCodec<E> dispatchMap(String var1, Function<? super E, ? extends A> var2, Function<? super A, ? extends Codec<? extends E>> var3) {
      return new KeyDispatchCodec(var1, this, var2.andThen(DataResult::success), var3.andThen(DataResult::success));
   }

   static <N extends Number & Comparable<N>> Function<N, DataResult<N>> checkRange(N var0, N var1) {
      return (var2) -> {
         return ((Comparable)var2).compareTo(var0) >= 0 && ((Comparable)var2).compareTo(var1) <= 0 ? DataResult.success(var2) : DataResult.error("Value " + var2 + " outside of range [" + var0 + ":" + var1 + "]", (Object)var2);
      };
   }

   static Codec<Integer> intRange(int var0, int var1) {
      Function var2 = checkRange(var0, var1);
      return INT.flatXmap(var2, var2);
   }

   static Codec<Float> floatRange(float var0, float var1) {
      Function var2 = checkRange(var0, var1);
      return FLOAT.flatXmap(var2, var2);
   }

   static Codec<Double> doubleRange(double var0, double var2) {
      Function var4 = checkRange(var0, var2);
      return DOUBLE.flatXmap(var4, var4);
   }

   public interface ResultFunction<A> {
      <T> DataResult<Pair<A, T>> apply(DynamicOps<T> var1, T var2, DataResult<Pair<A, T>> var3);

      <T> DataResult<T> coApply(DynamicOps<T> var1, A var2, DataResult<T> var3);
   }
}
