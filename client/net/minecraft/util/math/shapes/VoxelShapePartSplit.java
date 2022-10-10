package net.minecraft.util.math.shapes;

import net.minecraft.util.EnumFacing;

final class VoxelShapePartSplit extends VoxelShapePart {
   private final VoxelShapePart field_197847_k;
   private final int field_197841_e;
   private final int field_197842_f;
   private final int field_197843_g;
   private final int field_197844_h;
   private final int field_197845_i;
   private final int field_197846_j;

   public VoxelShapePartSplit(VoxelShapePart var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      super(var5 - var2, var6 - var3, var7 - var4);
      this.field_197847_k = var1;
      this.field_197841_e = var2;
      this.field_197842_f = var3;
      this.field_197843_g = var4;
      this.field_197844_h = var5;
      this.field_197845_i = var6;
      this.field_197846_j = var7;
   }

   public boolean func_197835_b(int var1, int var2, int var3) {
      return this.field_197847_k.func_197835_b(this.field_197841_e + var1, this.field_197842_f + var2, this.field_197843_g + var3);
   }

   public void func_199625_a(int var1, int var2, int var3, boolean var4, boolean var5) {
      this.field_197847_k.func_199625_a(this.field_197841_e + var1, this.field_197842_f + var2, this.field_197843_g + var3, var4, var5);
   }

   public int func_199623_a(EnumFacing.Axis var1) {
      return Math.max(0, this.field_197847_k.func_199623_a(var1) - var1.func_196052_a(this.field_197841_e, this.field_197842_f, this.field_197843_g));
   }

   public int func_199624_b(EnumFacing.Axis var1) {
      return Math.min(var1.func_196052_a(this.field_197844_h, this.field_197845_i, this.field_197846_j), this.field_197847_k.func_199624_b(var1) - var1.func_196052_a(this.field_197841_e, this.field_197842_f, this.field_197843_g));
   }
}
