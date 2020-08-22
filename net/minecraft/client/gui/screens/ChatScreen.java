package net.minecraft.client.gui.screens;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.components.CommandSuggestions;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class ChatScreen extends Screen {
   private String historyBuffer = "";
   private int historyPos = -1;
   protected EditBox input;
   private String initial = "";
   private CommandSuggestions commandSuggestions;

   public ChatScreen(String var1) {
      super(NarratorChatListener.NO_TITLE);
      this.initial = var1;
   }

   protected void init() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
      this.historyPos = this.minecraft.gui.getChat().getRecentChat().size();
      this.input = new EditBox(this.font, 4, this.height - 12, this.width - 4, 12, I18n.get("chat.editBox")) {
         protected String getNarrationMessage() {
            return super.getNarrationMessage() + ChatScreen.this.commandSuggestions.getNarrationMessage();
         }
      };
      this.input.setMaxLength(256);
      this.input.setBordered(false);
      this.input.setValue(this.initial);
      this.input.setResponder(this::onEdited);
      this.children.add(this.input);
      this.commandSuggestions = new CommandSuggestions(this.minecraft, this, this.input, this.font, false, false, 1, 10, true, -805306368);
      this.commandSuggestions.updateCommandInfo();
      this.setInitialFocus(this.input);
   }

   public void resize(Minecraft var1, int var2, int var3) {
      String var4 = this.input.getValue();
      this.init(var1, var2, var3);
      this.setChatLine(var4);
      this.commandSuggestions.updateCommandInfo();
   }

   public void removed() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
      this.minecraft.gui.getChat().resetChatScroll();
   }

   public void tick() {
      this.input.tick();
   }

   private void onEdited(String var1) {
      String var2 = this.input.getValue();
      this.commandSuggestions.setAllowSuggestions(!var2.equals(this.initial));
      this.commandSuggestions.updateCommandInfo();
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      if (this.commandSuggestions.keyPressed(var1, var2, var3)) {
         return true;
      } else if (super.keyPressed(var1, var2, var3)) {
         return true;
      } else if (var1 == 256) {
         this.minecraft.setScreen((Screen)null);
         return true;
      } else if (var1 != 257 && var1 != 335) {
         if (var1 == 265) {
            this.moveInHistory(-1);
            return true;
         } else if (var1 == 264) {
            this.moveInHistory(1);
            return true;
         } else if (var1 == 266) {
            this.minecraft.gui.getChat().scrollChat((double)(this.minecraft.gui.getChat().getLinesPerPage() - 1));
            return true;
         } else if (var1 == 267) {
            this.minecraft.gui.getChat().scrollChat((double)(-this.minecraft.gui.getChat().getLinesPerPage() + 1));
            return true;
         } else {
            return false;
         }
      } else {
         String var4 = this.input.getValue().trim();
         if (!var4.isEmpty()) {
            this.sendMessage(var4);
         }

         this.minecraft.setScreen((Screen)null);
         return true;
      }
   }

   public boolean mouseScrolled(double var1, double var3, double var5) {
      if (var5 > 1.0D) {
         var5 = 1.0D;
      }

      if (var5 < -1.0D) {
         var5 = -1.0D;
      }

      if (this.commandSuggestions.mouseScrolled(var5)) {
         return true;
      } else {
         if (!hasShiftDown()) {
            var5 *= 7.0D;
         }

         this.minecraft.gui.getChat().scrollChat(var5);
         return true;
      }
   }

   public boolean mouseClicked(double var1, double var3, int var5) {
      if (this.commandSuggestions.mouseClicked((double)((int)var1), (double)((int)var3), var5)) {
         return true;
      } else {
         if (var5 == 0) {
            Component var6 = this.minecraft.gui.getChat().getClickedComponentAt(var1, var3);
            if (var6 != null && this.handleComponentClicked(var6)) {
               return true;
            }
         }

         return this.input.mouseClicked(var1, var3, var5) ? true : super.mouseClicked(var1, var3, var5);
      }
   }

   protected void insertText(String var1, boolean var2) {
      if (var2) {
         this.input.setValue(var1);
      } else {
         this.input.insertText(var1);
      }

   }

   public void moveInHistory(int var1) {
      int var2 = this.historyPos + var1;
      int var3 = this.minecraft.gui.getChat().getRecentChat().size();
      var2 = Mth.clamp(var2, 0, var3);
      if (var2 != this.historyPos) {
         if (var2 == var3) {
            this.historyPos = var3;
            this.input.setValue(this.historyBuffer);
         } else {
            if (this.historyPos == var3) {
               this.historyBuffer = this.input.getValue();
            }

            this.input.setValue((String)this.minecraft.gui.getChat().getRecentChat().get(var2));
            this.commandSuggestions.setAllowSuggestions(false);
            this.historyPos = var2;
         }
      }
   }

   public void render(int var1, int var2, float var3) {
      this.setFocused(this.input);
      this.input.setFocus(true);
      fill(2, this.height - 14, this.width - 2, this.height - 2, this.minecraft.options.getBackgroundColor(Integer.MIN_VALUE));
      this.input.render(var1, var2, var3);
      this.commandSuggestions.render(var1, var2);
      Component var4 = this.minecraft.gui.getChat().getClickedComponentAt((double)var1, (double)var2);
      if (var4 != null && var4.getStyle().getHoverEvent() != null) {
         this.renderComponentHoverEffect(var4, var1, var2);
      }

      super.render(var1, var2, var3);
   }

   public boolean isPauseScreen() {
      return false;
   }

   private void setChatLine(String var1) {
      this.input.setValue(var1);
   }
}
