package net.minecraft.client.multiplayer.chat;

import com.google.common.collect.Queues;
import com.mojang.authlib.GameProfile;
import java.time.Instant;
import java.util.Deque;
import java.util.UUID;
import java.util.function.BooleanSupplier;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.GuiMessageTag;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FilterMask;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.util.StringDecomposer;
import org.apache.commons.lang3.StringUtils;

public class ChatListener {
   private static final Component CHAT_VALIDATION_ERROR = Component.translatable("chat.validation_error").withStyle(ChatFormatting.RED, ChatFormatting.ITALIC);
   private final Minecraft minecraft;
   private final Deque<ChatListener.Message> delayedMessageQueue = Queues.newArrayDeque();
   private long messageDelay;
   private long previousMessageTime;

   public ChatListener(Minecraft var1) {
      super();
      this.minecraft = var1;
   }

   public void tick() {
      if (this.messageDelay != 0L) {
         if (Util.getMillis() >= this.previousMessageTime + this.messageDelay) {
            ChatListener.Message var1 = this.delayedMessageQueue.poll();

            while(var1 != null && !var1.accept()) {
               var1 = this.delayedMessageQueue.poll();
            }
         }
      }
   }

   public void setMessageDelay(double var1) {
      long var3 = (long)(var1 * 1000.0);
      if (var3 == 0L && this.messageDelay > 0L) {
         this.delayedMessageQueue.forEach(ChatListener.Message::accept);
         this.delayedMessageQueue.clear();
      }

      this.messageDelay = var3;
   }

   public void acceptNextDelayedMessage() {
      this.delayedMessageQueue.remove().accept();
   }

   public long queueSize() {
      return (long)this.delayedMessageQueue.size();
   }

   public void clearQueue() {
      this.delayedMessageQueue.forEach(ChatListener.Message::accept);
      this.delayedMessageQueue.clear();
   }

   public boolean removeFromDelayedMessageQueue(MessageSignature var1) {
      return this.delayedMessageQueue.removeIf(var1x -> var1.equals(var1x.signature()));
   }

   private boolean willDelayMessages() {
      return this.messageDelay > 0L && Util.getMillis() < this.previousMessageTime + this.messageDelay;
   }

   private void handleMessage(@Nullable MessageSignature var1, BooleanSupplier var2) {
      if (this.willDelayMessages()) {
         this.delayedMessageQueue.add(new ChatListener.Message(var1, var2));
      } else {
         var2.getAsBoolean();
      }
   }

   public void handlePlayerChatMessage(PlayerChatMessage var1, GameProfile var2, ChatType.Bound var3) {
      boolean var4 = this.minecraft.options.onlyShowSecureChat().get();
      PlayerChatMessage var5 = var4 ? var1.removeUnsignedContent() : var1;
      Component var6 = var3.decorate(var5.decoratedContent());
      Instant var7 = Instant.now();
      this.handleMessage(var1.signature(), () -> {
         boolean var7x = this.showMessageToPlayer(var3, var1, var6, var2, var4, var7);
         ClientPacketListener var8 = this.minecraft.getConnection();
         if (var8 != null) {
            var8.markMessageAsProcessed(var1, var7x);
         }

         return var7x;
      });
   }

   public void handleChatMessageError(UUID var1, ChatType.Bound var2) {
      this.handleMessage(null, () -> {
         if (this.minecraft.isBlocked(var1)) {
            return false;
         } else {
            Component var3 = var2.decorate(CHAT_VALIDATION_ERROR);
            this.minecraft.gui.getChat().addMessage(var3, null, GuiMessageTag.chatError());
            this.previousMessageTime = Util.getMillis();
            return true;
         }
      });
   }

   public void handleDisguisedChatMessage(Component var1, ChatType.Bound var2) {
      Instant var3 = Instant.now();
      this.handleMessage(null, () -> {
         Component var4 = var2.decorate(var1);
         this.minecraft.gui.getChat().addMessage(var4);
         this.narrateChatMessage(var2, var1);
         this.logSystemMessage(var4, var3);
         this.previousMessageTime = Util.getMillis();
         return true;
      });
   }

   private boolean showMessageToPlayer(ChatType.Bound var1, PlayerChatMessage var2, Component var3, GameProfile var4, boolean var5, Instant var6) {
      ChatTrustLevel var7 = this.evaluateTrustLevel(var2, var3, var6);
      if (var5 && var7.isNotSecure()) {
         return false;
      } else if (!this.minecraft.isBlocked(var2.sender()) && !var2.isFullyFiltered()) {
         GuiMessageTag var8 = var7.createTag(var2);
         MessageSignature var9 = var2.signature();
         FilterMask var10 = var2.filterMask();
         if (var10.isEmpty()) {
            this.minecraft.gui.getChat().addMessage(var3, var9, var8);
            this.narrateChatMessage(var1, var2.decoratedContent());
         } else {
            Component var11 = var10.applyWithFormatting(var2.signedContent());
            if (var11 != null) {
               this.minecraft.gui.getChat().addMessage(var1.decorate(var11), var9, var8);
               this.narrateChatMessage(var1, var11);
            }
         }

         this.logPlayerMessage(var2, var1, var4, var7);
         this.previousMessageTime = Util.getMillis();
         return true;
      } else {
         return false;
      }
   }

   private void narrateChatMessage(ChatType.Bound var1, Component var2) {
      this.minecraft.getNarrator().sayChat(var1.decorateNarration(var2));
   }

   private ChatTrustLevel evaluateTrustLevel(PlayerChatMessage var1, Component var2, Instant var3) {
      return this.isSenderLocalPlayer(var1.sender()) ? ChatTrustLevel.SECURE : ChatTrustLevel.evaluate(var1, var2, var3);
   }

   private void logPlayerMessage(PlayerChatMessage var1, ChatType.Bound var2, GameProfile var3, ChatTrustLevel var4) {
      ChatLog var5 = this.minecraft.getReportingContext().chatLog();
      var5.push(LoggedChatMessage.player(var3, var1, var4));
   }

   private void logSystemMessage(Component var1, Instant var2) {
      ChatLog var3 = this.minecraft.getReportingContext().chatLog();
      var3.push(LoggedChatMessage.system(var1, var2));
   }

   public void handleSystemMessage(Component var1, boolean var2) {
      if (!this.minecraft.options.hideMatchedNames().get() || !this.minecraft.isBlocked(this.guessChatUUID(var1))) {
         if (var2) {
            this.minecraft.gui.setOverlayMessage(var1, false);
         } else {
            this.minecraft.gui.getChat().addMessage(var1);
            this.logSystemMessage(var1, Instant.now());
         }

         this.minecraft.getNarrator().say(var1);
      }
   }

   private UUID guessChatUUID(Component var1) {
      String var2 = StringDecomposer.getPlainText(var1);
      String var3 = StringUtils.substringBetween(var2, "<", ">");
      return var3 == null ? Util.NIL_UUID : this.minecraft.getPlayerSocialManager().getDiscoveredUUID(var3);
   }

   private boolean isSenderLocalPlayer(UUID var1) {
      if (this.minecraft.isLocalServer() && this.minecraft.player != null) {
         UUID var2 = this.minecraft.player.getGameProfile().getId();
         return var2.equals(var1);
      } else {
         return false;
      }
   }

   static record Message(@Nullable MessageSignature a, BooleanSupplier b) {
      @Nullable
      private final MessageSignature signature;
      private final BooleanSupplier handler;

      Message(@Nullable MessageSignature var1, BooleanSupplier var2) {
         super();
         this.signature = var1;
         this.handler = var2;
      }

      public boolean accept() {
         return this.handler.getAsBoolean();
      }
   }
}
