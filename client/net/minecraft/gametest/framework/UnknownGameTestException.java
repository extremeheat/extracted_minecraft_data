package net.minecraft.gametest.framework;

import net.minecraft.network.chat.Component;

public class UnknownGameTestException extends GameTestException {
   private final Throwable reason;

   public UnknownGameTestException(Throwable var1) {
      super(var1.getMessage());
      this.reason = var1;
   }

   public Component getDescription() {
      return Component.translatable("test.error.unknown", this.reason.getMessage());
   }
}
