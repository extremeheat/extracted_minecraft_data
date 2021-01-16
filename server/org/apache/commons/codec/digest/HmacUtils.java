package org.apache.commons.codec.digest;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.binary.StringUtils;

public final class HmacUtils {
   private static final int STREAM_BUFFER_LENGTH = 1024;

   public HmacUtils() {
      super();
   }

   public static Mac getHmacMd5(byte[] var0) {
      return getInitializedMac(HmacAlgorithms.HMAC_MD5, var0);
   }

   public static Mac getHmacSha1(byte[] var0) {
      return getInitializedMac(HmacAlgorithms.HMAC_SHA_1, var0);
   }

   public static Mac getHmacSha256(byte[] var0) {
      return getInitializedMac(HmacAlgorithms.HMAC_SHA_256, var0);
   }

   public static Mac getHmacSha384(byte[] var0) {
      return getInitializedMac(HmacAlgorithms.HMAC_SHA_384, var0);
   }

   public static Mac getHmacSha512(byte[] var0) {
      return getInitializedMac(HmacAlgorithms.HMAC_SHA_512, var0);
   }

   public static Mac getInitializedMac(HmacAlgorithms var0, byte[] var1) {
      return getInitializedMac(var0.toString(), var1);
   }

   public static Mac getInitializedMac(String var0, byte[] var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("Null key");
      } else {
         try {
            SecretKeySpec var2 = new SecretKeySpec(var1, var0);
            Mac var3 = Mac.getInstance(var0);
            var3.init(var2);
            return var3;
         } catch (NoSuchAlgorithmException var4) {
            throw new IllegalArgumentException(var4);
         } catch (InvalidKeyException var5) {
            throw new IllegalArgumentException(var5);
         }
      }
   }

   public static byte[] hmacMd5(byte[] var0, byte[] var1) {
      try {
         return getHmacMd5(var0).doFinal(var1);
      } catch (IllegalStateException var3) {
         throw new IllegalArgumentException(var3);
      }
   }

   public static byte[] hmacMd5(byte[] var0, InputStream var1) throws IOException {
      return updateHmac(getHmacMd5(var0), var1).doFinal();
   }

   public static byte[] hmacMd5(String var0, String var1) {
      return hmacMd5(StringUtils.getBytesUtf8(var0), StringUtils.getBytesUtf8(var1));
   }

   public static String hmacMd5Hex(byte[] var0, byte[] var1) {
      return Hex.encodeHexString(hmacMd5(var0, var1));
   }

   public static String hmacMd5Hex(byte[] var0, InputStream var1) throws IOException {
      return Hex.encodeHexString(hmacMd5(var0, var1));
   }

   public static String hmacMd5Hex(String var0, String var1) {
      return Hex.encodeHexString(hmacMd5(var0, var1));
   }

   public static byte[] hmacSha1(byte[] var0, byte[] var1) {
      try {
         return getHmacSha1(var0).doFinal(var1);
      } catch (IllegalStateException var3) {
         throw new IllegalArgumentException(var3);
      }
   }

   public static byte[] hmacSha1(byte[] var0, InputStream var1) throws IOException {
      return updateHmac(getHmacSha1(var0), var1).doFinal();
   }

   public static byte[] hmacSha1(String var0, String var1) {
      return hmacSha1(StringUtils.getBytesUtf8(var0), StringUtils.getBytesUtf8(var1));
   }

   public static String hmacSha1Hex(byte[] var0, byte[] var1) {
      return Hex.encodeHexString(hmacSha1(var0, var1));
   }

   public static String hmacSha1Hex(byte[] var0, InputStream var1) throws IOException {
      return Hex.encodeHexString(hmacSha1(var0, var1));
   }

   public static String hmacSha1Hex(String var0, String var1) {
      return Hex.encodeHexString(hmacSha1(var0, var1));
   }

   public static byte[] hmacSha256(byte[] var0, byte[] var1) {
      try {
         return getHmacSha256(var0).doFinal(var1);
      } catch (IllegalStateException var3) {
         throw new IllegalArgumentException(var3);
      }
   }

   public static byte[] hmacSha256(byte[] var0, InputStream var1) throws IOException {
      return updateHmac(getHmacSha256(var0), var1).doFinal();
   }

   public static byte[] hmacSha256(String var0, String var1) {
      return hmacSha256(StringUtils.getBytesUtf8(var0), StringUtils.getBytesUtf8(var1));
   }

   public static String hmacSha256Hex(byte[] var0, byte[] var1) {
      return Hex.encodeHexString(hmacSha256(var0, var1));
   }

   public static String hmacSha256Hex(byte[] var0, InputStream var1) throws IOException {
      return Hex.encodeHexString(hmacSha256(var0, var1));
   }

   public static String hmacSha256Hex(String var0, String var1) {
      return Hex.encodeHexString(hmacSha256(var0, var1));
   }

   public static byte[] hmacSha384(byte[] var0, byte[] var1) {
      try {
         return getHmacSha384(var0).doFinal(var1);
      } catch (IllegalStateException var3) {
         throw new IllegalArgumentException(var3);
      }
   }

   public static byte[] hmacSha384(byte[] var0, InputStream var1) throws IOException {
      return updateHmac(getHmacSha384(var0), var1).doFinal();
   }

   public static byte[] hmacSha384(String var0, String var1) {
      return hmacSha384(StringUtils.getBytesUtf8(var0), StringUtils.getBytesUtf8(var1));
   }

   public static String hmacSha384Hex(byte[] var0, byte[] var1) {
      return Hex.encodeHexString(hmacSha384(var0, var1));
   }

   public static String hmacSha384Hex(byte[] var0, InputStream var1) throws IOException {
      return Hex.encodeHexString(hmacSha384(var0, var1));
   }

   public static String hmacSha384Hex(String var0, String var1) {
      return Hex.encodeHexString(hmacSha384(var0, var1));
   }

   public static byte[] hmacSha512(byte[] var0, byte[] var1) {
      try {
         return getHmacSha512(var0).doFinal(var1);
      } catch (IllegalStateException var3) {
         throw new IllegalArgumentException(var3);
      }
   }

   public static byte[] hmacSha512(byte[] var0, InputStream var1) throws IOException {
      return updateHmac(getHmacSha512(var0), var1).doFinal();
   }

   public static byte[] hmacSha512(String var0, String var1) {
      return hmacSha512(StringUtils.getBytesUtf8(var0), StringUtils.getBytesUtf8(var1));
   }

   public static String hmacSha512Hex(byte[] var0, byte[] var1) {
      return Hex.encodeHexString(hmacSha512(var0, var1));
   }

   public static String hmacSha512Hex(byte[] var0, InputStream var1) throws IOException {
      return Hex.encodeHexString(hmacSha512(var0, var1));
   }

   public static String hmacSha512Hex(String var0, String var1) {
      return Hex.encodeHexString(hmacSha512(var0, var1));
   }

   public static Mac updateHmac(Mac var0, byte[] var1) {
      var0.reset();
      var0.update(var1);
      return var0;
   }

   public static Mac updateHmac(Mac var0, InputStream var1) throws IOException {
      var0.reset();
      byte[] var2 = new byte[1024];

      for(int var3 = var1.read(var2, 0, 1024); var3 > -1; var3 = var1.read(var2, 0, 1024)) {
         var0.update(var2, 0, var3);
      }

      return var0;
   }

   public static Mac updateHmac(Mac var0, String var1) {
      var0.reset();
      var0.update(StringUtils.getBytesUtf8(var1));
      return var0;
   }
}
