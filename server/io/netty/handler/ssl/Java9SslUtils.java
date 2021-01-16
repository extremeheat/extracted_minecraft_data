package io.netty.handler.ssl;

import io.netty.util.internal.EmptyArrays;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.security.SecureRandom;
import java.util.List;
import java.util.function.BiFunction;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.TrustManager;

final class Java9SslUtils {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(Java9SslUtils.class);
   private static final Method SET_APPLICATION_PROTOCOLS;
   private static final Method GET_APPLICATION_PROTOCOL;
   private static final Method GET_HANDSHAKE_APPLICATION_PROTOCOL;
   private static final Method SET_HANDSHAKE_APPLICATION_PROTOCOL_SELECTOR;
   private static final Method GET_HANDSHAKE_APPLICATION_PROTOCOL_SELECTOR;

   private Java9SslUtils() {
      super();
   }

   static boolean supportsAlpn() {
      return GET_APPLICATION_PROTOCOL != null;
   }

   static String getApplicationProtocol(SSLEngine var0) {
      try {
         return (String)GET_APPLICATION_PROTOCOL.invoke(var0);
      } catch (UnsupportedOperationException var2) {
         throw var2;
      } catch (Exception var3) {
         throw new IllegalStateException(var3);
      }
   }

   static String getHandshakeApplicationProtocol(SSLEngine var0) {
      try {
         return (String)GET_HANDSHAKE_APPLICATION_PROTOCOL.invoke(var0);
      } catch (UnsupportedOperationException var2) {
         throw var2;
      } catch (Exception var3) {
         throw new IllegalStateException(var3);
      }
   }

   static void setApplicationProtocols(SSLEngine var0, List<String> var1) {
      SSLParameters var2 = var0.getSSLParameters();
      String[] var3 = (String[])var1.toArray(EmptyArrays.EMPTY_STRINGS);

      try {
         SET_APPLICATION_PROTOCOLS.invoke(var2, var3);
      } catch (UnsupportedOperationException var5) {
         throw var5;
      } catch (Exception var6) {
         throw new IllegalStateException(var6);
      }

      var0.setSSLParameters(var2);
   }

   static void setHandshakeApplicationProtocolSelector(SSLEngine var0, BiFunction<SSLEngine, List<String>, String> var1) {
      try {
         SET_HANDSHAKE_APPLICATION_PROTOCOL_SELECTOR.invoke(var0, var1);
      } catch (UnsupportedOperationException var3) {
         throw var3;
      } catch (Exception var4) {
         throw new IllegalStateException(var4);
      }
   }

   static BiFunction<SSLEngine, List<String>, String> getHandshakeApplicationProtocolSelector(SSLEngine var0) {
      try {
         return (BiFunction)GET_HANDSHAKE_APPLICATION_PROTOCOL_SELECTOR.invoke(var0);
      } catch (UnsupportedOperationException var2) {
         throw var2;
      } catch (Exception var3) {
         throw new IllegalStateException(var3);
      }
   }

   static {
      Method var0 = null;
      Method var1 = null;
      Method var2 = null;
      Method var3 = null;
      Method var4 = null;

      try {
         SSLContext var5 = SSLContext.getInstance("TLS");
         var5.init((KeyManager[])null, (TrustManager[])null, (SecureRandom)null);
         SSLEngine var6 = var5.createSSLEngine();
         var0 = (Method)AccessController.doPrivileged(new PrivilegedExceptionAction<Method>() {
            public Method run() throws Exception {
               return SSLEngine.class.getMethod("getHandshakeApplicationProtocol");
            }
         });
         var0.invoke(var6);
         var1 = (Method)AccessController.doPrivileged(new PrivilegedExceptionAction<Method>() {
            public Method run() throws Exception {
               return SSLEngine.class.getMethod("getApplicationProtocol");
            }
         });
         var1.invoke(var6);
         var2 = (Method)AccessController.doPrivileged(new PrivilegedExceptionAction<Method>() {
            public Method run() throws Exception {
               return SSLParameters.class.getMethod("setApplicationProtocols", String[].class);
            }
         });
         var2.invoke(var6.getSSLParameters(), EmptyArrays.EMPTY_STRINGS);
         var3 = (Method)AccessController.doPrivileged(new PrivilegedExceptionAction<Method>() {
            public Method run() throws Exception {
               return SSLEngine.class.getMethod("setHandshakeApplicationProtocolSelector", BiFunction.class);
            }
         });
         var3.invoke(var6, new BiFunction<SSLEngine, List<String>, String>() {
            public String apply(SSLEngine var1, List<String> var2) {
               return null;
            }
         });
         var4 = (Method)AccessController.doPrivileged(new PrivilegedExceptionAction<Method>() {
            public Method run() throws Exception {
               return SSLEngine.class.getMethod("getHandshakeApplicationProtocolSelector");
            }
         });
         var4.invoke(var6);
      } catch (Throwable var7) {
         logger.error("Unable to initialize Java9SslUtils, but the detected javaVersion was: {}", PlatformDependent.javaVersion(), var7);
         var0 = null;
         var1 = null;
         var2 = null;
         var3 = null;
         var4 = null;
      }

      GET_HANDSHAKE_APPLICATION_PROTOCOL = var0;
      GET_APPLICATION_PROTOCOL = var1;
      SET_APPLICATION_PROTOCOLS = var2;
      SET_HANDSHAKE_APPLICATION_PROTOCOL_SELECTOR = var3;
      GET_HANDSHAKE_APPLICATION_PROTOCOL_SELECTOR = var4;
   }
}
