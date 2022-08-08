package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.network.chat.SignedMessageHeader;
import net.minecraft.network.protocol.Packet;

public record ClientboundPlayerChatHeaderPacket(SignedMessageHeader a, MessageSignature b, byte[] c) implements Packet<ClientGamePacketListener> {
   private final SignedMessageHeader header;
   private final MessageSignature headerSignature;
   private final byte[] bodyDigest;

   public ClientboundPlayerChatHeaderPacket(PlayerChatMessage var1) {
      this(var1.signedHeader(), var1.headerSignature(), var1.signedBody().hash().asBytes());
   }

   public ClientboundPlayerChatHeaderPacket(FriendlyByteBuf var1) {
      this(new SignedMessageHeader(var1), new MessageSignature(var1), var1.readByteArray());
   }

   public ClientboundPlayerChatHeaderPacket(SignedMessageHeader var1, MessageSignature var2, byte[] var3) {
      super();
      this.header = var1;
      this.headerSignature = var2;
      this.bodyDigest = var3;
   }

   public void write(FriendlyByteBuf var1) {
      this.header.write(var1);
      this.headerSignature.write(var1);
      var1.writeByteArray(this.bodyDigest);
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handlePlayerChatHeader(this);
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
