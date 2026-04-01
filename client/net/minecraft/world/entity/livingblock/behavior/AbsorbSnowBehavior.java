package net.minecraft.world.entity.livingblock.behavior;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.livingblock.LivingBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;

public class AbsorbSnowBehavior implements LivingBlockBehavior {
   private static final int REEVALUATION_TICKS = 20;
   private BlockPos lastEntityPos = new BlockPos(0, 0, 0);
   private int lastTriggeredTick;

   public AbsorbSnowBehavior() {
      super();
   }

   public boolean canStartUsing(final LivingBlock entity) {
      return this.lastTriggeredTick + 20 < entity.tickCount;
   }

   public boolean tick(final LivingBlock entity, final ServerLevel level, final int tickCount) {
      BlockPos entityPos = entity.blockPosition();
      this.lastTriggeredTick = tickCount;
      int posDiffX = entityPos.getX() - this.lastEntityPos.getX();
      int posDiffZ = entityPos.getZ() - this.lastEntityPos.getZ();
      if ((float)Math.abs(posDiffX) > 1.0F || (float)Math.abs(posDiffZ) > 1.0F) {
         BlockState entityState = entity.getBlockState();
         int entityHeight = (Integer)entityState.getValue(SnowLayerBlock.LAYERS);
         int maxSnowHeight = 8;
         if (entityHeight < maxSnowHeight) {
            BlockState snowLayerState = level.getBlockState(entityPos);
            BlockPos belowPos = entityPos.below();
            if (snowLayerState.hasProperty(SnowLayerBlock.LAYERS)) {
               int snowLayerHeight = (Integer)snowLayerState.getValue(SnowLayerBlock.LAYERS) - 1;
               if (snowLayerHeight <= 0) {
                  level.setBlock(entityPos, Blocks.AIR.defaultBlockState(), 2);
               } else {
                  level.setBlock(entityPos, (BlockState)snowLayerState.setValue(SnowLayerBlock.LAYERS, snowLayerHeight), 2);
               }

               this.AddSnowLayer(level, entity, entityPos, entityState, entityHeight);
            } else if (level.getBlockState(belowPos).is(Blocks.SNOW_BLOCK)) {
               BlockState snowDefaultState = Blocks.SNOW.defaultBlockState();
               level.setBlock(belowPos, (BlockState)snowDefaultState.setValue(SnowLayerBlock.LAYERS, 7), 2);
               this.AddSnowLayer(level, entity, entityPos, entityState, entityHeight);
            }
         }
      }

      return false;
   }

   private void AddSnowLayer(final ServerLevel level, final LivingBlock entity, final BlockPos entityPos, final BlockState entityState, final int entityHeight) {
      entity.setBlockState((BlockState)entityState.setValue(SnowLayerBlock.LAYERS, entityHeight + 1));
      level.playSound((Entity)null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.SNOW_STEP, SoundSource.BLOCKS, 0.7F, 1.0F);
      this.lastEntityPos = entityPos;
   }

   public static LivingBlockBehaviorType absorbSnow() {
      return LivingBlockBehaviorType.behaviorType(AbsorbSnowBehavior::new);
   }
}
