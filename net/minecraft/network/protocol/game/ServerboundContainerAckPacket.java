package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ServerboundContainerAckPacket implements Packet {
   private int containerId;
   private short uid;
   private boolean accepted;

   public ServerboundContainerAckPacket() {
   }

   public ServerboundContainerAckPacket(int var1, short var2, boolean var3) {
      this.containerId = var1;
      this.uid = var2;
      this.accepted = var3;
   }

   public void handle(ServerGamePacketListener var1) {
      var1.handleContainerAck(this);
   }

   public void read(FriendlyByteBuf var1) throws IOException {
      this.containerId = var1.readByte();
      this.uid = var1.readShort();
      this.accepted = var1.readByte() != 0;
   }

   public void write(FriendlyByteBuf var1) throws IOException {
      var1.writeByte(this.containerId);
      var1.writeShort(this.uid);
      var1.writeByte(this.accepted ? 1 : 0);
   }

   public int getContainerId() {
      return this.containerId;
   }

   public short getUid() {
      return this.uid;
   }
}
