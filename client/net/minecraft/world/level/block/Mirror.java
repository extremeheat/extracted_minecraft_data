package net.minecraft.world.level.block;

import com.mojang.math.OctahedralGroup;
import com.mojang.serialization.Codec;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;

public enum Mirror implements StringRepresentable {
   NONE("none", OctahedralGroup.IDENTITY),
   LEFT_RIGHT("left_right", OctahedralGroup.INVERT_Z),
   FRONT_BACK("front_back", OctahedralGroup.INVERT_X);

   public static final Codec<Mirror> CODEC = StringRepresentable.fromEnum(Mirror::values);
   private final String id;
   private final Component symbol;
   private final OctahedralGroup rotation;

   private Mirror(String var3, OctahedralGroup var4) {
      this.id = var3;
      this.symbol = Component.translatable("mirror." + var3);
      this.rotation = var4;
   }

   public int mirror(int var1, int var2) {
      int var3 = var2 / 2;
      int var4 = var1 > var3 ? var1 - var2 : var1;
      switch (this.ordinal()) {
         case 1 -> {
            return (var3 - var4 + var2) % var2;
         }
         case 2 -> {
            return (var2 - var4) % var2;
         }
         default -> {
            return var1;
         }
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

   public OctahedralGroup rotation() {
      return this.rotation;
   }

   public Component symbol() {
      return this.symbol;
   }

   public String getSerializedName() {
      return this.id;
   }

   // $FF: synthetic method
   private static Mirror[] $values() {
      return new Mirror[]{NONE, LEFT_RIGHT, FRONT_BACK};
   }
}
