package io.netty.handler.ssl;

import java.io.File;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLException;
import javax.net.ssl.TrustManagerFactory;

public final class OpenSslClientContext extends OpenSslContext {
   private final OpenSslSessionContext sessionContext;

   /** @deprecated */
   @Deprecated
   public OpenSslClientContext() throws SSLException {
      this((File)null, (TrustManagerFactory)null, (File)null, (File)null, (String)null, (KeyManagerFactory)null, (Iterable)null, IdentityCipherSuiteFilter.INSTANCE, (ApplicationProtocolConfig)null, 0L, 0L);
   }

   /** @deprecated */
   @Deprecated
   public OpenSslClientContext(File var1) throws SSLException {
      this(var1, (TrustManagerFactory)null);
   }

   /** @deprecated */
   @Deprecated
   public OpenSslClientContext(TrustManagerFactory var1) throws SSLException {
      this((File)null, var1);
   }

   /** @deprecated */
   @Deprecated
   public OpenSslClientContext(File var1, TrustManagerFactory var2) throws SSLException {
      this(var1, var2, (File)null, (File)null, (String)null, (KeyManagerFactory)null, (Iterable)null, IdentityCipherSuiteFilter.INSTANCE, (ApplicationProtocolConfig)null, 0L, 0L);
   }

   /** @deprecated */
   @Deprecated
   public OpenSslClientContext(File var1, TrustManagerFactory var2, Iterable<String> var3, ApplicationProtocolConfig var4, long var5, long var7) throws SSLException {
      this(var1, var2, (File)null, (File)null, (String)null, (KeyManagerFactory)null, var3, IdentityCipherSuiteFilter.INSTANCE, var4, var5, var7);
   }

   /** @deprecated */
   @Deprecated
   public OpenSslClientContext(File var1, TrustManagerFactory var2, Iterable<String> var3, CipherSuiteFilter var4, ApplicationProtocolConfig var5, long var6, long var8) throws SSLException {
      this(var1, var2, (File)null, (File)null, (String)null, (KeyManagerFactory)null, var3, var4, var5, var6, var8);
   }

   /** @deprecated */
   @Deprecated
   public OpenSslClientContext(File var1, TrustManagerFactory var2, File var3, File var4, String var5, KeyManagerFactory var6, Iterable<String> var7, CipherSuiteFilter var8, ApplicationProtocolConfig var9, long var10, long var12) throws SSLException {
      this(toX509CertificatesInternal(var1), var2, toX509CertificatesInternal(var3), toPrivateKeyInternal(var4, var5), var5, var6, var7, var8, var9, (String[])null, var10, var12, false);
   }

   OpenSslClientContext(X509Certificate[] var1, TrustManagerFactory var2, X509Certificate[] var3, PrivateKey var4, String var5, KeyManagerFactory var6, Iterable<String> var7, CipherSuiteFilter var8, ApplicationProtocolConfig var9, String[] var10, long var11, long var13, boolean var15) throws SSLException {
      super(var7, var8, (ApplicationProtocolConfig)var9, var11, var13, 0, var3, ClientAuth.NONE, var10, false, var15);
      boolean var16 = false;

      try {
         this.sessionContext = ReferenceCountedOpenSslClientContext.newSessionContext(this, this.ctx, this.engineMap, var1, var2, var3, var4, var5, var6);
         var16 = true;
      } finally {
         if (!var16) {
            this.release();
         }

      }

   }

   public OpenSslSessionContext sessionContext() {
      return this.sessionContext;
   }

   OpenSslKeyMaterialManager keyMaterialManager() {
      return null;
   }
}
