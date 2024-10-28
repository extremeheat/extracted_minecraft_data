package net.minecraft.network.protocol.game;

import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.entity.Entity;

public class ClientboundSetEntityLinkPacket implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ClientboundSetEntityLinkPacket> STREAM_CODEC = Packet.codec(ClientboundSetEntityLinkPacket::write, ClientboundSetEntityLinkPacket::new);
   private final int sourceId;
   private final int destId;

   public ClientboundSetEntityLinkPacket(Entity var1, @Nullable Entity var2) {
      super();
      this.sourceId = var1.getId();
      this.destId = var2 != null ? var2.getId() : 0;
   }

   private ClientboundSetEntityLinkPacket(FriendlyByteBuf var1) {
      super();
      this.sourceId = var1.readInt();
      this.destId = var1.readInt();
   }

   private void write(FriendlyByteBuf var1) {
      var1.writeInt(this.sourceId);
      var1.writeInt(this.destId);
   }

   public PacketType<ClientboundSetEntityLinkPacket> type() {
      return GamePacketTypes.CLIENTBOUND_SET_ENTITY_LINK;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleEntityLinkPacket(this);
   }

   public int getSourceId() {
      return this.sourceId;
   }

   public int getDestId() {
      return this.destId;
   }
}
