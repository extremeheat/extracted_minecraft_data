package com.google.common.base;

import com.google.common.annotations.GwtCompatible;
import java.util.Collections;
import java.util.Set;
import javax.annotation.Nullable;

@GwtCompatible
final class Present<T> extends Optional<T> {
   private final T reference;
   private static final long serialVersionUID = 0L;

   Present(T var1) {
      super();
      this.reference = var1;
   }

   public boolean isPresent() {
      return true;
   }

   public T get() {
      return this.reference;
   }

   public T or(T var1) {
      Preconditions.checkNotNull(var1, "use Optional.orNull() instead of Optional.or(null)");
      return this.reference;
   }

   public Optional<T> or(Optional<? extends T> var1) {
      Preconditions.checkNotNull(var1);
      return this;
   }

   public T or(Supplier<? extends T> var1) {
      Preconditions.checkNotNull(var1);
      return this.reference;
   }

   public T orNull() {
      return this.reference;
   }

   public Set<T> asSet() {
      return Collections.singleton(this.reference);
   }

   public <V> Optional<V> transform(Function<? super T, V> var1) {
      return new Present(Preconditions.checkNotNull(var1.apply(this.reference), "the Function passed to Optional.transform() must not return null."));
   }

   public boolean equals(@Nullable Object var1) {
      if (var1 instanceof Present) {
         Present var2 = (Present)var1;
         return this.reference.equals(var2.reference);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return 1502476572 + this.reference.hashCode();
   }

   public String toString() {
      return "Optional.of(" + this.reference + ")";
   }
}
