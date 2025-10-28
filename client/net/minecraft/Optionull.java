package net.minecraft;

import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import org.jspecify.annotations.Nullable;

public class Optionull {
   public Optionull() {
      super();
   }

   /** @deprecated */
   @Deprecated
   public static <T> T orElse(@Nullable T var0, T var1) {
      return (T)Objects.requireNonNullElse(var0, var1);
   }

   public static <T, R> @Nullable R map(@Nullable T var0, Function<T, R> var1) {
      return (R)(var0 == null ? null : var1.apply(var0));
   }

   public static <T, R> R mapOrDefault(@Nullable T var0, Function<T, R> var1, R var2) {
      return var0 == null ? var2 : var1.apply(var0);
   }

   public static <T, R> R mapOrElse(@Nullable T var0, Function<T, R> var1, Supplier<R> var2) {
      return (R)(var0 == null ? var2.get() : var1.apply(var0));
   }

   public static <T> @Nullable T first(Collection<T> var0) {
      Iterator var1 = var0.iterator();
      return (T)(var1.hasNext() ? var1.next() : null);
   }

   public static <T> T firstOrDefault(Collection<T> var0, T var1) {
      Iterator var2 = var0.iterator();
      return var2.hasNext() ? var2.next() : var1;
   }

   public static <T> T firstOrElse(Collection<T> var0, Supplier<T> var1) {
      Iterator var2 = var0.iterator();
      return (T)(var2.hasNext() ? var2.next() : var1.get());
   }

   public static <T> boolean isNullOrEmpty(T @Nullable [] var0) {
      return var0 == null || var0.length == 0;
   }

   public static boolean isNullOrEmpty(boolean @Nullable [] var0) {
      return var0 == null || var0.length == 0;
   }

   public static boolean isNullOrEmpty(byte @Nullable [] var0) {
      return var0 == null || var0.length == 0;
   }

   public static boolean isNullOrEmpty(char @Nullable [] var0) {
      return var0 == null || var0.length == 0;
   }

   public static boolean isNullOrEmpty(short @Nullable [] var0) {
      return var0 == null || var0.length == 0;
   }

   public static boolean isNullOrEmpty(int @Nullable [] var0) {
      return var0 == null || var0.length == 0;
   }

   public static boolean isNullOrEmpty(long @Nullable [] var0) {
      return var0 == null || var0.length == 0;
   }

   public static boolean isNullOrEmpty(float @Nullable [] var0) {
      return var0 == null || var0.length == 0;
   }

   public static boolean isNullOrEmpty(double @Nullable [] var0) {
      return var0 == null || var0.length == 0;
   }
}
