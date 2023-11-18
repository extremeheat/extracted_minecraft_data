package net.minecraft.commands;

import net.minecraft.network.chat.Component;

public class FunctionInstantiationException extends Exception {
   private final Component messageComponent;

   public FunctionInstantiationException(Component var1) {
      super(var1.getString());
      this.messageComponent = var1;
   }

   public Component messageComponent() {
      return this.messageComponent;
   }
}
