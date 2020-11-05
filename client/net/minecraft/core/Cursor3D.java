package net.minecraft.core;

public class Cursor3D {
   private int originX;
   private int originY;
   private int originZ;
   private int width;
   private int height;
   private int depth;
   private int end;
   private int index;
   private int x;
   private int y;
   private int z;

   public Cursor3D(int var1, int var2, int var3, int var4, int var5, int var6) {
      super();
      this.originX = var1;
      this.originY = var2;
      this.originZ = var3;
      this.width = var4 - var1 + 1;
      this.height = var5 - var2 + 1;
      this.depth = var6 - var3 + 1;
      this.end = this.width * this.height * this.depth;
   }

   public boolean advance() {
      if (this.index == this.end) {
         return false;
      } else {
         this.x = this.index % this.width;
         int var1 = this.index / this.width;
         this.y = var1 % this.height;
         this.z = var1 / this.height;
         ++this.index;
         return true;
      }
   }

   public int nextX() {
      return this.originX + this.x;
   }

   public int nextY() {
      return this.originY + this.y;
   }

   public int nextZ() {
      return this.originZ + this.z;
   }

   public int getNextType() {
      int var1 = 0;
      if (this.x == 0 || this.x == this.width - 1) {
         ++var1;
      }

      if (this.y == 0 || this.y == this.height - 1) {
         ++var1;
      }

      if (this.z == 0 || this.z == this.depth - 1) {
         ++var1;
      }

      return var1;
   }
}
