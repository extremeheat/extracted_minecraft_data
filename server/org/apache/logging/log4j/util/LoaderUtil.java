package org.apache.logging.log4j.util;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Objects;

public final class LoaderUtil {
   public static final String IGNORE_TCCL_PROPERTY = "log4j.ignoreTCL";
   private static final SecurityManager SECURITY_MANAGER = System.getSecurityManager();
   private static Boolean ignoreTCCL;
   private static final boolean GET_CLASS_LOADER_DISABLED;
   private static final PrivilegedAction<ClassLoader> TCCL_GETTER = new LoaderUtil.ThreadContextClassLoaderGetter();

   private LoaderUtil() {
      super();
   }

   public static ClassLoader getThreadContextClassLoader() {
      if (GET_CLASS_LOADER_DISABLED) {
         return LoaderUtil.class.getClassLoader();
      } else {
         return SECURITY_MANAGER == null ? (ClassLoader)TCCL_GETTER.run() : (ClassLoader)AccessController.doPrivileged(TCCL_GETTER);
      }
   }

   public static boolean isClassAvailable(String var0) {
      try {
         Class var1 = loadClass(var0);
         return var1 != null;
      } catch (ClassNotFoundException var2) {
         return false;
      } catch (Throwable var3) {
         LowLevelLogUtil.logException("Unknown error checking for existence of class: " + var0, var3);
         return false;
      }
   }

   public static Class<?> loadClass(String var0) throws ClassNotFoundException {
      if (isIgnoreTccl()) {
         return Class.forName(var0);
      } else {
         try {
            return getThreadContextClassLoader().loadClass(var0);
         } catch (Throwable var2) {
            return Class.forName(var0);
         }
      }
   }

   public static <T> T newInstanceOf(Class<T> var0) throws InstantiationException, IllegalAccessException, InvocationTargetException {
      try {
         return var0.getConstructor().newInstance();
      } catch (NoSuchMethodException var2) {
         return var0.newInstance();
      }
   }

   public static <T> T newInstanceOf(String var0) throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
      return newInstanceOf(loadClass(var0));
   }

   public static <T> T newCheckedInstanceOf(String var0, Class<T> var1) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
      return var1.cast(newInstanceOf(var0));
   }

   public static <T> T newCheckedInstanceOfProperty(String var0, Class<T> var1) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
      String var2 = PropertiesUtil.getProperties().getStringProperty(var0);
      return var2 == null ? null : newCheckedInstanceOf(var2, var1);
   }

   private static boolean isIgnoreTccl() {
      if (ignoreTCCL == null) {
         String var0 = PropertiesUtil.getProperties().getStringProperty("log4j.ignoreTCL", (String)null);
         ignoreTCCL = var0 != null && !"false".equalsIgnoreCase(var0.trim());
      }

      return ignoreTCCL;
   }

   public static Collection<URL> findResources(String var0) {
      Collection var1 = findUrlResources(var0);
      LinkedHashSet var2 = new LinkedHashSet(var1.size());
      Iterator var3 = var1.iterator();

      while(var3.hasNext()) {
         LoaderUtil.UrlResource var4 = (LoaderUtil.UrlResource)var3.next();
         var2.add(var4.getUrl());
      }

      return var2;
   }

   static Collection<LoaderUtil.UrlResource> findUrlResources(String var0) {
      ClassLoader[] var1 = new ClassLoader[]{getThreadContextClassLoader(), LoaderUtil.class.getClassLoader(), GET_CLASS_LOADER_DISABLED ? null : ClassLoader.getSystemClassLoader()};
      LinkedHashSet var2 = new LinkedHashSet();
      ClassLoader[] var3 = var1;
      int var4 = var1.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         ClassLoader var6 = var3[var5];
         if (var6 != null) {
            try {
               Enumeration var7 = var6.getResources(var0);

               while(var7.hasMoreElements()) {
                  var2.add(new LoaderUtil.UrlResource(var6, (URL)var7.nextElement()));
               }
            } catch (IOException var8) {
               LowLevelLogUtil.logException(var8);
            }
         }
      }

      return var2;
   }

   static {
      if (SECURITY_MANAGER != null) {
         boolean var0;
         try {
            SECURITY_MANAGER.checkPermission(new RuntimePermission("getClassLoader"));
            var0 = false;
         } catch (SecurityException var2) {
            var0 = true;
         }

         GET_CLASS_LOADER_DISABLED = var0;
      } else {
         GET_CLASS_LOADER_DISABLED = false;
      }

   }

   static class UrlResource {
      private final ClassLoader classLoader;
      private final URL url;

      UrlResource(ClassLoader var1, URL var2) {
         super();
         this.classLoader = var1;
         this.url = var2;
      }

      public ClassLoader getClassLoader() {
         return this.classLoader;
      }

      public URL getUrl() {
         return this.url;
      }

      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else if (var1 != null && this.getClass() == var1.getClass()) {
            LoaderUtil.UrlResource var2 = (LoaderUtil.UrlResource)var1;
            if (this.classLoader != null) {
               if (!this.classLoader.equals(var2.classLoader)) {
                  return false;
               }
            } else if (var2.classLoader != null) {
               return false;
            }

            if (this.url != null) {
               if (this.url.equals(var2.url)) {
                  return true;
               }
            } else if (var2.url == null) {
               return true;
            }

            return false;
         } else {
            return false;
         }
      }

      public int hashCode() {
         return Objects.hashCode(this.classLoader) + Objects.hashCode(this.url);
      }
   }

   private static class ThreadContextClassLoaderGetter implements PrivilegedAction<ClassLoader> {
      private ThreadContextClassLoaderGetter() {
         super();
      }

      public ClassLoader run() {
         ClassLoader var1 = Thread.currentThread().getContextClassLoader();
         if (var1 != null) {
            return var1;
         } else {
            ClassLoader var2 = LoaderUtil.class.getClassLoader();
            return var2 == null && !LoaderUtil.GET_CLASS_LOADER_DISABLED ? ClassLoader.getSystemClassLoader() : var2;
         }
      }

      // $FF: synthetic method
      ThreadContextClassLoaderGetter(Object var1) {
         this();
      }
   }
}
