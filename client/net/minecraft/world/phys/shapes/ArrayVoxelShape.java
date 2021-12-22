package net.minecraft.world.phys.shapes;

import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import java.util.Arrays;
import net.minecraft.Util;
import net.minecraft.core.Direction;

public class ArrayVoxelShape extends VoxelShape {
   // $FF: renamed from: xs it.unimi.dsi.fastutil.doubles.DoubleList
   private final DoubleList field_123;
   // $FF: renamed from: ys it.unimi.dsi.fastutil.doubles.DoubleList
   private final DoubleList field_124;
   // $FF: renamed from: zs it.unimi.dsi.fastutil.doubles.DoubleList
   private final DoubleList field_125;

   protected ArrayVoxelShape(DiscreteVoxelShape var1, double[] var2, double[] var3, double[] var4) {
      this(var1, (DoubleList)DoubleArrayList.wrap(Arrays.copyOf(var2, var1.getXSize() + 1)), (DoubleList)DoubleArrayList.wrap(Arrays.copyOf(var3, var1.getYSize() + 1)), (DoubleList)DoubleArrayList.wrap(Arrays.copyOf(var4, var1.getZSize() + 1)));
   }

   ArrayVoxelShape(DiscreteVoxelShape var1, DoubleList var2, DoubleList var3, DoubleList var4) {
      super(var1);
      int var5 = var1.getXSize() + 1;
      int var6 = var1.getYSize() + 1;
      int var7 = var1.getZSize() + 1;
      if (var5 == var2.size() && var6 == var3.size() && var7 == var4.size()) {
         this.field_123 = var2;
         this.field_124 = var3;
         this.field_125 = var4;
      } else {
         throw (IllegalArgumentException)Util.pauseInIde(new IllegalArgumentException("Lengths of point arrays must be consistent with the size of the VoxelShape."));
      }
   }

   protected DoubleList getCoords(Direction.Axis var1) {
      switch(var1) {
      case field_500:
         return this.field_123;
      case field_501:
         return this.field_124;
      case field_502:
         return this.field_125;
      default:
         throw new IllegalArgumentException();
      }
   }
}
