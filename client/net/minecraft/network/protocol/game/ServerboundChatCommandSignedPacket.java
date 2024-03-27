package net.minecraft.network.protocol.game;

import java.time.Instant;
import net.minecraft.commands.arguments.ArgumentSignatures;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.LastSeenMessages;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public record ServerboundChatCommandSignedPacket(String b, Instant c, long d, ArgumentSignatures e, LastSeenMessages.Update f)
   implements Packet<ServerGamePacketListener> {
   private final String command;
   private final Instant timeStamp;
   private final long salt;
   private final ArgumentSignatures argumentSignatures;
   private final LastSeenMessages.Update lastSeenMessages;
   public static final StreamCodec<FriendlyByteBuf, ServerboundChatCommandSignedPacket> STREAM_CODEC = Packet.codec(
      ServerboundChatCommandSignedPacket::write, ServerboundChatCommandSignedPacket::new
   );

   private ServerboundChatCommandSignedPacket(FriendlyByteBuf var1) {
      this(var1.readUtf(256), var1.readInstant(), var1.readLong(), new ArgumentSignatures(var1), new LastSeenMessages.Update(var1));
   }

   public ServerboundChatCommandSignedPacket(String var1, Instant var2, long var3, ArgumentSignatures var5, LastSeenMessages.Update var6) {
      super();
      this.command = var1;
      this.timeStamp = var2;
      this.salt = var3;
      this.argumentSignatures = var5;
      this.lastSeenMessages = var6;
   }

   private void write(FriendlyByteBuf var1) {
      var1.writeUtf(this.command, 256);
      var1.writeInstant(this.timeStamp);
      var1.writeLong(this.salt);
      this.argumentSignatures.write(var1);
      this.lastSeenMessages.write(var1);
   }

   @Override
   public PacketType<ServerboundChatCommandSignedPacket> type() {
      return GamePacketTypes.SERVERBOUND_CHAT_COMMAND_SIGNED;
   }

   public void handle(ServerGamePacketListener var1) {
      var1.handleSignedChatCommand(this);
   }
}
