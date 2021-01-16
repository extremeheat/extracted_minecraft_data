package com.google.common.base;

import com.google.common.annotations.GwtCompatible;
import java.util.Collections;
import java.util.Set;
import javax.annotation.Nullable;

@GwtCompatible
final class Absent<T> extends Optional<T> {
   static final Absent<Object> INSTANCE = new Absent();
   private static final long serialVersionUID = 0L;

   static <T> Optional<T> withType() {
      return INSTANCE;
   }

   private Absent() {
      super();
   }

   public boolean isPresent() {
      return false;
   }

   public T get() {
      throw new IllegalStateException("Optional.get() cannot be called on an absent value");
   }

   public T or(T var1) {
      return Preconditions.checkNotNull(var1, "use Optional.orNull() instead of Optional.or(null)");
   }

   public Optional<T> or(Optional<? extends T> var1) {
      return (Optional)Preconditions.checkNotNull(var1);
   }

   public T or(Supplier<? extends T> var1) {
      return Preconditions.checkNotNull(var1.get(), "use Optional.orNull() instead of a Supplier that returns null");
   }

   @Nullable
   public T orNull() {
      return null;
   }

   public Set<T> asSet() {
      return Collections.emptySet();
   }

   public <V> Optional<V> transform(Function<? super T, V> var1) {
      Preconditions.checkNotNull(var1);
      return Optional.absent();
   }

   public boolean equals(@Nullable Object var1) {
      return var1 == this;
   }

   public int hashCode() {
      return 2040732332;
   }

   public String toString() {
      return "Optional.absent()";
   }

   private Object readResolve() {
      return INSTANCE;
   }
}
