package net.minecraft.command;

public class PlayerNotFoundException extends CommandException {
   public PlayerNotFoundException() {
      this("commands.generic.player.notFound");
   }

   public PlayerNotFoundException(String var1, Object... var2) {
      super(var1, var2);
   }
}
