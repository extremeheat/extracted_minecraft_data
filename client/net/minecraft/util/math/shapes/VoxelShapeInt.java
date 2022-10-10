package net.minecraft.util.math.shapes;

import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.util.EnumFacing;

public final class VoxelShapeInt extends VoxelShape {
   private final int field_197779_a;
   private final int field_197780_b;
   private final int field_197781_c;

   public VoxelShapeInt(VoxelShapePart var1, int var2, int var3, int var4) {
      super(var1);
      this.field_197779_a = var2;
      this.field_197780_b = var3;
      this.field_197781_c = var4;
   }

   protected DoubleList func_197757_a(EnumFacing.Axis var1) {
      return new IntRangeList(this.field_197768_g.func_197819_a(var1), var1.func_196052_a(this.field_197779_a, this.field_197780_b, this.field_197781_c));
   }
}
