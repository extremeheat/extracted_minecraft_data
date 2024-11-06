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

   private Rotation(final String var3, final OctahedralGroup var4) {
      this.id = var3;
      this.rotation = var4;
   }

   public Rotation getRotated(Rotation var1) {
      Rotation var10000;
      switch (var1.ordinal()) {
         case 1:
            switch (this.ordinal()) {
               case 0:
                  var10000 = CLOCKWISE_90;
                  return var10000;
               case 1:
                  var10000 = CLOCKWISE_180;
                  return var10000;
               case 2:
                  var10000 = COUNTERCLOCKWISE_90;
                  return var10000;
               case 3:
                  var10000 = NONE;
                  return var10000;
               default:
                  throw new MatchException((String)null, (Throwable)null);
            }
         case 2:
            switch (this.ordinal()) {
               case 0:
                  var10000 = CLOCKWISE_180;
                  return var10000;
               case 1:
                  var10000 = COUNTERCLOCKWISE_90;
                  return var10000;
               case 2:
                  var10000 = NONE;
                  return var10000;
               case 3:
                  var10000 = CLOCKWISE_90;
                  return var10000;
               default:
                  throw new MatchException((String)null, (Throwable)null);
            }
         case 3:
            switch (this.ordinal()) {
               case 0:
                  var10000 = COUNTERCLOCKWISE_90;
                  return var10000;
               case 1:
                  var10000 = NONE;
                  return var10000;
               case 2:
                  var10000 = CLOCKWISE_90;
                  return var10000;
               case 3:
                  var10000 = CLOCKWISE_180;
                  return var10000;
               default:
                  throw new MatchException((String)null, (Throwable)null);
            }
         default:
            var10000 = this;
            return var10000;
      }
   }

   public OctahedralGroup rotation() {
      return this.rotation;
   }

   public Direction rotate(Direction var1) {
      if (var1.getAxis() == Direction.Axis.Y) {
         return var1;
      } else {
         Direction var10000;
         switch (this.ordinal()) {
            case 1 -> var10000 = var1.getClockWise();
            case 2 -> var10000 = var1.getOpposite();
            case 3 -> var10000 = var1.getCounterClockWise();
            default -> var10000 = var1;
         }

         return var10000;
      }
   }

   public int rotate(int var1, int var2) {
      int var10000;
      switch (this.ordinal()) {
         case 1 -> var10000 = (var1 + var2 / 4) % var2;
         case 2 -> var10000 = (var1 + var2 / 2) % var2;
         case 3 -> var10000 = (var1 + var2 * 3 / 4) % var2;
         default -> var10000 = var1;
      }

      return var10000;
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
