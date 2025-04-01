package net.minecraft.world.inventory;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;

public class PlayerHubInventory extends SimpleContainer {
   public PlayerHubInventory(int var1) {
      super(var1);
   }

   public void fromTag(ListTag var1, HolderLookup.Provider var2) {
      for(int var3 = 0; var3 < this.getContainerSize(); ++var3) {
         this.setItem(var3, ItemStack.EMPTY);
      }

      for(int var6 = 0; var6 < var1.size(); ++var6) {
         CompoundTag var4 = var1.getCompoundOrEmpty(var6);
         int var5 = var4.getByteOr("Slot", (byte)0) & 255;
         if (var5 >= 0 && var5 < this.getContainerSize()) {
            this.setItem(var5, (ItemStack)ItemStack.parse(var2, var4).orElse(ItemStack.EMPTY));
         }
      }

   }

   public ListTag createTag(HolderLookup.Provider var1) {
      ListTag var2 = new ListTag();

      for(int var3 = 0; var3 < this.getContainerSize(); ++var3) {
         ItemStack var4 = this.getItem(var3);
         if (!var4.isEmpty()) {
            CompoundTag var5 = new CompoundTag();
            var5.putByte("Slot", (byte)var3);
            var2.add(var4.save(var1, var5));
         }
      }

      return var2;
   }
}
