package com.mojang.serialization.codecs;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.kinds.K1;
import com.mojang.datafixers.util.Function3;
import com.mojang.datafixers.util.Function4;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapDecoder;
import com.mojang.serialization.MapEncoder;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

public final class RecordCodecBuilder<O, F> implements App<RecordCodecBuilder.Mu<O>, F> {
   private final Function<O, F> getter;
   private final Function<O, MapEncoder<F>> encoder;
   private final MapDecoder<F> decoder;

   public static <O, F> RecordCodecBuilder<O, F> unbox(App<RecordCodecBuilder.Mu<O>, F> var0) {
      return (RecordCodecBuilder)var0;
   }

   private RecordCodecBuilder(Function<O, F> var1, Function<O, MapEncoder<F>> var2, MapDecoder<F> var3) {
      super();
      this.getter = var1;
      this.encoder = var2;
      this.decoder = var3;
   }

   public static <O> RecordCodecBuilder.Instance<O> instance() {
      return new RecordCodecBuilder.Instance();
   }

   public static <O, F> RecordCodecBuilder<O, F> of(Function<O, F> var0, String var1, Codec<F> var2) {
      return of(var0, var2.fieldOf(var1));
   }

   public static <O, F> RecordCodecBuilder<O, F> of(Function<O, F> var0, MapCodec<F> var1) {
      return new RecordCodecBuilder(var0, (var1x) -> {
         return var1;
      }, var1);
   }

   public static <O, F> RecordCodecBuilder<O, F> point(F var0) {
      return new RecordCodecBuilder((var1) -> {
         return var0;
      }, (var0x) -> {
         return Encoder.empty();
      }, Decoder.unit(var0));
   }

   public static <O, F> RecordCodecBuilder<O, F> stable(F var0) {
      return point(var0, Lifecycle.stable());
   }

   public static <O, F> RecordCodecBuilder<O, F> deprecated(F var0, int var1) {
      return point(var0, Lifecycle.deprecated(var1));
   }

   public static <O, F> RecordCodecBuilder<O, F> point(F var0, Lifecycle var1) {
      return new RecordCodecBuilder((var1x) -> {
         return var0;
      }, (var1x) -> {
         return Encoder.empty().withLifecycle(var1);
      }, Decoder.unit(var0).withLifecycle(var1));
   }

   public static <O> Codec<O> create(Function<RecordCodecBuilder.Instance<O>, ? extends App<RecordCodecBuilder.Mu<O>, O>> var0) {
      return build((App)var0.apply(instance())).codec();
   }

   public static <O> MapCodec<O> mapCodec(Function<RecordCodecBuilder.Instance<O>, ? extends App<RecordCodecBuilder.Mu<O>, O>> var0) {
      return build((App)var0.apply(instance()));
   }

   public <E> RecordCodecBuilder<O, E> dependent(Function<O, E> var1, final MapEncoder<E> var2, final Function<? super F, ? extends MapDecoder<E>> var3) {
      return new RecordCodecBuilder(var1, (var1x) -> {
         return var2;
      }, new MapDecoder.Implementation<E>() {
         public <T> DataResult<E> decode(DynamicOps<T> var1, MapLike<T> var2x) {
            return RecordCodecBuilder.this.decoder.decode(var1, var2x).map(var3).flatMap((var2xx) -> {
               return var2xx.decode(var1, var2x).map(Function.identity());
            });
         }

         public <T> Stream<T> keys(DynamicOps<T> var1) {
            return var2.keys(var1);
         }

         public String toString() {
            return "Dependent[" + var2 + "]";
         }
      });
   }

   public static <O> MapCodec<O> build(App<RecordCodecBuilder.Mu<O>, O> var0) {
      final RecordCodecBuilder var1 = unbox(var0);
      return new MapCodec<O>() {
         public <T> DataResult<O> decode(DynamicOps<T> var1x, MapLike<T> var2) {
            return var1.decoder.decode(var1x, var2);
         }

         public <T> RecordBuilder<T> encode(O var1x, DynamicOps<T> var2, RecordBuilder<T> var3) {
            return ((MapEncoder)var1.encoder.apply(var1x)).encode(var1x, var2, var3);
         }

         public <T> Stream<T> keys(DynamicOps<T> var1x) {
            return var1.decoder.keys(var1x);
         }

         public String toString() {
            return "RecordCodec[" + var1.decoder + "]";
         }
      };
   }

   // $FF: synthetic method
   RecordCodecBuilder(Function var1, Function var2, MapDecoder var3, Object var4) {
      this(var1, var2, var3);
   }

   public static final class Instance<O> implements Applicative<RecordCodecBuilder.Mu<O>, RecordCodecBuilder.Instance.Mu<O>> {
      public Instance() {
         super();
      }

      public <A> App<RecordCodecBuilder.Mu<O>, A> stable(A var1) {
         return RecordCodecBuilder.stable(var1);
      }

      public <A> App<RecordCodecBuilder.Mu<O>, A> deprecated(A var1, int var2) {
         return RecordCodecBuilder.deprecated(var1, var2);
      }

      public <A> App<RecordCodecBuilder.Mu<O>, A> point(A var1, Lifecycle var2) {
         return RecordCodecBuilder.point(var1, var2);
      }

      public <A> App<RecordCodecBuilder.Mu<O>, A> point(A var1) {
         return RecordCodecBuilder.point(var1);
      }

      public <A, R> Function<App<RecordCodecBuilder.Mu<O>, A>, App<RecordCodecBuilder.Mu<O>, R>> lift1(App<RecordCodecBuilder.Mu<O>, Function<A, R>> var1) {
         return (var2) -> {
            final RecordCodecBuilder var3 = RecordCodecBuilder.unbox(var1);
            final RecordCodecBuilder var4 = RecordCodecBuilder.unbox(var2);
            return new RecordCodecBuilder((var2x) -> {
               return ((Function)var3.getter.apply(var2x)).apply(var4.getter.apply(var2x));
            }, (var3x) -> {
               final MapEncoder var4x = (MapEncoder)var3.encoder.apply(var3x);
               final MapEncoder var5 = (MapEncoder)var4.encoder.apply(var3x);
               final Object var6 = var4.getter.apply(var3x);
               return new MapEncoder.Implementation<R>() {
                  public <T> RecordBuilder<T> encode(R var1, DynamicOps<T> var2, RecordBuilder<T> var3) {
                     var5.encode(var6, var2, var3);
                     var4.encode((var1x) -> {
                        return var1;
                     }, var2, var3);
                     return var3;
                  }

                  public <T> Stream<T> keys(DynamicOps<T> var1) {
                     return Stream.concat(var5.keys(var1), var4.keys(var1));
                  }

                  public String toString() {
                     return var4 + " * " + var5;
                  }
               };
            }, new MapDecoder.Implementation<R>() {
               public <T> DataResult<R> decode(DynamicOps<T> var1, MapLike<T> var2) {
                  return var4.decoder.decode(var1, var2).flatMap((var3x) -> {
                     return var3.decoder.decode(var1, var2).map((var1x) -> {
                        return var1x.apply(var3x);
                     });
                  });
               }

               public <T> Stream<T> keys(DynamicOps<T> var1) {
                  return Stream.concat(var4.decoder.keys(var1), var3.decoder.keys(var1));
               }

               public String toString() {
                  return var3.decoder + " * " + var4.decoder;
               }
            });
         };
      }

      public <A, B, R> App<RecordCodecBuilder.Mu<O>, R> ap2(App<RecordCodecBuilder.Mu<O>, BiFunction<A, B, R>> var1, App<RecordCodecBuilder.Mu<O>, A> var2, App<RecordCodecBuilder.Mu<O>, B> var3) {
         final RecordCodecBuilder var4 = RecordCodecBuilder.unbox(var1);
         final RecordCodecBuilder var5 = RecordCodecBuilder.unbox(var2);
         final RecordCodecBuilder var6 = RecordCodecBuilder.unbox(var3);
         return new RecordCodecBuilder((var3x) -> {
            return ((BiFunction)var4.getter.apply(var3x)).apply(var5.getter.apply(var3x), var6.getter.apply(var3x));
         }, (var4x) -> {
            final MapEncoder var5x = (MapEncoder)var4.encoder.apply(var4x);
            final MapEncoder var6x = (MapEncoder)var5.encoder.apply(var4x);
            final Object var7 = var5.getter.apply(var4x);
            final MapEncoder var8 = (MapEncoder)var6.encoder.apply(var4x);
            final Object var9 = var6.getter.apply(var4x);
            return new MapEncoder.Implementation<R>() {
               public <T> RecordBuilder<T> encode(R var1, DynamicOps<T> var2, RecordBuilder<T> var3) {
                  var6.encode(var7, var2, var3);
                  var8.encode(var9, var2, var3);
                  var5.encode((var1x, var2x) -> {
                     return var1;
                  }, var2, var3);
                  return var3;
               }

               public <T> Stream<T> keys(DynamicOps<T> var1) {
                  return Stream.of(var5.keys(var1), var6.keys(var1), var8.keys(var1)).flatMap(Function.identity());
               }

               public String toString() {
                  return var5 + " * " + var6 + " * " + var8;
               }
            };
         }, new MapDecoder.Implementation<R>() {
            public <T> DataResult<R> decode(DynamicOps<T> var1, MapLike<T> var2) {
               return DataResult.unbox(DataResult.instance().ap2(var4.decoder.decode(var1, var2), var5.decoder.decode(var1, var2), var6.decoder.decode(var1, var2)));
            }

            public <T> Stream<T> keys(DynamicOps<T> var1) {
               return Stream.of(var4.decoder.keys(var1), var5.decoder.keys(var1), var6.decoder.keys(var1)).flatMap(Function.identity());
            }

            public String toString() {
               return var4.decoder + " * " + var5.decoder + " * " + var6.decoder;
            }
         });
      }

      public <T1, T2, T3, R> App<RecordCodecBuilder.Mu<O>, R> ap3(App<RecordCodecBuilder.Mu<O>, Function3<T1, T2, T3, R>> var1, App<RecordCodecBuilder.Mu<O>, T1> var2, App<RecordCodecBuilder.Mu<O>, T2> var3, App<RecordCodecBuilder.Mu<O>, T3> var4) {
         final RecordCodecBuilder var5 = RecordCodecBuilder.unbox(var1);
         final RecordCodecBuilder var6 = RecordCodecBuilder.unbox(var2);
         final RecordCodecBuilder var7 = RecordCodecBuilder.unbox(var3);
         final RecordCodecBuilder var8 = RecordCodecBuilder.unbox(var4);
         return new RecordCodecBuilder((var4x) -> {
            return ((Function3)var5.getter.apply(var4x)).apply(var6.getter.apply(var4x), var7.getter.apply(var4x), var8.getter.apply(var4x));
         }, (var5x) -> {
            final MapEncoder var6x = (MapEncoder)var5.encoder.apply(var5x);
            final MapEncoder var7x = (MapEncoder)var6.encoder.apply(var5x);
            final Object var8x = var6.getter.apply(var5x);
            final MapEncoder var9 = (MapEncoder)var7.encoder.apply(var5x);
            final Object var10 = var7.getter.apply(var5x);
            final MapEncoder var11 = (MapEncoder)var8.encoder.apply(var5x);
            final Object var12 = var8.getter.apply(var5x);
            return new MapEncoder.Implementation<R>() {
               public <T> RecordBuilder<T> encode(R var1, DynamicOps<T> var2, RecordBuilder<T> var3) {
                  var7.encode(var8, var2, var3);
                  var9.encode(var10, var2, var3);
                  var11.encode(var12, var2, var3);
                  var6.encode((var1x, var2x, var3x) -> {
                     return var1;
                  }, var2, var3);
                  return var3;
               }

               public <T> Stream<T> keys(DynamicOps<T> var1) {
                  return Stream.of(var6.keys(var1), var7.keys(var1), var9.keys(var1), var11.keys(var1)).flatMap(Function.identity());
               }

               public String toString() {
                  return var6 + " * " + var7 + " * " + var9 + " * " + var11;
               }
            };
         }, new MapDecoder.Implementation<R>() {
            public <T> DataResult<R> decode(DynamicOps<T> var1, MapLike<T> var2) {
               return DataResult.unbox(DataResult.instance().ap3(var5.decoder.decode(var1, var2), var6.decoder.decode(var1, var2), var7.decoder.decode(var1, var2), var8.decoder.decode(var1, var2)));
            }

            public <T> Stream<T> keys(DynamicOps<T> var1) {
               return Stream.of(var5.decoder.keys(var1), var6.decoder.keys(var1), var7.decoder.keys(var1), var8.decoder.keys(var1)).flatMap(Function.identity());
            }

            public String toString() {
               return var5.decoder + " * " + var6.decoder + " * " + var7.decoder + " * " + var8.decoder;
            }
         });
      }

      public <T1, T2, T3, T4, R> App<RecordCodecBuilder.Mu<O>, R> ap4(App<RecordCodecBuilder.Mu<O>, Function4<T1, T2, T3, T4, R>> var1, App<RecordCodecBuilder.Mu<O>, T1> var2, App<RecordCodecBuilder.Mu<O>, T2> var3, App<RecordCodecBuilder.Mu<O>, T3> var4, App<RecordCodecBuilder.Mu<O>, T4> var5) {
         final RecordCodecBuilder var6 = RecordCodecBuilder.unbox(var1);
         final RecordCodecBuilder var7 = RecordCodecBuilder.unbox(var2);
         final RecordCodecBuilder var8 = RecordCodecBuilder.unbox(var3);
         final RecordCodecBuilder var9 = RecordCodecBuilder.unbox(var4);
         final RecordCodecBuilder var10 = RecordCodecBuilder.unbox(var5);
         return new RecordCodecBuilder((var5x) -> {
            return ((Function4)var6.getter.apply(var5x)).apply(var7.getter.apply(var5x), var8.getter.apply(var5x), var9.getter.apply(var5x), var10.getter.apply(var5x));
         }, (var6x) -> {
            final MapEncoder var7x = (MapEncoder)var6.encoder.apply(var6x);
            final MapEncoder var8x = (MapEncoder)var7.encoder.apply(var6x);
            final Object var9x = var7.getter.apply(var6x);
            final MapEncoder var10x = (MapEncoder)var8.encoder.apply(var6x);
            final Object var11 = var8.getter.apply(var6x);
            final MapEncoder var12 = (MapEncoder)var9.encoder.apply(var6x);
            final Object var13 = var9.getter.apply(var6x);
            final MapEncoder var14 = (MapEncoder)var10.encoder.apply(var6x);
            final Object var15 = var10.getter.apply(var6x);
            return new MapEncoder.Implementation<R>() {
               public <T> RecordBuilder<T> encode(R var1, DynamicOps<T> var2, RecordBuilder<T> var3) {
                  var8.encode(var9, var2, var3);
                  var10.encode(var11, var2, var3);
                  var12.encode(var13, var2, var3);
                  var14.encode(var15, var2, var3);
                  var7.encode((var1x, var2x, var3x, var4) -> {
                     return var1;
                  }, var2, var3);
                  return var3;
               }

               public <T> Stream<T> keys(DynamicOps<T> var1) {
                  return Stream.of(var7.keys(var1), var8.keys(var1), var10.keys(var1), var12.keys(var1), var14.keys(var1)).flatMap(Function.identity());
               }

               public String toString() {
                  return var7 + " * " + var8 + " * " + var10 + " * " + var12 + " * " + var14;
               }
            };
         }, new MapDecoder.Implementation<R>() {
            public <T> DataResult<R> decode(DynamicOps<T> var1, MapLike<T> var2) {
               return DataResult.unbox(DataResult.instance().ap4(var6.decoder.decode(var1, var2), var7.decoder.decode(var1, var2), var8.decoder.decode(var1, var2), var9.decoder.decode(var1, var2), var10.decoder.decode(var1, var2)));
            }

            public <T> Stream<T> keys(DynamicOps<T> var1) {
               return Stream.of(var6.decoder.keys(var1), var7.decoder.keys(var1), var8.decoder.keys(var1), var9.decoder.keys(var1), var10.decoder.keys(var1)).flatMap(Function.identity());
            }

            public String toString() {
               return var6.decoder + " * " + var7.decoder + " * " + var8.decoder + " * " + var9.decoder + " * " + var10.decoder;
            }
         });
      }

      public <T, R> App<RecordCodecBuilder.Mu<O>, R> map(Function<? super T, ? extends R> var1, App<RecordCodecBuilder.Mu<O>, T> var2) {
         RecordCodecBuilder var3 = RecordCodecBuilder.unbox(var2);
         Function var4 = var3.getter;
         return new RecordCodecBuilder(var4.andThen(var1), (var3x) -> {
            return new MapEncoder.Implementation<R>() {
               private final MapEncoder encoder;

               {
                  this.encoder = (MapEncoder)var1.encoder.apply(var3);
               }

               public <U> RecordBuilder<U> encode(R var1x, DynamicOps<U> var2x, RecordBuilder<U> var3x) {
                  return this.encoder.encode(var2.apply(var3), var2x, var3x);
               }

               public <U> Stream<U> keys(DynamicOps<U> var1x) {
                  return this.encoder.keys(var1x);
               }

               public String toString() {
                  return this.encoder + "[mapped]";
               }
            };
         }, var3.decoder.map(var1));
      }

      private static final class Mu<O> implements Applicative.Mu {
         private Mu() {
            super();
         }
      }
   }

   public static final class Mu<O> implements K1 {
      public Mu() {
         super();
      }
   }
}
