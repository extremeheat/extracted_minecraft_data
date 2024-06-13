package net.minecraft.client.gui.components;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Style;
import net.minecraft.util.Mth;
import net.minecraft.util.StringUtil;

public class MultilineTextField {
   public static final int NO_CHARACTER_LIMIT = 2147483647;
   private static final int LINE_SEEK_PIXEL_BIAS = 2;
   private final Font font;
   private final List<MultilineTextField.StringView> displayLines = Lists.newArrayList();
   private String value;
   private int cursor;
   private int selectCursor;
   private boolean selecting;
   private int characterLimit = 2147483647;
   private final int width;
   private Consumer<String> valueListener = var0 -> {
   };
   private Runnable cursorListener = () -> {
   };

   public MultilineTextField(Font var1, int var2) {
      super();
      this.font = var1;
      this.width = var2;
      this.setValue("");
   }

   public int characterLimit() {
      return this.characterLimit;
   }

   public void setCharacterLimit(int var1) {
      if (var1 < 0) {
         throw new IllegalArgumentException("Character limit cannot be negative");
      } else {
         this.characterLimit = var1;
      }
   }

   public boolean hasCharacterLimit() {
      return this.characterLimit != 2147483647;
   }

   public void setValueListener(Consumer<String> var1) {
      this.valueListener = var1;
   }

   public void setCursorListener(Runnable var1) {
      this.cursorListener = var1;
   }

   public void setValue(String var1) {
      this.value = this.truncateFullText(var1);
      this.cursor = this.value.length();
      this.selectCursor = this.cursor;
      this.onValueChange();
   }

   public String value() {
      return this.value;
   }

   public void insertText(String var1) {
      if (!var1.isEmpty() || this.hasSelection()) {
         String var2 = this.truncateInsertionText(StringUtil.filterText(var1, true));
         MultilineTextField.StringView var3 = this.getSelected();
         this.value = new StringBuilder(this.value).replace(var3.beginIndex, var3.endIndex, var2).toString();
         this.cursor = var3.beginIndex + var2.length();
         this.selectCursor = this.cursor;
         this.onValueChange();
      }
   }

   public void deleteText(int var1) {
      if (!this.hasSelection()) {
         this.selectCursor = Mth.clamp(this.cursor + var1, 0, this.value.length());
      }

      this.insertText("");
   }

   public int cursor() {
      return this.cursor;
   }

   public void setSelecting(boolean var1) {
      this.selecting = var1;
   }

   public MultilineTextField.StringView getSelected() {
      return new MultilineTextField.StringView(Math.min(this.selectCursor, this.cursor), Math.max(this.selectCursor, this.cursor));
   }

   public int getLineCount() {
      return this.displayLines.size();
   }

   public int getLineAtCursor() {
      for (int var1 = 0; var1 < this.displayLines.size(); var1++) {
         MultilineTextField.StringView var2 = this.displayLines.get(var1);
         if (this.cursor >= var2.beginIndex && this.cursor <= var2.endIndex) {
            return var1;
         }
      }

      return -1;
   }

   public MultilineTextField.StringView getLineView(int var1) {
      return this.displayLines.get(Mth.clamp(var1, 0, this.displayLines.size() - 1));
   }

   public void seekCursor(Whence var1, int var2) {
      switch (var1) {
         case ABSOLUTE:
            this.cursor = var2;
            break;
         case RELATIVE:
            this.cursor += var2;
            break;
         case END:
            this.cursor = this.value.length() + var2;
      }

      this.cursor = Mth.clamp(this.cursor, 0, this.value.length());
      this.cursorListener.run();
      if (!this.selecting) {
         this.selectCursor = this.cursor;
      }
   }

   public void seekCursorLine(int var1) {
      if (var1 != 0) {
         int var2 = this.font.width(this.value.substring(this.getCursorLineView().beginIndex, this.cursor)) + 2;
         MultilineTextField.StringView var3 = this.getCursorLineView(var1);
         int var4 = this.font.plainSubstrByWidth(this.value.substring(var3.beginIndex, var3.endIndex), var2).length();
         this.seekCursor(Whence.ABSOLUTE, var3.beginIndex + var4);
      }
   }

   public void seekCursorToPoint(double var1, double var3) {
      int var5 = Mth.floor(var1);
      int var6 = Mth.floor(var3 / 9.0);
      MultilineTextField.StringView var7 = this.displayLines.get(Mth.clamp(var6, 0, this.displayLines.size() - 1));
      int var8 = this.font.plainSubstrByWidth(this.value.substring(var7.beginIndex, var7.endIndex), var5).length();
      this.seekCursor(Whence.ABSOLUTE, var7.beginIndex + var8);
   }

   public boolean keyPressed(int var1) {
      this.selecting = Screen.hasShiftDown();
      if (Screen.isSelectAll(var1)) {
         this.cursor = this.value.length();
         this.selectCursor = 0;
         return true;
      } else if (Screen.isCopy(var1)) {
         Minecraft.getInstance().keyboardHandler.setClipboard(this.getSelectedText());
         return true;
      } else if (Screen.isPaste(var1)) {
         this.insertText(Minecraft.getInstance().keyboardHandler.getClipboard());
         return true;
      } else if (Screen.isCut(var1)) {
         Minecraft.getInstance().keyboardHandler.setClipboard(this.getSelectedText());
         this.insertText("");
         return true;
      } else {
         switch (var1) {
            case 257:
            case 335:
               this.insertText("\n");
               return true;
            case 259:
               if (Screen.hasControlDown()) {
                  MultilineTextField.StringView var5 = this.getPreviousWord();
                  this.deleteText(var5.beginIndex - this.cursor);
               } else {
                  this.deleteText(-1);
               }

               return true;
            case 261:
               if (Screen.hasControlDown()) {
                  MultilineTextField.StringView var4 = this.getNextWord();
                  this.deleteText(var4.beginIndex - this.cursor);
               } else {
                  this.deleteText(1);
               }

               return true;
            case 262:
               if (Screen.hasControlDown()) {
                  MultilineTextField.StringView var3 = this.getNextWord();
                  this.seekCursor(Whence.ABSOLUTE, var3.beginIndex);
               } else {
                  this.seekCursor(Whence.RELATIVE, 1);
               }

               return true;
            case 263:
               if (Screen.hasControlDown()) {
                  MultilineTextField.StringView var2 = this.getPreviousWord();
                  this.seekCursor(Whence.ABSOLUTE, var2.beginIndex);
               } else {
                  this.seekCursor(Whence.RELATIVE, -1);
               }

               return true;
            case 264:
               if (!Screen.hasControlDown()) {
                  this.seekCursorLine(1);
               }

               return true;
            case 265:
               if (!Screen.hasControlDown()) {
                  this.seekCursorLine(-1);
               }

               return true;
            case 266:
               this.seekCursor(Whence.ABSOLUTE, 0);
               return true;
            case 267:
               this.seekCursor(Whence.END, 0);
               return true;
            case 268:
               if (Screen.hasControlDown()) {
                  this.seekCursor(Whence.ABSOLUTE, 0);
               } else {
                  this.seekCursor(Whence.ABSOLUTE, this.getCursorLineView().beginIndex);
               }

               return true;
            case 269:
               if (Screen.hasControlDown()) {
                  this.seekCursor(Whence.END, 0);
               } else {
                  this.seekCursor(Whence.ABSOLUTE, this.getCursorLineView().endIndex);
               }

               return true;
            default:
               return false;
         }
      }
   }

   public Iterable<MultilineTextField.StringView> iterateLines() {
      return this.displayLines;
   }

   public boolean hasSelection() {
      return this.selectCursor != this.cursor;
   }

   @VisibleForTesting
   public String getSelectedText() {
      MultilineTextField.StringView var1 = this.getSelected();
      return this.value.substring(var1.beginIndex, var1.endIndex);
   }

   private MultilineTextField.StringView getCursorLineView() {
      return this.getCursorLineView(0);
   }

   private MultilineTextField.StringView getCursorLineView(int var1) {
      int var2 = this.getLineAtCursor();
      if (var2 < 0) {
         throw new IllegalStateException("Cursor is not within text (cursor = " + this.cursor + ", length = " + this.value.length() + ")");
      } else {
         return this.displayLines.get(Mth.clamp(var2 + var1, 0, this.displayLines.size() - 1));
      }
   }

   @VisibleForTesting
   public MultilineTextField.StringView getPreviousWord() {
      if (this.value.isEmpty()) {
         return MultilineTextField.StringView.EMPTY;
      } else {
         int var1 = Mth.clamp(this.cursor, 0, this.value.length() - 1);

         while (var1 > 0 && Character.isWhitespace(this.value.charAt(var1 - 1))) {
            var1--;
         }

         while (var1 > 0 && !Character.isWhitespace(this.value.charAt(var1 - 1))) {
            var1--;
         }

         return new MultilineTextField.StringView(var1, this.getWordEndPosition(var1));
      }
   }

   @VisibleForTesting
   public MultilineTextField.StringView getNextWord() {
      if (this.value.isEmpty()) {
         return MultilineTextField.StringView.EMPTY;
      } else {
         int var1 = Mth.clamp(this.cursor, 0, this.value.length() - 1);

         while (var1 < this.value.length() && !Character.isWhitespace(this.value.charAt(var1))) {
            var1++;
         }

         while (var1 < this.value.length() && Character.isWhitespace(this.value.charAt(var1))) {
            var1++;
         }

         return new MultilineTextField.StringView(var1, this.getWordEndPosition(var1));
      }
   }

   private int getWordEndPosition(int var1) {
      int var2 = var1;

      while (var2 < this.value.length() && !Character.isWhitespace(this.value.charAt(var2))) {
         var2++;
      }

      return var2;
   }

   private void onValueChange() {
      this.reflowDisplayLines();
      this.valueListener.accept(this.value);
      this.cursorListener.run();
   }

   private void reflowDisplayLines() {
      this.displayLines.clear();
      if (this.value.isEmpty()) {
         this.displayLines.add(MultilineTextField.StringView.EMPTY);
      } else {
         this.font
            .getSplitter()
            .splitLines(this.value, this.width, Style.EMPTY, false, (var1, var2, var3) -> this.displayLines.add(new MultilineTextField.StringView(var2, var3)));
         if (this.value.charAt(this.value.length() - 1) == '\n') {
            this.displayLines.add(new MultilineTextField.StringView(this.value.length(), this.value.length()));
         }
      }
   }

   private String truncateFullText(String var1) {
      return this.hasCharacterLimit() ? StringUtil.truncateStringIfNecessary(var1, this.characterLimit, false) : var1;
   }

   private String truncateInsertionText(String var1) {
      if (this.hasCharacterLimit()) {
         int var2 = this.characterLimit - this.value.length();
         return StringUtil.truncateStringIfNecessary(var1, var2, false);
      } else {
         return var1;
      }
   }

   protected static record StringView(int beginIndex, int endIndex) {
      static final MultilineTextField.StringView EMPTY = new MultilineTextField.StringView(0, 0);

      protected StringView(int beginIndex, int endIndex) {
         super();
         this.beginIndex = beginIndex;
         this.endIndex = endIndex;
      }
   }
}
