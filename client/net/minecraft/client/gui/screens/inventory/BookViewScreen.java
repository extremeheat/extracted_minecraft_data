package net.minecraft.client.gui.screens.inventory;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ActiveTextCollector;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.TextAlignment;
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
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.WritableBookContent;
import net.minecraft.world.item.component.WrittenBookContent;
import org.jspecify.annotations.Nullable;

public class BookViewScreen extends Screen {
   public static final int PAGE_INDICATOR_TEXT_Y_OFFSET = 16;
   public static final int PAGE_TEXT_X_OFFSET = 36;
   public static final int PAGE_TEXT_Y_OFFSET = 30;
   private static final int BACKGROUND_TEXTURE_WIDTH = 256;
   private static final int BACKGROUND_TEXTURE_HEIGHT = 256;
   private static final Component TITLE = Component.translatable("book.view.title");
   private static final Style PAGE_TEXT_STYLE;
   public static final BookAccess EMPTY_ACCESS;
   public static final Identifier BOOK_LOCATION;
   protected static final int TEXT_WIDTH = 114;
   protected static final int TEXT_HEIGHT = 128;
   protected static final int IMAGE_WIDTH = 192;
   private static final int PAGE_INDICATOR_X_OFFSET = 148;
   protected static final int IMAGE_HEIGHT = 192;
   private static final int PAGE_BUTTON_Y = 157;
   private static final int PAGE_BACK_BUTTON_X = 43;
   private static final int PAGE_FORWARD_BUTTON_X = 116;
   private BookAccess bookAccess;
   private int currentPage;
   private List<FormattedCharSequence> cachedPageComponents;
   private int cachedPage;
   private Component pageMsg;
   private PageButton forwardButton;
   private PageButton backButton;
   private final boolean playTurnSound;

   public BookViewScreen(final BookAccess bookAccess) {
      this(bookAccess, true);
   }

   public BookViewScreen() {
      this(EMPTY_ACCESS, false);
   }

   private BookViewScreen(final BookAccess bookAccess, final boolean playTurnSound) {
      super(TITLE);
      this.cachedPageComponents = Collections.emptyList();
      this.cachedPage = -1;
      this.pageMsg = CommonComponents.EMPTY;
      this.bookAccess = bookAccess;
      this.playTurnSound = playTurnSound;
   }

   public void setBookAccess(final BookAccess bookAccess) {
      this.bookAccess = bookAccess;
      this.currentPage = Mth.clamp(this.currentPage, 0, bookAccess.getPageCount());
      this.updateButtonVisibility();
      this.cachedPage = -1;
   }

   public boolean setPage(final int page) {
      int clampedPage = Mth.clamp(page, 0, this.bookAccess.getPageCount() - 1);
      if (clampedPage != this.currentPage) {
         this.currentPage = clampedPage;
         this.updateButtonVisibility();
         this.cachedPage = -1;
         return true;
      } else {
         return false;
      }
   }

   protected boolean forcePage(final int page) {
      return this.setPage(page);
   }

   protected void init() {
      this.createMenuControls();
      this.createPageControlButtons();
   }

   public Component getNarrationMessage() {
      return CommonComponents.joinLines(super.getNarrationMessage(), this.getPageNumberMessage(), this.bookAccess.getPage(this.currentPage));
   }

   private Component getPageNumberMessage() {
      return Component.translatable("book.pageIndicator", this.currentPage + 1, Math.max(this.getNumPages(), 1)).withStyle(PAGE_TEXT_STYLE);
   }

   protected void createMenuControls() {
      this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, (button) -> this.onClose()).pos((this.width - 200) / 2, this.menuControlsTop()).width(200).build());
   }

   protected void createPageControlButtons() {
      int left = this.backgroundLeft();
      int top = this.backgroundTop();
      this.forwardButton = (PageButton)this.addRenderableWidget(new PageButton(left + 116, top + 157, true, (button) -> this.pageForward(), this.playTurnSound));
      this.backButton = (PageButton)this.addRenderableWidget(new PageButton(left + 43, top + 157, false, (button) -> this.pageBack(), this.playTurnSound));
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

   public boolean keyPressed(final KeyEvent event) {
      if (super.keyPressed(event)) {
         return true;
      } else {
         boolean var10000;
         switch (event.key()) {
            case 266:
               this.backButton.onPress(event);
               var10000 = true;
               break;
            case 267:
               this.forwardButton.onPress(event);
               var10000 = true;
               break;
            default:
               var10000 = false;
         }

         return var10000;
      }
   }

   public void extractRenderState(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
      super.extractRenderState(graphics, mouseX, mouseY, a);
      this.visitText(graphics.textRenderer(GuiGraphicsExtractor.HoveredTextEffects.TOOLTIP_AND_CURSOR), false);
   }

   private void visitText(final ActiveTextCollector collector, final boolean clickableOnly) {
      if (this.cachedPage != this.currentPage) {
         FormattedText pageText = ComponentUtils.mergeStyles(this.bookAccess.getPage(this.currentPage), PAGE_TEXT_STYLE);
         this.cachedPageComponents = this.font.split(pageText, 114);
         this.pageMsg = this.getPageNumberMessage();
         this.cachedPage = this.currentPage;
      }

      int left = this.backgroundLeft();
      int top = this.backgroundTop();
      if (!clickableOnly) {
         collector.accept(TextAlignment.RIGHT, left + 148, top + 16, this.pageMsg);
      }

      Objects.requireNonNull(this.font);
      int shownLines = Math.min(128 / 9, this.cachedPageComponents.size());

      for(int i = 0; i < shownLines; ++i) {
         FormattedCharSequence component = (FormattedCharSequence)this.cachedPageComponents.get(i);
         int var10001 = left + 36;
         int var10002 = top + 30;
         Objects.requireNonNull(this.font);
         collector.accept(var10001, var10002 + i * 9, component);
      }

   }

   public void extractBackground(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
      super.extractBackground(graphics, mouseX, mouseY, a);
      graphics.blit(RenderPipelines.GUI_TEXTURED, BOOK_LOCATION, this.backgroundLeft(), this.backgroundTop(), 0.0F, 0.0F, 192, 192, 256, 256);
   }

   private int backgroundLeft() {
      return (this.width - 192) / 2;
   }

   private int backgroundTop() {
      return 2;
   }

   protected int menuControlsTop() {
      return this.backgroundTop() + 192 + 2;
   }

   public boolean mouseClicked(final MouseButtonEvent event, final boolean doubleClick) {
      if (event.button() == 0) {
         ActiveTextCollector.ClickableStyleFinder finder = new ActiveTextCollector.ClickableStyleFinder(this.font, (int)event.x(), (int)event.y());
         this.visitText(finder, true);
         Style clickedStyle = finder.result();
         if (clickedStyle != null && this.handleClickEvent(clickedStyle.getClickEvent())) {
            return true;
         }
      }

      return super.mouseClicked(event, doubleClick);
   }

   protected boolean handleClickEvent(final @Nullable ClickEvent event) {
      if (event == null) {
         return false;
      } else {
         LocalPlayer player = (LocalPlayer)Objects.requireNonNull(this.minecraft.player, "Player not available");
         Objects.requireNonNull(event);
         ClickEvent var3 = event;
         byte var4 = 0;

         while(true) {
            //$FF: var4->value
            //0->net/minecraft/network/chat/ClickEvent$ChangePage
            //1->net/minecraft/network/chat/ClickEvent$RunCommand
            switch (var3.typeSwitch<invokedynamic>(var3, var4)) {
               case 0:
                  ClickEvent.ChangePage var5 = (ClickEvent.ChangePage)var3;
                  ClickEvent.ChangePage var14 = var5;

                  try {
                     var15 = var14.page();
                  } catch (Throwable var11) {
                     throw new MatchException(var11.toString(), var11);
                  }

                  int page = var15;
                  if (true) {
                     this.forcePage(page - 1);
                     return true;
                  }

                  var4 = 1;
                  break;
               case 1:
                  ClickEvent.RunCommand page = (ClickEvent.RunCommand)var3;
                  ClickEvent.RunCommand var10000 = page;

                  try {
                     var13 = var10000.command();
                  } catch (Throwable var10) {
                     throw new MatchException(var10.toString(), var10);
                  }

                  String command = var13;
                  this.closeContainerOnServer();
                  clickCommandAction(player, command, (Screen)null);
                  return true;
               default:
                  defaultHandleGameClickEvent(event, this.minecraft, this);
                  return true;
            }
         }
      }
   }

   protected void closeContainerOnServer() {
   }

   public boolean isInGameUi() {
      return true;
   }

   static {
      PAGE_TEXT_STYLE = Style.EMPTY.withoutShadow().withColor(-16777216);
      EMPTY_ACCESS = new BookAccess(List.of());
      BOOK_LOCATION = Identifier.withDefaultNamespace("textures/gui/book.png");
   }

   public static record BookAccess(List<Component> pages) {
      public BookAccess {
         super();
      }

      public int getPageCount() {
         return this.pages.size();
      }

      public Component getPage(final int page) {
         return page >= 0 && page < this.getPageCount() ? (Component)this.pages.get(page) : CommonComponents.EMPTY;
      }

      public static @Nullable BookAccess fromItem(final ItemStack itemStack) {
         boolean filterEnabled = Minecraft.getInstance().isTextFilteringEnabled();
         WrittenBookContent writtenContent = (WrittenBookContent)itemStack.get(DataComponents.WRITTEN_BOOK_CONTENT);
         if (writtenContent != null) {
            return new BookAccess(writtenContent.getPages(filterEnabled));
         } else {
            WritableBookContent writableContent = (WritableBookContent)itemStack.get(DataComponents.WRITABLE_BOOK_CONTENT);
            return writableContent != null ? new BookAccess(writableContent.getPages(filterEnabled).map(Component::literal).toList()) : null;
         }
      }
   }
}
