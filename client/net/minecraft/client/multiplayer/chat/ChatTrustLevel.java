package net.minecraft.client.multiplayer.chat;

import java.time.Instant;
import javax.annotation.Nullable;
import net.minecraft.client.GuiMessageTag;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.network.chat.SignedMessageValidator;

public enum ChatTrustLevel {
   SECURE,
   MODIFIED,
   FILTERED,
   NOT_SECURE,
   BROKEN_CHAIN;

   private ChatTrustLevel() {
   }

   public static ChatTrustLevel evaluate(PlayerChatMessage var0, Component var1, @Nullable PlayerInfo var2, Instant var3) {
      if (var2 == null) {
         return NOT_SECURE;
      } else {
         SignedMessageValidator.State var4 = var2.getMessageValidator().validateMessage(var0);
         if (var4 == SignedMessageValidator.State.BROKEN_CHAIN) {
            return BROKEN_CHAIN;
         } else if (var4 == SignedMessageValidator.State.NOT_SECURE) {
            return NOT_SECURE;
         } else if (var0.hasExpiredClient(var3)) {
            return NOT_SECURE;
         } else if (!var0.filterMask().isEmpty()) {
            return FILTERED;
         } else if (var0.unsignedContent().isPresent()) {
            return MODIFIED;
         } else {
            return !var1.contains(var0.signedContent().decorated()) ? MODIFIED : SECURE;
         }
      }
   }

   public boolean isNotSecure() {
      return this == NOT_SECURE || this == BROKEN_CHAIN;
   }

   @Nullable
   public GuiMessageTag createTag(PlayerChatMessage var1) {
      GuiMessageTag var10000;
      switch (this) {
         case MODIFIED:
            var10000 = GuiMessageTag.chatModified(var1.signedContent().plain());
            break;
         case FILTERED:
            var10000 = GuiMessageTag.chatFiltered();
            break;
         case NOT_SECURE:
            var10000 = GuiMessageTag.chatNotSecure();
            break;
         default:
            var10000 = null;
      }

      return var10000;
   }

   // $FF: synthetic method
   private static ChatTrustLevel[] $values() {
      return new ChatTrustLevel[]{SECURE, MODIFIED, FILTERED, NOT_SECURE, BROKEN_CHAIN};
   }
}
