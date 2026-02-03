package net.minecraft.world.level.block;

import com.mojang.math.OctahedralGroup;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import java.util.List;
import java.util.function.IntFunction;
import net.minecraft.core.Direction;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.Util;

public enum Rotation implements StringRepresentable {
   NONE(0, "none", OctahedralGroup.IDENTITY),
   CLOCKWISE_90(1, "clockwise_90", OctahedralGroup.ROT_90_Y_NEG),
   CLOCKWISE_180(2, "180", OctahedralGroup.ROT_180_FACE_XZ),
   COUNTERCLOCKWISE_90(3, "counterclockwise_90", OctahedralGroup.ROT_90_Y_POS);

   public static final IntFunction<Rotation> BY_ID = ByIdMap.<Rotation>continuous(Rotation::getIndex, values(), ByIdMap.OutOfBoundsStrategy.WRAP);
   public static final Codec<Rotation> CODEC = StringRepresentable.<Rotation>fromEnum(Rotation::values);
   public static final StreamCodec<ByteBuf, Rotation> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, Rotation::getIndex);
   /** @deprecated */
   @Deprecated
   public static final Codec<Rotation> LEGACY_CODEC = ExtraCodecs.<Rotation>legacyEnum(Rotation::valueOf);
   private final int index;
   private final String id;
   private final OctahedralGroup rotation;

   private Rotation(final int index, final String id, final OctahedralGroup rotation) {
      this.index = index;
      this.id = id;
      this.rotation = rotation;
   }

   public Rotation getRotated(final Rotation rot) {
      Rotation var10000;
      switch (rot.ordinal()) {
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

   public Direction rotate(final Direction direction) {
      if (direction.getAxis() == Direction.Axis.Y) {
         return direction;
      } else {
         Direction var10000;
         switch (this.ordinal()) {
            case 1 -> var10000 = direction.getClockWise();
            case 2 -> var10000 = direction.getOpposite();
            case 3 -> var10000 = direction.getCounterClockWise();
            default -> var10000 = direction;
         }

         return var10000;
      }
   }

   public int rotate(final int rotation, final int steps) {
      int var10000;
      switch (this.ordinal()) {
         case 1 -> var10000 = (rotation + steps / 4) % steps;
         case 2 -> var10000 = (rotation + steps / 2) % steps;
         case 3 -> var10000 = (rotation + steps * 3 / 4) % steps;
         default -> var10000 = rotation;
      }

      return var10000;
   }

   public static Rotation getRandom(final RandomSource random) {
      return (Rotation)Util.getRandom(values(), random);
   }

   public static List<Rotation> getShuffled(final RandomSource random) {
      return Util.shuffledCopy(values(), random);
   }

   public String getSerializedName() {
      return this.id;
   }

   private int getIndex() {
      return this.index;
   }

   // $FF: synthetic method
   private static Rotation[] $values() {
      return new Rotation[]{NONE, CLOCKWISE_90, CLOCKWISE_180, COUNTERCLOCKWISE_90};
   }
}
