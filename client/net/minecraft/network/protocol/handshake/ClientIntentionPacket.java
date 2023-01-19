package net.minecraft.network.protocol.handshake;

import net.minecraft.SharedConstants;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ClientIntentionPacket implements Packet<ServerHandshakePacketListener> {
   private static final int MAX_HOST_LENGTH = 255;
   private final int protocolVersion;
   private final String hostName;
   private final int port;
   private final ConnectionProtocol intention;

   public ClientIntentionPacket(String var1, int var2, ConnectionProtocol var3) {
      super();
      this.protocolVersion = SharedConstants.getCurrentVersion().getProtocolVersion();
      this.hostName = var1;
      this.port = var2;
      this.intention = var3;
   }

   public ClientIntentionPacket(FriendlyByteBuf var1) {
      super();
      this.protocolVersion = var1.readVarInt();
      this.hostName = var1.readUtf(255);
      this.port = var1.readUnsignedShort();
      this.intention = ConnectionProtocol.getById(var1.readVarInt());
   }

   @Override
   public void write(FriendlyByteBuf var1) {
      var1.writeVarInt(this.protocolVersion);
      var1.writeUtf(this.hostName);
      var1.writeShort(this.port);
      var1.writeVarInt(this.intention.getId());
   }

   public void handle(ServerHandshakePacketListener var1) {
      var1.handleIntention(this);
   }

   public ConnectionProtocol getIntention() {
      return this.intention;
   }

   public int getProtocolVersion() {
      return this.protocolVersion;
   }

   public String getHostName() {
      return this.hostName;
   }

   public int getPort() {
      return this.port;
   }
}
