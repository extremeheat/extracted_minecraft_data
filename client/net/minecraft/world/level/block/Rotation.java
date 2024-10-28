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
      switch (var1.ordinal()) {
         case 2:
            switch (this.ordinal()) {
               case 0 -> {
                  return CLOCKWISE_180;
               }
               case 1 -> {
                  return COUNTERCLOCKWISE_90;
               }
               case 2 -> {
                  return NONE;
               }
               case 3 -> {
                  return CLOCKWISE_90;
               }
            }
         case 3:
            switch (this.ordinal()) {
               case 0 -> {
                  return COUNTERCLOCKWISE_90;
               }
               case 1 -> {
                  return NONE;
               }
               case 2 -> {
                  return CLOCKWISE_90;
               }
               case 3 -> {
                  return CLOCKWISE_180;
               }
            }
         case 1:
            switch (this.ordinal()) {
               case 0 -> {
                  return CLOCKWISE_90;
               }
               case 1 -> {
                  return CLOCKWISE_180;
               }
               case 2 -> {
                  return COUNTERCLOCKWISE_90;
               }
               case 3 -> {
                  return NONE;
               }
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
         switch (this.ordinal()) {
            case 1 -> {
               return var1.getClockWise();
            }
            case 2 -> {
               return var1.getOpposite();
            }
            case 3 -> {
               return var1.getCounterClockWise();
            }
            default -> {
               return var1;
            }
         }
      }
   }

   public int rotate(int var1, int var2) {
      switch (this.ordinal()) {
         case 1 -> {
            return (var1 + var2 / 4) % var2;
         }
         case 2 -> {
            return (var1 + var2 / 2) % var2;
         }
         case 3 -> {
            return (var1 + var2 * 3 / 4) % var2;
         }
         default -> {
            return var1;
         }
      }
   }

   public static Rotation getRandom(RandomSource var0) {
      return (Rotation)Util.getRandom((Object[])values(), var0);
   }

   public static List<Rotation> getShuffled(RandomSource var0) {
      return Util.shuffledCopy((Object[])values(), var0);
   }

   public String getSerializedName() {
      return this.id;
   }

   // $FF: synthetic method
   private static Rotation[] $values() {
      return new Rotation[]{NONE, CLOCKWISE_90, CLOCKWISE_180, COUNTERCLOCKWISE_90};
   }
}
