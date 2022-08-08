package net.minecraft.network.chat;

import javax.annotation.Nullable;
import net.minecraft.util.SignatureValidator;
import net.minecraft.world.entity.player.ProfilePublicKey;

public interface SignedMessageValidator {
   static SignedMessageValidator create(@Nullable ProfilePublicKey var0, boolean var1) {
      return (SignedMessageValidator)(var0 != null ? new KeyBased(var0.createSignatureValidator()) : new Unsigned(var1));
   }

   State validateHeader(SignedMessageHeader var1, MessageSignature var2, byte[] var3);

   State validateMessage(PlayerChatMessage var1);

   public static class KeyBased implements SignedMessageValidator {
      private final SignatureValidator validator;
      @Nullable
      private MessageSignature lastSignature;
      private boolean isChainConsistent = true;

      public KeyBased(SignatureValidator var1) {
         super();
         this.validator = var1;
      }

      private boolean validateChain(SignedMessageHeader var1, MessageSignature var2, boolean var3) {
         if (var2.isEmpty()) {
            return false;
         } else if (var3 && var2.equals(this.lastSignature)) {
            return true;
         } else {
            return this.lastSignature == null || this.lastSignature.equals(var1.previousSignature());
         }
      }

      private boolean validateContents(SignedMessageHeader var1, MessageSignature var2, byte[] var3, boolean var4) {
         return this.validateChain(var1, var2, var4) && var2.verify(this.validator, var1, var3);
      }

      private State updateAndValidate(SignedMessageHeader var1, MessageSignature var2, byte[] var3, boolean var4) {
         this.isChainConsistent = this.isChainConsistent && this.validateContents(var1, var2, var3, var4);
         if (!this.isChainConsistent) {
            return SignedMessageValidator.State.BROKEN_CHAIN;
         } else {
            this.lastSignature = var2;
            return SignedMessageValidator.State.SECURE;
         }
      }

      public State validateHeader(SignedMessageHeader var1, MessageSignature var2, byte[] var3) {
         return this.updateAndValidate(var1, var2, var3, false);
      }

      public State validateMessage(PlayerChatMessage var1) {
         byte[] var2 = var1.signedBody().hash().asBytes();
         return this.updateAndValidate(var1.signedHeader(), var1.headerSignature(), var2, true);
      }
   }

   public static class Unsigned implements SignedMessageValidator {
      private final boolean enforcesSecureChat;

      public Unsigned(boolean var1) {
         super();
         this.enforcesSecureChat = var1;
      }

      private State validate(MessageSignature var1) {
         if (!var1.isEmpty()) {
            return SignedMessageValidator.State.BROKEN_CHAIN;
         } else {
            return this.enforcesSecureChat ? SignedMessageValidator.State.BROKEN_CHAIN : SignedMessageValidator.State.NOT_SECURE;
         }
      }

      public State validateHeader(SignedMessageHeader var1, MessageSignature var2, byte[] var3) {
         return this.validate(var2);
      }

      public State validateMessage(PlayerChatMessage var1) {
         return this.validate(var1.headerSignature());
      }
   }

   public static enum State {
      SECURE,
      NOT_SECURE,
      BROKEN_CHAIN;

      private State() {
      }

      // $FF: synthetic method
      private static State[] $values() {
         return new State[]{SECURE, NOT_SECURE, BROKEN_CHAIN};
      }
   }
}
