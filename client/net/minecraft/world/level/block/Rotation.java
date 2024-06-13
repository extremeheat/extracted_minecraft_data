package net.minecraft.world.level.block;

import com.mojang.math.OctahedralGroup;
import com.mojang.serialization.Codec;
import java.util.List;
import net.minecraft.Util;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;

public enum Rotation implements StringRepresentable {
   NONE("none", OctahedralGroup.IDENTITY),
   CLOCKWISE_90("clockwise_90", OctahedralGroup.ROT_90_Y_NEG),
   CLOCKWISE_180("180", OctahedralGroup.ROT_180_FACE_XZ),
   COUNTERCLOCKWISE_90("counterclockwise_90", OctahedralGroup.ROT_90_Y_POS);

   public static final Codec<Rotation> CODEC = StringRepresentable.fromEnum(Rotation::values);
   private final String id;
   private final OctahedralGroup rotation;

   private Rotation(String var3, OctahedralGroup var4) {
      this.id = var3;
      this.rotation = var4;
   }

   public Rotation getRotated(Rotation var1) {
      switch (var1) {
         case CLOCKWISE_180:
            switch (this) {
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
            switch (this) {
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
            switch (this) {
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

   public OctahedralGroup rotation() {
      return this.rotation;
   }

   public Direction rotate(Direction var1) {
      if (var1.getAxis() == Direction.Axis.Y) {
         return var1;
      } else {
         switch (this) {
            case CLOCKWISE_90:
               return var1.getClockWise();
            case CLOCKWISE_180:
               return var1.getOpposite();
            case COUNTERCLOCKWISE_90:
               return var1.getCounterClockWise();
            default:
               return var1;
         }
      }
   }

   public int rotate(int var1, int var2) {
      switch (this) {
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

   public static Rotation getRandom(RandomSource var0) {
      return Util.getRandom(values(), var0);
   }

   public static List<Rotation> getShuffled(RandomSource var0) {
      return Util.shuffledCopy(values(), var0);
   }

   @Override
   public String getSerializedName() {
      return this.id;
   }
}
