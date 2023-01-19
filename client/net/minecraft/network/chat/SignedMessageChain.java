package net.minecraft.network.chat;

import com.mojang.logging.LogUtils;
import java.time.Instant;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.util.SignatureValidator;
import net.minecraft.util.Signer;
import net.minecraft.world.entity.player.ProfilePublicKey;
import org.slf4j.Logger;

public class SignedMessageChain {
   private static final Logger LOGGER = LogUtils.getLogger();
   @Nullable
   private SignedMessageLink nextLink;

   public SignedMessageChain(UUID var1, UUID var2) {
      super();
      this.nextLink = SignedMessageLink.root(var1, var2);
   }

   public SignedMessageChain.Encoder encoder(Signer var1) {
      return var2 -> {
         SignedMessageLink var3 = this.advanceLink();
         return var3 == null ? null : new MessageSignature(var1.sign(var2x -> PlayerChatMessage.updateSignature(var2x, var3, var2)));
      };
   }

   public SignedMessageChain.Decoder decoder(ProfilePublicKey var1) {
      SignatureValidator var2 = var1.createSignatureValidator();
      return (var3, var4) -> {
         SignedMessageLink var5 = this.advanceLink();
         if (var5 == null) {
            throw new SignedMessageChain.DecodeException(Component.translatable("chat.disabled.chain_broken"), false);
         } else if (var1.data().hasExpired()) {
            throw new SignedMessageChain.DecodeException(Component.translatable("chat.disabled.expiredProfileKey"), false);
         } else {
            PlayerChatMessage var6 = new PlayerChatMessage(var5, var3, var4, null, FilterMask.PASS_THROUGH);
            if (!var6.verify(var2)) {
               throw new SignedMessageChain.DecodeException(Component.translatable("multiplayer.disconnect.unsigned_chat"), true);
            } else {
               if (var6.hasExpiredServer(Instant.now())) {
                  LOGGER.warn("Received expired chat: '{}'. Is the client/server system time unsynchronized?", var4.content());
               }

               return var6;
            }
         }
      };
   }

   @Nullable
   private SignedMessageLink advanceLink() {
      SignedMessageLink var1 = this.nextLink;
      if (var1 != null) {
         this.nextLink = var1.advance();
      }

      return var1;
   }

   public static class DecodeException extends ThrowingComponent {
      private final boolean shouldDisconnect;

      public DecodeException(Component var1, boolean var2) {
         super(var1);
         this.shouldDisconnect = var2;
      }

      public boolean shouldDisconnect() {
         return this.shouldDisconnect;
      }
   }

   @FunctionalInterface
   public interface Decoder {
      SignedMessageChain.Decoder REJECT_ALL = (var0, var1) -> {
         throw new SignedMessageChain.DecodeException(Component.translatable("chat.disabled.missingProfileKey"), false);
      };

      static SignedMessageChain.Decoder unsigned(UUID var0) {
         return (var1, var2) -> PlayerChatMessage.unsigned(var0, var2.content());
      }

      PlayerChatMessage unpack(@Nullable MessageSignature var1, SignedMessageBody var2) throws SignedMessageChain.DecodeException;
   }

   @FunctionalInterface
   public interface Encoder {
      SignedMessageChain.Encoder UNSIGNED = var0 -> null;

      @Nullable
      MessageSignature pack(SignedMessageBody var1);
   }
}
