package net.minecraft.world.level.redstone;

import com.google.common.annotations.VisibleForTesting;
import io.netty.buffer.ByteBuf;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.minecraft.Util;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;

public class Orientation {
   public static final StreamCodec<ByteBuf, Orientation> STREAM_CODEC = ByteBufCodecs.idMapper(Orientation::fromIndex, Orientation::getIndex);
   private static final Orientation[] ORIENTATIONS = (Orientation[])Util.make(() -> {
      Orientation[] var0 = new Orientation[48];
      generateContext(new Orientation(Direction.UP, Direction.NORTH, Orientation.SideBias.LEFT), var0);
      return var0;
   });
   private final Direction up;
   private final Direction front;
   private final Direction side;
   private final SideBias sideBias;
   private final int index;
   private final List<Direction> neighbors;
   private final List<Direction> horizontalNeighbors;
   private final List<Direction> verticalNeighbors;
   private final Map<Direction, Orientation> withFront = new EnumMap(Direction.class);
   private final Map<Direction, Orientation> withUp = new EnumMap(Direction.class);
   private final Map<SideBias, Orientation> withSideBias = new EnumMap(SideBias.class);

   private Orientation(Direction var1, Direction var2, SideBias var3) {
      super();
      this.up = var1;
      this.front = var2;
      this.sideBias = var3;
      this.index = generateIndex(var1, var2, var3);
      Vec3i var4 = var2.getUnitVec3i().cross(var1.getUnitVec3i());
      Direction var5 = Direction.getNearest(var4, (Direction)null);
      Objects.requireNonNull(var5);
      if (this.sideBias == Orientation.SideBias.RIGHT) {
         this.side = var5;
      } else {
         this.side = var5.getOpposite();
      }

      this.neighbors = List.of(this.front.getOpposite(), this.front, this.side, this.side.getOpposite(), this.up.getOpposite(), this.up);
      this.horizontalNeighbors = this.neighbors.stream().filter((var1x) -> {
         return var1x.getAxis() != this.up.getAxis();
      }).toList();
      this.verticalNeighbors = this.neighbors.stream().filter((var1x) -> {
         return var1x.getAxis() == this.up.getAxis();
      }).toList();
   }

   public static Orientation of(Direction var0, Direction var1, SideBias var2) {
      return ORIENTATIONS[generateIndex(var0, var1, var2)];
   }

   public Orientation withUp(Direction var1) {
      return (Orientation)this.withUp.get(var1);
   }

   public Orientation withFront(Direction var1) {
      return (Orientation)this.withFront.get(var1);
   }

   public Orientation withFrontPreserveUp(Direction var1) {
      return var1.getAxis() == this.up.getAxis() ? this : (Orientation)this.withFront.get(var1);
   }

   public Orientation withFrontAdjustSideBias(Direction var1) {
      Orientation var2 = this.withFront(var1);
      return this.front == var2.side ? var2.withMirror() : var2;
   }

   public Orientation withSideBias(SideBias var1) {
      return (Orientation)this.withSideBias.get(var1);
   }

   public Orientation withMirror() {
      return this.withSideBias(this.sideBias.getOpposite());
   }

   public Direction getFront() {
      return this.front;
   }

   public Direction getUp() {
      return this.up;
   }

   public Direction getSide() {
      return this.side;
   }

   public SideBias getSideBias() {
      return this.sideBias;
   }

   public List<Direction> getDirections() {
      return this.neighbors;
   }

   public List<Direction> getHorizontalDirections() {
      return this.horizontalNeighbors;
   }

   public List<Direction> getVerticalDirections() {
      return this.verticalNeighbors;
   }

   public String toString() {
      String var10000 = String.valueOf(this.up);
      return "[up=" + var10000 + ",front=" + String.valueOf(this.front) + ",sideBias=" + String.valueOf(this.sideBias) + "]";
   }

   public int getIndex() {
      return this.index;
   }

   public static Orientation fromIndex(int var0) {
      return ORIENTATIONS[var0];
   }

   public static Orientation random(RandomSource var0) {
      return (Orientation)Util.getRandom((Object[])ORIENTATIONS, var0);
   }

   private static Orientation generateContext(Orientation var0, Orientation[] var1) {
      if (var1[var0.getIndex()] != null) {
         return var1[var0.getIndex()];
      } else {
         var1[var0.getIndex()] = var0;
         SideBias[] var2 = Orientation.SideBias.values();
         int var3 = var2.length;

         int var4;
         for(var4 = 0; var4 < var3; ++var4) {
            SideBias var5 = var2[var4];
            var0.withSideBias.put(var5, generateContext(new Orientation(var0.up, var0.front, var5), var1));
         }

         Direction[] var7 = Direction.values();
         var3 = var7.length;

         Direction var6;
         Direction var8;
         for(var4 = 0; var4 < var3; ++var4) {
            var8 = var7[var4];
            var6 = var0.up;
            if (var8 == var0.up) {
               var6 = var0.front.getOpposite();
            }

            if (var8 == var0.up.getOpposite()) {
               var6 = var0.front;
            }

            var0.withFront.put(var8, generateContext(new Orientation(var6, var8, var0.sideBias), var1));
         }

         var7 = Direction.values();
         var3 = var7.length;

         for(var4 = 0; var4 < var3; ++var4) {
            var8 = var7[var4];
            var6 = var0.front;
            if (var8 == var0.front) {
               var6 = var0.up.getOpposite();
            }

            if (var8 == var0.front.getOpposite()) {
               var6 = var0.up;
            }

            var0.withUp.put(var8, generateContext(new Orientation(var8, var6, var0.sideBias), var1));
         }

         return var0;
      }
   }

   @VisibleForTesting
   protected static int generateIndex(Direction var0, Direction var1, SideBias var2) {
      if (var0.getAxis() == var1.getAxis()) {
         throw new IllegalStateException("Up-vector and front-vector can not be on the same axis");
      } else {
         int var3;
         if (var0.getAxis() == Direction.Axis.Y) {
            var3 = var1.getAxis() == Direction.Axis.X ? 1 : 0;
         } else {
            var3 = var1.getAxis() == Direction.Axis.Y ? 1 : 0;
         }

         int var4 = var3 << 1 | var1.getAxisDirection().ordinal();
         return ((var0.ordinal() << 2) + var4 << 1) + var2.ordinal();
      }
   }

   public static enum SideBias {
      LEFT("left"),
      RIGHT("right");

      private final String name;

      private SideBias(final String var3) {
         this.name = var3;
      }

      public SideBias getOpposite() {
         return this == LEFT ? RIGHT : LEFT;
      }

      public String toString() {
         return this.name;
      }

      // $FF: synthetic method
      private static SideBias[] $values() {
         return new SideBias[]{LEFT, RIGHT};
      }
   }
}
