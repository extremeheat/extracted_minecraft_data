package net.minecraft.util;

import com.google.common.primitives.Longs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import it.unimi.dsi.fastutil.bytes.ByteArrays;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Objects;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import net.minecraft.network.FriendlyByteBuf;

public class Crypt {
   private static final String SYMMETRIC_ALGORITHM = "AES";
   private static final int SYMMETRIC_BITS = 128;
   private static final String ASYMMETRIC_ALGORITHM = "RSA";
   private static final int ASYMMETRIC_BITS = 1024;
   private static final String BYTE_ENCODING = "ISO_8859_1";
   private static final String HASH_ALGORITHM = "SHA-1";
   public static final String SIGNING_ALGORITHM = "SHA256withRSA";
   public static final int SIGNATURE_BYTES = 256;
   private static final String PEM_RSA_PRIVATE_KEY_HEADER = "-----BEGIN RSA PRIVATE KEY-----";
   private static final String PEM_RSA_PRIVATE_KEY_FOOTER = "-----END RSA PRIVATE KEY-----";
   public static final String RSA_PUBLIC_KEY_HEADER = "-----BEGIN RSA PUBLIC KEY-----";
   private static final String RSA_PUBLIC_KEY_FOOTER = "-----END RSA PUBLIC KEY-----";
   public static final String MIME_LINE_SEPARATOR = "\n";
   public static final Base64.Encoder MIME_ENCODER;
   public static final Codec<PublicKey> PUBLIC_KEY_CODEC;
   public static final Codec<PrivateKey> PRIVATE_KEY_CODEC;

   public Crypt() {
      super();
   }

   public static SecretKey generateSecretKey() throws CryptException {
      try {
         KeyGenerator var0 = KeyGenerator.getInstance("AES");
         var0.init(128);
         return var0.generateKey();
      } catch (Exception var1) {
         throw new CryptException(var1);
      }
   }

   public static KeyPair generateKeyPair() throws CryptException {
      try {
         KeyPairGenerator var0 = KeyPairGenerator.getInstance("RSA");
         var0.initialize(1024);
         return var0.generateKeyPair();
      } catch (Exception var1) {
         throw new CryptException(var1);
      }
   }

   public static byte[] digestData(String var0, PublicKey var1, SecretKey var2) throws CryptException {
      try {
         return digestData(var0.getBytes("ISO_8859_1"), var2.getEncoded(), var1.getEncoded());
      } catch (Exception var4) {
         throw new CryptException(var4);
      }
   }

   private static byte[] digestData(byte[]... var0) throws Exception {
      MessageDigest var1 = MessageDigest.getInstance("SHA-1");

      for(byte[] var5 : var0) {
         var1.update(var5);
      }

      return var1.digest();
   }

   private static <T extends Key> T rsaStringToKey(String var0, String var1, String var2, ByteArrayToKeyFunction<T> var3) throws CryptException {
      int var4 = var0.indexOf(var1);
      if (var4 != -1) {
         var4 += var1.length();
         int var5 = var0.indexOf(var2, var4);
         var0 = var0.substring(var4, var5 + 1);
      }

      try {
         return (T)var3.apply(Base64.getMimeDecoder().decode(var0));
      } catch (IllegalArgumentException var6) {
         throw new CryptException(var6);
      }
   }

   public static PrivateKey stringToPemRsaPrivateKey(String var0) throws CryptException {
      return (PrivateKey)rsaStringToKey(var0, "-----BEGIN RSA PRIVATE KEY-----", "-----END RSA PRIVATE KEY-----", Crypt::byteToPrivateKey);
   }

   public static PublicKey stringToRsaPublicKey(String var0) throws CryptException {
      return (PublicKey)rsaStringToKey(var0, "-----BEGIN RSA PUBLIC KEY-----", "-----END RSA PUBLIC KEY-----", Crypt::byteToPublicKey);
   }

   public static String rsaPublicKeyToString(PublicKey var0) {
      if (!"RSA".equals(var0.getAlgorithm())) {
         throw new IllegalArgumentException("Public key must be RSA");
      } else {
         Base64.Encoder var10000 = MIME_ENCODER;
         return "-----BEGIN RSA PUBLIC KEY-----\n" + var10000.encodeToString(var0.getEncoded()) + "\n-----END RSA PUBLIC KEY-----\n";
      }
   }

   public static String pemRsaPrivateKeyToString(PrivateKey var0) {
      if (!"RSA".equals(var0.getAlgorithm())) {
         throw new IllegalArgumentException("Private key must be RSA");
      } else {
         Base64.Encoder var10000 = MIME_ENCODER;
         return "-----BEGIN RSA PRIVATE KEY-----\n" + var10000.encodeToString(var0.getEncoded()) + "\n-----END RSA PRIVATE KEY-----\n";
      }
   }

   private static PrivateKey byteToPrivateKey(byte[] var0) throws CryptException {
      try {
         PKCS8EncodedKeySpec var1 = new PKCS8EncodedKeySpec(var0);
         KeyFactory var2 = KeyFactory.getInstance("RSA");
         return var2.generatePrivate(var1);
      } catch (Exception var3) {
         throw new CryptException(var3);
      }
   }

   public static PublicKey byteToPublicKey(byte[] var0) throws CryptException {
      try {
         X509EncodedKeySpec var1 = new X509EncodedKeySpec(var0);
         KeyFactory var2 = KeyFactory.getInstance("RSA");
         return var2.generatePublic(var1);
      } catch (Exception var3) {
         throw new CryptException(var3);
      }
   }

   public static SecretKey decryptByteToSecretKey(PrivateKey var0, byte[] var1) throws CryptException {
      byte[] var2 = decryptUsingKey(var0, var1);

      try {
         return new SecretKeySpec(var2, "AES");
      } catch (Exception var4) {
         throw new CryptException(var4);
      }
   }

   public static byte[] encryptUsingKey(Key var0, byte[] var1) throws CryptException {
      return cipherData(1, var0, var1);
   }

   public static byte[] decryptUsingKey(Key var0, byte[] var1) throws CryptException {
      return cipherData(2, var0, var1);
   }

   private static byte[] cipherData(int var0, Key var1, byte[] var2) throws CryptException {
      try {
         return setupCipher(var0, var1.getAlgorithm(), var1).doFinal(var2);
      } catch (Exception var4) {
         throw new CryptException(var4);
      }
   }

   private static Cipher setupCipher(int var0, String var1, Key var2) throws Exception {
      Cipher var3 = Cipher.getInstance(var1);
      var3.init(var0, var2);
      return var3;
   }

   public static Cipher getCipher(int var0, Key var1) throws CryptException {
      try {
         Cipher var2 = Cipher.getInstance("AES/CFB8/NoPadding");
         var2.init(var0, var1, new IvParameterSpec(var1.getEncoded()));
         return var2;
      } catch (Exception var3) {
         throw new CryptException(var3);
      }
   }

   static {
      MIME_ENCODER = Base64.getMimeEncoder(76, "\n".getBytes(StandardCharsets.UTF_8));
      PUBLIC_KEY_CODEC = Codec.STRING.comapFlatMap((var0) -> {
         try {
            return DataResult.success(stringToRsaPublicKey(var0));
         } catch (CryptException var2) {
            Objects.requireNonNull(var2);
            return DataResult.error(var2::getMessage);
         }
      }, Crypt::rsaPublicKeyToString);
      PRIVATE_KEY_CODEC = Codec.STRING.comapFlatMap((var0) -> {
         try {
            return DataResult.success(stringToPemRsaPrivateKey(var0));
         } catch (CryptException var2) {
            Objects.requireNonNull(var2);
            return DataResult.error(var2::getMessage);
         }
      }, Crypt::pemRsaPrivateKeyToString);
   }

   public static class SaltSupplier {
      private static final SecureRandom secureRandom = new SecureRandom();

      public SaltSupplier() {
         super();
      }

      public static long getLong() {
         return secureRandom.nextLong();
      }
   }

   public static record SaltSignaturePair(long salt, byte[] signature) {
      public static final SaltSignaturePair EMPTY;

      public SaltSignaturePair(FriendlyByteBuf var1) {
         this(var1.readLong(), var1.readByteArray());
      }

      public SaltSignaturePair(long var1, byte[] var3) {
         super();
         this.salt = var1;
         this.signature = var3;
      }

      public boolean isValid() {
         return this.signature.length > 0;
      }

      public static void write(FriendlyByteBuf var0, SaltSignaturePair var1) {
         var0.writeLong(var1.salt);
         var0.writeByteArray(var1.signature);
      }

      public byte[] saltAsBytes() {
         return Longs.toByteArray(this.salt);
      }

      static {
         EMPTY = new SaltSignaturePair(0L, ByteArrays.EMPTY_ARRAY);
      }
   }

   interface ByteArrayToKeyFunction<T extends Key> {
      T apply(byte[] var1) throws CryptException;
   }
}
