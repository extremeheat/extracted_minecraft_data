package net.minecraft.world.attribute.modifier;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record FloatWithAlpha(float value, float alpha) {
   private static final Codec<FloatWithAlpha> FULL_CODEC = RecordCodecBuilder.create((var0) -> var0.group(Codec.FLOAT.fieldOf("value").forGetter(FloatWithAlpha::value), Codec.floatRange(0.0F, 1.0F).optionalFieldOf("alpha", 1.0F).forGetter(FloatWithAlpha::alpha)).apply(var0, FloatWithAlpha::new));
   public static final Codec<FloatWithAlpha> CODEC;

   public FloatWithAlpha(float var1) {
      this(var1, 1.0F);
   }

   public FloatWithAlpha(float var1, float var2) {
      super();
      this.value = var1;
      this.alpha = var2;
   }

   static {
      CODEC = Codec.either(Codec.FLOAT, FULL_CODEC).xmap((var0) -> (FloatWithAlpha)var0.map(FloatWithAlpha::new, (var0x) -> var0x), (var0) -> var0.alpha() == 1.0F ? Either.left(var0.value()) : Either.right(var0));
   }
}
