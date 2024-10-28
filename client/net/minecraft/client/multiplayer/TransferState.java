package net.minecraft.client.multiplayer;

import java.util.Map;
import net.minecraft.resources.ResourceLocation;

public record TransferState(Map<ResourceLocation, byte[]> cookies) {
   public TransferState(Map<ResourceLocation, byte[]> cookies) {
      super();
      this.cookies = cookies;
   }

   public Map<ResourceLocation, byte[]> cookies() {
      return this.cookies;
   }
}
