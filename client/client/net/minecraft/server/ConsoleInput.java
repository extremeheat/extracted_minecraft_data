package net.minecraft.server;

import net.minecraft.commands.CommandSourceStack;

public class ConsoleInput {
   public final String msg;
   public final CommandSourceStack source;

   public ConsoleInput(String var1, CommandSourceStack var2) {
      super();
      this.msg = var1;
      this.source = var2;
   }
}
