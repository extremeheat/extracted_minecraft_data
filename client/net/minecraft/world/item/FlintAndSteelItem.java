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
      return (InteractionResult)(ignite(var1) ? InteractionResult.SUCCESS : InteractionResult.FAIL);
   }

   public static boolean ignite(UseOnContext var0) {
      Player var1 = var0.getPlayer();
      Level var2 = var0.getLevel();
      BlockPos var3 = var0.getClickedPos();
      BlockState var4 = var2.getBlockState(var3);
      if (!CampfireBlock.canLight(var4) && !CandleBlock.canLight(var4) && !CandleCakeBlock.canLight(var4)) {
         BlockPos var5 = var3.relative(var0.getClickedFace());
         if (BaseFireBlock.canBePlacedAt(var2, var5, var0.getHorizontalDirection())) {
            var2.playSound(var1, (BlockPos)var5, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, var2.getRandom().nextFloat() * 0.4F + 0.8F);
            BlockState var6 = BaseFireBlock.getState(var2, var5);
            var2.setBlock(var5, var6, 11);
            var2.gameEvent(var1, GameEvent.BLOCK_PLACE, var3);
            ItemStack var7 = var0.getItemInHand();
            if (var1 instanceof ServerPlayer) {
               CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayer)var1, var5, var7);
               var7.hurtAndBreak(1, var1, LivingEntity.getSlotForHand(var0.getHand()));
            }

            return true;
         } else {
            return false;
         }
      } else {
         var2.playSound(var1, (BlockPos)var3, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, var2.getRandom().nextFloat() * 0.4F + 0.8F);
         var2.setBlock(var3, (BlockState)var4.setValue(BlockStateProperties.LIT, true), 11);
         var2.gameEvent(var1, GameEvent.BLOCK_CHANGE, var3);
         if (var1 != null && !var0.getItemInHand().isEmpty()) {
            var0.getItemInHand().hurtAndBreak(1, var1, LivingEntity.getSlotForHand(var0.getHand()));
         }

         return true;
      }
   }
}
