package net.minecraft.command;

import com.mojang.brigadier.exceptions.BuiltInExceptionProvider;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.util.text.TextComponentTranslation;

public class TranslatableExceptionProvider implements BuiltInExceptionProvider {
   private static final Dynamic2CommandExceptionType field_208636_a = new Dynamic2CommandExceptionType((var0, var1) -> {
      return new TextComponentTranslation("argument.double.low", new Object[]{var1, var0});
   });
   private static final Dynamic2CommandExceptionType field_208637_b = new Dynamic2CommandExceptionType((var0, var1) -> {
      return new TextComponentTranslation("argument.double.big", new Object[]{var1, var0});
   });
   private static final Dynamic2CommandExceptionType field_208638_c = new Dynamic2CommandExceptionType((var0, var1) -> {
      return new TextComponentTranslation("argument.float.low", new Object[]{var1, var0});
   });
   private static final Dynamic2CommandExceptionType field_208639_d = new Dynamic2CommandExceptionType((var0, var1) -> {
      return new TextComponentTranslation("argument.float.big", new Object[]{var1, var0});
   });
   private static final Dynamic2CommandExceptionType field_208640_e = new Dynamic2CommandExceptionType((var0, var1) -> {
      return new TextComponentTranslation("argument.integer.low", new Object[]{var1, var0});
   });
   private static final Dynamic2CommandExceptionType field_208641_f = new Dynamic2CommandExceptionType((var0, var1) -> {
      return new TextComponentTranslation("argument.integer.big", new Object[]{var1, var0});
   });
   private static final DynamicCommandExceptionType field_208642_g = new DynamicCommandExceptionType((var0) -> {
      return new TextComponentTranslation("argument.literal.incorrect", new Object[]{var0});
   });
   private static final SimpleCommandExceptionType field_208643_h = new SimpleCommandExceptionType(new TextComponentTranslation("parsing.quote.expected.start", new Object[0]));
   private static final SimpleCommandExceptionType field_208644_i = new SimpleCommandExceptionType(new TextComponentTranslation("parsing.quote.expected.end", new Object[0]));
   private static final DynamicCommandExceptionType field_208645_j = new DynamicCommandExceptionType((var0) -> {
      return new TextComponentTranslation("parsing.quote.escape", new Object[]{var0});
   });
   private static final DynamicCommandExceptionType field_208646_k = new DynamicCommandExceptionType((var0) -> {
      return new TextComponentTranslation("parsing.bool.invalid", new Object[]{var0});
   });
   private static final DynamicCommandExceptionType field_208647_l = new DynamicCommandExceptionType((var0) -> {
      return new TextComponentTranslation("parsing.int.invalid", new Object[]{var0});
   });
   private static final SimpleCommandExceptionType field_208648_m = new SimpleCommandExceptionType(new TextComponentTranslation("parsing.int.expected", new Object[0]));
   private static final DynamicCommandExceptionType field_208649_n = new DynamicCommandExceptionType((var0) -> {
      return new TextComponentTranslation("parsing.double.invalid", new Object[]{var0});
   });
   private static final SimpleCommandExceptionType field_208650_o = new SimpleCommandExceptionType(new TextComponentTranslation("parsing.double.expected", new Object[0]));
   private static final DynamicCommandExceptionType field_208651_p = new DynamicCommandExceptionType((var0) -> {
      return new TextComponentTranslation("parsing.float.invalid", new Object[]{var0});
   });
   private static final SimpleCommandExceptionType field_208652_q = new SimpleCommandExceptionType(new TextComponentTranslation("parsing.float.expected", new Object[0]));
   private static final SimpleCommandExceptionType field_208653_r = new SimpleCommandExceptionType(new TextComponentTranslation("parsing.bool.expected", new Object[0]));
   private static final DynamicCommandExceptionType field_208654_s = new DynamicCommandExceptionType((var0) -> {
      return new TextComponentTranslation("parsing.expected", new Object[]{var0});
   });
   private static final SimpleCommandExceptionType field_208655_t = new SimpleCommandExceptionType(new TextComponentTranslation("command.unknown.command", new Object[0]));
   private static final SimpleCommandExceptionType field_208656_u = new SimpleCommandExceptionType(new TextComponentTranslation("command.unknown.argument", new Object[0]));
   private static final SimpleCommandExceptionType field_208657_v = new SimpleCommandExceptionType(new TextComponentTranslation("command.expected.separator", new Object[0]));
   private static final DynamicCommandExceptionType field_208658_w = new DynamicCommandExceptionType((var0) -> {
      return new TextComponentTranslation("command.exception", new Object[]{var0});
   });

   public TranslatableExceptionProvider() {
      super();
   }

   public Dynamic2CommandExceptionType doubleTooLow() {
      return field_208636_a;
   }

   public Dynamic2CommandExceptionType doubleTooHigh() {
      return field_208637_b;
   }

   public Dynamic2CommandExceptionType floatTooLow() {
      return field_208638_c;
   }

   public Dynamic2CommandExceptionType floatTooHigh() {
      return field_208639_d;
   }

   public Dynamic2CommandExceptionType integerTooLow() {
      return field_208640_e;
   }

   public Dynamic2CommandExceptionType integerTooHigh() {
      return field_208641_f;
   }

   public DynamicCommandExceptionType literalIncorrect() {
      return field_208642_g;
   }

   public SimpleCommandExceptionType readerExpectedStartOfQuote() {
      return field_208643_h;
   }

   public SimpleCommandExceptionType readerExpectedEndOfQuote() {
      return field_208644_i;
   }

   public DynamicCommandExceptionType readerInvalidEscape() {
      return field_208645_j;
   }

   public DynamicCommandExceptionType readerInvalidBool() {
      return field_208646_k;
   }

   public DynamicCommandExceptionType readerInvalidInt() {
      return field_208647_l;
   }

   public SimpleCommandExceptionType readerExpectedInt() {
      return field_208648_m;
   }

   public DynamicCommandExceptionType readerInvalidDouble() {
      return field_208649_n;
   }

   public SimpleCommandExceptionType readerExpectedDouble() {
      return field_208650_o;
   }

   public DynamicCommandExceptionType readerInvalidFloat() {
      return field_208651_p;
   }

   public SimpleCommandExceptionType readerExpectedFloat() {
      return field_208652_q;
   }

   public SimpleCommandExceptionType readerExpectedBool() {
      return field_208653_r;
   }

   public DynamicCommandExceptionType readerExpectedSymbol() {
      return field_208654_s;
   }

   public SimpleCommandExceptionType dispatcherUnknownCommand() {
      return field_208655_t;
   }

   public SimpleCommandExceptionType dispatcherUnknownArgument() {
      return field_208656_u;
   }

   public SimpleCommandExceptionType dispatcherExpectedArgumentSeparator() {
      return field_208657_v;
   }

   public DynamicCommandExceptionType dispatcherParseException() {
      return field_208658_w;
   }
}
