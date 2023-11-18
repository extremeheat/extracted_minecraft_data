package net.minecraft.client.gui.components;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
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
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
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
   private static final Component DELETED_CHAT_MESSAGE = Component.translatable("chat.deleted_marker").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC);
   private final Minecraft minecraft;
   private final List<String> recentChat = Lists.newArrayList();
   private final List<GuiMessage> allMessages = Lists.newArrayList();
   private final List<GuiMessage.Line> trimmedMessages = Lists.newArrayList();
   private int chatScrollbarPos;
   private boolean newMessageSinceScroll;
   private final List<ChatComponent.DelayedMessageDeletion> messageDeletionQueue = new ArrayList<>();

   public ChatComponent(Minecraft var1) {
      super();
      this.minecraft = var1;
   }

   public void tick() {
      if (!this.messageDeletionQueue.isEmpty()) {
         this.processMessageDeletionQueue();
      }
   }

   public void render(GuiGraphics var1, int var2, int var3, int var4) {
      if (!this.isChatHidden()) {
         int var5 = this.getLinesPerPage();
         int var6 = this.trimmedMessages.size();
         if (var6 > 0) {
            boolean var7 = this.isChatFocused();
            float var8 = (float)this.getScale();
            int var9 = Mth.ceil((float)this.getWidth() / var8);
            int var10 = var1.guiHeight();
            var1.pose().pushPose();
            var1.pose().scale(var8, var8, 1.0F);
            var1.pose().translate(4.0F, 0.0F, 0.0F);
            int var11 = Mth.floor((float)(var10 - 40) / var8);
            int var12 = this.getMessageEndIndexAt(this.screenToChatX((double)var3), this.screenToChatY((double)var4));
            double var13 = this.minecraft.options.chatOpacity().get() * 0.8999999761581421 + 0.10000000149011612;
            double var15 = this.minecraft.options.textBackgroundOpacity().get();
            double var17 = this.minecraft.options.chatLineSpacing().get();
            int var19 = this.getLineHeight();
            int var20 = (int)Math.round(-8.0 * (var17 + 1.0) + 4.0 * var17);
            int var21 = 0;

            for(int var22 = 0; var22 + this.chatScrollbarPos < this.trimmedMessages.size() && var22 < var5; ++var22) {
               int var23 = var22 + this.chatScrollbarPos;
               GuiMessage.Line var24 = this.trimmedMessages.get(var23);
               if (var24 != null) {
                  int var25 = var2 - var24.addedTime();
                  if (var25 < 200 || var7) {
                     double var26 = var7 ? 1.0 : getTimeFactor(var25);
                     int var28 = (int)(255.0 * var26 * var13);
                     int var29 = (int)(255.0 * var26 * var15);
                     ++var21;
                     if (var28 > 3) {
                        boolean var30 = false;
                        int var31 = var11 - var22 * var19;
                        int var32 = var31 + var20;
                        var1.pose().pushPose();
                        var1.pose().translate(0.0F, 0.0F, 50.0F);
                        var1.fill(-4, var31 - var19, 0 + var9 + 4 + 4, var31, var29 << 24);
                        GuiMessageTag var33 = var24.tag();
                        if (var33 != null) {
                           int var34 = var33.indicatorColor() | var28 << 24;
                           var1.fill(-4, var31 - var19, -2, var31, var34);
                           if (var23 == var12 && var33.icon() != null) {
                              int var35 = this.getTagIconLeft(var24);
                              int var36 = var32 + 9;
                              this.drawTagIcon(var1, var35, var36, var33.icon());
                           }
                        }

                        var1.pose().translate(0.0F, 0.0F, 50.0F);
                        var1.drawString(this.minecraft.font, var24.content(), 0, var32, 16777215 + (var28 << 24));
                        var1.pose().popPose();
                     }
                  }
               }
            }

            long var37 = this.minecraft.getChatListener().queueSize();
            if (var37 > 0L) {
               int var38 = (int)(128.0 * var13);
               int var40 = (int)(255.0 * var15);
               var1.pose().pushPose();
               var1.pose().translate(0.0F, (float)var11, 50.0F);
               var1.fill(-2, 0, var9 + 4, 9, var40 << 24);
               var1.pose().translate(0.0F, 0.0F, 50.0F);
               var1.drawString(this.minecraft.font, Component.translatable("chat.queue", var37), 0, 1, 16777215 + (var38 << 24));
               var1.pose().popPose();
            }

            if (var7) {
               int var39 = this.getLineHeight();
               int var41 = var6 * var39;
               int var42 = var21 * var39;
               int var27 = this.chatScrollbarPos * var42 / var6 - var11;
               int var43 = var42 * var42 / var41;
               if (var41 != var42) {
                  int var44 = var27 > 0 ? 170 : 96;
                  int var45 = this.newMessageSinceScroll ? 13382451 : 3355562;
                  int var46 = var9 + 4;
                  var1.fill(var46, -var27, var46 + 2, -var27 - var43, var45 + (var44 << 24));
                  var1.fill(var46 + 2, -var27, var46 + 1, -var27 - var43, 13421772 + (var44 << 24));
               }
            }

            var1.pose().popPose();
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
      return var1 * var1;
   }

   public void clearMessages(boolean var1) {
      this.minecraft.getChatListener().clearQueue();
      this.messageDeletionQueue.clear();
      this.trimmedMessages.clear();
      this.allMessages.clear();
      if (var1) {
         this.recentChat.clear();
      }
   }

   public void addMessage(Component var1) {
      this.addMessage(var1, null, this.minecraft.isSingleplayer() ? GuiMessageTag.systemSinglePlayer() : GuiMessageTag.system());
   }

   public void addMessage(Component var1, @Nullable MessageSignature var2, @Nullable GuiMessageTag var3) {
      this.logChatMessage(var1, var3);
      this.addMessage(var1, var2, this.minecraft.gui.getGuiTicks(), var3, false);
   }

   private void logChatMessage(Component var1, @Nullable GuiMessageTag var2) {
      String var3 = var1.getString().replaceAll("\r", "\\\\r").replaceAll("\n", "\\\\n");
      String var4 = Optionull.map(var2, GuiMessageTag::logTag);
      if (var4 != null) {
         LOGGER.info("[{}] [CHAT] {}", var4, var3);
      } else {
         LOGGER.info("[CHAT] {}", var3);
      }
   }

   private void addMessage(Component var1, @Nullable MessageSignature var2, int var3, @Nullable GuiMessageTag var4, boolean var5) {
      int var6 = Mth.floor((double)this.getWidth() / this.getScale());
      if (var4 != null && var4.icon() != null) {
         var6 -= var4.icon().width + 4 + 2;
      }

      List var7 = ComponentRenderUtils.wrapComponents(var1, var6, this.minecraft.font);
      boolean var8 = this.isChatFocused();

      for(int var9 = 0; var9 < var7.size(); ++var9) {
         FormattedCharSequence var10 = (FormattedCharSequence)var7.get(var9);
         if (var8 && this.chatScrollbarPos > 0) {
            this.newMessageSinceScroll = true;
            this.scrollChat(1);
         }

         boolean var11 = var9 == var7.size() - 1;
         this.trimmedMessages.add(0, new GuiMessage.Line(var3, var10, var4, var11));
      }

      while(this.trimmedMessages.size() > 100) {
         this.trimmedMessages.remove(this.trimmedMessages.size() - 1);
      }

      if (!var5) {
         this.allMessages.add(0, new GuiMessage(var3, var1, var2, var4));

         while(this.allMessages.size() > 100) {
            this.allMessages.remove(this.allMessages.size() - 1);
         }
      }
   }

   private void processMessageDeletionQueue() {
      int var1 = this.minecraft.gui.getGuiTicks();
      this.messageDeletionQueue.removeIf(var2 -> {
         if (var1 >= var2.deletableAfter()) {
            return this.deleteMessageOrDelay(var2.signature()) == null;
         } else {
            return false;
         }
      });
   }

   public void deleteMessage(MessageSignature var1) {
      ChatComponent.DelayedMessageDeletion var2 = this.deleteMessageOrDelay(var1);
      if (var2 != null) {
         this.messageDeletionQueue.add(var2);
      }
   }

   @Nullable
   private ChatComponent.DelayedMessageDeletion deleteMessageOrDelay(MessageSignature var1) {
      int var2 = this.minecraft.gui.getGuiTicks();
      ListIterator var3 = this.allMessages.listIterator();

      while(var3.hasNext()) {
         GuiMessage var4 = (GuiMessage)var3.next();
         if (var1.equals(var4.signature())) {
            int var5 = var4.addedTime() + 60;
            if (var2 >= var5) {
               var3.set(this.createDeletedMarker(var4));
               this.refreshTrimmedMessage();
               return null;
            }

            return new ChatComponent.DelayedMessageDeletion(var1, var5);
         }
      }

      return null;
   }

   private GuiMessage createDeletedMarker(GuiMessage var1) {
      return new GuiMessage(var1.addedTime(), DELETED_CHAT_MESSAGE, null, GuiMessageTag.system());
   }

   public void rescaleChat() {
      this.resetChatScroll();
      this.refreshTrimmedMessage();
   }

   private void refreshTrimmedMessage() {
      this.trimmedMessages.clear();

      for(int var1 = this.allMessages.size() - 1; var1 >= 0; --var1) {
         GuiMessage var2 = this.allMessages.get(var1);
         this.addMessage(var2.content(), var2.signature(), var2.addedTime(), var2.tag(), true);
      }
   }

   public List<String> getRecentChat() {
      return this.recentChat;
   }

   public void addRecentChat(String var1) {
      if (this.recentChat.isEmpty() || !this.recentChat.get(this.recentChat.size() - 1).equals(var1)) {
         this.recentChat.add(var1);
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
         GuiMessage.Line var10 = this.trimmedMessages.get(var9);
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
         GuiMessage.Line var10 = this.trimmedMessages.get(var9);
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
            if (this.trimmedMessages.get(var5).endOfEntry()) {
               return var5;
            }

            --var5;
         }

         return var5;
      }
   }

   private int getMessageLineIndexAt(double var1, double var3) {
      if (this.isChatFocused() && !this.minecraft.options.hideGui && !this.isChatHidden()) {
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

   private boolean isChatFocused() {
      return this.minecraft.screen instanceof ChatScreen;
   }

   public int getWidth() {
      return getWidth(this.minecraft.options.chatWidth().get());
   }

   public int getHeight() {
      return getHeight(this.isChatFocused() ? this.minecraft.options.chatHeightFocused().get() : this.minecraft.options.chatHeightUnfocused().get());
   }

   public double getScale() {
      return this.minecraft.options.chatScale().get();
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
      return (int)(9.0 * (this.minecraft.options.chatLineSpacing().get() + 1.0));
   }

   static record DelayedMessageDeletion(MessageSignature a, int b) {
      private final MessageSignature signature;
      private final int deletableAfter;

      DelayedMessageDeletion(MessageSignature var1, int var2) {
         super();
         this.signature = var1;
         this.deletableAfter = var2;
      }
   }
}
