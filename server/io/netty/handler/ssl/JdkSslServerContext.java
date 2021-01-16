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
public final class JdkSslServerContext extends JdkSslContext {
   /** @deprecated */
   @Deprecated
   public JdkSslServerContext(File var1, File var2) throws SSLException {
      this(var1, var2, (String)null);
   }

   /** @deprecated */
   @Deprecated
   public JdkSslServerContext(File var1, File var2, String var3) throws SSLException {
      this(var1, var2, var3, (Iterable)null, IdentityCipherSuiteFilter.INSTANCE, (JdkApplicationProtocolNegotiator)JdkDefaultApplicationProtocolNegotiator.INSTANCE, 0L, 0L);
   }

   /** @deprecated */
   @Deprecated
   public JdkSslServerContext(File var1, File var2, String var3, Iterable<String> var4, Iterable<String> var5, long var6, long var8) throws SSLException {
      this(var1, var2, var3, var4, IdentityCipherSuiteFilter.INSTANCE, (JdkApplicationProtocolNegotiator)toNegotiator(toApplicationProtocolConfig(var5), true), var6, var8);
   }

   /** @deprecated */
   @Deprecated
   public JdkSslServerContext(File var1, File var2, String var3, Iterable<String> var4, CipherSuiteFilter var5, ApplicationProtocolConfig var6, long var7, long var9) throws SSLException {
      this(var1, var2, var3, var4, var5, toNegotiator(var6, true), var7, var9);
   }

   /** @deprecated */
   @Deprecated
   public JdkSslServerContext(File var1, File var2, String var3, Iterable<String> var4, CipherSuiteFilter var5, JdkApplicationProtocolNegotiator var6, long var7, long var9) throws SSLException {
      this((Provider)null, var1, var2, var3, var4, var5, var6, var7, var9);
   }

   JdkSslServerContext(Provider var1, File var2, File var3, String var4, Iterable<String> var5, CipherSuiteFilter var6, JdkApplicationProtocolNegotiator var7, long var8, long var10) throws SSLException {
      super(newSSLContext(var1, (X509Certificate[])null, (TrustManagerFactory)null, toX509CertificatesInternal(var2), toPrivateKeyInternal(var3, var4), var4, (KeyManagerFactory)null, var8, var10), false, var5, var6, var7, ClientAuth.NONE, (String[])null, false);
   }

   /** @deprecated */
   @Deprecated
   public JdkSslServerContext(File var1, TrustManagerFactory var2, File var3, File var4, String var5, KeyManagerFactory var6, Iterable<String> var7, CipherSuiteFilter var8, ApplicationProtocolConfig var9, long var10, long var12) throws SSLException {
      this(var1, var2, var3, var4, var5, var6, var7, var8, toNegotiator(var9, true), var10, var12);
   }

   /** @deprecated */
   @Deprecated
   public JdkSslServerContext(File var1, TrustManagerFactory var2, File var3, File var4, String var5, KeyManagerFactory var6, Iterable<String> var7, CipherSuiteFilter var8, JdkApplicationProtocolNegotiator var9, long var10, long var12) throws SSLException {
      super(newSSLContext((Provider)null, toX509CertificatesInternal(var1), var2, toX509CertificatesInternal(var3), toPrivateKeyInternal(var4, var5), var5, var6, var10, var12), false, var7, var8, var9, ClientAuth.NONE, (String[])null, false);
   }

   JdkSslServerContext(Provider var1, X509Certificate[] var2, TrustManagerFactory var3, X509Certificate[] var4, PrivateKey var5, String var6, KeyManagerFactory var7, Iterable<String> var8, CipherSuiteFilter var9, ApplicationProtocolConfig var10, long var11, long var13, ClientAuth var15, String[] var16, boolean var17) throws SSLException {
      super(newSSLContext(var1, var2, var3, var4, var5, var6, var7, var11, var13), false, var8, var9, toNegotiator(var10, true), var15, var16, var17);
   }

   private static SSLContext newSSLContext(Provider var0, X509Certificate[] var1, TrustManagerFactory var2, X509Certificate[] var3, PrivateKey var4, String var5, KeyManagerFactory var6, long var7, long var9) throws SSLException {
      if (var4 == null && var6 == null) {
         throw new NullPointerException("key, keyManagerFactory");
      } else {
         try {
            if (var1 != null) {
               var2 = buildTrustManagerFactory(var1, var2);
            }

            if (var4 != null) {
               var6 = buildKeyManagerFactory(var3, var4, var5, var6);
            }

            SSLContext var11 = var0 == null ? SSLContext.getInstance("TLS") : SSLContext.getInstance("TLS", var0);
            var11.init(var6.getKeyManagers(), var2 == null ? null : var2.getTrustManagers(), (SecureRandom)null);
            SSLSessionContext var12 = var11.getServerSessionContext();
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
               throw new SSLException("failed to initialize the server-side SSL context", var13);
            }
         }
      }
   }
}
