package net.minecraft.network.protocol.game;

import java.time.Instant;
import net.minecraft.commands.arguments.ArgumentSignatures;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.LastSeenMessages;
import net.minecraft.network.protocol.Packet;
import net.minecraft.util.StringUtil;

public record ServerboundChatCommandPacket(String a, Instant b, long c, ArgumentSignatures d, boolean e, LastSeenMessages.Update f)
   implements Packet<ServerGamePacketListener> {
   private final String command;
   private final Instant timeStamp;
   private final long salt;
   private final ArgumentSignatures argumentSignatures;
   private final boolean signedPreview;
   private final LastSeenMessages.Update lastSeenMessages;

   public ServerboundChatCommandPacket(String var1, Instant var2, long var3, ArgumentSignatures var5, boolean var6, LastSeenMessages.Update var7) {
      super();
      var1 = StringUtil.trimChatMessage(var1);
      this.command = var1;
      this.timeStamp = var2;
      this.salt = var3;
      this.argumentSignatures = var5;
      this.signedPreview = var6;
      this.lastSeenMessages = var7;
   }

   public ServerboundChatCommandPacket(FriendlyByteBuf var1) {
      this(var1.readUtf(256), var1.readInstant(), var1.readLong(), new ArgumentSignatures(var1), var1.readBoolean(), new LastSeenMessages.Update(var1));
   }

   @Override
   public void write(FriendlyByteBuf var1) {
      var1.writeUtf(this.command, 256);
      var1.writeInstant(this.timeStamp);
      var1.writeLong(this.salt);
      this.argumentSignatures.write(var1);
      var1.writeBoolean(this.signedPreview);
      this.lastSeenMessages.write(var1);
   }

   public void handle(ServerGamePacketListener var1) {
      var1.handleChatCommand(this);
   }
}
