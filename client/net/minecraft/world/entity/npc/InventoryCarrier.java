package net.minecraft.world.entity.npc;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;

public interface InventoryCarrier {
   SimpleContainer getInventory();

   static void pickUpItem(Mob var0, InventoryCarrier var1, ItemEntity var2) {
      ItemStack var3 = var2.getItem();
      if (var0.wantsToPickUp(var3)) {
         SimpleContainer var4 = var1.getInventory();
         boolean var5 = var4.canAddItem(var3);
         if (!var5) {
            return;
         }

         var0.onItemPickup(var2);
         int var6 = var3.getCount();
         ItemStack var7 = var4.addItem(var3);
         var0.take(var2, var6 - var7.getCount());
         if (var7.isEmpty()) {
            var2.discard();
         } else {
            var3.setCount(var7.getCount());
         }
      }

   }
}
