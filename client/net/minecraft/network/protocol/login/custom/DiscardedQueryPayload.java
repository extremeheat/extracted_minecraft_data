package net.minecraft.network.protocol.login.custom;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record DiscardedQueryPayload(ResourceLocation id) implements CustomQueryPayload {
   public DiscardedQueryPayload(ResourceLocation var1) {
      super();
      this.id = var1;
   }

   public void write(FriendlyByteBuf var1) {
   }

   public ResourceLocation id() {
      return this.id;
   }
}
