package net.minecraft.util.math.shapes;

import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import java.util.Arrays;
import net.minecraft.util.EnumFacing;

final class VoxelShapeArray extends VoxelShape {
   private final DoubleList field_197782_a;
   private final DoubleList field_197783_b;
   private final DoubleList field_197784_c;

   VoxelShapeArray(VoxelShapePart var1, double[] var2, double[] var3, double[] var4) {
      this(var1, (DoubleList)DoubleArrayList.wrap(Arrays.copyOf(var2, var1.func_197823_b() + 1)), (DoubleList)DoubleArrayList.wrap(Arrays.copyOf(var3, var1.func_197820_c() + 1)), (DoubleList)DoubleArrayList.wrap(Arrays.copyOf(var4, var1.func_197821_d() + 1)));
   }

   VoxelShapeArray(VoxelShapePart var1, DoubleList var2, DoubleList var3, DoubleList var4) {
      super(var1);
      int var5 = var1.func_197823_b() + 1;
      int var6 = var1.func_197820_c() + 1;
      int var7 = var1.func_197821_d() + 1;
      if (var5 == var2.size() && var6 == var3.size() && var7 == var4.size()) {
         this.field_197782_a = var2;
         this.field_197783_b = var3;
         this.field_197784_c = var4;
      } else {
         throw new IllegalArgumentException("Lengths of point arrays must be consistent with the size of the VoxelShape.");
      }
   }

   protected DoubleList func_197757_a(EnumFacing.Axis var1) {
      switch(var1) {
      case X:
         return this.field_197782_a;
      case Y:
         return this.field_197783_b;
      case Z:
         return this.field_197784_c;
      default:
         throw new IllegalArgumentException();
      }
   }
}
