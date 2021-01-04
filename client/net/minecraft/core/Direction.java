package net.minecraft.core;

import com.google.common.collect.Iterators;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;

public enum Direction implements StringRepresentable {
   DOWN(0, 1, -1, "down", Direction.AxisDirection.NEGATIVE, Direction.Axis.Y, new Vec3i(0, -1, 0)),
   UP(1, 0, -1, "up", Direction.AxisDirection.POSITIVE, Direction.Axis.Y, new Vec3i(0, 1, 0)),
   NORTH(2, 3, 2, "north", Direction.AxisDirection.NEGATIVE, Direction.Axis.Z, new Vec3i(0, 0, -1)),
   SOUTH(3, 2, 0, "south", Direction.AxisDirection.POSITIVE, Direction.Axis.Z, new Vec3i(0, 0, 1)),
   WEST(4, 5, 1, "west", Direction.AxisDirection.NEGATIVE, Direction.Axis.X, new Vec3i(-1, 0, 0)),
   EAST(5, 4, 3, "east", Direction.AxisDirection.POSITIVE, Direction.Axis.X, new Vec3i(1, 0, 0));

   private final int data3d;
   private final int oppositeIndex;
   private final int data2d;
   private final String name;
   private final Direction.Axis axis;
   private final Direction.AxisDirection axisDirection;
   private final Vec3i normal;
   private static final Direction[] VALUES = values();
   private static final Map<String, Direction> BY_NAME = (Map)Arrays.stream(VALUES).collect(Collectors.toMap(Direction::getName, (var0) -> {
      return var0;
   }));
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

   private Direction(int var3, int var4, int var5, String var6, Direction.AxisDirection var7, Direction.Axis var8, Vec3i var9) {
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

   public int get3DDataValue() {
      return this.data3d;
   }

   public int get2DDataValue() {
      return this.data2d;
   }

   public Direction.AxisDirection getAxisDirection() {
      return this.axisDirection;
   }

   public Direction getOpposite() {
      return from3DDataValue(this.oppositeIndex);
   }

   public Direction getClockWise(Direction.Axis var1) {
      switch(var1) {
      case X:
         if (this != WEST && this != EAST) {
            return this.getClockWiseX();
         }

         return this;
      case Y:
         if (this != UP && this != DOWN) {
            return this.getClockWise();
         }

         return this;
      case Z:
         if (this != NORTH && this != SOUTH) {
            return this.getClockWiseZ();
         }

         return this;
      default:
         throw new IllegalStateException("Unable to get CW facing for axis " + var1);
      }
   }

   public Direction getClockWise() {
      switch(this) {
      case NORTH:
         return EAST;
      case EAST:
         return SOUTH;
      case SOUTH:
         return WEST;
      case WEST:
         return NORTH;
      default:
         throw new IllegalStateException("Unable to get Y-rotated facing of " + this);
      }
   }

   private Direction getClockWiseX() {
      switch(this) {
      case NORTH:
         return DOWN;
      case EAST:
      case WEST:
      default:
         throw new IllegalStateException("Unable to get X-rotated facing of " + this);
      case SOUTH:
         return UP;
      case UP:
         return NORTH;
      case DOWN:
         return SOUTH;
      }
   }

   private Direction getClockWiseZ() {
      switch(this) {
      case EAST:
         return DOWN;
      case SOUTH:
      default:
         throw new IllegalStateException("Unable to get Z-rotated facing of " + this);
      case WEST:
         return UP;
      case UP:
         return EAST;
      case DOWN:
         return WEST;
      }
   }

   public Direction getCounterClockWise() {
      switch(this) {
      case NORTH:
         return WEST;
      case EAST:
         return NORTH;
      case SOUTH:
         return EAST;
      case WEST:
         return SOUTH;
      default:
         throw new IllegalStateException("Unable to get CCW facing of " + this);
      }
   }

   public int getStepX() {
      return this.axis == Direction.Axis.X ? this.axisDirection.getStep() : 0;
   }

   public int getStepY() {
      return this.axis == Direction.Axis.Y ? this.axisDirection.getStep() : 0;
   }

   public int getStepZ() {
      return this.axis == Direction.Axis.Z ? this.axisDirection.getStep() : 0;
   }

   public String getName() {
      return this.name;
   }

   public Direction.Axis getAxis() {
      return this.axis;
   }

   @Nullable
   public static Direction byName(@Nullable String var0) {
      return var0 == null ? null : (Direction)BY_NAME.get(var0.toLowerCase(Locale.ROOT));
   }

   public static Direction from3DDataValue(int var0) {
      return BY_3D_DATA[Mth.abs(var0 % BY_3D_DATA.length)];
   }

   public static Direction from2DDataValue(int var0) {
      return BY_2D_DATA[Mth.abs(var0 % BY_2D_DATA.length)];
   }

   @Nullable
   public static Direction fromNormal(int var0, int var1, int var2) {
      return (Direction)BY_NORMAL.get(BlockPos.asLong(var0, var1, var2));
   }

   public static Direction fromYRot(double var0) {
      return from2DDataValue(Mth.floor(var0 / 90.0D + 0.5D) & 3);
   }

   public static Direction fromAxisAndDirection(Direction.Axis var0, Direction.AxisDirection var1) {
      switch(var0) {
      case X:
         return var1 == Direction.AxisDirection.POSITIVE ? EAST : WEST;
      case Y:
         return var1 == Direction.AxisDirection.POSITIVE ? UP : DOWN;
      case Z:
      default:
         return var1 == Direction.AxisDirection.POSITIVE ? SOUTH : NORTH;
      }
   }

   public float toYRot() {
      return (float)((this.data2d & 3) * 90);
   }

   public static Direction getRandomFace(Random var0) {
      return values()[var0.nextInt(values().length)];
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

   public static Direction get(Direction.AxisDirection var0, Direction.Axis var1) {
      Direction[] var2 = values();
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

   public static enum Plane implements Iterable<Direction>, Predicate<Direction> {
      HORIZONTAL(new Direction[]{Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST}, new Direction.Axis[]{Direction.Axis.X, Direction.Axis.Z}),
      VERTICAL(new Direction[]{Direction.UP, Direction.DOWN}, new Direction.Axis[]{Direction.Axis.Y});

      private final Direction[] faces;
      private final Direction.Axis[] axis;

      private Plane(Direction[] var3, Direction.Axis[] var4) {
         this.faces = var3;
         this.axis = var4;
      }

      public Direction getRandomDirection(Random var1) {
         return this.faces[var1.nextInt(this.faces.length)];
      }

      public boolean test(@Nullable Direction var1) {
         return var1 != null && var1.getAxis().getPlane() == this;
      }

      public Iterator<Direction> iterator() {
         return Iterators.forArray(this.faces);
      }

      // $FF: synthetic method
      public boolean test(@Nullable Object var1) {
         return this.test((Direction)var1);
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

      public String toString() {
         return this.name;
      }
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

      private static final Map<String, Direction.Axis> BY_NAME = (Map)Arrays.stream(values()).collect(Collectors.toMap(Direction.Axis::getName, (var0) -> {
         return var0;
      }));
      private final String name;

      private Axis(String var3) {
         this.name = var3;
      }

      @Nullable
      public static Direction.Axis byName(String var0) {
         return (Direction.Axis)BY_NAME.get(var0.toLowerCase(Locale.ROOT));
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

      public static Direction.Axis getRandomAxis(Random var0) {
         return values()[var0.nextInt(values().length)];
      }

      public boolean test(@Nullable Direction var1) {
         return var1 != null && var1.getAxis() == this;
      }

      public Direction.Plane getPlane() {
         switch(this) {
         case X:
         case Z:
            return Direction.Plane.HORIZONTAL;
         case Y:
            return Direction.Plane.VERTICAL;
         default:
            throw new Error("Someone's been tampering with the universe!");
         }
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
      Axis(String var3, Object var4) {
         this(var3);
      }
   }
}
