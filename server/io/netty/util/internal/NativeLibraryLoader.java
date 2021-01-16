package io.netty.util.internal;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFilePermission;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;

public final class NativeLibraryLoader {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(NativeLibraryLoader.class);
   private static final String NATIVE_RESOURCE_HOME = "META-INF/native/";
   private static final File WORKDIR;
   private static final boolean DELETE_NATIVE_LIB_AFTER_LOADING;

   public static void loadFirstAvailable(ClassLoader var0, String... var1) {
      ArrayList var2 = new ArrayList();
      String[] var3 = var1;
      int var4 = var1.length;
      int var5 = 0;

      while(var5 < var4) {
         String var6 = var3[var5];

         try {
            load(var6, var0);
            return;
         } catch (Throwable var8) {
            var2.add(var8);
            logger.debug("Unable to load the library '{}', trying next name...", var6, var8);
            ++var5;
         }
      }

      IllegalArgumentException var9 = new IllegalArgumentException("Failed to load any of the given libraries: " + Arrays.toString(var1));
      ThrowableUtil.addSuppressedAndClear(var9, var2);
      throw var9;
   }

   private static String calculatePackagePrefix() {
      String var0 = NativeLibraryLoader.class.getName();
      String var1 = "io!netty!util!internal!NativeLibraryLoader".replace('!', '.');
      if (!var0.endsWith(var1)) {
         throw new UnsatisfiedLinkError(String.format("Could not find prefix added to %s to get %s. When shading, only adding a package prefix is supported", var1, var0));
      } else {
         return var0.substring(0, var0.length() - var1.length());
      }
   }

   public static void load(String var0, ClassLoader var1) {
      String var2 = calculatePackagePrefix().replace('.', '_') + var0;
      ArrayList var3 = new ArrayList();

      try {
         loadLibrary(var1, var2, false);
      } catch (Throwable var25) {
         var3.add(var25);
         logger.debug("{} cannot be loaded from java.libary.path, now trying export to -Dio.netty.native.workdir: {}", var2, WORKDIR, var25);
         String var4 = System.mapLibraryName(var2);
         String var5 = "META-INF/native/" + var4;
         InputStream var6 = null;
         FileOutputStream var7 = null;
         File var8 = null;
         URL var9;
         if (var1 == null) {
            var9 = ClassLoader.getSystemResource(var5);
         } else {
            var9 = var1.getResource(var5);
         }

         try {
            if (var9 == null) {
               if (!PlatformDependent.isOsx()) {
                  FileNotFoundException var27 = new FileNotFoundException(var5);
                  ThrowableUtil.addSuppressedAndClear(var27, var3);
                  throw var27;
               }

               String var10 = var5.endsWith(".jnilib") ? "META-INF/native/lib" + var2 + ".dynlib" : "META-INF/native/lib" + var2 + ".jnilib";
               if (var1 == null) {
                  var9 = ClassLoader.getSystemResource(var10);
               } else {
                  var9 = var1.getResource(var10);
               }

               if (var9 == null) {
                  FileNotFoundException var29 = new FileNotFoundException(var10);
                  ThrowableUtil.addSuppressedAndClear(var29, var3);
                  throw var29;
               }
            }

            int var26 = var4.lastIndexOf(46);
            String var28 = var4.substring(0, var26);
            String var12 = var4.substring(var26, var4.length());
            var8 = File.createTempFile(var28, var12, WORKDIR);
            var6 = var9.openStream();
            var7 = new FileOutputStream(var8);
            byte[] var13 = new byte[8192];

            int var14;
            while((var14 = var6.read(var13)) > 0) {
               var7.write(var13, 0, var14);
            }

            var7.flush();
            closeQuietly(var7);
            var7 = null;
            loadLibrary(var1, var8.getPath(), true);
         } catch (UnsatisfiedLinkError var22) {
            try {
               if (var8 != null && var8.isFile() && var8.canRead() && !NativeLibraryLoader.NoexecVolumeDetector.canExecuteExecutable(var8)) {
                  logger.info("{} exists but cannot be executed even when execute permissions set; check volume for \"noexec\" flag; use -Dio.netty.native.workdir=[path] to set native working directory separately.", (Object)var8.getPath());
               }
            } catch (Throwable var21) {
               var3.add(var21);
               logger.debug("Error checking if {} is on a file store mounted with noexec", var8, var21);
            }

            ThrowableUtil.addSuppressedAndClear(var22, var3);
            throw var22;
         } catch (Exception var23) {
            UnsatisfiedLinkError var11 = new UnsatisfiedLinkError("could not load a native library: " + var2);
            var11.initCause(var23);
            ThrowableUtil.addSuppressedAndClear(var11, var3);
            throw var11;
         } finally {
            closeQuietly(var6);
            closeQuietly(var7);
            if (var8 != null && (!DELETE_NATIVE_LIB_AFTER_LOADING || !var8.delete())) {
               var8.deleteOnExit();
            }

         }
      }
   }

   private static void loadLibrary(ClassLoader var0, String var1, boolean var2) {
      Object var3 = null;

      try {
         try {
            Class var4 = tryToLoadClass(var0, NativeLibraryUtil.class);
            loadLibraryByHelper(var4, var1, var2);
            logger.debug("Successfully loaded the library {}", (Object)var1);
            return;
         } catch (UnsatisfiedLinkError var5) {
            logger.debug("Unable to load the library '{}', trying other loading mechanism.", var1, var5);
         } catch (Exception var6) {
            logger.debug("Unable to load the library '{}', trying other loading mechanism.", var1, var6);
         }

         NativeLibraryUtil.loadLibrary(var1, var2);
         logger.debug("Successfully loaded the library {}", (Object)var1);
      } catch (UnsatisfiedLinkError var7) {
         if (var3 != null) {
            ThrowableUtil.addSuppressed(var7, (Throwable)var3);
         }

         throw var7;
      }
   }

   private static void loadLibraryByHelper(final Class<?> var0, final String var1, final boolean var2) throws UnsatisfiedLinkError {
      Object var3 = AccessController.doPrivileged(new PrivilegedAction<Object>() {
         public Object run() {
            try {
               Method var1x = var0.getMethod("loadLibrary", String.class, Boolean.TYPE);
               var1x.setAccessible(true);
               return var1x.invoke((Object)null, var1, var2);
            } catch (Exception var2x) {
               return var2x;
            }
         }
      });
      if (var3 instanceof Throwable) {
         Throwable var4 = (Throwable)var3;

         assert !(var4 instanceof UnsatisfiedLinkError) : var4 + " should be a wrapper throwable";

         Throwable var5 = var4.getCause();
         if (var5 instanceof UnsatisfiedLinkError) {
            throw (UnsatisfiedLinkError)var5;
         } else {
            UnsatisfiedLinkError var6 = new UnsatisfiedLinkError(var4.getMessage());
            var6.initCause(var4);
            throw var6;
         }
      }
   }

   private static Class<?> tryToLoadClass(final ClassLoader var0, final Class<?> var1) throws ClassNotFoundException {
      try {
         return Class.forName(var1.getName(), false, var0);
      } catch (ClassNotFoundException var7) {
         if (var0 == null) {
            throw var7;
         } else {
            try {
               final byte[] var3 = classToByteArray(var1);
               return (Class)AccessController.doPrivileged(new PrivilegedAction<Class<?>>() {
                  public Class<?> run() {
                     try {
                        Method var1x = ClassLoader.class.getDeclaredMethod("defineClass", String.class, byte[].class, Integer.TYPE, Integer.TYPE);
                        var1x.setAccessible(true);
                        return (Class)var1x.invoke(var0, var1.getName(), var3, 0, var3.length);
                     } catch (Exception var2) {
                        throw new IllegalStateException("Define class failed!", var2);
                     }
                  }
               });
            } catch (ClassNotFoundException var4) {
               ThrowableUtil.addSuppressed(var4, (Throwable)var7);
               throw var4;
            } catch (RuntimeException var5) {
               ThrowableUtil.addSuppressed(var5, (Throwable)var7);
               throw var5;
            } catch (Error var6) {
               ThrowableUtil.addSuppressed(var6, (Throwable)var7);
               throw var6;
            }
         }
      }
   }

   private static byte[] classToByteArray(Class<?> var0) throws ClassNotFoundException {
      String var1 = var0.getName();
      int var2 = var1.lastIndexOf(46);
      if (var2 > 0) {
         var1 = var1.substring(var2 + 1);
      }

      URL var3 = var0.getResource(var1 + ".class");
      if (var3 == null) {
         throw new ClassNotFoundException(var0.getName());
      } else {
         byte[] var4 = new byte[1024];
         ByteArrayOutputStream var5 = new ByteArrayOutputStream(4096);
         InputStream var6 = null;

         try {
            var6 = var3.openStream();

            int var7;
            while((var7 = var6.read(var4)) != -1) {
               var5.write(var4, 0, var7);
            }

            byte[] var13 = var5.toByteArray();
            return var13;
         } catch (IOException var11) {
            throw new ClassNotFoundException(var0.getName(), var11);
         } finally {
            closeQuietly(var6);
            closeQuietly(var5);
         }
      }
   }

   private static void closeQuietly(Closeable var0) {
      if (var0 != null) {
         try {
            var0.close();
         } catch (IOException var2) {
         }
      }

   }

   private NativeLibraryLoader() {
      super();
   }

   static {
      String var0 = SystemPropertyUtil.get("io.netty.native.workdir");
      if (var0 != null) {
         File var1 = new File(var0);
         var1.mkdirs();

         try {
            var1 = var1.getAbsoluteFile();
         } catch (Exception var3) {
         }

         WORKDIR = var1;
         logger.debug("-Dio.netty.native.workdir: " + WORKDIR);
      } else {
         WORKDIR = PlatformDependent.tmpdir();
         logger.debug("-Dio.netty.native.workdir: " + WORKDIR + " (io.netty.tmpdir)");
      }

      DELETE_NATIVE_LIB_AFTER_LOADING = SystemPropertyUtil.getBoolean("io.netty.native.deleteLibAfterLoading", true);
   }

   private static final class NoexecVolumeDetector {
      private static boolean canExecuteExecutable(File var0) throws IOException {
         if (PlatformDependent.javaVersion() < 7) {
            return true;
         } else if (var0.canExecute()) {
            return true;
         } else {
            Set var1 = Files.getPosixFilePermissions(var0.toPath());
            EnumSet var2 = EnumSet.of(PosixFilePermission.OWNER_EXECUTE, PosixFilePermission.GROUP_EXECUTE, PosixFilePermission.OTHERS_EXECUTE);
            if (var1.containsAll(var2)) {
               return false;
            } else {
               EnumSet var3 = EnumSet.copyOf(var1);
               var3.addAll(var2);
               Files.setPosixFilePermissions(var0.toPath(), var3);
               return var0.canExecute();
            }
         }
      }

      private NoexecVolumeDetector() {
         super();
      }
   }
}
