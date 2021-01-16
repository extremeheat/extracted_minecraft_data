package com.mojang.brigadier.exceptions;

import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.Message;

public class Dynamic4CommandExceptionType implements CommandExceptionType {
   private final Dynamic4CommandExceptionType.Function function;

   public Dynamic4CommandExceptionType(Dynamic4CommandExceptionType.Function var1) {
      super();
      this.function = var1;
   }

   public CommandSyntaxException create(Object var1, Object var2, Object var3, Object var4) {
      return new CommandSyntaxException(this, this.function.apply(var1, var2, var3, var4));
   }

   public CommandSyntaxException createWithContext(ImmutableStringReader var1, Object var2, Object var3, Object var4, Object var5) {
      return new CommandSyntaxException(this, this.function.apply(var2, var3, var4, var5), var1.getString(), var1.getCursor());
   }

   public interface Function {
      Message apply(Object var1, Object var2, Object var3, Object var4);
   }
}
