package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.entity.Entity;

public class ClientboundAnimatePacket implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ClientboundAnimatePacket> STREAM_CODEC = Packet.<FriendlyByteBuf, ClientboundAnimatePacket>codec(ClientboundAnimatePacket::write, ClientboundAnimatePacket::new);
   public static final int WAKE_UP = 0;
   public static final int CRITICAL_HIT = 1;
   public static final int MAGIC_CRITICAL_HIT = 2;
   private final int id;
   private final int action;

   public ClientboundAnimatePacket(final Entity entity, final int action) {
      super();
      this.id = entity.getId();
      this.action = action;
   }

   private ClientboundAnimatePacket(final FriendlyByteBuf input) {
      super();
      this.id = input.readVarInt();
      this.action = input.readUnsignedByte();
   }

   private void write(final FriendlyByteBuf output) {
      output.writeVarInt(this.id);
      output.writeByte(this.action);
   }

   public PacketType<ClientboundAnimatePacket> type() {
      return GamePacketTypes.CLIENTBOUND_ANIMATE;
   }

   public void handle(final ClientGamePacketListener listener) {
      listener.handleAnimate(this);
   }

   public int getId() {
      return this.id;
   }

   public int getAction() {
      return this.action;
   }
}
