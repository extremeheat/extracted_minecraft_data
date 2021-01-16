package io.netty.handler.ssl;

import io.netty.buffer.ByteBuf;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.netty.internal.tcnative.Buffer;
import io.netty.internal.tcnative.Library;
import io.netty.internal.tcnative.SSL;
import io.netty.internal.tcnative.SSLContext;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.ReferenceCounted;
import io.netty.util.internal.NativeLibraryLoader;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class OpenSsl {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(OpenSsl.class);
   private static final Throwable UNAVAILABILITY_CAUSE;
   static final List<String> DEFAULT_CIPHERS;
   static final Set<String> AVAILABLE_CIPHER_SUITES;
   private static final Set<String> AVAILABLE_OPENSSL_CIPHER_SUITES;
   private static final Set<String> AVAILABLE_JAVA_CIPHER_SUITES;
   private static final boolean SUPPORTS_KEYMANAGER_FACTORY;
   private static final boolean SUPPORTS_HOSTNAME_VALIDATION;
   private static final boolean USE_KEYMANAGER_FACTORY;
   private static final boolean SUPPORTS_OCSP;
   static final Set<String> SUPPORTED_PROTOCOLS_SET;

   private static boolean doesSupportOcsp() {
      boolean var0 = false;
      if ((long)version() >= 268443648L) {
         long var1 = -1L;

         try {
            var1 = SSLContext.make(16, 1);
            SSLContext.enableOcsp(var1, false);
            var0 = true;
         } catch (Exception var7) {
         } finally {
            if (var1 != -1L) {
               SSLContext.free(var1);
            }

         }
      }

      return var0;
   }

   private static boolean doesSupportProtocol(int var0) {
      long var1 = -1L;

      boolean var4;
      try {
         var1 = SSLContext.make(var0, 2);
         boolean var3 = true;
         return var3;
      } catch (Exception var8) {
         var4 = false;
      } finally {
         if (var1 != -1L) {
            SSLContext.free(var1);
         }

      }

      return var4;
   }

   public static boolean isAvailable() {
      return UNAVAILABILITY_CAUSE == null;
   }

   public static boolean isAlpnSupported() {
      return (long)version() >= 268443648L;
   }

   public static boolean isOcspSupported() {
      return SUPPORTS_OCSP;
   }

   public static int version() {
      return isAvailable() ? SSL.version() : -1;
   }

   public static String versionString() {
      return isAvailable() ? SSL.versionString() : null;
   }

   public static void ensureAvailability() {
      if (UNAVAILABILITY_CAUSE != null) {
         throw (Error)(new UnsatisfiedLinkError("failed to load the required native library")).initCause(UNAVAILABILITY_CAUSE);
      }
   }

   public static Throwable unavailabilityCause() {
      return UNAVAILABILITY_CAUSE;
   }

   /** @deprecated */
   @Deprecated
   public static Set<String> availableCipherSuites() {
      return availableOpenSslCipherSuites();
   }

   public static Set<String> availableOpenSslCipherSuites() {
      return AVAILABLE_OPENSSL_CIPHER_SUITES;
   }

   public static Set<String> availableJavaCipherSuites() {
      return AVAILABLE_JAVA_CIPHER_SUITES;
   }

   public static boolean isCipherSuiteAvailable(String var0) {
      String var1 = CipherSuiteConverter.toOpenSsl(var0);
      if (var1 != null) {
         var0 = var1;
      }

      return AVAILABLE_OPENSSL_CIPHER_SUITES.contains(var0);
   }

   public static boolean supportsKeyManagerFactory() {
      return SUPPORTS_KEYMANAGER_FACTORY;
   }

   public static boolean supportsHostnameValidation() {
      return SUPPORTS_HOSTNAME_VALIDATION;
   }

   static boolean useKeyManagerFactory() {
      return USE_KEYMANAGER_FACTORY;
   }

   static long memoryAddress(ByteBuf var0) {
      assert var0.isDirect();

      return var0.hasMemoryAddress() ? var0.memoryAddress() : Buffer.address(var0.nioBuffer());
   }

   private OpenSsl() {
      super();
   }

   private static void loadTcNative() throws Exception {
      String var0 = PlatformDependent.normalizedOs();
      String var1 = PlatformDependent.normalizedArch();
      LinkedHashSet var2 = new LinkedHashSet(4);
      String var3 = "netty_tcnative";
      var2.add(var3 + "_" + var0 + '_' + var1);
      if ("linux".equalsIgnoreCase(var0)) {
         var2.add(var3 + "_" + var0 + '_' + var1 + "_fedora");
      }

      var2.add(var3 + "_" + var1);
      var2.add(var3);
      NativeLibraryLoader.loadFirstAvailable(SSL.class.getClassLoader(), (String[])var2.toArray(new String[var2.size()]));
   }

   private static boolean initializeTcNative() throws Exception {
      return Library.initialize();
   }

   static void releaseIfNeeded(ReferenceCounted var0) {
      if (var0.refCnt() > 0) {
         ReferenceCountUtil.safeRelease(var0);
      }

   }

   static {
      Object var0 = null;
      if (SystemPropertyUtil.getBoolean("io.netty.handler.ssl.noOpenSsl", false)) {
         var0 = new UnsupportedOperationException("OpenSSL was explicit disabled with -Dio.netty.handler.ssl.noOpenSsl=true");
         logger.debug("netty-tcnative explicit disabled; " + OpenSslEngine.class.getSimpleName() + " will be unavailable.", (Throwable)var0);
      } else {
         try {
            Class.forName("io.netty.internal.tcnative.SSL", false, OpenSsl.class.getClassLoader());
         } catch (ClassNotFoundException var41) {
            var0 = var41;
            logger.debug("netty-tcnative not in the classpath; " + OpenSslEngine.class.getSimpleName() + " will be unavailable.");
         }

         if (var0 == null) {
            try {
               loadTcNative();
            } catch (Throwable var40) {
               var0 = var40;
               logger.debug("Failed to load netty-tcnative; " + OpenSslEngine.class.getSimpleName() + " will be unavailable, unless the application has already loaded the symbols by some other means. See http://netty.io/wiki/forked-tomcat-native.html for more information.", var40);
            }

            try {
               initializeTcNative();
               var0 = null;
            } catch (Throwable var45) {
               if (var0 == null) {
                  var0 = var45;
               }

               logger.debug("Failed to initialize netty-tcnative; " + OpenSslEngine.class.getSimpleName() + " will be unavailable. See http://netty.io/wiki/forked-tomcat-native.html for more information.", var45);
            }
         }
      }

      UNAVAILABILITY_CAUSE = (Throwable)var0;
      if (var0 == null) {
         logger.debug("netty-tcnative using native library: {}", (Object)SSL.versionString());
         ArrayList var1 = new ArrayList();
         LinkedHashSet var2 = new LinkedHashSet(128);
         boolean var3 = false;
         boolean var4 = false;
         boolean var5 = false;

         try {
            long var6 = SSLContext.make(31, 1);
            long var8 = 0L;
            SelfSignedCertificate var10 = null;

            try {
               SSLContext.setCipherSuite(var6, "ALL");
               long var11 = SSL.newSSL(var6, true);

               try {
                  String[] var13 = SSL.getCiphers(var11);
                  int var14 = var13.length;

                  for(int var15 = 0; var15 < var14; ++var15) {
                     String var16 = var13[var15];
                     if (var16 != null && !var16.isEmpty() && !var2.contains(var16)) {
                        var2.add(var16);
                     }
                  }

                  try {
                     SSL.setHostNameValidation(var11, 0, "netty.io");
                     var5 = true;
                  } catch (Throwable var39) {
                     logger.debug("Hostname Verification not supported.");
                  }

                  try {
                     var10 = new SelfSignedCertificate();
                     var8 = ReferenceCountedOpenSslContext.toBIO(var10.cert());
                     SSL.setCertificateChainBio(var11, var8, false);
                     var3 = true;

                     try {
                        var4 = (Boolean)AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
                           public Boolean run() {
                              return SystemPropertyUtil.getBoolean("io.netty.handler.ssl.openssl.useKeyManagerFactory", true);
                           }
                        });
                     } catch (Throwable var37) {
                        logger.debug("Failed to get useKeyManagerFactory system property.");
                     }
                  } catch (Throwable var38) {
                     logger.debug("KeyManagerFactory not supported.");
                  }
               } finally {
                  SSL.freeSSL(var11);
                  if (var8 != 0L) {
                     SSL.freeBIO(var8);
                  }

                  if (var10 != null) {
                     var10.delete();
                  }

               }
            } finally {
               SSLContext.free(var6);
            }
         } catch (Exception var44) {
            logger.warn("Failed to get the list of available OpenSSL cipher suites.", (Throwable)var44);
         }

         AVAILABLE_OPENSSL_CIPHER_SUITES = Collections.unmodifiableSet(var2);
         LinkedHashSet var46 = new LinkedHashSet(AVAILABLE_OPENSSL_CIPHER_SUITES.size() * 2);
         Iterator var7 = AVAILABLE_OPENSSL_CIPHER_SUITES.iterator();

         while(var7.hasNext()) {
            String var48 = (String)var7.next();
            var46.add(CipherSuiteConverter.toJava(var48, "TLS"));
            var46.add(CipherSuiteConverter.toJava(var48, "SSL"));
         }

         SslUtils.addIfSupported(var46, var1, SslUtils.DEFAULT_CIPHER_SUITES);
         SslUtils.useFallbackCiphersIfDefaultIsEmpty(var1, (Iterable)var46);
         DEFAULT_CIPHERS = Collections.unmodifiableList(var1);
         AVAILABLE_JAVA_CIPHER_SUITES = Collections.unmodifiableSet(var46);
         LinkedHashSet var47 = new LinkedHashSet(AVAILABLE_OPENSSL_CIPHER_SUITES.size() + AVAILABLE_JAVA_CIPHER_SUITES.size());
         var47.addAll(AVAILABLE_OPENSSL_CIPHER_SUITES);
         var47.addAll(AVAILABLE_JAVA_CIPHER_SUITES);
         AVAILABLE_CIPHER_SUITES = var47;
         SUPPORTS_KEYMANAGER_FACTORY = var3;
         SUPPORTS_HOSTNAME_VALIDATION = var5;
         USE_KEYMANAGER_FACTORY = var4;
         LinkedHashSet var49 = new LinkedHashSet(6);
         var49.add("SSLv2Hello");
         if (doesSupportProtocol(1)) {
            var49.add("SSLv2");
         }

         if (doesSupportProtocol(2)) {
            var49.add("SSLv3");
         }

         if (doesSupportProtocol(4)) {
            var49.add("TLSv1");
         }

         if (doesSupportProtocol(8)) {
            var49.add("TLSv1.1");
         }

         if (doesSupportProtocol(16)) {
            var49.add("TLSv1.2");
         }

         SUPPORTED_PROTOCOLS_SET = Collections.unmodifiableSet(var49);
         SUPPORTS_OCSP = doesSupportOcsp();
         if (logger.isDebugEnabled()) {
            logger.debug("Supported protocols (OpenSSL): {} ", (Object)Arrays.asList(SUPPORTED_PROTOCOLS_SET));
            logger.debug("Default cipher suites (OpenSSL): {}", (Object)DEFAULT_CIPHERS);
         }
      } else {
         DEFAULT_CIPHERS = Collections.emptyList();
         AVAILABLE_OPENSSL_CIPHER_SUITES = Collections.emptySet();
         AVAILABLE_JAVA_CIPHER_SUITES = Collections.emptySet();
         AVAILABLE_CIPHER_SUITES = Collections.emptySet();
         SUPPORTS_KEYMANAGER_FACTORY = false;
         SUPPORTS_HOSTNAME_VALIDATION = false;
         USE_KEYMANAGER_FACTORY = false;
         SUPPORTED_PROTOCOLS_SET = Collections.emptySet();
         SUPPORTS_OCSP = false;
      }

   }
}
