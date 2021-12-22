package net.minecraft.core;

public class Cursor3D {
   public static final int TYPE_INSIDE = 0;
   public static final int TYPE_FACE = 1;
   public static final int TYPE_EDGE = 2;
   public static final int TYPE_CORNER = 3;
   private final int originX;
   private final int originY;
   private final int originZ;
   private final int width;
   private final int height;
   private final int depth;
   private final int end;
   private int index;
   // $FF: renamed from: x int
   private int field_273;
   // $FF: renamed from: y int
   private int field_274;
   // $FF: renamed from: z int
   private int field_275;

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
         this.field_273 = this.index % this.width;
         int var1 = this.index / this.width;
         this.field_274 = var1 % this.height;
         this.field_275 = var1 / this.height;
         ++this.index;
         return true;
      }
   }

   public int nextX() {
      return this.originX + this.field_273;
   }

   public int nextY() {
      return this.originY + this.field_274;
   }

   public int nextZ() {
      return this.originZ + this.field_275;
   }

   public int getNextType() {
      int var1 = 0;
      if (this.field_273 == 0 || this.field_273 == this.width - 1) {
         ++var1;
      }

      if (this.field_274 == 0 || this.field_274 == this.height - 1) {
         ++var1;
      }

      if (this.field_275 == 0 || this.field_275 == this.depth - 1) {
         ++var1;
      }

      return var1;
   }
}
