package net.minecraft.util.valueproviders;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Iterator;
import java.util.List;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.util.random.WeightedEntry;

public class WeightedListInt extends IntProvider {
   public static final MapCodec<WeightedListInt> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return var0.group(SimpleWeightedRandomList.wrappedCodec(IntProvider.CODEC).fieldOf("distribution").forGetter((var0x) -> {
         return var0x.distribution;
      })).apply(var0, WeightedListInt::new);
   });
   private final SimpleWeightedRandomList<IntProvider> distribution;
   private final int minValue;
   private final int maxValue;

   public WeightedListInt(SimpleWeightedRandomList<IntProvider> var1) {
      super();
      this.distribution = var1;
      List var2 = var1.unwrap();
      int var3 = 2147483647;
      int var4 = -2147483648;

      int var8;
      for(Iterator var5 = var2.iterator(); var5.hasNext(); var4 = Math.max(var4, var8)) {
         WeightedEntry.Wrapper var6 = (WeightedEntry.Wrapper)var5.next();
         int var7 = ((IntProvider)var6.data()).getMinValue();
         var8 = ((IntProvider)var6.data()).getMaxValue();
         var3 = Math.min(var3, var7);
      }

      this.minValue = var3;
      this.maxValue = var4;
   }

   public int sample(RandomSource var1) {
      return ((IntProvider)this.distribution.getRandomValue(var1).orElseThrow(IllegalStateException::new)).sample(var1);
   }

   public int getMinValue() {
      return this.minValue;
   }

   public int getMaxValue() {
      return this.maxValue;
   }

   public IntProviderType<?> getType() {
      return IntProviderType.WEIGHTED_LIST;
   }
}
