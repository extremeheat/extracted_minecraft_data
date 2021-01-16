package com.mojang.brigadier.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Arrays;
import java.util.Collection;

public class LongArgumentType implements ArgumentType<Long> {
   private static final Collection<String> EXAMPLES = Arrays.asList("0", "123", "-123");
   private final long minimum;
   private final long maximum;

   private LongArgumentType(long var1, long var3) {
      super();
      this.minimum = var1;
      this.maximum = var3;
   }

   public static LongArgumentType longArg() {
      return longArg(-9223372036854775808L);
   }

   public static LongArgumentType longArg(long var0) {
      return longArg(var0, 9223372036854775807L);
   }

   public static LongArgumentType longArg(long var0, long var2) {
      return new LongArgumentType(var0, var2);
   }

   public static long getLong(CommandContext<?> var0, String var1) {
      return (Long)var0.getArgument(var1, Long.TYPE);
   }

   public long getMinimum() {
      return this.minimum;
   }

   public long getMaximum() {
      return this.maximum;
   }

   public Long parse(StringReader var1) throws CommandSyntaxException {
      int var2 = var1.getCursor();
      long var3 = var1.readLong();
      if (var3 < this.minimum) {
         var1.setCursor(var2);
         throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.longTooLow().createWithContext(var1, var3, this.minimum);
      } else if (var3 > this.maximum) {
         var1.setCursor(var2);
         throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.longTooHigh().createWithContext(var1, var3, this.maximum);
      } else {
         return var3;
      }
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof LongArgumentType)) {
         return false;
      } else {
         LongArgumentType var2 = (LongArgumentType)var1;
         return this.maximum == var2.maximum && this.minimum == var2.minimum;
      }
   }

   public int hashCode() {
      return 31 * Long.hashCode(this.minimum) + Long.hashCode(this.maximum);
   }

   public String toString() {
      if (this.minimum == -9223372036854775808L && this.maximum == 9223372036854775807L) {
         return "longArg()";
      } else {
         return this.maximum == 9223372036854775807L ? "longArg(" + this.minimum + ")" : "longArg(" + this.minimum + ", " + this.maximum + ")";
      }
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }
}
