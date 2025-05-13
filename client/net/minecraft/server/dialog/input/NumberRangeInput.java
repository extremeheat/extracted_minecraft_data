package net.minecraft.server.dialog.input;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;

public record NumberRangeInput(int width, Component label, String labelFormat, RangeInfo rangeInfo) implements InputControl {
   public static final MapCodec<NumberRangeInput> MAP_CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(ExtraCodecs.POSITIVE_INT.optionalFieldOf("width", 200).forGetter(NumberRangeInput::width), ComponentSerialization.CODEC.fieldOf("label").forGetter(NumberRangeInput::label), Codec.STRING.optionalFieldOf("label_format", "options.generic_value").forGetter(NumberRangeInput::labelFormat), NumberRangeInput.RangeInfo.MAP_CODEC.forGetter(NumberRangeInput::rangeInfo)).apply(var0, NumberRangeInput::new));

   public NumberRangeInput(int var1, Component var2, String var3, RangeInfo var4) {
      super();
      this.width = var1;
      this.label = var2;
      this.labelFormat = var3;
      this.rangeInfo = var4;
   }

   public MapCodec<NumberRangeInput> mapCodec() {
      return MAP_CODEC;
   }

   public Component computeLabel(String var1) {
      return Component.translatable(this.labelFormat, this.label, var1);
   }

   public static record RangeInfo(double start, double end, Optional<Double> initial, int steps) {
      public static final MapCodec<RangeInfo> MAP_CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(Codec.DOUBLE.fieldOf("start").forGetter(RangeInfo::start), Codec.DOUBLE.fieldOf("end").forGetter(RangeInfo::end), Codec.DOUBLE.optionalFieldOf("initial").forGetter(RangeInfo::initial), ExtraCodecs.POSITIVE_INT.fieldOf("steps").forGetter(RangeInfo::steps)).apply(var0, RangeInfo::new)).validate((var0) -> {
         if (var0.initial.isPresent()) {
            double var1 = (Double)var0.initial.get();
            double var3 = Math.min(var0.start, var0.end);
            double var5 = Math.max(var0.start, var0.end);
            if (var1 < var3 || var1 > var5) {
               return DataResult.error(() -> "Initial value " + var1 + " is outside of range [" + var3 + ", " + var5 + "]");
            }
         }

         return DataResult.success(var0);
      });

      public RangeInfo(double var1, double var3, Optional<Double> var5, int var6) {
         super();
         this.start = var1;
         this.end = var3;
         this.initial = var5;
         this.steps = var6;
      }

      public double roundToNearestStep(double var1) {
         return Math.floor(var1 * (double)this.steps) / (double)this.steps;
      }

      public double computeScaledValue(double var1) {
         double var3 = this.roundToNearestStep(var1);
         return Mth.lerp(var3, this.start, this.end);
      }

      public double initialSliderValue() {
         double var1 = Mth.inverseLerp((Double)this.initial.orElse(this.start), this.start, this.end);
         return this.roundToNearestStep(var1);
      }
   }
}
