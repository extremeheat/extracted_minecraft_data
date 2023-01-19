package net.minecraft.network.chat;

import javax.annotation.Nullable;
import net.minecraft.util.SignatureValidator;

@FunctionalInterface
public interface SignedMessageValidator {
   SignedMessageValidator ACCEPT_UNSIGNED = var0 -> !var0.hasSignature();
   SignedMessageValidator REJECT_ALL = var0 -> false;

   boolean updateAndValidate(PlayerChatMessage var1);

   public static class KeyBased implements SignedMessageValidator {
      private final SignatureValidator validator;
      @Nullable
      private PlayerChatMessage lastMessage;
      private boolean isChainValid = true;

      public KeyBased(SignatureValidator var1) {
         super();
         this.validator = var1;
      }

      private boolean validateChain(PlayerChatMessage var1) {
         if (var1.equals(this.lastMessage)) {
            return true;
         } else {
            return this.lastMessage == null || var1.link().isDescendantOf(this.lastMessage.link());
         }
      }

      @Override
      public boolean updateAndValidate(PlayerChatMessage var1) {
         this.isChainValid = this.isChainValid && var1.verify(this.validator) && this.validateChain(var1);
         if (!this.isChainValid) {
            return false;
         } else {
            this.lastMessage = var1;
            return true;
         }
      }
   }
}
