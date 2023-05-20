package net.minecraft.core;

import com.google.common.collect.Iterators;
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
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

public enum Direction implements StringRepresentable {
   DOWN(0, 1, -1, "down", Direction.AxisDirection.NEGATIVE, Direction.Axis.Y, new Vec3i(0, -1, 0)),
   UP(1, 0, -1, "up", Direction.AxisDirection.POSITIVE, Direction.Axis.Y, new Vec3i(0, 1, 0)),
   NORTH(2, 3, 2, "north", Direction.AxisDirection.NEGATIVE, Direction.Axis.Z, new Vec3i(0, 0, -1)),
   SOUTH(3, 2, 0, "south", Direction.AxisDirection.POSITIVE, Direction.Axis.Z, new Vec3i(0, 0, 1)),
   WEST(4, 5, 1, "west", Direction.AxisDirection.NEGATIVE, Direction.Axis.X, new Vec3i(-1, 0, 0)),
   EAST(5, 4, 3, "east", Direction.AxisDirection.POSITIVE, Direction.Axis.X, new Vec3i(1, 0, 0));

   public static final StringRepresentable.EnumCodec<Direction> CODEC = StringRepresentable.fromEnum(Direction::values);
   public static final Codec<Direction> VERTICAL_CODEC = ExtraCodecs.validate(CODEC, Direction::verifyVertical);
   private final int data3d;
   private final int oppositeIndex;
   private final int data2d;
   private final String name;
   private final Direction.Axis axis;
   private final Direction.AxisDirection axisDirection;
   private final Vec3i normal;
   private static final Direction[] VALUES = values();
   private static final Direction[] BY_3D_DATA = Arrays.stream(VALUES)
      .sorted(Comparator.comparingInt(var0 -> var0.data3d))
      .toArray(var0 -> new Direction[var0]);
   private static final Direction[] BY_2D_DATA = Arrays.stream(VALUES)
      .filter(var0 -> var0.getAxis().isHorizontal())
      .sorted(Comparator.comparingInt(var0 -> var0.data2d))
      .toArray(var0 -> new Direction[var0]);
   private static final Long2ObjectMap<Direction> BY_NORMAL = Arrays.stream(VALUES)
      .collect(Collectors.toMap(var0 -> new BlockPos(var0.getNormal()).asLong(), var0 -> var0, (var0, var1) -> {
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

   public static Direction rotate(Matrix4f var0, Direction var1) {
      Vec3i var2 = var1.getNormal();
      Vector4f var3 = var0.transform(new Vector4f((float)var2.getX(), (float)var2.getY(), (float)var2.getZ(), 0.0F));
      return getNearest(var3.x(), var3.y(), var3.z());
   }

   public static Collection<Direction> allShuffled(RandomSource var0) {
      return Util.shuffledCopy(values(), var0);
   }

   public static Stream<Direction> stream() {
      return Stream.of(VALUES);
   }

   public Quaternionf getRotation() {
      return switch(this) {
         case DOWN -> new Quaternionf().rotationX(3.1415927F);
         case UP -> new Quaternionf();
         case NORTH -> new Quaternionf().rotationXYZ(1.5707964F, 0.0F, 3.1415927F);
         case SOUTH -> new Quaternionf().rotationX(1.5707964F);
         case WEST -> new Quaternionf().rotationXYZ(1.5707964F, 0.0F, 1.5707964F);
         case EAST -> new Quaternionf().rotationXYZ(1.5707964F, 0.0F, -1.5707964F);
      };
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

   public static Direction getFacingAxis(Entity var0, Direction.Axis var1) {
      return switch(var1) {
         case X -> EAST.isFacingAngle(var0.getViewYRot(1.0F)) ? EAST : WEST;
         case Z -> SOUTH.isFacingAngle(var0.getViewYRot(1.0F)) ? SOUTH : NORTH;
         case Y -> var0.getViewXRot(1.0F) < 0.0F ? UP : DOWN;
      };
   }

   public Direction getOpposite() {
      return from3DDataValue(this.oppositeIndex);
   }

   public Direction getClockWise(Direction.Axis var1) {
      return switch(var1) {
         case X -> this != WEST && this != EAST ? this.getClockWiseX() : this;
         case Z -> this != NORTH && this != SOUTH ? this.getClockWiseZ() : this;
         case Y -> this != UP && this != DOWN ? this.getClockWise() : this;
      };
   }

   public Direction getCounterClockWise(Direction.Axis var1) {
      return switch(var1) {
         case X -> this != WEST && this != EAST ? this.getCounterClockWiseX() : this;
         case Z -> this != NORTH && this != SOUTH ? this.getCounterClockWiseZ() : this;
         case Y -> this != UP && this != DOWN ? this.getCounterClockWise() : this;
      };
   }

   public Direction getClockWise() {
      return switch(this) {
         case NORTH -> EAST;
         case SOUTH -> WEST;
         case WEST -> NORTH;
         case EAST -> SOUTH;
         default -> throw new IllegalStateException("Unable to get Y-rotated facing of " + this);
      };
   }

   private Direction getClockWiseX() {
      return switch(this) {
         case DOWN -> SOUTH;
         case UP -> NORTH;
         case NORTH -> DOWN;
         case SOUTH -> UP;
         default -> throw new IllegalStateException("Unable to get X-rotated facing of " + this);
      };
   }

   private Direction getCounterClockWiseX() {
      return switch(this) {
         case DOWN -> NORTH;
         case UP -> SOUTH;
         case NORTH -> UP;
         case SOUTH -> DOWN;
         default -> throw new IllegalStateException("Unable to get X-rotated facing of " + this);
      };
   }

   private Direction getClockWiseZ() {
      return switch(this) {
         case DOWN -> WEST;
         case UP -> EAST;
         default -> throw new IllegalStateException("Unable to get Z-rotated facing of " + this);
         case WEST -> UP;
         case EAST -> DOWN;
      };
   }

   private Direction getCounterClockWiseZ() {
      return switch(this) {
         case DOWN -> EAST;
         case UP -> WEST;
         default -> throw new IllegalStateException("Unable to get Z-rotated facing of " + this);
         case WEST -> DOWN;
         case EAST -> UP;
      };
   }

   public Direction getCounterClockWise() {
      return switch(this) {
         case NORTH -> WEST;
         case SOUTH -> EAST;
         case WEST -> SOUTH;
         case EAST -> NORTH;
         default -> throw new IllegalStateException("Unable to get CCW facing of " + this);
      };
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

   public Direction.Axis getAxis() {
      return this.axis;
   }

   @Nullable
   public static Direction byName(@Nullable String var0) {
      return CODEC.byName(var0);
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

   public static Direction fromAxisAndDirection(Direction.Axis var0, Direction.AxisDirection var1) {
      return switch(var0) {
         case X -> var1 == Direction.AxisDirection.POSITIVE ? EAST : WEST;
         case Z -> var1 == Direction.AxisDirection.POSITIVE ? SOUTH : NORTH;
         case Y -> var1 == Direction.AxisDirection.POSITIVE ? UP : DOWN;
      };
   }

   public float toYRot() {
      return (float)((this.data2d & 3) * 90);
   }

   public static Direction getRandom(RandomSource var0) {
      return Util.getRandom(VALUES, var0);
   }

   public static Direction getNearest(double var0, double var2, double var4) {
      return getNearest((float)var0, (float)var2, (float)var4);
   }

   public static Direction getNearest(float var0, float var1, float var2) {
      Direction var3 = NORTH;
      float var4 = 1.4E-45F;

      for(Direction var8 : VALUES) {
         float var9 = var0 * (float)var8.normal.getX() + var1 * (float)var8.normal.getY() + var2 * (float)var8.normal.getZ();
         if (var9 > var4) {
            var4 = var9;
            var3 = var8;
         }
      }

      return var3;
   }

   @Override
   public String toString() {
      return this.name;
   }

   @Override
   public String getSerializedName() {
      return this.name;
   }

   private static DataResult<Direction> verifyVertical(Direction var0) {
      return var0.getAxis().isVertical() ? DataResult.success(var0) : DataResult.error(() -> "Expected a vertical direction");
   }

   public static Direction get(Direction.AxisDirection var0, Direction.Axis var1) {
      for(Direction var5 : VALUES) {
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

   public static enum Axis implements StringRepresentable, Predicate<Direction> {
      X("x") {
         @Override
         public int choose(int var1, int var2, int var3) {
            return var1;
         }

         @Override
         public double choose(double var1, double var3, double var5) {
            return var1;
         }
      },
      Y("y") {
         @Override
         public int choose(int var1, int var2, int var3) {
            return var2;
         }

         @Override
         public double choose(double var1, double var3, double var5) {
            return var3;
         }
      },
      Z("z") {
         @Override
         public int choose(int var1, int var2, int var3) {
            return var3;
         }

         @Override
         public double choose(double var1, double var3, double var5) {
            return var5;
         }
      };

      public static final Direction.Axis[] VALUES = values();
      public static final StringRepresentable.EnumCodec<Direction.Axis> CODEC = StringRepresentable.fromEnum(Direction.Axis::values);
      private final String name;

      Axis(String var3) {
         this.name = var3;
      }

      @Nullable
      public static Direction.Axis byName(String var0) {
         return CODEC.byName(var0);
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

      @Override
      public String toString() {
         return this.name;
      }

      public static Direction.Axis getRandom(RandomSource var0) {
         return Util.getRandom(VALUES, var0);
      }

      public boolean test(@Nullable Direction var1) {
         return var1 != null && var1.getAxis() == this;
      }

      public Direction.Plane getPlane() {
         return switch(this) {
            case X, Z -> Direction.Plane.HORIZONTAL;
            case Y -> Direction.Plane.VERTICAL;
         };
      }

      @Override
      public String getSerializedName() {
         return this.name;
      }

      public abstract int choose(int var1, int var2, int var3);

      public abstract double choose(double var1, double var3, double var5);
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

      @Override
      public String toString() {
         return this.name;
      }

      public Direction.AxisDirection opposite() {
         return this == POSITIVE ? NEGATIVE : POSITIVE;
      }
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

      public Direction getRandomDirection(RandomSource var1) {
         return Util.getRandom(this.faces, var1);
      }

      public Direction.Axis getRandomAxis(RandomSource var1) {
         return Util.getRandom(this.axis, var1);
      }

      public boolean test(@Nullable Direction var1) {
         return var1 != null && var1.getAxis().getPlane() == this;
      }

      @Override
      public Iterator<Direction> iterator() {
         return Iterators.forArray(this.faces);
      }

      public Stream<Direction> stream() {
         return Arrays.stream(this.faces);
      }

      public List<Direction> shuffledCopy(RandomSource var1) {
         return Util.shuffledCopy(this.faces, var1);
      }
   }
}
