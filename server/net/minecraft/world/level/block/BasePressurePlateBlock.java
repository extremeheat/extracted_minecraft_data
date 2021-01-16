package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class BasePressurePlateBlock extends Block {
   protected static final VoxelShape PRESSED_AABB = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 0.5D, 15.0D);
   protected static final VoxelShape AABB = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 1.0D, 15.0D);
   protected static final AABB TOUCH_AABB = new AABB(0.125D, 0.0D, 0.125D, 0.875D, 0.25D, 0.875D);

   protected BasePressurePlateBlock(BlockBehaviour.Properties var1) {
      super(var1);
   }

   public VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return this.getSignalForState(var1) > 0 ? PRESSED_AABB : AABB;
   }

   protected int getPressedTime() {
      return 20;
   }

   public boolean isPossibleToRespawnInThis() {
      return true;
   }

   public BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
      return var2 == Direction.DOWN && !var1.canSurvive(var4, var5) ? Blocks.AIR.defaultBlockState() : super.updateShape(var1, var2, var3, var4, var5, var6);
   }

   public boolean canSurvive(BlockState var1, LevelReader var2, BlockPos var3) {
      BlockPos var4 = var3.below();
      return canSupportRigidBlock(var2, var4) || canSupportCenter(var2, var4, Direction.UP);
   }

   public void tick(BlockState var1, ServerLevel var2, BlockPos var3, Random var4) {
      int var5 = this.getSignalForState(var1);
      if (var5 > 0) {
         this.checkPressed(var2, var3, var1, var5);
      }

   }

   public void entityInside(BlockState var1, Level var2, BlockPos var3, Entity var4) {
      if (!var2.isClientSide) {
         int var5 = this.getSignalForState(var1);
         if (var5 == 0) {
            this.checkPressed(var2, var3, var1, var5);
         }

      }
   }

   protected void checkPressed(Level var1, BlockPos var2, BlockState var3, int var4) {
      int var5 = this.getSignalStrength(var1, var2);
      boolean var6 = var4 > 0;
      boolean var7 = var5 > 0;
      if (var4 != var5) {
         BlockState var8 = this.setSignalForState(var3, var5);
         var1.setBlock(var2, var8, 2);
         this.updateNeighbours(var1, var2);
         var1.setBlocksDirty(var2, var3, var8);
      }

      if (!var7 && var6) {
         this.playOffSound(var1, var2);
      } else if (var7 && !var6) {
         this.playOnSound(var1, var2);
      }

      if (var7) {
         var1.getBlockTicks().scheduleTick(new BlockPos(var2), this, this.getPressedTime());
      }

   }

   protected abstract void playOnSound(LevelAccessor var1, BlockPos var2);

   protected abstract void playOffSound(LevelAccessor var1, BlockPos var2);

   public void onRemove(BlockState var1, Level var2, BlockPos var3, BlockState var4, boolean var5) {
      if (!var5 && !var1.is(var4.getBlock())) {
         if (this.getSignalForState(var1) > 0) {
            this.updateNeighbours(var2, var3);
         }

         super.onRemove(var1, var2, var3, var4, var5);
      }
   }

   protected void updateNeighbours(Level var1, BlockPos var2) {
      var1.updateNeighborsAt(var2, this);
      var1.updateNeighborsAt(var2.below(), this);
   }

   public int getSignal(BlockState var1, BlockGetter var2, BlockPos var3, Direction var4) {
      return this.getSignalForState(var1);
   }

   public int getDirectSignal(BlockState var1, BlockGetter var2, BlockPos var3, Direction var4) {
      return var4 == Direction.UP ? this.getSignalForState(var1) : 0;
   }

   public boolean isSignalSource(BlockState var1) {
      return true;
   }

   public PushReaction getPistonPushReaction(BlockState var1) {
      return PushReaction.DESTROY;
   }

   protected abstract int getSignalStrength(Level var1, BlockPos var2);

   protected abstract int getSignalForState(BlockState var1);

   protected abstract BlockState setSignalForState(BlockState var1, int var2);
}
