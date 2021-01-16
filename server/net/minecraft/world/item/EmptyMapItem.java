package net.minecraft.world.item;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class EmptyMapItem extends ComplexItem {
   public EmptyMapItem(Item.Properties var1) {
      super(var1);
   }

   public InteractionResultHolder<ItemStack> use(Level var1, Player var2, InteractionHand var3) {
      ItemStack var4 = MapItem.create(var1, Mth.floor(var2.getX()), Mth.floor(var2.getZ()), (byte)0, true, false);
      ItemStack var5 = var2.getItemInHand(var3);
      if (!var2.abilities.instabuild) {
         var5.shrink(1);
      }

      var2.awardStat(Stats.ITEM_USED.get(this));
      var2.playSound(SoundEvents.UI_CARTOGRAPHY_TABLE_TAKE_RESULT, 1.0F, 1.0F);
      if (var5.isEmpty()) {
         return InteractionResultHolder.sidedSuccess(var4, var1.isClientSide());
      } else {
         if (!var2.inventory.add(var4.copy())) {
            var2.drop(var4, false);
         }

         return InteractionResultHolder.sidedSuccess(var5, var1.isClientSide());
      }
   }
}
