package net.minecraft.world;

import net.minecraft.block.Block;
import net.minecraft.util.BlockPos;

public class NextTickListEntry implements Comparable<NextTickListEntry> {
   private static long field_77177_f;
   private final Block field_151352_g;
   public final BlockPos field_180282_a;
   public long field_77180_e;
   public int field_82754_f;
   private long field_77178_g;

   public NextTickListEntry(BlockPos var1, Block var2) {
      super();
      this.field_77178_g = (long)(field_77177_f++);
      this.field_180282_a = var1;
      this.field_151352_g = var2;
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof NextTickListEntry)) {
         return false;
      } else {
         NextTickListEntry var2 = (NextTickListEntry)var1;
         return this.field_180282_a.equals(var2.field_180282_a) && Block.func_149680_a(this.field_151352_g, var2.field_151352_g);
      }
   }

   public int hashCode() {
      return this.field_180282_a.hashCode();
   }

   public NextTickListEntry func_77176_a(long var1) {
      this.field_77180_e = var1;
      return this;
   }

   public void func_82753_a(int var1) {
      this.field_82754_f = var1;
   }

   public int compareTo(NextTickListEntry var1) {
      if (this.field_77180_e < var1.field_77180_e) {
         return -1;
      } else if (this.field_77180_e > var1.field_77180_e) {
         return 1;
      } else if (this.field_82754_f != var1.field_82754_f) {
         return this.field_82754_f - var1.field_82754_f;
      } else if (this.field_77178_g < var1.field_77178_g) {
         return -1;
      } else {
         return this.field_77178_g > var1.field_77178_g ? 1 : 0;
      }
   }

   public String toString() {
      return Block.func_149682_b(this.field_151352_g) + ": " + this.field_180282_a + ", " + this.field_77180_e + ", " + this.field_82754_f + ", " + this.field_77178_g;
   }

   public Block func_151351_a() {
      return this.field_151352_g;
   }

   // $FF: synthetic method
   public int compareTo(Object var1) {
      return this.compareTo((NextTickListEntry)var1);
   }
}
