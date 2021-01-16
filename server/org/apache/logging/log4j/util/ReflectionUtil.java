package org.apache.logging.log4j.util;

import java.lang.reflect.Method;
import java.util.Stack;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.status.StatusLogger;

public final class ReflectionUtil {
   static final int JDK_7u25_OFFSET;
   private static final Logger LOGGER = StatusLogger.getLogger();
   private static final boolean SUN_REFLECTION_SUPPORTED;
   private static final Method GET_CALLER_CLASS;
   private static final ReflectionUtil.PrivateSecurityManager SECURITY_MANAGER;

   private ReflectionUtil() {
      super();
   }

   public static boolean supportsFastReflection() {
      return SUN_REFLECTION_SUPPORTED;
   }

   @PerformanceSensitive
   public static Class<?> getCallerClass(int var0) {
      if (var0 < 0) {
         throw new IndexOutOfBoundsException(Integer.toString(var0));
      } else if (supportsFastReflection()) {
         try {
            return (Class)GET_CALLER_CLASS.invoke((Object)null, var0 + 1 + JDK_7u25_OFFSET);
         } catch (Exception var3) {
            LOGGER.error((String)"Error in ReflectionUtil.getCallerClass({}).", (Object)var0, (Object)var3);
            return null;
         }
      } else {
         StackTraceElement var1 = getEquivalentStackTraceElement(var0 + 1);

         try {
            return LoaderUtil.loadClass(var1.getClassName());
         } catch (ClassNotFoundException var4) {
            LOGGER.error((String)"Could not find class in ReflectionUtil.getCallerClass({}).", (Object)var0, (Object)var4);
            return null;
         }
      }
   }

   static StackTraceElement getEquivalentStackTraceElement(int var0) {
      StackTraceElement[] var1 = (new Throwable()).getStackTrace();
      int var2 = 0;
      StackTraceElement[] var3 = var1;
      int var4 = var1.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         StackTraceElement var6 = var3[var5];
         if (isValid(var6)) {
            if (var2 == var0) {
               return var6;
            }

            ++var2;
         }
      }

      LOGGER.error((String)"Could not find an appropriate StackTraceElement at index {}", (Object)var0);
      throw new IndexOutOfBoundsException(Integer.toString(var0));
   }

   private static boolean isValid(StackTraceElement var0) {
      if (var0.isNativeMethod()) {
         return false;
      } else {
         String var1 = var0.getClassName();
         if (var1.startsWith("sun.reflect.")) {
            return false;
         } else {
            String var2 = var0.getMethodName();
            if (!var1.startsWith("java.lang.reflect.") || !var2.equals("invoke") && !var2.equals("newInstance")) {
               if (var1.startsWith("jdk.internal.reflect.")) {
                  return false;
               } else if (var1.equals("java.lang.Class") && var2.equals("newInstance")) {
                  return false;
               } else {
                  return !var1.equals("java.lang.invoke.MethodHandle") || !var2.startsWith("invoke");
               }
            } else {
               return false;
            }
         }
      }
   }

   @PerformanceSensitive
   public static Class<?> getCallerClass(String var0) {
      return getCallerClass(var0, "");
   }

   @PerformanceSensitive
   public static Class<?> getCallerClass(String var0, String var1) {
      if (supportsFastReflection()) {
         boolean var2 = false;

         Class var3;
         for(int var4 = 2; null != (var3 = getCallerClass(var4)); ++var4) {
            if (var0.equals(var3.getName())) {
               var2 = true;
            } else if (var2 && var3.getName().startsWith(var1)) {
               return var3;
            }
         }

         return null;
      } else if (SECURITY_MANAGER != null) {
         return SECURITY_MANAGER.getCallerClass(var0, var1);
      } else {
         try {
            return LoaderUtil.loadClass(getCallerClassName(var0, var1, (new Throwable()).getStackTrace()));
         } catch (ClassNotFoundException var5) {
            return null;
         }
      }
   }

   @PerformanceSensitive
   public static Class<?> getCallerClass(Class<?> var0) {
      if (supportsFastReflection()) {
         boolean var1 = false;

         Class var2;
         for(int var3 = 2; null != (var2 = getCallerClass(var3)); ++var3) {
            if (var0.equals(var2)) {
               var1 = true;
            } else if (var1) {
               return var2;
            }
         }

         return Object.class;
      } else if (SECURITY_MANAGER != null) {
         return SECURITY_MANAGER.getCallerClass(var0);
      } else {
         try {
            return LoaderUtil.loadClass(getCallerClassName(var0.getName(), "", (new Throwable()).getStackTrace()));
         } catch (ClassNotFoundException var4) {
            return Object.class;
         }
      }
   }

   private static String getCallerClassName(String var0, String var1, StackTraceElement... var2) {
      boolean var3 = false;
      StackTraceElement[] var4 = var2;
      int var5 = var2.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         StackTraceElement var7 = var4[var6];
         String var8 = var7.getClassName();
         if (var8.equals(var0)) {
            var3 = true;
         } else if (var3 && var8.startsWith(var1)) {
            return var8;
         }
      }

      return Object.class.getName();
   }

   @PerformanceSensitive
   public static Stack<Class<?>> getCurrentStackTrace() {
      if (SECURITY_MANAGER != null) {
         Class[] var6 = SECURITY_MANAGER.getClassContext();
         Stack var7 = new Stack();
         var7.ensureCapacity(var6.length);
         Class[] var8 = var6;
         int var3 = var6.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            Class var5 = var8[var4];
            var7.push(var5);
         }

         return var7;
      } else if (!supportsFastReflection()) {
         return new Stack();
      } else {
         Stack var0 = new Stack();

         Class var1;
         for(int var2 = 1; null != (var1 = getCallerClass(var2)); ++var2) {
            var0.push(var1);
         }

         return var0;
      }
   }

   static {
      byte var1 = 0;

      Method var0;
      try {
         Class var2 = LoaderUtil.loadClass("sun.reflect.Reflection");
         var0 = var2.getDeclaredMethod("getCallerClass", Integer.TYPE);
         Object var3 = var0.invoke((Object)null, 0);
         Object var4 = var0.invoke((Object)null, 0);
         if (var3 != null && var3 == var2) {
            var3 = var0.invoke((Object)null, 1);
            if (var3 == var2) {
               LOGGER.warn("You are using Java 1.7.0_25 which has a broken implementation of Reflection.getCallerClass.");
               LOGGER.warn("You should upgrade to at least Java 1.7.0_40 or later.");
               LOGGER.debug("Using stack depth compensation offset of 1 due to Java 7u25.");
               var1 = 1;
            }
         } else {
            LOGGER.warn("Unexpected return value from Reflection.getCallerClass(): {}", var4);
            var0 = null;
            var1 = -1;
         }
      } catch (LinkageError | Exception var6) {
         LOGGER.info((String)"sun.reflect.Reflection.getCallerClass is not supported. ReflectionUtil.getCallerClass will be much slower due to this.", (Throwable)var6);
         var0 = null;
         var1 = -1;
      }

      SUN_REFLECTION_SUPPORTED = var0 != null;
      GET_CALLER_CLASS = var0;
      JDK_7u25_OFFSET = var1;

      ReflectionUtil.PrivateSecurityManager var7;
      try {
         SecurityManager var8 = System.getSecurityManager();
         if (var8 != null) {
            var8.checkPermission(new RuntimePermission("createSecurityManager"));
         }

         var7 = new ReflectionUtil.PrivateSecurityManager();
      } catch (SecurityException var5) {
         LOGGER.debug("Not allowed to create SecurityManager. Falling back to slowest ReflectionUtil implementation.");
         var7 = null;
      }

      SECURITY_MANAGER = var7;
   }

   static final class PrivateSecurityManager extends SecurityManager {
      PrivateSecurityManager() {
         super();
      }

      protected Class<?>[] getClassContext() {
         return super.getClassContext();
      }

      protected Class<?> getCallerClass(String var1, String var2) {
         boolean var3 = false;
         Class[] var4 = this.getClassContext();
         int var5 = var4.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            Class var7 = var4[var6];
            if (var1.equals(var7.getName())) {
               var3 = true;
            } else if (var3 && var7.getName().startsWith(var2)) {
               return var7;
            }
         }

         return null;
      }

      protected Class<?> getCallerClass(Class<?> var1) {
         boolean var2 = false;
         Class[] var3 = this.getClassContext();
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            Class var6 = var3[var5];
            if (var1.equals(var6)) {
               var2 = true;
            } else if (var2) {
               return var6;
            }
         }

         return Object.class;
      }
   }
}
