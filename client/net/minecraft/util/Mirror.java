package net.minecraft.util;

public enum Mirror {
   NONE,
   LEFT_RIGHT,
   FRONT_BACK;

   private Mirror() {
   }

   public int func_185802_a(int var1, int var2) {
      int var3 = var2 / 2;
      int var4 = var1 > var3 ? var1 - var2 : var1;
      switch(this) {
      case FRONT_BACK:
         return (var2 - var4) % var2;
      case LEFT_RIGHT:
         return (var3 - var4 + var2) % var2;
      default:
         return var1;
      }
   }

   public Rotation func_185800_a(EnumFacing var1) {
      EnumFacing.Axis var2 = var1.func_176740_k();
      return (this != LEFT_RIGHT || var2 != EnumFacing.Axis.Z) && (this != FRONT_BACK || var2 != EnumFacing.Axis.X) ? Rotation.NONE : Rotation.CLOCKWISE_180;
   }

   public EnumFacing func_185803_b(EnumFacing var1) {
      if (this == FRONT_BACK && var1.func_176740_k() == EnumFacing.Axis.X) {
         return var1.func_176734_d();
      } else {
         return this == LEFT_RIGHT && var1.func_176740_k() == EnumFacing.Axis.Z ? var1.func_176734_d() : var1;
      }
   }
}
