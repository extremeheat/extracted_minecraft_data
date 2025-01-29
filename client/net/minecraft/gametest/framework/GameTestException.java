package net.minecraft.gametest.framework;

import net.minecraft.network.chat.Component;

public abstract class GameTestException extends RuntimeException {
   public GameTestException(String var1) {
      super(var1);
   }

   public abstract Component getDescription();
}
