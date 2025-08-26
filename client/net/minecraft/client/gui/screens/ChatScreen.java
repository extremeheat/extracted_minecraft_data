package net.minecraft.client.gui.screens;

import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.GuiMessageTag;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.gui.components.CommandSuggestions;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.util.StringUtil;
import org.apache.commons.lang3.StringUtils;

public class ChatScreen extends Screen {
   public static final double MOUSE_SCROLL_SPEED = 7.0;
   private static final Component USAGE_TEXT = Component.translatable("chat_screen.usage");
   private static final int TOOLTIP_MAX_WIDTH = 210;
   private String historyBuffer = "";
   private int historyPos = -1;
   protected EditBox input;
   protected String initial;
   protected boolean isDraft;
   protected ExitReason exitReason;
   CommandSuggestions commandSuggestions;

   public ChatScreen(String var1, boolean var2) {
      super(Component.translatable("chat_screen.title"));
      this.exitReason = ChatScreen.ExitReason.INTERRUPTED;
      this.initial = var1;
      this.isDraft = var2;
   }

   protected void init() {
      this.historyPos = this.minecraft.gui.getChat().getRecentChat().size();
      this.input = new EditBox(this.minecraft.fontFilterFishy, 4, this.height - 12, this.width - 4, 12, Component.translatable("chat.editBox")) {
         protected MutableComponent createNarrationMessage() {
            return super.createNarrationMessage().append(ChatScreen.this.commandSuggestions.getNarrationMessage());
         }
      };
      this.input.setMaxLength(256);
      this.input.setBordered(false);
      this.input.setValue(this.initial);
      this.input.setResponder(this::onEdited);
      this.input.addFormatter(this::formatChat);
      this.input.setCanLoseFocus(false);
      this.addRenderableWidget(this.input);
      this.commandSuggestions = new CommandSuggestions(this.minecraft, this, this.input, this.font, false, false, 1, 10, true, -805306368);
      this.commandSuggestions.setAllowHiding(false);
      this.commandSuggestions.setAllowSuggestions(false);
      this.commandSuggestions.updateCommandInfo();
   }

   protected void setInitialFocus() {
      this.setInitialFocus(this.input);
   }

   public void resize(Minecraft var1, int var2, int var3) {
      this.initial = this.input.getValue();
      this.init(var1, var2, var3);
   }

   public void onClose() {
      this.exitReason = ChatScreen.ExitReason.INTENTIONAL;
      super.onClose();
   }

   public void removed() {
      this.minecraft.gui.getChat().resetChatScroll();
      this.initial = this.input.getValue();
      if (!this.shouldDiscardDraft() && !StringUtils.isBlank(this.initial)) {
         if (!this.isDraft) {
            this.minecraft.gui.getChat().saveAsDraft(this.initial);
         }
      } else {
         this.minecraft.gui.getChat().discardDraft();
      }

   }

   protected boolean shouldDiscardDraft() {
      return this.exitReason != ChatScreen.ExitReason.INTERRUPTED && (this.exitReason != ChatScreen.ExitReason.INTENTIONAL || !(Boolean)this.minecraft.options.saveChatDrafts().get());
   }

   private void onEdited(String var1) {
      this.commandSuggestions.setAllowSuggestions(true);
      this.commandSuggestions.updateCommandInfo();
      this.isDraft = false;
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      if (this.commandSuggestions.keyPressed(var1, var2, var3)) {
         return true;
      } else if (this.isDraft && var1 == 259) {
         this.input.setValue("");
         this.isDraft = false;
         return true;
      } else if (super.keyPressed(var1, var2, var3)) {
         return true;
      } else {
         switch (var1) {
            case 257:
            case 335:
               this.handleChatInput(this.input.getValue(), true);
               this.exitReason = ChatScreen.ExitReason.DONE;
               this.minecraft.setScreen((Screen)null);
               break;
            case 264:
               this.moveInHistory(1);
               break;
            case 265:
               this.moveInHistory(-1);
               break;
            case 266:
               this.minecraft.gui.getChat().scrollChat(this.minecraft.gui.getChat().getLinesPerPage() - 1);
               break;
            case 267:
               this.minecraft.gui.getChat().scrollChat(-this.minecraft.gui.getChat().getLinesPerPage() + 1);
               break;
            default:
               return false;
         }

         return true;
      }
   }

   public boolean mouseScrolled(double var1, double var3, double var5, double var7) {
      var7 = Mth.clamp(var7, -1.0, 1.0);
      if (this.commandSuggestions.mouseScrolled(var7)) {
         return true;
      } else {
         if (!hasShiftDown()) {
            var7 *= 7.0;
         }

         this.minecraft.gui.getChat().scrollChat((int)var7);
         return true;
      }
   }

   public boolean mouseClicked(double var1, double var3, int var5, boolean var6) {
      if (this.commandSuggestions.mouseClicked((double)((int)var1), (double)((int)var3), var5)) {
         return true;
      } else {
         if (var5 == 0) {
            ChatComponent var7 = this.minecraft.gui.getChat();
            if (var7.handleChatQueueClicked(var1, var3)) {
               return true;
            }

            Style var8 = this.getComponentStyleAt(var1, var3);
            if (var8 != null && this.handleComponentClicked(var8)) {
               this.initial = this.input.getValue();
               return true;
            }
         }

         return this.input.mouseClicked(var1, var3, var5, var6) ? true : super.mouseClicked(var1, var3, var5, var6);
      }
   }

   public boolean mouseDragged(double var1, double var3, int var5, double var6, double var8) {
      return this.input.mouseDragged(var1, var3, var5, var6, var8) ? true : super.mouseDragged(var1, var3, var5, var6, var8);
   }

   public void insertText(String var1, boolean var2) {
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

   @Nullable
   private FormattedCharSequence formatChat(String var1, int var2) {
      return this.isDraft ? FormattedCharSequence.forward(var1, Style.EMPTY.withColor(ChatFormatting.GRAY).withItalic(true)) : null;
   }

   public void render(GuiGraphics var1, int var2, int var3, float var4) {
      var1.fill(2, this.height - 14, this.width - 2, this.height - 2, this.minecraft.options.getBackgroundColor(-2147483648));
      this.minecraft.gui.getChat().render(var1, this.minecraft.gui.getGuiTicks(), var2, var3, true);
      super.render(var1, var2, var3, var4);
      this.commandSuggestions.render(var1, var2, var3);
      GuiMessageTag var5 = this.minecraft.gui.getChat().getMessageTagAt((double)var2, (double)var3);
      if (var5 != null && var5.text() != null) {
         var1.setTooltipForNextFrame(this.font, this.font.split(var5.text(), 210), var2, var3);
      } else {
         Style var6 = this.getComponentStyleAt((double)var2, (double)var3);
         var1.renderComponentHoverEffect(this.font, var6, var2, var3);
      }

   }

   public void renderBackground(GuiGraphics var1, int var2, int var3, float var4) {
   }

   public boolean isPauseScreen() {
      return false;
   }

   public boolean isAllowedInPortal() {
      return true;
   }

   protected void updateNarrationState(NarrationElementOutput var1) {
      var1.add(NarratedElementType.TITLE, this.getTitle());
      var1.add(NarratedElementType.USAGE, USAGE_TEXT);
      String var2 = this.input.getValue();
      if (!var2.isEmpty()) {
         var1.nest().add(NarratedElementType.TITLE, (Component)Component.translatable("chat_screen.message", var2));
      }

   }

   @Nullable
   private Style getComponentStyleAt(double var1, double var3) {
      return this.minecraft.gui.getChat().getClickedComponentStyleAt(var1, var3);
   }

   public void handleChatInput(String var1, boolean var2) {
      var1 = this.normalizeChatMessage(var1);
      if (!var1.isEmpty()) {
         if (var2) {
            this.minecraft.gui.getChat().addRecentChat(var1);
         }

         if (var1.startsWith("/")) {
            this.minecraft.player.connection.sendCommand(var1.substring(1));
         } else {
            this.minecraft.player.connection.sendChat(var1);
         }

      }
   }

   public String normalizeChatMessage(String var1) {
      return StringUtil.trimChatMessage(StringUtils.normalizeSpace(var1.trim()));
   }

   protected static enum ExitReason {
      INTENTIONAL,
      INTERRUPTED,
      DONE;

      private ExitReason() {
      }

      // $FF: synthetic method
      private static ExitReason[] $values() {
         return new ExitReason[]{INTENTIONAL, INTERRUPTED, DONE};
      }
   }

   @FunctionalInterface
   public interface ChatConstructor<T extends ChatScreen> {
      T create(String var1, boolean var2);
   }
}
