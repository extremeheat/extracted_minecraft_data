package net.minecraft.core;

import com.google.common.collect.AbstractIterator;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.concurrent.Immutable;
import net.minecraft.Util;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;

@Immutable
public class BlockPos extends Vec3i {
   public static final Codec<BlockPos> CODEC;
   public static final StreamCodec<ByteBuf, BlockPos> STREAM_CODEC;
   private static final Logger LOGGER;
   public static final BlockPos ZERO;
   public static final int PACKED_HORIZONTAL_LENGTH;
   public static final int PACKED_Y_LENGTH;
   private static final long PACKED_X_MASK;
   private static final long PACKED_Y_MASK;
   private static final long PACKED_Z_MASK;
   private static final int Y_OFFSET = 0;
   private static final int Z_OFFSET;
   private static final int X_OFFSET;
   public static final int MAX_HORIZONTAL_COORDINATE;

   public BlockPos(int var1, int var2, int var3) {
      super(var1, var2, var3);
   }

   public BlockPos(Vec3i var1) {
      this(var1.getX(), var1.getY(), var1.getZ());
   }

   public static long offset(long var0, Direction var2) {
      return offset(var0, var2.getStepX(), var2.getStepY(), var2.getStepZ());
   }

   public static long offset(long var0, int var2, int var3, int var4) {
      return asLong(getX(var0) + var2, getY(var0) + var3, getZ(var0) + var4);
   }

   public static int getX(long var0) {
      return (int)(var0 << 64 - X_OFFSET - PACKED_HORIZONTAL_LENGTH >> 64 - PACKED_HORIZONTAL_LENGTH);
   }

   public static int getY(long var0) {
      return (int)(var0 << 64 - PACKED_Y_LENGTH >> 64 - PACKED_Y_LENGTH);
   }

   public static int getZ(long var0) {
      return (int)(var0 << 64 - Z_OFFSET - PACKED_HORIZONTAL_LENGTH >> 64 - PACKED_HORIZONTAL_LENGTH);
   }

   public static BlockPos of(long var0) {
      return new BlockPos(getX(var0), getY(var0), getZ(var0));
   }

   public static BlockPos containing(double var0, double var2, double var4) {
      return new BlockPos(Mth.floor(var0), Mth.floor(var2), Mth.floor(var4));
   }

   public static BlockPos containing(Position var0) {
      return containing(var0.x(), var0.y(), var0.z());
   }

   public static BlockPos min(BlockPos var0, BlockPos var1) {
      return new BlockPos(Math.min(var0.getX(), var1.getX()), Math.min(var0.getY(), var1.getY()), Math.min(var0.getZ(), var1.getZ()));
   }

   public static BlockPos max(BlockPos var0, BlockPos var1) {
      return new BlockPos(Math.max(var0.getX(), var1.getX()), Math.max(var0.getY(), var1.getY()), Math.max(var0.getZ(), var1.getZ()));
   }

   public long asLong() {
      return asLong(this.getX(), this.getY(), this.getZ());
   }

   public static long asLong(int var0, int var1, int var2) {
      long var3 = 0L;
      var3 |= ((long)var0 & PACKED_X_MASK) << X_OFFSET;
      var3 |= ((long)var1 & PACKED_Y_MASK) << 0;
      var3 |= ((long)var2 & PACKED_Z_MASK) << Z_OFFSET;
      return var3;
   }

   public static long getFlatIndex(long var0) {
      return var0 & -16L;
   }

   public BlockPos offset(int var1, int var2, int var3) {
      return var1 == 0 && var2 == 0 && var3 == 0 ? this : new BlockPos(this.getX() + var1, this.getY() + var2, this.getZ() + var3);
   }

   public Vec3 getCenter() {
      return Vec3.atCenterOf(this);
   }

   public Vec3 getBottomCenter() {
      return Vec3.atBottomCenterOf(this);
   }

   public BlockPos offset(Vec3i var1) {
      return this.offset(var1.getX(), var1.getY(), var1.getZ());
   }

   public BlockPos subtract(Vec3i var1) {
      return this.offset(-var1.getX(), -var1.getY(), -var1.getZ());
   }

   public BlockPos multiply(int var1) {
      if (var1 == 1) {
         return this;
      } else {
         return var1 == 0 ? ZERO : new BlockPos(this.getX() * var1, this.getY() * var1, this.getZ() * var1);
      }
   }

   public BlockPos above() {
      return this.relative(Direction.UP);
   }

   public BlockPos above(int var1) {
      return this.relative(Direction.UP, var1);
   }

   public BlockPos below() {
      return this.relative(Direction.DOWN);
   }

   public BlockPos below(int var1) {
      return this.relative(Direction.DOWN, var1);
   }

   public BlockPos north() {
      return this.relative(Direction.NORTH);
   }

   public BlockPos north(int var1) {
      return this.relative(Direction.NORTH, var1);
   }

   public BlockPos south() {
      return this.relative(Direction.SOUTH);
   }

   public BlockPos south(int var1) {
      return this.relative(Direction.SOUTH, var1);
   }

   public BlockPos west() {
      return this.relative(Direction.WEST);
   }

   public BlockPos west(int var1) {
      return this.relative(Direction.WEST, var1);
   }

   public BlockPos east() {
      return this.relative(Direction.EAST);
   }

   public BlockPos east(int var1) {
      return this.relative(Direction.EAST, var1);
   }

   public BlockPos relative(Direction var1) {
      return new BlockPos(this.getX() + var1.getStepX(), this.getY() + var1.getStepY(), this.getZ() + var1.getStepZ());
   }

   public BlockPos relative(Direction var1, int var2) {
      return var2 == 0 ? this : new BlockPos(this.getX() + var1.getStepX() * var2, this.getY() + var1.getStepY() * var2, this.getZ() + var1.getStepZ() * var2);
   }

   public BlockPos relative(Direction.Axis var1, int var2) {
      if (var2 == 0) {
         return this;
      } else {
         int var3 = var1 == Direction.Axis.X ? var2 : 0;
         int var4 = var1 == Direction.Axis.Y ? var2 : 0;
         int var5 = var1 == Direction.Axis.Z ? var2 : 0;
         return new BlockPos(this.getX() + var3, this.getY() + var4, this.getZ() + var5);
      }
   }

   public BlockPos rotate(Rotation var1) {
      switch (var1) {
         case NONE:
         default:
            return this;
         case CLOCKWISE_90:
            return new BlockPos(-this.getZ(), this.getY(), this.getX());
         case CLOCKWISE_180:
            return new BlockPos(-this.getX(), this.getY(), -this.getZ());
         case COUNTERCLOCKWISE_90:
            return new BlockPos(this.getZ(), this.getY(), -this.getX());
      }
   }

   public BlockPos cross(Vec3i var1) {
      return new BlockPos(this.getY() * var1.getZ() - this.getZ() * var1.getY(), this.getZ() * var1.getX() - this.getX() * var1.getZ(), this.getX() * var1.getY() - this.getY() * var1.getX());
   }

   public BlockPos atY(int var1) {
      return new BlockPos(this.getX(), var1, this.getZ());
   }

   public BlockPos immutable() {
      return this;
   }

   public MutableBlockPos mutable() {
      return new MutableBlockPos(this.getX(), this.getY(), this.getZ());
   }

   public Vec3 clampLocationWithin(Vec3 var1) {
      return new Vec3(Mth.clamp(var1.x, (double)((float)this.getX() + 1.0E-5F), (double)this.getX() + 1.0 - 9.999999747378752E-6), Mth.clamp(var1.y, (double)((float)this.getY() + 1.0E-5F), (double)this.getY() + 1.0 - 9.999999747378752E-6), Mth.clamp(var1.z, (double)((float)this.getZ() + 1.0E-5F), (double)this.getZ() + 1.0 - 9.999999747378752E-6));
   }

   public static Iterable<BlockPos> randomInCube(RandomSource var0, int var1, BlockPos var2, int var3) {
      return randomBetweenClosed(var0, var1, var2.getX() - var3, var2.getY() - var3, var2.getZ() - var3, var2.getX() + var3, var2.getY() + var3, var2.getZ() + var3);
   }

   /** @deprecated */
   @Deprecated
   public static Stream<BlockPos> squareOutSouthEast(BlockPos var0) {
      return Stream.of(var0, var0.south(), var0.east(), var0.south().east());
   }

   public static Iterable<BlockPos> randomBetweenClosed(RandomSource var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      int var8 = var5 - var2 + 1;
      int var9 = var6 - var3 + 1;
      int var10 = var7 - var4 + 1;
      return () -> {
         return new AbstractIterator<BlockPos>() {
            final MutableBlockPos nextPos = new MutableBlockPos();
            int counter = var0;

            protected BlockPos computeNext() {
               if (this.counter <= 0) {
                  return (BlockPos)this.endOfData();
               } else {
                  MutableBlockPos var1x = this.nextPos.set(var1 + var2.nextInt(var3), var4 + var2.nextInt(var5), var6 + var2.nextInt(var7));
                  --this.counter;
                  return var1x;
               }
            }

            // $FF: synthetic method
            protected Object computeNext() {
               return this.computeNext();
            }
         };
      };
   }

   public static Iterable<BlockPos> withinManhattan(BlockPos var0, int var1, int var2, int var3) {
      int var4 = var1 + var2 + var3;
      int var5 = var0.getX();
      int var6 = var0.getY();
      int var7 = var0.getZ();
      return () -> {
         return new AbstractIterator<BlockPos>() {
            private final MutableBlockPos cursor = new MutableBlockPos();
            private int currentDepth;
            private int maxX;
            private int maxY;
            private int x;
            private int y;
            private boolean zMirror;

            protected BlockPos computeNext() {
               if (this.zMirror) {
                  this.zMirror = false;
                  this.cursor.setZ(var0 - (this.cursor.getZ() - var0));
                  return this.cursor;
               } else {
                  MutableBlockPos var1x;
                  for(var1x = null; var1x == null; ++this.y) {
                     if (this.y > this.maxY) {
                        ++this.x;
                        if (this.x > this.maxX) {
                           ++this.currentDepth;
                           if (this.currentDepth > var1) {
                              return (BlockPos)this.endOfData();
                           }

                           this.maxX = Math.min(var2, this.currentDepth);
                           this.x = -this.maxX;
                        }

                        this.maxY = Math.min(var3, this.currentDepth - Math.abs(this.x));
                        this.y = -this.maxY;
                     }

                     int var2x = this.x;
                     int var3x = this.y;
                     int var4x = this.currentDepth - Math.abs(var2x) - Math.abs(var3x);
                     if (var4x <= var4) {
                        this.zMirror = var4x != 0;
                        var1x = this.cursor.set(var5 + var2x, var6 + var3x, var0 + var4x);
                     }
                  }

                  return var1x;
               }
            }

            // $FF: synthetic method
            protected Object computeNext() {
               return this.computeNext();
            }
         };
      };
   }

   public static Optional<BlockPos> findClosestMatch(BlockPos var0, int var1, int var2, Predicate<BlockPos> var3) {
      Iterator var4 = withinManhattan(var0, var1, var2, var1).iterator();

      BlockPos var5;
      do {
         if (!var4.hasNext()) {
            return Optional.empty();
         }

         var5 = (BlockPos)var4.next();
      } while(!var3.test(var5));

      return Optional.of(var5);
   }

   public static Stream<BlockPos> withinManhattanStream(BlockPos var0, int var1, int var2, int var3) {
      return StreamSupport.stream(withinManhattan(var0, var1, var2, var3).spliterator(), false);
   }

   public static Iterable<BlockPos> betweenClosed(AABB var0) {
      BlockPos var1 = containing(var0.minX, var0.minY, var0.minZ);
      BlockPos var2 = containing(var0.maxX, var0.maxY, var0.maxZ);
      return betweenClosed(var1, var2);
   }

   public static Iterable<BlockPos> betweenClosed(BlockPos var0, BlockPos var1) {
      return betweenClosed(Math.min(var0.getX(), var1.getX()), Math.min(var0.getY(), var1.getY()), Math.min(var0.getZ(), var1.getZ()), Math.max(var0.getX(), var1.getX()), Math.max(var0.getY(), var1.getY()), Math.max(var0.getZ(), var1.getZ()));
   }

   public static Stream<BlockPos> betweenClosedStream(BlockPos var0, BlockPos var1) {
      return StreamSupport.stream(betweenClosed(var0, var1).spliterator(), false);
   }

   public static Stream<BlockPos> betweenClosedStream(BoundingBox var0) {
      return betweenClosedStream(Math.min(var0.minX(), var0.maxX()), Math.min(var0.minY(), var0.maxY()), Math.min(var0.minZ(), var0.maxZ()), Math.max(var0.minX(), var0.maxX()), Math.max(var0.minY(), var0.maxY()), Math.max(var0.minZ(), var0.maxZ()));
   }

   public static Stream<BlockPos> betweenClosedStream(AABB var0) {
      return betweenClosedStream(Mth.floor(var0.minX), Mth.floor(var0.minY), Mth.floor(var0.minZ), Mth.floor(var0.maxX), Mth.floor(var0.maxY), Mth.floor(var0.maxZ));
   }

   public static Stream<BlockPos> betweenClosedStream(int var0, int var1, int var2, int var3, int var4, int var5) {
      return StreamSupport.stream(betweenClosed(var0, var1, var2, var3, var4, var5).spliterator(), false);
   }

   public static Iterable<BlockPos> betweenClosed(int var0, int var1, int var2, int var3, int var4, int var5) {
      int var6 = var3 - var0 + 1;
      int var7 = var4 - var1 + 1;
      int var8 = var5 - var2 + 1;
      int var9 = var6 * var7 * var8;
      return () -> {
         return new AbstractIterator<BlockPos>() {
            private final MutableBlockPos cursor = new MutableBlockPos();
            private int index;

            protected BlockPos computeNext() {
               if (this.index == var0) {
                  return (BlockPos)this.endOfData();
               } else {
                  int var1x = this.index % var1;
                  int var2x = this.index / var1;
                  int var3x = var2x % var2;
                  int var4x = var2x / var2;
                  ++this.index;
                  return this.cursor.set(var3 + var1x, var4 + var3x, var5 + var4x);
               }
            }

            // $FF: synthetic method
            protected Object computeNext() {
               return this.computeNext();
            }
         };
      };
   }

   public static Iterable<MutableBlockPos> spiralAround(BlockPos var0, int var1, Direction var2, Direction var3) {
      Validate.validState(var2.getAxis() != var3.getAxis(), "The two directions cannot be on the same axis", new Object[0]);
      return () -> {
         return new AbstractIterator<MutableBlockPos>() {
            private final Direction[] directions = new Direction[]{var0, var1, var0.getOpposite(), var1.getOpposite()};
            private final MutableBlockPos cursor = var2.mutable().move(var1);
            private final int legs = 4 * var3;
            private int leg = -1;
            private int legSize;
            private int legIndex;
            private int lastX;
            private int lastY;
            private int lastZ;

            {
               this.lastX = this.cursor.getX();
               this.lastY = this.cursor.getY();
               this.lastZ = this.cursor.getZ();
            }

            protected MutableBlockPos computeNext() {
               this.cursor.set(this.lastX, this.lastY, this.lastZ).move(this.directions[(this.leg + 4) % 4]);
               this.lastX = this.cursor.getX();
               this.lastY = this.cursor.getY();
               this.lastZ = this.cursor.getZ();
               if (this.legIndex >= this.legSize) {
                  if (this.leg >= this.legs) {
                     return (MutableBlockPos)this.endOfData();
                  }

                  ++this.leg;
                  this.legIndex = 0;
                  this.legSize = this.leg / 2 + 1;
               }

               ++this.legIndex;
               return this.cursor;
            }

            // $FF: synthetic method
            protected Object computeNext() {
               return this.computeNext();
            }
         };
      };
   }

   public static int breadthFirstTraversal(BlockPos var0, int var1, int var2, BiConsumer<BlockPos, Consumer<BlockPos>> var3, Function<BlockPos, TraversalNodeStatus> var4) {
      ArrayDeque var5 = new ArrayDeque();
      LongOpenHashSet var6 = new LongOpenHashSet();
      var5.add(Pair.of(var0, 0));
      int var7 = 0;

      while(!var5.isEmpty()) {
         Pair var8 = (Pair)var5.poll();
         BlockPos var9 = (BlockPos)var8.getLeft();
         int var10 = (Integer)var8.getRight();
         long var11 = var9.asLong();
         if (var6.add(var11)) {
            TraversalNodeStatus var13 = (TraversalNodeStatus)var4.apply(var9);
            if (var13 != BlockPos.TraversalNodeStatus.SKIP) {
               if (var13 == BlockPos.TraversalNodeStatus.STOP) {
                  break;
               }

               ++var7;
               if (var7 >= var2) {
                  return var7;
               }

               if (var10 < var1) {
                  var3.accept(var9, (var2x) -> {
                     var5.add(Pair.of(var2x, var10 + 1));
                  });
               }
            }
         }
      }

      return var7;
   }

   // $FF: synthetic method
   public Vec3i cross(final Vec3i var1) {
      return this.cross(var1);
   }

   // $FF: synthetic method
   public Vec3i relative(final Direction.Axis var1, final int var2) {
      return this.relative(var1, var2);
   }

   // $FF: synthetic method
   public Vec3i relative(final Direction var1, final int var2) {
      return this.relative(var1, var2);
   }

   // $FF: synthetic method
   public Vec3i relative(final Direction var1) {
      return this.relative(var1);
   }

   // $FF: synthetic method
   public Vec3i east(final int var1) {
      return this.east(var1);
   }

   // $FF: synthetic method
   public Vec3i east() {
      return this.east();
   }

   // $FF: synthetic method
   public Vec3i west(final int var1) {
      return this.west(var1);
   }

   // $FF: synthetic method
   public Vec3i west() {
      return this.west();
   }

   // $FF: synthetic method
   public Vec3i south(final int var1) {
      return this.south(var1);
   }

   // $FF: synthetic method
   public Vec3i south() {
      return this.south();
   }

   // $FF: synthetic method
   public Vec3i north(final int var1) {
      return this.north(var1);
   }

   // $FF: synthetic method
   public Vec3i north() {
      return this.north();
   }

   // $FF: synthetic method
   public Vec3i below(final int var1) {
      return this.below(var1);
   }

   // $FF: synthetic method
   public Vec3i below() {
      return this.below();
   }

   // $FF: synthetic method
   public Vec3i above(final int var1) {
      return this.above(var1);
   }

   // $FF: synthetic method
   public Vec3i above() {
      return this.above();
   }

   // $FF: synthetic method
   public Vec3i multiply(final int var1) {
      return this.multiply(var1);
   }

   // $FF: synthetic method
   public Vec3i subtract(final Vec3i var1) {
      return this.subtract(var1);
   }

   // $FF: synthetic method
   public Vec3i offset(final Vec3i var1) {
      return this.offset(var1);
   }

   // $FF: synthetic method
   public Vec3i offset(final int var1, final int var2, final int var3) {
      return this.offset(var1, var2, var3);
   }

   static {
      CODEC = Codec.INT_STREAM.comapFlatMap((var0) -> {
         return Util.fixedSize((IntStream)var0, 3).map((var0x) -> {
            return new BlockPos(var0x[0], var0x[1], var0x[2]);
         });
      }, (var0) -> {
         return IntStream.of(new int[]{var0.getX(), var0.getY(), var0.getZ()});
      }).stable();
      STREAM_CODEC = new StreamCodec<ByteBuf, BlockPos>() {
         public BlockPos decode(ByteBuf var1) {
            return FriendlyByteBuf.readBlockPos(var1);
         }

         public void encode(ByteBuf var1, BlockPos var2) {
            FriendlyByteBuf.writeBlockPos(var1, var2);
         }

         // $FF: synthetic method
         public void encode(final Object var1, final Object var2) {
            this.encode((ByteBuf)var1, (BlockPos)var2);
         }

         // $FF: synthetic method
         public Object decode(final Object var1) {
            return this.decode((ByteBuf)var1);
         }
      };
      LOGGER = LogUtils.getLogger();
      ZERO = new BlockPos(0, 0, 0);
      PACKED_HORIZONTAL_LENGTH = 1 + Mth.log2(Mth.smallestEncompassingPowerOfTwo(30000000));
      PACKED_Y_LENGTH = 64 - 2 * PACKED_HORIZONTAL_LENGTH;
      PACKED_X_MASK = (1L << PACKED_HORIZONTAL_LENGTH) - 1L;
      PACKED_Y_MASK = (1L << PACKED_Y_LENGTH) - 1L;
      PACKED_Z_MASK = (1L << PACKED_HORIZONTAL_LENGTH) - 1L;
      Z_OFFSET = PACKED_Y_LENGTH;
      X_OFFSET = PACKED_Y_LENGTH + PACKED_HORIZONTAL_LENGTH;
      MAX_HORIZONTAL_COORDINATE = (1 << PACKED_HORIZONTAL_LENGTH) / 2 - 1;
   }

   public static class MutableBlockPos extends BlockPos {
      public MutableBlockPos() {
         this(0, 0, 0);
      }

      public MutableBlockPos(int var1, int var2, int var3) {
         super(var1, var2, var3);
      }

      public MutableBlockPos(double var1, double var3, double var5) {
         this(Mth.floor(var1), Mth.floor(var3), Mth.floor(var5));
      }

      public BlockPos offset(int var1, int var2, int var3) {
         return super.offset(var1, var2, var3).immutable();
      }

      public BlockPos multiply(int var1) {
         return super.multiply(var1).immutable();
      }

      public BlockPos relative(Direction var1, int var2) {
         return super.relative(var1, var2).immutable();
      }

      public BlockPos relative(Direction.Axis var1, int var2) {
         return super.relative(var1, var2).immutable();
      }

      public BlockPos rotate(Rotation var1) {
         return super.rotate(var1).immutable();
      }

      public MutableBlockPos set(int var1, int var2, int var3) {
         this.setX(var1);
         this.setY(var2);
         this.setZ(var3);
         return this;
      }

      public MutableBlockPos set(double var1, double var3, double var5) {
         return this.set(Mth.floor(var1), Mth.floor(var3), Mth.floor(var5));
      }

      public MutableBlockPos set(Vec3i var1) {
         return this.set(var1.getX(), var1.getY(), var1.getZ());
      }

      public MutableBlockPos set(long var1) {
         return this.set(getX(var1), getY(var1), getZ(var1));
      }

      public MutableBlockPos set(AxisCycle var1, int var2, int var3, int var4) {
         return this.set(var1.cycle(var2, var3, var4, Direction.Axis.X), var1.cycle(var2, var3, var4, Direction.Axis.Y), var1.cycle(var2, var3, var4, Direction.Axis.Z));
      }

      public MutableBlockPos setWithOffset(Vec3i var1, Direction var2) {
         return this.set(var1.getX() + var2.getStepX(), var1.getY() + var2.getStepY(), var1.getZ() + var2.getStepZ());
      }

      public MutableBlockPos setWithOffset(Vec3i var1, int var2, int var3, int var4) {
         return this.set(var1.getX() + var2, var1.getY() + var3, var1.getZ() + var4);
      }

      public MutableBlockPos setWithOffset(Vec3i var1, Vec3i var2) {
         return this.set(var1.getX() + var2.getX(), var1.getY() + var2.getY(), var1.getZ() + var2.getZ());
      }

      public MutableBlockPos move(Direction var1) {
         return this.move(var1, 1);
      }

      public MutableBlockPos move(Direction var1, int var2) {
         return this.set(this.getX() + var1.getStepX() * var2, this.getY() + var1.getStepY() * var2, this.getZ() + var1.getStepZ() * var2);
      }

      public MutableBlockPos move(int var1, int var2, int var3) {
         return this.set(this.getX() + var1, this.getY() + var2, this.getZ() + var3);
      }

      public MutableBlockPos move(Vec3i var1) {
         return this.set(this.getX() + var1.getX(), this.getY() + var1.getY(), this.getZ() + var1.getZ());
      }

      public MutableBlockPos clamp(Direction.Axis var1, int var2, int var3) {
         switch (var1) {
            case X -> {
               return this.set(Mth.clamp(this.getX(), var2, var3), this.getY(), this.getZ());
            }
            case Y -> {
               return this.set(this.getX(), Mth.clamp(this.getY(), var2, var3), this.getZ());
            }
            case Z -> {
               return this.set(this.getX(), this.getY(), Mth.clamp(this.getZ(), var2, var3));
            }
            default -> throw new IllegalStateException("Unable to clamp axis " + String.valueOf(var1));
         }
      }

      public MutableBlockPos setX(int var1) {
         super.setX(var1);
         return this;
      }

      public MutableBlockPos setY(int var1) {
         super.setY(var1);
         return this;
      }

      public MutableBlockPos setZ(int var1) {
         super.setZ(var1);
         return this;
      }

      public BlockPos immutable() {
         return new BlockPos(this);
      }

      // $FF: synthetic method
      public Vec3i cross(final Vec3i var1) {
         return super.cross(var1);
      }

      // $FF: synthetic method
      public Vec3i relative(final Direction.Axis var1, final int var2) {
         return this.relative(var1, var2);
      }

      // $FF: synthetic method
      public Vec3i relative(final Direction var1, final int var2) {
         return this.relative(var1, var2);
      }

      // $FF: synthetic method
      public Vec3i relative(final Direction var1) {
         return super.relative(var1);
      }

      // $FF: synthetic method
      public Vec3i east(final int var1) {
         return super.east(var1);
      }

      // $FF: synthetic method
      public Vec3i east() {
         return super.east();
      }

      // $FF: synthetic method
      public Vec3i west(final int var1) {
         return super.west(var1);
      }

      // $FF: synthetic method
      public Vec3i west() {
         return super.west();
      }

      // $FF: synthetic method
      public Vec3i south(final int var1) {
         return super.south(var1);
      }

      // $FF: synthetic method
      public Vec3i south() {
         return super.south();
      }

      // $FF: synthetic method
      public Vec3i north(final int var1) {
         return super.north(var1);
      }

      // $FF: synthetic method
      public Vec3i north() {
         return super.north();
      }

      // $FF: synthetic method
      public Vec3i below(final int var1) {
         return super.below(var1);
      }

      // $FF: synthetic method
      public Vec3i below() {
         return super.below();
      }

      // $FF: synthetic method
      public Vec3i above(final int var1) {
         return super.above(var1);
      }

      // $FF: synthetic method
      public Vec3i above() {
         return super.above();
      }

      // $FF: synthetic method
      public Vec3i multiply(final int var1) {
         return this.multiply(var1);
      }

      // $FF: synthetic method
      public Vec3i subtract(final Vec3i var1) {
         return super.subtract(var1);
      }

      // $FF: synthetic method
      public Vec3i offset(final Vec3i var1) {
         return super.offset(var1);
      }

      // $FF: synthetic method
      public Vec3i offset(final int var1, final int var2, final int var3) {
         return this.offset(var1, var2, var3);
      }

      // $FF: synthetic method
      public Vec3i setZ(final int var1) {
         return this.setZ(var1);
      }

      // $FF: synthetic method
      public Vec3i setY(final int var1) {
         return this.setY(var1);
      }

      // $FF: synthetic method
      public Vec3i setX(final int var1) {
         return this.setX(var1);
      }
   }

   public static enum TraversalNodeStatus {
      ACCEPT,
      SKIP,
      STOP;

      private TraversalNodeStatus() {
      }

      // $FF: synthetic method
      private static TraversalNodeStatus[] $values() {
         return new TraversalNodeStatus[]{ACCEPT, SKIP, STOP};
      }
   }
}
