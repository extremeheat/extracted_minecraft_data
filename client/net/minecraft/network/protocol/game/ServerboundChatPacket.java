package net.minecraft.network.protocol.game;

import java.time.Instant;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.LastSeenMessages;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.network.chat.MessageSigner;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerPlayer;

public record ServerboundChatPacket(String a, Instant b, long c, MessageSignature d, boolean e, LastSeenMessages.Update f) implements Packet<ServerGamePacketListener> {
   private final String message;
   private final Instant timeStamp;
   private final long salt;
   private final MessageSignature signature;
   private final boolean signedPreview;
   private final LastSeenMessages.Update lastSeenMessages;

   public ServerboundChatPacket(FriendlyByteBuf var1) {
      this(var1.readUtf(256), var1.readInstant(), var1.readLong(), new MessageSignature(var1), var1.readBoolean(), new LastSeenMessages.Update(var1));
   }

   public ServerboundChatPacket(String var1, Instant var2, long var3, MessageSignature var5, boolean var6, LastSeenMessages.Update var7) {
      super();
      this.message = var1;
      this.timeStamp = var2;
      this.salt = var3;
      this.signature = var5;
      this.signedPreview = var6;
      this.lastSeenMessages = var7;
   }

   public void write(FriendlyByteBuf var1) {
      var1.writeUtf(this.message, 256);
      var1.writeInstant(this.timeStamp);
      var1.writeLong(this.salt);
      this.signature.write(var1);
      var1.writeBoolean(this.signedPreview);
      this.lastSeenMessages.write(var1);
   }

   public void handle(ServerGamePacketListener var1) {
      var1.handleChat(this);
   }

   public MessageSigner getSigner(ServerPlayer var1) {
      return new MessageSigner(var1.getUUID(), this.timeStamp, this.salt);
   }

   public String message() {
      return this.message;
   }

   public Instant timeStamp() {
      return this.timeStamp;
   }

   public long salt() {
      return this.salt;
   }

   public MessageSignature signature() {
      return this.signature;
   }

   public boolean signedPreview() {
      return this.signedPreview;
   }

   public LastSeenMessages.Update lastSeenMessages() {
      return this.lastSeenMessages;
   }
}
