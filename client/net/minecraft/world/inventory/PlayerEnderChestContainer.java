package net.minecraft.world.inventory;

import javax.annotation.Nullable;
import net.minecraft.world.ItemStackWithSlot;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.EnderChestBlockEntity;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

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

   public void fromSlots(ValueInput.TypedInputList<ItemStackWithSlot> var1) {
      for(int var2 = 0; var2 < this.getContainerSize(); ++var2) {
         this.setItem(var2, ItemStack.EMPTY);
      }

      for(ItemStackWithSlot var3 : var1) {
         if (var3.isValidInContainer(this.getContainerSize())) {
            this.setItem(var3.slot(), var3.stack());
         }
      }

   }

   public void storeAsSlots(ValueOutput.TypedOutputList<ItemStackWithSlot> var1) {
      for(int var2 = 0; var2 < this.getContainerSize(); ++var2) {
         ItemStack var3 = this.getItem(var2);
         if (!var3.isEmpty()) {
            var1.add(new ItemStackWithSlot(var2, var3));
         }
      }

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
