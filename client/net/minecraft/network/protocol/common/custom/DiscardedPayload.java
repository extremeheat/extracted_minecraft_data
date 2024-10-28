package net.minecraft.network.protocol.common.custom;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record DiscardedPayload(ResourceLocation id) implements CustomPacketPayload {
   public DiscardedPayload(ResourceLocation var1) {
      super();
      this.id = var1;
   }

   public static <T extends FriendlyByteBuf> StreamCodec<T, DiscardedPayload> codec(ResourceLocation var0, int var1) {
      return CustomPacketPayload.codec((var0x, var1x) -> {
      }, (var2) -> {
         int var3 = var2.readableBytes();
         if (var3 >= 0 && var3 <= var1) {
            var2.skipBytes(var3);
            return new DiscardedPayload(var0);
         } else {
            throw new IllegalArgumentException("Payload may not be larger than " + var1 + " bytes");
         }
      });
   }

   public CustomPacketPayload.Type<DiscardedPayload> type() {
      return new CustomPacketPayload.Type(this.id);
   }

   public ResourceLocation id() {
      return this.id;
   }
}
