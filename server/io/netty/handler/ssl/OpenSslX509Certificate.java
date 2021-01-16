package io.netty.handler.ssl;

import java.io.ByteArrayInputStream;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Principal;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Set;

final class OpenSslX509Certificate extends X509Certificate {
   private final byte[] bytes;
   private X509Certificate wrapped;

   public OpenSslX509Certificate(byte[] var1) {
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

   public byte[] getTBSCertificate() throws CertificateEncodingException {
      return this.unwrap().getTBSCertificate();
   }

   public byte[] getSignature() {
      return this.unwrap().getSignature();
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

   public boolean[] getIssuerUniqueID() {
      return this.unwrap().getIssuerUniqueID();
   }

   public boolean[] getSubjectUniqueID() {
      return this.unwrap().getSubjectUniqueID();
   }

   public boolean[] getKeyUsage() {
      return this.unwrap().getKeyUsage();
   }

   public int getBasicConstraints() {
      return this.unwrap().getBasicConstraints();
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

   public boolean hasUnsupportedCriticalExtension() {
      return this.unwrap().hasUnsupportedCriticalExtension();
   }

   public Set<String> getCriticalExtensionOIDs() {
      return this.unwrap().getCriticalExtensionOIDs();
   }

   public Set<String> getNonCriticalExtensionOIDs() {
      return this.unwrap().getNonCriticalExtensionOIDs();
   }

   public byte[] getExtensionValue(String var1) {
      return this.unwrap().getExtensionValue(var1);
   }

   private X509Certificate unwrap() {
      X509Certificate var1 = this.wrapped;
      if (var1 == null) {
         try {
            var1 = this.wrapped = (X509Certificate)SslContext.X509_CERT_FACTORY.generateCertificate(new ByteArrayInputStream(this.bytes));
         } catch (CertificateException var3) {
            throw new IllegalStateException(var3);
         }
      }

      return var1;
   }
}
