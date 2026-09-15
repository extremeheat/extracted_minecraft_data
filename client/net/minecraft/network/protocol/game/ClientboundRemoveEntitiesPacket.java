package net.minecraft.network.protocol.game;

import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public record ClientboundRemoveEntitiesPacket(IntList entityIds) implements Packet<ClientGamePacketListener> {
   private static final StreamCodec<ByteBuf, IntList> ID_LIST_STREAM_CODEC;
   public static final StreamCodec<ByteBuf, ClientboundRemoveEntitiesPacket> STREAM_CODEC;

   public ClientboundRemoveEntitiesPacket(final int... ids) {
      this(IntList.of(ids));
   }

   public ClientboundRemoveEntitiesPacket {
      super();
   }

   public PacketType<ClientboundRemoveEntitiesPacket> type() {
      return GamePacketTypes.CLIENTBOUND_REMOVE_ENTITIES;
   }

   public void handle(final ClientGamePacketListener listener) {
      listener.handleRemoveEntities(this);
   }

   static {
      ID_LIST_STREAM_CODEC = ByteBufCodecs.VAR_INT.apply(ByteBufCodecs.collection(IntArrayList::new));
      STREAM_CODEC = StreamCodec.composite(ID_LIST_STREAM_CODEC, ClientboundRemoveEntitiesPacket::entityIds, ClientboundRemoveEntitiesPacket::new);
   }
}
