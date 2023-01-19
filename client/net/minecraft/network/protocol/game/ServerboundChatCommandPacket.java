package net.minecraft.network.protocol.game;

import java.time.Instant;
import java.util.UUID;
import net.minecraft.commands.CommandSigningContext;
import net.minecraft.commands.arguments.ArgumentSignatures;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.util.StringUtil;

public record ServerboundChatCommandPacket(String a, Instant b, ArgumentSignatures c, boolean d) implements Packet<ServerGamePacketListener> {
   private final String command;
   private final Instant timeStamp;
   private final ArgumentSignatures argumentSignatures;
   private final boolean signedPreview;

   public ServerboundChatCommandPacket(String var1, Instant var2, ArgumentSignatures var3, boolean var4) {
      super();
      var1 = StringUtil.trimChatMessage(var1);
      this.command = var1;
      this.timeStamp = var2;
      this.argumentSignatures = var3;
      this.signedPreview = var4;
   }

   public ServerboundChatCommandPacket(FriendlyByteBuf var1) {
      this(var1.readUtf(256), var1.readInstant(), new ArgumentSignatures(var1), var1.readBoolean());
   }

   @Override
   public void write(FriendlyByteBuf var1) {
      var1.writeUtf(this.command, 256);
      var1.writeInstant(this.timeStamp);
      this.argumentSignatures.write(var1);
      var1.writeBoolean(this.signedPreview);
   }

   public void handle(ServerGamePacketListener var1) {
      var1.handleChatCommand(this);
   }

   public CommandSigningContext signingContext(UUID var1) {
      return new CommandSigningContext.SignedArguments(var1, this.timeStamp, this.argumentSignatures, this.signedPreview);
   }
}
