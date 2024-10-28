package net.minecraft.network.chat;

import com.mojang.logging.LogUtils;
import java.util.function.BooleanSupplier;
import javax.annotation.Nullable;
import net.minecraft.util.SignatureValidator;
import org.slf4j.Logger;

@FunctionalInterface
public interface SignedMessageValidator {
   Logger LOGGER = LogUtils.getLogger();
   SignedMessageValidator ACCEPT_UNSIGNED = PlayerChatMessage::removeSignature;
   SignedMessageValidator REJECT_ALL = (var0) -> {
      LOGGER.error("Received chat message from {}, but they have no chat session initialized and secure chat is enforced", var0.sender());
      return null;
   };

   @Nullable
   PlayerChatMessage updateAndValidate(PlayerChatMessage var1);

   public static class KeyBased implements SignedMessageValidator {
      private final SignatureValidator validator;
      private final BooleanSupplier expired;
      @Nullable
      private PlayerChatMessage lastMessage;
      private boolean isChainValid = true;

      public KeyBased(SignatureValidator var1, BooleanSupplier var2) {
         super();
         this.validator = var1;
         this.expired = var2;
      }

      private boolean validateChain(PlayerChatMessage var1) {
         if (var1.equals(this.lastMessage)) {
            return true;
         } else if (this.lastMessage != null && !var1.link().isDescendantOf(this.lastMessage.link())) {
            LOGGER.error("Received out-of-order chat message from {}: expected index > {} for session {}, but was {} for session {}", new Object[]{var1.sender(), this.lastMessage.link().index(), this.lastMessage.link().sessionId(), var1.link().index(), var1.link().sessionId()});
            return false;
         } else {
            return true;
         }
      }

      private boolean validate(PlayerChatMessage var1) {
         if (this.expired.getAsBoolean()) {
            LOGGER.error("Received message from player with expired profile public key: {}", var1);
            return false;
         } else if (!var1.verify(this.validator)) {
            LOGGER.error("Received message with invalid signature from {}", var1.sender());
            return false;
         } else {
            return this.validateChain(var1);
         }
      }

      @Nullable
      public PlayerChatMessage updateAndValidate(PlayerChatMessage var1) {
         this.isChainValid = this.isChainValid && this.validate(var1);
         if (!this.isChainValid) {
            return null;
         } else {
            this.lastMessage = var1;
            return var1;
         }
      }
   }
}
