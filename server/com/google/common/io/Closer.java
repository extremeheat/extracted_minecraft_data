package com.google.common.io;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.logging.Level;
import javax.annotation.Nullable;

@Beta
@GwtIncompatible
public final class Closer implements Closeable {
   private static final Closer.Suppressor SUPPRESSOR;
   @VisibleForTesting
   final Closer.Suppressor suppressor;
   private final Deque<Closeable> stack = new ArrayDeque(4);
   private Throwable thrown;

   public static Closer create() {
      return new Closer(SUPPRESSOR);
   }

   @VisibleForTesting
   Closer(Closer.Suppressor var1) {
      super();
      this.suppressor = (Closer.Suppressor)Preconditions.checkNotNull(var1);
   }

   @CanIgnoreReturnValue
   public <C extends Closeable> C register(@Nullable C var1) {
      if (var1 != null) {
         this.stack.addFirst(var1);
      }

      return var1;
   }

   public RuntimeException rethrow(Throwable var1) throws IOException {
      Preconditions.checkNotNull(var1);
      this.thrown = var1;
      Throwables.propagateIfPossible(var1, IOException.class);
      throw new RuntimeException(var1);
   }

   public <X extends Exception> RuntimeException rethrow(Throwable var1, Class<X> var2) throws IOException, X {
      Preconditions.checkNotNull(var1);
      this.thrown = var1;
      Throwables.propagateIfPossible(var1, IOException.class);
      Throwables.propagateIfPossible(var1, var2);
      throw new RuntimeException(var1);
   }

   public <X1 extends Exception, X2 extends Exception> RuntimeException rethrow(Throwable var1, Class<X1> var2, Class<X2> var3) throws IOException, X1, X2 {
      Preconditions.checkNotNull(var1);
      this.thrown = var1;
      Throwables.propagateIfPossible(var1, IOException.class);
      Throwables.propagateIfPossible(var1, var2, var3);
      throw new RuntimeException(var1);
   }

   public void close() throws IOException {
      Throwable var1 = this.thrown;

      while(!this.stack.isEmpty()) {
         Closeable var2 = (Closeable)this.stack.removeFirst();

         try {
            var2.close();
         } catch (Throwable var4) {
            if (var1 == null) {
               var1 = var4;
            } else {
               this.suppressor.suppress(var2, var1, var4);
            }
         }
      }

      if (this.thrown == null && var1 != null) {
         Throwables.propagateIfPossible(var1, IOException.class);
         throw new AssertionError(var1);
      }
   }

   static {
      SUPPRESSOR = (Closer.Suppressor)(Closer.SuppressingSuppressor.isAvailable() ? Closer.SuppressingSuppressor.INSTANCE : Closer.LoggingSuppressor.INSTANCE);
   }

   @VisibleForTesting
   static final class SuppressingSuppressor implements Closer.Suppressor {
      static final Closer.SuppressingSuppressor INSTANCE = new Closer.SuppressingSuppressor();
      static final Method addSuppressed = getAddSuppressed();

      SuppressingSuppressor() {
         super();
      }

      static boolean isAvailable() {
         return addSuppressed != null;
      }

      private static Method getAddSuppressed() {
         try {
            return Throwable.class.getMethod("addSuppressed", Throwable.class);
         } catch (Throwable var1) {
            return null;
         }
      }

      public void suppress(Closeable var1, Throwable var2, Throwable var3) {
         if (var2 != var3) {
            try {
               addSuppressed.invoke(var2, var3);
            } catch (Throwable var5) {
               Closer.LoggingSuppressor.INSTANCE.suppress(var1, var2, var3);
            }

         }
      }
   }

   @VisibleForTesting
   static final class LoggingSuppressor implements Closer.Suppressor {
      static final Closer.LoggingSuppressor INSTANCE = new Closer.LoggingSuppressor();

      LoggingSuppressor() {
         super();
      }

      public void suppress(Closeable var1, Throwable var2, Throwable var3) {
         Closeables.logger.log(Level.WARNING, "Suppressing exception thrown when closing " + var1, var3);
      }
   }

   @VisibleForTesting
   interface Suppressor {
      void suppress(Closeable var1, Throwable var2, Throwable var3);
   }
}
