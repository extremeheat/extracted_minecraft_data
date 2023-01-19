package net.minecraft.client.multiplayer.chat;

import com.google.common.collect.Queues;
import com.mojang.authlib.GameProfile;
import java.time.Instant;
import java.util.Deque;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.GuiMessageTag;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FilterMask;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.network.chat.MessageSigner;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.network.chat.SignedMessageHeader;
import net.minecraft.network.chat.SignedMessageValidator;
import net.minecraft.util.StringDecomposer;
import org.apache.commons.lang3.StringUtils;

public class ChatListener {
   private static final Component CHAT_VALIDATION_FAILED_ERROR = Component.translatable("multiplayer.disconnect.chat_validation_failed");
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
      return this.delayedMessageQueue.stream().filter(ChatListener.Message::isVisible).count();
   }

   public void clearQueue() {
      this.delayedMessageQueue.forEach(var0 -> {
         var0.remove();
         var0.accept();
      });
      this.delayedMessageQueue.clear();
   }

   public boolean removeFromDelayedMessageQueue(MessageSignature var1) {
      for(ChatListener.Message var3 : this.delayedMessageQueue) {
         if (var3.removeIfSignatureMatches(var1)) {
            return true;
         }
      }

      return false;
   }

   private boolean willDelayMessages() {
      return this.messageDelay > 0L && Util.getMillis() < this.previousMessageTime + this.messageDelay;
   }

   private void handleMessage(ChatListener.Message var1) {
      if (this.willDelayMessages()) {
         this.delayedMessageQueue.add(var1);
      } else {
         var1.accept();
      }
   }

   public void handleChatMessage(final PlayerChatMessage var1, final ChatType.Bound var2) {
      final boolean var3 = this.minecraft.options.onlyShowSecureChat().get();
      final PlayerChatMessage var4 = var3 ? var1.removeUnsignedContent() : var1;
      final Component var5 = var2.decorate(var4.serverContent());
      MessageSigner var6 = var1.signer();
      if (!var6.isSystem()) {
         final PlayerInfo var7 = this.resolveSenderPlayer(var6.profileId());
         final Instant var8 = Instant.now();
         this.handleMessage(new ChatListener.Message() {
            private boolean removed;

            @Override
            public boolean accept() {
               if (this.removed) {
                  byte[] var1x = var1.signedBody().hash().asBytes();
                  ChatListener.this.processPlayerChatHeader(var1.signedHeader(), var1.headerSignature(), var1x);
                  return false;
               } else {
                  return ChatListener.this.processPlayerChatMessage(var2, var1, var5, var7, var3, var8);
               }
            }

            @Override
            public boolean removeIfSignatureMatches(MessageSignature var1x) {
               if (var1.headerSignature().equals(var1x)) {
                  this.removed = true;
                  return true;
               } else {
                  return false;
               }
            }

            @Override
            public void remove() {
               this.removed = true;
            }

            @Override
            public boolean isVisible() {
               return !this.removed;
            }
         });
      } else {
         this.handleMessage(new ChatListener.Message() {
            @Override
            public boolean accept() {
               return ChatListener.this.processNonPlayerChatMessage(var2, var4, var5);
            }

            @Override
            public boolean isVisible() {
               return true;
            }
         });
      }
   }

   public void handleChatHeader(SignedMessageHeader var1, MessageSignature var2, byte[] var3) {
      this.handleMessage(() -> this.processPlayerChatHeader(var1, var2, var3));
   }

   boolean processPlayerChatMessage(ChatType.Bound var1, PlayerChatMessage var2, Component var3, @Nullable PlayerInfo var4, boolean var5, Instant var6) {
      boolean var7 = this.showMessageToPlayer(var1, var2, var3, var4, var5, var6);
      ClientPacketListener var8 = this.minecraft.getConnection();
      if (var8 != null) {
         var8.markMessageAsProcessed(var2, var7);
      }

      return var7;
   }

   private boolean showMessageToPlayer(ChatType.Bound var1, PlayerChatMessage var2, Component var3, @Nullable PlayerInfo var4, boolean var5, Instant var6) {
      ChatTrustLevel var7 = this.evaluateTrustLevel(var2, var3, var4, var6);
      if (var7 == ChatTrustLevel.BROKEN_CHAIN) {
         this.onChatChainBroken();
         return true;
      } else if (var5 && var7.isNotSecure()) {
         return false;
      } else if (!this.minecraft.isBlocked(var2.signer().profileId()) && !var2.isFullyFiltered()) {
         GuiMessageTag var8 = var7.createTag(var2);
         MessageSignature var9 = var2.headerSignature();
         FilterMask var10 = var2.filterMask();
         if (var10.isEmpty()) {
            this.minecraft.gui.getChat().addMessage(var3, var9, var8);
            this.narrateChatMessage(var1, var2.serverContent());
         } else {
            Component var11 = var10.apply(var2.signedContent());
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

   boolean processNonPlayerChatMessage(ChatType.Bound var1, PlayerChatMessage var2, Component var3) {
      this.minecraft.gui.getChat().addMessage(var3);
      this.narrateChatMessage(var1, var2.serverContent());
      this.logSystemMessage(var3, var2.timeStamp());
      this.previousMessageTime = Util.getMillis();
      return true;
   }

   boolean processPlayerChatHeader(SignedMessageHeader var1, MessageSignature var2, byte[] var3) {
      PlayerInfo var4 = this.resolveSenderPlayer(var1.sender());
      if (var4 != null) {
         SignedMessageValidator.State var5 = var4.getMessageValidator().validateHeader(var1, var2, var3);
         if (var5 == SignedMessageValidator.State.BROKEN_CHAIN) {
            this.onChatChainBroken();
            return true;
         }
      }

      this.logPlayerHeader(var1, var2, var3);
      return false;
   }

   private void onChatChainBroken() {
      ClientPacketListener var1 = this.minecraft.getConnection();
      if (var1 != null) {
         var1.getConnection().disconnect(CHAT_VALIDATION_FAILED_ERROR);
      }
   }

   private void narrateChatMessage(ChatType.Bound var1, Component var2) {
      this.minecraft.getNarrator().sayChatNow(() -> var1.decorateNarration(var2));
   }

   private ChatTrustLevel evaluateTrustLevel(PlayerChatMessage var1, Component var2, @Nullable PlayerInfo var3, Instant var4) {
      return this.isSenderLocalPlayer(var1.signer().profileId()) ? ChatTrustLevel.SECURE : ChatTrustLevel.evaluate(var1, var2, var3, var4);
   }

   private void logPlayerMessage(PlayerChatMessage var1, ChatType.Bound var2, @Nullable PlayerInfo var3, ChatTrustLevel var4) {
      GameProfile var5;
      if (var3 != null) {
         var5 = var3.getProfile();
      } else {
         var5 = new GameProfile(var1.signer().profileId(), var2.name().getString());
      }

      ChatLog var6 = this.minecraft.getReportingContext().chatLog();
      var6.push(LoggedChatMessage.player(var5, var2.name(), var1, var4));
   }

   private void logSystemMessage(Component var1, Instant var2) {
      ChatLog var3 = this.minecraft.getReportingContext().chatLog();
      var3.push(LoggedChatMessage.system(var1, var2));
   }

   private void logPlayerHeader(SignedMessageHeader var1, MessageSignature var2, byte[] var3) {
      ChatLog var4 = this.minecraft.getReportingContext().chatLog();
      var4.push(LoggedChatMessageLink.header(var1, var2, var3));
   }

   @Nullable
   private PlayerInfo resolveSenderPlayer(UUID var1) {
      ClientPacketListener var2 = this.minecraft.getConnection();
      return var2 != null ? var2.getPlayerInfo(var1) : null;
   }

   public void handleSystemMessage(Component var1, boolean var2) {
      if (!this.minecraft.options.hideMatchedNames().get() || !this.minecraft.isBlocked(this.guessChatUUID(var1))) {
         if (var2) {
            this.minecraft.gui.setOverlayMessage(var1, false);
         } else {
            this.minecraft.gui.getChat().addMessage(var1);
            this.logSystemMessage(var1, Instant.now());
         }

         this.minecraft.getNarrator().sayNow(var1);
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

   interface Message {
      default boolean removeIfSignatureMatches(MessageSignature var1) {
         return false;
      }

      default void remove() {
      }

      boolean accept();

      default boolean isVisible() {
         return false;
      }
   }
}
