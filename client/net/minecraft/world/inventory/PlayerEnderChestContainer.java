package net.minecraft.world.inventory;

import javax.annotation.Nullable;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.EnderChestBlockEntity;

public class PlayerEnderChestContainer extends SimpleContainer {
   @Nullable
   private EnderChestBlockEntity activeChest;

   public PlayerEnderChestContainer() {
      super(27);
   }

   public void setActiveChest(EnderChestBlockEntity var1) {
      this.activeChest = var1;
   }

   public boolean isActiveChest(EnderChestBlockEntity var1) {
      return this.activeChest == var1;
   }

   public void fromTag(ListTag var1, HolderLookup.Provider var2) {
      int var3;
      for(var3 = 0; var3 < this.getContainerSize(); ++var3) {
         this.setItem(var3, ItemStack.EMPTY);
      }

      for(var3 = 0; var3 < var1.size(); ++var3) {
         CompoundTag var4 = var1.getCompound(var3);
         int var5 = var4.getByte("Slot") & 255;
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

   public boolean stillValid(Player var1) {
      return this.activeChest != null && !this.activeChest.stillValid(var1) ? false : super.stillValid(var1);
   }

   public void startOpen(Player var1) {
      if (this.activeChest != null) {
         this.activeChest.startOpen(var1);
      }

      super.startOpen(var1);
   }

   public void stopOpen(Player var1) {
      if (this.activeChest != null) {
         this.activeChest.stopOpen(var1);
      }

      super.stopOpen(var1);
      this.activeChest = null;
   }
}
