package net.minecraft.util;

public class ChunkCoordinates implements Comparable {
   public int field_71574_a;
   public int field_71572_b;
   public int field_71573_c;

   public ChunkCoordinates() {
      super();
   }

   public ChunkCoordinates(int var1, int var2, int var3) {
      super();
      this.field_71574_a = var1;
      this.field_71572_b = var2;
      this.field_71573_c = var3;
   }

   public ChunkCoordinates(ChunkCoordinates var1) {
      super();
      this.field_71574_a = var1.field_71574_a;
      this.field_71572_b = var1.field_71572_b;
      this.field_71573_c = var1.field_71573_c;
   }

   @Override
   public boolean equals(Object var1) {
      if (!(var1 instanceof ChunkCoordinates)) {
         return false;
      } else {
         ChunkCoordinates var2 = (ChunkCoordinates)var1;
         return this.field_71574_a == var2.field_71574_a && this.field_71572_b == var2.field_71572_b && this.field_71573_c == var2.field_71573_c;
      }
   }

   @Override
   public int hashCode() {
      return this.field_71574_a + this.field_71573_c << 8 + this.field_71572_b << 16;
   }

   public int compareTo(ChunkCoordinates var1) {
      if (this.field_71572_b == var1.field_71572_b) {
         return this.field_71573_c == var1.field_71573_c ? this.field_71574_a - var1.field_71574_a : this.field_71573_c - var1.field_71573_c;
      } else {
         return this.field_71572_b - var1.field_71572_b;
      }
   }

   public void func_71571_b(int var1, int var2, int var3) {
      this.field_71574_a = var1;
      this.field_71572_b = var2;
      this.field_71573_c = var3;
   }

   public float func_71569_e(int var1, int var2, int var3) {
      float var4 = (float)(this.field_71574_a - var1);
      float var5 = (float)(this.field_71572_b - var2);
      float var6 = (float)(this.field_71573_c - var3);
      return var4 * var4 + var5 * var5 + var6 * var6;
   }

   public float func_82371_e(ChunkCoordinates var1) {
      return this.func_71569_e(var1.field_71574_a, var1.field_71572_b, var1.field_71573_c);
   }

   @Override
   public String toString() {
      return "Pos{x=" + this.field_71574_a + ", y=" + this.field_71572_b + ", z=" + this.field_71573_c + '}';
   }
}
