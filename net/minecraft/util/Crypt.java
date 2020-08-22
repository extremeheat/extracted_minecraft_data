package net.minecraft.util;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Crypt {
   private static final Logger LOGGER = LogManager.getLogger();

   public static SecretKey generateSecretKey() {
      try {
         KeyGenerator var0 = KeyGenerator.getInstance("AES");
         var0.init(128);
         return var0.generateKey();
      } catch (NoSuchAlgorithmException var1) {
         throw new Error(var1);
      }
   }

   public static KeyPair generateKeyPair() {
      try {
         KeyPairGenerator var0 = KeyPairGenerator.getInstance("RSA");
         var0.initialize(1024);
         return var0.generateKeyPair();
      } catch (NoSuchAlgorithmException var1) {
         var1.printStackTrace();
         LOGGER.error("Key pair generation failed!");
         return null;
      }
   }

   public static byte[] digestData(String var0, PublicKey var1, SecretKey var2) {
      try {
         return digestData("SHA-1", var0.getBytes("ISO_8859_1"), var2.getEncoded(), var1.getEncoded());
      } catch (UnsupportedEncodingException var4) {
         var4.printStackTrace();
         return null;
      }
   }

   private static byte[] digestData(String var0, byte[]... var1) {
      try {
         MessageDigest var2 = MessageDigest.getInstance(var0);
         byte[][] var3 = var1;
         int var4 = var1.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            byte[] var6 = var3[var5];
            var2.update(var6);
         }

         return var2.digest();
      } catch (NoSuchAlgorithmException var7) {
         var7.printStackTrace();
         return null;
      }
   }

   public static PublicKey byteToPublicKey(byte[] var0) {
      try {
         X509EncodedKeySpec var1 = new X509EncodedKeySpec(var0);
         KeyFactory var2 = KeyFactory.getInstance("RSA");
         return var2.generatePublic(var1);
      } catch (NoSuchAlgorithmException var3) {
      } catch (InvalidKeySpecException var4) {
      }

      LOGGER.error("Public key reconstitute failed!");
      return null;
   }

   public static SecretKey decryptByteToSecretKey(PrivateKey var0, byte[] var1) {
      return new SecretKeySpec(decryptUsingKey(var0, var1), "AES");
   }

   public static byte[] encryptUsingKey(Key var0, byte[] var1) {
      return cipherData(1, var0, var1);
   }

   public static byte[] decryptUsingKey(Key var0, byte[] var1) {
      return cipherData(2, var0, var1);
   }

   private static byte[] cipherData(int var0, Key var1, byte[] var2) {
      try {
         return setupCipher(var0, var1.getAlgorithm(), var1).doFinal(var2);
      } catch (IllegalBlockSizeException var4) {
         var4.printStackTrace();
      } catch (BadPaddingException var5) {
         var5.printStackTrace();
      }

      LOGGER.error("Cipher data failed!");
      return null;
   }

   private static Cipher setupCipher(int var0, String var1, Key var2) {
      try {
         Cipher var3 = Cipher.getInstance(var1);
         var3.init(var0, var2);
         return var3;
      } catch (InvalidKeyException var4) {
         var4.printStackTrace();
      } catch (NoSuchAlgorithmException var5) {
         var5.printStackTrace();
      } catch (NoSuchPaddingException var6) {
         var6.printStackTrace();
      }

      LOGGER.error("Cipher creation failed!");
      return null;
   }

   public static Cipher getCipher(int var0, Key var1) {
      try {
         Cipher var2 = Cipher.getInstance("AES/CFB8/NoPadding");
         var2.init(var0, var1, new IvParameterSpec(var1.getEncoded()));
         return var2;
      } catch (GeneralSecurityException var3) {
         throw new RuntimeException(var3);
      }
   }
}
