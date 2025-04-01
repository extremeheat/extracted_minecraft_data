package net.minecraft.network.protocol.game;

import java.util.List;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.inventory.DoorMenu;

public record ClientboundOpenDoorPacket(int containerId, List<DoorMenu.Layout> options) implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundOpenDoorPacket> STREAM_CODEC;

   public ClientboundOpenDoorPacket(int var1, List<DoorMenu.Layout> var2) {
      super();
      this.containerId = var1;
      this.options = var2;
   }

   public PacketType<ClientboundOpenDoorPacket> type() {
      return GamePacketTypes.CLIENTBOUND_OPEN_DOOR_PACKET;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleOpenDoorPacket(this);
   }

   static {
      STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.CONTAINER_ID, ClientboundOpenDoorPacket::containerId, DoorMenu.Layout.STREAM_CODEC.apply(ByteBufCodecs.list()), ClientboundOpenDoorPacket::options, ClientboundOpenDoorPacket::new);
   }
}
