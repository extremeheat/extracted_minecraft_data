package com.mojang.brigadier.exceptions;

import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.Message;

public class Dynamic3CommandExceptionType implements CommandExceptionType {
   private final Dynamic3CommandExceptionType.Function function;

   public Dynamic3CommandExceptionType(Dynamic3CommandExceptionType.Function var1) {
      super();
      this.function = var1;
   }

   public CommandSyntaxException create(Object var1, Object var2, Object var3) {
      return new CommandSyntaxException(this, this.function.apply(var1, var2, var3));
   }

   public CommandSyntaxException createWithContext(ImmutableStringReader var1, Object var2, Object var3, Object var4) {
      return new CommandSyntaxException(this, this.function.apply(var2, var3, var4), var1.getString(), var1.getCursor());
   }

   public interface Function {
      Message apply(Object var1, Object var2, Object var3);
   }
}
