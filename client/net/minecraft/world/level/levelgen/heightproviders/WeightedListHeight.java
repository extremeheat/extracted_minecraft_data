package net.minecraft.world.level.levelgen.heightproviders;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.level.levelgen.WorldGenerationContext;

public class WeightedListHeight extends HeightProvider {
   public static final MapCodec<WeightedListHeight> CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(WeightedList.nonEmptyCodec(HeightProvider.CODEC).fieldOf("distribution").forGetter((var0x) -> var0x.distribution)).apply(var0, WeightedListHeight::new));
   private final WeightedList<HeightProvider> distribution;

   public WeightedListHeight(WeightedList<HeightProvider> var1) {
      super();
      this.distribution = var1;
   }

   public int sample(RandomSource var1, WorldGenerationContext var2) {
      return ((HeightProvider)this.distribution.getRandomOrThrow(var1)).sample(var1, var2);
   }

   public HeightProviderType<?> getType() {
      return HeightProviderType.WEIGHTED_LIST;
   }
}
