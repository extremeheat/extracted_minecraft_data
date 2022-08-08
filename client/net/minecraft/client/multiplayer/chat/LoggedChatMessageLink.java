package net.minecraft.client.multiplayer.chat;

import net.minecraft.network.chat.MessageSignature;
import net.minecraft.network.chat.SignedMessageHeader;

public interface LoggedChatMessageLink extends LoggedChatEvent {
   static Header header(SignedMessageHeader var0, MessageSignature var1, byte[] var2) {
      return new Header(var0, var1, var2);
   }

   SignedMessageHeader header();

   MessageSignature headerSignature();

   byte[] bodyDigest();

   public static record Header(SignedMessageHeader a, MessageSignature b, byte[] c) implements LoggedChatMessageLink {
      private final SignedMessageHeader header;
      private final MessageSignature headerSignature;
      private final byte[] bodyDigest;

      public Header(SignedMessageHeader var1, MessageSignature var2, byte[] var3) {
         super();
         this.header = var1;
         this.headerSignature = var2;
         this.bodyDigest = var3;
      }

      public SignedMessageHeader header() {
         return this.header;
      }

      public MessageSignature headerSignature() {
         return this.headerSignature;
      }

      public byte[] bodyDigest() {
         return this.bodyDigest;
      }
   }
}
