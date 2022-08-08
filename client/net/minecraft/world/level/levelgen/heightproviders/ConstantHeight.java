package net.minecraft.world.level.levelgen.heightproviders;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.WorldGenerationContext;

public class ConstantHeight extends HeightProvider {
   public static final ConstantHeight ZERO = new ConstantHeight(VerticalAnchor.absolute(0));
   public static final Codec<ConstantHeight> CODEC;
   private final VerticalAnchor value;

   public static ConstantHeight of(VerticalAnchor var0) {
      return new ConstantHeight(var0);
   }

   private ConstantHeight(VerticalAnchor var1) {
      super();
      this.value = var1;
   }

   public VerticalAnchor getValue() {
      return this.value;
   }

   public int sample(RandomSource var1, WorldGenerationContext var2) {
      return this.value.resolveY(var2);
   }

   public HeightProviderType<?> getType() {
      return HeightProviderType.CONSTANT;
   }

   public String toString() {
      return this.value.toString();
   }

   static {
      CODEC = Codec.either(VerticalAnchor.CODEC, RecordCodecBuilder.create((var0) -> {
         return var0.group(VerticalAnchor.CODEC.fieldOf("value").forGetter((var0x) -> {
            return var0x.value;
         })).apply(var0, ConstantHeight::new);
      })).xmap((var0) -> {
         return (ConstantHeight)var0.map(ConstantHeight::of, (var0x) -> {
            return var0x;
         });
      }, (var0) -> {
         return Either.left(var0.value);
      });
   }
}
