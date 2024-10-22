package net.minecraft.world.entity.npc;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;

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

   default void readInventoryFromTag(CompoundTag var1, HolderLookup.Provider var2) {
      if (var1.contains("Inventory", 9)) {
         this.getInventory().fromTag(var1.getList("Inventory", 10), var2);
      }
   }

   default void writeInventoryToTag(CompoundTag var1, HolderLookup.Provider var2) {
      var1.put("Inventory", this.getInventory().createTag(var2));
   }
}
