package com.google.common.base;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Set;
import javax.annotation.Nullable;

@GwtCompatible(
   serializable = true
)
public abstract class Optional<T> implements Serializable {
   private static final long serialVersionUID = 0L;

   public static <T> Optional<T> absent() {
      return Absent.withType();
   }

   public static <T> Optional<T> of(T var0) {
      return new Present(Preconditions.checkNotNull(var0));
   }

   public static <T> Optional<T> fromNullable(@Nullable T var0) {
      return (Optional)(var0 == null ? absent() : new Present(var0));
   }

   @Nullable
   public static <T> Optional<T> fromJavaUtil(@Nullable java.util.Optional<T> var0) {
      return var0 == null ? null : fromNullable(var0.orElse((Object)null));
   }

   @Nullable
   public static <T> java.util.Optional<T> toJavaUtil(@Nullable Optional<T> var0) {
      return var0 == null ? null : var0.toJavaUtil();
   }

   Optional() {
      super();
   }

   public abstract boolean isPresent();

   public abstract T get();

   public abstract T or(T var1);

   public abstract Optional<T> or(Optional<? extends T> var1);

   @Beta
   public abstract T or(Supplier<? extends T> var1);

   @Nullable
   public abstract T orNull();

   public abstract Set<T> asSet();

   public abstract <V> Optional<V> transform(Function<? super T, V> var1);

   public java.util.Optional<T> toJavaUtil() {
      return java.util.Optional.ofNullable(this.orNull());
   }

   public abstract boolean equals(@Nullable Object var1);

   public abstract int hashCode();

   public abstract String toString();

   @Beta
   public static <T> Iterable<T> presentInstances(final Iterable<? extends Optional<? extends T>> var0) {
      Preconditions.checkNotNull(var0);
      return new Iterable<T>() {
         public Iterator<T> iterator() {
            return new AbstractIterator<T>() {
               private final Iterator<? extends Optional<? extends T>> iterator = (Iterator)Preconditions.checkNotNull(var0.iterator());

               protected T computeNext() {
                  while(true) {
                     if (this.iterator.hasNext()) {
                        Optional var1 = (Optional)this.iterator.next();
                        if (!var1.isPresent()) {
                           continue;
                        }

                        return var1.get();
                     }

                     return this.endOfData();
                  }
               }
            };
         }
      };
   }
}
