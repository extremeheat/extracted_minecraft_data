package net.minecraft.network.protocol.login.custom;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.Identifier;

public record DiscardedQueryPayload(Identifier id) implements CustomQueryPayload {
   public DiscardedQueryPayload(Identifier var1) {
      super();
      this.id = var1;
   }

   public void write(FriendlyByteBuf var1) {
   }
}
