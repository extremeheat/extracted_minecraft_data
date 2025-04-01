package net.minecraft.network.protocol.game;

import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.level.dimension.DimensionType;

public record ClientboundChangeDimensionTypePacket(Holder<DimensionType> dimensionType) implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundChangeDimensionTypePacket> STREAM_CODEC;

   public ClientboundChangeDimensionTypePacket(Holder<DimensionType> var1) {
      super();
      this.dimensionType = var1;
   }

   public PacketType<ClientboundChangeDimensionTypePacket> type() {
      return GamePacketTypes.CLIENTBOUND_CHANGE_DIMENSION_TYPE;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleChangeDimensionType(this);
   }

   static {
      STREAM_CODEC = StreamCodec.composite(DimensionType.STREAM_CODEC, ClientboundChangeDimensionTypePacket::dimensionType, ClientboundChangeDimensionTypePacket::new);
   }
}
