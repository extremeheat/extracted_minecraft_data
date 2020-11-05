package net.minecraft.util;

import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Crypt {
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
      byte[][] var2 = var0;
      int var3 = var0.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         byte[] var5 = var2[var4];
         var1.update(var5);
      }

      return var1.digest();
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
}
