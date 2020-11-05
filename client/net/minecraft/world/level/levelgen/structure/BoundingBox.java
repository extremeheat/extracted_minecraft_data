package net.minecraft.world.level.levelgen.structure;

import com.google.common.base.MoreObjects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.IntArrayTag;

public class BoundingBox {
   public int x0;
   public int y0;
   public int z0;
   public int x1;
   public int y1;
   public int z1;

   public BoundingBox() {
      super();
   }

   public BoundingBox(int[] var1) {
      super();
      if (var1.length == 6) {
         this.x0 = var1[0];
         this.y0 = var1[1];
         this.z0 = var1[2];
         this.x1 = var1[3];
         this.y1 = var1[4];
         this.z1 = var1[5];
      }

   }

   public static BoundingBox getUnknownBox() {
      return new BoundingBox(2147483647, 2147483647, 2147483647, -2147483648, -2147483648, -2147483648);
   }

   public static BoundingBox infinite() {
      return new BoundingBox(-2147483648, -2147483648, -2147483648, 2147483647, 2147483647, 2147483647);
   }

   public static BoundingBox orientBox(int var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, Direction var9) {
      switch(var9) {
      case NORTH:
         return new BoundingBox(var0 + var3, var1 + var4, var2 - var8 + 1 + var5, var0 + var6 - 1 + var3, var1 + var7 - 1 + var4, var2 + var5);
      case SOUTH:
         return new BoundingBox(var0 + var3, var1 + var4, var2 + var5, var0 + var6 - 1 + var3, var1 + var7 - 1 + var4, var2 + var8 - 1 + var5);
      case WEST:
         return new BoundingBox(var0 - var8 + 1 + var5, var1 + var4, var2 + var3, var0 + var5, var1 + var7 - 1 + var4, var2 + var6 - 1 + var3);
      case EAST:
         return new BoundingBox(var0 + var5, var1 + var4, var2 + var3, var0 + var8 - 1 + var5, var1 + var7 - 1 + var4, var2 + var6 - 1 + var3);
      default:
         return new BoundingBox(var0 + var3, var1 + var4, var2 + var5, var0 + var6 - 1 + var3, var1 + var7 - 1 + var4, var2 + var8 - 1 + var5);
      }
   }

   public static BoundingBox createProper(int var0, int var1, int var2, int var3, int var4, int var5) {
      return new BoundingBox(Math.min(var0, var3), Math.min(var1, var4), Math.min(var2, var5), Math.max(var0, var3), Math.max(var1, var4), Math.max(var2, var5));
   }

   public BoundingBox(int var1, int var2, int var3, int var4, int var5, int var6) {
      super();
      this.x0 = var1;
      this.y0 = var2;
      this.z0 = var3;
      this.x1 = var4;
      this.y1 = var5;
      this.z1 = var6;
   }

   public BoundingBox(Vec3i var1, Vec3i var2) {
      super();
      this.x0 = Math.min(var1.getX(), var2.getX());
      this.y0 = Math.min(var1.getY(), var2.getY());
      this.z0 = Math.min(var1.getZ(), var2.getZ());
      this.x1 = Math.max(var1.getX(), var2.getX());
      this.y1 = Math.max(var1.getY(), var2.getY());
      this.z1 = Math.max(var1.getZ(), var2.getZ());
   }

   public boolean intersects(BoundingBox var1) {
      return this.x1 >= var1.x0 && this.x0 <= var1.x1 && this.z1 >= var1.z0 && this.z0 <= var1.z1 && this.y1 >= var1.y0 && this.y0 <= var1.y1;
   }

   public boolean intersects(int var1, int var2, int var3, int var4) {
      return this.x1 >= var1 && this.x0 <= var3 && this.z1 >= var2 && this.z0 <= var4;
   }

   public void expand(BoundingBox var1) {
      this.x0 = Math.min(this.x0, var1.x0);
      this.y0 = Math.min(this.y0, var1.y0);
      this.z0 = Math.min(this.z0, var1.z0);
      this.x1 = Math.max(this.x1, var1.x1);
      this.y1 = Math.max(this.y1, var1.y1);
      this.z1 = Math.max(this.z1, var1.z1);
   }

   public void move(int var1, int var2, int var3) {
      this.x0 += var1;
      this.y0 += var2;
      this.z0 += var3;
      this.x1 += var1;
      this.y1 += var2;
      this.z1 += var3;
   }

   public BoundingBox moved(int var1, int var2, int var3) {
      return new BoundingBox(this.x0 + var1, this.y0 + var2, this.z0 + var3, this.x1 + var1, this.y1 + var2, this.z1 + var3);
   }

   public void move(Vec3i var1) {
      this.move(var1.getX(), var1.getY(), var1.getZ());
   }

   public boolean isInside(Vec3i var1) {
      return var1.getX() >= this.x0 && var1.getX() <= this.x1 && var1.getZ() >= this.z0 && var1.getZ() <= this.z1 && var1.getY() >= this.y0 && var1.getY() <= this.y1;
   }

   public Vec3i getLength() {
      return new Vec3i(this.x1 - this.x0, this.y1 - this.y0, this.z1 - this.z0);
   }

   public int getXSpan() {
      return this.x1 - this.x0 + 1;
   }

   public int getYSpan() {
      return this.y1 - this.y0 + 1;
   }

   public int getZSpan() {
      return this.z1 - this.z0 + 1;
   }

   public Vec3i getCenter() {
      return new BlockPos(this.x0 + (this.x1 - this.x0 + 1) / 2, this.y0 + (this.y1 - this.y0 + 1) / 2, this.z0 + (this.z1 - this.z0 + 1) / 2);
   }

   public String toString() {
      return MoreObjects.toStringHelper(this).add("x0", this.x0).add("y0", this.y0).add("z0", this.z0).add("x1", this.x1).add("y1", this.y1).add("z1", this.z1).toString();
   }

   public IntArrayTag createTag() {
      return new IntArrayTag(new int[]{this.x0, this.y0, this.z0, this.x1, this.y1, this.z1});
   }
}
