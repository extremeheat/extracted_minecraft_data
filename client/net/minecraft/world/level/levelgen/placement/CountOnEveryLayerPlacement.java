package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.stream.Stream;
import java.util.stream.Stream.Builder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;

@Deprecated
public class CountOnEveryLayerPlacement extends PlacementModifier {
   public static final Codec<CountOnEveryLayerPlacement> CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(
               IntProvider.codec(0, 256).fieldOf("count").forGetter(var0x -> var0x.count),
               Codec.INT.fieldOf("start_offset").forGetter(var0x -> var0x.start_offset)
            )
            .apply(var0, CountOnEveryLayerPlacement::new)
   );
   private final IntProvider count;
   private final int start_offset;

   private CountOnEveryLayerPlacement(IntProvider var1, int var2) {
      super();
      this.start_offset = var2;
      this.count = var1;
   }

   public static CountOnEveryLayerPlacement of(IntProvider var0) {
      return new CountOnEveryLayerPlacement(var0, 0);
   }

   public static CountOnEveryLayerPlacement of(int var0) {
      return of(ConstantInt.of(var0));
   }

   public static CountOnEveryLayerPlacement of(int var0, int var1) {
      return new CountOnEveryLayerPlacement(ConstantInt.of(var0), var1);
   }

   @Override
   public Stream<BlockPos> getPositions(PlacementContext var1, RandomSource var2, BlockPos var3) {
      Builder var4 = Stream.builder();
      int var6 = 0;

      boolean var5;
      do {
         var5 = false;

         for(int var7 = 0; var7 < this.count.sample(var2); ++var7) {
            int var8 = var2.nextInt(16) + var3.getX();
            int var9 = var2.nextInt(16) + var3.getZ();
            int var10 = var1.getHeight(Heightmap.Types.MOTION_BLOCKING, var8, var9) + this.start_offset;
            int var11 = findOnGroundYPosition(var1, var8, var10, var9, var6);
            if (var11 != 2147483647) {
               var4.add(new BlockPos(var8, var11, var9));
               var5 = true;
            }
         }

         ++var6;
      } while(var5);

      return var4.build();
   }

   @Override
   public PlacementModifierType<?> type() {
      return PlacementModifierType.COUNT_ON_EVERY_LAYER;
   }

   private static int findOnGroundYPosition(PlacementContext var0, int var1, int var2, int var3, int var4) {
      BlockPos.MutableBlockPos var5 = new BlockPos.MutableBlockPos(var1, var2, var3);
      int var6 = 0;
      BlockState var7 = var0.getBlockState(var5);

      for(int var8 = var2; var8 >= var0.getMinBuildHeight() + 1; --var8) {
         var5.setY(var8 - 1);
         BlockState var9 = var0.getBlockState(var5);
         if (!isEmpty(var9) && isEmpty(var7) && !var9.is(Blocks.BEDROCK)) {
            if (var6 == var4) {
               return var5.getY() + 1;
            }

            ++var6;
         }

         var7 = var9;
      }

      return 2147483647;
   }

   private static boolean isEmpty(BlockState var0) {
      return var0.isAir() || var0.is(Blocks.WATER) || var0.is(Blocks.LAVA);
   }
}
