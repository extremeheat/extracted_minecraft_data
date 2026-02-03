package net.minecraft.network.protocol.common.custom;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.StreamDecoder;
import net.minecraft.network.codec.StreamMemberEncoder;
import net.minecraft.resources.Identifier;

public record DiscardedPayload(Identifier id) implements CustomPacketPayload {
   public DiscardedPayload {
      super();
   }

   public static <T extends FriendlyByteBuf> StreamCodec<T, DiscardedPayload> codec(final Identifier id, final int maxPayloadSize) {
      return CustomPacketPayload.codec((StreamMemberEncoder)((payload, buf) -> {
      }), (StreamDecoder)((buf) -> {
         int length = buf.readableBytes();
         if (length >= 0 && length <= maxPayloadSize) {
            buf.skipBytes(length);
            return new DiscardedPayload(id);
         } else {
            throw new IllegalArgumentException("Payload may not be larger than " + maxPayloadSize + " bytes");
         }
      }));
   }

   public CustomPacketPayload.Type<DiscardedPayload> type() {
      return new CustomPacketPayload.Type<DiscardedPayload>(this.id);
   }
}
