package net.minecraft.world.level.levelgen.heightproviders;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.WorldGenerationContext;

public class ConstantHeight extends HeightProvider {
   public static final ConstantHeight ZERO = new ConstantHeight(VerticalAnchor.absolute(0));
   public static final Codec<ConstantHeight> CODEC = Codec.either(
         VerticalAnchor.CODEC,
         RecordCodecBuilder.create(var0 -> var0.group(VerticalAnchor.CODEC.fieldOf("value").forGetter(var0x -> var0x.value)).apply(var0, ConstantHeight::new))
      )
      .xmap(var0 -> (ConstantHeight)var0.map(ConstantHeight::of, var0x -> var0x), var0 -> Either.left(var0.value));
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

   @Override
   public int sample(RandomSource var1, WorldGenerationContext var2) {
      return this.value.resolveY(var2);
   }

   @Override
   public HeightProviderType<?> getType() {
      return HeightProviderType.CONSTANT;
   }

   @Override
   public String toString() {
      return this.value.toString();
   }
}
