package net.minecraft.gametest.framework;

import net.minecraft.network.chat.Component;

public class GameTestAssertException extends GameTestException {
   protected final Component message;
   protected final int tick;

   public GameTestAssertException(Component var1, int var2) {
      super(var1.getString());
      this.message = var1;
      this.tick = var2;
   }

   public Component getDescription() {
      return Component.translatable("test.error.tick", this.message, this.tick);
   }

   public String getMessage() {
      return this.getDescription().getString();
   }
}
