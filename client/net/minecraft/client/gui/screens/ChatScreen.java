package net.minecraft.client.gui.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.brigadier.tree.CommandNode;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.chat.ClientChatPreview;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.gui.components.CommandSuggestions;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.commands.arguments.PreviewedArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import org.apache.commons.lang3.StringUtils;

public class ChatScreen extends Screen {
   public static final double MOUSE_SCROLL_SPEED = 7.0;
   private static final Component USAGE_TEXT = Component.translatable("chat_screen.usage");
   private static final int PREVIEW_MARGIN_SIDES = 2;
   private static final int PREVIEW_PADDING = 2;
   private static final int PREVIEW_MARGIN_BOTTOM = 15;
   private static final Component PREVIEW_WARNING_TITLE = Component.translatable("chatPreview.warning.toast.title");
   private static final Component PREVIEW_WARNING_TOAST = Component.translatable("chatPreview.warning.toast");
   private static final Component PREVIEW_HINT = Component.translatable("chat.preview").withStyle(ChatFormatting.DARK_GRAY);
   private String historyBuffer = "";
   private int historyPos = -1;
   protected EditBox input;
   private String initial;
   CommandSuggestions commandSuggestions;
   private ClientChatPreview chatPreview;

   public ChatScreen(String var1) {
      super(Component.translatable("chat_screen.title"));
      this.initial = var1;
   }

   @Override
   protected void init() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
      this.historyPos = this.minecraft.gui.getChat().getRecentChat().size();
      this.input = new EditBox(this.font, 4, this.height - 12, this.width - 4, 12, Component.translatable("chat.editBox")) {
         @Override
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
      this.chatPreview = new ClientChatPreview(this.minecraft);
      this.updateChatPreview(this.input.getValue());
      ServerData var1 = this.minecraft.getCurrentServer();
      if (var1 != null && this.minecraft.options.chatPreview().get()) {
         ServerData.ChatPreview var2 = var1.getChatPreview();
         if (var2 != null && var1.previewsChat() && var2.showToast()) {
            ServerList.saveSingleServer(var1);
            SystemToast var3 = SystemToast.multiline(
               this.minecraft, SystemToast.SystemToastIds.CHAT_PREVIEW_WARNING, PREVIEW_WARNING_TITLE, PREVIEW_WARNING_TOAST
            );
            this.minecraft.getToasts().addToast(var3);
         }
      }
   }

   @Override
   public void resize(Minecraft var1, int var2, int var3) {
      String var4 = this.input.getValue();
      this.init(var1, var2, var3);
      this.setChatLine(var4);
      this.commandSuggestions.updateCommandInfo();
   }

   @Override
   public void removed() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
      this.minecraft.gui.getChat().resetChatScroll();
   }

   @Override
   public void tick() {
      this.input.tick();
      this.chatPreview.tick();
   }

   private void onEdited(String var1) {
      String var2 = this.input.getValue();
      this.commandSuggestions.setAllowSuggestions(!var2.equals(this.initial));
      this.commandSuggestions.updateCommandInfo();
      this.updateChatPreview(var2);
   }

   private void updateChatPreview(String var1) {
      String var2 = this.normalizeChatMessage(var1);
      if (this.sendsChatPreviewRequests()) {
         this.requestPreview(var2);
      } else {
         this.chatPreview.disable();
      }
   }

   private void requestPreview(String var1) {
      if (var1.startsWith("/")) {
         this.requestCommandArgumentPreview(var1);
      } else {
         this.requestChatMessagePreview(var1);
      }
   }

   private void requestChatMessagePreview(String var1) {
      this.chatPreview.update(var1);
   }

   private void requestCommandArgumentPreview(String var1) {
      CommandNode var2 = this.commandSuggestions.getNodeAt(this.input.getCursorPosition());
      if (var2 != null && PreviewedArgument.isPreviewed(var2)) {
         this.chatPreview.update(var1);
      } else {
         this.chatPreview.disable();
      }
   }

   private boolean sendsChatPreviewRequests() {
      if (this.minecraft.player == null) {
         return false;
      } else if (!this.minecraft.options.chatPreview().get()) {
         return false;
      } else {
         ServerData var1 = this.minecraft.getCurrentServer();
         return var1 != null && var1.previewsChat();
      }
   }

   @Override
   public boolean keyPressed(int var1, int var2, int var3) {
      if (this.commandSuggestions.keyPressed(var1, var2, var3)) {
         return true;
      } else if (super.keyPressed(var1, var2, var3)) {
         return true;
      } else if (var1 == 256) {
         this.minecraft.setScreen(null);
         return true;
      } else if (var1 == 257 || var1 == 335) {
         this.handleChatInput(this.input.getValue(), true);
         this.minecraft.setScreen(null);
         return true;
      } else if (var1 == 265) {
         this.moveInHistory(-1);
         return true;
      } else if (var1 == 264) {
         this.moveInHistory(1);
         return true;
      } else if (var1 == 266) {
         this.minecraft.gui.getChat().scrollChat(this.minecraft.gui.getChat().getLinesPerPage() - 1);
         return true;
      } else if (var1 == 267) {
         this.minecraft.gui.getChat().scrollChat(-this.minecraft.gui.getChat().getLinesPerPage() + 1);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public boolean mouseScrolled(double var1, double var3, double var5) {
      var5 = Mth.clamp(var5, -1.0, 1.0);
      if (this.commandSuggestions.mouseScrolled(var5)) {
         return true;
      } else {
         if (!hasShiftDown()) {
            var5 *= 7.0;
         }

         this.minecraft.gui.getChat().scrollChat((int)var5);
         return true;
      }
   }

   @Override
   public boolean mouseClicked(double var1, double var3, int var5) {
      if (this.commandSuggestions.mouseClicked((double)((int)var1), (double)((int)var3), var5)) {
         return true;
      } else {
         if (var5 == 0) {
            ChatComponent var6 = this.minecraft.gui.getChat();
            if (var6.handleChatQueueClicked(var1, var3)) {
               return true;
            }

            Style var7 = this.getComponentStyleAt(var1, var3);
            if (var7 != null && this.handleComponentClicked(var7)) {
               this.initial = this.input.getValue();
               return true;
            }
         }

         return this.input.mouseClicked(var1, var3, var5) ? true : super.mouseClicked(var1, var3, var5);
      }
   }

   @Override
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

            this.input.setValue(this.minecraft.gui.getChat().getRecentChat().get(var2));
            this.commandSuggestions.setAllowSuggestions(false);
            this.historyPos = var2;
         }
      }
   }

   @Override
   public void render(PoseStack var1, int var2, int var3, float var4) {
      this.setFocused(this.input);
      this.input.setFocus(true);
      fill(var1, 2, this.height - 14, this.width - 2, this.height - 2, this.minecraft.options.getBackgroundColor(-2147483648));
      this.input.render(var1, var2, var3, var4);
      if (this.chatPreview.isEnabled()) {
         this.renderChatPreview(var1);
      } else {
         this.commandSuggestions.render(var1, var2, var3);
      }

      Style var5 = this.getComponentStyleAt((double)var2, (double)var3);
      if (var5 != null && var5.getHoverEvent() != null) {
         this.renderComponentHoverEffect(var1, var5, var2, var3);
      }

      super.render(var1, var2, var3, var4);
   }

   @Override
   public boolean isPauseScreen() {
      return false;
   }

   private void setChatLine(String var1) {
      this.input.setValue(var1);
   }

   @Override
   protected void updateNarrationState(NarrationElementOutput var1) {
      var1.add(NarratedElementType.TITLE, this.getTitle());
      var1.add(NarratedElementType.USAGE, USAGE_TEXT);
      String var2 = this.input.getValue();
      if (!var2.isEmpty()) {
         var1.nest().add(NarratedElementType.TITLE, Component.translatable("chat_screen.message", var2));
      }
   }

   public void renderChatPreview(PoseStack var1) {
      int var2 = (int)(255.0 * (this.minecraft.options.chatOpacity().get() * 0.8999999761581421 + 0.10000000149011612));
      int var3 = (int)(255.0 * this.minecraft.options.textBackgroundOpacity().get());
      int var4 = this.chatPreviewWidth();
      List var5 = this.peekChatPreview();
      int var6 = this.chatPreviewHeight(var5);
      RenderSystem.enableBlend();
      var1.pushPose();
      var1.translate((double)this.chatPreviewLeft(), (double)this.chatPreviewTop(var6), 0.0);
      fill(var1, 0, 0, var4, var6, var3 << 24);
      var1.translate(2.0, 2.0, 0.0);

      for(int var7 = 0; var7 < var5.size(); ++var7) {
         FormattedCharSequence var8 = (FormattedCharSequence)var5.get(var7);
         this.minecraft.font.drawShadow(var1, var8, 0.0F, (float)(var7 * 9), var2 << 24 | 16777215);
      }

      var1.popPose();
      RenderSystem.disableBlend();
   }

   @Nullable
   private Style getComponentStyleAt(double var1, double var3) {
      Style var5 = this.minecraft.gui.getChat().getClickedComponentStyleAt(var1, var3);
      if (var5 == null) {
         var5 = this.getChatPreviewStyleAt(var1, var3);
      }

      return var5;
   }

   @Nullable
   private Style getChatPreviewStyleAt(double var1, double var3) {
      if (this.minecraft.options.hideGui) {
         return null;
      } else {
         List var5 = this.peekChatPreview();
         int var6 = this.chatPreviewHeight(var5);
         if (!(var1 < (double)this.chatPreviewLeft())
            && !(var1 > (double)this.chatPreviewRight())
            && !(var3 < (double)this.chatPreviewTop(var6))
            && !(var3 > (double)this.chatPreviewBottom())) {
            int var7 = this.chatPreviewLeft() + 2;
            int var8 = this.chatPreviewTop(var6) + 2;
            int var9 = (Mth.floor(var3) - var8) / 9;
            if (var9 >= 0 && var9 < var5.size()) {
               FormattedCharSequence var10 = (FormattedCharSequence)var5.get(var9);
               return this.minecraft.font.getSplitter().componentStyleAtWidth(var10, (int)(var1 - (double)var7));
            } else {
               return null;
            }
         } else {
            return null;
         }
      }
   }

   private List<FormattedCharSequence> peekChatPreview() {
      Component var1 = this.chatPreview.peek();
      return var1 != null ? this.font.split(var1, this.chatPreviewWidth()) : List.of(PREVIEW_HINT.getVisualOrderText());
   }

   private int chatPreviewWidth() {
      return this.minecraft.screen.width - 4;
   }

   private int chatPreviewHeight(List<FormattedCharSequence> var1) {
      return Math.max(var1.size(), 1) * 9 + 4;
   }

   private int chatPreviewBottom() {
      return this.minecraft.screen.height - 15;
   }

   private int chatPreviewTop(int var1) {
      return this.chatPreviewBottom() - var1;
   }

   private int chatPreviewLeft() {
      return 2;
   }

   private int chatPreviewRight() {
      return this.minecraft.screen.width - 2;
   }

   public void handleChatInput(String var1, boolean var2) {
      var1 = this.normalizeChatMessage(var1);
      if (!var1.isEmpty()) {
         if (var2) {
            this.minecraft.gui.getChat().addRecentChat(var1);
         }

         Component var3 = this.chatPreview.pull(var1);
         if (var1.startsWith("/")) {
            this.minecraft.player.command(var1.substring(1), var3);
         } else {
            this.minecraft.player.chat(var1, var3);
         }
      }
   }

   public String normalizeChatMessage(String var1) {
      return StringUtils.normalizeSpace(var1.trim());
   }

   public ClientChatPreview getChatPreview() {
      return this.chatPreview;
   }
}
