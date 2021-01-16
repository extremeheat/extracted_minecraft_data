package io.netty.handler.ssl.util;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.base64.Base64;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Date;

public final class SelfSignedCertificate {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(SelfSignedCertificate.class);
   private static final Date DEFAULT_NOT_BEFORE = new Date(SystemPropertyUtil.getLong("io.netty.selfSignedCertificate.defaultNotBefore", System.currentTimeMillis() - 31536000000L));
   private static final Date DEFAULT_NOT_AFTER = new Date(SystemPropertyUtil.getLong("io.netty.selfSignedCertificate.defaultNotAfter", 253402300799000L));
   private final File certificate;
   private final File privateKey;
   private final X509Certificate cert;
   private final PrivateKey key;

   public SelfSignedCertificate() throws CertificateException {
      this(DEFAULT_NOT_BEFORE, DEFAULT_NOT_AFTER);
   }

   public SelfSignedCertificate(Date var1, Date var2) throws CertificateException {
      this("example.com", var1, var2);
   }

   public SelfSignedCertificate(String var1) throws CertificateException {
      this(var1, DEFAULT_NOT_BEFORE, DEFAULT_NOT_AFTER);
   }

   public SelfSignedCertificate(String var1, Date var2, Date var3) throws CertificateException {
      this(var1, ThreadLocalInsecureRandom.current(), 1024, var2, var3);
   }

   public SelfSignedCertificate(String var1, SecureRandom var2, int var3) throws CertificateException {
      this(var1, var2, var3, DEFAULT_NOT_BEFORE, DEFAULT_NOT_AFTER);
   }

   public SelfSignedCertificate(String var1, SecureRandom var2, int var3, Date var4, Date var5) throws CertificateException {
      super();

      KeyPair var6;
      try {
         KeyPairGenerator var7 = KeyPairGenerator.getInstance("RSA");
         var7.initialize(var3, var2);
         var6 = var7.generateKeyPair();
      } catch (NoSuchAlgorithmException var23) {
         throw new Error(var23);
      }

      String[] var25;
      try {
         var25 = OpenJdkSelfSignedCertGenerator.generate(var1, var6, var2, var4, var5);
      } catch (Throwable var22) {
         logger.debug("Failed to generate a self-signed X.509 certificate using sun.security.x509:", var22);

         try {
            var25 = BouncyCastleSelfSignedCertGenerator.generate(var1, var6, var2, var4, var5);
         } catch (Throwable var21) {
            logger.debug("Failed to generate a self-signed X.509 certificate using Bouncy Castle:", var21);
            throw new CertificateException("No provider succeeded to generate a self-signed certificate. See debug log for the root cause.", var21);
         }
      }

      this.certificate = new File(var25[0]);
      this.privateKey = new File(var25[1]);
      this.key = var6.getPrivate();
      FileInputStream var8 = null;

      try {
         var8 = new FileInputStream(this.certificate);
         this.cert = (X509Certificate)CertificateFactory.getInstance("X509").generateCertificate(var8);
      } catch (Exception var20) {
         throw new CertificateEncodingException(var20);
      } finally {
         if (var8 != null) {
            try {
               var8.close();
            } catch (IOException var19) {
               logger.warn("Failed to close a file: " + this.certificate, (Throwable)var19);
            }
         }

      }

   }

   public File certificate() {
      return this.certificate;
   }

   public File privateKey() {
      return this.privateKey;
   }

   public X509Certificate cert() {
      return this.cert;
   }

   public PrivateKey key() {
      return this.key;
   }

   public void delete() {
      safeDelete(this.certificate);
      safeDelete(this.privateKey);
   }

   static String[] newSelfSignedCertificate(String var0, PrivateKey var1, X509Certificate var2) throws IOException, CertificateEncodingException {
      ByteBuf var3 = Unpooled.wrappedBuffer(var1.getEncoded());

      ByteBuf var4;
      String var5;
      try {
         var4 = Base64.encode(var3, true);

         try {
            var5 = "-----BEGIN PRIVATE KEY-----\n" + var4.toString(CharsetUtil.US_ASCII) + "\n-----END PRIVATE KEY-----\n";
         } finally {
            var4.release();
         }
      } finally {
         var3.release();
      }

      File var6 = File.createTempFile("keyutil_" + var0 + '_', ".key");
      var6.deleteOnExit();
      FileOutputStream var7 = new FileOutputStream(var6);

      try {
         var7.write(var5.getBytes(CharsetUtil.US_ASCII));
         var7.close();
         var7 = null;
      } finally {
         if (var7 != null) {
            safeClose(var6, var7);
            safeDelete(var6);
         }

      }

      var3 = Unpooled.wrappedBuffer(var2.getEncoded());

      String var8;
      try {
         var4 = Base64.encode(var3, true);

         try {
            var8 = "-----BEGIN CERTIFICATE-----\n" + var4.toString(CharsetUtil.US_ASCII) + "\n-----END CERTIFICATE-----\n";
         } finally {
            var4.release();
         }
      } finally {
         var3.release();
      }

      File var9 = File.createTempFile("keyutil_" + var0 + '_', ".crt");
      var9.deleteOnExit();
      FileOutputStream var10 = new FileOutputStream(var9);

      try {
         var10.write(var8.getBytes(CharsetUtil.US_ASCII));
         var10.close();
         var10 = null;
      } finally {
         if (var10 != null) {
            safeClose(var9, var10);
            safeDelete(var9);
            safeDelete(var6);
         }

      }

      return new String[]{var9.getPath(), var6.getPath()};
   }

   private static void safeDelete(File var0) {
      if (!var0.delete()) {
         logger.warn("Failed to delete a file: " + var0);
      }

   }

   private static void safeClose(File var0, OutputStream var1) {
      try {
         var1.close();
      } catch (IOException var3) {
         logger.warn("Failed to close a file: " + var0, (Throwable)var3);
      }

   }
}
