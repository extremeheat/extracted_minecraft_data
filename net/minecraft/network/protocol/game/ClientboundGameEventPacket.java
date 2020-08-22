package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ClientboundGameEventPacket implements Packet {
   public static final String[] EVENT_LANGUAGE_ID = new String[]{"block.minecraft.bed.not_valid"};
   private int event;
   private float param;

   public ClientboundGameEventPacket() {
   }

   public ClientboundGameEventPacket(int var1, float var2) {
      this.event = var1;
      this.param = var2;
   }

   public void read(FriendlyByteBuf var1) throws IOException {
      this.event = var1.readUnsignedByte();
      this.param = var1.readFloat();
   }

   public void write(FriendlyByteBuf var1) throws IOException {
      var1.writeByte(this.event);
      var1.writeFloat(this.param);
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleGameEvent(this);
   }

   public int getEvent() {
      return this.event;
   }

   public float getParam() {
      return this.param;
   }
}
