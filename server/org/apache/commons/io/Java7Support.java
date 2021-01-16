package org.apache.commons.io;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

class Java7Support {
   private static final boolean IS_JAVA7;
   private static Method isSymbolicLink;
   private static Method delete;
   private static Method toPath;
   private static Method exists;
   private static Method toFile;
   private static Method readSymlink;
   private static Method createSymlink;
   private static Object emptyLinkOpts;
   private static Object emptyFileAttributes;

   Java7Support() {
      super();
   }

   public static boolean isSymLink(File var0) {
      try {
         Object var1 = toPath.invoke(var0);
         Boolean var2 = (Boolean)isSymbolicLink.invoke((Object)null, var1);
         return var2;
      } catch (IllegalAccessException var3) {
         throw new RuntimeException(var3);
      } catch (InvocationTargetException var4) {
         throw new RuntimeException(var4);
      }
   }

   public static File readSymbolicLink(File var0) throws IOException {
      try {
         Object var1 = toPath.invoke(var0);
         Object var2 = readSymlink.invoke((Object)null, var1);
         return (File)toFile.invoke(var2);
      } catch (IllegalAccessException var3) {
         throw new RuntimeException(var3);
      } catch (InvocationTargetException var4) {
         throw new RuntimeException(var4);
      }
   }

   private static boolean exists(File var0) throws IOException {
      try {
         Object var1 = toPath.invoke(var0);
         Boolean var2 = (Boolean)exists.invoke((Object)null, var1, emptyLinkOpts);
         return var2;
      } catch (IllegalAccessException var3) {
         throw new RuntimeException(var3);
      } catch (InvocationTargetException var4) {
         throw (RuntimeException)var4.getTargetException();
      }
   }

   public static File createSymbolicLink(File var0, File var1) throws IOException {
      try {
         if (!exists(var0)) {
            Object var2 = toPath.invoke(var0);
            Object var6 = createSymlink.invoke((Object)null, var2, toPath.invoke(var1), emptyFileAttributes);
            return (File)toFile.invoke(var6);
         } else {
            return var0;
         }
      } catch (IllegalAccessException var4) {
         throw new RuntimeException(var4);
      } catch (InvocationTargetException var5) {
         Throwable var3 = var5.getTargetException();
         throw (IOException)var3;
      }
   }

   public static void delete(File var0) throws IOException {
      try {
         Object var1 = toPath.invoke(var0);
         delete.invoke((Object)null, var1);
      } catch (IllegalAccessException var2) {
         throw new RuntimeException(var2);
      } catch (InvocationTargetException var3) {
         throw (IOException)var3.getTargetException();
      }
   }

   public static boolean isAtLeastJava7() {
      return IS_JAVA7;
   }

   static {
      boolean var0 = true;

      try {
         ClassLoader var1 = Thread.currentThread().getContextClassLoader();
         Class var2 = var1.loadClass("java.nio.file.Files");
         Class var3 = var1.loadClass("java.nio.file.Path");
         Class var4 = var1.loadClass("java.nio.file.attribute.FileAttribute");
         Class var5 = var1.loadClass("java.nio.file.LinkOption");
         isSymbolicLink = var2.getMethod("isSymbolicLink", var3);
         delete = var2.getMethod("delete", var3);
         readSymlink = var2.getMethod("readSymbolicLink", var3);
         emptyFileAttributes = Array.newInstance(var4, 0);
         createSymlink = var2.getMethod("createSymbolicLink", var3, var3, emptyFileAttributes.getClass());
         emptyLinkOpts = Array.newInstance(var5, 0);
         exists = var2.getMethod("exists", var3, emptyLinkOpts.getClass());
         toPath = File.class.getMethod("toPath");
         toFile = var3.getMethod("toFile");
      } catch (ClassNotFoundException var6) {
         var0 = false;
      } catch (NoSuchMethodException var7) {
         var0 = false;
      }

      IS_JAVA7 = var0;
   }
}
