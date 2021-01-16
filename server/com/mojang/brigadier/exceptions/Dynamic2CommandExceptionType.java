package com.mojang.brigadier.exceptions;

import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.Message;

public class Dynamic2CommandExceptionType implements CommandExceptionType {
   private final Dynamic2CommandExceptionType.Function function;

   public Dynamic2CommandExceptionType(Dynamic2CommandExceptionType.Function var1) {
      super();
      this.function = var1;
   }

   public CommandSyntaxException create(Object var1, Object var2) {
      return new CommandSyntaxException(this, this.function.apply(var1, var2));
   }

   public CommandSyntaxException createWithContext(ImmutableStringReader var1, Object var2, Object var3) {
      return new CommandSyntaxException(this, this.function.apply(var2, var3), var1.getString(), var1.getCursor());
   }

   public interface Function {
      Message apply(Object var1, Object var2);
   }
}
