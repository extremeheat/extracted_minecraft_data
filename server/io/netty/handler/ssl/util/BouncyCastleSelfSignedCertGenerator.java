package io.netty.handler.ssl.util;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Date;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

final class BouncyCastleSelfSignedCertGenerator {
   private static final Provider PROVIDER = new BouncyCastleProvider();

   static String[] generate(String var0, KeyPair var1, SecureRandom var2, Date var3, Date var4) throws Exception {
      PrivateKey var5 = var1.getPrivate();
      X500Name var6 = new X500Name("CN=" + var0);
      JcaX509v3CertificateBuilder var7 = new JcaX509v3CertificateBuilder(var6, new BigInteger(64, var2), var3, var4, var6, var1.getPublic());
      ContentSigner var8 = (new JcaContentSignerBuilder("SHA256WithRSAEncryption")).build(var5);
      X509CertificateHolder var9 = var7.build(var8);
      X509Certificate var10 = (new JcaX509CertificateConverter()).setProvider(PROVIDER).getCertificate(var9);
      var10.verify(var1.getPublic());
      return SelfSignedCertificate.newSelfSignedCertificate(var0, var5, var10);
   }

   private BouncyCastleSelfSignedCertGenerator() {
      super();
   }
}
