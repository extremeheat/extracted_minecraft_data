package net.minecraft.world.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.FletchingBlockEntity;

public class FletchingMenu extends AbstractContainerMenu {
   public static final int FEATHER_PADDING = 59;
   public static final int MIDDLE_COLUMN = 79;
   public static final int FEATHER_AXIS = 38;
   private static final int DATA_COUNT = 6;
   private static final int INV_SLOT_START = 3;
   private static final int INV_SLOT_END = 30;
   private static final int USE_ROW_SLOT_START = 30;
   private static final int USE_ROW_SLOT_END = 39;
   public static final int TITLE_PADDING = 160;
   private final Container fletching;
   private final ContainerData fletchingData;
   private final Slot ingredientSlot;

   public FletchingMenu(int var1, Inventory var2) {
      this(var1, var2, new SimpleContainer(3), new SimpleContainerData(6));
   }

   public FletchingMenu(int var1, Inventory var2, Container var3, ContainerData var4) {
      super(MenuType.FLETCHING, var1);
      checkContainerSize(var3, 3);
      checkContainerDataCount(var4, 6);
      this.fletching = var3;
      this.fletchingData = var4;
      this.ingredientSlot = this.addSlot(new FletchingMenu.IngredientsSlot(var3, 0, 239, 17));
      this.addSlot(new FurnaceResultSlot(var2.player, var3, 1, 239, 59));
      this.addSlot(new Slot(var3, 2, 180, 38) {
         @Override
         public int getMaxStackSize() {
            return 1;
         }
      });
      this.addDataSlots(var4);

      for(int var5 = 0; var5 < 3; ++var5) {
         for(int var6 = 0; var6 < 9; ++var6) {
            this.addSlot(new Slot(var2, var6 + var5 * 9 + 9, 168 + var6 * 18, 84 + var5 * 18));
         }
      }

      for(int var7 = 0; var7 < 9; ++var7) {
         this.addSlot(new Slot(var2, var7, 168 + var7 * 18, 142));
      }
   }

   @Override
   public boolean stillValid(Player var1) {
      return this.fletching.stillValid(var1);
   }

   @Override
   public ItemStack quickMoveStack(Player var1, int var2) {
      ItemStack var3 = ItemStack.EMPTY;
      Slot var4 = this.slots.get(var2);
      if (var4 != null && var4.hasItem()) {
         ItemStack var5 = var4.getItem();
         var3 = var5.copy();
         if (var2 != 0 && var2 != 1 && var2 != 2) {
            if (this.ingredientSlot.mayPlace(var5)) {
               if (!this.moveItemStackTo(var5, 0, 1, false)) {
                  return ItemStack.EMPTY;
               }
            } else if (var5.is(Items.FEATHER)) {
               if (!this.moveItemStackTo(var5, 2, 3, false)) {
                  return ItemStack.EMPTY;
               }
            } else if (var2 >= 3 && var2 < 30) {
               if (!this.moveItemStackTo(var5, 30, 39, false)) {
                  return ItemStack.EMPTY;
               }
            } else if (var2 >= 30 && var2 < 39) {
               if (!this.moveItemStackTo(var5, 3, 30, false)) {
                  return ItemStack.EMPTY;
               }
            } else if (!this.moveItemStackTo(var5, 3, 39, false)) {
               return ItemStack.EMPTY;
            }
         } else {
            if (!this.moveItemStackTo(var5, 3, 39, true)) {
               return ItemStack.EMPTY;
            }

            var4.onQuickCraft(var5, var3);
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

   public int getProgresss() {
      return this.fletchingData.get(0);
   }

   public char getSourceQuality() {
      return (char)this.fletchingData.get(1);
   }

   public char getSourceImpurities() {
      return (char)this.fletchingData.get(2);
   }

   public char getResultImpurities() {
      return (char)this.fletchingData.get(3);
   }

   public int getProcessTime() {
      return this.fletchingData.get(4);
   }

   public boolean isExplored() {
      return this.fletchingData.get(5) > 0;
   }

   class IngredientsSlot extends Slot {
      public IngredientsSlot(Container var2, int var3, int var4, int var5) {
         super(var2, var3, var4, var5);
      }

      @Override
      public boolean mayPlace(ItemStack var1) {
         return FletchingBlockEntity.canAcceptItem(var1, FletchingMenu.this.getSourceQuality(), FletchingMenu.this.getSourceImpurities());
      }

      @Override
      public int getMaxStackSize() {
         return 64;
      }
   }
}
