package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.entity.Entity;

public class ClientboundAnimatePacket implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ClientboundAnimatePacket> STREAM_CODEC = Packet.codec(ClientboundAnimatePacket::write, ClientboundAnimatePacket::new);
   public static final int SWING_MAIN_HAND = 0;
   public static final int WAKE_UP = 2;
   public static final int SWING_OFF_HAND = 3;
   public static final int CRITICAL_HIT = 4;
   public static final int MAGIC_CRITICAL_HIT = 5;
   private final int id;
   private final int action;

   public ClientboundAnimatePacket(Entity var1, int var2) {
      super();
      this.id = var1.getId();
      this.action = var2;
   }

   private ClientboundAnimatePacket(FriendlyByteBuf var1) {
      super();
      this.id = var1.readVarInt();
      this.action = var1.readUnsignedByte();
   }

   private void write(FriendlyByteBuf var1) {
      var1.writeVarInt(this.id);
      var1.writeByte(this.action);
   }

   public PacketType<ClientboundAnimatePacket> type() {
      return GamePacketTypes.CLIENTBOUND_ANIMATE;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleAnimate(this);
   }

   public int getId() {
      return this.id;
   }

   public int getAction() {
      return this.action;
   }
}
