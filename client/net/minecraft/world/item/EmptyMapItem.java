package net.minecraft.world.item;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class EmptyMapItem extends ComplexItem {
   public EmptyMapItem(Item.Properties var1) {
      super(var1);
   }

   @Override
   public InteractionResult use(Level var1, Player var2, InteractionHand var3) {
      ItemStack var4 = var2.getItemInHand(var3);
      if (var1.isClientSide) {
         return InteractionResult.SUCCESS;
      } else {
         var4.consume(1, var2);
         var2.awardStat(Stats.ITEM_USED.get(this));
         var2.level().playSound(null, var2, SoundEvents.UI_CARTOGRAPHY_TABLE_TAKE_RESULT, var2.getSoundSource(), 1.0F, 1.0F);
         ItemStack var5 = MapItem.create(var1, var2.getBlockX(), var2.getBlockZ(), (byte)0, true, false);
         if (var4.isEmpty()) {
            return InteractionResult.SUCCESS.heldItemTransformedTo(var5);
         } else {
            if (!var2.getInventory().add(var5.copy())) {
               var2.drop(var5, false);
            }

            return InteractionResult.SUCCESS;
         }
      }
   }
}
