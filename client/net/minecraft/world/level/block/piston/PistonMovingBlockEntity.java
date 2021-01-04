package net.minecraft.world.level.block.piston;

import java.util.Iterator;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
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
   private static final ThreadLocal<Direction> NOCLIP = new ThreadLocal<Direction>() {
      protected Direction initialValue() {
         return null;
      }

      // $FF: synthetic method
      protected Object initialValue() {
         return this.initialValue();
      }
   };
   private float progress;
   private float progressO;
   private long lastTicked;

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
      return !this.isExtending() && this.isSourcePiston() && this.movedState.getBlock() instanceof PistonBaseBlock ? (BlockState)((BlockState)Blocks.PISTON_HEAD.defaultBlockState().setValue(PistonHeadBlock.TYPE, this.movedState.getBlock() == Blocks.STICKY_PISTON ? PistonType.STICKY : PistonType.DEFAULT)).setValue(PistonHeadBlock.FACING, this.movedState.getValue(PistonBaseBlock.FACING)) : this.movedState;
   }

   private void moveCollidedEntities(float var1) {
      Direction var2 = this.getMovementDirection();
      double var3 = (double)(var1 - this.progress);
      VoxelShape var5 = this.getCollisionRelatedBlockState().getCollisionShape(this.level, this.getBlockPos());
      if (!var5.isEmpty()) {
         List var6 = var5.toAabbs();
         AABB var7 = this.moveByPositionAndProgress(this.getMinMaxPiecesAABB(var6));
         List var8 = this.level.getEntities((Entity)null, this.getMovementArea(var7, var2, var3).minmax(var7));
         if (!var8.isEmpty()) {
            boolean var9 = this.movedState.getBlock() == Blocks.SLIME_BLOCK;

            for(int var10 = 0; var10 < var8.size(); ++var10) {
               Entity var11 = (Entity)var8.get(var10);
               if (var11.getPistonPushReaction() != PushReaction.IGNORE) {
                  if (var9) {
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
                  }

                  double var19 = 0.0D;

                  for(int var14 = 0; var14 < var6.size(); ++var14) {
                     AABB var20 = this.getMovementArea(this.moveByPositionAndProgress((AABB)var6.get(var14)), var2, var3);
                     AABB var16 = var11.getBoundingBox();
                     if (var20.intersects(var16)) {
                        var19 = Math.max(var19, this.getMovement(var20, var2, var16));
                        if (var19 >= var3) {
                           break;
                        }
                     }
                  }

                  if (var19 > 0.0D) {
                     var19 = Math.min(var19, var3) + 0.01D;
                     NOCLIP.set(var2);
                     var11.move(MoverType.PISTON, new Vec3(var19 * (double)var2.getStepX(), var19 * (double)var2.getStepY(), var19 * (double)var2.getStepZ()));
                     NOCLIP.set((Object)null);
                     if (!this.extending && this.isSourcePiston) {
                        this.fixEntityWithinPistonBase(var11, var2, var3);
                     }
                  }
               }
            }

         }
      }
   }

   public Direction getMovementDirection() {
      return this.extending ? this.direction : this.direction.getOpposite();
   }

   private AABB getMinMaxPiecesAABB(List<AABB> var1) {
      double var2 = 0.0D;
      double var4 = 0.0D;
      double var6 = 0.0D;
      double var8 = 1.0D;
      double var10 = 1.0D;
      double var12 = 1.0D;

      AABB var15;
      for(Iterator var14 = var1.iterator(); var14.hasNext(); var12 = Math.max(var15.maxZ, var12)) {
         var15 = (AABB)var14.next();
         var2 = Math.min(var15.minX, var2);
         var4 = Math.min(var15.minY, var4);
         var6 = Math.min(var15.minZ, var6);
         var8 = Math.max(var15.maxX, var8);
         var10 = Math.max(var15.maxY, var10);
      }

      return new AABB(var2, var4, var6, var8, var10, var12);
   }

   private double getMovement(AABB var1, Direction var2, AABB var3) {
      switch(var2.getAxis()) {
      case X:
         return getDeltaX(var1, var2, var3);
      case Y:
      default:
         return getDeltaY(var1, var2, var3);
      case Z:
         return getDeltaZ(var1, var2, var3);
      }
   }

   private AABB moveByPositionAndProgress(AABB var1) {
      double var2 = (double)this.getExtendedProgress(this.progress);
      return var1.move((double)this.worldPosition.getX() + var2 * (double)this.direction.getStepX(), (double)this.worldPosition.getY() + var2 * (double)this.direction.getStepY(), (double)this.worldPosition.getZ() + var2 * (double)this.direction.getStepZ());
   }

   private AABB getMovementArea(AABB var1, Direction var2, double var3) {
      double var5 = var3 * (double)var2.getAxisDirection().getStep();
      double var7 = Math.min(var5, 0.0D);
      double var9 = Math.max(var5, 0.0D);
      switch(var2) {
      case WEST:
         return new AABB(var1.minX + var7, var1.minY, var1.minZ, var1.minX + var9, var1.maxY, var1.maxZ);
      case EAST:
         return new AABB(var1.maxX + var7, var1.minY, var1.minZ, var1.maxX + var9, var1.maxY, var1.maxZ);
      case DOWN:
         return new AABB(var1.minX, var1.minY + var7, var1.minZ, var1.maxX, var1.minY + var9, var1.maxZ);
      case UP:
      default:
         return new AABB(var1.minX, var1.maxY + var7, var1.minZ, var1.maxX, var1.maxY + var9, var1.maxZ);
      case NORTH:
         return new AABB(var1.minX, var1.minY, var1.minZ + var7, var1.maxX, var1.maxY, var1.minZ + var9);
      case SOUTH:
         return new AABB(var1.minX, var1.minY, var1.maxZ + var7, var1.maxX, var1.maxY, var1.maxZ + var9);
      }
   }

   private void fixEntityWithinPistonBase(Entity var1, Direction var2, double var3) {
      AABB var5 = var1.getBoundingBox();
      AABB var6 = Shapes.block().bounds().move(this.worldPosition);
      if (var5.intersects(var6)) {
         Direction var7 = var2.getOpposite();
         double var8 = this.getMovement(var6, var7, var5) + 0.01D;
         double var10 = this.getMovement(var6, var7, var5.intersect(var6)) + 0.01D;
         if (Math.abs(var8 - var10) < 0.01D) {
            var8 = Math.min(var8, var3) + 0.01D;
            NOCLIP.set(var2);
            var1.move(MoverType.PISTON, new Vec3(var8 * (double)var7.getStepX(), var8 * (double)var7.getStepY(), var8 * (double)var7.getStepZ()));
            NOCLIP.set((Object)null);
         }
      }

   }

   private static double getDeltaX(AABB var0, Direction var1, AABB var2) {
      return var1.getAxisDirection() == Direction.AxisDirection.POSITIVE ? var0.maxX - var2.minX : var2.maxX - var0.minX;
   }

   private static double getDeltaY(AABB var0, Direction var1, AABB var2) {
      return var1.getAxisDirection() == Direction.AxisDirection.POSITIVE ? var0.maxY - var2.minY : var2.maxY - var0.minY;
   }

   private static double getDeltaZ(AABB var0, Direction var1, AABB var2) {
      return var1.getAxisDirection() == Direction.AxisDirection.POSITIVE ? var0.maxZ - var2.minZ : var2.maxZ - var0.minZ;
   }

   public BlockState getMovedState() {
      return this.movedState;
   }

   public void finalTick() {
      if (this.progressO < 1.0F && this.level != null) {
         this.progress = 1.0F;
         this.progressO = this.progress;
         this.level.removeBlockEntity(this.worldPosition);
         this.setRemoved();
         if (this.level.getBlockState(this.worldPosition).getBlock() == Blocks.MOVING_PISTON) {
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
         this.level.removeBlockEntity(this.worldPosition);
         this.setRemoved();
         if (this.movedState != null && this.level.getBlockState(this.worldPosition).getBlock() == Blocks.MOVING_PISTON) {
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

      } else {
         float var1 = this.progress + 0.5F;
         this.moveCollidedEntities(var1);
         this.progress = var1;
         if (this.progress >= 1.0F) {
            this.progress = 1.0F;
         }

      }
   }

   public void load(CompoundTag var1) {
      super.load(var1);
      this.movedState = NbtUtils.readBlockState(var1.getCompound("blockState"));
      this.direction = Direction.from3DDataValue(var1.getInt("facing"));
      this.progress = var1.getFloat("progress");
      this.progressO = this.progress;
      this.extending = var1.getBoolean("extending");
      this.isSourcePiston = var1.getBoolean("source");
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
            var5 = (BlockState)((BlockState)Blocks.PISTON_HEAD.defaultBlockState().setValue(PistonHeadBlock.FACING, this.direction)).setValue(PistonHeadBlock.SHORT, this.extending != 1.0F - this.progress < 4.0F);
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
}
