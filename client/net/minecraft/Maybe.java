package net.minecraft;

import java.util.Objects;

public class Maybe<T> {
   private static final Maybe<?> NO = new Maybe((T)null);
   private final T value;

   private Maybe(T var1) {
      super();
      this.value = (T)var1;
   }

   public boolean isEmpty() {
      return this == NO;
   }

   public boolean hasValue() {
      return !this.isEmpty();
   }

   public T getValue() {
      if (this.isEmpty()) {
         throw new UnsupportedOperationException("No value");
      } else {
         return this.value;
      }
   }

   public static <T> Maybe<T> no() {
      return NO;
   }

   public static <T> Maybe<T> yes(T var0) {
      return new Maybe<>((T)var0);
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         Maybe var2 = (Maybe)var1;
         return this.isEmpty() != var2.isEmpty() ? false : Objects.equals(this.value, var2.value);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return this.isEmpty() ? 0 : Objects.hashCode(this.value);
   }
}
