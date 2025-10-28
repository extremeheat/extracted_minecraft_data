package net.minecraft.client.gui.screens.inventory;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ActiveTextCollector;
import net.minecraft.client.gui.GuiGraphics;
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
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
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
   public static final ResourceLocation BOOK_LOCATION;
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
      return Component.translatable("book.pageIndicator", this.currentPage + 1, Math.max(this.getNumPages(), 1)).withStyle(PAGE_TEXT_STYLE);
   }

   protected void createMenuControls() {
      this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, (var1) -> this.onClose()).pos((this.width - 200) / 2, this.menuControlsTop()).width(200).build());
   }

   protected void createPageControlButtons() {
      int var1 = this.backgroundLeft();
      int var2 = this.backgroundTop();
      this.forwardButton = (PageButton)this.addRenderableWidget(new PageButton(var1 + 116, var2 + 157, true, (var1x) -> this.pageForward(), this.playTurnSound));
      this.backButton = (PageButton)this.addRenderableWidget(new PageButton(var1 + 43, var2 + 157, false, (var1x) -> this.pageBack(), this.playTurnSound));
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
      this.visitText(var1.textRenderer(GuiGraphics.HoveredTextEffects.TOOLTIP_AND_CURSOR), false);
   }

   private void visitText(ActiveTextCollector var1, boolean var2) {
      if (this.cachedPage != this.currentPage) {
         Component var3 = ComponentUtils.mergeStyles(this.bookAccess.getPage(this.currentPage), PAGE_TEXT_STYLE);
         this.cachedPageComponents = this.font.split(var3, 114);
         this.pageMsg = this.getPageNumberMessage();
         this.cachedPage = this.currentPage;
      }

      int var8 = this.backgroundLeft();
      int var4 = this.backgroundTop();
      if (!var2) {
         var1.accept(TextAlignment.RIGHT, var8 + 148, var4 + 16, this.pageMsg);
      }

      Objects.requireNonNull(this.font);
      int var5 = Math.min(128 / 9, this.cachedPageComponents.size());

      for(int var6 = 0; var6 < var5; ++var6) {
         FormattedCharSequence var7 = (FormattedCharSequence)this.cachedPageComponents.get(var6);
         int var10001 = var8 + 36;
         int var10002 = var4 + 30;
         Objects.requireNonNull(this.font);
         var1.accept(var10001, var10002 + var6 * 9, var7);
      }

   }

   public void renderBackground(GuiGraphics var1, int var2, int var3, float var4) {
      super.renderBackground(var1, var2, var3, var4);
      var1.blit(RenderPipelines.GUI_TEXTURED, BOOK_LOCATION, this.backgroundLeft(), this.backgroundTop(), 0.0F, 0.0F, 192, 192, 256, 256);
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

   public boolean mouseClicked(MouseButtonEvent var1, boolean var2) {
      if (var1.button() == 0) {
         ActiveTextCollector.ClickableStyleFinder var3 = new ActiveTextCollector.ClickableStyleFinder(this.font, (int)var1.x(), (int)var1.y());
         this.visitText(var3, true);
         Style var4 = var3.result();
         if (var4 != null && this.handleClickEvent(var4.getClickEvent())) {
            return true;
         }
      }

      return super.mouseClicked(var1, var2);
   }

   protected boolean handleClickEvent(@Nullable ClickEvent var1) {
      if (var1 == null) {
         return false;
      } else {
         LocalPlayer var2 = (LocalPlayer)Objects.requireNonNull(this.minecraft.player, "Player not available");
         Objects.requireNonNull(var1);
         byte var4 = 0;
         //$FF: var4->value
         //0->net/minecraft/network/chat/ClickEvent$ChangePage
         //1->net/minecraft/network/chat/ClickEvent$RunCommand
         switch (var1.typeSwitch<invokedynamic>(var1, var4)) {
            case 0:
               ClickEvent.ChangePage var5 = (ClickEvent.ChangePage)var1;
               ClickEvent.ChangePage var14 = var5;

               try {
                  var15 = var14.page();
               } catch (Throwable var11) {
                  throw new MatchException(var11.toString(), var11);
               }

               int var12 = var15;
               this.forcePage(var12 - 1);
               break;
            case 1:
               ClickEvent.RunCommand var7 = (ClickEvent.RunCommand)var1;
               ClickEvent.RunCommand var10000 = var7;

               try {
                  var13 = var10000.command();
               } catch (Throwable var10) {
                  throw new MatchException(var10.toString(), var10);
               }

               String var9 = var13;
               this.closeContainerOnServer();
               clickCommandAction(var2, var9, (Screen)null);
               break;
            default:
               defaultHandleGameClickEvent(var1, this.minecraft, this);
         }

         return true;
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
      BOOK_LOCATION = ResourceLocation.withDefaultNamespace("textures/gui/book.png");
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

      public static @Nullable BookAccess fromItem(ItemStack var0) {
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
