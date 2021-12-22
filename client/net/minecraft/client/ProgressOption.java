package net.minecraft.client;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.SliderButton;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;

public class ProgressOption extends Option {
   protected final float steps;
   protected final double minValue;
   protected double maxValue;
   private final Function<Options, Double> getter;
   private final BiConsumer<Options, Double> setter;
   private final BiFunction<Options, ProgressOption, Component> toString;
   private final Function<Minecraft, List<FormattedCharSequence>> tooltipSupplier;

   public ProgressOption(String var1, double var2, double var4, float var6, Function<Options, Double> var7, BiConsumer<Options, Double> var8, BiFunction<Options, ProgressOption, Component> var9, Function<Minecraft, List<FormattedCharSequence>> var10) {
      super(var1);
      this.minValue = var2;
      this.maxValue = var4;
      this.steps = var6;
      this.getter = var7;
      this.setter = var8;
      this.toString = var9;
      this.tooltipSupplier = var10;
   }

   public ProgressOption(String var1, double var2, double var4, float var6, Function<Options, Double> var7, BiConsumer<Options, Double> var8, BiFunction<Options, ProgressOption, Component> var9) {
      this(var1, var2, var4, var6, var7, var8, var9, (var0) -> {
         return ImmutableList.of();
      });
   }

   public AbstractWidget createButton(Options var1, int var2, int var3, int var4) {
      List var5 = (List)this.tooltipSupplier.apply(Minecraft.getInstance());
      return new SliderButton(var1, var2, var3, var4, 20, this, var5);
   }

   public double toPct(double var1) {
      return Mth.clamp((this.clamp(var1) - this.minValue) / (this.maxValue - this.minValue), 0.0D, 1.0D);
   }

   public double toValue(double var1) {
      return this.clamp(Mth.lerp(Mth.clamp(var1, 0.0D, 1.0D), this.minValue, this.maxValue));
   }

   private double clamp(double var1) {
      if (this.steps > 0.0F) {
         var1 = (double)(this.steps * (float)Math.round(var1 / (double)this.steps));
      }

      return Mth.clamp(var1, this.minValue, this.maxValue);
   }

   public double getMinValue() {
      return this.minValue;
   }

   public double getMaxValue() {
      return this.maxValue;
   }

   public void setMaxValue(float var1) {
      this.maxValue = (double)var1;
   }

   public void set(Options var1, double var2) {
      this.setter.accept(var1, var2);
   }

   public double get(Options var1) {
      return (Double)this.getter.apply(var1);
   }

   public Component getMessage(Options var1) {
      return (Component)this.toString.apply(var1, this);
   }
}
