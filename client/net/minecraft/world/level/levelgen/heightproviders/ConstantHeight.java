package net.minecraft.world.level.levelgen.heightproviders;

import com.mojang.serialization.MapCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.VerticalAnchor;

public class ConstantHeight extends HeightProvider {
   public static final ConstantHeight ZERO = new ConstantHeight(VerticalAnchor.absolute(0));
   public static final MapCodec<ConstantHeight> CODEC;
   private final VerticalAnchor value;

   public static ConstantHeight of(final VerticalAnchor value) {
      return new ConstantHeight(value);
   }

   private ConstantHeight(final VerticalAnchor value) {
      super();
      this.value = value;
   }

   public VerticalAnchor getValue() {
      return this.value;
   }

   public int sample(final RandomSource random, final VerticalAnchor.Context anchorContext) {
      return this.value.resolveY(anchorContext);
   }

   public HeightProviderType<?> getType() {
      return HeightProviderType.CONSTANT;
   }

   public String toString() {
      return this.value.toString();
   }

   static {
      CODEC = VerticalAnchor.CODEC.fieldOf("value").xmap(ConstantHeight::new, ConstantHeight::getValue);
   }
}
