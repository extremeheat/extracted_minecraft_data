package net.minecraft.network.protocol.game;

import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public class ClientboundEntityEventPacket implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ClientboundEntityEventPacket> STREAM_CODEC = Packet.codec(ClientboundEntityEventPacket::write, ClientboundEntityEventPacket::new);
   private final int entityId;
   private final byte eventId;

   public ClientboundEntityEventPacket(Entity var1, byte var2) {
      super();
      this.entityId = var1.getId();
      this.eventId = var2;
   }

   private ClientboundEntityEventPacket(FriendlyByteBuf var1) {
      super();
      this.entityId = var1.readInt();
      this.eventId = var1.readByte();
   }

   private void write(FriendlyByteBuf var1) {
      var1.writeInt(this.entityId);
      var1.writeByte(this.eventId);
   }

   public PacketType<ClientboundEntityEventPacket> type() {
      return GamePacketTypes.CLIENTBOUND_ENTITY_EVENT;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleEntityEvent(this);
   }

   @Nullable
   public Entity getEntity(Level var1) {
      return var1.getEntity(this.entityId);
   }

   public byte getEventId() {
      return this.eventId;
   }
}
