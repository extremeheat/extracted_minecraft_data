package net.minecraft.world.item;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class EmptyMapItem extends Item {
   public EmptyMapItem(Item.Properties var1) {
      super(var1);
   }

   public InteractionResult use(Level var1, Player var2, InteractionHand var3) {
      ItemStack var4 = var2.getItemInHand(var3);
      if (var1 instanceof ServerLevel var5) {
         var4.consume(1, var2);
         var2.awardStat(Stats.ITEM_USED.get(this));
         var5.playSound((Entity)null, var2, SoundEvents.UI_CARTOGRAPHY_TABLE_TAKE_RESULT, var2.getSoundSource(), 1.0F, 1.0F);
         ItemStack var6 = MapItem.create(var5, var2.getBlockX(), var2.getBlockZ(), (byte)0, true, false);
         if (var4.isEmpty()) {
            return InteractionResult.SUCCESS.heldItemTransformedTo(var6);
         } else {
            if (!var2.getInventory().add(var6.copy())) {
               var2.drop(var6, false);
            }

            return InteractionResult.SUCCESS;
         }
      } else {
         return InteractionResult.SUCCESS;
      }
   }
}
