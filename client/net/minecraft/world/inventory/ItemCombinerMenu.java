package net.minecraft.world.inventory;

import javax.annotation.Nullable;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public abstract class ItemCombinerMenu extends AbstractContainerMenu {
   public static final int INPUT_SLOT = 0;
   public static final int ADDITIONAL_SLOT = 1;
   public static final int RESULT_SLOT = 2;
   private static final int INV_SLOT_START = 3;
   private static final int INV_SLOT_END = 30;
   private static final int USE_ROW_SLOT_START = 30;
   private static final int USE_ROW_SLOT_END = 39;
   protected final ResultContainer resultSlots = new ResultContainer();
   protected final Container inputSlots = new SimpleContainer(2) {
      public void setChanged() {
         super.setChanged();
         ItemCombinerMenu.this.slotsChanged(this);
      }
   };
   protected final ContainerLevelAccess access;
   protected final Player player;

   protected abstract boolean mayPickup(Player var1, boolean var2);

   protected abstract void onTake(Player var1, ItemStack var2);

   protected abstract boolean isValidBlock(BlockState var1);

   public ItemCombinerMenu(@Nullable MenuType<?> var1, int var2, Inventory var3, ContainerLevelAccess var4) {
      super(var1, var2);
      this.access = var4;
      this.player = var3.player;
      this.addSlot(new Slot(this.inputSlots, 0, 27, 47));
      this.addSlot(new Slot(this.inputSlots, 1, 76, 47));
      this.addSlot(new Slot(this.resultSlots, 2, 134, 47) {
         public boolean mayPlace(ItemStack var1) {
            return false;
         }

         public boolean mayPickup(Player var1) {
            return ItemCombinerMenu.this.mayPickup(var1, this.hasItem());
         }

         public void onTake(Player var1, ItemStack var2) {
            ItemCombinerMenu.this.onTake(var1, var2);
         }
      });

      int var5;
      for(var5 = 0; var5 < 3; ++var5) {
         for(int var6 = 0; var6 < 9; ++var6) {
            this.addSlot(new Slot(var3, var6 + var5 * 9 + 9, 8 + var6 * 18, 84 + var5 * 18));
         }
      }

      for(var5 = 0; var5 < 9; ++var5) {
         this.addSlot(new Slot(var3, var5, 8 + var5 * 18, 142));
      }

   }

   public abstract void createResult();

   public void slotsChanged(Container var1) {
      super.slotsChanged(var1);
      if (var1 == this.inputSlots) {
         this.createResult();
      }

   }

   public void removed(Player var1) {
      super.removed(var1);
      this.access.execute((var2, var3) -> {
         this.clearContainer(var1, this.inputSlots);
      });
   }

   public boolean stillValid(Player var1) {
      return (Boolean)this.access.evaluate((var2, var3) -> {
         return !this.isValidBlock(var2.getBlockState(var3)) ? false : var1.distanceToSqr((double)var3.getX() + 0.5D, (double)var3.getY() + 0.5D, (double)var3.getZ() + 0.5D) <= 64.0D;
      }, true);
   }

   protected boolean shouldQuickMoveToAdditionalSlot(ItemStack var1) {
      return false;
   }

   public ItemStack quickMoveStack(Player var1, int var2) {
      ItemStack var3 = ItemStack.EMPTY;
      Slot var4 = (Slot)this.slots.get(var2);
      if (var4 != null && var4.hasItem()) {
         ItemStack var5 = var4.getItem();
         var3 = var5.copy();
         if (var2 == 2) {
            if (!this.moveItemStackTo(var5, 3, 39, true)) {
               return ItemStack.EMPTY;
            }

            var4.onQuickCraft(var5, var3);
         } else if (var2 != 0 && var2 != 1) {
            if (var2 >= 3 && var2 < 39) {
               int var6 = this.shouldQuickMoveToAdditionalSlot(var3) ? 1 : 0;
               if (!this.moveItemStackTo(var5, var6, 2, false)) {
                  return ItemStack.EMPTY;
               }
            }
         } else if (!this.moveItemStackTo(var5, 3, 39, false)) {
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
}
