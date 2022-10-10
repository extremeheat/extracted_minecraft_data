package net.minecraft.util.math.shapes;

import java.util.BitSet;
import net.minecraft.util.EnumFacing;

public final class VoxelShapePartBitSet extends VoxelShapePart {
   private final BitSet field_197853_e;
   private int field_199630_f;
   private int field_199631_g;
   private int field_199632_h;
   private int field_199633_i;
   private int field_199634_j;
   private int field_199635_k;

   public VoxelShapePartBitSet(int var1, int var2, int var3) {
      this(var1, var2, var3, var1, var2, var3, 0, 0, 0);
   }

   public VoxelShapePartBitSet(int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9) {
      super(var1, var2, var3);
      this.field_197853_e = new BitSet(var1 * var2 * var3);
      this.field_199630_f = var4;
      this.field_199631_g = var5;
      this.field_199632_h = var6;
      this.field_199633_i = var7;
      this.field_199634_j = var8;
      this.field_199635_k = var9;
   }

   public VoxelShapePartBitSet(VoxelShapePart var1) {
      super(var1.field_197838_b, var1.field_197839_c, var1.field_197840_d);
      if (var1 instanceof VoxelShapePartBitSet) {
         this.field_197853_e = (BitSet)((VoxelShapePartBitSet)var1).field_197853_e.clone();
      } else {
         this.field_197853_e = new BitSet(this.field_197838_b * this.field_197839_c * this.field_197840_d);

         for(int var2 = 0; var2 < this.field_197838_b; ++var2) {
            for(int var3 = 0; var3 < this.field_197839_c; ++var3) {
               for(int var4 = 0; var4 < this.field_197840_d; ++var4) {
                  if (var1.func_197835_b(var2, var3, var4)) {
                     this.field_197853_e.set(this.func_197848_a(var2, var3, var4));
                  }
               }
            }
         }
      }

      this.field_199630_f = var1.func_199623_a(EnumFacing.Axis.X);
      this.field_199631_g = var1.func_199623_a(EnumFacing.Axis.Y);
      this.field_199632_h = var1.func_199623_a(EnumFacing.Axis.Z);
      this.field_199633_i = var1.func_199624_b(EnumFacing.Axis.X);
      this.field_199634_j = var1.func_199624_b(EnumFacing.Axis.Y);
      this.field_199635_k = var1.func_199624_b(EnumFacing.Axis.Z);
   }

   protected int func_197848_a(int var1, int var2, int var3) {
      return (var1 * this.field_197839_c + var2) * this.field_197840_d + var3;
   }

   public boolean func_197835_b(int var1, int var2, int var3) {
      return this.field_197853_e.get(this.func_197848_a(var1, var2, var3));
   }

   public void func_199625_a(int var1, int var2, int var3, boolean var4, boolean var5) {
      this.field_197853_e.set(this.func_197848_a(var1, var2, var3), var5);
      if (var4 && var5) {
         this.field_199630_f = Math.min(this.field_199630_f, var1);
         this.field_199631_g = Math.min(this.field_199631_g, var2);
         this.field_199632_h = Math.min(this.field_199632_h, var3);
         this.field_199633_i = Math.max(this.field_199633_i, var1 + 1);
         this.field_199634_j = Math.max(this.field_199634_j, var2 + 1);
         this.field_199635_k = Math.max(this.field_199635_k, var3 + 1);
      }

   }

   public boolean func_197830_a() {
      return this.field_197853_e.isEmpty();
   }

   public int func_199623_a(EnumFacing.Axis var1) {
      return var1.func_196052_a(this.field_199630_f, this.field_199631_g, this.field_199632_h);
   }

   public int func_199624_b(EnumFacing.Axis var1) {
      return var1.func_196052_a(this.field_199633_i, this.field_199634_j, this.field_199635_k);
   }

   protected boolean func_197833_a(int var1, int var2, int var3, int var4) {
      if (var3 >= 0 && var4 >= 0 && var1 >= 0) {
         if (var3 < this.field_197838_b && var4 < this.field_197839_c && var2 <= this.field_197840_d) {
            return this.field_197853_e.nextClearBit(this.func_197848_a(var3, var4, var1)) >= this.func_197848_a(var3, var4, var2);
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   protected void func_197834_a(int var1, int var2, int var3, int var4, boolean var5) {
      this.field_197853_e.set(this.func_197848_a(var3, var4, var1), this.func_197848_a(var3, var4, var2), var5);
   }

   static VoxelShapePartBitSet func_197852_a(VoxelShapePart var0, VoxelShapePart var1, IDoubleListMerger var2, IDoubleListMerger var3, IDoubleListMerger var4, IBooleanFunction var5) {
      VoxelShapePartBitSet var6 = new VoxelShapePartBitSet(var2.func_212435_a().size() - 1, var3.func_212435_a().size() - 1, var4.func_212435_a().size() - 1);
      int[] var7 = new int[]{2147483647, 2147483647, 2147483647, -2147483648, -2147483648, -2147483648};
      var2.func_197855_a((var7x, var8, var9) -> {
         boolean[] var10 = new boolean[]{false};
         boolean var11 = var3.func_197855_a((var10x, var11x, var12) -> {
            boolean[] var13 = new boolean[]{false};
            boolean var14 = var4.func_197855_a((var12x, var13x, var14x) -> {
               boolean var15 = var5.apply(var0.func_197818_c(var7x, var10x, var12x), var1.func_197818_c(var8, var11x, var13x));
               if (var15) {
                  var6.field_197853_e.set(var6.func_197848_a(var9, var12, var14x));
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
      var6.field_199630_f = var7[0];
      var6.field_199631_g = var7[1];
      var6.field_199632_h = var7[2];
      var6.field_199633_i = var7[3] + 1;
      var6.field_199634_j = var7[4] + 1;
      var6.field_199635_k = var7[5] + 1;
      return var6;
   }
}
