package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ClientboundContainerAckPacket implements Packet<ClientGamePacketListener> {
   private int containerId;
   private short uid;
   private boolean accepted;

   public ClientboundContainerAckPacket() {
      super();
   }

   public ClientboundContainerAckPacket(int var1, short var2, boolean var3) {
      super();
      this.containerId = var1;
      this.uid = var2;
      this.accepted = var3;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleContainerAck(this);
   }

   public void read(FriendlyByteBuf var1) throws IOException {
      this.containerId = var1.readUnsignedByte();
      this.uid = var1.readShort();
      this.accepted = var1.readBoolean();
   }

   public void write(FriendlyByteBuf var1) throws IOException {
      var1.writeByte(this.containerId);
      var1.writeShort(this.uid);
      var1.writeBoolean(this.accepted);
   }

   public int getContainerId() {
      return this.containerId;
   }

   public short getUid() {
      return this.uid;
   }

   public boolean isAccepted() {
      return this.accepted;
   }
}
