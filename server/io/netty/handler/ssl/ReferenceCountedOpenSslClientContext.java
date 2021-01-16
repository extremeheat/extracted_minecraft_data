package io.netty.handler.ssl;

import io.netty.internal.tcnative.CertificateRequestedCallback;
import io.netty.internal.tcnative.SSLContext;
import io.netty.internal.tcnative.CertificateRequestedCallback.KeyMaterial;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.HashSet;
import java.util.Set;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509ExtendedKeyManager;
import javax.net.ssl.X509ExtendedTrustManager;
import javax.net.ssl.X509KeyManager;
import javax.net.ssl.X509TrustManager;
import javax.security.auth.x500.X500Principal;

public final class ReferenceCountedOpenSslClientContext extends ReferenceCountedOpenSslContext {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(ReferenceCountedOpenSslClientContext.class);
   private final OpenSslSessionContext sessionContext;

   ReferenceCountedOpenSslClientContext(X509Certificate[] var1, TrustManagerFactory var2, X509Certificate[] var3, PrivateKey var4, String var5, KeyManagerFactory var6, Iterable<String> var7, CipherSuiteFilter var8, ApplicationProtocolConfig var9, String[] var10, long var11, long var13, boolean var15) throws SSLException {
      super(var7, var8, (ApplicationProtocolConfig)var9, var11, var13, 0, var3, ClientAuth.NONE, var10, false, var15, true);
      boolean var16 = false;

      try {
         this.sessionContext = newSessionContext(this, this.ctx, this.engineMap, var1, var2, var3, var4, var5, var6);
         var16 = true;
      } finally {
         if (!var16) {
            this.release();
         }

      }

   }

   OpenSslKeyMaterialManager keyMaterialManager() {
      return null;
   }

   public OpenSslSessionContext sessionContext() {
      return this.sessionContext;
   }

   static OpenSslSessionContext newSessionContext(ReferenceCountedOpenSslContext var0, long var1, OpenSslEngineMap var3, X509Certificate[] var4, TrustManagerFactory var5, X509Certificate[] var6, PrivateKey var7, String var8, KeyManagerFactory var9) throws SSLException {
      if ((var7 != null || var6 == null) && (var7 == null || var6 != null)) {
         try {
            if (!OpenSsl.useKeyManagerFactory()) {
               if (var9 != null) {
                  throw new IllegalArgumentException("KeyManagerFactory not supported");
               }

               if (var6 != null) {
                  setKeyMaterial(var1, var6, var7, var8);
               }
            } else {
               if (var9 == null && var6 != null) {
                  var9 = buildKeyManagerFactory(var6, var7, var8, var9);
               }

               if (var9 != null) {
                  X509KeyManager var10 = chooseX509KeyManager(var9.getKeyManagers());
                  Object var11 = useExtendedKeyManager(var10) ? new OpenSslExtendedKeyMaterialManager((X509ExtendedKeyManager)var10, var8) : new OpenSslKeyMaterialManager(var10, var8);
                  SSLContext.setCertRequestedCallback(var1, new ReferenceCountedOpenSslClientContext.OpenSslCertificateRequestedCallback(var3, (OpenSslKeyMaterialManager)var11));
               }
            }
         } catch (Exception var13) {
            throw new SSLException("failed to set certificate and key", var13);
         }

         SSLContext.setVerify(var1, 0, 10);

         try {
            if (var4 != null) {
               var5 = buildTrustManagerFactory(var4, var5);
            } else if (var5 == null) {
               var5 = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
               var5.init((KeyStore)null);
            }

            X509TrustManager var14 = chooseTrustManager(var5.getTrustManagers());
            if (useExtendedTrustManager(var14)) {
               SSLContext.setCertVerifyCallback(var1, new ReferenceCountedOpenSslClientContext.ExtendedTrustManagerVerifyCallback(var3, (X509ExtendedTrustManager)var14));
            } else {
               SSLContext.setCertVerifyCallback(var1, new ReferenceCountedOpenSslClientContext.TrustManagerVerifyCallback(var3, var14));
            }
         } catch (Exception var12) {
            throw new SSLException("unable to setup trustmanager", var12);
         }

         return new ReferenceCountedOpenSslClientContext.OpenSslClientSessionContext(var0);
      } else {
         throw new IllegalArgumentException("Either both keyCertChain and key needs to be null or none of them");
      }
   }

   private static final class OpenSslCertificateRequestedCallback implements CertificateRequestedCallback {
      private final OpenSslEngineMap engineMap;
      private final OpenSslKeyMaterialManager keyManagerHolder;

      OpenSslCertificateRequestedCallback(OpenSslEngineMap var1, OpenSslKeyMaterialManager var2) {
         super();
         this.engineMap = var1;
         this.keyManagerHolder = var2;
      }

      public KeyMaterial requested(long var1, byte[] var3, byte[][] var4) {
         ReferenceCountedOpenSslEngine var5 = this.engineMap.get(var1);

         try {
            Set var6 = supportedClientKeyTypes(var3);
            String[] var11 = (String[])var6.toArray(new String[var6.size()]);
            X500Principal[] var8;
            if (var4 == null) {
               var8 = null;
            } else {
               var8 = new X500Principal[var4.length];

               for(int var9 = 0; var9 < var4.length; ++var9) {
                  var8[var9] = new X500Principal(var4[var9]);
               }
            }

            return this.keyManagerHolder.keyMaterial(var5, var11, var8);
         } catch (Throwable var10) {
            ReferenceCountedOpenSslClientContext.logger.debug("request of key failed", var10);
            SSLHandshakeException var7 = new SSLHandshakeException("General OpenSslEngine problem");
            var7.initCause(var10);
            var5.handshakeException = var7;
            return null;
         }
      }

      private static Set<String> supportedClientKeyTypes(byte[] var0) {
         HashSet var1 = new HashSet(var0.length);
         byte[] var2 = var0;
         int var3 = var0.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            byte var5 = var2[var4];
            String var6 = clientKeyType(var5);
            if (var6 != null) {
               var1.add(var6);
            }
         }

         return var1;
      }

      private static String clientKeyType(byte var0) {
         switch(var0) {
         case 1:
            return "RSA";
         case 3:
            return "DH_RSA";
         case 64:
            return "EC";
         case 65:
            return "EC_RSA";
         case 66:
            return "EC_EC";
         default:
            return null;
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
         this.manager.checkServerTrusted(var2, var3, var1);
      }
   }

   private static final class TrustManagerVerifyCallback extends ReferenceCountedOpenSslContext.AbstractCertificateVerifier {
      private final X509TrustManager manager;

      TrustManagerVerifyCallback(OpenSslEngineMap var1, X509TrustManager var2) {
         super(var1);
         this.manager = var2;
      }

      void verify(ReferenceCountedOpenSslEngine var1, X509Certificate[] var2, String var3) throws Exception {
         this.manager.checkServerTrusted(var2, var3);
      }
   }

   static final class OpenSslClientSessionContext extends OpenSslSessionContext {
      OpenSslClientSessionContext(ReferenceCountedOpenSslContext var1) {
         super(var1);
      }

      public void setSessionTimeout(int var1) {
         if (var1 < 0) {
            throw new IllegalArgumentException();
         }
      }

      public int getSessionTimeout() {
         return 0;
      }

      public void setSessionCacheSize(int var1) {
         if (var1 < 0) {
            throw new IllegalArgumentException();
         }
      }

      public int getSessionCacheSize() {
         return 0;
      }

      public void setSessionCacheEnabled(boolean var1) {
      }

      public boolean isSessionCacheEnabled() {
         return false;
      }
   }
}
