package net.minecraft.world.level.block;

import com.mojang.math.OctahedralGroup;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

public enum Mirror {
   NONE(new TranslatableComponent("mirror.none"), OctahedralGroup.IDENTITY),
   LEFT_RIGHT(new TranslatableComponent("mirror.left_right"), OctahedralGroup.INVERT_Z),
   FRONT_BACK(new TranslatableComponent("mirror.front_back"), OctahedralGroup.INVERT_X);

   private final Component symbol;
   private final OctahedralGroup rotation;

   private Mirror(Component var3, OctahedralGroup var4) {
      this.symbol = var3;
      this.rotation = var4;
   }

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
      return (this != LEFT_RIGHT || var2 != Direction.Axis.field_502) && (this != FRONT_BACK || var2 != Direction.Axis.field_500) ? Rotation.NONE : Rotation.CLOCKWISE_180;
   }

   public Direction mirror(Direction var1) {
      if (this == FRONT_BACK && var1.getAxis() == Direction.Axis.field_500) {
         return var1.getOpposite();
      } else {
         return this == LEFT_RIGHT && var1.getAxis() == Direction.Axis.field_502 ? var1.getOpposite() : var1;
      }
   }

   public OctahedralGroup rotation() {
      return this.rotation;
   }

   public Component symbol() {
      return this.symbol;
   }

   // $FF: synthetic method
   private static Mirror[] $values() {
      return new Mirror[]{NONE, LEFT_RIGHT, FRONT_BACK};
   }
}
