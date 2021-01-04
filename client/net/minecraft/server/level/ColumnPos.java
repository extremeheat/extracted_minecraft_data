package net.minecraft.server.level;

import net.minecraft.core.BlockPos;

public class ColumnPos {
   public final int x;
   public final int z;

   public ColumnPos(int var1, int var2) {
      super();
      this.x = var1;
      this.z = var2;
   }

   public ColumnPos(BlockPos var1) {
      super();
      this.x = var1.getX();
      this.z = var1.getZ();
   }

   public long toLong() {
      return asLong(this.x, this.z);
   }

   public static long asLong(int var0, int var1) {
      return (long)var0 & 4294967295L | ((long)var1 & 4294967295L) << 32;
   }

   public String toString() {
      return "[" + this.x + ", " + this.z + "]";
   }

   public int hashCode() {
      int var1 = 1664525 * this.x + 1013904223;
      int var2 = 1664525 * (this.z ^ -559038737) + 1013904223;
      return var1 ^ var2;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof ColumnPos)) {
         return false;
      } else {
         ColumnPos var2 = (ColumnPos)var1;
         return this.x == var2.x && this.z == var2.z;
      }
   }
}
