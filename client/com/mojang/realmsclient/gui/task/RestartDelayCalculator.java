package com.mojang.realmsclient.gui.task;

public interface RestartDelayCalculator {
   void markExecutionStart();

   long getNextDelayMs();
}
