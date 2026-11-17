package net.minecraft.util.crypto;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.EncodedKeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Objects;

public class SigningCryptoUtils {
   private static final String SIGNATURE_KEY_TYPE = "RSA";
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

   public SigningCryptoUtils() {
      super();
   }

   private static <T extends Key> T rsaStringToKey(String input, final String header, final String footer, final ByteArrayToKeyFunction<T> byteArrayToKey) throws CryptException {
      int begin = input.indexOf(header);
      if (begin != -1) {
         begin += header.length();
         int end = input.indexOf(footer, begin);
         input = input.substring(begin, end + 1);
      }

      try {
         return byteArrayToKey.apply(Base64.getMimeDecoder().decode(input));
      } catch (IllegalArgumentException e) {
         throw new CryptException(e);
      }
   }

   public static PrivateKey stringToPemRsaPrivateKey(final String rsaString) throws CryptException {
      return (PrivateKey)rsaStringToKey(rsaString, "-----BEGIN RSA PRIVATE KEY-----", "-----END RSA PRIVATE KEY-----", SigningCryptoUtils::byteToPrivateKey);
   }

   public static PublicKey stringToRsaPublicKey(final String rsaString) throws CryptException {
      return (PublicKey)rsaStringToKey(rsaString, "-----BEGIN RSA PUBLIC KEY-----", "-----END RSA PUBLIC KEY-----", SigningCryptoUtils::byteToPublicKey);
   }

   public static String rsaPublicKeyToString(final PublicKey publicKey) {
      if (!"RSA".equals(publicKey.getAlgorithm())) {
         throw new IllegalArgumentException("Public key must be RSA");
      } else {
         Base64.Encoder var10000 = MIME_ENCODER;
         return "-----BEGIN RSA PUBLIC KEY-----\n" + var10000.encodeToString(publicKey.getEncoded()) + "\n-----END RSA PUBLIC KEY-----\n";
      }
   }

   public static String pemRsaPrivateKeyToString(final PrivateKey privateKey) {
      if (!"RSA".equals(privateKey.getAlgorithm())) {
         throw new IllegalArgumentException("Private key must be RSA");
      } else {
         Base64.Encoder var10000 = MIME_ENCODER;
         return "-----BEGIN RSA PRIVATE KEY-----\n" + var10000.encodeToString(privateKey.getEncoded()) + "\n-----END RSA PRIVATE KEY-----\n";
      }
   }

   private static PrivateKey byteToPrivateKey(final byte[] keyData) throws CryptException {
      try {
         EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyData);
         KeyFactory keyFactory = KeyFactory.getInstance("RSA");
         return keyFactory.generatePrivate(keySpec);
      } catch (Exception e) {
         throw new CryptException(e);
      }
   }

   public static PublicKey byteToPublicKey(final byte[] keyData) throws CryptException {
      try {
         EncodedKeySpec keySpec = new X509EncodedKeySpec(keyData);
         KeyFactory keyFactory = KeyFactory.getInstance("RSA");
         return keyFactory.generatePublic(keySpec);
      } catch (Exception e) {
         throw new CryptException(e);
      }
   }

   static {
      MIME_ENCODER = Base64.getMimeEncoder(76, "\n".getBytes(StandardCharsets.UTF_8));
      PUBLIC_KEY_CODEC = Codec.STRING.comapFlatMap((rsaString) -> {
         try {
            return DataResult.success(stringToRsaPublicKey(rsaString));
         } catch (CryptException e) {
            Objects.requireNonNull(e);
            return DataResult.error(e::getMessage);
         }
      }, SigningCryptoUtils::rsaPublicKeyToString);
      PRIVATE_KEY_CODEC = Codec.STRING.comapFlatMap((rsaString) -> {
         try {
            return DataResult.success(stringToPemRsaPrivateKey(rsaString));
         } catch (CryptException e) {
            Objects.requireNonNull(e);
            return DataResult.error(e::getMessage);
         }
      }, SigningCryptoUtils::pemRsaPrivateKeyToString);
   }

   public static class SaltSupplier {
      private static final SecureRandom SECURE_RANDOM = new SecureRandom();

      public SaltSupplier() {
         super();
      }

      public static long getLong() {
         return SECURE_RANDOM.nextLong();
      }
   }

   @FunctionalInterface
   private interface ByteArrayToKeyFunction<T extends Key> {
      T apply(byte[] input) throws CryptException;
   }
}
