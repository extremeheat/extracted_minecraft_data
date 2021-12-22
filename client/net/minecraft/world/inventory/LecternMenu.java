package net.minecraft.world.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class LecternMenu extends AbstractContainerMenu {
   private static final int DATA_COUNT = 1;
   private static final int SLOT_COUNT = 1;
   public static final int BUTTON_PREV_PAGE = 1;
   public static final int BUTTON_NEXT_PAGE = 2;
   public static final int BUTTON_TAKE_BOOK = 3;
   public static final int BUTTON_PAGE_JUMP_RANGE_START = 100;
   private final Container lectern;
   private final ContainerData lecternData;

   public LecternMenu(int var1) {
      this(var1, new SimpleContainer(1), new SimpleContainerData(1));
   }

   public LecternMenu(int var1, Container var2, ContainerData var3) {
      super(MenuType.LECTERN, var1);
      checkContainerSize(var2, 1);
      checkContainerDataCount(var3, 1);
      this.lectern = var2;
      this.lecternData = var3;
      this.addSlot(new Slot(var2, 0, 0, 0) {
         public void setChanged() {
            super.setChanged();
            LecternMenu.this.slotsChanged(this.container);
         }
      });
      this.addDataSlots(var3);
   }

   public boolean clickMenuButton(Player var1, int var2) {
      int var4;
      if (var2 >= 100) {
         var4 = var2 - 100;
         this.setData(0, var4);
         return true;
      } else {
         switch(var2) {
         case 1:
            var4 = this.lecternData.get(0);
            this.setData(0, var4 - 1);
            return true;
         case 2:
            var4 = this.lecternData.get(0);
            this.setData(0, var4 + 1);
            return true;
         case 3:
            if (!var1.mayBuild()) {
               return false;
            }

            ItemStack var3 = this.lectern.removeItemNoUpdate(0);
            this.lectern.setChanged();
            if (!var1.getInventory().add(var3)) {
               var1.drop(var3, false);
            }

            return true;
         default:
            return false;
         }
      }
   }

   public void setData(int var1, int var2) {
      super.setData(var1, var2);
      this.broadcastChanges();
   }

   public boolean stillValid(Player var1) {
      return this.lectern.stillValid(var1);
   }

   public ItemStack getBook() {
      return this.lectern.getItem(0);
   }

   public int getPage() {
      return this.lecternData.get(0);
   }
}
