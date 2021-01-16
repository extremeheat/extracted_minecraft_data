package com.mojang.bridge.game;

import java.util.Date;

public interface GameVersion {
   String getId();

   String getName();

   String getReleaseTarget();

   int getWorldVersion();

   int getProtocolVersion();

   int getPackVersion();

   Date getBuildTime();

   boolean isStable();
}
