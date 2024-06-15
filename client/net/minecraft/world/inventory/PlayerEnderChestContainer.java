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

   @Override
   public void fromTag(ListTag var1, HolderLookup.Provider var2) {
      for (int var3 = 0; var3 < this.getContainerSize(); var3++) {
         this.setItem(var3, ItemStack.EMPTY);
      }

      for (int var6 = 0; var6 < var1.size(); var6++) {
         CompoundTag var4 = var1.getCompound(var6);
         int var5 = var4.getByte("Slot") & 255;
         if (var5 >= 0 && var5 < this.getContainerSize()) {
            this.setItem(var5, ItemStack.parse(var2, var4).orElse(ItemStack.EMPTY));
         }
      }
   }

   @Override
   public ListTag createTag(HolderLookup.Provider var1) {
      ListTag var2 = new ListTag();

      for (int var3 = 0; var3 < this.getContainerSize(); var3++) {
         ItemStack var4 = this.getItem(var3);
         if (!var4.isEmpty()) {
            CompoundTag var5 = new CompoundTag();
            var5.putByte("Slot", (byte)var3);
            var2.add(var4.save(var1, var5));
         }
      }

      return var2;
   }

   @Override
   public boolean stillValid(Player var1) {
      return this.activeChest != null && !this.activeChest.stillValid(var1) ? false : super.stillValid(var1);
   }

   @Override
   public void startOpen(Player var1) {
      if (this.activeChest != null) {
         this.activeChest.startOpen(var1);
      }

      super.startOpen(var1);
   }

   @Override
   public void stopOpen(Player var1) {
      if (this.activeChest != null) {
         this.activeChest.stopOpen(var1);
      }

      super.stopOpen(var1);
      this.activeChest = null;
   }
}
