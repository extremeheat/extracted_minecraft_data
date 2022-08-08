package net.minecraft.network.protocol.game;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.network.protocol.Packet;
import net.minecraft.util.Crypt;
import net.minecraft.util.StringUtil;

public class ServerboundChatPacket implements Packet<ServerGamePacketListener> {
   public static final Duration MESSAGE_EXPIRES_AFTER = Duration.ofMinutes(5L);
   private final String message;
   private final Instant timeStamp;
   private final Crypt.SaltSignaturePair saltSignature;
   private final boolean signedPreview;

   public ServerboundChatPacket(String var1, MessageSignature var2, boolean var3) {
      super();
      this.message = StringUtil.trimChatMessage(var1);
      this.timeStamp = var2.timeStamp();
      this.saltSignature = var2.saltSignature();
      this.signedPreview = var3;
   }

   public ServerboundChatPacket(FriendlyByteBuf var1) {
      super();
      this.message = var1.readUtf(256);
      this.timeStamp = var1.readInstant();
      this.saltSignature = new Crypt.SaltSignaturePair(var1);
      this.signedPreview = var1.readBoolean();
   }

   public void write(FriendlyByteBuf var1) {
      var1.writeUtf(this.message, 256);
      var1.writeInstant(this.timeStamp);
      Crypt.SaltSignaturePair.write(var1, this.saltSignature);
      var1.writeBoolean(this.signedPreview);
   }

   public void handle(ServerGamePacketListener var1) {
      var1.handleChat(this);
   }

   public String getMessage() {
      return this.message;
   }

   public MessageSignature getSignature(UUID var1) {
      return new MessageSignature(var1, this.timeStamp, this.saltSignature);
   }

   public Instant getTimeStamp() {
      return this.timeStamp;
   }

   public boolean signedPreview() {
      return this.signedPreview;
   }
}
