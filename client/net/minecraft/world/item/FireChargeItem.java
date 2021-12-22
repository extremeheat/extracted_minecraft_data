package net.minecraft.world.item;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
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

public class FireChargeItem extends Item {
   public FireChargeItem(Item.Properties var1) {
      super(var1);
   }

   public InteractionResult useOn(UseOnContext var1) {
      Level var2 = var1.getLevel();
      BlockPos var3 = var1.getClickedPos();
      BlockState var4 = var2.getBlockState(var3);
      boolean var5 = false;
      if (!CampfireBlock.canLight(var4) && !CandleBlock.canLight(var4) && !CandleCakeBlock.canLight(var4)) {
         var3 = var3.relative(var1.getClickedFace());
         if (BaseFireBlock.canBePlacedAt(var2, var3, var1.getHorizontalDirection())) {
            this.playSound(var2, var3);
            var2.setBlockAndUpdate(var3, BaseFireBlock.getState(var2, var3));
            var2.gameEvent(var1.getPlayer(), GameEvent.BLOCK_PLACE, var3);
            var5 = true;
         }
      } else {
         this.playSound(var2, var3);
         var2.setBlockAndUpdate(var3, (BlockState)var4.setValue(BlockStateProperties.LIT, true));
         var2.gameEvent(var1.getPlayer(), GameEvent.BLOCK_PLACE, var3);
         var5 = true;
      }

      if (var5) {
         var1.getItemInHand().shrink(1);
         return InteractionResult.sidedSuccess(var2.isClientSide);
      } else {
         return InteractionResult.FAIL;
      }
   }

   private void playSound(Level var1, BlockPos var2) {
      Random var3 = var1.getRandom();
      var1.playSound((Player)null, (BlockPos)var2, SoundEvents.FIRECHARGE_USE, SoundSource.BLOCKS, 1.0F, (var3.nextFloat() - var3.nextFloat()) * 0.2F + 1.0F);
   }
}
