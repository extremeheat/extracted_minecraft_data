package net.minecraft.util.valueproviders;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.List;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.util.random.WeightedEntry;

public class WeightedListInt extends IntProvider {
   public static final Codec<WeightedListInt> CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(SimpleWeightedRandomList.wrappedCodec(IntProvider.CODEC).fieldOf("distribution").forGetter(var0x -> var0x.distribution))
            .apply(var0, WeightedListInt::new)
   );
   private final SimpleWeightedRandomList<IntProvider> distribution;
   private final int minValue;
   private final int maxValue;

   public WeightedListInt(SimpleWeightedRandomList<IntProvider> var1) {
      super();
      this.distribution = var1;
      List var2 = var1.unwrap();
      int var3 = 2147483647;
      int var4 = -2147483648;

      for(WeightedEntry.Wrapper var6 : var2) {
         int var7 = ((IntProvider)var6.getData()).getMinValue();
         int var8 = ((IntProvider)var6.getData()).getMaxValue();
         var3 = Math.min(var3, var7);
         var4 = Math.max(var4, var8);
      }

      this.minValue = var3;
      this.maxValue = var4;
   }

   @Override
   public int sample(RandomSource var1) {
      return this.distribution.getRandomValue(var1).orElseThrow(IllegalStateException::new).sample(var1);
   }

   @Override
   public int getMinValue() {
      return this.minValue;
   }

   @Override
   public int getMaxValue() {
      return this.maxValue;
   }

   @Override
   public IntProviderType<?> getType() {
      return IntProviderType.WEIGHTED_LIST;
   }
}
