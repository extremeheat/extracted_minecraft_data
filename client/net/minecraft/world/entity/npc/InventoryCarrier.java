package net.minecraft.world.entity.npc;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public interface InventoryCarrier {
   String TAG_INVENTORY = "Inventory";

   SimpleContainer getInventory();

   static void pickUpItem(ServerLevel var0, Mob var1, InventoryCarrier var2, ItemEntity var3) {
      ItemStack var4 = var3.getItem();
      if (var1.wantsToPickUp(var0, var4)) {
         SimpleContainer var5 = var2.getInventory();
         boolean var6 = var5.canAddItem(var4);
         if (!var6) {
            return;
         }

         var1.onItemPickup(var3);
         int var7 = var4.getCount();
         ItemStack var8 = var5.addItem(var4);
         var1.take(var3, var7 - var8.getCount());
         if (var8.isEmpty()) {
            var3.discard();
         } else {
            var4.setCount(var8.getCount());
         }
      }

   }

   default void readInventoryFromTag(ValueInput var1) {
      var1.list("Inventory", ItemStack.CODEC).ifPresent((var1x) -> this.getInventory().fromItemList(var1x));
   }

   default void writeInventoryToTag(ValueOutput var1) {
      this.getInventory().storeAsItemList(var1.list("Inventory", ItemStack.CODEC));
   }
}
