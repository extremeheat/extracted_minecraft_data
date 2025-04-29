package net.minecraft.client.gui.screens.inventory;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.protocol.game.ServerboundEditBookPacket;
import net.minecraft.server.network.Filterable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.WritableBookContent;

public class BookEditScreen extends Screen {
   public static final int TEXT_WIDTH = 114;
   public static final int TEXT_HEIGHT = 126;
   public static final int IMAGE_WIDTH = 192;
   public static final int IMAGE_HEIGHT = 192;
   public static final int BACKGROUND_TEXTURE_WIDTH = 256;
   public static final int BACKGROUND_TEXTURE_HEIGHT = 256;
   private static final Component TITLE = Component.translatable("book.edit.title");
   private final Player owner;
   private final ItemStack book;
   private final BookSignScreen signScreen;
   private int currentPage;
   private final List<String> pages = Lists.newArrayList();
   private PageButton forwardButton;
   private PageButton backButton;
   private final InteractionHand hand;
   private Component numberOfPages;
   private MultiLineEditBox page;

   public BookEditScreen(Player var1, ItemStack var2, InteractionHand var3, WritableBookContent var4) {
      super(TITLE);
      this.numberOfPages = CommonComponents.EMPTY;
      this.owner = var1;
      this.book = var2;
      this.hand = var3;
      Stream var10000 = var4.getPages(Minecraft.getInstance().isTextFilteringEnabled());
      List var10001 = this.pages;
      Objects.requireNonNull(var10001);
      var10000.forEach(var10001::add);
      if (this.pages.isEmpty()) {
         this.pages.add("");
      }

      this.signScreen = new BookSignScreen(this, var1, var3, this.pages);
   }

   private int getNumPages() {
      return this.pages.size();
   }

   protected void init() {
      int var1 = (this.width - 192) / 2;
      boolean var2 = true;
      boolean var3 = true;
      this.page = MultiLineEditBox.builder().setShowDecorations(false).setTextColor(-16777216).setCursorColor(-16777216).setShowBackground(false).setTextShadow(false).setX((this.width - 114) / 2 - 8).setY(28).build(this.font, 122, 134, CommonComponents.EMPTY);
      this.page.setCharacterLimit(1024);
      MultiLineEditBox var10000 = this.page;
      Objects.requireNonNull(this.font);
      var10000.setLineLimit(126 / 9);
      this.page.setValueListener((var1x) -> this.pages.set(this.currentPage, var1x));
      this.addRenderableWidget(this.page);
      this.updatePageContent();
      this.numberOfPages = this.getPageNumberMessage();
      this.backButton = (PageButton)this.addRenderableWidget(new PageButton(var1 + 43, 159, false, (var1x) -> this.pageBack(), true));
      this.forwardButton = (PageButton)this.addRenderableWidget(new PageButton(var1 + 116, 159, true, (var1x) -> this.pageForward(), true));
      this.addRenderableWidget(Button.builder(Component.translatable("book.signButton"), (var1x) -> this.minecraft.setScreen(this.signScreen)).bounds(this.width / 2 - 100, 196, 98, 20).build());
      this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, (var1x) -> {
         this.minecraft.setScreen((Screen)null);
         this.saveChanges();
      }).bounds(this.width / 2 + 2, 196, 98, 20).build());
      this.updateButtonVisibility();
   }

   protected void setInitialFocus() {
      this.setInitialFocus(this.page);
   }

   public Component getNarrationMessage() {
      return CommonComponents.joinForNarration(super.getNarrationMessage(), this.getPageNumberMessage());
   }

   private Component getPageNumberMessage() {
      return Component.translatable("book.pageIndicator", this.currentPage + 1, this.getNumPages());
   }

   private void pageBack() {
      if (this.currentPage > 0) {
         --this.currentPage;
         this.updatePageContent();
      }

      this.updateButtonVisibility();
   }

   private void pageForward() {
      if (this.currentPage < this.getNumPages() - 1) {
         ++this.currentPage;
      } else {
         this.appendPageToBook();
         if (this.currentPage < this.getNumPages() - 1) {
            ++this.currentPage;
         }
      }

      this.updatePageContent();
      this.updateButtonVisibility();
   }

   private void updatePageContent() {
      this.page.setValue((String)this.pages.get(this.currentPage));
      this.numberOfPages = this.getPageNumberMessage();
   }

   private void updateButtonVisibility() {
      this.backButton.visible = this.currentPage > 0;
   }

   private void eraseEmptyTrailingPages() {
      ListIterator var1 = this.pages.listIterator(this.pages.size());

      while(var1.hasPrevious() && ((String)var1.previous()).isEmpty()) {
         var1.remove();
      }

   }

   private void saveChanges() {
      this.eraseEmptyTrailingPages();
      this.updateLocalCopy();
      int var1 = this.hand == InteractionHand.MAIN_HAND ? this.owner.getInventory().getSelectedSlot() : 40;
      this.minecraft.getConnection().send(new ServerboundEditBookPacket(var1, this.pages, Optional.empty()));
   }

   private void updateLocalCopy() {
      this.book.set(DataComponents.WRITABLE_BOOK_CONTENT, new WritableBookContent(this.pages.stream().map(Filterable::passThrough).toList()));
   }

   private void appendPageToBook() {
      if (this.getNumPages() < 100) {
         this.pages.add("");
      }
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      switch (var1) {
         case 266:
            this.backButton.onPress();
            return true;
         case 267:
            this.forwardButton.onPress();
            return true;
         default:
            return super.keyPressed(var1, var2, var3);
      }
   }

   public void render(GuiGraphics var1, int var2, int var3, float var4) {
      super.render(var1, var2, var3, var4);
      int var5 = (this.width - 192) / 2;
      boolean var6 = true;
      int var7 = this.font.width((FormattedText)this.numberOfPages);
      var1.drawString(this.font, (Component)this.numberOfPages, var5 - var7 + 192 - 44, 18, -16777216, false);
   }

   public void renderBackground(GuiGraphics var1, int var2, int var3, float var4) {
      this.renderTransparentBackground(var1);
      var1.blit(RenderPipelines.GUI_TEXTURED, BookViewScreen.BOOK_LOCATION, (this.width - 192) / 2, 2, 0.0F, 0.0F, 192, 192, 256, 256);
   }
}
