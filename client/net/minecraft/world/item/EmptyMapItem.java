package net.minecraft.world.item;

import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class EmptyMapItem extends ComplexItem {
   public EmptyMapItem(Item.Properties var1) {
      super(var1);
   }

   public InteractionResultHolder<ItemStack> use(Level var1, Player var2, InteractionHand var3) {
      ItemStack var4 = MapItem.create(var1, Mth.floor(var2.x), Mth.floor(var2.z), (byte)0, true, false);
      ItemStack var5 = var2.getItemInHand(var3);
      if (!var2.abilities.instabuild) {
         var5.shrink(1);
      }

      if (var5.isEmpty()) {
         return new InteractionResultHolder(InteractionResult.SUCCESS, var4);
      } else {
         if (!var2.inventory.add(var4.copy())) {
            var2.drop(var4, false);
         }

         var2.awardStat(Stats.ITEM_USED.get(this));
         return new InteractionResultHolder(InteractionResult.SUCCESS, var5);
      }
   }
}
