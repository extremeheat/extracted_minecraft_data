package net.minecraft.network.protocol.common.custom;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record DiscardedPayload(ResourceLocation a) implements CustomPacketPayload {
   private final ResourceLocation id;

   public DiscardedPayload(ResourceLocation var1) {
      super();
      this.id = var1;
   }

   @Override
   public void write(FriendlyByteBuf var1) {
   }
}
