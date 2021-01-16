package io.netty.handler.ssl;

import java.io.File;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLException;
import javax.net.ssl.TrustManagerFactory;

public final class OpenSslServerContext extends OpenSslContext {
   private final OpenSslServerSessionContext sessionContext;
   private final OpenSslKeyMaterialManager keyMaterialManager;

   /** @deprecated */
   @Deprecated
   public OpenSslServerContext(File var1, File var2) throws SSLException {
      this(var1, var2, (String)null);
   }

   /** @deprecated */
   @Deprecated
   public OpenSslServerContext(File var1, File var2, String var3) throws SSLException {
      this(var1, var2, var3, (Iterable)null, (CipherSuiteFilter)IdentityCipherSuiteFilter.INSTANCE, (ApplicationProtocolConfig)ApplicationProtocolConfig.DISABLED, 0L, 0L);
   }

   /** @deprecated */
   @Deprecated
   public OpenSslServerContext(File var1, File var2, String var3, Iterable<String> var4, ApplicationProtocolConfig var5, long var6, long var8) throws SSLException {
      this(var1, var2, var3, (Iterable)var4, (CipherSuiteFilter)IdentityCipherSuiteFilter.INSTANCE, (ApplicationProtocolConfig)var5, var6, var8);
   }

   /** @deprecated */
   @Deprecated
   public OpenSslServerContext(File var1, File var2, String var3, Iterable<String> var4, Iterable<String> var5, long var6, long var8) throws SSLException {
      this(var1, var2, var3, var4, toApplicationProtocolConfig(var5), var6, var8);
   }

   /** @deprecated */
   @Deprecated
   public OpenSslServerContext(File var1, File var2, String var3, TrustManagerFactory var4, Iterable<String> var5, ApplicationProtocolConfig var6, long var7, long var9) throws SSLException {
      this(var1, var2, var3, var4, var5, toNegotiator(var6), var7, var9);
   }

   /** @deprecated */
   @Deprecated
   public OpenSslServerContext(File var1, File var2, String var3, TrustManagerFactory var4, Iterable<String> var5, OpenSslApplicationProtocolNegotiator var6, long var7, long var9) throws SSLException {
      this((File)null, var4, var1, var2, var3, (KeyManagerFactory)null, var5, (CipherSuiteFilter)null, (OpenSslApplicationProtocolNegotiator)var6, var7, var9);
   }

   /** @deprecated */
   @Deprecated
   public OpenSslServerContext(File var1, File var2, String var3, Iterable<String> var4, CipherSuiteFilter var5, ApplicationProtocolConfig var6, long var7, long var9) throws SSLException {
      this((File)null, (TrustManagerFactory)null, var1, var2, var3, (KeyManagerFactory)null, var4, var5, (ApplicationProtocolConfig)var6, var7, var9);
   }

   /** @deprecated */
   @Deprecated
   public OpenSslServerContext(File var1, TrustManagerFactory var2, File var3, File var4, String var5, KeyManagerFactory var6, Iterable<String> var7, CipherSuiteFilter var8, ApplicationProtocolConfig var9, long var10, long var12) throws SSLException {
      this(var1, var2, var3, var4, var5, var6, var7, var8, toNegotiator(var9), var10, var12);
   }

   /** @deprecated */
   @Deprecated
   public OpenSslServerContext(File var1, File var2, String var3, TrustManagerFactory var4, Iterable<String> var5, CipherSuiteFilter var6, ApplicationProtocolConfig var7, long var8, long var10) throws SSLException {
      this((File)null, var4, var1, var2, var3, (KeyManagerFactory)null, var5, var6, (OpenSslApplicationProtocolNegotiator)toNegotiator(var7), var8, var10);
   }

   /** @deprecated */
   @Deprecated
   public OpenSslServerContext(File var1, File var2, String var3, TrustManagerFactory var4, Iterable<String> var5, CipherSuiteFilter var6, OpenSslApplicationProtocolNegotiator var7, long var8, long var10) throws SSLException {
      this((File)null, var4, var1, var2, var3, (KeyManagerFactory)null, var5, var6, (OpenSslApplicationProtocolNegotiator)var7, var8, var10);
   }

   /** @deprecated */
   @Deprecated
   public OpenSslServerContext(File var1, TrustManagerFactory var2, File var3, File var4, String var5, KeyManagerFactory var6, Iterable<String> var7, CipherSuiteFilter var8, OpenSslApplicationProtocolNegotiator var9, long var10, long var12) throws SSLException {
      this(toX509CertificatesInternal(var1), var2, toX509CertificatesInternal(var3), toPrivateKeyInternal(var4, var5), var5, var6, var7, var8, (OpenSslApplicationProtocolNegotiator)var9, var10, var12, ClientAuth.NONE, (String[])null, false, false);
   }

   OpenSslServerContext(X509Certificate[] var1, TrustManagerFactory var2, X509Certificate[] var3, PrivateKey var4, String var5, KeyManagerFactory var6, Iterable<String> var7, CipherSuiteFilter var8, ApplicationProtocolConfig var9, long var10, long var12, ClientAuth var14, String[] var15, boolean var16, boolean var17) throws SSLException {
      this(var1, var2, var3, var4, var5, var6, var7, var8, toNegotiator(var9), var10, var12, var14, var15, var16, var17);
   }

   private OpenSslServerContext(X509Certificate[] var1, TrustManagerFactory var2, X509Certificate[] var3, PrivateKey var4, String var5, KeyManagerFactory var6, Iterable<String> var7, CipherSuiteFilter var8, OpenSslApplicationProtocolNegotiator var9, long var10, long var12, ClientAuth var14, String[] var15, boolean var16, boolean var17) throws SSLException {
      super(var7, var8, (OpenSslApplicationProtocolNegotiator)var9, var10, var12, 1, var3, var14, var15, var16, var17);
      boolean var18 = false;

      try {
         ReferenceCountedOpenSslServerContext.ServerContext var19 = ReferenceCountedOpenSslServerContext.newSessionContext(this, this.ctx, this.engineMap, var1, var2, var3, var4, var5, var6);
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
}
