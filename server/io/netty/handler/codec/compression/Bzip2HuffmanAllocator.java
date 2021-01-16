package io.netty.handler.codec.compression;

final class Bzip2HuffmanAllocator {
   private static int first(int[] var0, int var1, int var2) {
      int var3 = var0.length;
      int var4 = var1;

      int var5;
      for(var5 = var0.length - 2; var1 >= var2 && var0[var1] % var3 > var4; var1 -= var4 - var1 + 1) {
         var5 = var1;
      }

      var1 = Math.max(var2 - 1, var1);

      while(var5 > var1 + 1) {
         int var6 = var1 + var5 >>> 1;
         if (var0[var6] % var3 > var4) {
            var5 = var6;
         } else {
            var1 = var6;
         }
      }

      return var5;
   }

   private static void setExtendedParentPointers(int[] var0) {
      int var1 = var0.length;
      var0[0] += var0[1];
      int var2 = 0;
      int var3 = 1;

      for(int var4 = 2; var3 < var1 - 1; ++var3) {
         int var5;
         if (var4 < var1 && var0[var2] >= var0[var4]) {
            var5 = var0[var4++];
         } else {
            var5 = var0[var2];
            var0[var2++] = var3;
         }

         if (var4 < var1 && (var2 >= var3 || var0[var2] >= var0[var4])) {
            var5 += var0[var4++];
         } else {
            var5 += var0[var2];
            var0[var2++] = var3 + var1;
         }

         var0[var3] = var5;
      }

   }

   private static int findNodesToRelocate(int[] var0, int var1) {
      int var2 = var0.length - 2;

      for(int var3 = 1; var3 < var1 - 1 && var2 > 1; ++var3) {
         var2 = first(var0, var2 - 1, 0);
      }

      return var2;
   }

   private static void allocateNodeLengths(int[] var0) {
      int var1 = var0.length - 2;
      int var2 = var0.length - 1;
      int var3 = 1;

      for(int var4 = 2; var4 > 0; ++var3) {
         int var5 = var1;
         var1 = first(var0, var1 - 1, 0);

         for(int var6 = var4 - (var5 - var1); var6 > 0; --var6) {
            var0[var2--] = var3;
         }

         var4 = var5 - var1 << 1;
      }

   }

   private static void allocateNodeLengthsWithRelocation(int[] var0, int var1, int var2) {
      int var3 = var0.length - 2;
      int var4 = var0.length - 1;
      int var5 = var2 == 1 ? 2 : 1;
      int var6 = var2 == 1 ? var1 - 2 : var1;

      for(int var7 = var5 << 1; var7 > 0; ++var5) {
         int var8 = var3;
         var3 = var3 <= var1 ? var3 : first(var0, var3 - 1, var1);
         int var9 = 0;
         if (var5 >= var2) {
            var9 = Math.min(var6, 1 << var5 - var2);
         } else if (var5 == var2 - 1) {
            var9 = 1;
            if (var0[var3] == var8) {
               ++var3;
            }
         }

         for(int var10 = var7 - (var8 - var3 + var9); var10 > 0; --var10) {
            var0[var4--] = var5;
         }

         var6 -= var9;
         var7 = var8 - var3 + var9 << 1;
      }

   }

   static void allocateHuffmanCodeLengths(int[] var0, int var1) {
      switch(var0.length) {
      case 2:
         var0[1] = 1;
      case 1:
         var0[0] = 1;
         return;
      default:
         setExtendedParentPointers(var0);
         int var2 = findNodesToRelocate(var0, var1);
         if (var0[0] % var0.length >= var2) {
            allocateNodeLengths(var0);
         } else {
            int var3 = var1 - (32 - Integer.numberOfLeadingZeros(var2 - 1));
            allocateNodeLengthsWithRelocation(var0, var2, var3);
         }

      }
   }

   private Bzip2HuffmanAllocator() {
      super();
   }
}
