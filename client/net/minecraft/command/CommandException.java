package net.minecraft.command;

public class CommandException extends Exception {
   private final Object[] field_74845_a;

   public CommandException(String var1, Object... var2) {
      super(var1);
      this.field_74845_a = var2;
   }

   public Object[] func_74844_a() {
      return this.field_74845_a;
   }
}
