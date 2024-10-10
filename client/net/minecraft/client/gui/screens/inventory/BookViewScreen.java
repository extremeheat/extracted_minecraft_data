package net.minecraft.client.gui.screens.inventory;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;

public class BookViewScreen extends Screen {
   public static final int PAGE_INDICATOR_TEXT_Y_OFFSET = 16;
   public static final int PAGE_TEXT_X_OFFSET = 36;
   public static final int PAGE_TEXT_Y_OFFSET = 30;
   private static final int BACKGROUND_TEXTURE_WIDTH = 256;
   private static final int BACKGROUND_TEXTURE_HEIGHT = 256;
   public static final BookViewScreen.BookAccess EMPTY_ACCESS = new BookViewScreen.BookAccess(List.of());
   public static final ResourceLocation BOOK_LOCATION = ResourceLocation.withDefaultNamespace("textures/gui/book.png");
   protected static final int TEXT_WIDTH = 114;
   protected static final int TEXT_HEIGHT = 128;
   protected static final int IMAGE_WIDTH = 192;
   protected static final int IMAGE_HEIGHT = 192;
   private BookViewScreen.BookAccess bookAccess;
   private int currentPage;
   private List<FormattedCharSequence> cachedPageComponents = Collections.emptyList();
   private int cachedPage = -1;
   private Component pageMsg = CommonComponents.EMPTY;
   private PageButton forwardButton;
   private PageButton backButton;
   private final boolean playTurnSound;

   public BookViewScreen(BookViewScreen.BookAccess var1) {
      this(var1, true);
   }

   public BookViewScreen() {
      this(EMPTY_ACCESS, false);
   }

   private BookViewScreen(BookViewScreen.BookAccess var1, boolean var2) {
      super(GameNarrator.NO_TITLE);
      this.bookAccess = var1;
      this.playTurnSound = var2;
   }

   public void setBookAccess(BookViewScreen.BookAccess var1) {
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

   @Override
   protected void init() {
      this.createMenuControls();
      this.createPageControlButtons();
   }

   protected void createMenuControls() {
      this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, var1 -> this.onClose()).bounds(this.width / 2 - 100, 196, 200, 20).build());
   }

   protected void createPageControlButtons() {
      int var1 = (this.width - 192) / 2;
      byte var2 = 2;
      this.forwardButton = this.addRenderableWidget(new PageButton(var1 + 116, 159, true, var1x -> this.pageForward(), this.playTurnSound));
      this.backButton = this.addRenderableWidget(new PageButton(var1 + 43, 159, false, var1x -> this.pageBack(), this.playTurnSound));
      this.updateButtonVisibility();
   }

   private int getNumPages() {
      return this.bookAccess.getPageCount();
   }

   protected void pageBack() {
      if (this.currentPage > 0) {
         this.currentPage--;
      }

      this.updateButtonVisibility();
   }

   protected void pageForward() {
      if (this.currentPage < this.getNumPages() - 1) {
         this.currentPage++;
      }

      this.updateButtonVisibility();
   }

   private void updateButtonVisibility() {
      this.forwardButton.visible = this.currentPage < this.getNumPages() - 1;
      this.backButton.visible = this.currentPage > 0;
   }

   @Override
   public boolean keyPressed(int var1, int var2, int var3) {
      if (super.keyPressed(var1, var2, var3)) {
         return true;
      } else {
         switch (var1) {
            case 266:
               this.backButton.onPress();
               return true;
            case 267:
               this.forwardButton.onPress();
               return true;
            default:
               return false;
         }
      }
   }

   @Override
   public void render(GuiGraphics var1, int var2, int var3, float var4) {
      super.render(var1, var2, var3, var4);
      int var5 = (this.width - 192) / 2;
      byte var6 = 2;
      if (this.cachedPage != this.currentPage) {
         FormattedText var7 = this.bookAccess.getPage(this.currentPage);
         this.cachedPageComponents = this.font.split(var7, 114);
         this.pageMsg = Component.translatable("book.pageIndicator", this.currentPage + 1, Math.max(this.getNumPages(), 1));
      }

      this.cachedPage = this.currentPage;
      int var11 = this.font.width(this.pageMsg);
      var1.drawString(this.font, this.pageMsg, var5 - var11 + 192 - 44, 18, 0, false);
      int var8 = Math.min(128 / 9, this.cachedPageComponents.size());

      for (int var9 = 0; var9 < var8; var9++) {
         FormattedCharSequence var10 = this.cachedPageComponents.get(var9);
         var1.drawString(this.font, var10, var5 + 36, 32 + var9 * 9, 0, false);
      }

      Style var12 = this.getClickedComponentStyleAt((double)var2, (double)var3);
      if (var12 != null) {
         var1.renderComponentHoverEffect(this.font, var12, var2, var3);
      }
   }

   @Override
   public void renderBackground(GuiGraphics var1, int var2, int var3, float var4) {
      this.renderTransparentBackground(var1);
      var1.blit(RenderType::guiTextured, BOOK_LOCATION, (this.width - 192) / 2, 2, 0.0F, 0.0F, 192, 192, 256, 256);
   }

   @Override
   public boolean mouseClicked(double var1, double var3, int var5) {
      if (var5 == 0) {
         Style var6 = this.getClickedComponentStyleAt(var1, var3);
         if (var6 != null && this.handleComponentClicked(var6)) {
            return true;
         }
      }

      return super.mouseClicked(var1, var3, var5);
   }

   @Override
   public boolean handleComponentClicked(Style var1) {
      ClickEvent var2 = var1.getClickEvent();
      if (var2 == null) {
         return false;
      } else if (var2.getAction() == ClickEvent.Action.CHANGE_PAGE) {
         String var6 = var2.getValue();

         try {
            int var4 = Integer.parseInt(var6) - 1;
            return this.forcePage(var4);
         } catch (Exception var5) {
            return false;
         }
      } else {
         boolean var3 = super.handleComponentClicked(var1);
         if (var3 && var2.getAction() == ClickEvent.Action.RUN_COMMAND) {
            this.closeScreen();
         }

         return var3;
      }
   }

   protected void closeScreen() {
      this.minecraft.setScreen(null);
   }

   @Nullable
   public Style getClickedComponentStyleAt(double var1, double var3) {
      if (this.cachedPageComponents.isEmpty()) {
         return null;
      } else {
         int var5 = Mth.floor(var1 - (double)((this.width - 192) / 2) - 36.0);
         int var6 = Mth.floor(var3 - 2.0 - 30.0);
         if (var5 >= 0 && var6 >= 0) {
            int var7 = Math.min(128 / 9, this.cachedPageComponents.size());
            if (var5 <= 114 && var6 < 9 * var7 + var7) {
               int var8 = var6 / 9;
               if (var8 >= 0 && var8 < this.cachedPageComponents.size()) {
                  FormattedCharSequence var9 = this.cachedPageComponents.get(var8);
                  return this.minecraft.font.getSplitter().componentStyleAtWidth(var9, var5);
               } else {
                  return null;
               }
            } else {
               return null;
            }
         } else {
            return null;
         }
      }
   }

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException: Cannot invoke "String.equals(Object)" because "varName" is null
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)
}
