package net.minecraft.client.gui.font;

import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.ChatFormatting;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.util.Mth;

public class TextFieldHelper {
   private final Minecraft minecraft;
   private final Font font;
   private final Supplier getMessageFn;
   private final Consumer setMessageFn;
   private final int maxWidth;
   private int cursorPos;
   private int selectionPos;

   public TextFieldHelper(Minecraft var1, Supplier var2, Consumer var3, int var4) {
      this.minecraft = var1;
      this.font = var1.font;
      this.getMessageFn = var2;
      this.setMessageFn = var3;
      this.maxWidth = var4;
      this.setEnd();
   }

   public boolean charTyped(char var1) {
      if (SharedConstants.isAllowedChatCharacter(var1)) {
         this.insertText(Character.toString(var1));
      }

      return true;
   }

   private void insertText(String var1) {
      if (this.selectionPos != this.cursorPos) {
         this.deleteSelection();
      }

      String var2 = (String)this.getMessageFn.get();
      this.cursorPos = Mth.clamp(this.cursorPos, 0, var2.length());
      String var3 = (new StringBuilder(var2)).insert(this.cursorPos, var1).toString();
      if (this.font.width(var3) <= this.maxWidth) {
         this.setMessageFn.accept(var3);
         this.selectionPos = this.cursorPos = Math.min(var3.length(), this.cursorPos + var1.length());
      }

   }

   public boolean keyPressed(int var1) {
      String var2 = (String)this.getMessageFn.get();
      if (Screen.isSelectAll(var1)) {
         this.selectionPos = 0;
         this.cursorPos = var2.length();
         return true;
      } else if (Screen.isCopy(var1)) {
         this.minecraft.keyboardHandler.setClipboard(this.getSelected());
         return true;
      } else if (Screen.isPaste(var1)) {
         this.insertText(SharedConstants.filterText(ChatFormatting.stripFormatting(this.minecraft.keyboardHandler.getClipboard().replaceAll("\\r", ""))));
         this.selectionPos = this.cursorPos;
         return true;
      } else if (Screen.isCut(var1)) {
         this.minecraft.keyboardHandler.setClipboard(this.getSelected());
         this.deleteSelection();
         return true;
      } else if (var1 == 259) {
         if (!var2.isEmpty()) {
            if (this.selectionPos != this.cursorPos) {
               this.deleteSelection();
            } else if (this.cursorPos > 0) {
               var2 = (new StringBuilder(var2)).deleteCharAt(Math.max(0, this.cursorPos - 1)).toString();
               this.selectionPos = this.cursorPos = Math.max(0, this.cursorPos - 1);
               this.setMessageFn.accept(var2);
            }
         }

         return true;
      } else if (var1 == 261) {
         if (!var2.isEmpty()) {
            if (this.selectionPos != this.cursorPos) {
               this.deleteSelection();
            } else if (this.cursorPos < var2.length()) {
               var2 = (new StringBuilder(var2)).deleteCharAt(Math.max(0, this.cursorPos)).toString();
               this.setMessageFn.accept(var2);
            }
         }

         return true;
      } else {
         int var3;
         if (var1 == 263) {
            var3 = this.font.isBidirectional() ? 1 : -1;
            if (Screen.hasControlDown()) {
               this.cursorPos = this.font.getWordPosition(var2, var3, this.cursorPos, true);
            } else {
               this.cursorPos = Math.max(0, Math.min(var2.length(), this.cursorPos + var3));
            }

            if (!Screen.hasShiftDown()) {
               this.selectionPos = this.cursorPos;
            }

            return true;
         } else if (var1 == 262) {
            var3 = this.font.isBidirectional() ? -1 : 1;
            if (Screen.hasControlDown()) {
               this.cursorPos = this.font.getWordPosition(var2, var3, this.cursorPos, true);
            } else {
               this.cursorPos = Math.max(0, Math.min(var2.length(), this.cursorPos + var3));
            }

            if (!Screen.hasShiftDown()) {
               this.selectionPos = this.cursorPos;
            }

            return true;
         } else if (var1 == 268) {
            this.cursorPos = 0;
            if (!Screen.hasShiftDown()) {
               this.selectionPos = this.cursorPos;
            }

            return true;
         } else if (var1 == 269) {
            this.cursorPos = ((String)this.getMessageFn.get()).length();
            if (!Screen.hasShiftDown()) {
               this.selectionPos = this.cursorPos;
            }

            return true;
         } else {
            return false;
         }
      }
   }

   private String getSelected() {
      String var1 = (String)this.getMessageFn.get();
      int var2 = Math.min(this.cursorPos, this.selectionPos);
      int var3 = Math.max(this.cursorPos, this.selectionPos);
      return var1.substring(var2, var3);
   }

   private void deleteSelection() {
      if (this.selectionPos != this.cursorPos) {
         String var1 = (String)this.getMessageFn.get();
         int var2 = Math.min(this.cursorPos, this.selectionPos);
         int var3 = Math.max(this.cursorPos, this.selectionPos);
         String var4 = var1.substring(0, var2) + var1.substring(var3);
         this.cursorPos = var2;
         this.selectionPos = this.cursorPos;
         this.setMessageFn.accept(var4);
      }
   }

   public void setEnd() {
      this.selectionPos = this.cursorPos = ((String)this.getMessageFn.get()).length();
   }

   public int getCursorPos() {
      return this.cursorPos;
   }

   public int getSelectionPos() {
      return this.selectionPos;
   }
}
