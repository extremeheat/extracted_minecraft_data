package com.mojang.brigadier.exceptions;

import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.Message;

public class SimpleCommandExceptionType implements CommandExceptionType {
   private final Message message;

   public SimpleCommandExceptionType(Message var1) {
      super();
      this.message = var1;
   }

   public CommandSyntaxException create() {
      return new CommandSyntaxException(this, this.message);
   }

   public CommandSyntaxException createWithContext(ImmutableStringReader var1) {
      return new CommandSyntaxException(this, this.message, var1.getString(), var1.getCursor());
   }

   public String toString() {
      return this.message.getString();
   }
}
