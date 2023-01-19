package net.minecraft.client;

import com.mojang.bridge.game.GameSession;
import java.util.UUID;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.player.LocalPlayer;

public class Session implements GameSession {
   private final int players;
   private final boolean isRemoteServer;
   private final String difficulty;
   private final String gameMode;
   private final UUID id;

   public Session(ClientLevel var1, LocalPlayer var2, ClientPacketListener var3) {
      super();
      this.players = var3.getOnlinePlayers().size();
      this.isRemoteServer = !var3.getConnection().isMemoryConnection();
      this.difficulty = var1.getDifficulty().getKey();
      PlayerInfo var4 = var3.getPlayerInfo(var2.getUUID());
      if (var4 != null) {
         this.gameMode = var4.getGameMode().getName();
      } else {
         this.gameMode = "unknown";
      }

      this.id = var3.getId();
   }

   public int getPlayerCount() {
      return this.players;
   }

   public boolean isRemoteServer() {
      return this.isRemoteServer;
   }

   public String getDifficulty() {
      return this.difficulty;
   }

   public String getGameMode() {
      return this.gameMode;
   }

   public UUID getSessionId() {
      return this.id;
   }
}
