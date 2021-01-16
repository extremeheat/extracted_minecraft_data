package com.google.common.hash;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;

final class FarmHashFingerprint64 extends AbstractNonStreamingHashFunction {
   private static final long K0 = -4348849565147123417L;
   private static final long K1 = -5435081209227447693L;
   private static final long K2 = -7286425919675154353L;

   FarmHashFingerprint64() {
      super();
   }

   public HashCode hashBytes(byte[] var1, int var2, int var3) {
      Preconditions.checkPositionIndexes(var2, var2 + var3, var1.length);
      return HashCode.fromLong(fingerprint(var1, var2, var3));
   }

   public int bits() {
      return 64;
   }

   public String toString() {
      return "Hashing.farmHashFingerprint64()";
   }

   @VisibleForTesting
   static long fingerprint(byte[] var0, int var1, int var2) {
      if (var2 <= 32) {
         return var2 <= 16 ? hashLength0to16(var0, var1, var2) : hashLength17to32(var0, var1, var2);
      } else {
         return var2 <= 64 ? hashLength33To64(var0, var1, var2) : hashLength65Plus(var0, var1, var2);
      }
   }

   private static long shiftMix(long var0) {
      return var0 ^ var0 >>> 47;
   }

   private static long hashLength16(long var0, long var2, long var4) {
      long var6 = (var0 ^ var2) * var4;
      var6 ^= var6 >>> 47;
      long var8 = (var2 ^ var6) * var4;
      var8 ^= var8 >>> 47;
      var8 *= var4;
      return var8;
   }

   private static void weakHashLength32WithSeeds(byte[] var0, int var1, long var2, long var4, long[] var6) {
      long var7 = LittleEndianByteArray.load64(var0, var1);
      long var9 = LittleEndianByteArray.load64(var0, var1 + 8);
      long var11 = LittleEndianByteArray.load64(var0, var1 + 16);
      long var13 = LittleEndianByteArray.load64(var0, var1 + 24);
      var2 += var7;
      var4 = Long.rotateRight(var4 + var2 + var13, 21);
      long var15 = var2;
      var2 += var9;
      var2 += var11;
      var4 += Long.rotateRight(var2, 44);
      var6[0] = var2 + var13;
      var6[1] = var4 + var15;
   }

   private static long hashLength0to16(byte[] var0, int var1, int var2) {
      long var13;
      long var14;
      if (var2 >= 8) {
         var13 = -7286425919675154353L + (long)(var2 * 2);
         var14 = LittleEndianByteArray.load64(var0, var1) + -7286425919675154353L;
         long var15 = LittleEndianByteArray.load64(var0, var1 + var2 - 8);
         long var9 = Long.rotateRight(var15, 37) * var13 + var14;
         long var11 = (Long.rotateRight(var14, 25) + var15) * var13;
         return hashLength16(var9, var11, var13);
      } else if (var2 >= 4) {
         var13 = -7286425919675154353L + (long)(var2 * 2);
         var14 = (long)LittleEndianByteArray.load32(var0, var1) & 4294967295L;
         return hashLength16((long)var2 + (var14 << 3), (long)LittleEndianByteArray.load32(var0, var1 + var2 - 4) & 4294967295L, var13);
      } else if (var2 > 0) {
         byte var3 = var0[var1];
         byte var4 = var0[var1 + (var2 >> 1)];
         byte var5 = var0[var1 + (var2 - 1)];
         int var6 = (var3 & 255) + ((var4 & 255) << 8);
         int var7 = var2 + ((var5 & 255) << 2);
         return shiftMix((long)var6 * -7286425919675154353L ^ (long)var7 * -4348849565147123417L) * -7286425919675154353L;
      } else {
         return -7286425919675154353L;
      }
   }

   private static long hashLength17to32(byte[] var0, int var1, int var2) {
      long var3 = -7286425919675154353L + (long)(var2 * 2);
      long var5 = LittleEndianByteArray.load64(var0, var1) * -5435081209227447693L;
      long var7 = LittleEndianByteArray.load64(var0, var1 + 8);
      long var9 = LittleEndianByteArray.load64(var0, var1 + var2 - 8) * var3;
      long var11 = LittleEndianByteArray.load64(var0, var1 + var2 - 16) * -7286425919675154353L;
      return hashLength16(Long.rotateRight(var5 + var7, 43) + Long.rotateRight(var9, 30) + var11, var5 + Long.rotateRight(var7 + -7286425919675154353L, 18) + var9, var3);
   }

   private static long hashLength33To64(byte[] var0, int var1, int var2) {
      long var3 = -7286425919675154353L + (long)(var2 * 2);
      long var5 = LittleEndianByteArray.load64(var0, var1) * -7286425919675154353L;
      long var7 = LittleEndianByteArray.load64(var0, var1 + 8);
      long var9 = LittleEndianByteArray.load64(var0, var1 + var2 - 8) * var3;
      long var11 = LittleEndianByteArray.load64(var0, var1 + var2 - 16) * -7286425919675154353L;
      long var13 = Long.rotateRight(var5 + var7, 43) + Long.rotateRight(var9, 30) + var11;
      long var15 = hashLength16(var13, var5 + Long.rotateRight(var7 + -7286425919675154353L, 18) + var9, var3);
      long var17 = LittleEndianByteArray.load64(var0, var1 + 16) * var3;
      long var19 = LittleEndianByteArray.load64(var0, var1 + 24);
      long var21 = (var13 + LittleEndianByteArray.load64(var0, var1 + var2 - 32)) * var3;
      long var23 = (var15 + LittleEndianByteArray.load64(var0, var1 + var2 - 24)) * var3;
      return hashLength16(Long.rotateRight(var17 + var19, 43) + Long.rotateRight(var21, 30) + var23, var17 + Long.rotateRight(var19 + var5, 18) + var21, var3);
   }

   private static long hashLength65Plus(byte[] var0, int var1, int var2) {
      boolean var3 = true;
      long var4 = 81L;
      long var6 = 2480279821605975764L;
      long var8 = shiftMix(var6 * -7286425919675154353L + 113L) * -7286425919675154353L;
      long[] var10 = new long[2];
      long[] var11 = new long[2];
      var4 = var4 * -7286425919675154353L + LittleEndianByteArray.load64(var0, var1);
      int var12 = var1 + (var2 - 1) / 64 * 64;
      int var13 = var12 + (var2 - 1 & 63) - 63;

      long var14;
      do {
         var4 = Long.rotateRight(var4 + var6 + var10[0] + LittleEndianByteArray.load64(var0, var1 + 8), 37) * -5435081209227447693L;
         var6 = Long.rotateRight(var6 + var10[1] + LittleEndianByteArray.load64(var0, var1 + 48), 42) * -5435081209227447693L;
         var4 ^= var11[1];
         var6 += var10[0] + LittleEndianByteArray.load64(var0, var1 + 40);
         var8 = Long.rotateRight(var8 + var11[0], 33) * -5435081209227447693L;
         weakHashLength32WithSeeds(var0, var1, var10[1] * -5435081209227447693L, var4 + var11[0], var10);
         weakHashLength32WithSeeds(var0, var1 + 32, var8 + var11[1], var6 + LittleEndianByteArray.load64(var0, var1 + 16), var11);
         var14 = var4;
         var4 = var8;
         var8 = var14;
         var1 += 64;
      } while(var1 != var12);

      var14 = -5435081209227447693L + ((var14 & 255L) << 1);
      var11[0] += (long)(var2 - 1 & 63);
      var10[0] += var11[0];
      var11[0] += var10[0];
      var4 = Long.rotateRight(var4 + var6 + var10[0] + LittleEndianByteArray.load64(var0, var13 + 8), 37) * var14;
      var6 = Long.rotateRight(var6 + var10[1] + LittleEndianByteArray.load64(var0, var13 + 48), 42) * var14;
      var4 ^= var11[1] * 9L;
      var6 += var10[0] * 9L + LittleEndianByteArray.load64(var0, var13 + 40);
      var8 = Long.rotateRight(var8 + var11[0], 33) * var14;
      weakHashLength32WithSeeds(var0, var13, var10[1] * var14, var4 + var11[0], var10);
      weakHashLength32WithSeeds(var0, var13 + 32, var8 + var11[1], var6 + LittleEndianByteArray.load64(var0, var13 + 16), var11);
      return hashLength16(hashLength16(var10[0], var11[0], var14) + shiftMix(var6) * -4348849565147123417L + var4, hashLength16(var10[1], var11[1], var14) + var8, var14);
   }
}
