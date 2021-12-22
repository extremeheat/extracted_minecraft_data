package net.minecraft.client.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.gui.components.CommandSuggestions;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Mth;

public class ChatScreen extends Screen {
   public static final int MOUSE_SCROLL_SPEED = 7;
   private static final Component USAGE_TEXT = new TranslatableComponent("chat_screen.usage");
   private String historyBuffer = "";
   private int historyPos = -1;
   protected EditBox input;
   private final String initial;
   CommandSuggestions commandSuggestions;

   public ChatScreen(String var1) {
      super(new TranslatableComponent("chat_screen.title"));
      this.initial = var1;
   }

   protected void init() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
      this.historyPos = this.minecraft.gui.getChat().getRecentChat().size();
      this.input = new EditBox(this.font, 4, this.height - 12, this.width - 4, 12, new TranslatableComponent("chat.editBox")) {
         protected MutableComponent createNarrationMessage() {
            return super.createNarrationMessage().append(ChatScreen.this.commandSuggestions.getNarrationMessage());
         }
      };
      this.input.setMaxLength(256);
      this.input.setBordered(false);
      this.input.setValue(this.initial);
      this.input.setResponder(this::onEdited);
      this.addWidget(this.input);
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
            ChatComponent var6 = this.minecraft.gui.getChat();
            if (var6.handleChatQueueClicked(var1, var3)) {
               return true;
            }

            Style var7 = var6.getClickedComponentStyleAt(var1, var3);
            if (var7 != null && this.handleComponentClicked(var7)) {
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
      var2 = Mth.clamp((int)var2, (int)0, (int)var3);
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

   public void render(PoseStack var1, int var2, int var3, float var4) {
      this.setFocused(this.input);
      this.input.setFocus(true);
      fill(var1, 2, this.height - 14, this.width - 2, this.height - 2, this.minecraft.options.getBackgroundColor(-2147483648));
      this.input.render(var1, var2, var3, var4);
      this.commandSuggestions.render(var1, var2, var3);
      Style var5 = this.minecraft.gui.getChat().getClickedComponentStyleAt((double)var2, (double)var3);
      if (var5 != null && var5.getHoverEvent() != null) {
         this.renderComponentHoverEffect(var1, var5, var2, var3);
      }

      super.render(var1, var2, var3, var4);
   }

   public boolean isPauseScreen() {
      return false;
   }

   private void setChatLine(String var1) {
      this.input.setValue(var1);
   }

   protected void updateNarrationState(NarrationElementOutput var1) {
      var1.add(NarratedElementType.TITLE, this.getTitle());
      var1.add(NarratedElementType.USAGE, USAGE_TEXT);
      String var2 = this.input.getValue();
      if (!var2.isEmpty()) {
         var1.nest().add(NarratedElementType.TITLE, (Component)(new TranslatableComponent("chat_screen.message", new Object[]{var2})));
      }

   }
}
