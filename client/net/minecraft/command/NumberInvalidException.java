package net.minecraft.command;

public class NumberInvalidException extends CommandException {
   public NumberInvalidException() {
      this("commands.generic.num.invalid");
   }

   public NumberInvalidException(String var1, Object... var2) {
      super(var1, var2);
   }
}
