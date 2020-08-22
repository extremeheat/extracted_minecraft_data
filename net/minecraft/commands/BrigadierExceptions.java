package net.minecraft.commands;

import com.mojang.brigadier.exceptions.BuiltInExceptionProvider;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.network.chat.TranslatableComponent;

public class BrigadierExceptions implements BuiltInExceptionProvider {
   private static final Dynamic2CommandExceptionType DOUBLE_TOO_SMALL = new Dynamic2CommandExceptionType((var0, var1) -> {
      return new TranslatableComponent("argument.double.low", new Object[]{var1, var0});
   });
   private static final Dynamic2CommandExceptionType DOUBLE_TOO_BIG = new Dynamic2CommandExceptionType((var0, var1) -> {
      return new TranslatableComponent("argument.double.big", new Object[]{var1, var0});
   });
   private static final Dynamic2CommandExceptionType FLOAT_TOO_SMALL = new Dynamic2CommandExceptionType((var0, var1) -> {
      return new TranslatableComponent("argument.float.low", new Object[]{var1, var0});
   });
   private static final Dynamic2CommandExceptionType FLOAT_TOO_BIG = new Dynamic2CommandExceptionType((var0, var1) -> {
      return new TranslatableComponent("argument.float.big", new Object[]{var1, var0});
   });
   private static final Dynamic2CommandExceptionType INTEGER_TOO_SMALL = new Dynamic2CommandExceptionType((var0, var1) -> {
      return new TranslatableComponent("argument.integer.low", new Object[]{var1, var0});
   });
   private static final Dynamic2CommandExceptionType INTEGER_TOO_BIG = new Dynamic2CommandExceptionType((var0, var1) -> {
      return new TranslatableComponent("argument.integer.big", new Object[]{var1, var0});
   });
   private static final Dynamic2CommandExceptionType LONG_TOO_SMALL = new Dynamic2CommandExceptionType((var0, var1) -> {
      return new TranslatableComponent("argument.long.low", new Object[]{var1, var0});
   });
   private static final Dynamic2CommandExceptionType LONG_TOO_BIG = new Dynamic2CommandExceptionType((var0, var1) -> {
      return new TranslatableComponent("argument.long.big", new Object[]{var1, var0});
   });
   private static final DynamicCommandExceptionType LITERAL_INCORRECT = new DynamicCommandExceptionType((var0) -> {
      return new TranslatableComponent("argument.literal.incorrect", new Object[]{var0});
   });
   private static final SimpleCommandExceptionType READER_EXPECTED_START_OF_QUOTE = new SimpleCommandExceptionType(new TranslatableComponent("parsing.quote.expected.start", new Object[0]));
   private static final SimpleCommandExceptionType READER_EXPECTED_END_OF_QUOTE = new SimpleCommandExceptionType(new TranslatableComponent("parsing.quote.expected.end", new Object[0]));
   private static final DynamicCommandExceptionType READER_INVALID_ESCAPE = new DynamicCommandExceptionType((var0) -> {
      return new TranslatableComponent("parsing.quote.escape", new Object[]{var0});
   });
   private static final DynamicCommandExceptionType READER_INVALID_BOOL = new DynamicCommandExceptionType((var0) -> {
      return new TranslatableComponent("parsing.bool.invalid", new Object[]{var0});
   });
   private static final DynamicCommandExceptionType READER_INVALID_INT = new DynamicCommandExceptionType((var0) -> {
      return new TranslatableComponent("parsing.int.invalid", new Object[]{var0});
   });
   private static final SimpleCommandExceptionType READER_EXPECTED_INT = new SimpleCommandExceptionType(new TranslatableComponent("parsing.int.expected", new Object[0]));
   private static final DynamicCommandExceptionType READER_INVALID_LONG = new DynamicCommandExceptionType((var0) -> {
      return new TranslatableComponent("parsing.long.invalid", new Object[]{var0});
   });
   private static final SimpleCommandExceptionType READER_EXPECTED_LONG = new SimpleCommandExceptionType(new TranslatableComponent("parsing.long.expected", new Object[0]));
   private static final DynamicCommandExceptionType READER_INVALID_DOUBLE = new DynamicCommandExceptionType((var0) -> {
      return new TranslatableComponent("parsing.double.invalid", new Object[]{var0});
   });
   private static final SimpleCommandExceptionType READER_EXPECTED_DOUBLE = new SimpleCommandExceptionType(new TranslatableComponent("parsing.double.expected", new Object[0]));
   private static final DynamicCommandExceptionType READER_INVALID_FLOAT = new DynamicCommandExceptionType((var0) -> {
      return new TranslatableComponent("parsing.float.invalid", new Object[]{var0});
   });
   private static final SimpleCommandExceptionType READER_EXPECTED_FLOAT = new SimpleCommandExceptionType(new TranslatableComponent("parsing.float.expected", new Object[0]));
   private static final SimpleCommandExceptionType READER_EXPECTED_BOOL = new SimpleCommandExceptionType(new TranslatableComponent("parsing.bool.expected", new Object[0]));
   private static final DynamicCommandExceptionType READER_EXPECTED_SYMBOL = new DynamicCommandExceptionType((var0) -> {
      return new TranslatableComponent("parsing.expected", new Object[]{var0});
   });
   private static final SimpleCommandExceptionType DISPATCHER_UNKNOWN_COMMAND = new SimpleCommandExceptionType(new TranslatableComponent("command.unknown.command", new Object[0]));
   private static final SimpleCommandExceptionType DISPATCHER_UNKNOWN_ARGUMENT = new SimpleCommandExceptionType(new TranslatableComponent("command.unknown.argument", new Object[0]));
   private static final SimpleCommandExceptionType DISPATCHER_EXPECTED_ARGUMENT_SEPARATOR = new SimpleCommandExceptionType(new TranslatableComponent("command.expected.separator", new Object[0]));
   private static final DynamicCommandExceptionType DISPATCHER_PARSE_EXCEPTION = new DynamicCommandExceptionType((var0) -> {
      return new TranslatableComponent("command.exception", new Object[]{var0});
   });

   public Dynamic2CommandExceptionType doubleTooLow() {
      return DOUBLE_TOO_SMALL;
   }

   public Dynamic2CommandExceptionType doubleTooHigh() {
      return DOUBLE_TOO_BIG;
   }

   public Dynamic2CommandExceptionType floatTooLow() {
      return FLOAT_TOO_SMALL;
   }

   public Dynamic2CommandExceptionType floatTooHigh() {
      return FLOAT_TOO_BIG;
   }

   public Dynamic2CommandExceptionType integerTooLow() {
      return INTEGER_TOO_SMALL;
   }

   public Dynamic2CommandExceptionType integerTooHigh() {
      return INTEGER_TOO_BIG;
   }

   public Dynamic2CommandExceptionType longTooLow() {
      return LONG_TOO_SMALL;
   }

   public Dynamic2CommandExceptionType longTooHigh() {
      return LONG_TOO_BIG;
   }

   public DynamicCommandExceptionType literalIncorrect() {
      return LITERAL_INCORRECT;
   }

   public SimpleCommandExceptionType readerExpectedStartOfQuote() {
      return READER_EXPECTED_START_OF_QUOTE;
   }

   public SimpleCommandExceptionType readerExpectedEndOfQuote() {
      return READER_EXPECTED_END_OF_QUOTE;
   }

   public DynamicCommandExceptionType readerInvalidEscape() {
      return READER_INVALID_ESCAPE;
   }

   public DynamicCommandExceptionType readerInvalidBool() {
      return READER_INVALID_BOOL;
   }

   public DynamicCommandExceptionType readerInvalidInt() {
      return READER_INVALID_INT;
   }

   public SimpleCommandExceptionType readerExpectedInt() {
      return READER_EXPECTED_INT;
   }

   public DynamicCommandExceptionType readerInvalidLong() {
      return READER_INVALID_LONG;
   }

   public SimpleCommandExceptionType readerExpectedLong() {
      return READER_EXPECTED_LONG;
   }

   public DynamicCommandExceptionType readerInvalidDouble() {
      return READER_INVALID_DOUBLE;
   }

   public SimpleCommandExceptionType readerExpectedDouble() {
      return READER_EXPECTED_DOUBLE;
   }

   public DynamicCommandExceptionType readerInvalidFloat() {
      return READER_INVALID_FLOAT;
   }

   public SimpleCommandExceptionType readerExpectedFloat() {
      return READER_EXPECTED_FLOAT;
   }

   public SimpleCommandExceptionType readerExpectedBool() {
      return READER_EXPECTED_BOOL;
   }

   public DynamicCommandExceptionType readerExpectedSymbol() {
      return READER_EXPECTED_SYMBOL;
   }

   public SimpleCommandExceptionType dispatcherUnknownCommand() {
      return DISPATCHER_UNKNOWN_COMMAND;
   }

   public SimpleCommandExceptionType dispatcherUnknownArgument() {
      return DISPATCHER_UNKNOWN_ARGUMENT;
   }

   public SimpleCommandExceptionType dispatcherExpectedArgumentSeparator() {
      return DISPATCHER_EXPECTED_ARGUMENT_SEPARATOR;
   }

   public DynamicCommandExceptionType dispatcherParseException() {
      return DISPATCHER_PARSE_EXCEPTION;
   }
}
