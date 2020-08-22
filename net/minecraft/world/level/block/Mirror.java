package net.minecraft.world.level.block;

import net.minecraft.core.Direction;

public enum Mirror {
   NONE,
   LEFT_RIGHT,
   FRONT_BACK;

   public int mirror(int var1, int var2) {
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

   public Rotation getRotation(Direction var1) {
      Direction.Axis var2 = var1.getAxis();
      return (this != LEFT_RIGHT || var2 != Direction.Axis.Z) && (this != FRONT_BACK || var2 != Direction.Axis.X) ? Rotation.NONE : Rotation.CLOCKWISE_180;
   }

   public Direction mirror(Direction var1) {
      if (this == FRONT_BACK && var1.getAxis() == Direction.Axis.X) {
         return var1.getOpposite();
      } else {
         return this == LEFT_RIGHT && var1.getAxis() == Direction.Axis.Z ? var1.getOpposite() : var1;
      }
   }
}
