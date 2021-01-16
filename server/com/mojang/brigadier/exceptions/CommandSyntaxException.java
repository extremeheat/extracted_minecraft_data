package com.mojang.brigadier.exceptions;

import com.mojang.brigadier.Message;

public class CommandSyntaxException extends Exception {
   public static final int CONTEXT_AMOUNT = 10;
   public static boolean ENABLE_COMMAND_STACK_TRACES = true;
   public static BuiltInExceptionProvider BUILT_IN_EXCEPTIONS = new BuiltInExceptions();
   private final CommandExceptionType type;
   private final Message message;
   private final String input;
   private final int cursor;

   public CommandSyntaxException(CommandExceptionType var1, Message var2) {
      super(var2.getString(), (Throwable)null, ENABLE_COMMAND_STACK_TRACES, ENABLE_COMMAND_STACK_TRACES);
      this.type = var1;
      this.message = var2;
      this.input = null;
      this.cursor = -1;
   }

   public CommandSyntaxException(CommandExceptionType var1, Message var2, String var3, int var4) {
      super(var2.getString(), (Throwable)null, ENABLE_COMMAND_STACK_TRACES, ENABLE_COMMAND_STACK_TRACES);
      this.type = var1;
      this.message = var2;
      this.input = var3;
      this.cursor = var4;
   }

   public String getMessage() {
      String var1 = this.message.getString();
      String var2 = this.getContext();
      if (var2 != null) {
         var1 = var1 + " at position " + this.cursor + ": " + var2;
      }

      return var1;
   }

   public Message getRawMessage() {
      return this.message;
   }

   public String getContext() {
      if (this.input != null && this.cursor >= 0) {
         StringBuilder var1 = new StringBuilder();
         int var2 = Math.min(this.input.length(), this.cursor);
         if (var2 > 10) {
            var1.append("...");
         }

         var1.append(this.input.substring(Math.max(0, var2 - 10), var2));
         var1.append("<--[HERE]");
         return var1.toString();
      } else {
         return null;
      }
   }

   public CommandExceptionType getType() {
      return this.type;
   }

   public String getInput() {
      return this.input;
   }

   public int getCursor() {
      return this.cursor;
   }
}
