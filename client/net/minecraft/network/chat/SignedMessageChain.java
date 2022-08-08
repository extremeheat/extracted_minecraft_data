package net.minecraft.network.chat;

import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.util.Signer;

public class SignedMessageChain {
   @Nullable
   private MessageSignature previousSignature;

   public SignedMessageChain() {
      super();
   }

   private Link pack(Signer var1, MessageSigner var2, ChatMessageContent var3, LastSeenMessages var4) {
      MessageSignature var5 = pack(var1, var2, this.previousSignature, var3, var4);
      this.previousSignature = var5;
      return new Link(var5);
   }

   private static MessageSignature pack(Signer var0, MessageSigner var1, @Nullable MessageSignature var2, ChatMessageContent var3, LastSeenMessages var4) {
      SignedMessageHeader var5 = new SignedMessageHeader(var2, var1.profileId());
      SignedMessageBody var6 = new SignedMessageBody(var3, var1.timeStamp(), var1.salt(), var4);
      byte[] var7 = var6.hash().asBytes();
      return new MessageSignature(var0.sign((var2x) -> {
         var5.updateSignature(var2x, var7);
      }));
   }

   private PlayerChatMessage unpack(Link var1, MessageSigner var2, ChatMessageContent var3, LastSeenMessages var4) {
      PlayerChatMessage var5 = unpack(var1, this.previousSignature, var2, var3, var4);
      this.previousSignature = var1.signature;
      return var5;
   }

   private static PlayerChatMessage unpack(Link var0, @Nullable MessageSignature var1, MessageSigner var2, ChatMessageContent var3, LastSeenMessages var4) {
      SignedMessageHeader var5 = new SignedMessageHeader(var1, var2.profileId());
      SignedMessageBody var6 = new SignedMessageBody(var3, var2.timeStamp(), var2.salt(), var4);
      return new PlayerChatMessage(var5, var0.signature, var6, Optional.empty(), FilterMask.PASS_THROUGH);
   }

   public Decoder decoder() {
      return this::unpack;
   }

   public Encoder encoder() {
      return this::pack;
   }

   public static record Link(MessageSignature a) {
      final MessageSignature signature;

      public Link(MessageSignature var1) {
         super();
         this.signature = var1;
      }

      public MessageSignature signature() {
         return this.signature;
      }
   }

   @FunctionalInterface
   public interface Decoder {
      Decoder UNSIGNED = (var0, var1, var2, var3) -> {
         return PlayerChatMessage.unsigned(var1, var2);
      };

      PlayerChatMessage unpack(Link var1, MessageSigner var2, ChatMessageContent var3, LastSeenMessages var4);
   }

   @FunctionalInterface
   public interface Encoder {
      Link pack(Signer var1, MessageSigner var2, ChatMessageContent var3, LastSeenMessages var4);
   }
}
