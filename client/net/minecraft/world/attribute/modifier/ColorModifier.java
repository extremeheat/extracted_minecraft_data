package net.minecraft.world.attribute.modifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ARGB;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import net.minecraft.world.attribute.EnvironmentAttribute;
import net.minecraft.world.attribute.LerpFunction;
import org.joml.Vector3fc;
import org.joml.Vector4fc;

public interface ColorModifier<Subject, Argument> extends AttributeModifier<Subject, Argument> {
   ArgbModifier<Vector3fc> ALPHA_BLEND_RGB = ARGB::alphaBlend;
   ArgbModifier<Vector4fc> ALPHA_BLEND_ARGB = ARGB::alphaBlend;
   RgbModifier<Vector3fc> ADD_RGB = ARGB::addRgb;
   RgbModifier<Vector3fc> SUBTRACT_RGB = ARGB::subtractRgb;
   RgbModifier<Vector3fc> MULTIPLY_RGB = ARGB::multiply;
   RgbModifier<Vector4fc> ADD_ARGB = ARGB::addRgb;
   RgbModifier<Vector4fc> SUBTRACT_ARGB = ARGB::subtractRgb;
   ArgbModifier<Vector4fc> MULTIPLY_ARGB = ARGB::multiply;
   ColorModifier<Vector3fc, BlendToGray> BLEND_TO_GRAY_RGB = new ColorModifier<Vector3fc, BlendToGray>() {
      public Vector3fc apply(final Vector3fc subject, final BlendToGray argument) {
         Vector3fc multipliedGreyscale = ARGB.scaleRGB(ARGB.greyscale(subject), argument.brightness);
         return ARGB.srgbLerp(argument.factor, subject, multipliedGreyscale);
      }

      public Codec<BlendToGray> argumentCodec(final EnvironmentAttribute<Vector3fc> type) {
         return ColorModifier.BlendToGray.CODEC;
      }

      public LerpFunction<BlendToGray> argumentKeyframeLerp(final EnvironmentAttribute<Vector3fc> type) {
         return BlendToGray::lerp;
      }
   };
   ColorModifier<Vector4fc, BlendToGray> BLEND_TO_GRAY_ARGB = new ColorModifier<Vector4fc, BlendToGray>() {
      public Vector4fc apply(final Vector4fc subject, final BlendToGray argument) {
         Vector4fc multipliedGreyscale = ARGB.scaleRGB(ARGB.greyscale(subject), argument.brightness);
         return ARGB.srgbLerp(argument.factor, subject, multipliedGreyscale);
      }

      public Codec<BlendToGray> argumentCodec(final EnvironmentAttribute<Vector4fc> type) {
         return ColorModifier.BlendToGray.CODEC;
      }

      public LerpFunction<BlendToGray> argumentKeyframeLerp(final EnvironmentAttribute<Vector4fc> type) {
         return BlendToGray::lerp;
      }
   };

   public interface RgbModifier<Subject> extends ColorModifier<Subject, Vector3fc> {
      default Codec<Vector3fc> argumentCodec(final EnvironmentAttribute<Subject> type) {
         return ExtraCodecs.STRING_RGB_VEC3_COLOR;
      }

      default LerpFunction<Vector3fc> argumentKeyframeLerp(final EnvironmentAttribute<Subject> type) {
         return LerpFunction.ofColorVec3();
      }
   }

   @FunctionalInterface
   public interface ArgbModifier<Subject> extends ColorModifier<Subject, Vector4fc> {
      default Codec<Vector4fc> argumentCodec(final EnvironmentAttribute<Subject> type) {
         return ExtraCodecs.STRING_ARGB_VEC4_COLOR;
      }

      default LerpFunction<Vector4fc> argumentKeyframeLerp(final EnvironmentAttribute<Subject> type) {
         return LerpFunction.ofColorVec4();
      }
   }

   public static record BlendToGray(float brightness, float factor) {
      public static final Codec<BlendToGray> CODEC = RecordCodecBuilder.create((i) -> i.group(Codec.floatRange(0.0F, 1.0F).fieldOf("brightness").forGetter(BlendToGray::brightness), Codec.floatRange(0.0F, 1.0F).fieldOf("factor").forGetter(BlendToGray::factor)).apply(i, BlendToGray::new));

      public BlendToGray {
         super();
      }

      public static BlendToGray lerp(final float alpha, final BlendToGray from, final BlendToGray to) {
         return new BlendToGray(Mth.lerp(alpha, from.brightness, to.brightness), Mth.lerp(alpha, from.factor, to.factor));
      }
   }
}
