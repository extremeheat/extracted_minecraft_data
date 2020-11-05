package net.minecraft.client;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import net.minecraft.network.chat.Component;

public class LogaritmicProgressOption extends ProgressOption {
   public LogaritmicProgressOption(String var1, double var2, double var4, float var6, Function<Options, Double> var7, BiConsumer<Options, Double> var8, BiFunction<Options, ProgressOption, Component> var9) {
      super(var1, var2, var4, var6, var7, var8, var9);
   }

   public double toPct(double var1) {
      return Math.log(var1 / this.minValue) / Math.log(this.maxValue / this.minValue);
   }

   public double toValue(double var1) {
      return this.minValue * Math.pow(2.718281828459045D, Math.log(this.maxValue / this.minValue) * var1);
   }
}
