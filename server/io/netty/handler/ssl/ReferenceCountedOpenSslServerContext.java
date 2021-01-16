package io.netty.handler.ssl;

import io.netty.internal.tcnative.SSLContext;
import io.netty.internal.tcnative.SniHostNameMatcher;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLException;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509ExtendedKeyManager;
import javax.net.ssl.X509ExtendedTrustManager;
import javax.net.ssl.X509KeyManager;
import javax.net.ssl.X509TrustManager;

public final class ReferenceCountedOpenSslServerContext extends ReferenceCountedOpenSslContext {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(ReferenceCountedOpenSslServerContext.class);
   private static final byte[] ID = new byte[]{110, 101, 116, 116, 121};
   private final OpenSslServerSessionContext sessionContext;
   private final OpenSslKeyMaterialManager keyMaterialManager;

   ReferenceCountedOpenSslServerContext(X509Certificate[] var1, TrustManagerFactory var2, X509Certificate[] var3, PrivateKey var4, String var5, KeyManagerFactory var6, Iterable<String> var7, CipherSuiteFilter var8, ApplicationProtocolConfig var9, long var10, long var12, ClientAuth var14, String[] var15, boolean var16, boolean var17) throws SSLException {
      this(var1, var2, var3, var4, var5, var6, var7, var8, toNegotiator(var9), var10, var12, var14, var15, var16, var17);
   }

   private ReferenceCountedOpenSslServerContext(X509Certificate[] var1, TrustManagerFactory var2, X509Certificate[] var3, PrivateKey var4, String var5, KeyManagerFactory var6, Iterable<String> var7, CipherSuiteFilter var8, OpenSslApplicationProtocolNegotiator var9, long var10, long var12, ClientAuth var14, String[] var15, boolean var16, boolean var17) throws SSLException {
      super(var7, var8, (OpenSslApplicationProtocolNegotiator)var9, var10, var12, 1, var3, var14, var15, var16, var17, true);
      boolean var18 = false;

      try {
         ReferenceCountedOpenSslServerContext.ServerContext var19 = newSessionContext(this, this.ctx, this.engineMap, var1, var2, var3, var4, var5, var6);
         this.sessionContext = var19.sessionContext;
         this.keyMaterialManager = var19.keyMaterialManager;
         var18 = true;
      } finally {
         if (!var18) {
            this.release();
         }

      }

   }

   public OpenSslServerSessionContext sessionContext() {
      return this.sessionContext;
   }

   OpenSslKeyMaterialManager keyMaterialManager() {
      return this.keyMaterialManager;
   }

   static ReferenceCountedOpenSslServerContext.ServerContext newSessionContext(ReferenceCountedOpenSslContext var0, long var1, OpenSslEngineMap var3, X509Certificate[] var4, TrustManagerFactory var5, X509Certificate[] var6, PrivateKey var7, String var8, KeyManagerFactory var9) throws SSLException {
      ReferenceCountedOpenSslServerContext.ServerContext var10 = new ReferenceCountedOpenSslServerContext.ServerContext();

      try {
         SSLContext.setVerify(var1, 0, 10);
         if (!OpenSsl.useKeyManagerFactory()) {
            if (var9 != null) {
               throw new IllegalArgumentException("KeyManagerFactory not supported");
            }

            ObjectUtil.checkNotNull(var6, "keyCertChain");
            setKeyMaterial(var1, var6, var7, var8);
         } else {
            if (var9 == null) {
               var9 = buildKeyManagerFactory(var6, var7, var8, var9);
            }

            X509KeyManager var11 = chooseX509KeyManager(var9.getKeyManagers());
            var10.keyMaterialManager = (OpenSslKeyMaterialManager)(useExtendedKeyManager(var11) ? new OpenSslExtendedKeyMaterialManager((X509ExtendedKeyManager)var11, var8) : new OpenSslKeyMaterialManager(var11, var8));
         }
      } catch (Exception var21) {
         throw new SSLException("failed to set certificate and key", var21);
      }

      try {
         if (var4 != null) {
            var5 = buildTrustManagerFactory(var4, var5);
         } else if (var5 == null) {
            var5 = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            var5.init((KeyStore)null);
         }

         X509TrustManager var24 = chooseTrustManager(var5.getTrustManagers());
         if (useExtendedTrustManager(var24)) {
            SSLContext.setCertVerifyCallback(var1, new ReferenceCountedOpenSslServerContext.ExtendedTrustManagerVerifyCallback(var3, (X509ExtendedTrustManager)var24));
         } else {
            SSLContext.setCertVerifyCallback(var1, new ReferenceCountedOpenSslServerContext.TrustManagerVerifyCallback(var3, var24));
         }

         X509Certificate[] var12 = var24.getAcceptedIssuers();
         if (var12 != null && var12.length > 0) {
            long var13 = 0L;

            try {
               var13 = toBIO(var12);
               if (!SSLContext.setCACertificateBio(var1, var13)) {
                  throw new SSLException("unable to setup accepted issuers for trustmanager " + var24);
               }
            } finally {
               freeBio(var13);
            }
         }

         if (PlatformDependent.javaVersion() >= 8) {
            SSLContext.setSniHostnameMatcher(var1, new ReferenceCountedOpenSslServerContext.OpenSslSniHostnameMatcher(var3));
         }
      } catch (SSLException var22) {
         throw var22;
      } catch (Exception var23) {
         throw new SSLException("unable to setup trustmanager", var23);
      }

      var10.sessionContext = new OpenSslServerSessionContext(var0);
      var10.sessionContext.setSessionIdContext(ID);
      return var10;
   }

   private static final class OpenSslSniHostnameMatcher implements SniHostNameMatcher {
      private final OpenSslEngineMap engineMap;

      OpenSslSniHostnameMatcher(OpenSslEngineMap var1) {
         super();
         this.engineMap = var1;
      }

      public boolean match(long var1, String var3) {
         ReferenceCountedOpenSslEngine var4 = this.engineMap.get(var1);
         if (var4 != null) {
            return var4.checkSniHostnameMatch(var3);
         } else {
            ReferenceCountedOpenSslServerContext.logger.warn("No ReferenceCountedOpenSslEngine found for SSL pointer: {}", (Object)var1);
            return false;
         }
      }
   }

   private static final class ExtendedTrustManagerVerifyCallback extends ReferenceCountedOpenSslContext.AbstractCertificateVerifier {
      private final X509ExtendedTrustManager manager;

      ExtendedTrustManagerVerifyCallback(OpenSslEngineMap var1, X509ExtendedTrustManager var2) {
         super(var1);
         this.manager = var2;
      }

      void verify(ReferenceCountedOpenSslEngine var1, X509Certificate[] var2, String var3) throws Exception {
         this.manager.checkClientTrusted(var2, var3, var1);
      }
   }

   private static final class TrustManagerVerifyCallback extends ReferenceCountedOpenSslContext.AbstractCertificateVerifier {
      private final X509TrustManager manager;

      TrustManagerVerifyCallback(OpenSslEngineMap var1, X509TrustManager var2) {
         super(var1);
         this.manager = var2;
      }

      void verify(ReferenceCountedOpenSslEngine var1, X509Certificate[] var2, String var3) throws Exception {
         this.manager.checkClientTrusted(var2, var3);
      }
   }

   static final class ServerContext {
      OpenSslServerSessionContext sessionContext;
      OpenSslKeyMaterialManager keyMaterialManager;

      ServerContext() {
         super();
      }
   }
}
