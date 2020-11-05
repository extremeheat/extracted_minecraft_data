package net.minecraft.world.phys.shapes;

import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.Util;
import net.minecraft.core.Direction;

public class ArrayVoxelShape extends VoxelShape {
   private final DoubleList xs;
   private final DoubleList ys;
   private final DoubleList zs;

   ArrayVoxelShape(DiscreteVoxelShape var1, DoubleList var2, DoubleList var3, DoubleList var4) {
      super(var1);
      int var5 = var1.getXSize() + 1;
      int var6 = var1.getYSize() + 1;
      int var7 = var1.getZSize() + 1;
      if (var5 == var2.size() && var6 == var3.size() && var7 == var4.size()) {
         this.xs = var2;
         this.ys = var3;
         this.zs = var4;
      } else {
         throw (IllegalArgumentException)Util.pauseInIde(new IllegalArgumentException("Lengths of point arrays must be consistent with the size of the VoxelShape."));
      }
   }

   protected DoubleList getCoords(Direction.Axis var1) {
      switch(var1) {
      case X:
         return this.xs;
      case Y:
         return this.ys;
      case Z:
         return this.zs;
      default:
         throw new IllegalArgumentException();
      }
   }
}
