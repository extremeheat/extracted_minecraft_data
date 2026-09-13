package net.minecraft.command;

public class CommandNotFoundException extends CommandException {
   public CommandNotFoundException() {
      this("commands.generic.notFound");
   }

   public CommandNotFoundException(String var1, Object... var2) {
      super(var1, var2);
   }
}
