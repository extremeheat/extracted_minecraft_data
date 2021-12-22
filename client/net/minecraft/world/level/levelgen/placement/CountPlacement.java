package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPos;
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

   // $FF: renamed from: of (net.minecraft.util.valueproviders.IntProvider) net.minecraft.world.level.levelgen.placement.CountPlacement
   public static CountPlacement method_38(IntProvider var0) {
      return new CountPlacement(var0);
   }

   // $FF: renamed from: of (int) net.minecraft.world.level.levelgen.placement.CountPlacement
   public static CountPlacement method_39(int var0) {
      return method_38(ConstantInt.method_49(var0));
   }

   protected int count(Random var1, BlockPos var2) {
      return this.count.sample(var1);
   }

   public PlacementModifierType<?> type() {
      return PlacementModifierType.COUNT;
   }
}
