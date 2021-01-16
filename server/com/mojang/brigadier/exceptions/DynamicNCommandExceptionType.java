package com.mojang.brigadier.exceptions;

import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.Message;

public class DynamicNCommandExceptionType implements CommandExceptionType {
   private final DynamicNCommandExceptionType.Function function;

   public DynamicNCommandExceptionType(DynamicNCommandExceptionType.Function var1) {
      super();
      this.function = var1;
   }

   public CommandSyntaxException create(Object var1, Object... var2) {
      return new CommandSyntaxException(this, this.function.apply(var2));
   }

   public CommandSyntaxException createWithContext(ImmutableStringReader var1, Object... var2) {
      return new CommandSyntaxException(this, this.function.apply(var2), var1.getString(), var1.getCursor());
   }

   public interface Function {
      Message apply(Object[] var1);
   }
}
