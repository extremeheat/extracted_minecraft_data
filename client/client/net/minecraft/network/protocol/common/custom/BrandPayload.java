package net.minecraft.network.protocol.common.custom;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record BrandPayload(String brand) implements CustomPacketPayload {
   public static final StreamCodec<FriendlyByteBuf, BrandPayload> STREAM_CODEC = CustomPacketPayload.codec(BrandPayload::write, BrandPayload::new);
   public static final CustomPacketPayload.Type<BrandPayload> TYPE = CustomPacketPayload.createType("brand");

   private BrandPayload(FriendlyByteBuf var1) {
      this(var1.readUtf());
   }

   public BrandPayload(String brand) {
      super();
      this.brand = brand;
   }

   private void write(FriendlyByteBuf var1) {
      var1.writeUtf(this.brand);
   }

   @Override
   public CustomPacketPayload.Type<BrandPayload> type() {
      return TYPE;
   }
}