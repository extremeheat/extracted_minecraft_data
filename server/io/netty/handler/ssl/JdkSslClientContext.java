package io.netty.handler.ssl;

import java.io.File;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSessionContext;
import javax.net.ssl.TrustManagerFactory;

/** @deprecated */
@Deprecated
public final class JdkSslClientContext extends JdkSslContext {
   /** @deprecated */
   @Deprecated
   public JdkSslClientContext() throws SSLException {
      this((File)null, (TrustManagerFactory)null);
   }

   /** @deprecated */
   @Deprecated
   public JdkSslClientContext(File var1) throws SSLException {
      this(var1, (TrustManagerFactory)null);
   }

   /** @deprecated */
   @Deprecated
   public JdkSslClientContext(TrustManagerFactory var1) throws SSLException {
      this((File)null, var1);
   }

   /** @deprecated */
   @Deprecated
   public JdkSslClientContext(File var1, TrustManagerFactory var2) throws SSLException {
      this(var1, var2, (Iterable)null, IdentityCipherSuiteFilter.INSTANCE, (JdkApplicationProtocolNegotiator)JdkDefaultApplicationProtocolNegotiator.INSTANCE, 0L, 0L);
   }

   /** @deprecated */
   @Deprecated
   public JdkSslClientContext(File var1, TrustManagerFactory var2, Iterable<String> var3, Iterable<String> var4, long var5, long var7) throws SSLException {
      this(var1, var2, var3, IdentityCipherSuiteFilter.INSTANCE, (JdkApplicationProtocolNegotiator)toNegotiator(toApplicationProtocolConfig(var4), false), var5, var7);
   }

   /** @deprecated */
   @Deprecated
   public JdkSslClientContext(File var1, TrustManagerFactory var2, Iterable<String> var3, CipherSuiteFilter var4, ApplicationProtocolConfig var5, long var6, long var8) throws SSLException {
      this(var1, var2, var3, var4, toNegotiator(var5, false), var6, var8);
   }

   /** @deprecated */
   @Deprecated
   public JdkSslClientContext(File var1, TrustManagerFactory var2, Iterable<String> var3, CipherSuiteFilter var4, JdkApplicationProtocolNegotiator var5, long var6, long var8) throws SSLException {
      this((Provider)null, var1, var2, var3, var4, var5, var6, var8);
   }

   JdkSslClientContext(Provider var1, File var2, TrustManagerFactory var3, Iterable<String> var4, CipherSuiteFilter var5, JdkApplicationProtocolNegotiator var6, long var7, long var9) throws SSLException {
      super(newSSLContext(var1, toX509CertificatesInternal(var2), var3, (X509Certificate[])null, (PrivateKey)null, (String)null, (KeyManagerFactory)null, var7, var9), true, var4, var5, var6, ClientAuth.NONE, (String[])null, false);
   }

   /** @deprecated */
   @Deprecated
   public JdkSslClientContext(File var1, TrustManagerFactory var2, File var3, File var4, String var5, KeyManagerFactory var6, Iterable<String> var7, CipherSuiteFilter var8, ApplicationProtocolConfig var9, long var10, long var12) throws SSLException {
      this(var1, var2, var3, var4, var5, var6, var7, var8, toNegotiator(var9, false), var10, var12);
   }

   /** @deprecated */
   @Deprecated
   public JdkSslClientContext(File var1, TrustManagerFactory var2, File var3, File var4, String var5, KeyManagerFactory var6, Iterable<String> var7, CipherSuiteFilter var8, JdkApplicationProtocolNegotiator var9, long var10, long var12) throws SSLException {
      super(newSSLContext((Provider)null, toX509CertificatesInternal(var1), var2, toX509CertificatesInternal(var3), toPrivateKeyInternal(var4, var5), var5, var6, var10, var12), true, var7, var8, var9, ClientAuth.NONE, (String[])null, false);
   }

   JdkSslClientContext(Provider var1, X509Certificate[] var2, TrustManagerFactory var3, X509Certificate[] var4, PrivateKey var5, String var6, KeyManagerFactory var7, Iterable<String> var8, CipherSuiteFilter var9, ApplicationProtocolConfig var10, String[] var11, long var12, long var14) throws SSLException {
      super(newSSLContext(var1, var2, var3, var4, var5, var6, var7, var12, var14), true, var8, var9, toNegotiator(var10, false), ClientAuth.NONE, var11, false);
   }

   private static SSLContext newSSLContext(Provider var0, X509Certificate[] var1, TrustManagerFactory var2, X509Certificate[] var3, PrivateKey var4, String var5, KeyManagerFactory var6, long var7, long var9) throws SSLException {
      try {
         if (var1 != null) {
            var2 = buildTrustManagerFactory(var1, var2);
         }

         if (var3 != null) {
            var6 = buildKeyManagerFactory(var3, var4, var5, var6);
         }

         SSLContext var11 = var0 == null ? SSLContext.getInstance("TLS") : SSLContext.getInstance("TLS", var0);
         var11.init(var6 == null ? null : var6.getKeyManagers(), var2 == null ? null : var2.getTrustManagers(), (SecureRandom)null);
         SSLSessionContext var12 = var11.getClientSessionContext();
         if (var7 > 0L) {
            var12.setSessionCacheSize((int)Math.min(var7, 2147483647L));
         }

         if (var9 > 0L) {
            var12.setSessionTimeout((int)Math.min(var9, 2147483647L));
         }

         return var11;
      } catch (Exception var13) {
         if (var13 instanceof SSLException) {
            throw (SSLException)var13;
         } else {
            throw new SSLException("failed to initialize the client-side SSL context", var13);
         }
      }
   }
}
