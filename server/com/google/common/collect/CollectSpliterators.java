package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import java.util.Comparator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import javax.annotation.Nullable;

@GwtCompatible
final class CollectSpliterators {
   private CollectSpliterators() {
      super();
   }

   static <T> Spliterator<T> indexed(int var0, int var1, IntFunction<T> var2) {
      return indexed(var0, var1, var2, (Comparator)null);
   }

   static <T> Spliterator<T> indexed(int var0, final int var1, IntFunction<T> var2, final Comparator<? super T> var3) {
      if (var3 != null) {
         Preconditions.checkArgument((var1 & 4) != 0);
      }

      class 1WithCharacteristics implements Spliterator<T> {
         private final Spliterator<T> delegate;

         _WithCharacteristics/* $FF was: 1WithCharacteristics*/(Spliterator<T> var1x) {
            super();
            this.delegate = var1x;
         }

         public boolean tryAdvance(Consumer<? super T> var1x) {
            return this.delegate.tryAdvance(var1x);
         }

         public void forEachRemaining(Consumer<? super T> var1x) {
            this.delegate.forEachRemaining(var1x);
         }

         @Nullable
         public Spliterator<T> trySplit() {
            Spliterator var1x = this.delegate.trySplit();
            return var1x == null ? null : new 1WithCharacteristics(var1x);
         }

         public long estimateSize() {
            return this.delegate.estimateSize();
         }

         public int characteristics() {
            return this.delegate.characteristics() | var1;
         }

         public Comparator<? super T> getComparator() {
            if (this.hasCharacteristics(4)) {
               return var3;
            } else {
               throw new IllegalStateException();
            }
         }
      }

      return new 1WithCharacteristics(IntStream.range(0, var0).mapToObj(var2).spliterator());
   }

   static <F, T> Spliterator<T> map(final Spliterator<F> var0, final Function<? super F, ? extends T> var1) {
      Preconditions.checkNotNull(var0);
      Preconditions.checkNotNull(var1);
      return new Spliterator<T>() {
         public boolean tryAdvance(Consumer<? super T> var1x) {
            return var0.tryAdvance((var2) -> {
               var1x.accept(var1.apply(var2));
            });
         }

         public void forEachRemaining(Consumer<? super T> var1x) {
            var0.forEachRemaining((var2) -> {
               var1x.accept(var1.apply(var2));
            });
         }

         public Spliterator<T> trySplit() {
            Spliterator var1x = var0.trySplit();
            return var1x != null ? CollectSpliterators.map(var1x, var1) : null;
         }

         public long estimateSize() {
            return var0.estimateSize();
         }

         public int characteristics() {
            return var0.characteristics() & -262;
         }
      };
   }

   static <T> Spliterator<T> filter(final Spliterator<T> var0, final Predicate<? super T> var1) {
      Preconditions.checkNotNull(var0);
      Preconditions.checkNotNull(var1);

      class 1Splitr implements Spliterator<T>, Consumer<T> {
         T holder = null;

         _Splitr/* $FF was: 1Splitr*/() {
            super();
         }

         public void accept(T var1x) {
            this.holder = var1x;
         }

         public boolean tryAdvance(Consumer<? super T> var1x) {
            while(true) {
               if (var0.tryAdvance(this)) {
                  boolean var2;
                  try {
                     if (!var1.test(this.holder)) {
                        continue;
                     }

                     var1x.accept(this.holder);
                     var2 = true;
                  } finally {
                     this.holder = null;
                  }

                  return var2;
               }

               return false;
            }
         }

         public Spliterator<T> trySplit() {
            Spliterator var1x = var0.trySplit();
            return var1x == null ? null : CollectSpliterators.filter(var1x, var1);
         }

         public long estimateSize() {
            return var0.estimateSize() / 2L;
         }

         public Comparator<? super T> getComparator() {
            return var0.getComparator();
         }

         public int characteristics() {
            return var0.characteristics() & 277;
         }
      }

      return new 1Splitr();
   }

   static <F, T> Spliterator<T> flatMap(Spliterator<F> var0, final Function<? super F, Spliterator<T>> var1, int var2, long var3) {
      Preconditions.checkArgument((var2 & 16384) == 0, "flatMap does not support SUBSIZED characteristic");
      Preconditions.checkArgument((var2 & 4) == 0, "flatMap does not support SORTED characteristic");
      Preconditions.checkNotNull(var0);
      Preconditions.checkNotNull(var1);

      class 1FlatMapSpliterator implements Spliterator<T> {
         @Nullable
         Spliterator<T> prefix;
         final Spliterator<F> from;
         final int characteristics;
         long estimatedSize;

         _FlatMapSpliterator/* $FF was: 1FlatMapSpliterator*/(Spliterator<T> var1x, Spliterator<F> var2, int var3, long var4) {
            super();
            this.prefix = var1x;
            this.from = var2;
            this.characteristics = var3;
            this.estimatedSize = var4;
         }

         public boolean tryAdvance(Consumer<? super T> var1x) {
            do {
               if (this.prefix != null && this.prefix.tryAdvance(var1x)) {
                  if (this.estimatedSize != 9223372036854775807L) {
                     --this.estimatedSize;
                  }

                  return true;
               }

               this.prefix = null;
            } while(this.from.tryAdvance((var2) -> {
               this.prefix = (Spliterator)var1.apply(var2);
            }));

            return false;
         }

         public void forEachRemaining(Consumer<? super T> var1x) {
            if (this.prefix != null) {
               this.prefix.forEachRemaining(var1x);
               this.prefix = null;
            }

            this.from.forEachRemaining((var2) -> {
               ((Spliterator)var1.apply(var2)).forEachRemaining(var1x);
            });
            this.estimatedSize = 0L;
         }

         public Spliterator<T> trySplit() {
            Spliterator var1x = this.from.trySplit();
            if (var1x != null) {
               int var6 = this.characteristics & -65;
               long var3 = this.estimateSize();
               if (var3 < 9223372036854775807L) {
                  var3 /= 2L;
                  this.estimatedSize -= var3;
               }

               1FlatMapSpliterator var5 = new 1FlatMapSpliterator(this.prefix, var1x, var6, var3);
               this.prefix = null;
               return var5;
            } else if (this.prefix != null) {
               Spliterator var2 = this.prefix;
               this.prefix = null;
               return var2;
            } else {
               return null;
            }
         }

         public long estimateSize() {
            if (this.prefix != null) {
               this.estimatedSize = Math.max(this.estimatedSize, this.prefix.estimateSize());
            }

            return Math.max(this.estimatedSize, 0L);
         }

         public int characteristics() {
            return this.characteristics;
         }
      }

      return new 1FlatMapSpliterator((Spliterator)null, var0, var2, var3);
   }
}
