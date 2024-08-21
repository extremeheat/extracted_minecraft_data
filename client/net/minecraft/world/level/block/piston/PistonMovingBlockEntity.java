package net.minecraft.world.level.block.piston;

import java.util.Iterator;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.PistonType;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.redstone.ExperimentalRedstoneUtils;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class PistonMovingBlockEntity extends BlockEntity {
   private static final int TICKS_TO_EXTEND = 2;
   private static final double PUSH_OFFSET = 0.01;
   public static final double TICK_MOVEMENT = 0.51;
   private BlockState movedState = Blocks.AIR.defaultBlockState();
   private Direction direction;
   private boolean extending;
   private boolean isSourcePiston;
   private static final ThreadLocal<Direction> NOCLIP = ThreadLocal.withInitial(() -> null);
   private float progress;
   private float progressO;
   private long lastTicked;
   private int deathTicks;

   public PistonMovingBlockEntity(BlockPos var1, BlockState var2) {
      super(BlockEntityType.PISTON, var1, var2);
   }

   public PistonMovingBlockEntity(BlockPos var1, BlockState var2, BlockState var3, Direction var4, boolean var5, boolean var6) {
      this(var1, var2);
      this.movedState = var3;
      this.direction = var4;
      this.extending = var5;
      this.isSourcePiston = var6;
   }

   @Override
   public CompoundTag getUpdateTag(HolderLookup.Provider var1) {
      return this.saveCustomOnly(var1);
   }

   public boolean isExtending() {
      return this.extending;
   }

   public Direction getDirection() {
      return this.direction;
   }

   public boolean isSourcePiston() {
      return this.isSourcePiston;
   }

   public float getProgress(float var1) {
      if (var1 > 1.0F) {
         var1 = 1.0F;
      }

      return Mth.lerp(var1, this.progressO, this.progress);
   }

   public float getXOff(float var1) {
      return (float)this.direction.getStepX() * this.getExtendedProgress(this.getProgress(var1));
   }

   public float getYOff(float var1) {
      return (float)this.direction.getStepY() * this.getExtendedProgress(this.getProgress(var1));
   }

   public float getZOff(float var1) {
      return (float)this.direction.getStepZ() * this.getExtendedProgress(this.getProgress(var1));
   }

   private float getExtendedProgress(float var1) {
      return this.extending ? var1 - 1.0F : 1.0F - var1;
   }

   private BlockState getCollisionRelatedBlockState() {
      return !this.isExtending() && this.isSourcePiston() && this.movedState.getBlock() instanceof PistonBaseBlock
         ? Blocks.PISTON_HEAD
            .defaultBlockState()
            .setValue(PistonHeadBlock.SHORT, Boolean.valueOf(this.progress > 0.25F))
            .setValue(PistonHeadBlock.TYPE, this.movedState.is(Blocks.STICKY_PISTON) ? PistonType.STICKY : PistonType.DEFAULT)
            .setValue(PistonHeadBlock.FACING, this.movedState.getValue(PistonBaseBlock.FACING))
         : this.movedState;
   }

   private static void moveCollidedEntities(Level var0, BlockPos var1, float var2, PistonMovingBlockEntity var3) {
      Direction var4 = var3.getMovementDirection();
      double var5 = (double)(var2 - var3.progress);
      VoxelShape var7 = var3.getCollisionRelatedBlockState().getCollisionShape(var0, var1);
      if (!var7.isEmpty()) {
         AABB var8 = moveByPositionAndProgress(var1, var7.bounds(), var3);
         List var9 = var0.getEntities(null, PistonMath.getMovementArea(var8, var4, var5).minmax(var8));
         if (!var9.isEmpty()) {
            List var10 = var7.toAabbs();
            boolean var11 = var3.movedState.is(Blocks.SLIME_BLOCK);
            Iterator var12 = var9.iterator();

            while (true) {
               Entity var13;
               while (true) {
                  if (!var12.hasNext()) {
                     return;
                  }

                  var13 = (Entity)var12.next();
                  if (var13.getPistonPushReaction() != PushReaction.IGNORE) {
                     if (!var11) {
                        break;
                     }

                     if (!(var13 instanceof ServerPlayer)) {
                        Vec3 var14 = var13.getDeltaMovement();
                        double var15 = var14.x;
                        double var17 = var14.y;
                        double var19 = var14.z;
                        switch (var4.getAxis()) {
                           case X:
                              var15 = (double)var4.getStepX();
                              break;
                           case Y:
                              var17 = (double)var4.getStepY();
                              break;
                           case Z:
                              var19 = (double)var4.getStepZ();
                        }

                        var13.setDeltaMovement(var15, var17, var19);
                        break;
                     }
                  }
               }

               double var21 = 0.0;

               for (AABB var23 : var10) {
                  AABB var18 = PistonMath.getMovementArea(moveByPositionAndProgress(var1, var23, var3), var4, var5);
                  AABB var24 = var13.getBoundingBox();
                  if (var18.intersects(var24)) {
                     var21 = Math.max(var21, getMovement(var18, var4, var24));
                     if (var21 >= var5) {
                        break;
                     }
                  }
               }

               if (!(var21 <= 0.0)) {
                  var21 = Math.min(var21, var5) + 0.01;
                  moveEntityByPiston(var4, var13, var21, var4);
                  if (!var3.extending && var3.isSourcePiston) {
                     fixEntityWithinPistonBase(var1, var13, var4, var5);
                  }
               }
            }
         }
      }
   }

   private static void moveEntityByPiston(Direction var0, Entity var1, double var2, Direction var4) {
      NOCLIP.set(var0);
      var1.move(MoverType.PISTON, new Vec3(var2 * (double)var4.getStepX(), var2 * (double)var4.getStepY(), var2 * (double)var4.getStepZ()));
      var1.applyEffectsFromBlocks();
      NOCLIP.set(null);
   }

   private static void moveStuckEntities(Level var0, BlockPos var1, float var2, PistonMovingBlockEntity var3) {
      if (var3.isStickyForEntities()) {
         Direction var4 = var3.getMovementDirection();
         if (var4.getAxis().isHorizontal()) {
            double var5 = var3.movedState.getCollisionShape(var0, var1).max(Direction.Axis.Y);
            AABB var7 = moveByPositionAndProgress(var1, new AABB(0.0, var5, 0.0, 1.0, 1.5000010000000001, 1.0), var3);
            double var8 = (double)(var2 - var3.progress);

            for (Entity var12 : var0.getEntities((Entity)null, var7, var2x -> matchesStickyCritera(var7, var2x, var1))) {
               moveEntityByPiston(var4, var12, var8, var4);
            }
         }
      }
   }

   private static boolean matchesStickyCritera(AABB var0, Entity var1, BlockPos var2) {
      return var1.getPistonPushReaction() == PushReaction.NORMAL
         && var1.onGround()
         && (var1.isSupportedBy(var2) || var1.getX() >= var0.minX && var1.getX() <= var0.maxX && var1.getZ() >= var0.minZ && var1.getZ() <= var0.maxZ);
   }

   private boolean isStickyForEntities() {
      return this.movedState.is(Blocks.HONEY_BLOCK);
   }

   public Direction getMovementDirection() {
      return this.extending ? this.direction : this.direction.getOpposite();
   }

   private static double getMovement(AABB var0, Direction var1, AABB var2) {
      switch (var1) {
         case EAST:
            return var0.maxX - var2.minX;
         case WEST:
            return var2.maxX - var0.minX;
         case UP:
         default:
            return var0.maxY - var2.minY;
         case DOWN:
            return var2.maxY - var0.minY;
         case SOUTH:
            return var0.maxZ - var2.minZ;
         case NORTH:
            return var2.maxZ - var0.minZ;
      }
   }

   private static AABB moveByPositionAndProgress(BlockPos var0, AABB var1, PistonMovingBlockEntity var2) {
      double var3 = (double)var2.getExtendedProgress(var2.progress);
      return var1.move(
         (double)var0.getX() + var3 * (double)var2.direction.getStepX(),
         (double)var0.getY() + var3 * (double)var2.direction.getStepY(),
         (double)var0.getZ() + var3 * (double)var2.direction.getStepZ()
      );
   }

   private static void fixEntityWithinPistonBase(BlockPos var0, Entity var1, Direction var2, double var3) {
      AABB var5 = var1.getBoundingBox();
      AABB var6 = Shapes.block().bounds().move(var0);
      if (var5.intersects(var6)) {
         Direction var7 = var2.getOpposite();
         double var8 = getMovement(var6, var7, var5) + 0.01;
         double var10 = getMovement(var6, var7, var5.intersect(var6)) + 0.01;
         if (Math.abs(var8 - var10) < 0.01) {
            var8 = Math.min(var8, var3) + 0.01;
            moveEntityByPiston(var2, var1, var8, var7);
         }
      }
   }

   public BlockState getMovedState() {
      return this.movedState;
   }

   public void finalTick() {
      if (this.level != null && (this.progressO < 1.0F || this.level.isClientSide)) {
         this.progress = 1.0F;
         this.progressO = this.progress;
         this.level.removeBlockEntity(this.worldPosition);
         this.setRemoved();
         if (this.level.getBlockState(this.worldPosition).is(Blocks.MOVING_PISTON)) {
            BlockState var1;
            if (this.isSourcePiston) {
               var1 = Blocks.AIR.defaultBlockState();
            } else {
               var1 = Block.updateFromNeighbourShapes(this.movedState, this.level, this.worldPosition);
            }

            this.level.setBlock(this.worldPosition, var1, 3);
            this.level
               .neighborChanged(this.worldPosition, var1.getBlock(), ExperimentalRedstoneUtils.initialOrientation(this.level, this.getPushDirection(), null));
         }
      }
   }

   public Direction getPushDirection() {
      return this.extending ? this.direction : this.direction.getOpposite();
   }

   public static void tick(Level var0, BlockPos var1, BlockState var2, PistonMovingBlockEntity var3) {
      var3.lastTicked = var0.getGameTime();
      var3.progressO = var3.progress;
      if (var3.progressO >= 1.0F) {
         if (var0.isClientSide && var3.deathTicks < 5) {
            var3.deathTicks++;
         } else {
            var0.removeBlockEntity(var1);
            var3.setRemoved();
            if (var0.getBlockState(var1).is(Blocks.MOVING_PISTON)) {
               BlockState var5 = Block.updateFromNeighbourShapes(var3.movedState, var0, var1);
               if (var5.isAir()) {
                  var0.setBlock(var1, var3.movedState, 84);
                  Block.updateOrDestroy(var3.movedState, var5, var0, var1, 3);
               } else {
                  if (var5.hasProperty(BlockStateProperties.WATERLOGGED) && var5.getValue(BlockStateProperties.WATERLOGGED)) {
                     var5 = var5.setValue(BlockStateProperties.WATERLOGGED, Boolean.valueOf(false));
                  }

                  var0.setBlock(var1, var5, 67);
                  var0.neighborChanged(var1, var5.getBlock(), ExperimentalRedstoneUtils.initialOrientation(var0, var3.getPushDirection(), null));
               }
            }
         }
      } else {
         float var4 = var3.progress + 0.5F;
         moveCollidedEntities(var0, var1, var4, var3);
         moveStuckEntities(var0, var1, var4, var3);
         var3.progress = var4;
         if (var3.progress >= 1.0F) {
            var3.progress = 1.0F;
         }
      }
   }

   @Override
   protected void loadAdditional(CompoundTag var1, HolderLookup.Provider var2) {
      super.loadAdditional(var1, var2);
      Object var3 = this.level != null ? this.level.holderLookup(Registries.BLOCK) : BuiltInRegistries.BLOCK.asLookup();
      this.movedState = NbtUtils.readBlockState((HolderGetter<Block>)var3, var1.getCompound("blockState"));
      this.direction = Direction.from3DDataValue(var1.getInt("facing"));
      this.progress = var1.getFloat("progress");
      this.progressO = this.progress;
      this.extending = var1.getBoolean("extending");
      this.isSourcePiston = var1.getBoolean("source");
   }

   @Override
   protected void saveAdditional(CompoundTag var1, HolderLookup.Provider var2) {
      super.saveAdditional(var1, var2);
      var1.put("blockState", NbtUtils.writeBlockState(this.movedState));
      var1.putInt("facing", this.direction.get3DDataValue());
      var1.putFloat("progress", this.progressO);
      var1.putBoolean("extending", this.extending);
      var1.putBoolean("source", this.isSourcePiston);
   }

   public VoxelShape getCollisionShape(BlockGetter var1, BlockPos var2) {
      VoxelShape var3;
      if (!this.extending && this.isSourcePiston && this.movedState.getBlock() instanceof PistonBaseBlock) {
         var3 = this.movedState.setValue(PistonBaseBlock.EXTENDED, Boolean.valueOf(true)).getCollisionShape(var1, var2);
      } else {
         var3 = Shapes.empty();
      }

      Direction var4 = NOCLIP.get();
      if ((double)this.progress < 1.0 && var4 == this.getMovementDirection()) {
         return var3;
      } else {
         BlockState var5;
         if (this.isSourcePiston()) {
            var5 = Blocks.PISTON_HEAD
               .defaultBlockState()
               .setValue(PistonHeadBlock.FACING, this.direction)
               .setValue(PistonHeadBlock.SHORT, Boolean.valueOf(this.extending != 1.0F - this.progress < 0.25F));
         } else {
            var5 = this.movedState;
         }

         float var6 = this.getExtendedProgress(this.progress);
         double var7 = (double)((float)this.direction.getStepX() * var6);
         double var9 = (double)((float)this.direction.getStepY() * var6);
         double var11 = (double)((float)this.direction.getStepZ() * var6);
         return Shapes.or(var3, var5.getCollisionShape(var1, var2).move(var7, var9, var11));
      }
   }

   public long getLastTicked() {
      return this.lastTicked;
   }

   @Override
   public void setLevel(Level var1) {
      super.setLevel(var1);
      if (var1.holderLookup(Registries.BLOCK).get(this.movedState.getBlock().builtInRegistryHolder().key()).isEmpty()) {
         this.movedState = Blocks.AIR.defaultBlockState();
      }
   }
}
