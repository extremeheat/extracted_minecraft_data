package net.minecraft.client.gui.screens.inventory;

import java.util.Objects;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.inventory.LecternMenu;
import net.minecraft.world.item.ItemStack;

public class LecternScreen extends BookViewScreen implements MenuAccess<LecternMenu> {
   private static final int MENU_BUTTON_MARGIN = 4;
   private static final int MENU_BUTTON_SIZE = 98;
   private static final Component TAKE_BOOK_LABEL = Component.translatable("lectern.take_book");
   private final LecternMenu menu;
   private final ContainerListener listener = new ContainerListener() {
      public void slotChanged(AbstractContainerMenu var1, int var2, ItemStack var3) {
         LecternScreen.this.bookChanged();
      }

      public void dataChanged(AbstractContainerMenu var1, int var2, int var3) {
         if (var2 == 0) {
            LecternScreen.this.pageChanged();
         }

      }
   };

   public LecternScreen(LecternMenu var1, Inventory var2, Component var3) {
      super();
      this.menu = var1;
   }

   public LecternMenu getMenu() {
      return this.menu;
   }

   protected void init() {
      super.init();
      this.menu.addSlotListener(this.listener);
   }

   public void onClose() {
      this.minecraft.player.closeContainer();
      super.onClose();
   }

   public void removed() {
      super.removed();
      this.menu.removeSlotListener(this.listener);
   }

   protected void createMenuControls() {
      if (this.minecraft.player.mayBuild()) {
         int var1 = this.menuControlsTop();
         int var2 = this.width / 2;
         this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, (var1x) -> this.onClose()).pos(var2 - 98 - 2, var1).width(98).build());
         this.addRenderableWidget(Button.builder(TAKE_BOOK_LABEL, (var1x) -> this.sendButtonClick(3)).pos(var2 + 2, var1).width(98).build());
      } else {
         super.createMenuControls();
      }

   }

   protected void pageBack() {
      this.sendButtonClick(1);
   }

   protected void pageForward() {
      this.sendButtonClick(2);
   }

   protected boolean forcePage(int var1) {
      if (var1 != this.menu.getPage()) {
         this.sendButtonClick(100 + var1);
         return true;
      } else {
         return false;
      }
   }

   private void sendButtonClick(int var1) {
      this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, var1);
   }

   public boolean isPauseScreen() {
      return false;
   }

   void bookChanged() {
      ItemStack var1 = this.menu.getBook();
      this.setBookAccess((BookViewScreen.BookAccess)Objects.requireNonNullElse(BookViewScreen.BookAccess.fromItem(var1), BookViewScreen.EMPTY_ACCESS));
   }

   void pageChanged() {
      this.setPage(this.menu.getPage());
   }

   protected void closeContainerOnServer() {
      this.minecraft.player.closeContainer();
   }

   // $FF: synthetic method
   public AbstractContainerMenu getMenu() {
      return this.getMenu();
   }
}
