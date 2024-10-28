package net.minecraft.world.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class ItemUtils {
   public ItemUtils() {
      super();
   }

   public static InteractionResultHolder<ItemStack> startUsingInstantly(Level var0, Player var1, InteractionHand var2) {
      var1.startUsingItem(var2);
      return InteractionResultHolder.consume(var1.getItemInHand(var2));
   }

   public static ItemStack createFilledResult(ItemStack var0, Player var1, ItemStack var2, boolean var3) {
      boolean var4 = var1.hasInfiniteMaterials();
      if (var3 && var4) {
         if (!var1.getInventory().contains(var2)) {
            var1.getInventory().add(var2);
         }

         return var0;
      } else {
         var0.consume(1, var1);
         if (var0.isEmpty()) {
            return var2;
         } else {
            if (!var1.getInventory().add(var2)) {
               var1.drop(var2, false);
            }

            return var0;
         }
      }
   }

   public static ItemStack createFilledResult(ItemStack var0, Player var1, ItemStack var2) {
      return createFilledResult(var0, var1, var2, true);
   }

   public static void onContainerDestroyed(ItemEntity var0, Iterable<ItemStack> var1) {
      Level var2 = var0.level();
      if (!var2.isClientSide) {
         var1.forEach((var2x) -> {
            var2.addFreshEntity(new ItemEntity(var2, var0.getX(), var0.getY(), var0.getZ(), var2x));
         });
      }
   }
}
