package com.mojang.brigadier.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Arrays;
import java.util.Collection;

public class FloatArgumentType implements ArgumentType<Float> {
   private static final Collection<String> EXAMPLES = Arrays.asList("0", "1.2", ".5", "-1", "-.5", "-1234.56");
   private final float minimum;
   private final float maximum;

   private FloatArgumentType(float var1, float var2) {
      super();
      this.minimum = var1;
      this.maximum = var2;
   }

   public static FloatArgumentType floatArg() {
      return floatArg(-3.4028235E38F);
   }

   public static FloatArgumentType floatArg(float var0) {
      return floatArg(var0, 3.4028235E38F);
   }

   public static FloatArgumentType floatArg(float var0, float var1) {
      return new FloatArgumentType(var0, var1);
   }

   public static float getFloat(CommandContext<?> var0, String var1) {
      return (Float)var0.getArgument(var1, Float.class);
   }

   public float getMinimum() {
      return this.minimum;
   }

   public float getMaximum() {
      return this.maximum;
   }

   public Float parse(StringReader var1) throws CommandSyntaxException {
      int var2 = var1.getCursor();
      float var3 = var1.readFloat();
      if (var3 < this.minimum) {
         var1.setCursor(var2);
         throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.floatTooLow().createWithContext(var1, var3, this.minimum);
      } else if (var3 > this.maximum) {
         var1.setCursor(var2);
         throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.floatTooHigh().createWithContext(var1, var3, this.maximum);
      } else {
         return var3;
      }
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof FloatArgumentType)) {
         return false;
      } else {
         FloatArgumentType var2 = (FloatArgumentType)var1;
         return this.maximum == var2.maximum && this.minimum == var2.minimum;
      }
   }

   public int hashCode() {
      return (int)(31.0F * this.minimum + this.maximum);
   }

   public String toString() {
      if (this.minimum == -3.4028235E38F && this.maximum == 3.4028235E38F) {
         return "float()";
      } else {
         return this.maximum == 3.4028235E38F ? "float(" + this.minimum + ")" : "float(" + this.minimum + ", " + this.maximum + ")";
      }
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }
}
