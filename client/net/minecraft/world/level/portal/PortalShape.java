package net.minecraft.world.level.portal;

import java.util.Optional;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.BlockUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.NetherPortalBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.apache.commons.lang3.mutable.MutableInt;

public class PortalShape {
   private static final int MIN_WIDTH = 2;
   public static final int MAX_WIDTH = 21;
   private static final int MIN_HEIGHT = 3;
   public static final int MAX_HEIGHT = 21;
   private static final BlockBehaviour.StatePredicate FRAME = (var0, var1, var2) -> {
      return var0.is(Blocks.OBSIDIAN);
   };
   private static final float SAFE_TRAVEL_MAX_ENTITY_XY = 4.0F;
   private static final double SAFE_TRAVEL_MAX_VERTICAL_DELTA = 1.0;
   private final Direction.Axis axis;
   private final Direction rightDir;
   private final int numPortalBlocks;
   private final BlockPos bottomLeft;
   private final int height;
   private final int width;

   private PortalShape(Direction.Axis var1, int var2, Direction var3, BlockPos var4, int var5, int var6) {
      super();
      this.axis = var1;
      this.numPortalBlocks = var2;
      this.rightDir = var3;
      this.bottomLeft = var4;
      this.width = var5;
      this.height = var6;
   }

   public static Optional<PortalShape> findEmptyPortalShape(LevelAccessor var0, BlockPos var1, Direction.Axis var2) {
      return findPortalShape(var0, var1, (var0x) -> {
         return var0x.isValid() && var0x.numPortalBlocks == 0;
      }, var2);
   }

   public static Optional<PortalShape> findPortalShape(LevelAccessor var0, BlockPos var1, Predicate<PortalShape> var2, Direction.Axis var3) {
      Optional var4 = Optional.of(findAnyShape(var0, var1, var3)).filter(var2);
      if (var4.isPresent()) {
         return var4;
      } else {
         Direction.Axis var5 = var3 == Direction.Axis.X ? Direction.Axis.Z : Direction.Axis.X;
         return Optional.of(findAnyShape(var0, var1, var5)).filter(var2);
      }
   }

   public static PortalShape findAnyShape(BlockGetter var0, BlockPos var1, Direction.Axis var2) {
      Direction var3 = var2 == Direction.Axis.X ? Direction.WEST : Direction.SOUTH;
      BlockPos var4 = calculateBottomLeft(var0, var3, var1);
      if (var4 == null) {
         return new PortalShape(var2, 0, var3, var1, 0, 0);
      } else {
         int var5 = calculateWidth(var0, var4, var3);
         if (var5 == 0) {
            return new PortalShape(var2, 0, var3, var4, 0, 0);
         } else {
            MutableInt var6 = new MutableInt();
            int var7 = calculateHeight(var0, var4, var3, var5, var6);
            return new PortalShape(var2, var6.getValue(), var3, var4, var5, var7);
         }
      }
   }

   @Nullable
   private static BlockPos calculateBottomLeft(BlockGetter var0, Direction var1, BlockPos var2) {
      for(int var3 = Math.max(var0.getMinY(), var2.getY() - 21); var2.getY() > var3 && isEmpty(var0.getBlockState(var2.below())); var2 = var2.below()) {
      }

      Direction var4 = var1.getOpposite();
      int var5 = getDistanceUntilEdgeAboveFrame(var0, var2, var4) - 1;
      return var5 < 0 ? null : var2.relative(var4, var5);
   }

   private static int calculateWidth(BlockGetter var0, BlockPos var1, Direction var2) {
      int var3 = getDistanceUntilEdgeAboveFrame(var0, var1, var2);
      return var3 >= 2 && var3 <= 21 ? var3 : 0;
   }

   private static int getDistanceUntilEdgeAboveFrame(BlockGetter var0, BlockPos var1, Direction var2) {
      BlockPos.MutableBlockPos var3 = new BlockPos.MutableBlockPos();

      for(int var4 = 0; var4 <= 21; ++var4) {
         var3.set(var1).move(var2, var4);
         BlockState var5 = var0.getBlockState(var3);
         if (!isEmpty(var5)) {
            if (FRAME.test(var5, var0, var3)) {
               return var4;
            }
            break;
         }

         BlockState var6 = var0.getBlockState(var3.move(Direction.DOWN));
         if (!FRAME.test(var6, var0, var3)) {
            break;
         }
      }

      return 0;
   }

   private static int calculateHeight(BlockGetter var0, BlockPos var1, Direction var2, int var3, MutableInt var4) {
      BlockPos.MutableBlockPos var5 = new BlockPos.MutableBlockPos();
      int var6 = getDistanceUntilTop(var0, var1, var2, var5, var3, var4);
      return var6 >= 3 && var6 <= 21 && hasTopFrame(var0, var1, var2, var5, var3, var6) ? var6 : 0;
   }

   private static boolean hasTopFrame(BlockGetter var0, BlockPos var1, Direction var2, BlockPos.MutableBlockPos var3, int var4, int var5) {
      for(int var6 = 0; var6 < var4; ++var6) {
         BlockPos.MutableBlockPos var7 = var3.set(var1).move(Direction.UP, var5).move(var2, var6);
         if (!FRAME.test(var0.getBlockState(var7), var0, var7)) {
            return false;
         }
      }

      return true;
   }

   private static int getDistanceUntilTop(BlockGetter var0, BlockPos var1, Direction var2, BlockPos.MutableBlockPos var3, int var4, MutableInt var5) {
      for(int var6 = 0; var6 < 21; ++var6) {
         var3.set(var1).move(Direction.UP, var6).move(var2, -1);
         if (!FRAME.test(var0.getBlockState(var3), var0, var3)) {
            return var6;
         }

         var3.set(var1).move(Direction.UP, var6).move(var2, var4);
         if (!FRAME.test(var0.getBlockState(var3), var0, var3)) {
            return var6;
         }

         for(int var7 = 0; var7 < var4; ++var7) {
            var3.set(var1).move(Direction.UP, var6).move(var2, var7);
            BlockState var8 = var0.getBlockState(var3);
            if (!isEmpty(var8)) {
               return var6;
            }

            if (var8.is(Blocks.NETHER_PORTAL)) {
               var5.increment();
            }
         }
      }

      return 21;
   }

   private static boolean isEmpty(BlockState var0) {
      return var0.isAir() || var0.is(BlockTags.FIRE) || var0.is(Blocks.NETHER_PORTAL);
   }

   public boolean isValid() {
      return this.width >= 2 && this.width <= 21 && this.height >= 3 && this.height <= 21;
   }

   public void createPortalBlocks(LevelAccessor var1) {
      BlockState var2 = (BlockState)Blocks.NETHER_PORTAL.defaultBlockState().setValue(NetherPortalBlock.AXIS, this.axis);
      BlockPos.betweenClosed(this.bottomLeft, this.bottomLeft.relative(Direction.UP, this.height - 1).relative(this.rightDir, this.width - 1)).forEach((var2x) -> {
         var1.setBlock(var2x, var2, 18);
      });
   }

   public boolean isComplete() {
      return this.isValid() && this.numPortalBlocks == this.width * this.height;
   }

   public static Vec3 getRelativePosition(BlockUtil.FoundRectangle var0, Direction.Axis var1, Vec3 var2, EntityDimensions var3) {
      double var4 = (double)var0.axis1Size - (double)var3.width();
      double var6 = (double)var0.axis2Size - (double)var3.height();
      BlockPos var8 = var0.minCorner;
      double var9;
      double var11;
      if (var4 > 0.0) {
         var11 = (double)var8.get(var1) + (double)var3.width() / 2.0;
         var9 = Mth.clamp(Mth.inverseLerp(var2.get(var1) - var11, 0.0, var4), 0.0, 1.0);
      } else {
         var9 = 0.5;
      }

      Direction.Axis var13;
      if (var6 > 0.0) {
         var13 = Direction.Axis.Y;
         var11 = Mth.clamp(Mth.inverseLerp(var2.get(var13) - (double)var8.get(var13), 0.0, var6), 0.0, 1.0);
      } else {
         var11 = 0.0;
      }

      var13 = var1 == Direction.Axis.X ? Direction.Axis.Z : Direction.Axis.X;
      double var14 = var2.get(var13) - ((double)var8.get(var13) + 0.5);
      return new Vec3(var9, var11, var14);
   }

   public static Vec3 findCollisionFreePosition(Vec3 var0, ServerLevel var1, Entity var2, EntityDimensions var3) {
      if (!(var3.width() > 4.0F) && !(var3.height() > 4.0F)) {
         double var4 = (double)var3.height() / 2.0;
         Vec3 var6 = var0.add(0.0, var4, 0.0);
         VoxelShape var7 = Shapes.create(AABB.ofSize(var6, (double)var3.width(), 0.0, (double)var3.width()).expandTowards(0.0, 1.0, 0.0).inflate(1.0E-6));
         Optional var8 = var1.findFreePosition(var2, var7, var6, (double)var3.width(), (double)var3.height(), (double)var3.width());
         Optional var9 = var8.map((var2x) -> {
            return var2x.subtract(0.0, var4, 0.0);
         });
         return (Vec3)var9.orElse(var0);
      } else {
         return var0;
      }
   }
}
