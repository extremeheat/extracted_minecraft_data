package com.mojang.bridge.game;

import java.util.UUID;

public interface GameSession {
   int getPlayerCount();

   boolean isRemoteServer();

   String getDifficulty();

   String getGameMode();

   UUID getSessionId();
}
