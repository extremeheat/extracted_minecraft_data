package io.netty.handler.ssl;

import io.netty.util.internal.PlatformDependent;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.net.ssl.SSLEngine;

final class Conscrypt {
   private static final Method IS_CONSCRYPT_SSLENGINE = loadIsConscryptEngine();

   private static Method loadIsConscryptEngine() {
      try {
         Class var0 = Class.forName("org.conscrypt.Conscrypt", true, ConscryptAlpnSslEngine.class.getClassLoader());
         return var0.getMethod("isConscrypt", SSLEngine.class);
      } catch (Throwable var1) {
         return null;
      }
   }

   static boolean isAvailable() {
      return IS_CONSCRYPT_SSLENGINE != null && PlatformDependent.javaVersion() >= 8;
   }

   static boolean isEngineSupported(SSLEngine var0) {
      return isAvailable() && isConscryptEngine(var0);
   }

   private static boolean isConscryptEngine(SSLEngine var0) {
      try {
         return (Boolean)IS_CONSCRYPT_SSLENGINE.invoke((Object)null, var0);
      } catch (IllegalAccessException var2) {
         return false;
      } catch (InvocationTargetException var3) {
         throw new RuntimeException(var3);
      }
   }

   private Conscrypt() {
      super();
   }
}
