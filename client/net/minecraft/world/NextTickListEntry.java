package net.minecraft.world;

import net.minecraft.block.Block;

public class NextTickListEntry implements Comparable {
   private static long field_77177_f;
   private final Block field_151352_g;
   public int field_77183_a;
   public int field_77181_b;
   public int field_77182_c;
   public long field_77180_e;
   public int field_82754_f;
   private long field_77178_g;

   public NextTickListEntry(int var1, int var2, int var3, Block var4) {
      super();
      this.field_77178_g = (long)(field_77177_f++);
      this.field_77183_a = var1;
      this.field_77181_b = var2;
      this.field_77182_c = var3;
      this.field_151352_g = var4;
   }

   @Override
   public boolean equals(Object var1) {
      if (!(var1 instanceof NextTickListEntry)) {
         return false;
      } else {
         NextTickListEntry var2 = (NextTickListEntry)var1;
         return this.field_77183_a == var2.field_77183_a
            && this.field_77181_b == var2.field_77181_b
            && this.field_77182_c == var2.field_77182_c
            && Block.func_149680_a(this.field_151352_g, var2.field_151352_g);
      }
   }

   @Override
   public int hashCode() {
      return (this.field_77183_a * 1024 * 1024 + this.field_77182_c * 1024 + this.field_77181_b) * 256;
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

   @Override
   public String toString() {
      return Block.func_149682_b(this.field_151352_g)
         + ": ("
         + this.field_77183_a
         + ", "
         + this.field_77181_b
         + ", "
         + this.field_77182_c
         + "), "
         + this.field_77180_e
         + ", "
         + this.field_82754_f
         + ", "
         + this.field_77178_g;
   }

   public Block func_151351_a() {
      return this.field_151352_g;
   }
}
