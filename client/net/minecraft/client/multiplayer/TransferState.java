package net.minecraft.client.multiplayer;

import java.util.Map;
import java.util.UUID;
import net.minecraft.resources.ResourceLocation;

public record TransferState(Map<ResourceLocation, byte[]> cookies, Map<UUID, PlayerInfo> seenPlayers, boolean seenInsecureChatWarning) {
   public TransferState(Map<ResourceLocation, byte[]> var1, Map<UUID, PlayerInfo> var2, boolean var3) {
      super();
      this.cookies = var1;
      this.seenPlayers = var2;
      this.seenInsecureChatWarning = var3;
   }
}
