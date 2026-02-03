package net.minecraft.server.dialog.input;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.server.dialog.Dialog;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;

public record NumberRangeInput(int width, Component label, String labelFormat, RangeInfo rangeInfo) implements InputControl {
   public static final MapCodec<NumberRangeInput> MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(Dialog.WIDTH_CODEC.optionalFieldOf("width", 200).forGetter(NumberRangeInput::width), ComponentSerialization.CODEC.fieldOf("label").forGetter(NumberRangeInput::label), Codec.STRING.optionalFieldOf("label_format", "options.generic_value").forGetter(NumberRangeInput::labelFormat), NumberRangeInput.RangeInfo.MAP_CODEC.forGetter(NumberRangeInput::rangeInfo)).apply(i, NumberRangeInput::new));

   public NumberRangeInput {
      super();
   }

   public MapCodec<NumberRangeInput> mapCodec() {
      return MAP_CODEC;
   }

   public Component computeLabel(final String value) {
      return Component.translatable(this.labelFormat, this.label, value);
   }

   public static record RangeInfo(float start, float end, Optional<Float> initial, Optional<Float> step) {
      public static final MapCodec<RangeInfo> MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(Codec.FLOAT.fieldOf("start").forGetter(RangeInfo::start), Codec.FLOAT.fieldOf("end").forGetter(RangeInfo::end), Codec.FLOAT.optionalFieldOf("initial").forGetter(RangeInfo::initial), ExtraCodecs.POSITIVE_FLOAT.optionalFieldOf("step").forGetter(RangeInfo::step)).apply(i, RangeInfo::new)).validate((range) -> {
         if (range.initial.isPresent()) {
            double initial = (double)(Float)range.initial.get();
            double min = (double)Math.min(range.start, range.end);
            double max = (double)Math.max(range.start, range.end);
            if (initial < min || initial > max) {
               return DataResult.error(() -> "Initial value " + initial + " is outside of range [" + min + ", " + max + "]");
            }
         }

         return DataResult.success(range);
      });

      public RangeInfo {
         super();
      }

      public float computeScaledValue(final float sliderValue) {
         float valueInRange = Mth.lerp(sliderValue, this.start, this.end);
         if (this.step.isEmpty()) {
            return valueInRange;
         } else {
            float step = (Float)this.step.get();
            float initialValue = this.initialScaledValue();
            float deltaToInitial = valueInRange - initialValue;
            int stepsOutsideInitial = Math.round(deltaToInitial / step);
            float result = initialValue + (float)stepsOutsideInitial * step;
            if (!this.isOutOfRange(result)) {
               return result;
            } else {
               int oneStepLess = stepsOutsideInitial - Mth.sign((double)stepsOutsideInitial);
               return initialValue + (float)oneStepLess * step;
            }
         }
      }

      private boolean isOutOfRange(final float scaledValue) {
         float sliderPos = this.scaledValueToSlider(scaledValue);
         return (double)sliderPos < 0.0 || (double)sliderPos > 1.0;
      }

      private float initialScaledValue() {
         return this.initial.isPresent() ? (Float)this.initial.get() : (this.start + this.end) / 2.0F;
      }

      public float initialSliderValue() {
         float value = this.initialScaledValue();
         return this.scaledValueToSlider(value);
      }

      private float scaledValueToSlider(final float value) {
         return this.start == this.end ? 0.5F : Mth.inverseLerp(value, this.start, this.end);
      }
   }
}
