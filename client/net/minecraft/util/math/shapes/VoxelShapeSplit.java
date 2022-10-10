package net.minecraft.util.math.shapes;

import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.util.EnumFacing;

public class VoxelShapeSplit extends VoxelShape {
   private final VoxelShape field_197776_a;
   private final EnumFacing.Axis field_197777_b;
   private final DoubleList field_197778_c = new DoubleRangeList(1);

   public VoxelShapeSplit(VoxelShape var1, EnumFacing.Axis var2, int var3) {
      super(func_197775_a(var1.field_197768_g, var2, var3));
      this.field_197776_a = var1;
      this.field_197777_b = var2;
   }

   private static VoxelShapePart func_197775_a(VoxelShapePart var0, EnumFacing.Axis var1, int var2) {
      return new VoxelShapePartSplit(var0, var1.func_196052_a(var2, 0, 0), var1.func_196052_a(0, var2, 0), var1.func_196052_a(0, 0, var2), var1.func_196052_a(var2 + 1, var0.field_197838_b, var0.field_197838_b), var1.func_196052_a(var0.field_197839_c, var2 + 1, var0.field_197839_c), var1.func_196052_a(var0.field_197840_d, var0.field_197840_d, var2 + 1));
   }

   protected DoubleList func_197757_a(EnumFacing.Axis var1) {
      return var1 == this.field_197777_b ? this.field_197778_c : this.field_197776_a.func_197757_a(var1);
   }
}
