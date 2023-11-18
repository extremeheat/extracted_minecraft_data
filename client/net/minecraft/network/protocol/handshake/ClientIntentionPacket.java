package net.minecraft.network.protocol.handshake;

import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public record ClientIntentionPacket(int a, String b, int c, ClientIntent d) implements Packet<ServerHandshakePacketListener> {
   private final int protocolVersion;
   private final String hostName;
   private final int port;
   private final ClientIntent intention;
   private static final int MAX_HOST_LENGTH = 255;

   @Deprecated
   public ClientIntentionPacket(int var1, String var2, int var3, ClientIntent var4) {
      super();
      this.protocolVersion = var1;
      this.hostName = var2;
      this.port = var3;
      this.intention = var4;
   }

   public ClientIntentionPacket(FriendlyByteBuf var1) {
      this(var1.readVarInt(), var1.readUtf(255), var1.readUnsignedShort(), ClientIntent.byId(var1.readVarInt()));
   }

   @Override
   public void write(FriendlyByteBuf var1) {
      var1.writeVarInt(this.protocolVersion);
      var1.writeUtf(this.hostName);
      var1.writeShort(this.port);
      var1.writeVarInt(this.intention.id());
   }

   public void handle(ServerHandshakePacketListener var1) {
      var1.handleIntention(this);
   }

   @Override
   public ConnectionProtocol nextProtocol() {
      return this.intention.protocol();
   }
}
