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
   public static final MapCodec<NumberRangeInput> MAP_CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(Dialog.WIDTH_CODEC.optionalFieldOf("width", 200).forGetter(NumberRangeInput::width), ComponentSerialization.CODEC.fieldOf("label").forGetter(NumberRangeInput::label), Codec.STRING.optionalFieldOf("label_format", "options.generic_value").forGetter(NumberRangeInput::labelFormat), NumberRangeInput.RangeInfo.MAP_CODEC.forGetter(NumberRangeInput::rangeInfo)).apply(var0, NumberRangeInput::new));

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

   public static record RangeInfo(float start, float end, Optional<Float> initial, Optional<Float> step) {
      public static final MapCodec<RangeInfo> MAP_CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(Codec.FLOAT.fieldOf("start").forGetter(RangeInfo::start), Codec.FLOAT.fieldOf("end").forGetter(RangeInfo::end), Codec.FLOAT.optionalFieldOf("initial").forGetter(RangeInfo::initial), ExtraCodecs.POSITIVE_FLOAT.optionalFieldOf("step").forGetter(RangeInfo::step)).apply(var0, RangeInfo::new)).validate((var0) -> {
         if (var0.initial.isPresent()) {
            double var1 = (double)(Float)var0.initial.get();
            double var3 = (double)Math.min(var0.start, var0.end);
            double var5 = (double)Math.max(var0.start, var0.end);
            if (var1 < var3 || var1 > var5) {
               return DataResult.error(() -> "Initial value " + var1 + " is outside of range [" + var3 + ", " + var5 + "]");
            }
         }

         return DataResult.success(var0);
      });

      public RangeInfo(float var1, float var2, Optional<Float> var3, Optional<Float> var4) {
         super();
         this.start = var1;
         this.end = var2;
         this.initial = var3;
         this.step = var4;
      }

      public float computeScaledValue(float var1) {
         float var2 = Mth.lerp(var1, this.start, this.end);
         if (this.step.isEmpty()) {
            return var2;
         } else {
            float var3 = (Float)this.step.get();
            float var4 = this.initialScaledValue();
            float var5 = var2 - var4;
            int var6 = Math.round(var5 / var3);
            float var7 = var4 + (float)var6 * var3;
            if (!this.isOutOfRange(var7)) {
               return var7;
            } else {
               int var8 = var6 - Mth.sign((double)var6);
               return var4 + (float)var8 * var3;
            }
         }
      }

      private boolean isOutOfRange(float var1) {
         float var2 = this.scaledValueToSlider(var1);
         return (double)var2 < 0.0 || (double)var2 > 1.0;
      }

      private float initialScaledValue() {
         return this.initial.isPresent() ? (Float)this.initial.get() : (this.start + this.end) / 2.0F;
      }

      public float initialSliderValue() {
         float var1 = this.initialScaledValue();
         return this.scaledValueToSlider(var1);
      }

      private float scaledValueToSlider(float var1) {
         return this.start == this.end ? 0.5F : Mth.inverseLerp(var1, this.start, this.end);
      }
   }
}
