package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.phys.BlockHitResult;

public class PumpkinBlock extends Block {
   protected PumpkinBlock(final BlockBehaviour.Properties properties) {
      super(properties);
   }

   protected InteractionResult useItemOn(final ItemStack itemStack, final BlockState state, final Level level, final BlockPos pos, final Player player, final InteractionHand hand, final BlockHitResult hitResult) {
      if (!itemStack.is(Items.SHEARS)) {
         return super.useItemOn(itemStack, state, level, pos, player, hand, hitResult);
      } else if (level instanceof ServerLevel) {
         ServerLevel serverLevel = (ServerLevel)level;
         Direction clickedDirection = hitResult.getDirection();
         Direction direction = clickedDirection.getAxis() == Direction.Axis.Y ? player.getDirection().getOpposite() : clickedDirection;
         dropFromBlockInteractLootTable(serverLevel, BuiltInLootTables.CARVE_PUMPKIN, state, level.getBlockEntity(pos), itemStack, player, (ignored, pumpkinSeeds) -> {
            ItemEntity entity = new ItemEntity(level, (double)pos.getX() + 0.5 + (double)direction.getStepX() * 0.65, (double)pos.getY() + 0.1, (double)pos.getZ() + 0.5 + (double)direction.getStepZ() * 0.65, pumpkinSeeds);
            RandomSource random = level.getRandom();
            entity.setDeltaMovement(0.05 * (double)direction.getStepX() + random.nextDouble() * 0.02, 0.05, 0.05 * (double)direction.getStepZ() + random.nextDouble() * 0.02);
            level.addFreshEntity(entity);
         });
         level.playSound((Entity)null, (BlockPos)pos, SoundEvents.PUMPKIN_CARVE, SoundSource.BLOCKS, 1.0F, 1.0F);
         level.setBlock(pos, (BlockState)Blocks.CARVED_PUMPKIN.defaultBlockState().setValue(CarvedPumpkinBlock.FACING, direction), 11);
         itemStack.hurtAndBreak(1, player, (EquipmentSlot)hand.asEquipmentSlot());
         level.gameEvent(player, GameEvent.SHEAR, pos);
         player.awardStat(Stats.ITEM_USED.get(Items.SHEARS));
         return InteractionResult.SUCCESS;
      } else {
         return InteractionResult.SUCCESS;
      }
   }
}
