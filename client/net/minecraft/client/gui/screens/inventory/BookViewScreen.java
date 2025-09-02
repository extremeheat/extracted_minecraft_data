package net.minecraft.client.gui.screens.inventory;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.WritableBookContent;
import net.minecraft.world.item.component.WrittenBookContent;

public class BookViewScreen extends Screen {
   public static final int PAGE_INDICATOR_TEXT_Y_OFFSET = 16;
   public static final int PAGE_TEXT_X_OFFSET = 36;
   public static final int PAGE_TEXT_Y_OFFSET = 30;
   private static final int BACKGROUND_TEXTURE_WIDTH = 256;
   private static final int BACKGROUND_TEXTURE_HEIGHT = 256;
   private static final Component TITLE = Component.translatable("book.view.title");
   public static final BookAccess EMPTY_ACCESS = new BookAccess(List.of());
   public static final ResourceLocation BOOK_LOCATION = ResourceLocation.withDefaultNamespace("textures/gui/book.png");
   protected static final int TEXT_WIDTH = 114;
   protected static final int TEXT_HEIGHT = 128;
   protected static final int IMAGE_WIDTH = 192;
   protected static final int IMAGE_HEIGHT = 192;
   private BookAccess bookAccess;
   private int currentPage;
   private List<FormattedCharSequence> cachedPageComponents;
   private int cachedPage;
   private Component pageMsg;
   private PageButton forwardButton;
   private PageButton backButton;
   private final boolean playTurnSound;

   public BookViewScreen(BookAccess var1) {
      this(var1, true);
   }

   public BookViewScreen() {
      this(EMPTY_ACCESS, false);
   }

   private BookViewScreen(BookAccess var1, boolean var2) {
      super(TITLE);
      this.cachedPageComponents = Collections.emptyList();
      this.cachedPage = -1;
      this.pageMsg = CommonComponents.EMPTY;
      this.bookAccess = var1;
      this.playTurnSound = var2;
   }

   public void setBookAccess(BookAccess var1) {
      this.bookAccess = var1;
      this.currentPage = Mth.clamp(this.currentPage, 0, var1.getPageCount());
      this.updateButtonVisibility();
      this.cachedPage = -1;
   }

   public boolean setPage(int var1) {
      int var2 = Mth.clamp(var1, 0, this.bookAccess.getPageCount() - 1);
      if (var2 != this.currentPage) {
         this.currentPage = var2;
         this.updateButtonVisibility();
         this.cachedPage = -1;
         return true;
      } else {
         return false;
      }
   }

   protected boolean forcePage(int var1) {
      return this.setPage(var1);
   }

   protected void init() {
      this.createMenuControls();
      this.createPageControlButtons();
   }

   public Component getNarrationMessage() {
      return CommonComponents.joinLines(super.getNarrationMessage(), this.getPageNumberMessage(), this.bookAccess.getPage(this.currentPage));
   }

   private Component getPageNumberMessage() {
      return Component.translatable("book.pageIndicator", this.currentPage + 1, Math.max(this.getNumPages(), 1));
   }

   protected void createMenuControls() {
      this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, (var1) -> this.onClose()).bounds(this.width / 2 - 100, 196, 200, 20).build());
   }

   protected void createPageControlButtons() {
      int var1 = (this.width - 192) / 2;
      boolean var2 = true;
      this.forwardButton = (PageButton)this.addRenderableWidget(new PageButton(var1 + 116, 159, true, (var1x) -> this.pageForward(), this.playTurnSound));
      this.backButton = (PageButton)this.addRenderableWidget(new PageButton(var1 + 43, 159, false, (var1x) -> this.pageBack(), this.playTurnSound));
      this.updateButtonVisibility();
   }

   private int getNumPages() {
      return this.bookAccess.getPageCount();
   }

   protected void pageBack() {
      if (this.currentPage > 0) {
         --this.currentPage;
      }

      this.updateButtonVisibility();
   }

   protected void pageForward() {
      if (this.currentPage < this.getNumPages() - 1) {
         ++this.currentPage;
      }

      this.updateButtonVisibility();
   }

   private void updateButtonVisibility() {
      this.forwardButton.visible = this.currentPage < this.getNumPages() - 1;
      this.backButton.visible = this.currentPage > 0;
   }

   public boolean keyPressed(KeyEvent var1) {
      if (super.keyPressed(var1)) {
         return true;
      } else {
         switch (var1.key()) {
            case 266:
               this.backButton.onPress(var1);
               return true;
            case 267:
               this.forwardButton.onPress(var1);
               return true;
            default:
               return false;
         }
      }
   }

   public void render(GuiGraphics var1, int var2, int var3, float var4) {
      super.render(var1, var2, var3, var4);
      int var5 = (this.width - 192) / 2;
      boolean var6 = true;
      if (this.cachedPage != this.currentPage) {
         Component var7 = this.bookAccess.getPage(this.currentPage);
         this.cachedPageComponents = this.font.split(var7, 114);
         this.pageMsg = this.getPageNumberMessage();
      }

      this.cachedPage = this.currentPage;
      int var11 = this.font.width((FormattedText)this.pageMsg);
      var1.drawString(this.font, (Component)this.pageMsg, var5 - var11 + 192 - 44, 18, -16777216, false);
      Objects.requireNonNull(this.font);
      int var8 = Math.min(128 / 9, this.cachedPageComponents.size());

      for(int var9 = 0; var9 < var8; ++var9) {
         FormattedCharSequence var10 = (FormattedCharSequence)this.cachedPageComponents.get(var9);
         Font var10001 = this.font;
         int var10003 = var5 + 36;
         Objects.requireNonNull(this.font);
         var1.drawString(var10001, var10, var10003, 32 + var9 * 9, -16777216, false);
      }

      Style var12 = this.getClickedComponentStyleAt((double)var2, (double)var3);
      if (var12 != null) {
         var1.renderComponentHoverEffect(this.font, var12, var2, var3);
      }

   }

   public void renderBackground(GuiGraphics var1, int var2, int var3, float var4) {
      super.renderBackground(var1, var2, var3, var4);
      var1.blit(RenderPipelines.GUI_TEXTURED, BOOK_LOCATION, (this.width - 192) / 2, 2, 0.0F, 0.0F, 192, 192, 256, 256);
   }

   public boolean mouseClicked(MouseButtonEvent var1, boolean var2) {
      if (var1.button() == 0) {
         Style var3 = this.getClickedComponentStyleAt(var1.x(), var1.y());
         if (var3 != null && this.handleComponentClicked(var3)) {
            return true;
         }
      }

      return super.mouseClicked(var1, var2);
   }

   protected void handleClickEvent(Minecraft var1, ClickEvent var2) {
      LocalPlayer var3 = (LocalPlayer)Objects.requireNonNull(var1.player, "Player not available");
      Objects.requireNonNull(var2);
      byte var5 = 0;
      //$FF: var5->value
      //0->net/minecraft/network/chat/ClickEvent$ChangePage
      //1->net/minecraft/network/chat/ClickEvent$RunCommand
      switch (var2.typeSwitch<invokedynamic>(var2, var5)) {
         case 0:
            ClickEvent.ChangePage var6 = (ClickEvent.ChangePage)var2;
            ClickEvent.ChangePage var15 = var6;

            try {
               var16 = var15.page();
            } catch (Throwable var12) {
               throw new MatchException(var12.toString(), var12);
            }

            int var13 = var16;
            this.forcePage(var13 - 1);
            break;
         case 1:
            ClickEvent.RunCommand var8 = (ClickEvent.RunCommand)var2;
            ClickEvent.RunCommand var10000 = var8;

            try {
               var14 = var10000.command();
            } catch (Throwable var11) {
               throw new MatchException(var11.toString(), var11);
            }

            String var10 = var14;
            this.closeContainerOnServer();
            clickCommandAction(var3, var10, (Screen)null);
            break;
         default:
            defaultHandleGameClickEvent(var2, var1, this);
      }

   }

   protected void closeContainerOnServer() {
   }

   public boolean isInGameUi() {
      return true;
   }

   @Nullable
   public Style getClickedComponentStyleAt(double var1, double var3) {
      if (this.cachedPageComponents.isEmpty()) {
         return null;
      } else {
         int var5 = Mth.floor(var1 - (double)((this.width - 192) / 2) - 36.0);
         int var6 = Mth.floor(var3 - 2.0 - 30.0);
         if (var5 >= 0 && var6 >= 0) {
            Objects.requireNonNull(this.font);
            int var7 = Math.min(128 / 9, this.cachedPageComponents.size());
            if (var5 <= 114) {
               Objects.requireNonNull(this.minecraft.font);
               if (var6 < 9 * var7 + var7) {
                  Objects.requireNonNull(this.minecraft.font);
                  int var8 = var6 / 9;
                  if (var8 >= 0 && var8 < this.cachedPageComponents.size()) {
                     FormattedCharSequence var9 = (FormattedCharSequence)this.cachedPageComponents.get(var8);
                     return this.minecraft.font.getSplitter().componentStyleAtWidth(var9, var5);
                  }

                  return null;
               }
            }

            return null;
         } else {
            return null;
         }
      }
   }

   public static record BookAccess(List<Component> pages) {
      public BookAccess(List<Component> var1) {
         super();
         this.pages = var1;
      }

      public int getPageCount() {
         return this.pages.size();
      }

      public Component getPage(int var1) {
         return var1 >= 0 && var1 < this.getPageCount() ? (Component)this.pages.get(var1) : CommonComponents.EMPTY;
      }

      @Nullable
      public static BookAccess fromItem(ItemStack var0) {
         boolean var1 = Minecraft.getInstance().isTextFilteringEnabled();
         WrittenBookContent var2 = (WrittenBookContent)var0.get(DataComponents.WRITTEN_BOOK_CONTENT);
         if (var2 != null) {
            return new BookAccess(var2.getPages(var1));
         } else {
            WritableBookContent var3 = (WritableBookContent)var0.get(DataComponents.WRITABLE_BOOK_CONTENT);
            return var3 != null ? new BookAccess(var3.getPages(var1).map(Component::literal).toList()) : null;
         }
      }
   }
}
