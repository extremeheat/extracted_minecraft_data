package com.mojang.bridge.game;

import com.mojang.bridge.launcher.SessionEventListener;

public interface RunningGame {
   GameVersion getVersion();

   Language getSelectedLanguage();

   GameSession getCurrentSession();

   PerformanceMetrics getPerformanceMetrics();

   void setSessionEventListener(SessionEventListener var1);
}
