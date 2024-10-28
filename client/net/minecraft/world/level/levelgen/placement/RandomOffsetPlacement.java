package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.IntProvider;

public class RandomOffsetPlacement extends PlacementModifier {
   public static final MapCodec<RandomOffsetPlacement> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return var0.group(IntProvider.codec(-16, 16).fieldOf("xz_spread").forGetter((var0x) -> {
         return var0x.xzSpread;
      }), IntProvider.codec(-16, 16).fieldOf("y_spread").forGetter((var0x) -> {
         return var0x.ySpread;
      })).apply(var0, RandomOffsetPlacement::new);
   });
   private final IntProvider xzSpread;
   private final IntProvider ySpread;

   public static RandomOffsetPlacement of(IntProvider var0, IntProvider var1) {
      return new RandomOffsetPlacement(var0, var1);
   }

   public static RandomOffsetPlacement vertical(IntProvider var0) {
      return new RandomOffsetPlacement(ConstantInt.of(0), var0);
   }

   public static RandomOffsetPlacement horizontal(IntProvider var0) {
      return new RandomOffsetPlacement(var0, ConstantInt.of(0));
   }

   private RandomOffsetPlacement(IntProvider var1, IntProvider var2) {
      super();
      this.xzSpread = var1;
      this.ySpread = var2;
   }

   public Stream<BlockPos> getPositions(PlacementContext var1, RandomSource var2, BlockPos var3) {
      int var4 = var3.getX() + this.xzSpread.sample(var2);
      int var5 = var3.getY() + this.ySpread.sample(var2);
      int var6 = var3.getZ() + this.xzSpread.sample(var2);
      return Stream.of(new BlockPos(var4, var5, var6));
   }

   public PlacementModifierType<?> type() {
      return PlacementModifierType.RANDOM_OFFSET;
   }
}
