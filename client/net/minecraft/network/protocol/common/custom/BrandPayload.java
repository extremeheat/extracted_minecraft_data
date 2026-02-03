package net.minecraft.network.protocol.common.custom;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record BrandPayload(String brand) implements CustomPacketPayload {
   public static final StreamCodec<FriendlyByteBuf, BrandPayload> STREAM_CODEC = CustomPacketPayload.codec(BrandPayload::write, BrandPayload::new);
   public static final CustomPacketPayload.Type<BrandPayload> TYPE = CustomPacketPayload.<BrandPayload>createType("brand");

   private BrandPayload(final FriendlyByteBuf input) {
      this(input.readUtf());
   }

   public BrandPayload {
      super();
   }

   private void write(final FriendlyByteBuf output) {
      output.writeUtf(this.brand);
   }

   public CustomPacketPayload.Type<BrandPayload> type() {
      return TYPE;
   }
}
