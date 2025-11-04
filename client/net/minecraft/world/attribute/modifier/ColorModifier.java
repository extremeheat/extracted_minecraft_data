package net.minecraft.world.attribute.modifier;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ARGB;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import net.minecraft.world.attribute.EnvironmentAttribute;
import net.minecraft.world.attribute.LerpFunction;

public interface ColorModifier<Argument> extends AttributeModifier<Integer, Argument> {
   ColorModifier<Integer> ALPHA_BLEND = new ColorModifier<Integer>() {
      public Integer apply(Integer var1, Integer var2) {
         return ARGB.alphaBlend(var1, var2);
      }

      public Codec<Integer> argumentCodec(EnvironmentAttribute<Integer> var1) {
         return ExtraCodecs.STRING_ARGB_COLOR;
      }

      public LerpFunction<Integer> argumentKeyframeLerp(EnvironmentAttribute<Integer> var1) {
         return LerpFunction.ofColor();
      }

      // $FF: synthetic method
      public Object apply(final Object var1, final Object var2) {
         return this.apply((Integer)var1, (Integer)var2);
      }
   };
   ColorModifier<Integer> ADD = ARGB::addRgb;
   ColorModifier<Integer> SUBTRACT = ARGB::subtractRgb;
   ColorModifier<Integer> MULTIPLY_RGB = ARGB::multiply;
   ColorModifier<Integer> MULTIPLY_ARGB = ARGB::multiply;
   ColorModifier<BlendToGray> BLEND_TO_GRAY = new ColorModifier<BlendToGray>() {
      public Integer apply(Integer var1, BlendToGray var2) {
         int var3 = ARGB.scaleRGB(ARGB.greyscale(var1), var2.brightness);
         return ARGB.srgbLerp(var2.factor, var1, var3);
      }

      public Codec<BlendToGray> argumentCodec(EnvironmentAttribute<Integer> var1) {
         return ColorModifier.BlendToGray.CODEC;
      }

      public LerpFunction<BlendToGray> argumentKeyframeLerp(EnvironmentAttribute<Integer> var1) {
         return (var0, var1x, var2) -> new BlendToGray(Mth.lerp(var0, var1x.brightness, var2.brightness), Mth.lerp(var0, var1x.factor, var2.factor));
      }

      // $FF: synthetic method
      public Object apply(final Object var1, final Object var2) {
         return this.apply((Integer)var1, (BlendToGray)var2);
      }
   };

   @FunctionalInterface
   public interface RgbModifier extends ColorModifier<Integer> {
      default Codec<Integer> argumentCodec(EnvironmentAttribute<Integer> var1) {
         return ExtraCodecs.STRING_RGB_COLOR;
      }

      default LerpFunction<Integer> argumentKeyframeLerp(EnvironmentAttribute<Integer> var1) {
         return LerpFunction.ofColor();
      }
   }

   @FunctionalInterface
   public interface ArgbModifier extends ColorModifier<Integer> {
      default Codec<Integer> argumentCodec(EnvironmentAttribute<Integer> var1) {
         return Codec.either(ExtraCodecs.STRING_ARGB_COLOR, ExtraCodecs.RGB_COLOR_CODEC).xmap(Either::unwrap, (var0) -> ARGB.alpha(var0) == 255 ? Either.right(var0) : Either.left(var0));
      }

      default LerpFunction<Integer> argumentKeyframeLerp(EnvironmentAttribute<Integer> var1) {
         return LerpFunction.ofColor();
      }
   }

   public static record BlendToGray(float brightness, float factor) {
      final float brightness;
      final float factor;
      public static final Codec<BlendToGray> CODEC = RecordCodecBuilder.create((var0) -> var0.group(Codec.floatRange(0.0F, 1.0F).fieldOf("brightness").forGetter(BlendToGray::brightness), Codec.floatRange(0.0F, 1.0F).fieldOf("factor").forGetter(BlendToGray::factor)).apply(var0, BlendToGray::new));

      public BlendToGray(float var1, float var2) {
         super();
         this.brightness = var1;
         this.factor = var2;
      }
   }
}
