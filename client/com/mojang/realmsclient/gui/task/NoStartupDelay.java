package com.mojang.realmsclient.gui.task;

public class NoStartupDelay implements RestartDelayCalculator {
   public NoStartupDelay() {
      super();
   }

   @Override
   public void markExecutionStart() {
   }

   @Override
   public long getNextDelayMs() {
      return 0L;
   }
}
