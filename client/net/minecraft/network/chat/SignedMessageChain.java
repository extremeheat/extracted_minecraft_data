package net.minecraft.network.chat;

import com.mojang.logging.LogUtils;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;
import java.util.function.BooleanSupplier;
import net.minecraft.util.SignatureUpdater;
import net.minecraft.util.SignatureValidator;
import net.minecraft.util.Signer;
import net.minecraft.world.entity.player.ProfilePublicKey;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class SignedMessageChain {
   private static final Logger LOGGER = LogUtils.getLogger();
   private @Nullable SignedMessageLink nextLink;
   private Instant lastTimeStamp;

   public SignedMessageChain(final UUID profileId, final UUID sessionId) {
      super();
      this.lastTimeStamp = Instant.EPOCH;
      this.nextLink = SignedMessageLink.root(profileId, sessionId);
   }

   public Encoder encoder(final Signer signer) {
      return (body) -> {
         SignedMessageLink link = this.nextLink;
         if (link == null) {
            return null;
         } else {
            this.nextLink = link.advance();
            return new MessageSignature(signer.sign((SignatureUpdater)((output) -> PlayerChatMessage.updateSignature(output, link, body))));
         }
      };
   }

   public Decoder decoder(final ProfilePublicKey profilePublicKey) {
      final SignatureValidator signatureValidator = profilePublicKey.createSignatureValidator();
      return new Decoder() {
         {
            Objects.requireNonNull(SignedMessageChain.this);
         }

         public PlayerChatMessage unpack(final @Nullable MessageSignature signature, final SignedMessageBody body) throws DecodeException {
            if (signature == null) {
               throw new DecodeException(SignedMessageChain.DecodeException.MISSING_PROFILE_KEY);
            } else if (profilePublicKey.data().hasExpired()) {
               throw new DecodeException(SignedMessageChain.DecodeException.EXPIRED_PROFILE_KEY);
            } else {
               SignedMessageLink link = SignedMessageChain.this.nextLink;
               if (link == null) {
                  throw new DecodeException(SignedMessageChain.DecodeException.CHAIN_BROKEN);
               } else if (body.timeStamp().isBefore(SignedMessageChain.this.lastTimeStamp)) {
                  this.setChainBroken();
                  throw new DecodeException(SignedMessageChain.DecodeException.OUT_OF_ORDER_CHAT);
               } else {
                  SignedMessageChain.this.lastTimeStamp = body.timeStamp();
                  PlayerChatMessage unpacked = new PlayerChatMessage(link, signature, body, (Component)null, FilterMask.PASS_THROUGH);
                  if (!unpacked.verify(signatureValidator)) {
                     this.setChainBroken();
                     throw new DecodeException(SignedMessageChain.DecodeException.INVALID_SIGNATURE);
                  } else {
                     if (unpacked.hasExpiredServer(Instant.now())) {
                        SignedMessageChain.LOGGER.warn("Received expired chat: '{}'. Is the client/server system time unsynchronized?", body.content());
                     }

                     SignedMessageChain.this.nextLink = link.advance();
                     return unpacked;
                  }
               }
            }
         }

         public void setChainBroken() {
            SignedMessageChain.this.nextLink = null;
         }
      };
   }

   @FunctionalInterface
   public interface Encoder {
      Encoder UNSIGNED = (body) -> null;

      @Nullable MessageSignature pack(SignedMessageBody body);
   }

   @FunctionalInterface
   public interface Decoder {
      static Decoder unsigned(final UUID profileId, final BooleanSupplier enforcesSecureChat) {
         return (signature, body) -> {
            if (enforcesSecureChat.getAsBoolean()) {
               throw new DecodeException(SignedMessageChain.DecodeException.MISSING_PROFILE_KEY);
            } else {
               return PlayerChatMessage.unsigned(profileId, body.content());
            }
         };
      }

      PlayerChatMessage unpack(@Nullable MessageSignature signature, SignedMessageBody body) throws DecodeException;

      default void setChainBroken() {
      }
   }

   public static class DecodeException extends ThrowingComponent {
      private static final Component MISSING_PROFILE_KEY = Component.translatable("chat.disabled.missingProfileKey");
      private static final Component CHAIN_BROKEN = Component.translatable("chat.disabled.chain_broken");
      private static final Component EXPIRED_PROFILE_KEY = Component.translatable("chat.disabled.expiredProfileKey");
      private static final Component INVALID_SIGNATURE = Component.translatable("chat.disabled.invalid_signature");
      private static final Component OUT_OF_ORDER_CHAT = Component.translatable("chat.disabled.out_of_order_chat");

      public DecodeException(final Component component) {
         super(component);
      }
   }
}
