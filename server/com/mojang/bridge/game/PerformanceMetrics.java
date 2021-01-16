package com.mojang.bridge.game;

public interface PerformanceMetrics {
   int getMinTime();

   int getMaxTime();

   int getAverageTime();

   int getSampleCount();
}
