package net.minecraft.world;

import net.minecraft.util.BlockPos;

public class ChunkCoordIntPair {
   public final int field_77276_a;
   public final int field_77275_b;

   public ChunkCoordIntPair(int var1, int var2) {
      super();
      this.field_77276_a = var1;
      this.field_77275_b = var2;
   }

   public static long func_77272_a(int var0, int var1) {
      return (long)var0 & 4294967295L | ((long)var1 & 4294967295L) << 32;
   }

   public int hashCode() {
      int var1 = 1664525 * this.field_77276_a + 1013904223;
      int var2 = 1664525 * (this.field_77275_b ^ -559038737) + 1013904223;
      return var1 ^ var2;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof ChunkCoordIntPair)) {
         return false;
      } else {
         ChunkCoordIntPair var2 = (ChunkCoordIntPair)var1;
         return this.field_77276_a == var2.field_77276_a && this.field_77275_b == var2.field_77275_b;
      }
   }

   public int func_77273_a() {
      return (this.field_77276_a << 4) + 8;
   }

   public int func_77274_b() {
      return (this.field_77275_b << 4) + 8;
   }

   public int func_180334_c() {
      return this.field_77276_a << 4;
   }

   public int func_180333_d() {
      return this.field_77275_b << 4;
   }

   public int func_180332_e() {
      return (this.field_77276_a << 4) + 15;
   }

   public int func_180330_f() {
      return (this.field_77275_b << 4) + 15;
   }

   public BlockPos func_180331_a(int var1, int var2, int var3) {
      return new BlockPos((this.field_77276_a << 4) + var1, var2, (this.field_77275_b << 4) + var3);
   }

   public BlockPos func_180619_a(int var1) {
      return new BlockPos(this.func_77273_a(), var1, this.func_77274_b());
   }

   public String toString() {
      return "[" + this.field_77276_a + ", " + this.field_77275_b + "]";
   }
}
