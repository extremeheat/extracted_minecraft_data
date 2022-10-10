package net.minecraft.util;

public enum Rotation {
   NONE,
   CLOCKWISE_90,
   CLOCKWISE_180,
   COUNTERCLOCKWISE_90;

   private Rotation() {
   }

   public Rotation func_185830_a(Rotation var1) {
      switch(var1) {
      case CLOCKWISE_180:
         switch(this) {
         case NONE:
            return CLOCKWISE_180;
         case CLOCKWISE_90:
            return COUNTERCLOCKWISE_90;
         case CLOCKWISE_180:
            return NONE;
         case COUNTERCLOCKWISE_90:
            return CLOCKWISE_90;
         }
      case COUNTERCLOCKWISE_90:
         switch(this) {
         case NONE:
            return COUNTERCLOCKWISE_90;
         case CLOCKWISE_90:
            return NONE;
         case CLOCKWISE_180:
            return CLOCKWISE_90;
         case COUNTERCLOCKWISE_90:
            return CLOCKWISE_180;
         }
      case CLOCKWISE_90:
         switch(this) {
         case NONE:
            return CLOCKWISE_90;
         case CLOCKWISE_90:
            return CLOCKWISE_180;
         case CLOCKWISE_180:
            return COUNTERCLOCKWISE_90;
         case COUNTERCLOCKWISE_90:
            return NONE;
         }
      default:
         return this;
      }
   }

   public EnumFacing func_185831_a(EnumFacing var1) {
      if (var1.func_176740_k() == EnumFacing.Axis.Y) {
         return var1;
      } else {
         switch(this) {
         case CLOCKWISE_90:
            return var1.func_176746_e();
         case CLOCKWISE_180:
            return var1.func_176734_d();
         case COUNTERCLOCKWISE_90:
            return var1.func_176735_f();
         default:
            return var1;
         }
      }
   }

   public int func_185833_a(int var1, int var2) {
      switch(this) {
      case CLOCKWISE_90:
         return (var1 + var2 / 4) % var2;
      case CLOCKWISE_180:
         return (var1 + var2 / 2) % var2;
      case COUNTERCLOCKWISE_90:
         return (var1 + var2 * 3 / 4) % var2;
      default:
         return var1;
      }
   }
}
