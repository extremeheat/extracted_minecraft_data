package net.minecraft.world.inventory;

import javax.annotation.Nullable;
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

   public void fromTag(ListTag var1) {
      int var2;
      for(var2 = 0; var2 < this.getContainerSize(); ++var2) {
         this.setItem(var2, ItemStack.EMPTY);
      }

      for(var2 = 0; var2 < var1.size(); ++var2) {
         CompoundTag var3 = var1.getCompound(var2);
         int var4 = var3.getByte("Slot") & 255;
         if (var4 >= 0 && var4 < this.getContainerSize()) {
            this.setItem(var4, ItemStack.of(var3));
         }
      }

   }

   public ListTag createTag() {
      ListTag var1 = new ListTag();

      for(int var2 = 0; var2 < this.getContainerSize(); ++var2) {
         ItemStack var3 = this.getItem(var2);
         if (!var3.isEmpty()) {
            CompoundTag var4 = new CompoundTag();
            var4.putByte("Slot", (byte)var2);
            var3.save(var4);
            var1.add(var4);
         }
      }

      return var1;
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
