package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.IntProvider;

public class CountPlacement extends RepeatingPlacement {
   public static final Codec<CountPlacement> CODEC = IntProvider.codec(0, 256).fieldOf("count").xmap(CountPlacement::new, (var0) -> {
      return var0.count;
   }).codec();
   private final IntProvider count;

   private CountPlacement(IntProvider var1) {
      super();
      this.count = var1;
   }

   public static CountPlacement of(IntProvider var0) {
      return new CountPlacement(var0);
   }

   public static CountPlacement of(int var0) {
      return of(ConstantInt.of(var0));
   }

   protected int count(RandomSource var1, BlockPos var2) {
      return this.count.sample(var1);
   }

   public PlacementModifierType<?> type() {
      return PlacementModifierType.COUNT;
   }
}
