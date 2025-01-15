package net.minecraft.network.protocol.game;

import java.util.Optional;
import net.minecraft.core.Vec3i;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public record ClientboundTestInstanceBlockStatus(Component status, Optional<Vec3i> size) implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundTestInstanceBlockStatus> STREAM_CODEC;

   public ClientboundTestInstanceBlockStatus(Component var1, Optional<Vec3i> var2) {
      super();
      this.status = var1;
      this.size = var2;
   }

   public PacketType<ClientboundTestInstanceBlockStatus> type() {
      return GamePacketTypes.CLIENTBOUND_TEST_INSTANCE_BLOCK_STATUS;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleTestInstanceBlockStatus(this);
   }

   static {
      STREAM_CODEC = StreamCodec.composite(ComponentSerialization.STREAM_CODEC, ClientboundTestInstanceBlockStatus::status, ByteBufCodecs.optional(Vec3i.STREAM_CODEC), ClientboundTestInstanceBlockStatus::size, ClientboundTestInstanceBlockStatus::new);
   }
}
