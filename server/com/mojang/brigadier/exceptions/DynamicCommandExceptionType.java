package com.mojang.brigadier.exceptions;

import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.Message;
import java.util.function.Function;

public class DynamicCommandExceptionType implements CommandExceptionType {
   private final Function<Object, Message> function;

   public DynamicCommandExceptionType(Function<Object, Message> var1) {
      super();
      this.function = var1;
   }

   public CommandSyntaxException create(Object var1) {
      return new CommandSyntaxException(this, (Message)this.function.apply(var1));
   }

   public CommandSyntaxException createWithContext(ImmutableStringReader var1, Object var2) {
      return new CommandSyntaxException(this, (Message)this.function.apply(var2), var1.getString(), var1.getCursor());
   }
}
