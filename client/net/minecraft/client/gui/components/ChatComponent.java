package net.minecraft.client.gui.components;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.Optionull;
import net.minecraft.client.GuiMessage;
import net.minecraft.client.GuiMessageTag;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.multiplayer.chat.ChatListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.network.chat.Style;
import net.minecraft.util.ARGB;
import net.minecraft.util.ArrayListDeque;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.player.ChatVisiblity;
import org.slf4j.Logger;

public class ChatComponent {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int MAX_CHAT_HISTORY = 100;
   private static final int MESSAGE_NOT_FOUND = -1;
   private static final int MESSAGE_INDENT = 4;
   private static final int MESSAGE_TAG_MARGIN_LEFT = 4;
   private static final int BOTTOM_MARGIN = 40;
   private static final int TIME_BEFORE_MESSAGE_DELETION = 60;
   private static final Component DELETED_CHAT_MESSAGE;
   private final Minecraft minecraft;
   private final ArrayListDeque<String> recentChat = new ArrayListDeque<String>(100);
   private final List<GuiMessage> allMessages = Lists.newArrayList();
   private final List<GuiMessage.Line> trimmedMessages = Lists.newArrayList();
   private int chatScrollbarPos;
   private boolean newMessageSinceScroll;
   private final List<DelayedMessageDeletion> messageDeletionQueue = new ArrayList();

   public ChatComponent(Minecraft var1) {
      super();
      this.minecraft = var1;
      this.recentChat.addAll(var1.commandHistory().history());
   }

   public void tick() {
      if (!this.messageDeletionQueue.isEmpty()) {
         this.processMessageDeletionQueue();
      }

   }

   private int forEachLine(int var1, int var2, boolean var3, int var4, LineConsumer var5) {
      int var6 = this.getLineHeight();
      int var7 = 0;

      for(int var8 = Math.min(this.trimmedMessages.size() - this.chatScrollbarPos, var1) - 1; var8 >= 0; --var8) {
         int var9 = var8 + this.chatScrollbarPos;
         GuiMessage.Line var10 = (GuiMessage.Line)this.trimmedMessages.get(var9);
         if (var10 != null) {
            int var11 = var2 - var10.addedTime();
            float var12 = var3 ? 1.0F : (float)getTimeFactor(var11);
            if (var12 > 1.0E-5F) {
               ++var7;
               int var13 = var4 - var8 * var6;
               int var14 = var13 - var6;
               var5.accept(0, var14, var13, var10, var8, var12);
            }
         }
      }

      return var7;
   }

   public void render(GuiGraphics var1, int var2, int var3, int var4, boolean var5) {
      if (!this.isChatHidden()) {
         int var6 = this.getLinesPerPage();
         int var7 = this.trimmedMessages.size();
         if (var7 > 0) {
            ProfilerFiller var8 = Profiler.get();
            var8.push("chat");
            float var9 = (float)this.getScale();
            int var10 = Mth.ceil((float)this.getWidth() / var9);
            int var11 = var1.guiHeight();
            var1.pose().pushMatrix();
            var1.pose().scale(var9, var9);
            var1.pose().translate(4.0F, 0.0F);
            int var12 = Mth.floor((float)(var11 - 40) / var9);
            int var13 = this.getMessageEndIndexAt(this.screenToChatX((double)var3), this.screenToChatY((double)var4));
            float var14 = ((Double)this.minecraft.options.chatOpacity().get()).floatValue() * 0.9F + 0.1F;
            float var15 = ((Double)this.minecraft.options.textBackgroundOpacity().get()).floatValue();
            double var16 = (Double)this.minecraft.options.chatLineSpacing().get();
            int var18 = (int)Math.round(-8.0 * (var16 + 1.0) + 4.0 * var16);
            this.forEachLine(var6, var2, var5, var12, (var7x, var8x, var9x, var10x, var11x, var12x) -> {
               var1.fill(var7x - 4, var8x, var7x + var10 + 4 + 4, var9x, ARGB.color(var12x * var15, -16777216));
               GuiMessageTag var13x = var10x.tag();
               if (var13x != null) {
                  int var14x = ARGB.color(var12x * var14, var13x.indicatorColor());
                  var1.fill(var7x - 4, var8x, var7x - 2, var9x, var14x);
                  if (var11x == var13 && var13x.icon() != null) {
                     int var15x = this.getTagIconLeft(var10x);
                     int var10000 = var9x + var18;
                     Objects.requireNonNull(this.minecraft.font);
                     int var16 = var10000 + 9;
                     this.drawTagIcon(var1, var15x, var16, var13x.icon());
                  }
               }

            });
            int var19 = this.forEachLine(var6, var2, var5, var12, (var4x, var5x, var6x, var7x, var8x, var9x) -> {
               int var10 = var6x + var18;
               var1.drawString(this.minecraft.font, var7x.content(), var4x, var10, ARGB.color(var9x * var14, -1));
            });
            long var20 = this.minecraft.getChatListener().queueSize();
            if (var20 > 0L) {
               int var22 = (int)(128.0F * var14);
               int var23 = (int)(255.0F * var15);
               var1.pose().pushMatrix();
               var1.pose().translate(0.0F, (float)var12);
               var1.fill(-2, 0, var10 + 4, 9, var23 << 24);
               var1.drawString(this.minecraft.font, (Component)Component.translatable("chat.queue", var20), 0, 1, ARGB.color(var22, -1));
               var1.pose().popMatrix();
            }

            if (var5) {
               int var30 = this.getLineHeight();
               int var31 = var7 * var30;
               int var24 = var19 * var30;
               int var25 = this.chatScrollbarPos * var24 / var7 - var12;
               int var26 = var24 * var24 / var31;
               if (var31 != var24) {
                  int var27 = var25 > 0 ? 170 : 96;
                  int var28 = this.newMessageSinceScroll ? 13382451 : 3355562;
                  int var29 = var10 + 4;
                  var1.fill(var29, -var25, var29 + 2, -var25 - var26, ARGB.color(var27, var28));
                  var1.fill(var29 + 2, -var25, var29 + 1, -var25 - var26, ARGB.color(var27, 13421772));
               }
            }

            var1.pose().popMatrix();
            var8.pop();
         }
      }
   }

   private void drawTagIcon(GuiGraphics var1, int var2, int var3, GuiMessageTag.Icon var4) {
      int var5 = var3 - var4.height - 1;
      var4.draw(var1, var2, var5);
   }

   private int getTagIconLeft(GuiMessage.Line var1) {
      return this.minecraft.font.width(var1.content()) + 4;
   }

   private boolean isChatHidden() {
      return this.minecraft.options.chatVisibility().get() == ChatVisiblity.HIDDEN;
   }

   private static double getTimeFactor(int var0) {
      double var1 = (double)var0 / 200.0;
      var1 = 1.0 - var1;
      var1 *= 10.0;
      var1 = Mth.clamp(var1, 0.0, 1.0);
      var1 *= var1;
      return var1;
   }

   public void clearMessages(boolean var1) {
      this.minecraft.getChatListener().flushQueue();
      this.messageDeletionQueue.clear();
      this.trimmedMessages.clear();
      this.allMessages.clear();
      if (var1) {
         this.recentChat.clear();
         this.recentChat.addAll(this.minecraft.commandHistory().history());
      }

   }

   public void addMessage(Component var1) {
      this.addMessage(var1, (MessageSignature)null, this.minecraft.isSingleplayer() ? GuiMessageTag.systemSinglePlayer() : GuiMessageTag.system());
   }

   public void addMessage(Component var1, @Nullable MessageSignature var2, @Nullable GuiMessageTag var3) {
      GuiMessage var4 = new GuiMessage(this.minecraft.gui.getGuiTicks(), var1, var2, var3);
      this.logChatMessage(var4);
      this.addMessageToDisplayQueue(var4);
      this.addMessageToQueue(var4);
   }

   private void logChatMessage(GuiMessage var1) {
      String var2 = var1.content().getString().replaceAll("\r", "\\\\r").replaceAll("\n", "\\\\n");
      String var3 = (String)Optionull.map(var1.tag(), GuiMessageTag::logTag);
      if (var3 != null) {
         LOGGER.info("[{}] [CHAT] {}", var3, var2);
      } else {
         LOGGER.info("[CHAT] {}", var2);
      }

   }

   private void addMessageToDisplayQueue(GuiMessage var1) {
      int var2 = Mth.floor((double)this.getWidth() / this.getScale());
      GuiMessageTag.Icon var3 = var1.icon();
      if (var3 != null) {
         var2 -= var3.width + 4 + 2;
      }

      List var4 = ComponentRenderUtils.wrapComponents(var1.content(), var2, this.minecraft.font);
      boolean var5 = this.isChatFocused();

      for(int var6 = 0; var6 < var4.size(); ++var6) {
         FormattedCharSequence var7 = (FormattedCharSequence)var4.get(var6);
         if (var5 && this.chatScrollbarPos > 0) {
            this.newMessageSinceScroll = true;
            this.scrollChat(1);
         }

         boolean var8 = var6 == var4.size() - 1;
         this.trimmedMessages.add(0, new GuiMessage.Line(var1.addedTime(), var7, var1.tag(), var8));
      }

      while(this.trimmedMessages.size() > 100) {
         this.trimmedMessages.remove(this.trimmedMessages.size() - 1);
      }

   }

   private void addMessageToQueue(GuiMessage var1) {
      this.allMessages.add(0, var1);

      while(this.allMessages.size() > 100) {
         this.allMessages.remove(this.allMessages.size() - 1);
      }

   }

   private void processMessageDeletionQueue() {
      int var1 = this.minecraft.gui.getGuiTicks();
      this.messageDeletionQueue.removeIf((var2) -> {
         if (var1 >= var2.deletableAfter()) {
            return this.deleteMessageOrDelay(var2.signature()) == null;
         } else {
            return false;
         }
      });
   }

   public void deleteMessage(MessageSignature var1) {
      DelayedMessageDeletion var2 = this.deleteMessageOrDelay(var1);
      if (var2 != null) {
         this.messageDeletionQueue.add(var2);
      }

   }

   @Nullable
   private DelayedMessageDeletion deleteMessageOrDelay(MessageSignature var1) {
      int var2 = this.minecraft.gui.getGuiTicks();
      ListIterator var3 = this.allMessages.listIterator();

      while(var3.hasNext()) {
         GuiMessage var4 = (GuiMessage)var3.next();
         if (var1.equals(var4.signature())) {
            int var5 = var4.addedTime() + 60;
            if (var2 >= var5) {
               var3.set(this.createDeletedMarker(var4));
               this.refreshTrimmedMessages();
               return null;
            }

            return new DelayedMessageDeletion(var1, var5);
         }
      }

      return null;
   }

   private GuiMessage createDeletedMarker(GuiMessage var1) {
      return new GuiMessage(var1.addedTime(), DELETED_CHAT_MESSAGE, (MessageSignature)null, GuiMessageTag.system());
   }

   public void rescaleChat() {
      this.resetChatScroll();
      this.refreshTrimmedMessages();
   }

   private void refreshTrimmedMessages() {
      this.trimmedMessages.clear();

      for(GuiMessage var2 : Lists.reverse(this.allMessages)) {
         this.addMessageToDisplayQueue(var2);
      }

   }

   public ArrayListDeque<String> getRecentChat() {
      return this.recentChat;
   }

   public void addRecentChat(String var1) {
      if (!var1.equals(this.recentChat.peekLast())) {
         if (this.recentChat.size() >= 100) {
            this.recentChat.removeFirst();
         }

         this.recentChat.addLast(var1);
      }

      if (var1.startsWith("/")) {
         this.minecraft.commandHistory().addCommand(var1);
      }

   }

   public void resetChatScroll() {
      this.chatScrollbarPos = 0;
      this.newMessageSinceScroll = false;
   }

   public void scrollChat(int var1) {
      this.chatScrollbarPos += var1;
      int var2 = this.trimmedMessages.size();
      if (this.chatScrollbarPos > var2 - this.getLinesPerPage()) {
         this.chatScrollbarPos = var2 - this.getLinesPerPage();
      }

      if (this.chatScrollbarPos <= 0) {
         this.chatScrollbarPos = 0;
         this.newMessageSinceScroll = false;
      }

   }

   public boolean handleChatQueueClicked(double var1, double var3) {
      if (this.isChatFocused() && !this.minecraft.options.hideGui && !this.isChatHidden()) {
         ChatListener var5 = this.minecraft.getChatListener();
         if (var5.queueSize() == 0L) {
            return false;
         } else {
            double var6 = var1 - 2.0;
            double var8 = (double)this.minecraft.getWindow().getGuiScaledHeight() - var3 - 40.0;
            if (var6 <= (double)Mth.floor((double)this.getWidth() / this.getScale()) && var8 < 0.0 && var8 > (double)Mth.floor(-9.0 * this.getScale())) {
               var5.acceptNextDelayedMessage();
               return true;
            } else {
               return false;
            }
         }
      } else {
         return false;
      }
   }

   @Nullable
   public Style getClickedComponentStyleAt(double var1, double var3) {
      double var5 = this.screenToChatX(var1);
      double var7 = this.screenToChatY(var3);
      int var9 = this.getMessageLineIndexAt(var5, var7);
      if (var9 >= 0 && var9 < this.trimmedMessages.size()) {
         GuiMessage.Line var10 = (GuiMessage.Line)this.trimmedMessages.get(var9);
         return this.minecraft.font.getSplitter().componentStyleAtWidth(var10.content(), Mth.floor(var5));
      } else {
         return null;
      }
   }

   @Nullable
   public GuiMessageTag getMessageTagAt(double var1, double var3) {
      double var5 = this.screenToChatX(var1);
      double var7 = this.screenToChatY(var3);
      int var9 = this.getMessageEndIndexAt(var5, var7);
      if (var9 >= 0 && var9 < this.trimmedMessages.size()) {
         GuiMessage.Line var10 = (GuiMessage.Line)this.trimmedMessages.get(var9);
         GuiMessageTag var11 = var10.tag();
         if (var11 != null && this.hasSelectedMessageTag(var5, var10, var11)) {
            return var11;
         }
      }

      return null;
   }

   private boolean hasSelectedMessageTag(double var1, GuiMessage.Line var3, GuiMessageTag var4) {
      if (var1 < 0.0) {
         return true;
      } else {
         GuiMessageTag.Icon var5 = var4.icon();
         if (var5 == null) {
            return false;
         } else {
            int var6 = this.getTagIconLeft(var3);
            int var7 = var6 + var5.width;
            return var1 >= (double)var6 && var1 <= (double)var7;
         }
      }
   }

   private double screenToChatX(double var1) {
      return var1 / this.getScale() - 4.0;
   }

   private double screenToChatY(double var1) {
      double var3 = (double)this.minecraft.getWindow().getGuiScaledHeight() - var1 - 40.0;
      return var3 / (this.getScale() * (double)this.getLineHeight());
   }

   private int getMessageEndIndexAt(double var1, double var3) {
      int var5 = this.getMessageLineIndexAt(var1, var3);
      if (var5 == -1) {
         return -1;
      } else {
         while(var5 >= 0) {
            if (((GuiMessage.Line)this.trimmedMessages.get(var5)).endOfEntry()) {
               return var5;
            }

            --var5;
         }

         return var5;
      }
   }

   private int getMessageLineIndexAt(double var1, double var3) {
      if (this.isChatFocused() && !this.isChatHidden()) {
         if (!(var1 < -4.0) && !(var1 > (double)Mth.floor((double)this.getWidth() / this.getScale()))) {
            int var5 = Math.min(this.getLinesPerPage(), this.trimmedMessages.size());
            if (var3 >= 0.0 && var3 < (double)var5) {
               int var6 = Mth.floor(var3 + (double)this.chatScrollbarPos);
               if (var6 >= 0 && var6 < this.trimmedMessages.size()) {
                  return var6;
               }
            }

            return -1;
         } else {
            return -1;
         }
      } else {
         return -1;
      }
   }

   public boolean isChatFocused() {
      return this.minecraft.screen instanceof ChatScreen;
   }

   public int getWidth() {
      return getWidth((Double)this.minecraft.options.chatWidth().get());
   }

   public int getHeight() {
      return getHeight(this.isChatFocused() ? (Double)this.minecraft.options.chatHeightFocused().get() : (Double)this.minecraft.options.chatHeightUnfocused().get());
   }

   public double getScale() {
      return (Double)this.minecraft.options.chatScale().get();
   }

   public static int getWidth(double var0) {
      boolean var2 = true;
      boolean var3 = true;
      return Mth.floor(var0 * 280.0 + 40.0);
   }

   public static int getHeight(double var0) {
      boolean var2 = true;
      boolean var3 = true;
      return Mth.floor(var0 * 160.0 + 20.0);
   }

   public static double defaultUnfocusedPct() {
      boolean var0 = true;
      boolean var1 = true;
      return 70.0 / (double)(getHeight(1.0) - 20);
   }

   public int getLinesPerPage() {
      return this.getHeight() / this.getLineHeight();
   }

   private int getLineHeight() {
      Objects.requireNonNull(this.minecraft.font);
      return (int)(9.0 * ((Double)this.minecraft.options.chatLineSpacing().get() + 1.0));
   }

   public State storeState() {
      return new State(List.copyOf(this.allMessages), List.copyOf(this.recentChat), List.copyOf(this.messageDeletionQueue));
   }

   public void restoreState(State var1) {
      this.recentChat.clear();
      this.recentChat.addAll(var1.history);
      this.messageDeletionQueue.clear();
      this.messageDeletionQueue.addAll(var1.delayedMessageDeletions);
      this.allMessages.clear();
      this.allMessages.addAll(var1.messages);
      this.refreshTrimmedMessages();
   }

   static {
      DELETED_CHAT_MESSAGE = Component.translatable("chat.deleted_marker").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC);
   }

   static record DelayedMessageDeletion(MessageSignature signature, int deletableAfter) {
      DelayedMessageDeletion(MessageSignature var1, int var2) {
         super();
         this.signature = var1;
         this.deletableAfter = var2;
      }
   }

   public static class State {
      final List<GuiMessage> messages;
      final List<String> history;
      final List<DelayedMessageDeletion> delayedMessageDeletions;

      public State(List<GuiMessage> var1, List<String> var2, List<DelayedMessageDeletion> var3) {
         super();
         this.messages = var1;
         this.history = var2;
         this.delayedMessageDeletions = var3;
      }
   }

   @FunctionalInterface
   interface LineConsumer {
      void accept(int var1, int var2, int var3, GuiMessage.Line var4, int var5, float var6);
   }
}
