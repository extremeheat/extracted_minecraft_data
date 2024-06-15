package net.minecraft.world.level.levelgen.heightproviders;

import com.mojang.serialization.MapCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.WorldGenerationContext;

public class ConstantHeight extends HeightProvider {
   public static final ConstantHeight ZERO = new ConstantHeight(VerticalAnchor.absolute(0));
   public static final MapCodec<ConstantHeight> CODEC = VerticalAnchor.CODEC.fieldOf("value").xmap(ConstantHeight::new, ConstantHeight::getValue);
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
