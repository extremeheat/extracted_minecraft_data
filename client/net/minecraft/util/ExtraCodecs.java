package net.minecraft.util;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.Codec.ResultFunction;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import net.minecraft.Util;
import org.apache.commons.lang3.mutable.MutableObject;

public class ExtraCodecs {
   public static final Codec<Integer> NON_NEGATIVE_INT = intRangeWithMessage(0, 2147483647, (var0) -> {
      return "Value must be non-negative: " + var0;
   });
   public static final Codec<Integer> POSITIVE_INT = intRangeWithMessage(1, 2147483647, (var0) -> {
      return "Value must be positive: " + var0;
   });
   public static final Codec<Float> POSITIVE_FLOAT = floatRangeMinExclusiveWithMessage(0.0F, 3.4028235E38F, (var0) -> {
      return "Value must be positive: " + var0;
   });

   public ExtraCodecs() {
      super();
   }

   public static <F, S> Codec<Either<F, S>> xor(Codec<F> var0, Codec<S> var1) {
      return new ExtraCodecs.XorCodec(var0, var1);
   }

   public static <P, I> Codec<I> intervalCodec(Codec<P> var0, String var1, String var2, BiFunction<P, P, DataResult<I>> var3, Function<I, P> var4, Function<I, P> var5) {
      Codec var6 = Codec.list(var0).comapFlatMap((var1x) -> {
         return Util.fixedSize((List)var1x, 2).flatMap((var1) -> {
            Object var2 = var1.get(0);
            Object var3x = var1.get(1);
            return (DataResult)var3.apply(var2, var3x);
         });
      }, (var2x) -> {
         return ImmutableList.of(var4.apply(var2x), var5.apply(var2x));
      });
      Codec var7 = RecordCodecBuilder.create((var3x) -> {
         return var3x.group(var0.fieldOf(var1).forGetter(Pair::getFirst), var0.fieldOf(var2).forGetter(Pair::getSecond)).apply(var3x, Pair::of);
      }).comapFlatMap((var1x) -> {
         return (DataResult)var3.apply(var1x.getFirst(), var1x.getSecond());
      }, (var2x) -> {
         return Pair.of(var4.apply(var2x), var5.apply(var2x));
      });
      Codec var8 = (new ExtraCodecs.EitherCodec(var6, var7)).xmap((var0x) -> {
         return var0x.map((var0) -> {
            return var0;
         }, (var0) -> {
            return var0;
         });
      }, Either::left);
      return Codec.either(var0, var8).comapFlatMap((var1x) -> {
         return (DataResult)var1x.map((var1) -> {
            return (DataResult)var3.apply(var1, var1);
         }, DataResult::success);
      }, (var2x) -> {
         Object var3 = var4.apply(var2x);
         Object var4x = var5.apply(var2x);
         return Objects.equals(var3, var4x) ? Either.left(var3) : Either.right(var2x);
      });
   }

   public static <A> ResultFunction<A> orElsePartial(final A var0) {
      return new ResultFunction<A>() {
         public <T> DataResult<Pair<A, T>> apply(DynamicOps<T> var1, T var2, DataResult<Pair<A, T>> var3) {
            MutableObject var4 = new MutableObject();
            Objects.requireNonNull(var4);
            Optional var5 = var3.resultOrPartial(var4::setValue);
            return var5.isPresent() ? var3 : DataResult.error("(" + (String)var4.getValue() + " -> using default)", Pair.of(var0, var2));
         }

         public <T> DataResult<T> coApply(DynamicOps<T> var1, A var2, DataResult<T> var3) {
            return var3;
         }

         public String toString() {
            return "OrElsePartial[" + var0 + "]";
         }
      };
   }

   public static <E> Codec<E> idResolverCodec(ToIntFunction<E> var0, IntFunction<E> var1, int var2) {
      return Codec.INT.flatXmap((var1x) -> {
         return (DataResult)Optional.ofNullable(var1.apply(var1x)).map(DataResult::success).orElseGet(() -> {
            return DataResult.error("Unknown element id: " + var1x);
         });
      }, (var2x) -> {
         int var3 = var0.applyAsInt(var2x);
         return var3 == var2 ? DataResult.error("Element with unknown id: " + var2x) : DataResult.success(var3);
      });
   }

   public static <E> Codec<E> stringResolverCodec(Function<E, String> var0, Function<String, E> var1) {
      return Codec.STRING.flatXmap((var1x) -> {
         return (DataResult)Optional.ofNullable(var1.apply(var1x)).map(DataResult::success).orElseGet(() -> {
            return DataResult.error("Unknown element name:" + var1x);
         });
      }, (var1x) -> {
         return (DataResult)Optional.ofNullable((String)var0.apply(var1x)).map(DataResult::success).orElseGet(() -> {
            return DataResult.error("Element with unknown name: " + var1x);
         });
      });
   }

   public static <E> Codec<E> orCompressed(final Codec<E> var0, final Codec<E> var1) {
      return new Codec<E>() {
         public <T> DataResult<T> encode(E var1x, DynamicOps<T> var2, T var3) {
            return var2.compressMaps() ? var1.encode(var1x, var2, var3) : var0.encode(var1x, var2, var3);
         }

         public <T> DataResult<Pair<E, T>> decode(DynamicOps<T> var1x, T var2) {
            return var1x.compressMaps() ? var1.decode(var1x, var2) : var0.decode(var1x, var2);
         }

         public String toString() {
            return var0 + " orCompressed " + var1;
         }
      };
   }

   public static <E> Codec<E> overrideLifecycle(Codec<E> var0, final Function<E, Lifecycle> var1, final Function<E, Lifecycle> var2) {
      return var0.mapResult(new ResultFunction<E>() {
         public <T> DataResult<Pair<E, T>> apply(DynamicOps<T> var1x, T var2x, DataResult<Pair<E, T>> var3) {
            return (DataResult)var3.result().map((var2xx) -> {
               return var3.setLifecycle((Lifecycle)var1.apply(var2xx.getFirst()));
            }).orElse(var3);
         }

         public <T> DataResult<T> coApply(DynamicOps<T> var1x, E var2x, DataResult<T> var3) {
            return var3.setLifecycle((Lifecycle)var2.apply(var2x));
         }

         public String toString() {
            return "WithLifecycle[" + var1 + " " + var2 + "]";
         }
      });
   }

   private static <N extends Number & Comparable<N>> Function<N, DataResult<N>> checkRangeWithMessage(N var0, N var1, Function<N, String> var2) {
      return (var3) -> {
         return ((Comparable)var3).compareTo(var0) >= 0 && ((Comparable)var3).compareTo(var1) <= 0 ? DataResult.success(var3) : DataResult.error((String)var2.apply(var3));
      };
   }

   private static Codec<Integer> intRangeWithMessage(int var0, int var1, Function<Integer, String> var2) {
      Function var3 = checkRangeWithMessage(var0, var1, var2);
      return Codec.INT.flatXmap(var3, var3);
   }

   private static <N extends Number & Comparable<N>> Function<N, DataResult<N>> checkRangeMinExclusiveWithMessage(N var0, N var1, Function<N, String> var2) {
      return (var3) -> {
         return ((Comparable)var3).compareTo(var0) > 0 && ((Comparable)var3).compareTo(var1) <= 0 ? DataResult.success(var3) : DataResult.error((String)var2.apply(var3));
      };
   }

   private static Codec<Float> floatRangeMinExclusiveWithMessage(float var0, float var1, Function<Float, String> var2) {
      Function var3 = checkRangeMinExclusiveWithMessage(var0, var1, var2);
      return Codec.FLOAT.flatXmap(var3, var3);
   }

   public static <T> Function<List<T>, DataResult<List<T>>> nonEmptyListCheck() {
      return (var0) -> {
         return var0.isEmpty() ? DataResult.error("List must have contents") : DataResult.success(var0);
      };
   }

   public static <T> Codec<List<T>> nonEmptyList(Codec<List<T>> var0) {
      return var0.flatXmap(nonEmptyListCheck(), nonEmptyListCheck());
   }

   public static <T> Function<List<Supplier<T>>, DataResult<List<Supplier<T>>>> nonNullSupplierListCheck() {
      return (var0) -> {
         ArrayList var1 = Lists.newArrayList();

         for(int var2 = 0; var2 < var0.size(); ++var2) {
            Supplier var3 = (Supplier)var0.get(var2);

            try {
               if (var3.get() == null) {
                  var1.add("Missing value [" + var2 + "] : " + var3);
               }
            } catch (Exception var5) {
               var1.add("Invalid value [" + var2 + "]: " + var3 + ", message: " + var5.getMessage());
            }
         }

         return !var1.isEmpty() ? DataResult.error(String.join("; ", var1)) : DataResult.success(var0, Lifecycle.stable());
      };
   }

   public static <T> Function<Supplier<T>, DataResult<Supplier<T>>> nonNullSupplierCheck() {
      return (var0) -> {
         try {
            if (var0.get() == null) {
               return DataResult.error("Missing value: " + var0);
            }
         } catch (Exception var2) {
            return DataResult.error("Invalid value: " + var0 + ", message: " + var2.getMessage());
         }

         return DataResult.success(var0, Lifecycle.stable());
      };
   }

   public static <A> Codec<A> lazyInitializedCodec(Supplier<Codec<A>> var0) {
      return new ExtraCodecs.LazyInitializedCodec(var0);
   }

   private static final class XorCodec<F, S> implements Codec<Either<F, S>> {
      private final Codec<F> first;
      private final Codec<S> second;

      public XorCodec(Codec<F> var1, Codec<S> var2) {
         super();
         this.first = var1;
         this.second = var2;
      }

      public <T> DataResult<Pair<Either<F, S>, T>> decode(DynamicOps<T> var1, T var2) {
         DataResult var3 = this.first.decode(var1, var2).map((var0) -> {
            return var0.mapFirst(Either::left);
         });
         DataResult var4 = this.second.decode(var1, var2).map((var0) -> {
            return var0.mapFirst(Either::right);
         });
         Optional var5 = var3.result();
         Optional var6 = var4.result();
         if (var5.isPresent() && var6.isPresent()) {
            return DataResult.error("Both alternatives read successfully, can not pick the correct one; first: " + var5.get() + " second: " + var6.get(), (Pair)var5.get());
         } else {
            return var5.isPresent() ? var3 : var4;
         }
      }

      public <T> DataResult<T> encode(Either<F, S> var1, DynamicOps<T> var2, T var3) {
         return (DataResult)var1.map((var3x) -> {
            return this.first.encode(var3x, var2, var3);
         }, (var3x) -> {
            return this.second.encode(var3x, var2, var3);
         });
      }

      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else if (var1 != null && this.getClass() == var1.getClass()) {
            ExtraCodecs.XorCodec var2 = (ExtraCodecs.XorCodec)var1;
            return Objects.equals(this.first, var2.first) && Objects.equals(this.second, var2.second);
         } else {
            return false;
         }
      }

      public int hashCode() {
         return Objects.hash(new Object[]{this.first, this.second});
      }

      public String toString() {
         return "XorCodec[" + this.first + ", " + this.second + "]";
      }

      // $FF: synthetic method
      public DataResult encode(Object var1, DynamicOps var2, Object var3) {
         return this.encode((Either)var1, var2, var3);
      }
   }

   static final class EitherCodec<F, S> implements Codec<Either<F, S>> {
      private final Codec<F> first;
      private final Codec<S> second;

      public EitherCodec(Codec<F> var1, Codec<S> var2) {
         super();
         this.first = var1;
         this.second = var2;
      }

      public <T> DataResult<Pair<Either<F, S>, T>> decode(DynamicOps<T> var1, T var2) {
         DataResult var3 = this.first.decode(var1, var2).map((var0) -> {
            return var0.mapFirst(Either::left);
         });
         if (!var3.error().isPresent()) {
            return var3;
         } else {
            DataResult var4 = this.second.decode(var1, var2).map((var0) -> {
               return var0.mapFirst(Either::right);
            });
            return !var4.error().isPresent() ? var4 : var3.apply2((var0, var1x) -> {
               return var1x;
            }, var4);
         }
      }

      public <T> DataResult<T> encode(Either<F, S> var1, DynamicOps<T> var2, T var3) {
         return (DataResult)var1.map((var3x) -> {
            return this.first.encode(var3x, var2, var3);
         }, (var3x) -> {
            return this.second.encode(var3x, var2, var3);
         });
      }

      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else if (var1 != null && this.getClass() == var1.getClass()) {
            ExtraCodecs.EitherCodec var2 = (ExtraCodecs.EitherCodec)var1;
            return Objects.equals(this.first, var2.first) && Objects.equals(this.second, var2.second);
         } else {
            return false;
         }
      }

      public int hashCode() {
         return Objects.hash(new Object[]{this.first, this.second});
      }

      public String toString() {
         return "EitherCodec[" + this.first + ", " + this.second + "]";
      }

      // $FF: synthetic method
      public DataResult encode(Object var1, DynamicOps var2, Object var3) {
         return this.encode((Either)var1, var2, var3);
      }
   }

   static record LazyInitializedCodec<A>(Supplier<Codec<A>> a) implements Codec<A> {
      private final Supplier<Codec<A>> delegate;

      LazyInitializedCodec(Supplier<Codec<A>> var1) {
         super();
         Objects.requireNonNull(var1);
         com.google.common.base.Supplier var2 = Suppliers.memoize(var1::get);
         this.delegate = var2;
      }

      public <T> DataResult<Pair<A, T>> decode(DynamicOps<T> var1, T var2) {
         return ((Codec)this.delegate.get()).decode(var1, var2);
      }

      public <T> DataResult<T> encode(A var1, DynamicOps<T> var2, T var3) {
         return ((Codec)this.delegate.get()).encode(var1, var2, var3);
      }

      public Supplier<Codec<A>> delegate() {
         return this.delegate;
      }
   }
}
