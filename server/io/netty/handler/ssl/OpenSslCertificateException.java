package io.netty.handler.ssl;

import io.netty.internal.tcnative.CertificateVerifier;
import java.security.cert.CertificateException;

public final class OpenSslCertificateException extends CertificateException {
   private static final long serialVersionUID = 5542675253797129798L;
   private final int errorCode;

   public OpenSslCertificateException(int var1) {
      this((String)null, var1);
   }

   public OpenSslCertificateException(String var1, int var2) {
      super(var1);
      this.errorCode = checkErrorCode(var2);
   }

   public OpenSslCertificateException(String var1, Throwable var2, int var3) {
      super(var1, var2);
      this.errorCode = checkErrorCode(var3);
   }

   public OpenSslCertificateException(Throwable var1, int var2) {
      this((String)null, var1, var2);
   }

   public int errorCode() {
      return this.errorCode;
   }

   private static int checkErrorCode(int var0) {
      if (!CertificateVerifier.isValid(var0)) {
         throw new IllegalArgumentException("errorCode '" + var0 + "' invalid, see https://www.openssl.org/docs/man1.0.2/apps/verify.html.");
      } else {
         return var0;
      }
   }
}
