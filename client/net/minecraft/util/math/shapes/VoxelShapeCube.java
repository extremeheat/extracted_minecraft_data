package net.minecraft.util.math.shapes;

import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.util.EnumFacing;

final class VoxelShapeCube extends VoxelShape {
   VoxelShapeCube(VoxelShapePart var1) {
      super(var1);
   }

   protected DoubleList func_197757_a(EnumFacing.Axis var1) {
      return new DoubleRangeList(this.field_197768_g.func_197819_a(var1));
   }
}
