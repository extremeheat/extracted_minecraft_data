package io.netty.handler.ssl;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.base64.Base64;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class PemReader {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(PemReader.class);
   private static final Pattern CERT_PATTERN = Pattern.compile("-+BEGIN\\s+.*CERTIFICATE[^-]*-+(?:\\s|\\r|\\n)+([a-z0-9+/=\\r\\n]+)-+END\\s+.*CERTIFICATE[^-]*-+", 2);
   private static final Pattern KEY_PATTERN = Pattern.compile("-+BEGIN\\s+.*PRIVATE\\s+KEY[^-]*-+(?:\\s|\\r|\\n)+([a-z0-9+/=\\r\\n]+)-+END\\s+.*PRIVATE\\s+KEY[^-]*-+", 2);

   static ByteBuf[] readCertificates(File var0) throws CertificateException {
      try {
         FileInputStream var1 = new FileInputStream(var0);

         ByteBuf[] var2;
         try {
            var2 = readCertificates((InputStream)var1);
         } finally {
            safeClose((InputStream)var1);
         }

         return var2;
      } catch (FileNotFoundException var7) {
         throw new CertificateException("could not find certificate file: " + var0);
      }
   }

   static ByteBuf[] readCertificates(InputStream var0) throws CertificateException {
      String var1;
      try {
         var1 = readContent(var0);
      } catch (IOException var7) {
         throw new CertificateException("failed to read certificate input stream", var7);
      }

      ArrayList var2 = new ArrayList();
      Matcher var3 = CERT_PATTERN.matcher(var1);

      for(int var4 = 0; var3.find(var4); var4 = var3.end()) {
         ByteBuf var5 = Unpooled.copiedBuffer((CharSequence)var3.group(1), CharsetUtil.US_ASCII);
         ByteBuf var6 = Base64.decode(var5);
         var5.release();
         var2.add(var6);
      }

      if (var2.isEmpty()) {
         throw new CertificateException("found no certificates in input stream");
      } else {
         return (ByteBuf[])var2.toArray(new ByteBuf[var2.size()]);
      }
   }

   static ByteBuf readPrivateKey(File var0) throws KeyException {
      try {
         FileInputStream var1 = new FileInputStream(var0);

         ByteBuf var2;
         try {
            var2 = readPrivateKey((InputStream)var1);
         } finally {
            safeClose((InputStream)var1);
         }

         return var2;
      } catch (FileNotFoundException var7) {
         throw new KeyException("could not find key file: " + var0);
      }
   }

   static ByteBuf readPrivateKey(InputStream var0) throws KeyException {
      String var1;
      try {
         var1 = readContent(var0);
      } catch (IOException var5) {
         throw new KeyException("failed to read key input stream", var5);
      }

      Matcher var2 = KEY_PATTERN.matcher(var1);
      if (!var2.find()) {
         throw new KeyException("could not find a PKCS #8 private key in input stream (see http://netty.io/wiki/sslcontextbuilder-and-private-key.html for more information)");
      } else {
         ByteBuf var3 = Unpooled.copiedBuffer((CharSequence)var2.group(1), CharsetUtil.US_ASCII);
         ByteBuf var4 = Base64.decode(var3);
         var3.release();
         return var4;
      }
   }

   private static String readContent(InputStream var0) throws IOException {
      ByteArrayOutputStream var1 = new ByteArrayOutputStream();

      try {
         byte[] var2 = new byte[8192];

         while(true) {
            int var3 = var0.read(var2);
            if (var3 < 0) {
               String var7 = var1.toString(CharsetUtil.US_ASCII.name());
               return var7;
            }

            var1.write(var2, 0, var3);
         }
      } finally {
         safeClose((OutputStream)var1);
      }
   }

   private static void safeClose(InputStream var0) {
      try {
         var0.close();
      } catch (IOException var2) {
         logger.warn("Failed to close a stream.", (Throwable)var2);
      }

   }

   private static void safeClose(OutputStream var0) {
      try {
         var0.close();
      } catch (IOException var2) {
         logger.warn("Failed to close a stream.", (Throwable)var2);
      }

   }

   private PemReader() {
      super();
   }
}
