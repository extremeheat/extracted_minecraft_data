package com.mojang.brigadier.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Arrays;
import java.util.Collection;

public class DoubleArgumentType implements ArgumentType<Double> {
   private static final Collection<String> EXAMPLES = Arrays.asList("0", "1.2", ".5", "-1", "-.5", "-1234.56");
   private final double minimum;
   private final double maximum;

   private DoubleArgumentType(double var1, double var3) {
      super();
      this.minimum = var1;
      this.maximum = var3;
   }

   public static DoubleArgumentType doubleArg() {
      return doubleArg(-1.7976931348623157E308D);
   }

   public static DoubleArgumentType doubleArg(double var0) {
      return doubleArg(var0, 1.7976931348623157E308D);
   }

   public static DoubleArgumentType doubleArg(double var0, double var2) {
      return new DoubleArgumentType(var0, var2);
   }

   public static double getDouble(CommandContext<?> var0, String var1) {
      return (Double)var0.getArgument(var1, Double.class);
   }

   public double getMinimum() {
      return this.minimum;
   }

   public double getMaximum() {
      return this.maximum;
   }

   public Double parse(StringReader var1) throws CommandSyntaxException {
      int var2 = var1.getCursor();
      double var3 = var1.readDouble();
      if (var3 < this.minimum) {
         var1.setCursor(var2);
         throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.doubleTooLow().createWithContext(var1, var3, this.minimum);
      } else if (var3 > this.maximum) {
         var1.setCursor(var2);
         throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.doubleTooHigh().createWithContext(var1, var3, this.maximum);
      } else {
         return var3;
      }
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof DoubleArgumentType)) {
         return false;
      } else {
         DoubleArgumentType var2 = (DoubleArgumentType)var1;
         return this.maximum == var2.maximum && this.minimum == var2.minimum;
      }
   }

   public int hashCode() {
      return (int)(31.0D * this.minimum + this.maximum);
   }

   public String toString() {
      if (this.minimum == -1.7976931348623157E308D && this.maximum == 1.7976931348623157E308D) {
         return "double()";
      } else {
         return this.maximum == 1.7976931348623157E308D ? "double(" + this.minimum + ")" : "double(" + this.minimum + ", " + this.maximum + ")";
      }
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }
}
