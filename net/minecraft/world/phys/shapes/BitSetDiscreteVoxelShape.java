package net.minecraft.world.phys.shapes;

import java.util.BitSet;
import net.minecraft.core.Direction;

public final class BitSetDiscreteVoxelShape extends DiscreteVoxelShape {
   private final BitSet storage;
   private int xMin;
   private int yMin;
   private int zMin;
   private int xMax;
   private int yMax;
   private int zMax;

   public BitSetDiscreteVoxelShape(int var1, int var2, int var3) {
      this(var1, var2, var3, var1, var2, var3, 0, 0, 0);
   }

   public BitSetDiscreteVoxelShape(int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9) {
      super(var1, var2, var3);
      this.storage = new BitSet(var1 * var2 * var3);
      this.xMin = var4;
      this.yMin = var5;
      this.zMin = var6;
      this.xMax = var7;
      this.yMax = var8;
      this.zMax = var9;
   }

   public BitSetDiscreteVoxelShape(DiscreteVoxelShape var1) {
      super(var1.xSize, var1.ySize, var1.zSize);
      if (var1 instanceof BitSetDiscreteVoxelShape) {
         this.storage = (BitSet)((BitSetDiscreteVoxelShape)var1).storage.clone();
      } else {
         this.storage = new BitSet(this.xSize * this.ySize * this.zSize);

         for(int var2 = 0; var2 < this.xSize; ++var2) {
            for(int var3 = 0; var3 < this.ySize; ++var3) {
               for(int var4 = 0; var4 < this.zSize; ++var4) {
                  if (var1.isFull(var2, var3, var4)) {
                     this.storage.set(this.getIndex(var2, var3, var4));
                  }
               }
            }
         }
      }

      this.xMin = var1.firstFull(Direction.Axis.X);
      this.yMin = var1.firstFull(Direction.Axis.Y);
      this.zMin = var1.firstFull(Direction.Axis.Z);
      this.xMax = var1.lastFull(Direction.Axis.X);
      this.yMax = var1.lastFull(Direction.Axis.Y);
      this.zMax = var1.lastFull(Direction.Axis.Z);
   }

   protected int getIndex(int var1, int var2, int var3) {
      return (var1 * this.ySize + var2) * this.zSize + var3;
   }

   public boolean isFull(int var1, int var2, int var3) {
      return this.storage.get(this.getIndex(var1, var2, var3));
   }

   public void setFull(int var1, int var2, int var3, boolean var4, boolean var5) {
      this.storage.set(this.getIndex(var1, var2, var3), var5);
      if (var4 && var5) {
         this.xMin = Math.min(this.xMin, var1);
         this.yMin = Math.min(this.yMin, var2);
         this.zMin = Math.min(this.zMin, var3);
         this.xMax = Math.max(this.xMax, var1 + 1);
         this.yMax = Math.max(this.yMax, var2 + 1);
         this.zMax = Math.max(this.zMax, var3 + 1);
      }

   }

   public boolean isEmpty() {
      return this.storage.isEmpty();
   }

   public int firstFull(Direction.Axis var1) {
      return var1.choose(this.xMin, this.yMin, this.zMin);
   }

   public int lastFull(Direction.Axis var1) {
      return var1.choose(this.xMax, this.yMax, this.zMax);
   }

   protected boolean isZStripFull(int var1, int var2, int var3, int var4) {
      if (var3 >= 0 && var4 >= 0 && var1 >= 0) {
         if (var3 < this.xSize && var4 < this.ySize && var2 <= this.zSize) {
            return this.storage.nextClearBit(this.getIndex(var3, var4, var1)) >= this.getIndex(var3, var4, var2);
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   protected void setZStrip(int var1, int var2, int var3, int var4, boolean var5) {
      this.storage.set(this.getIndex(var3, var4, var1), this.getIndex(var3, var4, var2), var5);
   }

   static BitSetDiscreteVoxelShape join(DiscreteVoxelShape var0, DiscreteVoxelShape var1, IndexMerger var2, IndexMerger var3, IndexMerger var4, BooleanOp var5) {
      BitSetDiscreteVoxelShape var6 = new BitSetDiscreteVoxelShape(var2.getList().size() - 1, var3.getList().size() - 1, var4.getList().size() - 1);
      int[] var7 = new int[]{Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE};
      var2.forMergedIndexes((var7x, var8, var9) -> {
         boolean[] var10 = new boolean[]{false};
         boolean var11 = var3.forMergedIndexes((var10x, var11x, var12) -> {
            boolean[] var13 = new boolean[]{false};
            boolean var14 = var4.forMergedIndexes((var12x, var13x, var14x) -> {
               boolean var15 = var5.apply(var0.isFullWide(var7x, var10x, var12x), var1.isFullWide(var8, var11x, var13x));
               if (var15) {
                  var6.storage.set(var6.getIndex(var9, var12, var14x));
                  var7[2] = Math.min(var7[2], var14x);
                  var7[5] = Math.max(var7[5], var14x);
                  var13[0] = true;
               }

               return true;
            });
            if (var13[0]) {
               var7[1] = Math.min(var7[1], var12);
               var7[4] = Math.max(var7[4], var12);
               var10[0] = true;
            }

            return var14;
         });
         if (var10[0]) {
            var7[0] = Math.min(var7[0], var9);
            var7[3] = Math.max(var7[3], var9);
         }

         return var11;
      });
      var6.xMin = var7[0];
      var6.yMin = var7[1];
      var6.zMin = var7[2];
      var6.xMax = var7[3] + 1;
      var6.yMax = var7[4] + 1;
      var6.zMax = var7[5] + 1;
      return var6;
   }
}
