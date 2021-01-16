package io.netty.handler.ssl.util;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.util.Date;
import sun.security.x509.AlgorithmId;
import sun.security.x509.CertificateAlgorithmId;
import sun.security.x509.CertificateIssuerName;
import sun.security.x509.CertificateSerialNumber;
import sun.security.x509.CertificateSubjectName;
import sun.security.x509.CertificateValidity;
import sun.security.x509.CertificateVersion;
import sun.security.x509.CertificateX509Key;
import sun.security.x509.X500Name;
import sun.security.x509.X509CertImpl;
import sun.security.x509.X509CertInfo;

final class OpenJdkSelfSignedCertGenerator {
   static String[] generate(String var0, KeyPair var1, SecureRandom var2, Date var3, Date var4) throws Exception {
      PrivateKey var5 = var1.getPrivate();
      X509CertInfo var6 = new X509CertInfo();
      X500Name var7 = new X500Name("CN=" + var0);
      var6.set("version", new CertificateVersion(2));
      var6.set("serialNumber", new CertificateSerialNumber(new BigInteger(64, var2)));

      try {
         var6.set("subject", new CertificateSubjectName(var7));
      } catch (CertificateException var10) {
         var6.set("subject", var7);
      }

      try {
         var6.set("issuer", new CertificateIssuerName(var7));
      } catch (CertificateException var9) {
         var6.set("issuer", var7);
      }

      var6.set("validity", new CertificateValidity(var3, var4));
      var6.set("key", new CertificateX509Key(var1.getPublic()));
      var6.set("algorithmID", new CertificateAlgorithmId(new AlgorithmId(AlgorithmId.sha1WithRSAEncryption_oid)));
      X509CertImpl var8 = new X509CertImpl(var6);
      var8.sign(var5, "SHA1withRSA");
      var6.set("algorithmID.algorithm", var8.get("x509.algorithm"));
      var8 = new X509CertImpl(var6);
      var8.sign(var5, "SHA1withRSA");
      var8.verify(var1.getPublic());
      return SelfSignedCertificate.newSelfSignedCertificate(var0, var5, var8);
   }

   private OpenJdkSelfSignedCertGenerator() {
      super();
   }
}
