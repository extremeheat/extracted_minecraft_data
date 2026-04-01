package net.minecraft.world.level.redstone;

import com.google.common.annotations.VisibleForTesting;
import io.netty.buffer.ByteBuf;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Util;

public class Orientation {
   public static final StreamCodec<ByteBuf, Orientation> STREAM_CODEC = ByteBufCodecs.idMapper(Orientation::fromIndex, Orientation::getIndex);
   private static final Orientation[] ORIENTATIONS = (Orientation[])Util.make(() -> {
      Orientation[] orientations = new Orientation[48];
      generateContext(new Orientation(Direction.UP, Direction.NORTH, Orientation.SideBias.LEFT), orientations);
      return orientations;
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

   private Orientation(final Direction up, final Direction front, final SideBias sideBias) {
      super();
      this.up = up;
      this.front = front;
      this.sideBias = sideBias;
      this.index = generateIndex(up, front, sideBias);
      Vec3i rightVector = front.getUnitVec3i().cross(up.getUnitVec3i());
      Direction side = Direction.getNearest((Vec3i)rightVector, (Direction)null);
      Objects.requireNonNull(side);
      if (this.sideBias == Orientation.SideBias.RIGHT) {
         this.side = side;
      } else {
         this.side = side.getOpposite();
      }

      this.neighbors = List.of(this.front.getOpposite(), this.front, this.side, this.side.getOpposite(), this.up.getOpposite(), this.up);
      this.horizontalNeighbors = this.neighbors.stream().filter((d) -> d.getAxis() != this.up.getAxis()).toList();
      this.verticalNeighbors = this.neighbors.stream().filter((d) -> d.getAxis() == this.up.getAxis()).toList();
   }

   public static Orientation of(final Direction up, final Direction front, final SideBias sideBias) {
      return ORIENTATIONS[generateIndex(up, front, sideBias)];
   }

   public Orientation withUp(final Direction up) {
      return (Orientation)this.withUp.get(up);
   }

   public Orientation withFront(final Direction front) {
      return (Orientation)this.withFront.get(front);
   }

   public Orientation withFrontPreserveUp(final Direction front) {
      return front.getAxis() == this.up.getAxis() ? this : (Orientation)this.withFront.get(front);
   }

   public Orientation withFrontAdjustSideBias(final Direction front) {
      Orientation withFront = this.withFront(front);
      return this.front == withFront.side ? withFront.withMirror() : withFront;
   }

   public Orientation withSideBias(final SideBias sideBias) {
      return (Orientation)this.withSideBias.get(sideBias);
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

   public static Orientation fromIndex(final int index) {
      return ORIENTATIONS[index];
   }

   public static Orientation random(final RandomSource rand) {
      return (Orientation)Util.getRandom(ORIENTATIONS, rand);
   }

   private static Orientation generateContext(final Orientation self, final Orientation[] lookup) {
      if (lookup[self.getIndex()] != null) {
         return lookup[self.getIndex()];
      } else {
         lookup[self.getIndex()] = self;

         for(SideBias sideBias : Orientation.SideBias.values()) {
            self.withSideBias.put(sideBias, generateContext(new Orientation(self.up, self.front, sideBias), lookup));
         }

         for(Direction facing : Direction.values()) {
            Direction up = self.up;
            if (facing == self.up) {
               up = self.front.getOpposite();
            }

            if (facing == self.up.getOpposite()) {
               up = self.front;
            }

            self.withFront.put(facing, generateContext(new Orientation(up, facing, self.sideBias), lookup));
         }

         for(Direction facing : Direction.values()) {
            Direction front = self.front;
            if (facing == self.front) {
               front = self.up.getOpposite();
            }

            if (facing == self.front.getOpposite()) {
               front = self.up;
            }

            self.withUp.put(facing, generateContext(new Orientation(facing, front, self.sideBias), lookup));
         }

         return self;
      }
   }

   @VisibleForTesting
   protected static int generateIndex(final Direction up, final Direction front, final SideBias sideBias) {
      if (up.getAxis() == front.getAxis()) {
         throw new IllegalStateException("Up-vector and front-vector can not be on the same axis");
      } else {
         int frontAxisKey;
         if (up.getAxis() == Direction.Axis.Y) {
            frontAxisKey = front.getAxis() == Direction.Axis.X ? 1 : 0;
         } else {
            frontAxisKey = front.getAxis() == Direction.Axis.Y ? 1 : 0;
         }

         int frontKey = frontAxisKey << 1 | front.getAxisDirection().ordinal();
         return ((up.ordinal() << 2) + frontKey << 1) + sideBias.ordinal();
      }
   }

   public static enum SideBias {
      LEFT("left"),
      RIGHT("right");

      private final String name;

      private SideBias(final String name) {
         this.name = name;
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
