package net.minecraft.client.gui.screens.inventory;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import java.util.List;
import java.util.ListIterator;
import net.minecraft.ChatFormatting;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundEditBookPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class BookEditScreen extends Screen {
   private final Player owner;
   private final ItemStack book;
   private boolean isModified;
   private boolean isSigning;
   private int frameTick;
   private int currentPage;
   private final List pages = Lists.newArrayList();
   private String title = "";
   private int cursorPos;
   private int selectionPos;
   private long lastClickTime;
   private int lastIndex = -1;
   private PageButton forwardButton;
   private PageButton backButton;
   private Button doneButton;
   private Button signButton;
   private Button finalizeButton;
   private Button cancelButton;
   private final InteractionHand hand;

   public BookEditScreen(Player var1, ItemStack var2, InteractionHand var3) {
      super(NarratorChatListener.NO_TITLE);
      this.owner = var1;
      this.book = var2;
      this.hand = var3;
      CompoundTag var4 = var2.getTag();
      if (var4 != null) {
         ListTag var5 = var4.getList("pages", 8).copy();

         for(int var6 = 0; var6 < var5.size(); ++var6) {
            this.pages.add(var5.getString(var6));
         }
      }

      if (this.pages.isEmpty()) {
         this.pages.add("");
      }

   }

   private int getNumPages() {
      return this.pages.size();
   }

   public void tick() {
      super.tick();
      ++this.frameTick;
   }

   protected void init() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
      this.signButton = (Button)this.addButton(new Button(this.width / 2 - 100, 196, 98, 20, I18n.get("book.signButton"), (var1x) -> {
         this.isSigning = true;
         this.updateButtonVisibility();
      }));
      this.doneButton = (Button)this.addButton(new Button(this.width / 2 + 2, 196, 98, 20, I18n.get("gui.done"), (var1x) -> {
         this.minecraft.setScreen((Screen)null);
         this.saveChanges(false);
      }));
      this.finalizeButton = (Button)this.addButton(new Button(this.width / 2 - 100, 196, 98, 20, I18n.get("book.finalizeButton"), (var1x) -> {
         if (this.isSigning) {
            this.saveChanges(true);
            this.minecraft.setScreen((Screen)null);
         }

      }));
      this.cancelButton = (Button)this.addButton(new Button(this.width / 2 + 2, 196, 98, 20, I18n.get("gui.cancel"), (var1x) -> {
         if (this.isSigning) {
            this.isSigning = false;
         }

         this.updateButtonVisibility();
      }));
      int var1 = (this.width - 192) / 2;
      boolean var2 = true;
      this.forwardButton = (PageButton)this.addButton(new PageButton(var1 + 116, 159, true, (var1x) -> {
         this.pageForward();
      }, true));
      this.backButton = (PageButton)this.addButton(new PageButton(var1 + 43, 159, false, (var1x) -> {
         this.pageBack();
      }, true));
      this.updateButtonVisibility();
   }

   private String filterText(String var1) {
      StringBuilder var2 = new StringBuilder();
      char[] var3 = var1.toCharArray();
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         char var6 = var3[var5];
         if (var6 != 167 && var6 != 127) {
            var2.append(var6);
         }
      }

      return var2.toString();
   }

   private void pageBack() {
      if (this.currentPage > 0) {
         --this.currentPage;
         this.cursorPos = 0;
         this.selectionPos = this.cursorPos;
      }

      this.updateButtonVisibility();
   }

   private void pageForward() {
      if (this.currentPage < this.getNumPages() - 1) {
         ++this.currentPage;
         this.cursorPos = 0;
         this.selectionPos = this.cursorPos;
      } else {
         this.appendPageToBook();
         if (this.currentPage < this.getNumPages() - 1) {
            ++this.currentPage;
         }

         this.cursorPos = 0;
         this.selectionPos = this.cursorPos;
      }

      this.updateButtonVisibility();
   }

   public void removed() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
   }

   private void updateButtonVisibility() {
      this.backButton.visible = !this.isSigning && this.currentPage > 0;
      this.forwardButton.visible = !this.isSigning;
      this.doneButton.visible = !this.isSigning;
      this.signButton.visible = !this.isSigning;
      this.cancelButton.visible = this.isSigning;
      this.finalizeButton.visible = this.isSigning;
      this.finalizeButton.active = !this.title.trim().isEmpty();
   }

   private void eraseEmptyTrailingPages() {
      ListIterator var1 = this.pages.listIterator(this.pages.size());

      while(var1.hasPrevious() && ((String)var1.previous()).isEmpty()) {
         var1.remove();
      }

   }

   private void saveChanges(boolean var1) {
      if (this.isModified) {
         this.eraseEmptyTrailingPages();
         ListTag var2 = new ListTag();
         this.pages.stream().map(StringTag::valueOf).forEach(var2::add);
         if (!this.pages.isEmpty()) {
            this.book.addTagElement("pages", var2);
         }

         if (var1) {
            this.book.addTagElement("author", StringTag.valueOf(this.owner.getGameProfile().getName()));
            this.book.addTagElement("title", StringTag.valueOf(this.title.trim()));
         }

         this.minecraft.getConnection().send((Packet)(new ServerboundEditBookPacket(this.book, var1, this.hand)));
      }
   }

   private void appendPageToBook() {
      if (this.getNumPages() < 100) {
         this.pages.add("");
         this.isModified = true;
      }
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      if (super.keyPressed(var1, var2, var3)) {
         return true;
      } else {
         return this.isSigning ? this.titleKeyPressed(var1, var2, var3) : this.bookKeyPressed(var1, var2, var3);
      }
   }

   public boolean charTyped(char var1, int var2) {
      if (super.charTyped(var1, var2)) {
         return true;
      } else if (this.isSigning) {
         if (this.title.length() < 16 && SharedConstants.isAllowedChatCharacter(var1)) {
            this.title = this.title + Character.toString(var1);
            this.updateButtonVisibility();
            this.isModified = true;
            return true;
         } else {
            return false;
         }
      } else if (SharedConstants.isAllowedChatCharacter(var1)) {
         this.insertText(Character.toString(var1));
         return true;
      } else {
         return false;
      }
   }

   private boolean bookKeyPressed(int var1, int var2, int var3) {
      String var4 = this.getCurrentPageText();
      if (Screen.isSelectAll(var1)) {
         this.selectionPos = 0;
         this.cursorPos = var4.length();
         return true;
      } else if (Screen.isCopy(var1)) {
         this.minecraft.keyboardHandler.setClipboard(this.getSelected());
         return true;
      } else if (Screen.isPaste(var1)) {
         this.insertText(this.filterText(ChatFormatting.stripFormatting(this.minecraft.keyboardHandler.getClipboard().replaceAll("\\r", ""))));
         this.selectionPos = this.cursorPos;
         return true;
      } else if (Screen.isCut(var1)) {
         this.minecraft.keyboardHandler.setClipboard(this.getSelected());
         this.deleteSelection();
         return true;
      } else {
         switch(var1) {
         case 257:
         case 335:
            this.insertText("\n");
            return true;
         case 259:
            this.keyBackspace(var4);
            return true;
         case 261:
            this.keyDelete(var4);
            return true;
         case 262:
            this.keyRight(var4);
            return true;
         case 263:
            this.keyLeft(var4);
            return true;
         case 264:
            this.keyDown(var4);
            return true;
         case 265:
            this.keyUp(var4);
            return true;
         case 266:
            this.backButton.onPress();
            return true;
         case 267:
            this.forwardButton.onPress();
            return true;
         case 268:
            this.keyHome(var4);
            return true;
         case 269:
            this.keyEnd(var4);
            return true;
         default:
            return false;
         }
      }
   }

   private void keyBackspace(String var1) {
      if (!var1.isEmpty()) {
         if (this.selectionPos != this.cursorPos) {
            this.deleteSelection();
         } else if (this.cursorPos > 0) {
            String var2 = (new StringBuilder(var1)).deleteCharAt(Math.max(0, this.cursorPos - 1)).toString();
            this.setCurrentPageText(var2);
            this.cursorPos = Math.max(0, this.cursorPos - 1);
            this.selectionPos = this.cursorPos;
         }
      }

   }

   private void keyDelete(String var1) {
      if (!var1.isEmpty()) {
         if (this.selectionPos != this.cursorPos) {
            this.deleteSelection();
         } else if (this.cursorPos < var1.length()) {
            String var2 = (new StringBuilder(var1)).deleteCharAt(Math.max(0, this.cursorPos)).toString();
            this.setCurrentPageText(var2);
         }
      }

   }

   private void keyLeft(String var1) {
      int var2 = this.font.isBidirectional() ? 1 : -1;
      if (Screen.hasControlDown()) {
         this.cursorPos = this.font.getWordPosition(var1, var2, this.cursorPos, true);
      } else {
         this.cursorPos = Math.max(0, this.cursorPos + var2);
      }

      if (!Screen.hasShiftDown()) {
         this.selectionPos = this.cursorPos;
      }

   }

   private void keyRight(String var1) {
      int var2 = this.font.isBidirectional() ? -1 : 1;
      if (Screen.hasControlDown()) {
         this.cursorPos = this.font.getWordPosition(var1, var2, this.cursorPos, true);
      } else {
         this.cursorPos = Math.min(var1.length(), this.cursorPos + var2);
      }

      if (!Screen.hasShiftDown()) {
         this.selectionPos = this.cursorPos;
      }

   }

   private void keyUp(String var1) {
      if (!var1.isEmpty()) {
         BookEditScreen.Pos2i var2 = this.getPositionAtIndex(var1, this.cursorPos);
         if (var2.y == 0) {
            this.cursorPos = 0;
            if (!Screen.hasShiftDown()) {
               this.selectionPos = this.cursorPos;
            }
         } else {
            int var10005 = var2.x + this.getWidthAt(var1, this.cursorPos) / 3;
            int var10006 = var2.y;
            this.font.getClass();
            int var3 = this.getIndexAtPosition(var1, new BookEditScreen.Pos2i(var10005, var10006 - 9));
            if (var3 >= 0) {
               this.cursorPos = var3;
               if (!Screen.hasShiftDown()) {
                  this.selectionPos = this.cursorPos;
               }
            }
         }
      }

   }

   private void keyDown(String var1) {
      if (!var1.isEmpty()) {
         BookEditScreen.Pos2i var2 = this.getPositionAtIndex(var1, this.cursorPos);
         int var3 = this.font.wordWrapHeight(var1 + "" + ChatFormatting.BLACK + "_", 114);
         int var10000 = var2.y;
         this.font.getClass();
         if (var10000 + 9 == var3) {
            this.cursorPos = var1.length();
            if (!Screen.hasShiftDown()) {
               this.selectionPos = this.cursorPos;
            }
         } else {
            int var10005 = var2.x + this.getWidthAt(var1, this.cursorPos) / 3;
            int var10006 = var2.y;
            this.font.getClass();
            int var4 = this.getIndexAtPosition(var1, new BookEditScreen.Pos2i(var10005, var10006 + 9));
            if (var4 >= 0) {
               this.cursorPos = var4;
               if (!Screen.hasShiftDown()) {
                  this.selectionPos = this.cursorPos;
               }
            }
         }
      }

   }

   private void keyHome(String var1) {
      this.cursorPos = this.getIndexAtPosition(var1, new BookEditScreen.Pos2i(0, this.getPositionAtIndex(var1, this.cursorPos).y));
      if (!Screen.hasShiftDown()) {
         this.selectionPos = this.cursorPos;
      }

   }

   private void keyEnd(String var1) {
      this.cursorPos = this.getIndexAtPosition(var1, new BookEditScreen.Pos2i(113, this.getPositionAtIndex(var1, this.cursorPos).y));
      if (!Screen.hasShiftDown()) {
         this.selectionPos = this.cursorPos;
      }

   }

   private void deleteSelection() {
      if (this.selectionPos != this.cursorPos) {
         String var1 = this.getCurrentPageText();
         int var2 = Math.min(this.cursorPos, this.selectionPos);
         int var3 = Math.max(this.cursorPos, this.selectionPos);
         String var4 = var1.substring(0, var2) + var1.substring(var3);
         this.cursorPos = var2;
         this.selectionPos = this.cursorPos;
         this.setCurrentPageText(var4);
      }
   }

   private int getWidthAt(String var1, int var2) {
      return (int)this.font.charWidth(var1.charAt(Mth.clamp(var2, 0, var1.length() - 1)));
   }

   private boolean titleKeyPressed(int var1, int var2, int var3) {
      switch(var1) {
      case 257:
      case 335:
         if (!this.title.isEmpty()) {
            this.saveChanges(true);
            this.minecraft.setScreen((Screen)null);
         }

         return true;
      case 259:
         if (!this.title.isEmpty()) {
            this.title = this.title.substring(0, this.title.length() - 1);
            this.updateButtonVisibility();
         }

         return true;
      default:
         return false;
      }
   }

   private String getCurrentPageText() {
      return this.currentPage >= 0 && this.currentPage < this.pages.size() ? (String)this.pages.get(this.currentPage) : "";
   }

   private void setCurrentPageText(String var1) {
      if (this.currentPage >= 0 && this.currentPage < this.pages.size()) {
         this.pages.set(this.currentPage, var1);
         this.isModified = true;
      }

   }

   private void insertText(String var1) {
      if (this.selectionPos != this.cursorPos) {
         this.deleteSelection();
      }

      String var2 = this.getCurrentPageText();
      this.cursorPos = Mth.clamp(this.cursorPos, 0, var2.length());
      String var3 = (new StringBuilder(var2)).insert(this.cursorPos, var1).toString();
      int var4 = this.font.wordWrapHeight(var3 + "" + ChatFormatting.BLACK + "_", 114);
      if (var4 <= 128 && var3.length() < 1024) {
         this.setCurrentPageText(var3);
         this.selectionPos = this.cursorPos = Math.min(this.getCurrentPageText().length(), this.cursorPos + var1.length());
      }

   }

   public void render(int var1, int var2, float var3) {
      this.renderBackground();
      this.setFocused((GuiEventListener)null);
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.minecraft.getTextureManager().bind(BookViewScreen.BOOK_LOCATION);
      int var4 = (this.width - 192) / 2;
      boolean var5 = true;
      this.blit(var4, 2, 0, 0, 192, 192);
      String var6;
      String var7;
      int var8;
      if (this.isSigning) {
         var6 = this.title;
         if (this.frameTick / 6 % 2 == 0) {
            var6 = var6 + "" + ChatFormatting.BLACK + "_";
         } else {
            var6 = var6 + "" + ChatFormatting.GRAY + "_";
         }

         var7 = I18n.get("book.editTitle");
         var8 = this.strWidth(var7);
         this.font.draw(var7, (float)(var4 + 36 + (114 - var8) / 2), 34.0F, 0);
         int var9 = this.strWidth(var6);
         this.font.draw(var6, (float)(var4 + 36 + (114 - var9) / 2), 50.0F, 0);
         String var10 = I18n.get("book.byAuthor", this.owner.getName().getString());
         int var11 = this.strWidth(var10);
         this.font.draw(ChatFormatting.DARK_GRAY + var10, (float)(var4 + 36 + (114 - var11) / 2), 60.0F, 0);
         String var12 = I18n.get("book.finalizeWarning");
         this.font.drawWordWrap(var12, var4 + 36, 82, 114, 0);
      } else {
         var6 = I18n.get("book.pageIndicator", this.currentPage + 1, this.getNumPages());
         var7 = this.getCurrentPageText();
         var8 = this.strWidth(var6);
         this.font.draw(var6, (float)(var4 - var8 + 192 - 44), 18.0F, 0);
         this.font.drawWordWrap(var7, var4 + 36, 32, 114, 0);
         this.renderSelection(var7);
         if (this.frameTick / 6 % 2 == 0) {
            BookEditScreen.Pos2i var13 = this.getPositionAtIndex(var7, this.cursorPos);
            if (this.font.isBidirectional()) {
               this.handleBidi(var13);
               var13.x = var13.x - 4;
            }

            this.convertLocalToScreen(var13);
            if (this.cursorPos < var7.length()) {
               int var10000 = var13.x;
               int var10001 = var13.y - 1;
               int var10002 = var13.x + 1;
               int var10003 = var13.y;
               this.font.getClass();
               GuiComponent.fill(var10000, var10001, var10002, var10003 + 9, -16777216);
            } else {
               this.font.draw("_", (float)var13.x, (float)var13.y, 0);
            }
         }
      }

      super.render(var1, var2, var3);
   }

   private int strWidth(String var1) {
      return this.font.width(this.font.isBidirectional() ? this.font.bidirectionalShaping(var1) : var1);
   }

   private int strIndexAtWidth(String var1, int var2) {
      return this.font.indexAtWidth(var1, var2);
   }

   private String getSelected() {
      String var1 = this.getCurrentPageText();
      int var2 = Math.min(this.cursorPos, this.selectionPos);
      int var3 = Math.max(this.cursorPos, this.selectionPos);
      return var1.substring(var2, var3);
   }

   private void renderSelection(String var1) {
      if (this.selectionPos != this.cursorPos) {
         int var2 = Math.min(this.cursorPos, this.selectionPos);
         int var3 = Math.max(this.cursorPos, this.selectionPos);
         String var4 = var1.substring(var2, var3);
         int var5 = this.font.getWordPosition(var1, 1, var3, true);
         String var6 = var1.substring(var2, var5);
         BookEditScreen.Pos2i var7 = this.getPositionAtIndex(var1, var2);
         int var10003 = var7.x;
         int var10004 = var7.y;
         this.font.getClass();
         BookEditScreen.Pos2i var8 = new BookEditScreen.Pos2i(var10003, var10004 + 9);

         while(!var4.isEmpty()) {
            int var9 = this.strIndexAtWidth(var6, 114 - var7.x);
            if (var4.length() <= var9) {
               var8.x = var7.x + this.strWidth(var4);
               this.renderHighlight(var7, var8);
               break;
            }

            var9 = Math.min(var9, var4.length() - 1);
            String var10 = var4.substring(0, var9);
            char var11 = var4.charAt(var9);
            boolean var12 = var11 == ' ' || var11 == '\n';
            var4 = ChatFormatting.getLastColors(var10) + var4.substring(var9 + (var12 ? 1 : 0));
            var6 = ChatFormatting.getLastColors(var10) + var6.substring(var9 + (var12 ? 1 : 0));
            var8.x = var7.x + this.strWidth(var10 + " ");
            this.renderHighlight(var7, var8);
            var7.x = 0;
            int var10001 = var7.y;
            this.font.getClass();
            var7.y = var10001 + 9;
            var10001 = var8.y;
            this.font.getClass();
            var8.y = var10001 + 9;
         }

      }
   }

   private void renderHighlight(BookEditScreen.Pos2i var1, BookEditScreen.Pos2i var2) {
      BookEditScreen.Pos2i var3 = new BookEditScreen.Pos2i(var1.x, var1.y);
      BookEditScreen.Pos2i var4 = new BookEditScreen.Pos2i(var2.x, var2.y);
      if (this.font.isBidirectional()) {
         this.handleBidi(var3);
         this.handleBidi(var4);
         int var5 = var4.x;
         var4.x = var3.x;
         var3.x = var5;
      }

      this.convertLocalToScreen(var3);
      this.convertLocalToScreen(var4);
      Tesselator var7 = Tesselator.getInstance();
      BufferBuilder var6 = var7.getBuilder();
      RenderSystem.color4f(0.0F, 0.0F, 255.0F, 255.0F);
      RenderSystem.disableTexture();
      RenderSystem.enableColorLogicOp();
      RenderSystem.logicOp(GlStateManager.LogicOp.OR_REVERSE);
      var6.begin(7, DefaultVertexFormat.POSITION);
      var6.vertex((double)var3.x, (double)var4.y, 0.0D).endVertex();
      var6.vertex((double)var4.x, (double)var4.y, 0.0D).endVertex();
      var6.vertex((double)var4.x, (double)var3.y, 0.0D).endVertex();
      var6.vertex((double)var3.x, (double)var3.y, 0.0D).endVertex();
      var7.end();
      RenderSystem.disableColorLogicOp();
      RenderSystem.enableTexture();
   }

   private BookEditScreen.Pos2i getPositionAtIndex(String var1, int var2) {
      BookEditScreen.Pos2i var3 = new BookEditScreen.Pos2i();
      int var4 = 0;
      int var5 = 0;

      for(String var6 = var1; !var6.isEmpty(); var5 = var4) {
         int var7 = this.strIndexAtWidth(var6, 114);
         String var8;
         if (var6.length() <= var7) {
            var8 = var6.substring(0, Math.min(Math.max(var2 - var5, 0), var6.length()));
            var3.x = var3.x + this.strWidth(var8);
            break;
         }

         var8 = var6.substring(0, var7);
         char var9 = var6.charAt(var7);
         boolean var10 = var9 == ' ' || var9 == '\n';
         var6 = ChatFormatting.getLastColors(var8) + var6.substring(var7 + (var10 ? 1 : 0));
         var4 += var8.length() + (var10 ? 1 : 0);
         if (var4 - 1 >= var2) {
            String var11 = var8.substring(0, Math.min(Math.max(var2 - var5, 0), var8.length()));
            var3.x = var3.x + this.strWidth(var11);
            break;
         }

         int var10001 = var3.y;
         this.font.getClass();
         var3.y = var10001 + 9;
      }

      return var3;
   }

   private void handleBidi(BookEditScreen.Pos2i var1) {
      if (this.font.isBidirectional()) {
         var1.x = 114 - var1.x;
      }

   }

   private void convertScreenToLocal(BookEditScreen.Pos2i var1) {
      var1.x = var1.x - (this.width - 192) / 2 - 36;
      var1.y = var1.y - 32;
   }

   private void convertLocalToScreen(BookEditScreen.Pos2i var1) {
      var1.x = var1.x + (this.width - 192) / 2 + 36;
      var1.y = var1.y + 32;
   }

   private int indexInLine(String var1, int var2) {
      if (var2 < 0) {
         return 0;
      } else {
         float var4 = 0.0F;
         boolean var5 = false;
         String var6 = var1 + " ";

         for(int var7 = 0; var7 < var6.length(); ++var7) {
            char var8 = var6.charAt(var7);
            float var9 = this.font.charWidth(var8);
            if (var8 == 167 && var7 < var6.length() - 1) {
               ++var7;
               var8 = var6.charAt(var7);
               if (var8 != 'l' && var8 != 'L') {
                  if (var8 == 'r' || var8 == 'R') {
                     var5 = false;
                  }
               } else {
                  var5 = true;
               }

               var9 = 0.0F;
            }

            float var3 = var4;
            var4 += var9;
            if (var5 && var9 > 0.0F) {
               ++var4;
            }

            if ((float)var2 >= var3 && (float)var2 < var4) {
               return var7;
            }
         }

         if ((float)var2 >= var4) {
            return var6.length() - 1;
         } else {
            return -1;
         }
      }
   }

   private int getIndexAtPosition(String var1, BookEditScreen.Pos2i var2) {
      this.font.getClass();
      int var3 = 16 * 9;
      if (var2.y > var3) {
         return -1;
      } else {
         int var4 = Integer.MIN_VALUE;
         this.font.getClass();
         int var5 = 9;
         int var6 = 0;

         for(String var7 = var1; !var7.isEmpty() && var4 < var3; var5 += 9) {
            int var8 = this.strIndexAtWidth(var7, 114);
            if (var8 < var7.length()) {
               String var9 = var7.substring(0, var8);
               if (var2.y >= var4 && var2.y < var5) {
                  int var13 = this.indexInLine(var9, var2.x);
                  return var13 < 0 ? -1 : var6 + var13;
               }

               char var10 = var7.charAt(var8);
               boolean var11 = var10 == ' ' || var10 == '\n';
               var7 = ChatFormatting.getLastColors(var9) + var7.substring(var8 + (var11 ? 1 : 0));
               var6 += var9.length() + (var11 ? 1 : 0);
            } else if (var2.y >= var4 && var2.y < var5) {
               int var12 = this.indexInLine(var7, var2.x);
               return var12 < 0 ? -1 : var6 + var12;
            }

            var4 = var5;
            this.font.getClass();
         }

         return var1.length();
      }
   }

   public boolean mouseClicked(double var1, double var3, int var5) {
      if (var5 == 0) {
         long var6 = Util.getMillis();
         String var8 = this.getCurrentPageText();
         if (!var8.isEmpty()) {
            BookEditScreen.Pos2i var9 = new BookEditScreen.Pos2i((int)var1, (int)var3);
            this.convertScreenToLocal(var9);
            this.handleBidi(var9);
            int var10 = this.getIndexAtPosition(var8, var9);
            if (var10 >= 0) {
               if (var10 == this.lastIndex && var6 - this.lastClickTime < 250L) {
                  if (this.selectionPos == this.cursorPos) {
                     this.selectionPos = this.font.getWordPosition(var8, -1, var10, false);
                     this.cursorPos = this.font.getWordPosition(var8, 1, var10, false);
                  } else {
                     this.selectionPos = 0;
                     this.cursorPos = this.getCurrentPageText().length();
                  }
               } else {
                  this.cursorPos = var10;
                  if (!Screen.hasShiftDown()) {
                     this.selectionPos = this.cursorPos;
                  }
               }
            }

            this.lastIndex = var10;
         }

         this.lastClickTime = var6;
      }

      return super.mouseClicked(var1, var3, var5);
   }

   public boolean mouseDragged(double var1, double var3, int var5, double var6, double var8) {
      if (var5 == 0 && this.currentPage >= 0 && this.currentPage < this.pages.size()) {
         String var10 = (String)this.pages.get(this.currentPage);
         BookEditScreen.Pos2i var11 = new BookEditScreen.Pos2i((int)var1, (int)var3);
         this.convertScreenToLocal(var11);
         this.handleBidi(var11);
         int var12 = this.getIndexAtPosition(var10, var11);
         if (var12 >= 0) {
            this.cursorPos = var12;
         }
      }

      return super.mouseDragged(var1, var3, var5, var6, var8);
   }

   class Pos2i {
      private int x;
      private int y;

      Pos2i() {
      }

      Pos2i(int var2, int var3) {
         this.x = var2;
         this.y = var3;
      }
   }
}
