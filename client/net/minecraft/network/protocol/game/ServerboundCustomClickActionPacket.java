package net.minecraft.network.protocol.game;

import io.netty.buffer.ByteBuf;
import java.util.Optional;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.resources.ResourceLocation;

public record ServerboundCustomClickActionPacket(ResourceLocation id, Optional<String> payload) implements Packet<ServerGamePacketListener> {
   public static final StreamCodec<ByteBuf, ServerboundCustomClickActionPacket> STREAM_CODEC;

   public ServerboundCustomClickActionPacket(ResourceLocation var1, Optional<String> var2) {
      super();
      this.id = var1;
      this.payload = var2;
   }

   public PacketType<ServerboundCustomClickActionPacket> type() {
      return GamePacketTypes.SERVERBOUND_CUSTOM_CLICK_ACTION;
   }

   public void handle(ServerGamePacketListener var1) {
      var1.handleCustomClickAction(this);
   }

   static {
      STREAM_CODEC = StreamCodec.composite(ResourceLocation.STREAM_CODEC, ServerboundCustomClickActionPacket::id, ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs::optional), ServerboundCustomClickActionPacket::payload, ServerboundCustomClickActionPacket::new);
   }
}
