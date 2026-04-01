package net.minecraft.world.entity.livingblock.behavior;

import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.livingblock.LivingBlock;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.CandleCakeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.gameevent.GameEvent;

public class RandomIgnitionBehavior implements LivingBlockBehavior {
   private static final int REEVALUATION_TICKS = 10;
   private final int lastTriggeredTick = 0;

   public RandomIgnitionBehavior() {
      super();
   }

   public boolean canStartUsing(final LivingBlock entity) {
      return 10 < entity.tickCount;
   }

   public boolean tick(final LivingBlock entity, final ServerLevel level, final int tickCount) {
      if (level.getRandom().nextFloat() < 0.99F) {
         return false;
      } else {
         BlockPos pos = entity.blockPosition();
         BlockState state = level.getBlockState(pos);
         if (!CampfireBlock.canLight(state) && !CandleBlock.canLight(state) && !CandleCakeBlock.canLight(state)) {
            if (BaseFireBlock.canBePlacedAt(level, pos, entity.getDirection())) {
               level.playSound(entity, pos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, level.getRandom().nextFloat() * 0.4F + 0.8F);
               BlockState fireState = BaseFireBlock.getState(level, pos);
               level.setBlock(pos, fireState, 11);
               level.gameEvent(entity, GameEvent.BLOCK_PLACE, pos);
               return true;
            } else {
               return false;
            }
         } else {
            level.playSound(entity, pos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, level.getRandom().nextFloat() * 0.4F + 0.8F);
            level.setBlock(pos, (BlockState)state.setValue(BlockStateProperties.LIT, true), 11);
            level.gameEvent(entity, GameEvent.BLOCK_CHANGE, pos);
            return true;
         }
      }
   }

   public static LivingBlockBehaviorType randomIgnition() {
      return LivingBlockBehaviorType.behaviorType((Supplier)(() -> new RandomIgnitionBehavior()));
   }
}
