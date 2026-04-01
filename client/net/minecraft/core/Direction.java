package net.minecraft.core;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterators;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import io.netty.buffer.ByteBuf;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.Util;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Contract;
import org.joml.Matrix4fc;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

public enum Direction implements StringRepresentable {
   DOWN(0, 1, -1, "down", Direction.AxisDirection.NEGATIVE, Direction.Axis.Y, new Vec3i(0, -1, 0)),
   UP(1, 0, -1, "up", Direction.AxisDirection.POSITIVE, Direction.Axis.Y, new Vec3i(0, 1, 0)),
   NORTH(2, 3, 2, "north", Direction.AxisDirection.NEGATIVE, Direction.Axis.Z, new Vec3i(0, 0, -1)),
   SOUTH(3, 2, 0, "south", Direction.AxisDirection.POSITIVE, Direction.Axis.Z, new Vec3i(0, 0, 1)),
   WEST(4, 5, 1, "west", Direction.AxisDirection.NEGATIVE, Direction.Axis.X, new Vec3i(-1, 0, 0)),
   EAST(5, 4, 3, "east", Direction.AxisDirection.POSITIVE, Direction.Axis.X, new Vec3i(1, 0, 0));

   public static final StringRepresentable.EnumCodec<Direction> CODEC = StringRepresentable.<Direction>fromEnum(Direction::values);
   public static final Codec<Direction> VERTICAL_CODEC = CODEC.validate(Direction::verifyVertical);
   public static final IntFunction<Direction> BY_ID = ByIdMap.<Direction>continuous(Direction::get3DDataValue, values(), ByIdMap.OutOfBoundsStrategy.WRAP);
   public static final StreamCodec<ByteBuf, Direction> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, Direction::get3DDataValue);
   /** @deprecated */
   @Deprecated
   public static final Codec<Direction> LEGACY_ID_CODEC = Codec.BYTE.xmap(Direction::from3DDataValue, (d) -> (byte)d.get3DDataValue());
   /** @deprecated */
   @Deprecated
   public static final Codec<Direction> LEGACY_ID_CODEC_2D = Codec.BYTE.xmap(Direction::from2DDataValue, (d) -> (byte)d.get2DDataValue());
   private static final ImmutableList<Axis> YXZ_AXIS_ORDER = ImmutableList.of(Direction.Axis.Y, Direction.Axis.X, Direction.Axis.Z);
   private static final ImmutableList<Axis> YZX_AXIS_ORDER = ImmutableList.of(Direction.Axis.Y, Direction.Axis.Z, Direction.Axis.X);
   private final int data3d;
   private final int oppositeIndex;
   private final int data2d;
   private final String name;
   private final Axis axis;
   private final AxisDirection axisDirection;
   private final Vec3i normal;
   private final Vec3 normalVec3;
   private final Vector3fc normalVec3f;
   private static final Direction[] VALUES = values();
   private static final Direction[] BY_3D_DATA = (Direction[])Arrays.stream(VALUES).sorted(Comparator.comparingInt((d) -> d.data3d)).toArray((x$0) -> new Direction[x$0]);
   private static final Direction[] BY_2D_DATA = (Direction[])Arrays.stream(VALUES).filter((d) -> d.getAxis().isHorizontal()).sorted(Comparator.comparingInt((d) -> d.data2d)).toArray((x$0) -> new Direction[x$0]);

   private Direction(final int data3d, final int oppositeIndex, final int data2d, final String name, final AxisDirection axisDirection, final Axis axis, final Vec3i normal) {
      this.data3d = data3d;
      this.data2d = data2d;
      this.oppositeIndex = oppositeIndex;
      this.name = name;
      this.axis = axis;
      this.axisDirection = axisDirection;
      this.normal = normal;
      this.normalVec3 = Vec3.atLowerCornerOf(normal);
      this.normalVec3f = new Vector3f((float)normal.getX(), (float)normal.getY(), (float)normal.getZ());
   }

   public static Direction[] orderedByNearest(final Entity entity) {
      float pitch = entity.getViewXRot(1.0F) * 0.017453292F;
      float yaw = -entity.getViewYRot(1.0F) * 0.017453292F;
      float pitchSin = Mth.sin((double)pitch);
      float pitchCos = Mth.cos((double)pitch);
      float yawSin = Mth.sin((double)yaw);
      float yawCos = Mth.cos((double)yaw);
      boolean xPos = yawSin > 0.0F;
      boolean yPos = pitchSin < 0.0F;
      boolean zPos = yawCos > 0.0F;
      float xYaw = xPos ? yawSin : -yawSin;
      float yMag = yPos ? -pitchSin : pitchSin;
      float zYaw = zPos ? yawCos : -yawCos;
      float xMag = xYaw * pitchCos;
      float zMag = zYaw * pitchCos;
      Direction axisX = xPos ? EAST : WEST;
      Direction axisY = yPos ? UP : DOWN;
      Direction axisZ = zPos ? SOUTH : NORTH;
      if (xYaw > zYaw) {
         if (yMag > xMag) {
            return makeDirectionArray(axisY, axisX, axisZ);
         } else {
            return zMag > yMag ? makeDirectionArray(axisX, axisZ, axisY) : makeDirectionArray(axisX, axisY, axisZ);
         }
      } else if (yMag > zMag) {
         return makeDirectionArray(axisY, axisZ, axisX);
      } else {
         return xMag > yMag ? makeDirectionArray(axisZ, axisX, axisY) : makeDirectionArray(axisZ, axisY, axisX);
      }
   }

   private static Direction[] makeDirectionArray(final Direction axis1, final Direction axis2, final Direction axis3) {
      return new Direction[]{axis1, axis2, axis3, axis3.getOpposite(), axis2.getOpposite(), axis1.getOpposite()};
   }

   public static Direction rotate(final Matrix4fc matrix, final Direction facing) {
      Vector3f vec = matrix.transformDirection(facing.normalVec3f, new Vector3f());
      return getApproximateNearest(vec.x(), vec.y(), vec.z());
   }

   public static Collection<Direction> allShuffled(final RandomSource random) {
      return Util.shuffledCopy(values(), random);
   }

   public static Stream<Direction> stream() {
      return Stream.of(VALUES);
   }

   public static float getYRot(final Direction direction) {
      float var10000;
      switch (direction.ordinal()) {
         case 2 -> var10000 = 180.0F;
         case 3 -> var10000 = 0.0F;
         case 4 -> var10000 = 90.0F;
         case 5 -> var10000 = -90.0F;
         default -> throw new IllegalStateException("No y-Rot for vertical axis: " + String.valueOf(direction));
      }

      return var10000;
   }

   public Quaternionf getRotation() {
      Quaternionf var10000;
      switch (this.ordinal()) {
         case 0 -> var10000 = (new Quaternionf()).rotationX(3.1415927F);
         case 1 -> var10000 = new Quaternionf();
         case 2 -> var10000 = (new Quaternionf()).rotationXYZ(1.5707964F, 0.0F, 3.1415927F);
         case 3 -> var10000 = (new Quaternionf()).rotationX(1.5707964F);
         case 4 -> var10000 = (new Quaternionf()).rotationXYZ(1.5707964F, 0.0F, 1.5707964F);
         case 5 -> var10000 = (new Quaternionf()).rotationXYZ(1.5707964F, 0.0F, -1.5707964F);
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public int get3DDataValue() {
      return this.data3d;
   }

   public int get2DDataValue() {
      return this.data2d;
   }

   public AxisDirection getAxisDirection() {
      return this.axisDirection;
   }

   public static Direction getFacingAxis(final Entity entity, final Axis axis) {
      Direction var10000;
      switch (axis.ordinal()) {
         case 0 -> var10000 = EAST.isFacingAngle(entity.getViewYRot(1.0F)) ? EAST : WEST;
         case 1 -> var10000 = entity.getViewXRot(1.0F) < 0.0F ? UP : DOWN;
         case 2 -> var10000 = SOUTH.isFacingAngle(entity.getViewYRot(1.0F)) ? SOUTH : NORTH;
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public Direction getOpposite() {
      return from3DDataValue(this.oppositeIndex);
   }

   public Direction getClockWise(final Axis axis) {
      Direction var10000;
      switch (axis.ordinal()) {
         case 0 -> var10000 = this != WEST && this != EAST ? this.getClockWiseX() : this;
         case 1 -> var10000 = this != UP && this != DOWN ? this.getClockWise() : this;
         case 2 -> var10000 = this != NORTH && this != SOUTH ? this.getClockWiseZ() : this;
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public Direction getCounterClockWise(final Axis axis) {
      Direction var10000;
      switch (axis.ordinal()) {
         case 0 -> var10000 = this != WEST && this != EAST ? this.getCounterClockWiseX() : this;
         case 1 -> var10000 = this != UP && this != DOWN ? this.getCounterClockWise() : this;
         case 2 -> var10000 = this != NORTH && this != SOUTH ? this.getCounterClockWiseZ() : this;
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public Direction getClockWise() {
      Direction var10000;
      switch (this.ordinal()) {
         case 2 -> var10000 = EAST;
         case 3 -> var10000 = WEST;
         case 4 -> var10000 = NORTH;
         case 5 -> var10000 = SOUTH;
         default -> throw new IllegalStateException("Unable to get Y-rotated facing of " + String.valueOf(this));
      }

      return var10000;
   }

   private Direction getClockWiseX() {
      Direction var10000;
      switch (this.ordinal()) {
         case 0 -> var10000 = SOUTH;
         case 1 -> var10000 = NORTH;
         case 2 -> var10000 = DOWN;
         case 3 -> var10000 = UP;
         default -> throw new IllegalStateException("Unable to get X-rotated facing of " + String.valueOf(this));
      }

      return var10000;
   }

   private Direction getCounterClockWiseX() {
      Direction var10000;
      switch (this.ordinal()) {
         case 0 -> var10000 = NORTH;
         case 1 -> var10000 = SOUTH;
         case 2 -> var10000 = UP;
         case 3 -> var10000 = DOWN;
         default -> throw new IllegalStateException("Unable to get X-rotated facing of " + String.valueOf(this));
      }

      return var10000;
   }

   private Direction getClockWiseZ() {
      Direction var10000;
      switch (this.ordinal()) {
         case 0:
            var10000 = WEST;
            break;
         case 1:
            var10000 = EAST;
            break;
         case 2:
         case 3:
         default:
            throw new IllegalStateException("Unable to get Z-rotated facing of " + String.valueOf(this));
         case 4:
            var10000 = UP;
            break;
         case 5:
            var10000 = DOWN;
      }

      return var10000;
   }

   private Direction getCounterClockWiseZ() {
      Direction var10000;
      switch (this.ordinal()) {
         case 0:
            var10000 = EAST;
            break;
         case 1:
            var10000 = WEST;
            break;
         case 2:
         case 3:
         default:
            throw new IllegalStateException("Unable to get Z-rotated facing of " + String.valueOf(this));
         case 4:
            var10000 = DOWN;
            break;
         case 5:
            var10000 = UP;
      }

      return var10000;
   }

   public Direction getCounterClockWise() {
      Direction var10000;
      switch (this.ordinal()) {
         case 2 -> var10000 = WEST;
         case 3 -> var10000 = EAST;
         case 4 -> var10000 = SOUTH;
         case 5 -> var10000 = NORTH;
         default -> throw new IllegalStateException("Unable to get CCW facing of " + String.valueOf(this));
      }

      return var10000;
   }

   public int getStepX() {
      return this.normal.getX();
   }

   public int getStepY() {
      return this.normal.getY();
   }

   public int getStepZ() {
      return this.normal.getZ();
   }

   public Vector3f step() {
      return new Vector3f(this.normalVec3f);
   }

   public String getName() {
      return this.name;
   }

   public Axis getAxis() {
      return this.axis;
   }

   public static @Nullable Direction byName(final String name) {
      return CODEC.byName(name);
   }

   public static Direction from3DDataValue(final int data) {
      return BY_3D_DATA[Mth.abs(data % BY_3D_DATA.length)];
   }

   public static Direction from2DDataValue(final int data) {
      return BY_2D_DATA[Mth.abs(data % BY_2D_DATA.length)];
   }

   public static Direction fromYRot(final double yRot) {
      return from2DDataValue(Mth.floor(yRot / 90.0 + 0.5) & 3);
   }

   public static Direction fromAxisAndDirection(final Axis axis, final AxisDirection direction) {
      Direction var10000;
      switch (axis.ordinal()) {
         case 0 -> var10000 = direction == Direction.AxisDirection.POSITIVE ? EAST : WEST;
         case 1 -> var10000 = direction == Direction.AxisDirection.POSITIVE ? UP : DOWN;
         case 2 -> var10000 = direction == Direction.AxisDirection.POSITIVE ? SOUTH : NORTH;
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public float toYRot() {
      return (float)((this.data2d & 3) * 90);
   }

   public static Direction getRandom(final RandomSource random) {
      return (Direction)Util.getRandom(VALUES, random);
   }

   public static Direction getApproximateNearest(final double dx, final double dy, final double dz) {
      return getApproximateNearest((float)dx, (float)dy, (float)dz);
   }

   public static Direction getApproximateNearest(final float dx, final float dy, final float dz) {
      Direction result = NORTH;
      float highestDot = 1.4E-45F;

      for(Direction direction : VALUES) {
         float dot = dx * (float)direction.normal.getX() + dy * (float)direction.normal.getY() + dz * (float)direction.normal.getZ();
         if (dot > highestDot) {
            highestDot = dot;
            result = direction;
         }
      }

      return result;
   }

   public static Direction getApproximateNearest(final Vec3 vec) {
      return getApproximateNearest(vec.x, vec.y, vec.z);
   }

   @Contract("_,_,_,!null->!null;_,_,_,_->_")
   public static @Nullable Direction getNearest(final float x, final float y, final float z, final @Nullable Direction orElse) {
      float absX = Math.abs(x);
      float absY = Math.abs(y);
      float absZ = Math.abs(z);
      if (absX > absZ && absX > absY) {
         return x < 0.0F ? WEST : EAST;
      } else if (absZ > absX && absZ > absY) {
         return z < 0.0F ? NORTH : SOUTH;
      } else if (absY > absX && absY > absZ) {
         return y < 0.0F ? DOWN : UP;
      } else {
         return orElse;
      }
   }

   @Contract("_,!null->!null;_,_->_")
   public static @Nullable Direction getNearest(final Vec3i vec, final @Nullable Direction orElse) {
      return getNearest((float)vec.getX(), (float)vec.getY(), (float)vec.getZ(), orElse);
   }

   @Contract("_,!null->!null;_,_->_")
   public static @Nullable Direction getNearest(final Vector3fc vec, final @Nullable Direction orElse) {
      return getNearest(vec.x(), vec.y(), vec.z(), orElse);
   }

   public String toString() {
      return this.name;
   }

   public String getSerializedName() {
      return this.name;
   }

   private static DataResult<Direction> verifyVertical(final Direction v) {
      return v.getAxis().isVertical() ? DataResult.success(v) : DataResult.error(() -> "Expected a vertical direction");
   }

   public static Direction get(final AxisDirection axisDirection, final Axis axis) {
      for(Direction direction : VALUES) {
         if (direction.getAxisDirection() == axisDirection && direction.getAxis() == axis) {
            return direction;
         }
      }

      String var10002 = String.valueOf(axisDirection);
      throw new IllegalArgumentException("No such direction: " + var10002 + " " + String.valueOf(axis));
   }

   public static ImmutableList<Axis> axisStepOrder(final Vec3 movement) {
      return Math.abs(movement.x) < Math.abs(movement.z) ? YZX_AXIS_ORDER : YXZ_AXIS_ORDER;
   }

   public Vec3i getUnitVec3i() {
      return this.normal;
   }

   public Vec3 getUnitVec3() {
      return this.normalVec3;
   }

   public Vector3fc getUnitVec3f() {
      return this.normalVec3f;
   }

   public boolean isFacingAngle(final float yAngle) {
      float radians = yAngle * 0.017453292F;
      float dx = -Mth.sin((double)radians);
      float dz = Mth.cos((double)radians);
      return (float)this.normal.getX() * dx + (float)this.normal.getZ() * dz > 0.0F;
   }

   // $FF: synthetic method
   private static Direction[] $values() {
      return new Direction[]{DOWN, UP, NORTH, SOUTH, WEST, EAST};
   }

   public static enum Axis implements Predicate<Direction>, StringRepresentable {
      X("x") {
         public int choose(final int x, final int y, final int z) {
            return x;
         }

         public boolean choose(final boolean x, final boolean y, final boolean z) {
            return x;
         }

         public double choose(final double x, final double y, final double z) {
            return x;
         }

         public Direction getPositive() {
            return Direction.EAST;
         }

         public Direction getNegative() {
            return Direction.WEST;
         }
      },
      Y("y") {
         public int choose(final int x, final int y, final int z) {
            return y;
         }

         public double choose(final double x, final double y, final double z) {
            return y;
         }

         public boolean choose(final boolean x, final boolean y, final boolean z) {
            return y;
         }

         public Direction getPositive() {
            return Direction.UP;
         }

         public Direction getNegative() {
            return Direction.DOWN;
         }
      },
      Z("z") {
         public int choose(final int x, final int y, final int z) {
            return z;
         }

         public double choose(final double x, final double y, final double z) {
            return z;
         }

         public boolean choose(final boolean x, final boolean y, final boolean z) {
            return z;
         }

         public Direction getPositive() {
            return Direction.SOUTH;
         }

         public Direction getNegative() {
            return Direction.NORTH;
         }
      };

      public static final Axis[] VALUES = values();
      public static final StringRepresentable.EnumCodec<Axis> CODEC = StringRepresentable.<Axis>fromEnum(Axis::values);
      private final String name;

      private Axis(final String name) {
         this.name = name;
      }

      public static @Nullable Axis byName(final String name) {
         return CODEC.byName(name);
      }

      public String getName() {
         return this.name;
      }

      public boolean isVertical() {
         return this == Y;
      }

      public boolean isHorizontal() {
         return this == X || this == Z;
      }

      public abstract Direction getPositive();

      public abstract Direction getNegative();

      public Direction[] getDirections() {
         return new Direction[]{this.getPositive(), this.getNegative()};
      }

      public String toString() {
         return this.name;
      }

      public static Axis getRandom(final RandomSource random) {
         return (Axis)Util.getRandom(VALUES, random);
      }

      public boolean test(final @Nullable Direction input) {
         return input != null && input.getAxis() == this;
      }

      public Plane getPlane() {
         Plane var10000;
         switch (this.ordinal()) {
            case 0:
            case 2:
               var10000 = Direction.Plane.HORIZONTAL;
               break;
            case 1:
               var10000 = Direction.Plane.VERTICAL;
               break;
            default:
               throw new MatchException((String)null, (Throwable)null);
         }

         return var10000;
      }

      public String getSerializedName() {
         return this.name;
      }

      public abstract int choose(final int x, final int y, final int z);

      public abstract double choose(final double x, final double y, final double z);

      public abstract boolean choose(final boolean x, final boolean y, final boolean z);

      // $FF: synthetic method
      private static Axis[] $values() {
         return new Axis[]{X, Y, Z};
      }
   }

   public static enum AxisDirection {
      POSITIVE(1, "Towards positive"),
      NEGATIVE(-1, "Towards negative");

      private final int step;
      private final String name;

      private AxisDirection(final int step, final String name) {
         this.step = step;
         this.name = name;
      }

      public int getStep() {
         return this.step;
      }

      public String getName() {
         return this.name;
      }

      public String toString() {
         return this.name;
      }

      public AxisDirection opposite() {
         return this == POSITIVE ? NEGATIVE : POSITIVE;
      }

      // $FF: synthetic method
      private static AxisDirection[] $values() {
         return new AxisDirection[]{POSITIVE, NEGATIVE};
      }
   }

   public static enum Plane implements Predicate<Direction>, Iterable<Direction> {
      HORIZONTAL(new Direction[]{Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST}, new Axis[]{Direction.Axis.X, Direction.Axis.Z}),
      VERTICAL(new Direction[]{Direction.UP, Direction.DOWN}, new Axis[]{Direction.Axis.Y});

      private final Direction[] faces;
      private final Axis[] axis;

      private Plane(final Direction[] faces, final Axis[] axis) {
         this.faces = faces;
         this.axis = axis;
      }

      public Direction getRandomDirection(final RandomSource random) {
         return (Direction)Util.getRandom(this.faces, random);
      }

      public Axis getRandomAxis(final RandomSource random) {
         return (Axis)Util.getRandom(this.axis, random);
      }

      public boolean test(final @Nullable Direction input) {
         return input != null && input.getAxis().getPlane() == this;
      }

      public Iterator<Direction> iterator() {
         return Iterators.forArray(this.faces);
      }

      public Stream<Direction> stream() {
         return Arrays.stream(this.faces);
      }

      public List<Direction> shuffledCopy(final RandomSource random) {
         return Util.shuffledCopy(this.faces, random);
      }

      public int length() {
         return this.faces.length;
      }

      // $FF: synthetic method
      private static Plane[] $values() {
         return new Plane[]{HORIZONTAL, VERTICAL};
      }
   }
}
