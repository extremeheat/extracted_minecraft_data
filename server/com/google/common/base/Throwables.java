package com.google.common.base;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;

@GwtCompatible(
   emulated = true
)
public final class Throwables {
   @GwtIncompatible
   private static final String JAVA_LANG_ACCESS_CLASSNAME = "sun.misc.JavaLangAccess";
   @GwtIncompatible
   @VisibleForTesting
   static final String SHARED_SECRETS_CLASSNAME = "sun.misc.SharedSecrets";
   @Nullable
   @GwtIncompatible
   private static final Object jla = getJLA();
   @Nullable
   @GwtIncompatible
   private static final Method getStackTraceElementMethod;
   @Nullable
   @GwtIncompatible
   private static final Method getStackTraceDepthMethod;

   private Throwables() {
      super();
   }

   @GwtIncompatible
   public static <X extends Throwable> void throwIfInstanceOf(Throwable var0, Class<X> var1) throws X {
      Preconditions.checkNotNull(var0);
      if (var1.isInstance(var0)) {
         throw (Throwable)var1.cast(var0);
      }
   }

   /** @deprecated */
   @Deprecated
   @GwtIncompatible
   public static <X extends Throwable> void propagateIfInstanceOf(@Nullable Throwable var0, Class<X> var1) throws X {
      if (var0 != null) {
         throwIfInstanceOf(var0, var1);
      }

   }

   public static void throwIfUnchecked(Throwable var0) {
      Preconditions.checkNotNull(var0);
      if (var0 instanceof RuntimeException) {
         throw (RuntimeException)var0;
      } else if (var0 instanceof Error) {
         throw (Error)var0;
      }
   }

   /** @deprecated */
   @Deprecated
   @GwtIncompatible
   public static void propagateIfPossible(@Nullable Throwable var0) {
      if (var0 != null) {
         throwIfUnchecked(var0);
      }

   }

   @GwtIncompatible
   public static <X extends Throwable> void propagateIfPossible(@Nullable Throwable var0, Class<X> var1) throws X {
      propagateIfInstanceOf(var0, var1);
      propagateIfPossible(var0);
   }

   @GwtIncompatible
   public static <X1 extends Throwable, X2 extends Throwable> void propagateIfPossible(@Nullable Throwable var0, Class<X1> var1, Class<X2> var2) throws X1, X2 {
      Preconditions.checkNotNull(var2);
      propagateIfInstanceOf(var0, var1);
      propagateIfPossible(var0, var2);
   }

   /** @deprecated */
   @Deprecated
   @CanIgnoreReturnValue
   @GwtIncompatible
   public static RuntimeException propagate(Throwable var0) {
      throwIfUnchecked(var0);
      throw new RuntimeException(var0);
   }

   public static Throwable getRootCause(Throwable var0) {
      Throwable var1;
      while((var1 = var0.getCause()) != null) {
         var0 = var1;
      }

      return var0;
   }

   @Beta
   public static List<Throwable> getCausalChain(Throwable var0) {
      Preconditions.checkNotNull(var0);

      ArrayList var1;
      for(var1 = new ArrayList(4); var0 != null; var0 = var0.getCause()) {
         var1.add(var0);
      }

      return Collections.unmodifiableList(var1);
   }

   @GwtIncompatible
   public static String getStackTraceAsString(Throwable var0) {
      StringWriter var1 = new StringWriter();
      var0.printStackTrace(new PrintWriter(var1));
      return var1.toString();
   }

   @Beta
   @GwtIncompatible
   public static List<StackTraceElement> lazyStackTrace(Throwable var0) {
      return lazyStackTraceIsLazy() ? jlaStackTrace(var0) : Collections.unmodifiableList(Arrays.asList(var0.getStackTrace()));
   }

   @Beta
   @GwtIncompatible
   public static boolean lazyStackTraceIsLazy() {
      return getStackTraceElementMethod != null & getStackTraceDepthMethod != null;
   }

   @GwtIncompatible
   private static List<StackTraceElement> jlaStackTrace(final Throwable var0) {
      Preconditions.checkNotNull(var0);
      return new AbstractList<StackTraceElement>() {
         public StackTraceElement get(int var1) {
            return (StackTraceElement)Throwables.invokeAccessibleNonThrowingMethod(Throwables.getStackTraceElementMethod, Throwables.jla, var0, var1);
         }

         public int size() {
            return (Integer)Throwables.invokeAccessibleNonThrowingMethod(Throwables.getStackTraceDepthMethod, Throwables.jla, var0);
         }
      };
   }

   @GwtIncompatible
   private static Object invokeAccessibleNonThrowingMethod(Method var0, Object var1, Object... var2) {
      try {
         return var0.invoke(var1, var2);
      } catch (IllegalAccessException var4) {
         throw new RuntimeException(var4);
      } catch (InvocationTargetException var5) {
         throw propagate(var5.getCause());
      }
   }

   @Nullable
   @GwtIncompatible
   private static Object getJLA() {
      try {
         Class var0 = Class.forName("sun.misc.SharedSecrets", false, (ClassLoader)null);
         Method var1 = var0.getMethod("getJavaLangAccess");
         return var1.invoke((Object)null);
      } catch (ThreadDeath var2) {
         throw var2;
      } catch (Throwable var3) {
         return null;
      }
   }

   @Nullable
   @GwtIncompatible
   private static Method getGetMethod() {
      return getJlaMethod("getStackTraceElement", Throwable.class, Integer.TYPE);
   }

   @Nullable
   @GwtIncompatible
   private static Method getSizeMethod() {
      return getJlaMethod("getStackTraceDepth", Throwable.class);
   }

   @Nullable
   @GwtIncompatible
   private static Method getJlaMethod(String var0, Class<?>... var1) throws ThreadDeath {
      try {
         return Class.forName("sun.misc.JavaLangAccess", false, (ClassLoader)null).getMethod(var0, var1);
      } catch (ThreadDeath var3) {
         throw var3;
      } catch (Throwable var4) {
         return null;
      }
   }

   static {
      getStackTraceElementMethod = jla == null ? null : getGetMethod();
      getStackTraceDepthMethod = jla == null ? null : getSizeMethod();
   }
}
