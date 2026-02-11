package net.minecraft.client.gui.screens;

import java.util.Objects;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.ActiveTextCollector;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.gui.components.CommandSuggestions;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.multiplayer.RestrictionsScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.multiplayer.chat.ChatAbilities;
import net.minecraft.client.multiplayer.chat.ChatListener;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.Nullable;

public class ChatScreen extends Screen {
   public static final double MOUSE_SCROLL_SPEED = 7.0;
   private static final Component USAGE_TEXT = Component.translatable("chat_screen.usage");
   private static final Component RESTRICTED_NARRATION_TEXT = Component.translatable("chat_screen.restricted.narration");
   public static final int USAGE_BACKGROUND_COLOR = -805306368;
   private final boolean closeOnSubmit;
   private String historyBuffer;
   private int historyPos;
   protected EditBox input;
   protected String initial;
   protected boolean isDraft;
   private final ChatComponent.DisplayMode displayMode;
   private final ChatAbilities chatAbilities;
   protected ExitReason exitReason;
   private CommandSuggestions commandSuggestions;

   public ChatScreen(final String initial, final boolean isDraft, final ChatAbilities chatAbilities) {
      this(initial, isDraft, chatAbilities, true);
   }

   public ChatScreen(final String initial, final boolean isDraft, final ChatAbilities chatAbilities, final boolean closeOnSubmit) {
      super(Component.translatable("chat_screen.title"));
      this.historyBuffer = "";
      this.historyPos = -1;
      this.exitReason = ChatScreen.ExitReason.INTERRUPTED;
      this.closeOnSubmit = closeOnSubmit;
      this.initial = initial;
      this.isDraft = isDraft;
      this.chatAbilities = chatAbilities;
      this.displayMode = chatAbilities.hasAnyRestrictions() ? ChatComponent.DisplayMode.FOREGROUND_RESTRICTED : ChatComponent.DisplayMode.FOREGROUND;
   }

   protected void init() {
      this.historyPos = this.minecraft.gui.getChat().getRecentChat().size();
      this.input = new EditBox(this.minecraft.fontFilterFishy, 4, this.height - 12, this.width - 4, 12, Component.translatable("chat.editBox")) {
         {
            Objects.requireNonNull(ChatScreen.this);
         }

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
      this.commandSuggestions.setRestrictions(this.chatAbilities.canSendMessages(), this.chatAbilities.canSendCommands());
      this.commandSuggestions.updateCommandInfo();
   }

   protected void setInitialFocus() {
      this.setInitialFocus(this.input);
   }

   public void resize(final int width, final int height) {
      this.initial = this.input.getValue();
      this.init(width, height);
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

   private void onEdited(final String value) {
      this.commandSuggestions.setAllowSuggestions(true);
      this.commandSuggestions.updateCommandInfo();
      this.isDraft = false;
   }

   public boolean keyPressed(final KeyEvent event) {
      if (this.commandSuggestions.keyPressed(event)) {
         return true;
      } else if (this.isDraft && event.key() == 259) {
         this.input.setValue("");
         this.isDraft = false;
         return true;
      } else if (super.keyPressed(event)) {
         return true;
      } else if (event.isConfirmation()) {
         if (!this.commandSuggestions.hasAllowedInput()) {
            return true;
         } else {
            this.handleChatInput(this.input.getValue(), true);
            if (this.closeOnSubmit) {
               this.exitReason = ChatScreen.ExitReason.DONE;
               this.minecraft.setScreen((Screen)null);
            } else {
               this.input.setValue("");
               this.minecraft.gui.getChat().resetChatScroll();
            }

            return true;
         }
      } else {
         switch (event.key()) {
            case 264 -> this.moveInHistory(1);
            case 265 -> this.moveInHistory(-1);
            case 266 -> this.minecraft.gui.getChat().scrollChat(this.minecraft.gui.getChat().getLinesPerPage() - 1);
            case 267 -> this.minecraft.gui.getChat().scrollChat(-this.minecraft.gui.getChat().getLinesPerPage() + 1);
            default -> {
               return false;
            }
         }

         return true;
      }
   }

   public boolean mouseScrolled(final double x, final double y, final double scrollX, double scrollY) {
      scrollY = Mth.clamp(scrollY, -1.0, 1.0);
      if (this.commandSuggestions.mouseScrolled(scrollY)) {
         return true;
      } else {
         if (!this.minecraft.hasShiftDown()) {
            scrollY *= 7.0;
         }

         this.minecraft.gui.getChat().scrollChat((int)scrollY);
         return true;
      }
   }

   public boolean mouseClicked(final MouseButtonEvent event, final boolean doubleClick) {
      if (this.commandSuggestions.mouseClicked(event)) {
         return true;
      } else {
         if (event.button() == 0) {
            int screenHeight = this.minecraft.getWindow().getGuiScaledHeight();
            ActiveTextCollector.ClickableStyleFinder finder = (new ActiveTextCollector.ClickableStyleFinder(this.getFont(), (int)event.x(), (int)event.y())).includeInsertions(this.insertionClickMode());
            this.minecraft.gui.getChat().captureClickableText(finder, screenHeight, this.minecraft.gui.getGuiTicks(), this.displayMode);
            Style clicked = finder.result();
            if (clicked != null && this.handleComponentClicked(clicked, this.insertionClickMode())) {
               this.initial = this.input.getValue();
               return true;
            }
         }

         return super.mouseClicked(event, doubleClick);
      }
   }

   private boolean insertionClickMode() {
      return this.minecraft.hasShiftDown();
   }

   private boolean handleComponentClicked(final Style clicked, final boolean allowInsertions) {
      ClickEvent event = clicked.getClickEvent();
      if (allowInsertions) {
         if (clicked.getInsertion() != null) {
            this.insertText(clicked.getInsertion(), false);
         }
      } else if (event != null) {
         Objects.requireNonNull(event);
         ClickEvent var4 = event;
         byte var5 = 0;

         while(true) {
            //$FF: var5->value
            //0->net/minecraft/network/chat/ClickEvent$Custom
            //1->net/minecraft/network/chat/ClickEvent$Custom
            switch (var4.typeSwitch<invokedynamic>(var4, var5)) {
               case 0:
                  ClickEvent.Custom customEvent = (ClickEvent.Custom)var4;
                  if (!customEvent.id().equals(ChatComponent.QUEUE_EXPAND_ID)) {
                     var5 = 1;
                     break;
                  }

                  ChatListener chatListener = this.minecraft.getChatListener();
                  if (chatListener.queueSize() != 0L) {
                     chatListener.acceptNextDelayedMessage();
                  }

                  return true;
               case 1:
                  ClickEvent.Custom customEvent = (ClickEvent.Custom)var4;
                  if (!customEvent.id().equals(ChatComponent.GO_TO_RESTRICTIONS_SCREEN)) {
                     var5 = 2;
                     break;
                  }

                  this.minecraft.setScreen(new RestrictionsScreen(this, this.chatAbilities));
                  return true;
               default:
                  defaultHandleGameClickEvent(event, this.minecraft, this);
                  return true;
            }
         }
      }

      return false;
   }

   public void insertText(final String text, final boolean replace) {
      if (replace) {
         this.input.setValue(text);
      } else {
         this.input.insertText(text);
      }

   }

   public void moveInHistory(final int dir) {
      int newPos = this.historyPos + dir;
      int max = this.minecraft.gui.getChat().getRecentChat().size();
      newPos = Mth.clamp(newPos, 0, max);
      if (newPos != this.historyPos) {
         if (newPos == max) {
            this.historyPos = max;
            this.input.setValue(this.historyBuffer);
         } else {
            if (this.historyPos == max) {
               this.historyBuffer = this.input.getValue();
            }

            this.input.setValue((String)this.minecraft.gui.getChat().getRecentChat().get(newPos));
            this.commandSuggestions.setAllowSuggestions(false);
            this.historyPos = newPos;
         }
      }
   }

   private @Nullable FormattedCharSequence formatChat(final String text, final int offset) {
      return this.isDraft ? FormattedCharSequence.forward(text, Style.EMPTY.withColor(ChatFormatting.GRAY).withItalic(true)) : null;
   }

   public void render(final GuiGraphics graphics, final int mouseX, final int mouseY, final float a) {
      graphics.fill(2, this.height - 14, this.width - 2, this.height - 2, this.minecraft.options.getBackgroundColor(-2147483648));
      this.minecraft.gui.getChat().render(graphics, this.font, this.minecraft.gui.getGuiTicks(), mouseX, mouseY, this.displayMode, this.insertionClickMode());
      super.render(graphics, mouseX, mouseY, a);
      this.commandSuggestions.render(graphics, mouseX, mouseY);
   }

   public void renderBackground(final GuiGraphics graphics, final int mouseX, final int mouseY, final float a) {
   }

   public boolean isPauseScreen() {
      return false;
   }

   public boolean isAllowedInPortal() {
      return true;
   }

   protected void updateNarrationState(final NarrationElementOutput output) {
      output.add(NarratedElementType.TITLE, this.getTitle());
      if (this.displayMode.showRestrictedPrompt) {
         output.add(NarratedElementType.USAGE, (Component)CommonComponents.joinForNarration(USAGE_TEXT, RESTRICTED_NARRATION_TEXT));
      } else {
         output.add(NarratedElementType.USAGE, USAGE_TEXT);
      }

      String value = this.input.getValue();
      if (!value.isEmpty()) {
         output.nest().add(NarratedElementType.TITLE, (Component)Component.translatable("chat_screen.message", value));
      }

   }

   public void handleChatInput(String msg, final boolean addToRecent) {
      msg = this.normalizeChatMessage(msg);
      if (!msg.isEmpty()) {
         if (addToRecent) {
            this.minecraft.gui.getChat().addRecentChat(msg);
         }

         if (msg.startsWith("/")) {
            this.minecraft.player.connection.sendCommand(msg.substring(1));
         } else {
            this.minecraft.player.connection.sendChat(msg);
         }

      }
   }

   public String normalizeChatMessage(final String message) {
      return StringUtil.trimChatMessage(StringUtils.normalizeSpace(message.trim()));
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
      T create(String initial, boolean isDraft, ChatAbilities chatAbilities);
   }
}
