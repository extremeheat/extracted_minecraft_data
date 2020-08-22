package net.minecraft.world.level.chunk.storage;

import java.util.BitSet;

public class RegionBitmap {
   private final BitSet used = new BitSet();

   public void force(int var1, int var2) {
      this.used.set(var1, var1 + var2);
   }

   public void free(int var1, int var2) {
      this.used.clear(var1, var1 + var2);
   }

   public int allocate(int var1) {
      int var2 = 0;

      while(true) {
         int var3 = this.used.nextClearBit(var2);
         int var4 = this.used.nextSetBit(var3);
         if (var4 == -1 || var4 - var3 >= var1) {
            this.force(var3, var1);
            return var3;
         }

         var2 = var4;
      }
   }
}
