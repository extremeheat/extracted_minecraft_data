package net.minecraft.client.gui.screens.inventory;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.StringSplitter;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.font.TextFieldHelper;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.protocol.game.ServerboundEditBookPacket;
import net.minecraft.server.network.Filterable;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.StringUtil;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.WritableBookContent;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.mutable.MutableInt;

public class BookEditScreen extends Screen {
   private static final int TEXT_WIDTH = 114;
   private static final int TEXT_HEIGHT = 128;
   private static final int IMAGE_WIDTH = 192;
   private static final int IMAGE_HEIGHT = 192;
   private static final int BACKGROUND_TEXTURE_WIDTH = 256;
   private static final int BACKGROUND_TEXTURE_HEIGHT = 256;
   private static final Component EDIT_TITLE_LABEL = Component.translatable("book.editTitle");
   private static final Component FINALIZE_WARNING_LABEL = Component.translatable("book.finalizeWarning");
   private static final FormattedCharSequence BLACK_CURSOR = FormattedCharSequence.forward("_", Style.EMPTY.withColor(ChatFormatting.BLACK));
   private static final FormattedCharSequence GRAY_CURSOR = FormattedCharSequence.forward("_", Style.EMPTY.withColor(ChatFormatting.GRAY));
   private final Player owner;
   private final ItemStack book;
   private boolean isModified;
   private boolean isSigning;
   private int frameTick;
   private int currentPage;
   private final List<String> pages = Lists.newArrayList();
   private String title = "";
   private final TextFieldHelper pageEdit = new TextFieldHelper(
      this::getCurrentPageText,
      this::setCurrentPageText,
      this::getClipboard,
      this::setClipboard,
      var1x -> var1x.length() < 1024 && this.font.wordWrapHeight(var1x, 114) <= 128
   );
   private final TextFieldHelper titleEdit = new TextFieldHelper(
      () -> this.title, var1x -> this.title = var1x, this::getClipboard, this::setClipboard, var0 -> var0.length() < 16
   );
   private long lastClickTime;
   private int lastIndex = -1;
   private PageButton forwardButton;
   private PageButton backButton;
   private Button doneButton;
   private Button signButton;
   private Button finalizeButton;
   private Button cancelButton;
   private final InteractionHand hand;
   @Nullable
   private BookEditScreen.DisplayCache displayCache = BookEditScreen.DisplayCache.EMPTY;
   private Component pageMsg = CommonComponents.EMPTY;
   private final Component ownerText;

   public BookEditScreen(Player var1, ItemStack var2, InteractionHand var3) {
      super(GameNarrator.NO_TITLE);
      this.owner = var1;
      this.book = var2;
      this.hand = var3;
      WritableBookContent var4 = var2.get(DataComponents.WRITABLE_BOOK_CONTENT);
      if (var4 != null) {
         var4.getPages(Minecraft.getInstance().isTextFilteringEnabled()).forEach(this.pages::add);
      }

      if (this.pages.isEmpty()) {
         this.pages.add("");
      }

      this.ownerText = Component.translatable("book.byAuthor", var1.getName()).withStyle(ChatFormatting.DARK_GRAY);
   }

   private void setClipboard(String var1) {
      if (this.minecraft != null) {
         TextFieldHelper.setClipboardContents(this.minecraft, var1);
      }
   }

   private String getClipboard() {
      return this.minecraft != null ? TextFieldHelper.getClipboardContents(this.minecraft) : "";
   }

   private int getNumPages() {
      return this.pages.size();
   }

   @Override
   public void tick() {
      super.tick();
      this.frameTick++;
   }

   @Override
   protected void init() {
      this.clearDisplayCache();
      this.signButton = this.addRenderableWidget(Button.builder(Component.translatable("book.signButton"), var1x -> {
         this.isSigning = true;
         this.updateButtonVisibility();
      }).bounds(this.width / 2 - 100, 196, 98, 20).build());
      this.doneButton = this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, var1x -> {
         this.minecraft.setScreen(null);
         this.saveChanges(false);
      }).bounds(this.width / 2 + 2, 196, 98, 20).build());
      this.finalizeButton = this.addRenderableWidget(Button.builder(Component.translatable("book.finalizeButton"), var1x -> {
         if (this.isSigning) {
            this.saveChanges(true);
            this.minecraft.setScreen(null);
         }
      }).bounds(this.width / 2 - 100, 196, 98, 20).build());
      this.cancelButton = this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, var1x -> {
         if (this.isSigning) {
            this.isSigning = false;
         }

         this.updateButtonVisibility();
      }).bounds(this.width / 2 + 2, 196, 98, 20).build());
      int var1 = (this.width - 192) / 2;
      byte var2 = 2;
      this.forwardButton = this.addRenderableWidget(new PageButton(var1 + 116, 159, true, var1x -> this.pageForward(), true));
      this.backButton = this.addRenderableWidget(new PageButton(var1 + 43, 159, false, var1x -> this.pageBack(), true));
      this.updateButtonVisibility();
   }

   private void pageBack() {
      if (this.currentPage > 0) {
         this.currentPage--;
      }

      this.updateButtonVisibility();
      this.clearDisplayCacheAfterPageChange();
   }

   private void pageForward() {
      if (this.currentPage < this.getNumPages() - 1) {
         this.currentPage++;
      } else {
         this.appendPageToBook();
         if (this.currentPage < this.getNumPages() - 1) {
            this.currentPage++;
         }
      }

      this.updateButtonVisibility();
      this.clearDisplayCacheAfterPageChange();
   }

   private void updateButtonVisibility() {
      this.backButton.visible = !this.isSigning && this.currentPage > 0;
      this.forwardButton.visible = !this.isSigning;
      this.doneButton.visible = !this.isSigning;
      this.signButton.visible = !this.isSigning;
      this.cancelButton.visible = this.isSigning;
      this.finalizeButton.visible = this.isSigning;
      this.finalizeButton.active = !StringUtil.isBlank(this.title);
   }

   private void eraseEmptyTrailingPages() {
      ListIterator var1 = this.pages.listIterator(this.pages.size());

      while (var1.hasPrevious() && ((String)var1.previous()).isEmpty()) {
         var1.remove();
      }
   }

   private void saveChanges(boolean var1) {
      if (this.isModified) {
         this.eraseEmptyTrailingPages();
         this.updateLocalCopy();
         int var2 = this.hand == InteractionHand.MAIN_HAND ? this.owner.getInventory().selected : 40;
         this.minecraft.getConnection().send(new ServerboundEditBookPacket(var2, this.pages, var1 ? Optional.of(this.title.trim()) : Optional.empty()));
      }
   }

   private void updateLocalCopy() {
      this.book.set(DataComponents.WRITABLE_BOOK_CONTENT, new WritableBookContent(this.pages.stream().map(Filterable::passThrough).toList()));
   }

   private void appendPageToBook() {
      if (this.getNumPages() < 100) {
         this.pages.add("");
         this.isModified = true;
      }
   }

   @Override
   public boolean keyPressed(int var1, int var2, int var3) {
      if (super.keyPressed(var1, var2, var3)) {
         return true;
      } else if (this.isSigning) {
         return this.titleKeyPressed(var1, var2, var3);
      } else {
         boolean var4 = this.bookKeyPressed(var1, var2, var3);
         if (var4) {
            this.clearDisplayCache();
            return true;
         } else {
            return false;
         }
      }
   }

   @Override
   public boolean charTyped(char var1, int var2) {
      if (super.charTyped(var1, var2)) {
         return true;
      } else if (this.isSigning) {
         boolean var3 = this.titleEdit.charTyped(var1);
         if (var3) {
            this.updateButtonVisibility();
            this.isModified = true;
            return true;
         } else {
            return false;
         }
      } else if (StringUtil.isAllowedChatCharacter(var1)) {
         this.pageEdit.insertText(Character.toString(var1));
         this.clearDisplayCache();
         return true;
      } else {
         return false;
      }
   }

   private boolean bookKeyPressed(int var1, int var2, int var3) {
      if (Screen.isSelectAll(var1)) {
         this.pageEdit.selectAll();
         return true;
      } else if (Screen.isCopy(var1)) {
         this.pageEdit.copy();
         return true;
      } else if (Screen.isPaste(var1)) {
         this.pageEdit.paste();
         return true;
      } else if (Screen.isCut(var1)) {
         this.pageEdit.cut();
         return true;
      } else {
         TextFieldHelper.CursorStep var4 = Screen.hasControlDown() ? TextFieldHelper.CursorStep.WORD : TextFieldHelper.CursorStep.CHARACTER;
         switch (var1) {
            case 257:
            case 335:
               this.pageEdit.insertText("\n");
               return true;
            case 259:
               this.pageEdit.removeFromCursor(-1, var4);
               return true;
            case 261:
               this.pageEdit.removeFromCursor(1, var4);
               return true;
            case 262:
               this.pageEdit.moveBy(1, Screen.hasShiftDown(), var4);
               return true;
            case 263:
               this.pageEdit.moveBy(-1, Screen.hasShiftDown(), var4);
               return true;
            case 264:
               this.keyDown();
               return true;
            case 265:
               this.keyUp();
               return true;
            case 266:
               this.backButton.onPress();
               return true;
            case 267:
               this.forwardButton.onPress();
               return true;
            case 268:
               this.keyHome();
               return true;
            case 269:
               this.keyEnd();
               return true;
            default:
               return false;
         }
      }
   }

   private void keyUp() {
      this.changeLine(-1);
   }

   private void keyDown() {
      this.changeLine(1);
   }

   private void changeLine(int var1) {
      int var2 = this.pageEdit.getCursorPos();
      int var3 = this.getDisplayCache().changeLine(var2, var1);
      this.pageEdit.setCursorPos(var3, Screen.hasShiftDown());
   }

   private void keyHome() {
      if (Screen.hasControlDown()) {
         this.pageEdit.setCursorToStart(Screen.hasShiftDown());
      } else {
         int var1 = this.pageEdit.getCursorPos();
         int var2 = this.getDisplayCache().findLineStart(var1);
         this.pageEdit.setCursorPos(var2, Screen.hasShiftDown());
      }
   }

   private void keyEnd() {
      if (Screen.hasControlDown()) {
         this.pageEdit.setCursorToEnd(Screen.hasShiftDown());
      } else {
         BookEditScreen.DisplayCache var1 = this.getDisplayCache();
         int var2 = this.pageEdit.getCursorPos();
         int var3 = var1.findLineEnd(var2);
         this.pageEdit.setCursorPos(var3, Screen.hasShiftDown());
      }
   }

   private boolean titleKeyPressed(int var1, int var2, int var3) {
      switch (var1) {
         case 257:
         case 335:
            if (!this.title.isEmpty()) {
               this.saveChanges(true);
               this.minecraft.setScreen(null);
            }

            return true;
         case 259:
            this.titleEdit.removeCharsFromCursor(-1);
            this.updateButtonVisibility();
            this.isModified = true;
            return true;
         default:
            return false;
      }
   }

   private String getCurrentPageText() {
      return this.currentPage >= 0 && this.currentPage < this.pages.size() ? this.pages.get(this.currentPage) : "";
   }

   private void setCurrentPageText(String var1) {
      if (this.currentPage >= 0 && this.currentPage < this.pages.size()) {
         this.pages.set(this.currentPage, var1);
         this.isModified = true;
         this.clearDisplayCache();
      }
   }

   @Override
   public void render(GuiGraphics var1, int var2, int var3, float var4) {
      super.render(var1, var2, var3, var4);
      this.setFocused(null);
      int var5 = (this.width - 192) / 2;
      byte var6 = 2;
      if (this.isSigning) {
         boolean var7 = this.frameTick / 6 % 2 == 0;
         FormattedCharSequence var8 = FormattedCharSequence.composite(FormattedCharSequence.forward(this.title, Style.EMPTY), var7 ? BLACK_CURSOR : GRAY_CURSOR);
         int var9 = this.font.width(EDIT_TITLE_LABEL);
         var1.drawString(this.font, EDIT_TITLE_LABEL, var5 + 36 + (114 - var9) / 2, 34, 0, false);
         int var10 = this.font.width(var8);
         var1.drawString(this.font, var8, var5 + 36 + (114 - var10) / 2, 50, 0, false);
         int var11 = this.font.width(this.ownerText);
         var1.drawString(this.font, this.ownerText, var5 + 36 + (114 - var11) / 2, 60, 0, false);
         var1.drawWordWrap(this.font, FINALIZE_WARNING_LABEL, var5 + 36, 82, 114, 0);
      } else {
         int var13 = this.font.width(this.pageMsg);
         var1.drawString(this.font, this.pageMsg, var5 - var13 + 192 - 44, 18, 0, false);
         BookEditScreen.DisplayCache var14 = this.getDisplayCache();

         for (BookEditScreen.LineInfo var12 : var14.lines) {
            var1.drawString(this.font, var12.asComponent, var12.x, var12.y, -16777216, false);
         }

         this.renderHighlight(var1, var14.selection);
         this.renderCursor(var1, var14.cursor, var14.cursorAtEnd);
      }
   }

   @Override
   public void renderBackground(GuiGraphics var1, int var2, int var3, float var4) {
      this.renderTransparentBackground(var1);
      var1.blit(RenderType::guiTextured, BookViewScreen.BOOK_LOCATION, (this.width - 192) / 2, 2, 0.0F, 0.0F, 192, 192, 256, 256);
   }

   private void renderCursor(GuiGraphics var1, BookEditScreen.Pos2i var2, boolean var3) {
      if (this.frameTick / 6 % 2 == 0) {
         var2 = this.convertLocalToScreen(var2);
         if (!var3) {
            var1.fill(var2.x, var2.y - 1, var2.x + 1, var2.y + 9, -16777216);
         } else {
            var1.drawString(this.font, "_", var2.x, var2.y, 0, false);
         }
      }
   }

   private void renderHighlight(GuiGraphics var1, Rect2i[] var2) {
      for (Rect2i var6 : var2) {
         int var7 = var6.getX();
         int var8 = var6.getY();
         int var9 = var7 + var6.getWidth();
         int var10 = var8 + var6.getHeight();
         var1.fill(RenderType.guiTextHighlight(), var7, var8, var9, var10, -16776961);
      }
   }

   private BookEditScreen.Pos2i convertScreenToLocal(BookEditScreen.Pos2i var1) {
      return new BookEditScreen.Pos2i(var1.x - (this.width - 192) / 2 - 36, var1.y - 32);
   }

   private BookEditScreen.Pos2i convertLocalToScreen(BookEditScreen.Pos2i var1) {
      return new BookEditScreen.Pos2i(var1.x + (this.width - 192) / 2 + 36, var1.y + 32);
   }

   @Override
   public boolean mouseClicked(double var1, double var3, int var5) {
      if (super.mouseClicked(var1, var3, var5)) {
         return true;
      } else {
         if (var5 == 0) {
            long var6 = Util.getMillis();
            BookEditScreen.DisplayCache var8 = this.getDisplayCache();
            int var9 = var8.getIndexAtPosition(this.font, this.convertScreenToLocal(new BookEditScreen.Pos2i((int)var1, (int)var3)));
            if (var9 >= 0) {
               if (var9 != this.lastIndex || var6 - this.lastClickTime >= 250L) {
                  this.pageEdit.setCursorPos(var9, Screen.hasShiftDown());
               } else if (!this.pageEdit.isSelecting()) {
                  this.selectWord(var9);
               } else {
                  this.pageEdit.selectAll();
               }

               this.clearDisplayCache();
            }

            this.lastIndex = var9;
            this.lastClickTime = var6;
         }

         return true;
      }
   }

   private void selectWord(int var1) {
      String var2 = this.getCurrentPageText();
      this.pageEdit.setSelectionRange(StringSplitter.getWordPosition(var2, -1, var1, false), StringSplitter.getWordPosition(var2, 1, var1, false));
   }

   @Override
   public boolean mouseDragged(double var1, double var3, int var5, double var6, double var8) {
      if (super.mouseDragged(var1, var3, var5, var6, var8)) {
         return true;
      } else {
         if (var5 == 0) {
            BookEditScreen.DisplayCache var10 = this.getDisplayCache();
            int var11 = var10.getIndexAtPosition(this.font, this.convertScreenToLocal(new BookEditScreen.Pos2i((int)var1, (int)var3)));
            this.pageEdit.setCursorPos(var11, true);
            this.clearDisplayCache();
         }

         return true;
      }
   }

   private BookEditScreen.DisplayCache getDisplayCache() {
      if (this.displayCache == null) {
         this.displayCache = this.rebuildDisplayCache();
         this.pageMsg = Component.translatable("book.pageIndicator", this.currentPage + 1, this.getNumPages());
      }

      return this.displayCache;
   }

   private void clearDisplayCache() {
      this.displayCache = null;
   }

   private void clearDisplayCacheAfterPageChange() {
      this.pageEdit.setCursorToEnd();
      this.clearDisplayCache();
   }

   private BookEditScreen.DisplayCache rebuildDisplayCache() {
      String var1 = this.getCurrentPageText();
      if (var1.isEmpty()) {
         return BookEditScreen.DisplayCache.EMPTY;
      } else {
         int var2 = this.pageEdit.getCursorPos();
         int var3 = this.pageEdit.getSelectionPos();
         IntArrayList var4 = new IntArrayList();
         ArrayList var5 = Lists.newArrayList();
         MutableInt var6 = new MutableInt();
         MutableBoolean var7 = new MutableBoolean();
         StringSplitter var8 = this.font.getSplitter();
         var8.splitLines(var1, 114, Style.EMPTY, true, (var6x, var7x, var8x) -> {
            int var9x = var6.getAndIncrement();
            String var10x = var1.substring(var7x, var8x);
            var7.setValue(var10x.endsWith("\n"));
            String var11x = StringUtils.stripEnd(var10x, " \n");
            int var12x = var9x * 9;
            BookEditScreen.Pos2i var13x = this.convertLocalToScreen(new BookEditScreen.Pos2i(0, var12x));
            var4.add(var7x);
            var5.add(new BookEditScreen.LineInfo(var6x, var11x, var13x.x, var13x.y));
         });
         int[] var9 = var4.toIntArray();
         boolean var10 = var2 == var1.length();
         BookEditScreen.Pos2i var11;
         if (var10 && var7.isTrue()) {
            var11 = new BookEditScreen.Pos2i(0, var5.size() * 9);
         } else {
            int var12 = findLineFromPos(var9, var2);
            int var13 = this.font.width(var1.substring(var9[var12], var2));
            var11 = new BookEditScreen.Pos2i(var13, var12 * 9);
         }

         ArrayList var22 = Lists.newArrayList();
         if (var2 != var3) {
            int var23 = Math.min(var2, var3);
            int var14 = Math.max(var2, var3);
            int var15 = findLineFromPos(var9, var23);
            int var16 = findLineFromPos(var9, var14);
            if (var15 == var16) {
               int var17 = var15 * 9;
               int var18 = var9[var15];
               var22.add(this.createPartialLineSelection(var1, var8, var23, var14, var17, var18));
            } else {
               int var24 = var15 + 1 > var9.length ? var1.length() : var9[var15 + 1];
               var22.add(this.createPartialLineSelection(var1, var8, var23, var24, var15 * 9, var9[var15]));

               for (int var25 = var15 + 1; var25 < var16; var25++) {
                  int var19 = var25 * 9;
                  String var20 = var1.substring(var9[var25], var9[var25 + 1]);
                  int var21 = (int)var8.stringWidth(var20);
                  var22.add(this.createSelection(new BookEditScreen.Pos2i(0, var19), new BookEditScreen.Pos2i(var21, var19 + 9)));
               }

               var22.add(this.createPartialLineSelection(var1, var8, var9[var16], var14, var16 * 9, var9[var16]));
            }
         }

         return new BookEditScreen.DisplayCache(var1, var11, var10, var9, var5.toArray(new BookEditScreen.LineInfo[0]), var22.toArray(new Rect2i[0]));
      }
   }

   static int findLineFromPos(int[] var0, int var1) {
      int var2 = Arrays.binarySearch(var0, var1);
      return var2 < 0 ? -(var2 + 2) : var2;
   }

   private Rect2i createPartialLineSelection(String var1, StringSplitter var2, int var3, int var4, int var5, int var6) {
      String var7 = var1.substring(var6, var3);
      String var8 = var1.substring(var6, var4);
      BookEditScreen.Pos2i var9 = new BookEditScreen.Pos2i((int)var2.stringWidth(var7), var5);
      BookEditScreen.Pos2i var10 = new BookEditScreen.Pos2i((int)var2.stringWidth(var8), var5 + 9);
      return this.createSelection(var9, var10);
   }

   private Rect2i createSelection(BookEditScreen.Pos2i var1, BookEditScreen.Pos2i var2) {
      BookEditScreen.Pos2i var3 = this.convertLocalToScreen(var1);
      BookEditScreen.Pos2i var4 = this.convertLocalToScreen(var2);
      int var5 = Math.min(var3.x, var4.x);
      int var6 = Math.max(var3.x, var4.x);
      int var7 = Math.min(var3.y, var4.y);
      int var8 = Math.max(var3.y, var4.y);
      return new Rect2i(var5, var7, var6 - var5, var8 - var7);
   }

   static class DisplayCache {
      static final BookEditScreen.DisplayCache EMPTY = new BookEditScreen.DisplayCache(
         "",
         new BookEditScreen.Pos2i(0, 0),
         true,
         new int[]{0},
         new BookEditScreen.LineInfo[]{new BookEditScreen.LineInfo(Style.EMPTY, "", 0, 0)},
         new Rect2i[0]
      );
      private final String fullText;
      final BookEditScreen.Pos2i cursor;
      final boolean cursorAtEnd;
      private final int[] lineStarts;
      final BookEditScreen.LineInfo[] lines;
      final Rect2i[] selection;

      public DisplayCache(String var1, BookEditScreen.Pos2i var2, boolean var3, int[] var4, BookEditScreen.LineInfo[] var5, Rect2i[] var6) {
         super();
         this.fullText = var1;
         this.cursor = var2;
         this.cursorAtEnd = var3;
         this.lineStarts = var4;
         this.lines = var5;
         this.selection = var6;
      }

      public int getIndexAtPosition(Font var1, BookEditScreen.Pos2i var2) {
         int var3 = var2.y / 9;
         if (var3 < 0) {
            return 0;
         } else if (var3 >= this.lines.length) {
            return this.fullText.length();
         } else {
            BookEditScreen.LineInfo var4 = this.lines[var3];
            return this.lineStarts[var3] + var1.getSplitter().plainIndexAtWidth(var4.contents, var2.x, var4.style);
         }
      }

      public int changeLine(int var1, int var2) {
         int var3 = BookEditScreen.findLineFromPos(this.lineStarts, var1);
         int var4 = var3 + var2;
         int var5;
         if (0 <= var4 && var4 < this.lineStarts.length) {
            int var6 = var1 - this.lineStarts[var3];
            int var7 = this.lines[var4].contents.length();
            var5 = this.lineStarts[var4] + Math.min(var6, var7);
         } else {
            var5 = var1;
         }

         return var5;
      }

      public int findLineStart(int var1) {
         int var2 = BookEditScreen.findLineFromPos(this.lineStarts, var1);
         return this.lineStarts[var2];
      }

      public int findLineEnd(int var1) {
         int var2 = BookEditScreen.findLineFromPos(this.lineStarts, var1);
         return this.lineStarts[var2] + this.lines[var2].contents.length();
      }
   }

   static class LineInfo {
      final Style style;
      final String contents;
      final Component asComponent;
      final int x;
      final int y;

      public LineInfo(Style var1, String var2, int var3, int var4) {
         super();
         this.style = var1;
         this.contents = var2;
         this.x = var3;
         this.y = var4;
         this.asComponent = Component.literal(var2).setStyle(var1);
      }
   }

   static class Pos2i {
      public final int x;
      public final int y;

      Pos2i(int var1, int var2) {
         super();
         this.x = var1;
         this.y = var2;
      }
   }
}
