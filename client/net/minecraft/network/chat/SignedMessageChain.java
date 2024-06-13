package net.minecraft.network.chat;

import com.mojang.logging.LogUtils;
import java.time.Instant;
import java.util.UUID;
import java.util.function.BooleanSupplier;
import javax.annotation.Nullable;
import net.minecraft.util.SignatureValidator;
import net.minecraft.util.Signer;
import net.minecraft.world.entity.player.ProfilePublicKey;
import org.slf4j.Logger;

public class SignedMessageChain {
   static final Logger LOGGER = LogUtils.getLogger();
   @Nullable
   SignedMessageLink nextLink;
   Instant lastTimeStamp = Instant.EPOCH;

   public SignedMessageChain(UUID var1, UUID var2) {
      super();
      this.nextLink = SignedMessageLink.root(var1, var2);
   }

   public SignedMessageChain.Encoder encoder(Signer var1) {
      return var2 -> {
         SignedMessageLink var3 = this.nextLink;
         if (var3 == null) {
            return null;
         } else {
            this.nextLink = var3.advance();
            return new MessageSignature(var1.sign(var2x -> PlayerChatMessage.updateSignature(var2x, var3, var2)));
         }
      };
   }

   public SignedMessageChain.Decoder decoder(final ProfilePublicKey var1) {
      final SignatureValidator var2 = var1.createSignatureValidator();
      return new SignedMessageChain.Decoder() {
         @Override
         public PlayerChatMessage unpack(@Nullable MessageSignature var1x, SignedMessageBody var2x) throws SignedMessageChain.DecodeException {
            if (var1x == null) {
               throw new SignedMessageChain.DecodeException(SignedMessageChain.DecodeException.MISSING_PROFILE_KEY);
            } else if (var1.data().hasExpired()) {
               throw new SignedMessageChain.DecodeException(SignedMessageChain.DecodeException.EXPIRED_PROFILE_KEY);
            } else {
               SignedMessageLink var3 = SignedMessageChain.this.nextLink;
               if (var3 == null) {
                  throw new SignedMessageChain.DecodeException(SignedMessageChain.DecodeException.CHAIN_BROKEN);
               } else if (var2x.timeStamp().isBefore(SignedMessageChain.this.lastTimeStamp)) {
                  this.setChainBroken();
                  throw new SignedMessageChain.DecodeException(SignedMessageChain.DecodeException.OUT_OF_ORDER_CHAT);
               } else {
                  SignedMessageChain.this.lastTimeStamp = var2x.timeStamp();
                  PlayerChatMessage var4 = new PlayerChatMessage(var3, var1x, var2x, null, FilterMask.PASS_THROUGH);
                  if (!var4.verify(var2)) {
                     this.setChainBroken();
                     throw new SignedMessageChain.DecodeException(SignedMessageChain.DecodeException.INVALID_SIGNATURE);
                  } else {
                     if (var4.hasExpiredServer(Instant.now())) {
                        SignedMessageChain.LOGGER.warn("Received expired chat: '{}'. Is the client/server system time unsynchronized?", var2x.content());
                     }

                     SignedMessageChain.this.nextLink = var3.advance();
                     return var4;
                  }
               }
            }
         }

         @Override
         public void setChainBroken() {
            SignedMessageChain.this.nextLink = null;
         }
      };
   }

   public static class DecodeException extends ThrowingComponent {
      static final Component MISSING_PROFILE_KEY = Component.translatable("chat.disabled.missingProfileKey");
      static final Component CHAIN_BROKEN = Component.translatable("chat.disabled.chain_broken");
      static final Component EXPIRED_PROFILE_KEY = Component.translatable("chat.disabled.expiredProfileKey");
      static final Component INVALID_SIGNATURE = Component.translatable("chat.disabled.invalid_signature");
      static final Component OUT_OF_ORDER_CHAT = Component.translatable("chat.disabled.out_of_order_chat");

      public DecodeException(Component var1) {
         super(var1);
      }
   }

   @FunctionalInterface
   public interface Decoder {
      static SignedMessageChain.Decoder unsigned(UUID var0, BooleanSupplier var1) {
         return (var2, var3) -> {
            if (var1.getAsBoolean()) {
               throw new SignedMessageChain.DecodeException(SignedMessageChain.DecodeException.MISSING_PROFILE_KEY);
            } else {
               return PlayerChatMessage.unsigned(var0, var3.content());
            }
         };
      }

      PlayerChatMessage unpack(@Nullable MessageSignature var1, SignedMessageBody var2) throws SignedMessageChain.DecodeException;

      default void setChainBroken() {
      }
   }

   @FunctionalInterface
   public interface Encoder {
      SignedMessageChain.Encoder UNSIGNED = var0 -> null;

      @Nullable
      MessageSignature pack(SignedMessageBody var1);
   }
}
