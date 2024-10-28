package net.minecraft.world.level.block.entity;

import com.google.common.annotations.VisibleForTesting;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.CrafterMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CrafterBlock;
import net.minecraft.world.level.block.state.BlockState;

public class CrafterBlockEntity extends RandomizableContainerBlockEntity implements CraftingContainer {
   public static final int CONTAINER_WIDTH = 3;
   public static final int CONTAINER_HEIGHT = 3;
   public static final int CONTAINER_SIZE = 9;
   public static final int SLOT_DISABLED = 1;
   public static final int SLOT_ENABLED = 0;
   public static final int DATA_TRIGGERED = 9;
   public static final int NUM_DATA = 10;
   private NonNullList<ItemStack> items;
   private int craftingTicksRemaining;
   protected final ContainerData containerData;

   public CrafterBlockEntity(BlockPos var1, BlockState var2) {
      super(BlockEntityType.CRAFTER, var1, var2);
      this.items = NonNullList.withSize(9, ItemStack.EMPTY);
      this.craftingTicksRemaining = 0;
      this.containerData = new ContainerData(this) {
         private final int[] slotStates = new int[9];
         private int triggered = 0;

         public int get(int var1) {
            return var1 == 9 ? this.triggered : this.slotStates[var1];
         }

         public void set(int var1, int var2) {
            if (var1 == 9) {
               this.triggered = var2;
            } else {
               this.slotStates[var1] = var2;
            }

         }

         public int getCount() {
            return 10;
         }
      };
   }

   protected Component getDefaultName() {
      return Component.translatable("container.crafter");
   }

   protected AbstractContainerMenu createMenu(int var1, Inventory var2) {
      return new CrafterMenu(var1, var2, this, this.containerData);
   }

   public void setSlotState(int var1, boolean var2) {
      if (this.slotCanBeDisabled(var1)) {
         this.containerData.set(var1, var2 ? 0 : 1);
         this.setChanged();
      }
   }

   public boolean isSlotDisabled(int var1) {
      if (var1 >= 0 && var1 < 9) {
         return this.containerData.get(var1) == 1;
      } else {
         return false;
      }
   }

   public boolean canPlaceItem(int var1, ItemStack var2) {
      if (this.containerData.get(var1) == 1) {
         return false;
      } else {
         ItemStack var3 = (ItemStack)this.items.get(var1);
         int var4 = var3.getCount();
         if (var4 >= var3.getMaxStackSize()) {
            return false;
         } else if (var3.isEmpty()) {
            return true;
         } else {
            return !this.smallerStackExist(var4, var3, var1);
         }
      }
   }

   private boolean smallerStackExist(int var1, ItemStack var2, int var3) {
      for(int var4 = var3 + 1; var4 < 9; ++var4) {
         if (!this.isSlotDisabled(var4)) {
            ItemStack var5 = this.getItem(var4);
            if (var5.isEmpty() || var5.getCount() < var1 && ItemStack.isSameItemSameComponents(var5, var2)) {
               return true;
            }
         }
      }

      return false;
   }

   protected void loadAdditional(CompoundTag var1, HolderLookup.Provider var2) {
      super.loadAdditional(var1, var2);
      this.craftingTicksRemaining = var1.getInt("crafting_ticks_remaining");
      this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
      if (!this.tryLoadLootTable(var1)) {
         ContainerHelper.loadAllItems(var1, this.items, var2);
      }

      int[] var3 = var1.getIntArray("disabled_slots");

      for(int var4 = 0; var4 < 9; ++var4) {
         this.containerData.set(var4, 0);
      }

      int[] var8 = var3;
      int var5 = var3.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         int var7 = var8[var6];
         if (this.slotCanBeDisabled(var7)) {
            this.containerData.set(var7, 1);
         }
      }

      this.containerData.set(9, var1.getInt("triggered"));
   }

   protected void saveAdditional(CompoundTag var1, HolderLookup.Provider var2) {
      super.saveAdditional(var1, var2);
      var1.putInt("crafting_ticks_remaining", this.craftingTicksRemaining);
      if (!this.trySaveLootTable(var1)) {
         ContainerHelper.saveAllItems(var1, this.items, var2);
      }

      this.addDisabledSlots(var1);
      this.addTriggered(var1);
   }

   public int getContainerSize() {
      return 9;
   }

   public boolean isEmpty() {
      Iterator var1 = this.items.iterator();

      ItemStack var2;
      do {
         if (!var1.hasNext()) {
            return true;
         }

         var2 = (ItemStack)var1.next();
      } while(var2.isEmpty());

      return false;
   }

   public ItemStack getItem(int var1) {
      return (ItemStack)this.items.get(var1);
   }

   public void setItem(int var1, ItemStack var2) {
      if (this.isSlotDisabled(var1)) {
         this.setSlotState(var1, true);
      }

      super.setItem(var1, var2);
   }

   public boolean stillValid(Player var1) {
      return Container.stillValidBlockEntity(this, var1);
   }

   public NonNullList<ItemStack> getItems() {
      return this.items;
   }

   protected void setItems(NonNullList<ItemStack> var1) {
      this.items = var1;
   }

   public int getWidth() {
      return 3;
   }

   public int getHeight() {
      return 3;
   }

   public void fillStackedContents(StackedContents var1) {
      Iterator var2 = this.items.iterator();

      while(var2.hasNext()) {
         ItemStack var3 = (ItemStack)var2.next();
         var1.accountSimpleStack(var3);
      }

   }

   private void addDisabledSlots(CompoundTag var1) {
      IntArrayList var2 = new IntArrayList();

      for(int var3 = 0; var3 < 9; ++var3) {
         if (this.isSlotDisabled(var3)) {
            var2.add(var3);
         }
      }

      var1.putIntArray("disabled_slots", (List)var2);
   }

   private void addTriggered(CompoundTag var1) {
      var1.putInt("triggered", this.containerData.get(9));
   }

   public void setTriggered(boolean var1) {
      this.containerData.set(9, var1 ? 1 : 0);
   }

   @VisibleForTesting
   public boolean isTriggered() {
      return this.containerData.get(9) == 1;
   }

   public static void serverTick(Level var0, BlockPos var1, BlockState var2, CrafterBlockEntity var3) {
      int var4 = var3.craftingTicksRemaining - 1;
      if (var4 >= 0) {
         var3.craftingTicksRemaining = var4;
         if (var4 == 0) {
            var0.setBlock(var1, (BlockState)var2.setValue(CrafterBlock.CRAFTING, false), 3);
         }

      }
   }

   public void setCraftingTicksRemaining(int var1) {
      this.craftingTicksRemaining = var1;
   }

   public int getRedstoneSignal() {
      int var1 = 0;

      for(int var2 = 0; var2 < this.getContainerSize(); ++var2) {
         ItemStack var3 = this.getItem(var2);
         if (!var3.isEmpty() || this.isSlotDisabled(var2)) {
            ++var1;
         }
      }

      return var1;
   }

   private boolean slotCanBeDisabled(int var1) {
      return var1 > -1 && var1 < 9 && ((ItemStack)this.items.get(var1)).isEmpty();
   }

   // $FF: synthetic method
   public List getItems() {
      return this.getItems();
   }
}
