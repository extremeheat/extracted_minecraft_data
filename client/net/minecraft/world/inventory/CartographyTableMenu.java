package net.minecraft.world.inventory;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.item.component.MapPostProcessing;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

public class CartographyTableMenu extends AbstractContainerMenu {
   public static final int MAP_SLOT = 0;
   public static final int ADDITIONAL_SLOT = 1;
   public static final int RESULT_SLOT = 2;
   private static final int INV_SLOT_START = 3;
   private static final int INV_SLOT_END = 30;
   private static final int USE_ROW_SLOT_START = 30;
   private static final int USE_ROW_SLOT_END = 39;
   private final ContainerLevelAccess access;
   long lastSoundTime;
   public final Container container;
   private final ResultContainer resultContainer;

   public CartographyTableMenu(int var1, Inventory var2) {
      this(var1, var2, ContainerLevelAccess.NULL);
   }

   public CartographyTableMenu(int var1, Inventory var2, final ContainerLevelAccess var3) {
      super(MenuType.CARTOGRAPHY_TABLE, var1);
      this.container = new SimpleContainer(2) {
         public void setChanged() {
            CartographyTableMenu.this.slotsChanged(this);
            super.setChanged();
         }
      };
      this.resultContainer = new ResultContainer() {
         public void setChanged() {
            CartographyTableMenu.this.slotsChanged(this);
            super.setChanged();
         }
      };
      this.access = var3;
      this.addSlot(new Slot(this, this.container, 0, 15, 15) {
         public boolean mayPlace(ItemStack var1) {
            return var1.is(Items.FILLED_MAP);
         }
      });
      this.addSlot(new Slot(this, this.container, 1, 15, 52) {
         public boolean mayPlace(ItemStack var1) {
            return var1.is(Items.PAPER) || var1.is(Items.MAP) || var1.is(Items.GLASS_PANE);
         }
      });
      this.addSlot(new Slot(this.resultContainer, 2, 145, 39) {
         public boolean mayPlace(ItemStack var1) {
            return false;
         }

         public void onTake(Player var1, ItemStack var2) {
            ((Slot)CartographyTableMenu.this.slots.get(0)).remove(1);
            ((Slot)CartographyTableMenu.this.slots.get(1)).remove(1);
            var2.getItem().onCraftedBy(var2, var1.level(), var1);
            var3.execute((var1x, var2x) -> {
               long var3x = var1x.getGameTime();
               if (CartographyTableMenu.this.lastSoundTime != var3x) {
                  var1x.playSound((Player)null, (BlockPos)var2x, SoundEvents.UI_CARTOGRAPHY_TABLE_TAKE_RESULT, SoundSource.BLOCKS, 1.0F, 1.0F);
                  CartographyTableMenu.this.lastSoundTime = var3x;
               }

            });
            super.onTake(var1, var2);
         }
      });

      int var4;
      for(var4 = 0; var4 < 3; ++var4) {
         for(int var5 = 0; var5 < 9; ++var5) {
            this.addSlot(new Slot(var2, var5 + var4 * 9 + 9, 8 + var5 * 18, 84 + var4 * 18));
         }
      }

      for(var4 = 0; var4 < 9; ++var4) {
         this.addSlot(new Slot(var2, var4, 8 + var4 * 18, 142));
      }

   }

   public boolean stillValid(Player var1) {
      return stillValid(this.access, var1, Blocks.CARTOGRAPHY_TABLE);
   }

   public void slotsChanged(Container var1) {
      ItemStack var2 = this.container.getItem(0);
      ItemStack var3 = this.container.getItem(1);
      ItemStack var4 = this.resultContainer.getItem(2);
      if (var4.isEmpty() || !var2.isEmpty() && !var3.isEmpty()) {
         if (!var2.isEmpty() && !var3.isEmpty()) {
            this.setupResultSlot(var2, var3, var4);
         }
      } else {
         this.resultContainer.removeItemNoUpdate(2);
      }

   }

   private void setupResultSlot(ItemStack var1, ItemStack var2, ItemStack var3) {
      this.access.execute((var4, var5) -> {
         MapItemSavedData var6 = MapItem.getSavedData(var1, var4);
         if (var6 != null) {
            ItemStack var7;
            if (var2.is(Items.PAPER) && !var6.locked && var6.scale < 4) {
               var7 = var1.copyWithCount(1);
               var7.set(DataComponents.MAP_POST_PROCESSING, MapPostProcessing.SCALE);
               this.broadcastChanges();
            } else if (var2.is(Items.GLASS_PANE) && !var6.locked) {
               var7 = var1.copyWithCount(1);
               var7.set(DataComponents.MAP_POST_PROCESSING, MapPostProcessing.LOCK);
               this.broadcastChanges();
            } else {
               if (!var2.is(Items.MAP)) {
                  this.resultContainer.removeItemNoUpdate(2);
                  this.broadcastChanges();
                  return;
               }

               var7 = var1.copyWithCount(2);
               this.broadcastChanges();
            }

            if (!ItemStack.matches(var7, var3)) {
               this.resultContainer.setItem(2, var7);
               this.broadcastChanges();
            }

         }
      });
   }

   public boolean canTakeItemForPickAll(ItemStack var1, Slot var2) {
      return var2.container != this.resultContainer && super.canTakeItemForPickAll(var1, var2);
   }

   public ItemStack quickMoveStack(Player var1, int var2) {
      ItemStack var3 = ItemStack.EMPTY;
      Slot var4 = (Slot)this.slots.get(var2);
      if (var4 != null && var4.hasItem()) {
         ItemStack var5 = var4.getItem();
         var3 = var5.copy();
         if (var2 == 2) {
            var5.getItem().onCraftedBy(var5, var1.level(), var1);
            if (!this.moveItemStackTo(var5, 3, 39, true)) {
               return ItemStack.EMPTY;
            }

            var4.onQuickCraft(var5, var3);
         } else if (var2 != 1 && var2 != 0) {
            if (var5.is(Items.FILLED_MAP)) {
               if (!this.moveItemStackTo(var5, 0, 1, false)) {
                  return ItemStack.EMPTY;
               }
            } else if (!var5.is(Items.PAPER) && !var5.is(Items.MAP) && !var5.is(Items.GLASS_PANE)) {
               if (var2 >= 3 && var2 < 30) {
                  if (!this.moveItemStackTo(var5, 30, 39, false)) {
                     return ItemStack.EMPTY;
                  }
               } else if (var2 >= 30 && var2 < 39 && !this.moveItemStackTo(var5, 3, 30, false)) {
                  return ItemStack.EMPTY;
               }
            } else if (!this.moveItemStackTo(var5, 1, 2, false)) {
               return ItemStack.EMPTY;
            }
         } else if (!this.moveItemStackTo(var5, 3, 39, false)) {
            return ItemStack.EMPTY;
         }

         if (var5.isEmpty()) {
            var4.setByPlayer(ItemStack.EMPTY);
         }

         var4.setChanged();
         if (var5.getCount() == var3.getCount()) {
            return ItemStack.EMPTY;
         }

         var4.onTake(var1, var5);
         this.broadcastChanges();
      }

      return var3;
   }

   public void removed(Player var1) {
      super.removed(var1);
      this.resultContainer.removeItemNoUpdate(2);
      this.access.execute((var2, var3) -> {
         this.clearContainer(var1, this.container);
      });
   }
}
