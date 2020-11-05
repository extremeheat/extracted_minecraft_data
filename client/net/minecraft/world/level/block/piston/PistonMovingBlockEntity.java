package net.minecraft.world.level.block.piston;

import java.util.Iterator;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.TickableBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.PistonType;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class PistonMovingBlockEntity extends BlockEntity implements TickableBlockEntity {
   private BlockState movedState;
   private Direction direction;
   private boolean extending;
   private boolean isSourcePiston;
   private static final ThreadLocal<Direction> NOCLIP = ThreadLocal.withInitial(() -> {
      return null;
   });
   private float progress;
   private float progressO;
   private long lastTicked;
   private int deathTicks;

   public PistonMovingBlockEntity() {
      super(BlockEntityType.PISTON);
   }

   public PistonMovingBlockEntity(BlockState var1, Direction var2, boolean var3, boolean var4) {
      this();
      this.movedState = var1;
      this.direction = var2;
      this.extending = var3;
      this.isSourcePiston = var4;
   }

   public CompoundTag getUpdateTag() {
      return this.save(new CompoundTag());
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
      return !this.isExtending() && this.isSourcePiston() && this.movedState.getBlock() instanceof PistonBaseBlock ? (BlockState)((BlockState)((BlockState)Blocks.PISTON_HEAD.defaultBlockState().setValue(PistonHeadBlock.SHORT, this.progress > 0.25F)).setValue(PistonHeadBlock.TYPE, this.movedState.is(Blocks.STICKY_PISTON) ? PistonType.STICKY : PistonType.DEFAULT)).setValue(PistonHeadBlock.FACING, this.movedState.getValue(PistonBaseBlock.FACING)) : this.movedState;
   }

   private void moveCollidedEntities(float var1) {
      Direction var2 = this.getMovementDirection();
      double var3 = (double)(var1 - this.progress);
      VoxelShape var5 = this.getCollisionRelatedBlockState().getCollisionShape(this.level, this.getBlockPos());
      if (!var5.isEmpty()) {
         AABB var6 = this.moveByPositionAndProgress(var5.bounds());
         List var7 = this.level.getEntities((Entity)null, PistonMath.getMovementArea(var6, var2, var3).minmax(var6));
         if (!var7.isEmpty()) {
            List var8 = var5.toAabbs();
            boolean var9 = this.movedState.is(Blocks.SLIME_BLOCK);
            Iterator var10 = var7.iterator();

            while(true) {
               Entity var11;
               while(true) {
                  do {
                     if (!var10.hasNext()) {
                        return;
                     }

                     var11 = (Entity)var10.next();
                  } while(var11.getPistonPushReaction() == PushReaction.IGNORE);

                  if (!var9) {
                     break;
                  }

                  if (!(var11 instanceof ServerPlayer)) {
                     Vec3 var12 = var11.getDeltaMovement();
                     double var13 = var12.x;
                     double var15 = var12.y;
                     double var17 = var12.z;
                     switch(var2.getAxis()) {
                     case X:
                        var13 = (double)var2.getStepX();
                        break;
                     case Y:
                        var15 = (double)var2.getStepY();
                        break;
                     case Z:
                        var17 = (double)var2.getStepZ();
                     }

                     var11.setDeltaMovement(var13, var15, var17);
                     break;
                  }
               }

               double var19 = 0.0D;
               Iterator var14 = var8.iterator();

               while(var14.hasNext()) {
                  AABB var20 = (AABB)var14.next();
                  AABB var16 = PistonMath.getMovementArea(this.moveByPositionAndProgress(var20), var2, var3);
                  AABB var21 = var11.getBoundingBox();
                  if (var16.intersects(var21)) {
                     var19 = Math.max(var19, getMovement(var16, var2, var21));
                     if (var19 >= var3) {
                        break;
                     }
                  }
               }

               if (var19 > 0.0D) {
                  var19 = Math.min(var19, var3) + 0.01D;
                  moveEntityByPiston(var2, var11, var19, var2);
                  if (!this.extending && this.isSourcePiston) {
                     this.fixEntityWithinPistonBase(var11, var2, var3);
                  }
               }
            }
         }
      }
   }

   private static void moveEntityByPiston(Direction var0, Entity var1, double var2, Direction var4) {
      NOCLIP.set(var0);
      var1.move(MoverType.PISTON, new Vec3(var2 * (double)var4.getStepX(), var2 * (double)var4.getStepY(), var2 * (double)var4.getStepZ()));
      NOCLIP.set((Object)null);
   }

   private void moveStuckEntities(float var1) {
      if (this.isStickyForEntities()) {
         Direction var2 = this.getMovementDirection();
         if (var2.getAxis().isHorizontal()) {
            double var3 = this.movedState.getCollisionShape(this.level, this.worldPosition).max(Direction.Axis.Y);
            AABB var5 = this.moveByPositionAndProgress(new AABB(0.0D, var3, 0.0D, 1.0D, 1.5000000999999998D, 1.0D));
            double var6 = (double)(var1 - this.progress);
            List var8 = this.level.getEntities((Entity)null, var5, (var1x) -> {
               return matchesStickyCritera(var5, var1x);
            });
            Iterator var9 = var8.iterator();

            while(var9.hasNext()) {
               Entity var10 = (Entity)var9.next();
               moveEntityByPiston(var2, var10, var6, var2);
            }

         }
      }
   }

   private static boolean matchesStickyCritera(AABB var0, Entity var1) {
      return var1.getPistonPushReaction() == PushReaction.NORMAL && var1.isOnGround() && var1.getX() >= var0.minX && var1.getX() <= var0.maxX && var1.getZ() >= var0.minZ && var1.getZ() <= var0.maxZ;
   }

   private boolean isStickyForEntities() {
      return this.movedState.is(Blocks.HONEY_BLOCK);
   }

   public Direction getMovementDirection() {
      return this.extending ? this.direction : this.direction.getOpposite();
   }

   private static double getMovement(AABB var0, Direction var1, AABB var2) {
      switch(var1) {
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

   private AABB moveByPositionAndProgress(AABB var1) {
      double var2 = (double)this.getExtendedProgress(this.progress);
      return var1.move((double)this.worldPosition.getX() + var2 * (double)this.direction.getStepX(), (double)this.worldPosition.getY() + var2 * (double)this.direction.getStepY(), (double)this.worldPosition.getZ() + var2 * (double)this.direction.getStepZ());
   }

   private void fixEntityWithinPistonBase(Entity var1, Direction var2, double var3) {
      AABB var5 = var1.getBoundingBox();
      AABB var6 = Shapes.block().bounds().move(this.worldPosition);
      if (var5.intersects(var6)) {
         Direction var7 = var2.getOpposite();
         double var8 = getMovement(var6, var7, var5) + 0.01D;
         double var10 = getMovement(var6, var7, var5.intersect(var6)) + 0.01D;
         if (Math.abs(var8 - var10) < 0.01D) {
            var8 = Math.min(var8, var3) + 0.01D;
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
            this.level.neighborChanged(this.worldPosition, var1.getBlock(), this.worldPosition);
         }
      }

   }

   public void tick() {
      this.lastTicked = this.level.getGameTime();
      this.progressO = this.progress;
      if (this.progressO >= 1.0F) {
         if (this.level.isClientSide && this.deathTicks < 5) {
            ++this.deathTicks;
         } else {
            this.level.removeBlockEntity(this.worldPosition);
            this.setRemoved();
            if (this.movedState != null && this.level.getBlockState(this.worldPosition).is(Blocks.MOVING_PISTON)) {
               BlockState var2 = Block.updateFromNeighbourShapes(this.movedState, this.level, this.worldPosition);
               if (var2.isAir()) {
                  this.level.setBlock(this.worldPosition, this.movedState, 84);
                  Block.updateOrDestroy(this.movedState, var2, this.level, this.worldPosition, 3);
               } else {
                  if (var2.hasProperty(BlockStateProperties.WATERLOGGED) && (Boolean)var2.getValue(BlockStateProperties.WATERLOGGED)) {
                     var2 = (BlockState)var2.setValue(BlockStateProperties.WATERLOGGED, false);
                  }

                  this.level.setBlock(this.worldPosition, var2, 67);
                  this.level.neighborChanged(this.worldPosition, var2.getBlock(), this.worldPosition);
               }
            }

         }
      } else {
         float var1 = this.progress + 0.5F;
         this.moveCollidedEntities(var1);
         this.moveStuckEntities(var1);
         this.progress = var1;
         if (this.progress >= 1.0F) {
            this.progress = 1.0F;
         }

      }
   }

   public void load(BlockState var1, CompoundTag var2) {
      super.load(var1, var2);
      this.movedState = NbtUtils.readBlockState(var2.getCompound("blockState"));
      this.direction = Direction.from3DDataValue(var2.getInt("facing"));
      this.progress = var2.getFloat("progress");
      this.progressO = this.progress;
      this.extending = var2.getBoolean("extending");
      this.isSourcePiston = var2.getBoolean("source");
   }

   public CompoundTag save(CompoundTag var1) {
      super.save(var1);
      var1.put("blockState", NbtUtils.writeBlockState(this.movedState));
      var1.putInt("facing", this.direction.get3DDataValue());
      var1.putFloat("progress", this.progressO);
      var1.putBoolean("extending", this.extending);
      var1.putBoolean("source", this.isSourcePiston);
      return var1;
   }

   public VoxelShape getCollisionShape(BlockGetter var1, BlockPos var2) {
      VoxelShape var3;
      if (!this.extending && this.isSourcePiston) {
         var3 = ((BlockState)this.movedState.setValue(PistonBaseBlock.EXTENDED, true)).getCollisionShape(var1, var2);
      } else {
         var3 = Shapes.empty();
      }

      Direction var4 = (Direction)NOCLIP.get();
      if ((double)this.progress < 1.0D && var4 == this.getMovementDirection()) {
         return var3;
      } else {
         BlockState var5;
         if (this.isSourcePiston()) {
            var5 = (BlockState)((BlockState)Blocks.PISTON_HEAD.defaultBlockState().setValue(PistonHeadBlock.FACING, this.direction)).setValue(PistonHeadBlock.SHORT, this.extending != 1.0F - this.progress < 0.25F);
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

   public double getViewDistance() {
      return 68.0D;
   }
}
