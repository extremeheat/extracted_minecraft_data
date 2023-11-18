package net.minecraft.network.protocol.login.custom;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record DiscardedQueryPayload(ResourceLocation a) implements CustomQueryPayload {
   private final ResourceLocation id;

   public DiscardedQueryPayload(ResourceLocation var1) {
      super();
      this.id = var1;
   }

   @Override
   public void write(FriendlyByteBuf var1) {
   }
}
