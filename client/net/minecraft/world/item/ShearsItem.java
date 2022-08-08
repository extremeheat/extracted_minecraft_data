package net.minecraft.world.item;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.GrowingPlantHeadBlock;
import net.minecraft.world.level.block.state.BlockState;

public class ShearsItem extends Item {
   public ShearsItem(Item.Properties var1) {
      super(var1);
   }

   public boolean mineBlock(ItemStack var1, Level var2, BlockState var3, BlockPos var4, LivingEntity var5) {
      if (!var2.isClientSide && !var3.is(BlockTags.FIRE)) {
         var1.hurtAndBreak(1, var5, (var0) -> {
            var0.broadcastBreakEvent(EquipmentSlot.MAINHAND);
         });
      }

      return !var3.is(BlockTags.LEAVES) && !var3.is(Blocks.COBWEB) && !var3.is(Blocks.GRASS) && !var3.is(Blocks.FERN) && !var3.is(Blocks.DEAD_BUSH) && !var3.is(Blocks.HANGING_ROOTS) && !var3.is(Blocks.VINE) && !var3.is(Blocks.TRIPWIRE) && !var3.is(BlockTags.WOOL) ? super.mineBlock(var1, var2, var3, var4, var5) : true;
   }

   public boolean isCorrectToolForDrops(BlockState var1) {
      return var1.is(Blocks.COBWEB) || var1.is(Blocks.REDSTONE_WIRE) || var1.is(Blocks.TRIPWIRE);
   }

   public float getDestroySpeed(ItemStack var1, BlockState var2) {
      if (!var2.is(Blocks.COBWEB) && !var2.is(BlockTags.LEAVES)) {
         if (var2.is(BlockTags.WOOL)) {
            return 5.0F;
         } else {
            return !var2.is(Blocks.VINE) && !var2.is(Blocks.GLOW_LICHEN) ? super.getDestroySpeed(var1, var2) : 2.0F;
         }
      } else {
         return 15.0F;
      }
   }

   public InteractionResult useOn(UseOnContext var1) {
      Level var2 = var1.getLevel();
      BlockPos var3 = var1.getClickedPos();
      BlockState var4 = var2.getBlockState(var3);
      Block var5 = var4.getBlock();
      if (var5 instanceof GrowingPlantHeadBlock var6) {
         if (!var6.isMaxAge(var4)) {
            Player var7 = var1.getPlayer();
            ItemStack var8 = var1.getItemInHand();
            if (var7 instanceof ServerPlayer) {
               CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger((ServerPlayer)var7, var3, var8);
            }

            var2.playSound(var7, var3, SoundEvents.GROWING_PLANT_CROP, SoundSource.BLOCKS, 1.0F, 1.0F);
            var2.setBlockAndUpdate(var3, var6.getMaxAgeState(var4));
            if (var7 != null) {
               var8.hurtAndBreak(1, var7, (var1x) -> {
                  var1x.broadcastBreakEvent(var1.getHand());
               });
            }

            return InteractionResult.sidedSuccess(var2.isClientSide);
         }
      }

      return super.useOn(var1);
   }
}
