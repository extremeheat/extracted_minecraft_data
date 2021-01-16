package com.google.common.hash;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import java.security.Key;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.zip.Adler32;
import java.util.zip.CRC32;
import java.util.zip.Checksum;
import javax.annotation.Nullable;
import javax.crypto.spec.SecretKeySpec;

@Beta
public final class Hashing {
   private static final int GOOD_FAST_HASH_SEED = (int)System.currentTimeMillis();

   public static HashFunction goodFastHash(int var0) {
      int var1 = checkPositiveAndMakeMultipleOf32(var0);
      if (var1 == 32) {
         return Hashing.Murmur3_32Holder.GOOD_FAST_HASH_FUNCTION_32;
      } else if (var1 <= 128) {
         return Hashing.Murmur3_128Holder.GOOD_FAST_HASH_FUNCTION_128;
      } else {
         int var2 = (var1 + 127) / 128;
         HashFunction[] var3 = new HashFunction[var2];
         var3[0] = Hashing.Murmur3_128Holder.GOOD_FAST_HASH_FUNCTION_128;
         int var4 = GOOD_FAST_HASH_SEED;

         for(int var5 = 1; var5 < var2; ++var5) {
            var4 += 1500450271;
            var3[var5] = murmur3_128(var4);
         }

         return new Hashing.ConcatenatedHashFunction(var3);
      }
   }

   public static HashFunction murmur3_32(int var0) {
      return new Murmur3_32HashFunction(var0);
   }

   public static HashFunction murmur3_32() {
      return Hashing.Murmur3_32Holder.MURMUR3_32;
   }

   public static HashFunction murmur3_128(int var0) {
      return new Murmur3_128HashFunction(var0);
   }

   public static HashFunction murmur3_128() {
      return Hashing.Murmur3_128Holder.MURMUR3_128;
   }

   public static HashFunction sipHash24() {
      return Hashing.SipHash24Holder.SIP_HASH_24;
   }

   public static HashFunction sipHash24(long var0, long var2) {
      return new SipHashFunction(2, 4, var0, var2);
   }

   public static HashFunction md5() {
      return Hashing.Md5Holder.MD5;
   }

   public static HashFunction sha1() {
      return Hashing.Sha1Holder.SHA_1;
   }

   public static HashFunction sha256() {
      return Hashing.Sha256Holder.SHA_256;
   }

   public static HashFunction sha384() {
      return Hashing.Sha384Holder.SHA_384;
   }

   public static HashFunction sha512() {
      return Hashing.Sha512Holder.SHA_512;
   }

   public static HashFunction hmacMd5(Key var0) {
      return new MacHashFunction("HmacMD5", var0, hmacToString("hmacMd5", var0));
   }

   public static HashFunction hmacMd5(byte[] var0) {
      return hmacMd5((Key)(new SecretKeySpec((byte[])Preconditions.checkNotNull(var0), "HmacMD5")));
   }

   public static HashFunction hmacSha1(Key var0) {
      return new MacHashFunction("HmacSHA1", var0, hmacToString("hmacSha1", var0));
   }

   public static HashFunction hmacSha1(byte[] var0) {
      return hmacSha1((Key)(new SecretKeySpec((byte[])Preconditions.checkNotNull(var0), "HmacSHA1")));
   }

   public static HashFunction hmacSha256(Key var0) {
      return new MacHashFunction("HmacSHA256", var0, hmacToString("hmacSha256", var0));
   }

   public static HashFunction hmacSha256(byte[] var0) {
      return hmacSha256((Key)(new SecretKeySpec((byte[])Preconditions.checkNotNull(var0), "HmacSHA256")));
   }

   public static HashFunction hmacSha512(Key var0) {
      return new MacHashFunction("HmacSHA512", var0, hmacToString("hmacSha512", var0));
   }

   public static HashFunction hmacSha512(byte[] var0) {
      return hmacSha512((Key)(new SecretKeySpec((byte[])Preconditions.checkNotNull(var0), "HmacSHA512")));
   }

   private static String hmacToString(String var0, Key var1) {
      return String.format("Hashing.%s(Key[algorithm=%s, format=%s])", var0, var1.getAlgorithm(), var1.getFormat());
   }

   public static HashFunction crc32c() {
      return Hashing.Crc32cHolder.CRC_32_C;
   }

   public static HashFunction crc32() {
      return Hashing.Crc32Holder.CRC_32;
   }

   public static HashFunction adler32() {
      return Hashing.Adler32Holder.ADLER_32;
   }

   private static HashFunction checksumHashFunction(Hashing.ChecksumType var0, String var1) {
      return new ChecksumHashFunction(var0, var0.bits, var1);
   }

   public static HashFunction farmHashFingerprint64() {
      return Hashing.FarmHashFingerprint64Holder.FARMHASH_FINGERPRINT_64;
   }

   public static int consistentHash(HashCode var0, int var1) {
      return consistentHash(var0.padToLong(), var1);
   }

   public static int consistentHash(long var0, int var2) {
      Preconditions.checkArgument(var2 > 0, "buckets must be positive: %s", var2);
      Hashing.LinearCongruentialGenerator var3 = new Hashing.LinearCongruentialGenerator(var0);
      int var4 = 0;

      while(true) {
         int var5 = (int)((double)(var4 + 1) / var3.nextDouble());
         if (var5 < 0 || var5 >= var2) {
            return var4;
         }

         var4 = var5;
      }
   }

   public static HashCode combineOrdered(Iterable<HashCode> var0) {
      Iterator var1 = var0.iterator();
      Preconditions.checkArgument(var1.hasNext(), "Must be at least 1 hash code to combine.");
      int var2 = ((HashCode)var1.next()).bits();
      byte[] var3 = new byte[var2 / 8];
      Iterator var4 = var0.iterator();

      while(var4.hasNext()) {
         HashCode var5 = (HashCode)var4.next();
         byte[] var6 = var5.asBytes();
         Preconditions.checkArgument(var6.length == var3.length, "All hashcodes must have the same bit length.");

         for(int var7 = 0; var7 < var6.length; ++var7) {
            var3[var7] = (byte)(var3[var7] * 37 ^ var6[var7]);
         }
      }

      return HashCode.fromBytesNoCopy(var3);
   }

   public static HashCode combineUnordered(Iterable<HashCode> var0) {
      Iterator var1 = var0.iterator();
      Preconditions.checkArgument(var1.hasNext(), "Must be at least 1 hash code to combine.");
      byte[] var2 = new byte[((HashCode)var1.next()).bits() / 8];
      Iterator var3 = var0.iterator();

      while(var3.hasNext()) {
         HashCode var4 = (HashCode)var3.next();
         byte[] var5 = var4.asBytes();
         Preconditions.checkArgument(var5.length == var2.length, "All hashcodes must have the same bit length.");

         for(int var6 = 0; var6 < var5.length; ++var6) {
            var2[var6] += var5[var6];
         }
      }

      return HashCode.fromBytesNoCopy(var2);
   }

   static int checkPositiveAndMakeMultipleOf32(int var0) {
      Preconditions.checkArgument(var0 > 0, "Number of bits must be positive");
      return var0 + 31 & -32;
   }

   public static HashFunction concatenating(HashFunction var0, HashFunction var1, HashFunction... var2) {
      ArrayList var3 = new ArrayList();
      var3.add(var0);
      var3.add(var1);
      HashFunction[] var4 = var2;
      int var5 = var2.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         HashFunction var7 = var4[var6];
         var3.add(var7);
      }

      return new Hashing.ConcatenatedHashFunction((HashFunction[])var3.toArray(new HashFunction[0]));
   }

   public static HashFunction concatenating(Iterable<HashFunction> var0) {
      Preconditions.checkNotNull(var0);
      ArrayList var1 = new ArrayList();
      Iterator var2 = var0.iterator();

      while(var2.hasNext()) {
         HashFunction var3 = (HashFunction)var2.next();
         var1.add(var3);
      }

      Preconditions.checkArgument(var1.size() > 0, "number of hash functions (%s) must be > 0", var1.size());
      return new Hashing.ConcatenatedHashFunction((HashFunction[])var1.toArray(new HashFunction[0]));
   }

   private Hashing() {
      super();
   }

   private static final class LinearCongruentialGenerator {
      private long state;

      public LinearCongruentialGenerator(long var1) {
         super();
         this.state = var1;
      }

      public double nextDouble() {
         this.state = 2862933555777941757L * this.state + 1L;
         return (double)((int)(this.state >>> 33) + 1) / 2.147483648E9D;
      }
   }

   private static final class ConcatenatedHashFunction extends AbstractCompositeHashFunction {
      private final int bits;

      private ConcatenatedHashFunction(HashFunction... var1) {
         super(var1);
         int var2 = 0;
         HashFunction[] var3 = var1;
         int var4 = var1.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            HashFunction var6 = var3[var5];
            var2 += var6.bits();
            Preconditions.checkArgument(var6.bits() % 8 == 0, "the number of bits (%s) in hashFunction (%s) must be divisible by 8", (int)var6.bits(), var6);
         }

         this.bits = var2;
      }

      HashCode makeHash(Hasher[] var1) {
         byte[] var2 = new byte[this.bits / 8];
         int var3 = 0;
         Hasher[] var4 = var1;
         int var5 = var1.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            Hasher var7 = var4[var6];
            HashCode var8 = var7.hash();
            var3 += var8.writeBytesTo(var2, var3, var8.bits() / 8);
         }

         return HashCode.fromBytesNoCopy(var2);
      }

      public int bits() {
         return this.bits;
      }

      public boolean equals(@Nullable Object var1) {
         if (var1 instanceof Hashing.ConcatenatedHashFunction) {
            Hashing.ConcatenatedHashFunction var2 = (Hashing.ConcatenatedHashFunction)var1;
            return Arrays.equals(this.functions, var2.functions);
         } else {
            return false;
         }
      }

      public int hashCode() {
         return Arrays.hashCode(this.functions) * 31 + this.bits;
      }

      // $FF: synthetic method
      ConcatenatedHashFunction(HashFunction[] var1, Object var2) {
         this(var1);
      }
   }

   private static class FarmHashFingerprint64Holder {
      static final HashFunction FARMHASH_FINGERPRINT_64 = new FarmHashFingerprint64();

      private FarmHashFingerprint64Holder() {
         super();
      }
   }

   static enum ChecksumType implements Supplier<Checksum> {
      CRC_32(32) {
         public Checksum get() {
            return new CRC32();
         }
      },
      ADLER_32(32) {
         public Checksum get() {
            return new Adler32();
         }
      };

      private final int bits;

      private ChecksumType(int var3) {
         this.bits = var3;
      }

      public abstract Checksum get();

      // $FF: synthetic method
      ChecksumType(int var3, Object var4) {
         this(var3);
      }
   }

   private static class Adler32Holder {
      static final HashFunction ADLER_32;

      private Adler32Holder() {
         super();
      }

      static {
         ADLER_32 = Hashing.checksumHashFunction(Hashing.ChecksumType.ADLER_32, "Hashing.adler32()");
      }
   }

   private static class Crc32Holder {
      static final HashFunction CRC_32;

      private Crc32Holder() {
         super();
      }

      static {
         CRC_32 = Hashing.checksumHashFunction(Hashing.ChecksumType.CRC_32, "Hashing.crc32()");
      }
   }

   private static final class Crc32cHolder {
      static final HashFunction CRC_32_C = new Crc32cHashFunction();

      private Crc32cHolder() {
         super();
      }
   }

   private static class Sha512Holder {
      static final HashFunction SHA_512 = new MessageDigestHashFunction("SHA-512", "Hashing.sha512()");

      private Sha512Holder() {
         super();
      }
   }

   private static class Sha384Holder {
      static final HashFunction SHA_384 = new MessageDigestHashFunction("SHA-384", "Hashing.sha384()");

      private Sha384Holder() {
         super();
      }
   }

   private static class Sha256Holder {
      static final HashFunction SHA_256 = new MessageDigestHashFunction("SHA-256", "Hashing.sha256()");

      private Sha256Holder() {
         super();
      }
   }

   private static class Sha1Holder {
      static final HashFunction SHA_1 = new MessageDigestHashFunction("SHA-1", "Hashing.sha1()");

      private Sha1Holder() {
         super();
      }
   }

   private static class Md5Holder {
      static final HashFunction MD5 = new MessageDigestHashFunction("MD5", "Hashing.md5()");

      private Md5Holder() {
         super();
      }
   }

   private static class SipHash24Holder {
      static final HashFunction SIP_HASH_24 = new SipHashFunction(2, 4, 506097522914230528L, 1084818905618843912L);

      private SipHash24Holder() {
         super();
      }
   }

   private static class Murmur3_128Holder {
      static final HashFunction MURMUR3_128 = new Murmur3_128HashFunction(0);
      static final HashFunction GOOD_FAST_HASH_FUNCTION_128;

      private Murmur3_128Holder() {
         super();
      }

      static {
         GOOD_FAST_HASH_FUNCTION_128 = Hashing.murmur3_128(Hashing.GOOD_FAST_HASH_SEED);
      }
   }

   private static class Murmur3_32Holder {
      static final HashFunction MURMUR3_32 = new Murmur3_32HashFunction(0);
      static final HashFunction GOOD_FAST_HASH_FUNCTION_32;

      private Murmur3_32Holder() {
         super();
      }

      static {
         GOOD_FAST_HASH_FUNCTION_32 = Hashing.murmur3_32(Hashing.GOOD_FAST_HASH_SEED);
      }
   }
}
