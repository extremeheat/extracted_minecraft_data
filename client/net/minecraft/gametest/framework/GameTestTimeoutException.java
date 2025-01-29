package net.minecraft.gametest.framework;

import net.minecraft.network.chat.Component;

public class GameTestTimeoutException extends GameTestException {
   protected final Component message;

   public GameTestTimeoutException(Component var1) {
      super(var1.getString());
      this.message = var1;
   }

   public Component getDescription() {
      return this.message;
   }
}
