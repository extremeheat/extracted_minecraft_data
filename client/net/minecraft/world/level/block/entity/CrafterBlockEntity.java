package net.minecraft.world.level.block.entity;

import com.google.common.annotations.VisibleForTesting;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.Objects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedItemContents;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.CrafterMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CrafterBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class CrafterBlockEntity extends RandomizableContainerBlockEntity implements CraftingContainer {
   public static final int CONTAINER_WIDTH = 3;
   public static final int CONTAINER_HEIGHT = 3;
   public static final int CONTAINER_SIZE = 9;
   public static final int SLOT_DISABLED = 1;
   public static final int SLOT_ENABLED = 0;
   public static final int DATA_TRIGGERED = 9;
   public static final int NUM_DATA = 10;
   private static final int DEFAULT_CRAFTING_TICKS_REMAINING = 0;
   private static final int DEFAULT_TRIGGERED = 0;
   private static final Component DEFAULT_NAME = Component.translatable("container.crafter");
   private NonNullList<ItemStack> items;
   private int craftingTicksRemaining;
   protected final ContainerData containerData;

   public CrafterBlockEntity(final BlockPos worldPosition, final BlockState blockState) {
      super(BlockEntityType.CRAFTER, worldPosition, blockState);
      this.items = NonNullList.<ItemStack>withSize(9, ItemStack.EMPTY);
      this.craftingTicksRemaining = 0;
      this.containerData = new ContainerData() {
         private final int[] slotStates;
         private int triggered;

         {
            Objects.requireNonNull(CrafterBlockEntity.this);
            this.slotStates = new int[9];
            this.triggered = 0;
         }

         public int get(final int dataId) {
            return dataId == 9 ? this.triggered : this.slotStates[dataId];
         }

         public void set(final int dataId, final int value) {
            if (dataId == 9) {
               this.triggered = value;
            } else {
               this.slotStates[dataId] = value;
            }

         }

         public int getCount() {
            return 10;
         }
      };
   }

   protected Component getDefaultName() {
      return DEFAULT_NAME;
   }

   protected AbstractContainerMenu createMenu(final int containerId, final Inventory inventory) {
      return new CrafterMenu(containerId, inventory, this, this.containerData);
   }

   public void setSlotState(final int slotId, final boolean enabled) {
      if (this.slotCanBeDisabled(slotId)) {
         this.containerData.set(slotId, enabled ? 0 : 1);
         this.setChanged();
      }
   }

   public boolean isSlotDisabled(final int slotId) {
      if (slotId >= 0 && slotId < 9) {
         return this.containerData.get(slotId) == 1;
      } else {
         return false;
      }
   }

   public boolean canPlaceItem(final int slot, final ItemStack itemStack) {
      if (this.containerData.get(slot) == 1) {
         return false;
      } else {
         ItemStack slotStack = this.items.get(slot);
         int currentStackSize = slotStack.getCount();
         if (currentStackSize >= slotStack.getMaxStackSize()) {
            return false;
         } else if (slotStack.isEmpty()) {
            return true;
         } else {
            return !this.smallerStackExist(currentStackSize, slotStack, slot);
         }
      }
   }

   private boolean smallerStackExist(final int baseSize, final ItemStack baseItem, final int baseSlot) {
      for(int i = baseSlot + 1; i < 9; ++i) {
         if (!this.isSlotDisabled(i)) {
            ItemStack slotStack = this.getItem(i);
            if (slotStack.isEmpty() || slotStack.getCount() < baseSize && ItemStack.isSameItemSameComponents(slotStack, baseItem)) {
               return true;
            }
         }
      }

      return false;
   }

   protected void loadAdditional(final ValueInput input) {
      super.loadAdditional(input);
      this.craftingTicksRemaining = input.getIntOr("crafting_ticks_remaining", 0);
      this.items = NonNullList.<ItemStack>withSize(this.getContainerSize(), ItemStack.EMPTY);
      if (!this.tryLoadLootTable(input)) {
         ContainerHelper.loadAllItems(input, this.items);
      }

      for(int i = 0; i < 9; ++i) {
         this.containerData.set(i, 0);
      }

      input.getIntArray("disabled_slots").ifPresent((disabledSlots) -> {
         for(int i : disabledSlots) {
            if (this.slotCanBeDisabled(i)) {
               this.containerData.set(i, 1);
            }
         }

      });
      this.containerData.set(9, input.getIntOr("triggered", 0));
   }

   protected void saveAdditional(final ValueOutput output) {
      super.saveAdditional(output);
      output.putInt("crafting_ticks_remaining", this.craftingTicksRemaining);
      if (!this.trySaveLootTable(output)) {
         ContainerHelper.saveAllItems(output, this.items);
      }

      this.addDisabledSlots(output);
      this.addTriggered(output);
   }

   public int getContainerSize() {
      return 9;
   }

   public boolean isEmpty() {
      for(ItemStack is : this.items) {
         if (!is.isEmpty()) {
            return false;
         }
      }

      return true;
   }

   public ItemStack getItem(final int slot) {
      return this.items.get(slot);
   }

   public void setItem(final int slot, final ItemStack itemStack) {
      if (this.isSlotDisabled(slot)) {
         this.setSlotState(slot, true);
      }

      super.setItem(slot, itemStack);
   }

   public boolean stillValid(final Player player) {
      return Container.stillValidBlockEntity(this, player);
   }

   public NonNullList<ItemStack> getItems() {
      return this.items;
   }

   protected void setItems(final NonNullList<ItemStack> items) {
      this.items = items;
   }

   public int getWidth() {
      return 3;
   }

   public int getHeight() {
      return 3;
   }

   public void fillStackedContents(final StackedItemContents contents) {
      for(ItemStack itemStack : this.items) {
         contents.accountSimpleStack(itemStack);
      }

   }

   private void addDisabledSlots(final ValueOutput output) {
      IntList disabledSlots = new IntArrayList();

      for(int i = 0; i < 9; ++i) {
         if (this.isSlotDisabled(i)) {
            disabledSlots.add(i);
         }
      }

      output.putIntArray("disabled_slots", disabledSlots.toIntArray());
   }

   private void addTriggered(final ValueOutput output) {
      output.putInt("triggered", this.containerData.get(9));
   }

   public void setTriggered(final boolean value) {
      this.containerData.set(9, value ? 1 : 0);
   }

   @VisibleForTesting
   public boolean isTriggered() {
      return this.containerData.get(9) == 1;
   }

   public static void serverTick(final Level level, final BlockPos blockPos, final BlockState blockState, final CrafterBlockEntity entity) {
      int craftingTicksRemaining = entity.craftingTicksRemaining - 1;
      if (craftingTicksRemaining >= 0) {
         entity.craftingTicksRemaining = craftingTicksRemaining;
         if (craftingTicksRemaining == 0) {
            level.setBlock(blockPos, (BlockState)blockState.setValue(CrafterBlock.CRAFTING, false), 3);
         }

      }
   }

   public void setCraftingTicksRemaining(final int maxCraftingTicks) {
      this.craftingTicksRemaining = maxCraftingTicks;
   }

   public int getRedstoneSignal() {
      int count = 0;

      for(int i = 0; i < this.getContainerSize(); ++i) {
         ItemStack itemStack = this.getItem(i);
         if (!itemStack.isEmpty() || this.isSlotDisabled(i)) {
            ++count;
         }
      }

      return count;
   }

   private boolean slotCanBeDisabled(final int slotId) {
      return slotId > -1 && slotId < 9 && ((ItemStack)this.items.get(slotId)).isEmpty();
   }
}
