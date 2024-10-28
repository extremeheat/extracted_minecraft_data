package net.minecraft.world.item;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.CandleCakeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.gameevent.GameEvent;

public class FlintAndSteelItem extends Item {
   public FlintAndSteelItem(Item.Properties var1) {
      super(var1);
   }

   public InteractionResult useOn(UseOnContext var1) {
      Player var2 = var1.getPlayer();
      Level var3 = var1.getLevel();
      BlockPos var4 = var1.getClickedPos();
      BlockState var5 = var3.getBlockState(var4);
      if (!CampfireBlock.canLight(var5) && !CandleBlock.canLight(var5) && !CandleCakeBlock.canLight(var5)) {
         BlockPos var6 = var4.relative(var1.getClickedFace());
         if (BaseFireBlock.canBePlacedAt(var3, var6, var1.getHorizontalDirection())) {
            var3.playSound(var2, var6, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, var3.getRandom().nextFloat() * 0.4F + 0.8F);
            BlockState var7 = BaseFireBlock.getState(var3, var6);
            var3.setBlock(var6, var7, 11);
            var3.gameEvent(var2, GameEvent.BLOCK_PLACE, var4);
            ItemStack var8 = var1.getItemInHand();
            if (var2 instanceof ServerPlayer) {
               CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayer)var2, var6, var8);
               var8.hurtAndBreak(1, var2, LivingEntity.getSlotForHand(var1.getHand()));
            }

            return InteractionResult.sidedSuccess(var3.isClientSide());
         } else {
            return InteractionResult.FAIL;
         }
      } else {
         var3.playSound(var2, var4, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, var3.getRandom().nextFloat() * 0.4F + 0.8F);
         var3.setBlock(var4, (BlockState)var5.setValue(BlockStateProperties.LIT, true), 11);
         var3.gameEvent(var2, GameEvent.BLOCK_CHANGE, var4);
         if (var2 != null) {
            var1.getItemInHand().hurtAndBreak(1, var2, LivingEntity.getSlotForHand(var1.getHand()));
         }

         return InteractionResult.sidedSuccess(var3.isClientSide());
      }
   }
}
