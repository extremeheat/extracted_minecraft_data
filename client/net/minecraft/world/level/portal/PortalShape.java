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
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.NetherPortalBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

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
   private final LevelAccessor level;
   private final Direction.Axis axis;
   private final Direction rightDir;
   private int numPortalBlocks;
   @Nullable
   private BlockPos bottomLeft;
   private int height;
   private final int width;

   public static Optional<PortalShape> findEmptyPortalShape(LevelAccessor var0, BlockPos var1, Direction.Axis var2) {
      return findPortalShape(var0, var1, (var0x) -> {
         return var0x.isValid() && var0x.numPortalBlocks == 0;
      }, var2);
   }

   public static Optional<PortalShape> findPortalShape(LevelAccessor var0, BlockPos var1, Predicate<PortalShape> var2, Direction.Axis var3) {
      Optional var4 = Optional.of(new PortalShape(var0, var1, var3)).filter(var2);
      if (var4.isPresent()) {
         return var4;
      } else {
         Direction.Axis var5 = var3 == Direction.Axis.X ? Direction.Axis.Z : Direction.Axis.X;
         return Optional.of(new PortalShape(var0, var1, var5)).filter(var2);
      }
   }

   public PortalShape(LevelAccessor var1, BlockPos var2, Direction.Axis var3) {
      super();
      this.level = var1;
      this.axis = var3;
      this.rightDir = var3 == Direction.Axis.X ? Direction.WEST : Direction.SOUTH;
      this.bottomLeft = this.calculateBottomLeft(var2);
      if (this.bottomLeft == null) {
         this.bottomLeft = var2;
         this.width = 1;
         this.height = 1;
      } else {
         this.width = this.calculateWidth();
         if (this.width > 0) {
            this.height = this.calculateHeight();
         }
      }

   }

   @Nullable
   private BlockPos calculateBottomLeft(BlockPos var1) {
      for(int var2 = Math.max(this.level.getMinBuildHeight(), var1.getY() - 21); var1.getY() > var2 && isEmpty(this.level.getBlockState(var1.below())); var1 = var1.below()) {
      }

      Direction var3 = this.rightDir.getOpposite();
      int var4 = this.getDistanceUntilEdgeAboveFrame(var1, var3) - 1;
      return var4 < 0 ? null : var1.relative(var3, var4);
   }

   private int calculateWidth() {
      int var1 = this.getDistanceUntilEdgeAboveFrame(this.bottomLeft, this.rightDir);
      return var1 >= 2 && var1 <= 21 ? var1 : 0;
   }

   private int getDistanceUntilEdgeAboveFrame(BlockPos var1, Direction var2) {
      BlockPos.MutableBlockPos var3 = new BlockPos.MutableBlockPos();

      for(int var4 = 0; var4 <= 21; ++var4) {
         var3.set(var1).move(var2, var4);
         BlockState var5 = this.level.getBlockState(var3);
         if (!isEmpty(var5)) {
            if (FRAME.test(var5, this.level, var3)) {
               return var4;
            }
            break;
         }

         BlockState var6 = this.level.getBlockState(var3.move(Direction.DOWN));
         if (!FRAME.test(var6, this.level, var3)) {
            break;
         }
      }

      return 0;
   }

   private int calculateHeight() {
      BlockPos.MutableBlockPos var1 = new BlockPos.MutableBlockPos();
      int var2 = this.getDistanceUntilTop(var1);
      return var2 >= 3 && var2 <= 21 && this.hasTopFrame(var1, var2) ? var2 : 0;
   }

   private boolean hasTopFrame(BlockPos.MutableBlockPos var1, int var2) {
      for(int var3 = 0; var3 < this.width; ++var3) {
         BlockPos.MutableBlockPos var4 = var1.set(this.bottomLeft).move(Direction.UP, var2).move(this.rightDir, var3);
         if (!FRAME.test(this.level.getBlockState(var4), this.level, var4)) {
            return false;
         }
      }

      return true;
   }

   private int getDistanceUntilTop(BlockPos.MutableBlockPos var1) {
      for(int var2 = 0; var2 < 21; ++var2) {
         var1.set(this.bottomLeft).move(Direction.UP, var2).move(this.rightDir, -1);
         if (!FRAME.test(this.level.getBlockState(var1), this.level, var1)) {
            return var2;
         }

         var1.set(this.bottomLeft).move(Direction.UP, var2).move(this.rightDir, this.width);
         if (!FRAME.test(this.level.getBlockState(var1), this.level, var1)) {
            return var2;
         }

         for(int var3 = 0; var3 < this.width; ++var3) {
            var1.set(this.bottomLeft).move(Direction.UP, var2).move(this.rightDir, var3);
            BlockState var4 = this.level.getBlockState(var1);
            if (!isEmpty(var4)) {
               return var2;
            }

            if (var4.is(Blocks.NETHER_PORTAL)) {
               ++this.numPortalBlocks;
            }
         }
      }

      return 21;
   }

   private static boolean isEmpty(BlockState var0) {
      return var0.isAir() || var0.is(BlockTags.FIRE) || var0.is(Blocks.NETHER_PORTAL);
   }

   public boolean isValid() {
      return this.bottomLeft != null && this.width >= 2 && this.width <= 21 && this.height >= 3 && this.height <= 21;
   }

   public void createPortalBlocks() {
      BlockState var1 = (BlockState)Blocks.NETHER_PORTAL.defaultBlockState().setValue(NetherPortalBlock.AXIS, this.axis);
      BlockPos.betweenClosed(this.bottomLeft, this.bottomLeft.relative(Direction.UP, this.height - 1).relative(this.rightDir, this.width - 1)).forEach((var2) -> {
         this.level.setBlock(var2, var1, 18);
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

   public static PortalInfo createPortalInfo(ServerLevel var0, BlockUtil.FoundRectangle var1, Direction.Axis var2, Vec3 var3, Entity var4, Vec3 var5, float var6, float var7) {
      BlockPos var8 = var1.minCorner;
      BlockState var9 = var0.getBlockState(var8);
      Direction.Axis var10 = (Direction.Axis)var9.getOptionalValue(BlockStateProperties.HORIZONTAL_AXIS).orElse(Direction.Axis.X);
      double var11 = (double)var1.axis1Size;
      double var13 = (double)var1.axis2Size;
      EntityDimensions var15 = var4.getDimensions(var4.getPose());
      int var16 = var2 == var10 ? 0 : 90;
      Vec3 var17 = var2 == var10 ? var5 : new Vec3(var5.z, var5.y, -var5.x);
      double var18 = (double)var15.width() / 2.0 + (var11 - (double)var15.width()) * var3.x();
      double var20 = (var13 - (double)var15.height()) * var3.y();
      double var22 = 0.5 + var3.z();
      boolean var24 = var10 == Direction.Axis.X;
      Vec3 var25 = new Vec3((double)var8.getX() + (var24 ? var18 : var22), (double)var8.getY() + var20, (double)var8.getZ() + (var24 ? var22 : var18));
      Vec3 var26 = findCollisionFreePosition(var25, var0, var4, var15);
      return new PortalInfo(var26, var17, var6 + (float)var16, var7);
   }

   private static Vec3 findCollisionFreePosition(Vec3 var0, ServerLevel var1, Entity var2, EntityDimensions var3) {
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
