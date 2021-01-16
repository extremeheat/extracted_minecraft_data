package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.math.LongMath;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Iterator;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.Spliterator.OfDouble;
import java.util.Spliterator.OfInt;
import java.util.Spliterator.OfLong;
import java.util.Spliterators.AbstractSpliterator;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.IntConsumer;
import java.util.function.LongConsumer;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;

@Beta
@GwtCompatible
public final class Streams {
   public static <T> Stream<T> stream(Iterable<T> var0) {
      return var0 instanceof Collection ? ((Collection)var0).stream() : StreamSupport.stream(var0.spliterator(), false);
   }

   /** @deprecated */
   @Deprecated
   public static <T> Stream<T> stream(Collection<T> var0) {
      return var0.stream();
   }

   public static <T> Stream<T> stream(Iterator<T> var0) {
      return StreamSupport.stream(Spliterators.spliteratorUnknownSize(var0, 0), false);
   }

   public static <T> Stream<T> stream(Optional<T> var0) {
      return var0.isPresent() ? Stream.of(var0.get()) : Stream.of();
   }

   public static <T> Stream<T> stream(java.util.Optional<T> var0) {
      return var0.isPresent() ? Stream.of(var0.get()) : Stream.of();
   }

   @SafeVarargs
   public static <T> Stream<T> concat(Stream<? extends T>... var0) {
      boolean var1 = false;
      int var2 = 336;
      long var3 = 0L;
      ImmutableList.Builder var5 = new ImmutableList.Builder(var0.length);
      Stream[] var6 = var0;
      int var7 = var0.length;

      for(int var8 = 0; var8 < var7; ++var8) {
         Stream var9 = var6[var8];
         var1 |= var9.isParallel();
         Spliterator var10 = var9.spliterator();
         var5.add((Object)var10);
         var2 &= var10.characteristics();
         var3 = LongMath.saturatedAdd(var3, var10.estimateSize());
      }

      return StreamSupport.stream(CollectSpliterators.flatMap(var5.build().spliterator(), (var0x) -> {
         return var0x;
      }, var2, var3), var1);
   }

   public static IntStream concat(IntStream... var0) {
      return Stream.of(var0).flatMapToInt((var0x) -> {
         return var0x;
      });
   }

   public static LongStream concat(LongStream... var0) {
      return Stream.of(var0).flatMapToLong((var0x) -> {
         return var0x;
      });
   }

   public static DoubleStream concat(DoubleStream... var0) {
      return Stream.of(var0).flatMapToDouble((var0x) -> {
         return var0x;
      });
   }

   public static IntStream stream(OptionalInt var0) {
      return var0.isPresent() ? IntStream.of(var0.getAsInt()) : IntStream.empty();
   }

   public static LongStream stream(OptionalLong var0) {
      return var0.isPresent() ? LongStream.of(var0.getAsLong()) : LongStream.empty();
   }

   public static DoubleStream stream(OptionalDouble var0) {
      return var0.isPresent() ? DoubleStream.of(var0.getAsDouble()) : DoubleStream.empty();
   }

   public static <T> java.util.Optional<T> findLast(Stream<T> var0) {
      class 1OptionalState<T> {
         boolean set = false;
         T value = null;

         _OptionalState/* $FF was: 1OptionalState*/() {
            super();
         }

         void set(@Nullable T var1) {
            this.set = true;
            this.value = var1;
         }

         T get() {
            Preconditions.checkState(this.set);
            return this.value;
         }
      }

      1OptionalState var1 = new 1OptionalState();
      ArrayDeque var2 = new ArrayDeque();
      var2.addLast(var0.spliterator());

      while(true) {
         while(true) {
            Spliterator var3;
            do {
               if (var2.isEmpty()) {
                  return java.util.Optional.empty();
               }

               var3 = (Spliterator)var2.removeLast();
            } while(var3.getExactSizeIfKnown() == 0L);

            Spliterator var4;
            if (var3.hasCharacteristics(16384)) {
               while(true) {
                  var4 = var3.trySplit();
                  if (var4 == null || var4.getExactSizeIfKnown() == 0L) {
                     break;
                  }

                  if (var3.getExactSizeIfKnown() == 0L) {
                     var3 = var4;
                     break;
                  }
               }

               var3.forEachRemaining(var1::set);
               return java.util.Optional.of(var1.get());
            }

            var4 = var3.trySplit();
            if (var4 != null && var4.getExactSizeIfKnown() != 0L) {
               var2.addLast(var4);
               var2.addLast(var3);
            } else {
               var3.forEachRemaining(var1::set);
               if (var1.set) {
                  return java.util.Optional.of(var1.get());
               }
            }
         }
      }
   }

   public static OptionalInt findLast(IntStream var0) {
      java.util.Optional var1 = findLast(var0.boxed());
      return var1.isPresent() ? OptionalInt.of((Integer)var1.get()) : OptionalInt.empty();
   }

   public static OptionalLong findLast(LongStream var0) {
      java.util.Optional var1 = findLast(var0.boxed());
      return var1.isPresent() ? OptionalLong.of((Long)var1.get()) : OptionalLong.empty();
   }

   public static OptionalDouble findLast(DoubleStream var0) {
      java.util.Optional var1 = findLast(var0.boxed());
      return var1.isPresent() ? OptionalDouble.of((Double)var1.get()) : OptionalDouble.empty();
   }

   public static <A, B, R> Stream<R> zip(Stream<A> var0, Stream<B> var1, final BiFunction<? super A, ? super B, R> var2) {
      Preconditions.checkNotNull(var0);
      Preconditions.checkNotNull(var1);
      Preconditions.checkNotNull(var2);
      boolean var3 = var0.isParallel() || var1.isParallel();
      Spliterator var4 = var0.spliterator();
      Spliterator var5 = var1.spliterator();
      int var6 = var4.characteristics() & var5.characteristics() & 80;
      final Iterator var7 = Spliterators.iterator(var4);
      final Iterator var8 = Spliterators.iterator(var5);
      return StreamSupport.stream(new AbstractSpliterator<R>(Math.min(var4.estimateSize(), var5.estimateSize()), var6) {
         public boolean tryAdvance(Consumer<? super R> var1) {
            if (var7.hasNext() && var8.hasNext()) {
               var1.accept(var2.apply(var7.next(), var8.next()));
               return true;
            } else {
               return false;
            }
         }
      }, var3);
   }

   public static <T, R> Stream<R> mapWithIndex(Stream<T> var0, final Streams.FunctionWithIndex<? super T, ? extends R> var1) {
      Preconditions.checkNotNull(var0);
      Preconditions.checkNotNull(var1);
      boolean var2 = var0.isParallel();
      Spliterator var3 = var0.spliterator();
      if (!var3.hasCharacteristics(16384)) {
         final Iterator var4 = Spliterators.iterator(var3);
         return StreamSupport.stream(new AbstractSpliterator<R>(var3.estimateSize(), var3.characteristics() & 80) {
            long index = 0L;

            public boolean tryAdvance(Consumer<? super R> var1x) {
               if (var4.hasNext()) {
                  var1x.accept(var1.apply(var4.next(), (long)(this.index++)));
                  return true;
               } else {
                  return false;
               }
            }
         }, var2);
      } else {
         class 1Splitr extends Streams.MapWithIndexSpliterator<Spliterator<T>, R, 1Splitr> implements Consumer<T> {
            T holder;

            _Splitr/* $FF was: 1Splitr*/(Spliterator<T> var1x, long var2) {
               super(var1x, var2);
            }

            public void accept(@Nullable T var1x) {
               this.holder = var1x;
            }

            public boolean tryAdvance(Consumer<? super R> var1x) {
               if (this.fromSpliterator.tryAdvance(this)) {
                  boolean var2;
                  try {
                     var1x.accept(var1.apply(this.holder, (long)(this.index++)));
                     var2 = true;
                  } finally {
                     this.holder = null;
                  }

                  return var2;
               } else {
                  return false;
               }
            }

            1Splitr createSplit(Spliterator<T> var1x, long var2) {
               return new 1Splitr(var1x, var2);
            }
         }

         return StreamSupport.stream(new 1Splitr(var3, 0L), var2);
      }
   }

   public static <R> Stream<R> mapWithIndex(IntStream var0, final Streams.IntFunctionWithIndex<R> var1) {
      Preconditions.checkNotNull(var0);
      Preconditions.checkNotNull(var1);
      boolean var2 = var0.isParallel();
      OfInt var3 = var0.spliterator();
      if (!var3.hasCharacteristics(16384)) {
         final java.util.PrimitiveIterator.OfInt var4 = Spliterators.iterator(var3);
         return StreamSupport.stream(new AbstractSpliterator<R>(var3.estimateSize(), var3.characteristics() & 80) {
            long index = 0L;

            public boolean tryAdvance(Consumer<? super R> var1x) {
               if (var4.hasNext()) {
                  var1x.accept(var1.apply(var4.nextInt(), (long)(this.index++)));
                  return true;
               } else {
                  return false;
               }
            }
         }, var2);
      } else {
         class 2Splitr extends Streams.MapWithIndexSpliterator<OfInt, R, 2Splitr> implements IntConsumer, Spliterator<R> {
            int holder;

            _Splitr/* $FF was: 2Splitr*/(OfInt var1x, long var2) {
               super(var1x, var2);
            }

            public void accept(int var1x) {
               this.holder = var1x;
            }

            public boolean tryAdvance(Consumer<? super R> var1x) {
               if (((OfInt)this.fromSpliterator).tryAdvance(this)) {
                  var1x.accept(var1.apply(this.holder, (long)(this.index++)));
                  return true;
               } else {
                  return false;
               }
            }

            2Splitr createSplit(OfInt var1x, long var2) {
               return new 2Splitr(var1x, var2);
            }
         }

         return StreamSupport.stream(new 2Splitr(var3, 0L), var2);
      }
   }

   public static <R> Stream<R> mapWithIndex(LongStream var0, final Streams.LongFunctionWithIndex<R> var1) {
      Preconditions.checkNotNull(var0);
      Preconditions.checkNotNull(var1);
      boolean var2 = var0.isParallel();
      OfLong var3 = var0.spliterator();
      if (!var3.hasCharacteristics(16384)) {
         final java.util.PrimitiveIterator.OfLong var4 = Spliterators.iterator(var3);
         return StreamSupport.stream(new AbstractSpliterator<R>(var3.estimateSize(), var3.characteristics() & 80) {
            long index = 0L;

            public boolean tryAdvance(Consumer<? super R> var1x) {
               if (var4.hasNext()) {
                  var1x.accept(var1.apply(var4.nextLong(), (long)(this.index++)));
                  return true;
               } else {
                  return false;
               }
            }
         }, var2);
      } else {
         class 3Splitr extends Streams.MapWithIndexSpliterator<OfLong, R, 3Splitr> implements LongConsumer, Spliterator<R> {
            long holder;

            _Splitr/* $FF was: 3Splitr*/(OfLong var1x, long var2) {
               super(var1x, var2);
            }

            public void accept(long var1x) {
               this.holder = var1x;
            }

            public boolean tryAdvance(Consumer<? super R> var1x) {
               if (((OfLong)this.fromSpliterator).tryAdvance(this)) {
                  var1x.accept(var1.apply(this.holder, (long)(this.index++)));
                  return true;
               } else {
                  return false;
               }
            }

            3Splitr createSplit(OfLong var1x, long var2) {
               return new 3Splitr(var1x, var2);
            }
         }

         return StreamSupport.stream(new 3Splitr(var3, 0L), var2);
      }
   }

   public static <R> Stream<R> mapWithIndex(DoubleStream var0, final Streams.DoubleFunctionWithIndex<R> var1) {
      Preconditions.checkNotNull(var0);
      Preconditions.checkNotNull(var1);
      boolean var2 = var0.isParallel();
      OfDouble var3 = var0.spliterator();
      if (!var3.hasCharacteristics(16384)) {
         final java.util.PrimitiveIterator.OfDouble var4 = Spliterators.iterator(var3);
         return StreamSupport.stream(new AbstractSpliterator<R>(var3.estimateSize(), var3.characteristics() & 80) {
            long index = 0L;

            public boolean tryAdvance(Consumer<? super R> var1x) {
               if (var4.hasNext()) {
                  var1x.accept(var1.apply(var4.nextDouble(), (long)(this.index++)));
                  return true;
               } else {
                  return false;
               }
            }
         }, var2);
      } else {
         class 4Splitr extends Streams.MapWithIndexSpliterator<OfDouble, R, 4Splitr> implements DoubleConsumer, Spliterator<R> {
            double holder;

            _Splitr/* $FF was: 4Splitr*/(OfDouble var1x, long var2) {
               super(var1x, var2);
            }

            public void accept(double var1x) {
               this.holder = var1x;
            }

            public boolean tryAdvance(Consumer<? super R> var1x) {
               if (((OfDouble)this.fromSpliterator).tryAdvance(this)) {
                  var1x.accept(var1.apply(this.holder, (long)(this.index++)));
                  return true;
               } else {
                  return false;
               }
            }

            4Splitr createSplit(OfDouble var1x, long var2) {
               return new 4Splitr(var1x, var2);
            }
         }

         return StreamSupport.stream(new 4Splitr(var3, 0L), var2);
      }
   }

   private Streams() {
      super();
   }

   @Beta
   public interface DoubleFunctionWithIndex<R> {
      R apply(double var1, long var3);
   }

   @Beta
   public interface LongFunctionWithIndex<R> {
      R apply(long var1, long var3);
   }

   @Beta
   public interface IntFunctionWithIndex<R> {
      R apply(int var1, long var2);
   }

   private abstract static class MapWithIndexSpliterator<F extends Spliterator<?>, R, S extends Streams.MapWithIndexSpliterator<F, R, S>> implements Spliterator<R> {
      final F fromSpliterator;
      long index;

      MapWithIndexSpliterator(F var1, long var2) {
         super();
         this.fromSpliterator = var1;
         this.index = var2;
      }

      abstract S createSplit(F var1, long var2);

      public S trySplit() {
         Spliterator var1 = this.fromSpliterator.trySplit();
         if (var1 == null) {
            return null;
         } else {
            Streams.MapWithIndexSpliterator var2 = this.createSplit(var1, this.index);
            this.index += var1.getExactSizeIfKnown();
            return var2;
         }
      }

      public long estimateSize() {
         return this.fromSpliterator.estimateSize();
      }

      public int characteristics() {
         return this.fromSpliterator.characteristics() & 16464;
      }
   }

   @Beta
   public interface FunctionWithIndex<T, R> {
      R apply(T var1, long var2);
   }
}
