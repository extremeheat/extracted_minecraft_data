package io.netty.handler.ssl;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Principal;
import java.security.PublicKey;
import java.security.SignatureException;
import java.util.Date;
import javax.security.cert.CertificateException;
import javax.security.cert.CertificateExpiredException;
import javax.security.cert.CertificateNotYetValidException;
import javax.security.cert.X509Certificate;

final class OpenSslJavaxX509Certificate extends X509Certificate {
   private final byte[] bytes;
   private X509Certificate wrapped;

   public OpenSslJavaxX509Certificate(byte[] var1) {
      super();
      this.bytes = var1;
   }

   public void checkValidity() throws CertificateExpiredException, CertificateNotYetValidException {
      this.unwrap().checkValidity();
   }

   public void checkValidity(Date var1) throws CertificateExpiredException, CertificateNotYetValidException {
      this.unwrap().checkValidity(var1);
   }

   public int getVersion() {
      return this.unwrap().getVersion();
   }

   public BigInteger getSerialNumber() {
      return this.unwrap().getSerialNumber();
   }

   public Principal getIssuerDN() {
      return this.unwrap().getIssuerDN();
   }

   public Principal getSubjectDN() {
      return this.unwrap().getSubjectDN();
   }

   public Date getNotBefore() {
      return this.unwrap().getNotBefore();
   }

   public Date getNotAfter() {
      return this.unwrap().getNotAfter();
   }

   public String getSigAlgName() {
      return this.unwrap().getSigAlgName();
   }

   public String getSigAlgOID() {
      return this.unwrap().getSigAlgOID();
   }

   public byte[] getSigAlgParams() {
      return this.unwrap().getSigAlgParams();
   }

   public byte[] getEncoded() {
      return (byte[])this.bytes.clone();
   }

   public void verify(PublicKey var1) throws CertificateException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException {
      this.unwrap().verify(var1);
   }

   public void verify(PublicKey var1, String var2) throws CertificateException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException {
      this.unwrap().verify(var1, var2);
   }

   public String toString() {
      return this.unwrap().toString();
   }

   public PublicKey getPublicKey() {
      return this.unwrap().getPublicKey();
   }

   private X509Certificate unwrap() {
      X509Certificate var1 = this.wrapped;
      if (var1 == null) {
         try {
            var1 = this.wrapped = X509Certificate.getInstance(this.bytes);
         } catch (CertificateException var3) {
            throw new IllegalStateException(var3);
         }
      }

      return var1;
   }
}
