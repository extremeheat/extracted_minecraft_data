package net.minecraft.network.protocol.handshake;

import java.io.IOException;
import net.minecraft.SharedConstants;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ClientIntentionPacket implements Packet<ServerHandshakePacketListener> {
   private int protocolVersion;
   private String hostName;
   private int port;
   private ConnectionProtocol intention;

   public ClientIntentionPacket() {
      super();
   }

   public ClientIntentionPacket(String var1, int var2, ConnectionProtocol var3) {
      super();
      this.protocolVersion = SharedConstants.getCurrentVersion().getProtocolVersion();
      this.hostName = var1;
      this.port = var2;
      this.intention = var3;
   }

   public void read(FriendlyByteBuf var1) throws IOException {
      this.protocolVersion = var1.readVarInt();
      this.hostName = var1.readUtf(255);
      this.port = var1.readUnsignedShort();
      this.intention = ConnectionProtocol.getById(var1.readVarInt());
   }

   public void write(FriendlyByteBuf var1) throws IOException {
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
}
