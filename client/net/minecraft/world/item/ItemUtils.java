package net.minecraft.world.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class ItemUtils {
   public static InteractionResultHolder<ItemStack> useDrink(Level var0, Player var1, InteractionHand var2) {
      var1.startUsingItem(var2);
      return InteractionResultHolder.consume(var1.getItemInHand(var2));
   }

   public static ItemStack createFilledResult(ItemStack var0, Player var1, ItemStack var2, boolean var3) {
      boolean var4 = var1.abilities.instabuild;
      if (var3 && var4) {
         if (!var1.inventory.contains(var2)) {
            var1.inventory.add(var2);
         }

         return var0;
      } else {
         if (!var4) {
            var0.shrink(1);
         }

         if (var0.isEmpty()) {
            return var2;
         } else {
            if (!var1.inventory.add(var2)) {
               var1.drop(var2, false);
            }

            return var0;
         }
      }
   }

   public static ItemStack createFilledResult(ItemStack var0, Player var1, ItemStack var2) {
      return createFilledResult(var0, var1, var2, true);
   }
}
