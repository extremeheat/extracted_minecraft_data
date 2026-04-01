package net.minecraft.world.entity.livingblock.behavior;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.livingblock.LivingBlock;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class BlockMiningBehavior {
   private float lastBreakProgress = 0.0F;
   private int breakTime = 0;

   public BlockMiningBehavior() {
      super();
   }

   protected void startMining() {
      this.breakTime = 0;
      this.lastBreakProgress = 0.0F;
   }

   protected static boolean canMine(final LivingBlock entity, final BlockPos target) {
      return canMine(entity, entity.level().getBlockState(target));
   }

   protected static boolean canMine(final LivingBlock entity, final BlockState blockState) {
      return blockState.isAir() ? false : hasCorrectToolForDrops(entity, blockState);
   }

   protected static boolean canMineEfficiently(final LivingBlock entity, final BlockState blockState) {
      ItemStack itemStack = entity.getItemStack();
      Tool tool = (Tool)itemStack.get(DataComponents.TOOL);
      if (tool == null) {
         return !blockState.is(BlockTags.NEEDS_STONE_TOOL);
      } else {
         return tool.isCorrectForDrops(blockState);
      }
   }

   private static boolean hasCorrectToolForDrops(final LivingBlock block, final BlockState state) {
      return !state.requiresCorrectToolForDrops() || block.getItemStack().isCorrectToolForDrops(state);
   }

   private static float getDestroySpeed(final LivingBlock entity, final BlockState state) {
      float speed = entity.getItemStack().getDestroySpeed(state);
      if (entity.isEyeInFluid(FluidTags.WATER)) {
         speed *= 0.2F;
      }

      if (!entity.onGround()) {
         speed /= 5.0F;
      }

      return speed;
   }

   private static float getDestroyProgress(final LivingBlock entity, final BlockState state) {
      float destroySpeed = state.getDestroySpeed();
      if (destroySpeed == -1.0F) {
         return 0.0F;
      } else {
         int modifier = hasCorrectToolForDrops(entity, state) ? 30 : 100;
         return getDestroySpeed(entity, state) / destroySpeed / (float)modifier;
      }
   }

   private static int getDestroyStage(final float destroyProgress) {
      return destroyProgress > 0.0F ? (int)(destroyProgress * 10.0F) : -1;
   }

   protected boolean mineBlockTick(final LivingBlock entity, final ServerLevel level, final BlockPos blockPos) {
      BlockState blockState = level.getBlockState(blockPos);
      if (blockState.isAir()) {
         return false;
      } else {
         float progress = getDestroyProgress(entity, blockState) * (float)(++this.breakTime);
         if (progress != this.lastBreakProgress) {
            level.destroyBlockProgress(entity.getId(), blockPos, getDestroyStage(progress));
            this.lastBreakProgress = progress;
         }

         if (progress > 1.0F) {
            level.removeBlock(blockPos, false);
            level.levelEvent(2001, blockPos, Block.getId(blockState));
            Block.dropResources(blockState, level, blockPos, (BlockEntity)null, entity, entity.getItemStack());
            entity.hurtServer(level, entity.damageSources().generic(), 0.1F);
            this.breakTime = 0;
            this.lastBreakProgress = -1.0F;
            return false;
         } else {
            return true;
         }
      }
   }
}
