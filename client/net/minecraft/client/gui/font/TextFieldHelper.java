package net.minecraft.client.gui.font;

import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.minecraft.ChatFormatting;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.StringSplitter;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.util.Mth;

public class TextFieldHelper {
   private final Supplier<String> getMessageFn;
   private final Consumer<String> setMessageFn;
   private final Supplier<String> getClipboardFn;
   private final Consumer<String> setClipboardFn;
   private final Predicate<String> stringValidator;
   private int cursorPos;
   private int selectionPos;

   public TextFieldHelper(Supplier<String> var1, Consumer<String> var2, Supplier<String> var3, Consumer<String> var4, Predicate<String> var5) {
      super();
      this.getMessageFn = var1;
      this.setMessageFn = var2;
      this.getClipboardFn = var3;
      this.setClipboardFn = var4;
      this.stringValidator = var5;
      this.setCursorToEnd();
   }

   public static Supplier<String> createClipboardGetter(Minecraft var0) {
      return () -> {
         return getClipboardContents(var0);
      };
   }

   public static String getClipboardContents(Minecraft var0) {
      return ChatFormatting.stripFormatting(var0.keyboardHandler.getClipboard().replaceAll("\\r", ""));
   }

   public static Consumer<String> createClipboardSetter(Minecraft var0) {
      return (var1) -> {
         setClipboardContents(var0, var1);
      };
   }

   public static void setClipboardContents(Minecraft var0, String var1) {
      var0.keyboardHandler.setClipboard(var1);
   }

   public boolean charTyped(char var1) {
      if (SharedConstants.isAllowedChatCharacter(var1)) {
         this.insertText((String)this.getMessageFn.get(), Character.toString(var1));
      }

      return true;
   }

   public boolean keyPressed(int var1) {
      if (Screen.isSelectAll(var1)) {
         this.selectAll();
         return true;
      } else if (Screen.isCopy(var1)) {
         this.copy();
         return true;
      } else if (Screen.isPaste(var1)) {
         this.paste();
         return true;
      } else if (Screen.isCut(var1)) {
         this.cut();
         return true;
      } else {
         CursorStep var2 = Screen.hasControlDown() ? TextFieldHelper.CursorStep.WORD : TextFieldHelper.CursorStep.CHARACTER;
         if (var1 == 259) {
            this.removeFromCursor(-1, var2);
            return true;
         } else {
            if (var1 == 261) {
               this.removeFromCursor(1, var2);
            } else {
               if (var1 == 263) {
                  this.moveBy(-1, Screen.hasShiftDown(), var2);
                  return true;
               }

               if (var1 == 262) {
                  this.moveBy(1, Screen.hasShiftDown(), var2);
                  return true;
               }

               if (var1 == 268) {
                  this.setCursorToStart(Screen.hasShiftDown());
                  return true;
               }

               if (var1 == 269) {
                  this.setCursorToEnd(Screen.hasShiftDown());
                  return true;
               }
            }

            return false;
         }
      }
   }

   private int clampToMsgLength(int var1) {
      return Mth.clamp((int)var1, (int)0, (int)((String)this.getMessageFn.get()).length());
   }

   private void insertText(String var1, String var2) {
      if (this.selectionPos != this.cursorPos) {
         var1 = this.deleteSelection(var1);
      }

      this.cursorPos = Mth.clamp((int)this.cursorPos, (int)0, (int)var1.length());
      String var3 = (new StringBuilder(var1)).insert(this.cursorPos, var2).toString();
      if (this.stringValidator.test(var3)) {
         this.setMessageFn.accept(var3);
         this.selectionPos = this.cursorPos = Math.min(var3.length(), this.cursorPos + var2.length());
      }

   }

   public void insertText(String var1) {
      this.insertText((String)this.getMessageFn.get(), var1);
   }

   private void resetSelectionIfNeeded(boolean var1) {
      if (!var1) {
         this.selectionPos = this.cursorPos;
      }

   }

   public void moveBy(int var1, boolean var2, CursorStep var3) {
      switch (var3) {
         case CHARACTER:
            this.moveByChars(var1, var2);
            break;
         case WORD:
            this.moveByWords(var1, var2);
      }

   }

   public void moveByChars(int var1) {
      this.moveByChars(var1, false);
   }

   public void moveByChars(int var1, boolean var2) {
      this.cursorPos = Util.offsetByCodepoints((String)this.getMessageFn.get(), this.cursorPos, var1);
      this.resetSelectionIfNeeded(var2);
   }

   public void moveByWords(int var1) {
      this.moveByWords(var1, false);
   }

   public void moveByWords(int var1, boolean var2) {
      this.cursorPos = StringSplitter.getWordPosition((String)this.getMessageFn.get(), var1, this.cursorPos, true);
      this.resetSelectionIfNeeded(var2);
   }

   public void removeFromCursor(int var1, CursorStep var2) {
      switch (var2) {
         case CHARACTER:
            this.removeCharsFromCursor(var1);
            break;
         case WORD:
            this.removeWordsFromCursor(var1);
      }

   }

   public void removeWordsFromCursor(int var1) {
      int var2 = StringSplitter.getWordPosition((String)this.getMessageFn.get(), var1, this.cursorPos, true);
      this.removeCharsFromCursor(var2 - this.cursorPos);
   }

   public void removeCharsFromCursor(int var1) {
      String var2 = (String)this.getMessageFn.get();
      if (!var2.isEmpty()) {
         String var3;
         if (this.selectionPos != this.cursorPos) {
            var3 = this.deleteSelection(var2);
         } else {
            int var4 = Util.offsetByCodepoints(var2, this.cursorPos, var1);
            int var5 = Math.min(var4, this.cursorPos);
            int var6 = Math.max(var4, this.cursorPos);
            var3 = (new StringBuilder(var2)).delete(var5, var6).toString();
            if (var1 < 0) {
               this.selectionPos = this.cursorPos = var5;
            }
         }

         this.setMessageFn.accept(var3);
      }

   }

   public void cut() {
      String var1 = (String)this.getMessageFn.get();
      this.setClipboardFn.accept(this.getSelected(var1));
      this.setMessageFn.accept(this.deleteSelection(var1));
   }

   public void paste() {
      this.insertText((String)this.getMessageFn.get(), (String)this.getClipboardFn.get());
      this.selectionPos = this.cursorPos;
   }

   public void copy() {
      this.setClipboardFn.accept(this.getSelected((String)this.getMessageFn.get()));
   }

   public void selectAll() {
      this.selectionPos = 0;
      this.cursorPos = ((String)this.getMessageFn.get()).length();
   }

   private String getSelected(String var1) {
      int var2 = Math.min(this.cursorPos, this.selectionPos);
      int var3 = Math.max(this.cursorPos, this.selectionPos);
      return var1.substring(var2, var3);
   }

   private String deleteSelection(String var1) {
      if (this.selectionPos == this.cursorPos) {
         return var1;
      } else {
         int var2 = Math.min(this.cursorPos, this.selectionPos);
         int var3 = Math.max(this.cursorPos, this.selectionPos);
         String var10000 = var1.substring(0, var2);
         String var4 = var10000 + var1.substring(var3);
         this.selectionPos = this.cursorPos = var2;
         return var4;
      }
   }

   public void setCursorToStart() {
      this.setCursorToStart(false);
   }

   public void setCursorToStart(boolean var1) {
      this.cursorPos = 0;
      this.resetSelectionIfNeeded(var1);
   }

   public void setCursorToEnd() {
      this.setCursorToEnd(false);
   }

   public void setCursorToEnd(boolean var1) {
      this.cursorPos = ((String)this.getMessageFn.get()).length();
      this.resetSelectionIfNeeded(var1);
   }

   public int getCursorPos() {
      return this.cursorPos;
   }

   public void setCursorPos(int var1) {
      this.setCursorPos(var1, true);
   }

   public void setCursorPos(int var1, boolean var2) {
      this.cursorPos = this.clampToMsgLength(var1);
      this.resetSelectionIfNeeded(var2);
   }

   public int getSelectionPos() {
      return this.selectionPos;
   }

   public void setSelectionPos(int var1) {
      this.selectionPos = this.clampToMsgLength(var1);
   }

   public void setSelectionRange(int var1, int var2) {
      int var3 = ((String)this.getMessageFn.get()).length();
      this.cursorPos = Mth.clamp((int)var1, (int)0, (int)var3);
      this.selectionPos = Mth.clamp((int)var2, (int)0, (int)var3);
   }

   public boolean isSelecting() {
      return this.cursorPos != this.selectionPos;
   }

   public static enum CursorStep {
      CHARACTER,
      WORD;

      private CursorStep() {
      }

      // $FF: synthetic method
      private static CursorStep[] $values() {
         return new CursorStep[]{CHARACTER, WORD};
      }
   }
}
