package io.netty.handler.ssl.util;

import io.netty.util.internal.ObjectUtil;
import java.net.Socket;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.X509ExtendedTrustManager;
import javax.net.ssl.X509TrustManager;

final class X509TrustManagerWrapper extends X509ExtendedTrustManager {
   private final X509TrustManager delegate;

   X509TrustManagerWrapper(X509TrustManager var1) {
      super();
      this.delegate = (X509TrustManager)ObjectUtil.checkNotNull(var1, "delegate");
   }

   public void checkClientTrusted(X509Certificate[] var1, String var2) throws CertificateException {
      this.delegate.checkClientTrusted(var1, var2);
   }

   public void checkClientTrusted(X509Certificate[] var1, String var2, Socket var3) throws CertificateException {
      this.delegate.checkClientTrusted(var1, var2);
   }

   public void checkClientTrusted(X509Certificate[] var1, String var2, SSLEngine var3) throws CertificateException {
      this.delegate.checkClientTrusted(var1, var2);
   }

   public void checkServerTrusted(X509Certificate[] var1, String var2) throws CertificateException {
      this.delegate.checkServerTrusted(var1, var2);
   }

   public void checkServerTrusted(X509Certificate[] var1, String var2, Socket var3) throws CertificateException {
      this.delegate.checkServerTrusted(var1, var2);
   }

   public void checkServerTrusted(X509Certificate[] var1, String var2, SSLEngine var3) throws CertificateException {
      this.delegate.checkServerTrusted(var1, var2);
   }

   public X509Certificate[] getAcceptedIssuers() {
      return this.delegate.getAcceptedIssuers();
   }
}
