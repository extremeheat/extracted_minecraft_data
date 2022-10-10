package net.minecraft.command;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.util.text.ITextComponent;

public class CommandException extends RuntimeException {
   private final ITextComponent field_197004_a;

   public CommandException(ITextComponent var1) {
      super(var1.func_150261_e(), (Throwable)null, CommandSyntaxException.ENABLE_COMMAND_STACK_TRACES, CommandSyntaxException.ENABLE_COMMAND_STACK_TRACES);
      this.field_197004_a = var1;
   }

   public ITextComponent func_197003_a() {
      return this.field_197004_a;
   }
}
