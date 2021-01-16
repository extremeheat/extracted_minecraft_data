package com.mojang.brigadier.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Arrays;
import java.util.Collection;

public class IntegerArgumentType implements ArgumentType<Integer> {
   private static final Collection<String> EXAMPLES = Arrays.asList("0", "123", "-123");
   private final int minimum;
   private final int maximum;

   private IntegerArgumentType(int var1, int var2) {
      super();
      this.minimum = var1;
      this.maximum = var2;
   }

   public static IntegerArgumentType integer() {
      return integer(-2147483648);
   }

   public static IntegerArgumentType integer(int var0) {
      return integer(var0, 2147483647);
   }

   public static IntegerArgumentType integer(int var0, int var1) {
      return new IntegerArgumentType(var0, var1);
   }

   public static int getInteger(CommandContext<?> var0, String var1) {
      return (Integer)var0.getArgument(var1, Integer.TYPE);
   }

   public int getMinimum() {
      return this.minimum;
   }

   public int getMaximum() {
      return this.maximum;
   }

   public Integer parse(StringReader var1) throws CommandSyntaxException {
      int var2 = var1.getCursor();
      int var3 = var1.readInt();
      if (var3 < this.minimum) {
         var1.setCursor(var2);
         throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.integerTooLow().createWithContext(var1, var3, this.minimum);
      } else if (var3 > this.maximum) {
         var1.setCursor(var2);
         throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.integerTooHigh().createWithContext(var1, var3, this.maximum);
      } else {
         return var3;
      }
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof IntegerArgumentType)) {
         return false;
      } else {
         IntegerArgumentType var2 = (IntegerArgumentType)var1;
         return this.maximum == var2.maximum && this.minimum == var2.minimum;
      }
   }

   public int hashCode() {
      return 31 * this.minimum + this.maximum;
   }

   public String toString() {
      if (this.minimum == -2147483648 && this.maximum == 2147483647) {
         return "integer()";
      } else {
         return this.maximum == 2147483647 ? "integer(" + this.minimum + ")" : "integer(" + this.minimum + ", " + this.maximum + ")";
      }
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }
}
