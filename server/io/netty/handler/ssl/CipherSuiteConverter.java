package io.netty.handler.ssl;

import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class CipherSuiteConverter {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(CipherSuiteConverter.class);
   private static final Pattern JAVA_CIPHERSUITE_PATTERN = Pattern.compile("^(?:TLS|SSL)_((?:(?!_WITH_).)+)_WITH_(.*)_(.*)$");
   private static final Pattern OPENSSL_CIPHERSUITE_PATTERN = Pattern.compile("^(?:((?:(?:EXP-)?(?:(?:DHE|EDH|ECDH|ECDHE|SRP|RSA)-(?:DSS|RSA|ECDSA|PSK)|(?:ADH|AECDH|KRB5|PSK|SRP)))|EXP)-)?(.*)-(.*)$");
   private static final Pattern JAVA_AES_CBC_PATTERN = Pattern.compile("^(AES)_([0-9]+)_CBC$");
   private static final Pattern JAVA_AES_PATTERN = Pattern.compile("^(AES)_([0-9]+)_(.*)$");
   private static final Pattern OPENSSL_AES_CBC_PATTERN = Pattern.compile("^(AES)([0-9]+)$");
   private static final Pattern OPENSSL_AES_PATTERN = Pattern.compile("^(AES)([0-9]+)-(.*)$");
   private static final ConcurrentMap<String, String> j2o = PlatformDependent.newConcurrentHashMap();
   private static final ConcurrentMap<String, Map<String, String>> o2j = PlatformDependent.newConcurrentHashMap();

   static void clearCache() {
      j2o.clear();
      o2j.clear();
   }

   static boolean isJ2OCached(String var0, String var1) {
      return var1.equals(j2o.get(var0));
   }

   static boolean isO2JCached(String var0, String var1, String var2) {
      Map var3 = (Map)o2j.get(var0);
      return var3 == null ? false : var2.equals(var3.get(var1));
   }

   static String toOpenSsl(Iterable<String> var0) {
      StringBuilder var1 = new StringBuilder();
      Iterator var2 = var0.iterator();

      while(var2.hasNext()) {
         String var3 = (String)var2.next();
         if (var3 == null) {
            break;
         }

         String var4 = toOpenSsl(var3);
         if (var4 != null) {
            var3 = var4;
         }

         var1.append(var3);
         var1.append(':');
      }

      if (var1.length() > 0) {
         var1.setLength(var1.length() - 1);
         return var1.toString();
      } else {
         return "";
      }
   }

   static String toOpenSsl(String var0) {
      String var1 = (String)j2o.get(var0);
      return var1 != null ? var1 : cacheFromJava(var0);
   }

   private static String cacheFromJava(String var0) {
      String var1 = toOpenSslUncached(var0);
      if (var1 == null) {
         return null;
      } else {
         j2o.putIfAbsent(var0, var1);
         String var2 = var0.substring(4);
         HashMap var3 = new HashMap(4);
         var3.put("", var2);
         var3.put("SSL", "SSL_" + var2);
         var3.put("TLS", "TLS_" + var2);
         o2j.put(var1, var3);
         logger.debug("Cipher suite mapping: {} => {}", var0, var1);
         return var1;
      }
   }

   static String toOpenSslUncached(String var0) {
      Matcher var1 = JAVA_CIPHERSUITE_PATTERN.matcher(var0);
      if (!var1.matches()) {
         return null;
      } else {
         String var2 = toOpenSslHandshakeAlgo(var1.group(1));
         String var3 = toOpenSslBulkCipher(var1.group(2));
         String var4 = toOpenSslHmacAlgo(var1.group(3));
         if (var2.isEmpty()) {
            return var3 + '-' + var4;
         } else {
            return var3.contains("CHACHA20") ? var2 + '-' + var3 : var2 + '-' + var3 + '-' + var4;
         }
      }
   }

   private static String toOpenSslHandshakeAlgo(String var0) {
      boolean var1 = var0.endsWith("_EXPORT");
      if (var1) {
         var0 = var0.substring(0, var0.length() - 7);
      }

      if ("RSA".equals(var0)) {
         var0 = "";
      } else if (var0.endsWith("_anon")) {
         var0 = 'A' + var0.substring(0, var0.length() - 5);
      }

      if (var1) {
         if (var0.isEmpty()) {
            var0 = "EXP";
         } else {
            var0 = "EXP-" + var0;
         }
      }

      return var0.replace('_', '-');
   }

   private static String toOpenSslBulkCipher(String var0) {
      if (var0.startsWith("AES_")) {
         Matcher var1 = JAVA_AES_CBC_PATTERN.matcher(var0);
         if (var1.matches()) {
            return var1.replaceFirst("$1$2");
         }

         var1 = JAVA_AES_PATTERN.matcher(var0);
         if (var1.matches()) {
            return var1.replaceFirst("$1$2-$3");
         }
      }

      if ("3DES_EDE_CBC".equals(var0)) {
         return "DES-CBC3";
      } else if (!"RC4_128".equals(var0) && !"RC4_40".equals(var0)) {
         if (!"DES40_CBC".equals(var0) && !"DES_CBC_40".equals(var0)) {
            return "RC2_CBC_40".equals(var0) ? "RC2-CBC" : var0.replace('_', '-');
         } else {
            return "DES-CBC";
         }
      } else {
         return "RC4";
      }
   }

   private static String toOpenSslHmacAlgo(String var0) {
      return var0;
   }

   static String toJava(String var0, String var1) {
      Map var2 = (Map)o2j.get(var0);
      if (var2 == null) {
         var2 = cacheFromOpenSsl(var0);
         if (var2 == null) {
            return null;
         }
      }

      String var3 = (String)var2.get(var1);
      if (var3 == null) {
         var3 = var1 + '_' + (String)var2.get("");
      }

      return var3;
   }

   private static Map<String, String> cacheFromOpenSsl(String var0) {
      String var1 = toJavaUncached(var0);
      if (var1 == null) {
         return null;
      } else {
         String var2 = "SSL_" + var1;
         String var3 = "TLS_" + var1;
         HashMap var4 = new HashMap(4);
         var4.put("", var1);
         var4.put("SSL", var2);
         var4.put("TLS", var3);
         o2j.putIfAbsent(var0, var4);
         j2o.putIfAbsent(var3, var0);
         j2o.putIfAbsent(var2, var0);
         logger.debug("Cipher suite mapping: {} => {}", var3, var0);
         logger.debug("Cipher suite mapping: {} => {}", var2, var0);
         return var4;
      }
   }

   static String toJavaUncached(String var0) {
      Matcher var1 = OPENSSL_CIPHERSUITE_PATTERN.matcher(var0);
      if (!var1.matches()) {
         return null;
      } else {
         String var2 = var1.group(1);
         boolean var3;
         if (var2 == null) {
            var2 = "";
            var3 = false;
         } else if (var2.startsWith("EXP-")) {
            var2 = var2.substring(4);
            var3 = true;
         } else if ("EXP".equals(var2)) {
            var2 = "";
            var3 = true;
         } else {
            var3 = false;
         }

         var2 = toJavaHandshakeAlgo(var2, var3);
         String var4 = toJavaBulkCipher(var1.group(2), var3);
         String var5 = toJavaHmacAlgo(var1.group(3));
         String var6 = var2 + "_WITH_" + var4 + '_' + var5;
         return var4.contains("CHACHA20") ? var6 + "_SHA256" : var6;
      }
   }

   private static String toJavaHandshakeAlgo(String var0, boolean var1) {
      if (var0.isEmpty()) {
         var0 = "RSA";
      } else if ("ADH".equals(var0)) {
         var0 = "DH_anon";
      } else if ("AECDH".equals(var0)) {
         var0 = "ECDH_anon";
      }

      var0 = var0.replace('-', '_');
      return var1 ? var0 + "_EXPORT" : var0;
   }

   private static String toJavaBulkCipher(String var0, boolean var1) {
      if (var0.startsWith("AES")) {
         Matcher var2 = OPENSSL_AES_CBC_PATTERN.matcher(var0);
         if (var2.matches()) {
            return var2.replaceFirst("$1_$2_CBC");
         }

         var2 = OPENSSL_AES_PATTERN.matcher(var0);
         if (var2.matches()) {
            return var2.replaceFirst("$1_$2_$3");
         }
      }

      if ("DES-CBC3".equals(var0)) {
         return "3DES_EDE_CBC";
      } else if ("RC4".equals(var0)) {
         return var1 ? "RC4_40" : "RC4_128";
      } else if ("DES-CBC".equals(var0)) {
         return var1 ? "DES_CBC_40" : "DES_CBC";
      } else if ("RC2-CBC".equals(var0)) {
         return var1 ? "RC2_CBC_40" : "RC2_CBC";
      } else {
         return var0.replace('-', '_');
      }
   }

   private static String toJavaHmacAlgo(String var0) {
      return var0;
   }

   private CipherSuiteConverter() {
      super();
   }
}
