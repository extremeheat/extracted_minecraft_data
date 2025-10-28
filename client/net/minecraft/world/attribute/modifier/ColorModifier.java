package net.minecraft.world.attribute.modifier;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import net.minecraft.util.ARGB;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.attribute.EnvironmentAttribute;

public interface ColorModifier<Argument> extends AttributeModifier<Integer, Argument> {
   ColorModifier<Integer> ALPHA_BLEND = new ColorModifier<Integer>() {
      public Integer apply(Integer var1, Integer var2) {
         return ARGB.alphaBlend(var1, var2);
      }

      public Codec<Integer> argumentCodec(EnvironmentAttribute<Integer> var1) {
         return ExtraCodecs.STRING_ARGB_COLOR;
      }

      // $FF: synthetic method
      public Object apply(final Object var1, final Object var2) {
         return this.apply((Integer)var1, (Integer)var2);
      }
   };
   ColorModifier<Integer> ADD = ARGB::addRgb;
   ColorModifier<Integer> SUBTRACT = ARGB::subtractRgb;
   ColorModifier<Integer> MULTIPLY = ARGB::multiply;
   ColorModifier<Integer> MULTIPLY_ARGB = ARGB::multiply;

   @FunctionalInterface
   public interface RgbModifier extends ColorModifier<Integer> {
      default Codec<Integer> argumentCodec(EnvironmentAttribute<Integer> var1) {
         return ExtraCodecs.STRING_RGB_COLOR;
      }
   }

   @FunctionalInterface
   public interface ArgbModifier extends ColorModifier<Integer> {
      default Codec<Integer> argumentCodec(EnvironmentAttribute<Integer> var1) {
         return Codec.either(ExtraCodecs.STRING_ARGB_COLOR, ExtraCodecs.RGB_COLOR_CODEC).xmap(Either::unwrap, (var0) -> ARGB.alpha(var0) == 255 ? Either.right(var0) : Either.left(var0));
      }
   }
}
