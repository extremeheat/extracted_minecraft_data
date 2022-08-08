package net.minecraft.core;

import com.google.common.collect.Iterators;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;

public enum Direction implements StringRepresentable {
   DOWN(0, 1, -1, "down", Direction.AxisDirection.NEGATIVE, Direction.Axis.Y, new Vec3i(0, -1, 0)),
   UP(1, 0, -1, "up", Direction.AxisDirection.POSITIVE, Direction.Axis.Y, new Vec3i(0, 1, 0)),
   NORTH(2, 3, 2, "north", Direction.AxisDirection.NEGATIVE, Direction.Axis.Z, new Vec3i(0, 0, -1)),
   SOUTH(3, 2, 0, "south", Direction.AxisDirection.POSITIVE, Direction.Axis.Z, new Vec3i(0, 0, 1)),
   WEST(4, 5, 1, "west", Direction.AxisDirection.NEGATIVE, Direction.Axis.X, new Vec3i(-1, 0, 0)),
   EAST(5, 4, 3, "east", Direction.AxisDirection.POSITIVE, Direction.Axis.X, new Vec3i(1, 0, 0));

   public static final StringRepresentable.EnumCodec<Direction> CODEC = StringRepresentable.fromEnum(Direction::values);
   public static final Codec<Direction> VERTICAL_CODEC = CODEC.flatXmap(Direction::verifyVertical, Direction::verifyVertical);
   private final int data3d;
   private final int oppositeIndex;
   private final int data2d;
   private final String name;
   private final Axis axis;
   private final AxisDirection axisDirection;
   private final Vec3i normal;
   private static final Direction[] VALUES = values();
   private static final Direction[] BY_3D_DATA = (Direction[])Arrays.stream(VALUES).sorted(Comparator.comparingInt((var0) -> {
      return var0.data3d;
   })).toArray((var0) -> {
      return new Direction[var0];
   });
   private static final Direction[] BY_2D_DATA = (Direction[])Arrays.stream(VALUES).filter((var0) -> {
      return var0.getAxis().isHorizontal();
   }).sorted(Comparator.comparingInt((var0) -> {
      return var0.data2d;
   })).toArray((var0) -> {
      return new Direction[var0];
   });
   private static final Long2ObjectMap<Direction> BY_NORMAL = (Long2ObjectMap)Arrays.stream(VALUES).collect(Collectors.toMap((var0) -> {
      return (new BlockPos(var0.getNormal())).asLong();
   }, (var0) -> {
      return var0;
   }, (var0, var1) -> {
      throw new IllegalArgumentException("Duplicate keys");
   }, Long2ObjectOpenHashMap::new));

   private Direction(int var3, int var4, int var5, String var6, AxisDirection var7, Axis var8, Vec3i var9) {
      this.data3d = var3;
      this.data2d = var5;
      this.oppositeIndex = var4;
      this.name = var6;
      this.axis = var8;
      this.axisDirection = var7;
      this.normal = var9;
   }

   public static Direction[] orderedByNearest(Entity var0) {
      float var1 = var0.getViewXRot(1.0F) * 0.017453292F;
      float var2 = -var0.getViewYRot(1.0F) * 0.017453292F;
      float var3 = Mth.sin(var1);
      float var4 = Mth.cos(var1);
      float var5 = Mth.sin(var2);
      float var6 = Mth.cos(var2);
      boolean var7 = var5 > 0.0F;
      boolean var8 = var3 < 0.0F;
      boolean var9 = var6 > 0.0F;
      float var10 = var7 ? var5 : -var5;
      float var11 = var8 ? -var3 : var3;
      float var12 = var9 ? var6 : -var6;
      float var13 = var10 * var4;
      float var14 = var12 * var4;
      Direction var15 = var7 ? EAST : WEST;
      Direction var16 = var8 ? UP : DOWN;
      Direction var17 = var9 ? SOUTH : NORTH;
      if (var10 > var12) {
         if (var11 > var13) {
            return makeDirectionArray(var16, var15, var17);
         } else {
            return var14 > var11 ? makeDirectionArray(var15, var17, var16) : makeDirectionArray(var15, var16, var17);
         }
      } else if (var11 > var14) {
         return makeDirectionArray(var16, var17, var15);
      } else {
         return var13 > var11 ? makeDirectionArray(var17, var15, var16) : makeDirectionArray(var17, var16, var15);
      }
   }

   private static Direction[] makeDirectionArray(Direction var0, Direction var1, Direction var2) {
      return new Direction[]{var0, var1, var2, var2.getOpposite(), var1.getOpposite(), var0.getOpposite()};
   }

   public static Direction rotate(Matrix4f var0, Direction var1) {
      Vec3i var2 = var1.getNormal();
      Vector4f var3 = new Vector4f((float)var2.getX(), (float)var2.getY(), (float)var2.getZ(), 0.0F);
      var3.transform(var0);
      return getNearest(var3.x(), var3.y(), var3.z());
   }

   public static Collection<Direction> allShuffled(RandomSource var0) {
      return Util.shuffledCopy((Object[])values(), var0);
   }

   public static Stream<Direction> stream() {
      return Stream.of(VALUES);
   }

   public Quaternion getRotation() {
      Quaternion var1 = Vector3f.XP.rotationDegrees(90.0F);
      Quaternion var10000;
      switch (this) {
         case DOWN:
            var10000 = Vector3f.XP.rotationDegrees(180.0F);
            break;
         case UP:
            var10000 = Quaternion.ONE.copy();
            break;
         case NORTH:
            var1.mul(Vector3f.ZP.rotationDegrees(180.0F));
            var10000 = var1;
            break;
         case SOUTH:
            var10000 = var1;
            break;
         case WEST:
            var1.mul(Vector3f.ZP.rotationDegrees(90.0F));
            var10000 = var1;
            break;
         case EAST:
            var1.mul(Vector3f.ZP.rotationDegrees(-90.0F));
            var10000 = var1;
            break;
         default:
            throw new IncompatibleClassChangeError();
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

   public static Direction getFacingAxis(Entity var0, Axis var1) {
      Direction var10000;
      switch (var1) {
         case X:
            var10000 = EAST.isFacingAngle(var0.getViewYRot(1.0F)) ? EAST : WEST;
            break;
         case Z:
            var10000 = SOUTH.isFacingAngle(var0.getViewYRot(1.0F)) ? SOUTH : NORTH;
            break;
         case Y:
            var10000 = var0.getViewXRot(1.0F) < 0.0F ? UP : DOWN;
            break;
         default:
            throw new IncompatibleClassChangeError();
      }

      return var10000;
   }

   public Direction getOpposite() {
      return from3DDataValue(this.oppositeIndex);
   }

   public Direction getClockWise(Axis var1) {
      Direction var10000;
      switch (var1) {
         case X:
            var10000 = this != WEST && this != EAST ? this.getClockWiseX() : this;
            break;
         case Z:
            var10000 = this != NORTH && this != SOUTH ? this.getClockWiseZ() : this;
            break;
         case Y:
            var10000 = this != UP && this != DOWN ? this.getClockWise() : this;
            break;
         default:
            throw new IncompatibleClassChangeError();
      }

      return var10000;
   }

   public Direction getCounterClockWise(Axis var1) {
      Direction var10000;
      switch (var1) {
         case X:
            var10000 = this != WEST && this != EAST ? this.getCounterClockWiseX() : this;
            break;
         case Z:
            var10000 = this != NORTH && this != SOUTH ? this.getCounterClockWiseZ() : this;
            break;
         case Y:
            var10000 = this != UP && this != DOWN ? this.getCounterClockWise() : this;
            break;
         default:
            throw new IncompatibleClassChangeError();
      }

      return var10000;
   }

   public Direction getClockWise() {
      Direction var10000;
      switch (this) {
         case NORTH:
            var10000 = EAST;
            break;
         case SOUTH:
            var10000 = WEST;
            break;
         case WEST:
            var10000 = NORTH;
            break;
         case EAST:
            var10000 = SOUTH;
            break;
         default:
            throw new IllegalStateException("Unable to get Y-rotated facing of " + this);
      }

      return var10000;
   }

   private Direction getClockWiseX() {
      Direction var10000;
      switch (this) {
         case DOWN:
            var10000 = SOUTH;
            break;
         case UP:
            var10000 = NORTH;
            break;
         case NORTH:
            var10000 = DOWN;
            break;
         case SOUTH:
            var10000 = UP;
            break;
         default:
            throw new IllegalStateException("Unable to get X-rotated facing of " + this);
      }

      return var10000;
   }

   private Direction getCounterClockWiseX() {
      Direction var10000;
      switch (this) {
         case DOWN:
            var10000 = NORTH;
            break;
         case UP:
            var10000 = SOUTH;
            break;
         case NORTH:
            var10000 = UP;
            break;
         case SOUTH:
            var10000 = DOWN;
            break;
         default:
            throw new IllegalStateException("Unable to get X-rotated facing of " + this);
      }

      return var10000;
   }

   private Direction getClockWiseZ() {
      Direction var10000;
      switch (this) {
         case DOWN:
            var10000 = WEST;
            break;
         case UP:
            var10000 = EAST;
            break;
         case NORTH:
         case SOUTH:
         default:
            throw new IllegalStateException("Unable to get Z-rotated facing of " + this);
         case WEST:
            var10000 = UP;
            break;
         case EAST:
            var10000 = DOWN;
      }

      return var10000;
   }

   private Direction getCounterClockWiseZ() {
      Direction var10000;
      switch (this) {
         case DOWN:
            var10000 = EAST;
            break;
         case UP:
            var10000 = WEST;
            break;
         case NORTH:
         case SOUTH:
         default:
            throw new IllegalStateException("Unable to get Z-rotated facing of " + this);
         case WEST:
            var10000 = DOWN;
            break;
         case EAST:
            var10000 = UP;
      }

      return var10000;
   }

   public Direction getCounterClockWise() {
      Direction var10000;
      switch (this) {
         case NORTH:
            var10000 = WEST;
            break;
         case SOUTH:
            var10000 = EAST;
            break;
         case WEST:
            var10000 = SOUTH;
            break;
         case EAST:
            var10000 = NORTH;
            break;
         default:
            throw new IllegalStateException("Unable to get CCW facing of " + this);
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
      return new Vector3f((float)this.getStepX(), (float)this.getStepY(), (float)this.getStepZ());
   }

   public String getName() {
      return this.name;
   }

   public Axis getAxis() {
      return this.axis;
   }

   @Nullable
   public static Direction byName(@Nullable String var0) {
      return (Direction)CODEC.byName(var0);
   }

   public static Direction from3DDataValue(int var0) {
      return BY_3D_DATA[Mth.abs(var0 % BY_3D_DATA.length)];
   }

   public static Direction from2DDataValue(int var0) {
      return BY_2D_DATA[Mth.abs(var0 % BY_2D_DATA.length)];
   }

   @Nullable
   public static Direction fromNormal(BlockPos var0) {
      return (Direction)BY_NORMAL.get(var0.asLong());
   }

   @Nullable
   public static Direction fromNormal(int var0, int var1, int var2) {
      return (Direction)BY_NORMAL.get(BlockPos.asLong(var0, var1, var2));
   }

   public static Direction fromYRot(double var0) {
      return from2DDataValue(Mth.floor(var0 / 90.0 + 0.5) & 3);
   }

   public static Direction fromAxisAndDirection(Axis var0, AxisDirection var1) {
      Direction var10000;
      switch (var0) {
         case X:
            var10000 = var1 == Direction.AxisDirection.POSITIVE ? EAST : WEST;
            break;
         case Z:
            var10000 = var1 == Direction.AxisDirection.POSITIVE ? SOUTH : NORTH;
            break;
         case Y:
            var10000 = var1 == Direction.AxisDirection.POSITIVE ? UP : DOWN;
            break;
         default:
            throw new IncompatibleClassChangeError();
      }

      return var10000;
   }

   public float toYRot() {
      return (float)((this.data2d & 3) * 90);
   }

   public static Direction getRandom(RandomSource var0) {
      return (Direction)Util.getRandom((Object[])VALUES, var0);
   }

   public static Direction getNearest(double var0, double var2, double var4) {
      return getNearest((float)var0, (float)var2, (float)var4);
   }

   public static Direction getNearest(float var0, float var1, float var2) {
      Direction var3 = NORTH;
      float var4 = 1.4E-45F;
      Direction[] var5 = VALUES;
      int var6 = var5.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         Direction var8 = var5[var7];
         float var9 = var0 * (float)var8.normal.getX() + var1 * (float)var8.normal.getY() + var2 * (float)var8.normal.getZ();
         if (var9 > var4) {
            var4 = var9;
            var3 = var8;
         }
      }

      return var3;
   }

   public String toString() {
      return this.name;
   }

   public String getSerializedName() {
      return this.name;
   }

   private static DataResult<Direction> verifyVertical(Direction var0) {
      return var0.getAxis().isVertical() ? DataResult.success(var0) : DataResult.error("Expected a vertical direction");
   }

   public static Direction get(AxisDirection var0, Axis var1) {
      Direction[] var2 = VALUES;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Direction var5 = var2[var4];
         if (var5.getAxisDirection() == var0 && var5.getAxis() == var1) {
            return var5;
         }
      }

      throw new IllegalArgumentException("No such direction: " + var0 + " " + var1);
   }

   public Vec3i getNormal() {
      return this.normal;
   }

   public boolean isFacingAngle(float var1) {
      float var2 = var1 * 0.017453292F;
      float var3 = -Mth.sin(var2);
      float var4 = Mth.cos(var2);
      return (float)this.normal.getX() * var3 + (float)this.normal.getZ() * var4 > 0.0F;
   }

   // $FF: synthetic method
   private static Direction[] $values() {
      return new Direction[]{DOWN, UP, NORTH, SOUTH, WEST, EAST};
   }

   public static enum Axis implements StringRepresentable, Predicate<Direction> {
      X("x") {
         public int choose(int var1, int var2, int var3) {
            return var1;
         }

         public double choose(double var1, double var3, double var5) {
            return var1;
         }

         // $FF: synthetic method
         public boolean test(@Nullable Object var1) {
            return super.test((Direction)var1);
         }
      },
      Y("y") {
         public int choose(int var1, int var2, int var3) {
            return var2;
         }

         public double choose(double var1, double var3, double var5) {
            return var3;
         }

         // $FF: synthetic method
         public boolean test(@Nullable Object var1) {
            return super.test((Direction)var1);
         }
      },
      Z("z") {
         public int choose(int var1, int var2, int var3) {
            return var3;
         }

         public double choose(double var1, double var3, double var5) {
            return var5;
         }

         // $FF: synthetic method
         public boolean test(@Nullable Object var1) {
            return super.test((Direction)var1);
         }
      };

      public static final Axis[] VALUES = values();
      public static final StringRepresentable.EnumCodec<Axis> CODEC = StringRepresentable.fromEnum(Axis::values);
      private final String name;

      Axis(String var3) {
         this.name = var3;
      }

      @Nullable
      public static Axis byName(String var0) {
         return (Axis)CODEC.byName(var0);
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

      public String toString() {
         return this.name;
      }

      public static Axis getRandom(RandomSource var0) {
         return (Axis)Util.getRandom((Object[])VALUES, var0);
      }

      public boolean test(@Nullable Direction var1) {
         return var1 != null && var1.getAxis() == this;
      }

      public Plane getPlane() {
         Plane var10000;
         switch (this) {
            case X:
            case Z:
               var10000 = Direction.Plane.HORIZONTAL;
               break;
            case Y:
               var10000 = Direction.Plane.VERTICAL;
               break;
            default:
               throw new IncompatibleClassChangeError();
         }

         return var10000;
      }

      public String getSerializedName() {
         return this.name;
      }

      public abstract int choose(int var1, int var2, int var3);

      public abstract double choose(double var1, double var3, double var5);

      // $FF: synthetic method
      public boolean test(@Nullable Object var1) {
         return this.test((Direction)var1);
      }

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

      private AxisDirection(int var3, String var4) {
         this.step = var3;
         this.name = var4;
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

   public static enum Plane implements Iterable<Direction>, Predicate<Direction> {
      HORIZONTAL(new Direction[]{Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST}, new Axis[]{Direction.Axis.X, Direction.Axis.Z}),
      VERTICAL(new Direction[]{Direction.UP, Direction.DOWN}, new Axis[]{Direction.Axis.Y});

      private final Direction[] faces;
      private final Axis[] axis;

      private Plane(Direction[] var3, Axis[] var4) {
         this.faces = var3;
         this.axis = var4;
      }

      public Direction getRandomDirection(RandomSource var1) {
         return (Direction)Util.getRandom((Object[])this.faces, var1);
      }

      public Axis getRandomAxis(RandomSource var1) {
         return (Axis)Util.getRandom((Object[])this.axis, var1);
      }

      public boolean test(@Nullable Direction var1) {
         return var1 != null && var1.getAxis().getPlane() == this;
      }

      public Iterator<Direction> iterator() {
         return Iterators.forArray(this.faces);
      }

      public Stream<Direction> stream() {
         return Arrays.stream(this.faces);
      }

      public List<Direction> shuffledCopy(RandomSource var1) {
         return Util.shuffledCopy((Object[])this.faces, var1);
      }

      // $FF: synthetic method
      public boolean test(@Nullable Object var1) {
         return this.test((Direction)var1);
      }

      // $FF: synthetic method
      private static Plane[] $values() {
         return new Plane[]{HORIZONTAL, VERTICAL};
      }
   }
}
