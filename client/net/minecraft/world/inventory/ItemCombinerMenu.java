package net.minecraft.world.inventory;

import javax.annotation.Nullable;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public abstract class ItemCombinerMenu extends AbstractContainerMenu {
   private static final int INVENTORY_SLOTS_PER_ROW = 9;
   private static final int INVENTORY_ROWS = 3;
   private static final int INPUT_SLOT_START = 0;
   protected final ContainerLevelAccess access;
   protected final Player player;
   protected final Container inputSlots;
   protected final ResultContainer resultSlots = new ResultContainer();
   private final int resultSlotIndex;

   protected boolean mayPickup(Player var1, boolean var2) {
      return true;
   }

   protected abstract void onTake(Player var1, ItemStack var2);

   protected abstract boolean isValidBlock(BlockState var1);

   public ItemCombinerMenu(@Nullable MenuType<?> var1, int var2, Inventory var3, ContainerLevelAccess var4, ItemCombinerMenuSlotDefinition var5) {
      super(var1, var2);
      this.access = var4;
      this.player = var3.player;
      this.inputSlots = this.createContainer(var5.getNumOfInputSlots());
      this.resultSlotIndex = var5.getResultSlotIndex();
      this.createInputSlots(var5);
      this.createResultSlot(var5);
      this.addStandardInventorySlots(var3, 8, 84);
   }

   private void createInputSlots(ItemCombinerMenuSlotDefinition var1) {
      for (final ItemCombinerMenuSlotDefinition.SlotDefinition var3 : var1.getSlots()) {
         this.addSlot(new Slot(this.inputSlots, var3.slotIndex(), var3.x(), var3.y()) {
            @Override
            public boolean mayPlace(ItemStack var1) {
               return var3.mayPlace().test(var1);
            }
         });
      }
   }

   private void createResultSlot(ItemCombinerMenuSlotDefinition var1) {
      this.addSlot(new Slot(this.resultSlots, var1.getResultSlot().slotIndex(), var1.getResultSlot().x(), var1.getResultSlot().y()) {
         @Override
         public boolean mayPlace(ItemStack var1) {
            return false;
         }

         @Override
         public boolean mayPickup(Player var1) {
            return ItemCombinerMenu.this.mayPickup(var1, this.hasItem());
         }

         @Override
         public void onTake(Player var1, ItemStack var2) {
            ItemCombinerMenu.this.onTake(var1, var2);
         }
      });
   }

   public abstract void createResult();

   private SimpleContainer createContainer(int var1) {
      return new SimpleContainer(var1) {
         @Override
         public void setChanged() {
            super.setChanged();
            ItemCombinerMenu.this.slotsChanged(this);
         }
      };
   }

   @Override
   public void slotsChanged(Container var1) {
      super.slotsChanged(var1);
      if (var1 == this.inputSlots) {
         this.createResult();
      }
   }

   @Override
   public void removed(Player var1) {
      super.removed(var1);
      this.access.execute((var2, var3) -> this.clearContainer(var1, this.inputSlots));
   }

   @Override
   public boolean stillValid(Player var1) {
      return this.access.evaluate((var2, var3) -> !this.isValidBlock(var2.getBlockState(var3)) ? false : var1.canInteractWithBlock(var3, 4.0), true);
   }

   @Override
   public ItemStack quickMoveStack(Player var1, int var2) {
      ItemStack var3 = ItemStack.EMPTY;
      Slot var4 = this.slots.get(var2);
      if (var4 != null && var4.hasItem()) {
         ItemStack var5 = var4.getItem();
         var3 = var5.copy();
         int var6 = this.getInventorySlotStart();
         int var7 = this.getUseRowEnd();
         if (var2 == this.getResultSlot()) {
            if (!this.moveItemStackTo(var5, var6, var7, true)) {
               return ItemStack.EMPTY;
            }

            var4.onQuickCraft(var5, var3);
         } else if (var2 >= 0 && var2 < this.getResultSlot()) {
            if (!this.moveItemStackTo(var5, var6, var7, false)) {
               return ItemStack.EMPTY;
            }
         } else if (this.canMoveIntoInputSlots(var5) && var2 >= this.getInventorySlotStart() && var2 < this.getUseRowEnd()) {
            if (!this.moveItemStackTo(var5, 0, this.getResultSlot(), false)) {
               return ItemStack.EMPTY;
            }
         } else if (var2 >= this.getInventorySlotStart() && var2 < this.getInventorySlotEnd()) {
            if (!this.moveItemStackTo(var5, this.getUseRowStart(), this.getUseRowEnd(), false)) {
               return ItemStack.EMPTY;
            }
         } else if (var2 >= this.getUseRowStart()
            && var2 < this.getUseRowEnd()
            && !this.moveItemStackTo(var5, this.getInventorySlotStart(), this.getInventorySlotEnd(), false)) {
            return ItemStack.EMPTY;
         }

         if (var5.isEmpty()) {
            var4.setByPlayer(ItemStack.EMPTY);
         } else {
            var4.setChanged();
         }

         if (var5.getCount() == var3.getCount()) {
            return ItemStack.EMPTY;
         }

         var4.onTake(var1, var5);
      }

      return var3;
   }

   protected boolean canMoveIntoInputSlots(ItemStack var1) {
      return true;
   }

   public int getResultSlot() {
      return this.resultSlotIndex;
   }

   private int getInventorySlotStart() {
      return this.getResultSlot() + 1;
   }

   private int getInventorySlotEnd() {
      return this.getInventorySlotStart() + 27;
   }

   private int getUseRowStart() {
      return this.getInventorySlotEnd();
   }

   private int getUseRowEnd() {
      return this.getUseRowStart() + 9;
   }
}
