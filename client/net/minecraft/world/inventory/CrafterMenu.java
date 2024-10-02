package net.minecraft.world.inventory;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.level.block.CrafterBlock;

public class CrafterMenu extends AbstractContainerMenu implements ContainerListener {
   protected static final int SLOT_COUNT = 9;
   private static final int INV_SLOT_START = 9;
   private static final int INV_SLOT_END = 36;
   private static final int USE_ROW_SLOT_START = 36;
   private static final int USE_ROW_SLOT_END = 45;
   private final ResultContainer resultContainer = new ResultContainer();
   private final ContainerData containerData;
   private final Player player;
   private final CraftingContainer container;

   public CrafterMenu(int var1, Inventory var2) {
      super(MenuType.CRAFTER_3x3, var1);
      this.player = var2.player;
      this.containerData = new SimpleContainerData(10);
      this.container = new TransientCraftingContainer(this, 3, 3);
      this.addSlots(var2);
   }

   public CrafterMenu(int var1, Inventory var2, CraftingContainer var3, ContainerData var4) {
      super(MenuType.CRAFTER_3x3, var1);
      this.player = var2.player;
      this.containerData = var4;
      this.container = var3;
      checkContainerSize(var3, 9);
      var3.startOpen(var2.player);
      this.addSlots(var2);
      this.addSlotListener(this);
   }

   private void addSlots(Inventory var1) {
      for (int var2 = 0; var2 < 3; var2++) {
         for (int var3 = 0; var3 < 3; var3++) {
            int var4 = var3 + var2 * 3;
            this.addSlot(new CrafterSlot(this.container, var4, 26 + var3 * 18, 17 + var2 * 18, this));
         }
      }

      this.addStandardInventorySlots(var1, 8, 84);
      this.addSlot(new NonInteractiveResultSlot(this.resultContainer, 0, 134, 35));
      this.addDataSlots(this.containerData);
      this.refreshRecipeResult();
   }

   public void setSlotState(int var1, boolean var2) {
      CrafterSlot var3 = (CrafterSlot)this.getSlot(var1);
      this.containerData.set(var3.index, var2 ? 0 : 1);
      this.broadcastChanges();
   }

   public boolean isSlotDisabled(int var1) {
      return var1 > -1 && var1 < 9 ? this.containerData.get(var1) == 1 : false;
   }

   public boolean isPowered() {
      return this.containerData.get(9) == 1;
   }

   @Override
   public ItemStack quickMoveStack(Player var1, int var2) {
      ItemStack var3 = ItemStack.EMPTY;
      Slot var4 = this.slots.get(var2);
      if (var4 != null && var4.hasItem()) {
         ItemStack var5 = var4.getItem();
         var3 = var5.copy();
         if (var2 < 9) {
            if (!this.moveItemStackTo(var5, 9, 45, true)) {
               return ItemStack.EMPTY;
            }
         } else if (!this.moveItemStackTo(var5, 0, 9, false)) {
            return ItemStack.EMPTY;
         }

         if (var5.isEmpty()) {
            var4.set(ItemStack.EMPTY);
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

   @Override
   public boolean stillValid(Player var1) {
      return this.container.stillValid(var1);
   }

   private void refreshRecipeResult() {
      if (this.player instanceof ServerPlayer var1) {
         ServerLevel var5 = var1.serverLevel();
         CraftingInput var3 = this.container.asCraftInput();
         ItemStack var4 = CrafterBlock.getPotentialResults(var5, var3).map(var2 -> var2.value().assemble(var3, var5.registryAccess())).orElse(ItemStack.EMPTY);
         this.resultContainer.setItem(0, var4);
      }
   }

   public Container getContainer() {
      return this.container;
   }

   @Override
   public void slotChanged(AbstractContainerMenu var1, int var2, ItemStack var3) {
      this.refreshRecipeResult();
   }

   @Override
   public void dataChanged(AbstractContainerMenu var1, int var2, int var3) {
   }
}
