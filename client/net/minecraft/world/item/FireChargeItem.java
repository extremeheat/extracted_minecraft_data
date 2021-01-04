package net.minecraft.world.item;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.state.BlockState;

public class FireChargeItem extends Item {
   public FireChargeItem(Item.Properties var1) {
      super(var1);
   }

   public InteractionResult useOn(UseOnContext var1) {
      Level var2 = var1.getLevel();
      if (var2.isClientSide) {
         return InteractionResult.SUCCESS;
      } else {
         BlockPos var3 = var1.getClickedPos();
         BlockState var4 = var2.getBlockState(var3);
         if (var4.getBlock() == Blocks.CAMPFIRE) {
            if (!(Boolean)var4.getValue(CampfireBlock.LIT) && !(Boolean)var4.getValue(CampfireBlock.WATERLOGGED)) {
               this.playSound(var2, var3);
               var2.setBlockAndUpdate(var3, (BlockState)var4.setValue(CampfireBlock.LIT, true));
            }
         } else {
            var3 = var3.relative(var1.getClickedFace());
            if (var2.getBlockState(var3).isAir()) {
               this.playSound(var2, var3);
               var2.setBlockAndUpdate(var3, ((FireBlock)Blocks.FIRE).getStateForPlacement(var2, var3));
            }
         }

         var1.getItemInHand().shrink(1);
         return InteractionResult.SUCCESS;
      }
   }

   private void playSound(Level var1, BlockPos var2) {
      var1.playSound((Player)null, (BlockPos)var2, SoundEvents.FIRECHARGE_USE, SoundSource.BLOCKS, 1.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F);
   }
}
