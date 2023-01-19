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

   @Override
   public void fromTag(ListTag var1) {
      for(int var2 = 0; var2 < this.getContainerSize(); ++var2) {
         this.setItem(var2, ItemStack.EMPTY);
      }

      for(int var5 = 0; var5 < var1.size(); ++var5) {
         CompoundTag var3 = var1.getCompound(var5);
         int var4 = var3.getByte("Slot") & 255;
         if (var4 >= 0 && var4 < this.getContainerSize()) {
            this.setItem(var4, ItemStack.of(var3));
         }
      }
   }

   @Override
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
