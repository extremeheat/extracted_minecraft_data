package net.minecraft.world.level.levelgen.feature.treedecorators;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelSimulatedRW;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class AlterGroundDecorator extends TreeDecorator {
   public static final Codec<AlterGroundDecorator> CODEC;
   private final BlockStateProvider provider;

   public AlterGroundDecorator(BlockStateProvider var1) {
      super();
      this.provider = var1;
   }

   protected TreeDecoratorType<?> type() {
      return TreeDecoratorType.ALTER_GROUND;
   }

   public void place(WorldGenLevel var1, Random var2, List<BlockPos> var3, List<BlockPos> var4, Set<BlockPos> var5, BoundingBox var6) {
      int var7 = ((BlockPos)var3.get(0)).getY();
      var3.stream().filter((var1x) -> {
         return var1x.getY() == var7;
      }).forEach((var3x) -> {
         this.placeCircle(var1, var2, var3x.west().north());
         this.placeCircle(var1, var2, var3x.east(2).north());
         this.placeCircle(var1, var2, var3x.west().south(2));
         this.placeCircle(var1, var2, var3x.east(2).south(2));

         for(int var4 = 0; var4 < 5; ++var4) {
            int var5 = var2.nextInt(64);
            int var6 = var5 % 8;
            int var7 = var5 / 8;
            if (var6 == 0 || var6 == 7 || var7 == 0 || var7 == 7) {
               this.placeCircle(var1, var2, var3x.offset(-3 + var6, 0, -3 + var7));
            }
         }

      });
   }

   private void placeCircle(LevelSimulatedRW var1, Random var2, BlockPos var3) {
      for(int var4 = -2; var4 <= 2; ++var4) {
         for(int var5 = -2; var5 <= 2; ++var5) {
            if (Math.abs(var4) != 2 || Math.abs(var5) != 2) {
               this.placeBlockAt(var1, var2, var3.offset(var4, 0, var5));
            }
         }
      }

   }

   private void placeBlockAt(LevelSimulatedRW var1, Random var2, BlockPos var3) {
      for(int var4 = 2; var4 >= -3; --var4) {
         BlockPos var5 = var3.above(var4);
         if (Feature.isGrassOrDirt(var1, var5)) {
            var1.setBlock(var5, this.provider.getState(var2, var3), 19);
            break;
         }

         if (!Feature.isAir(var1, var5) && var4 < 0) {
            break;
         }
      }

   }

   static {
      CODEC = BlockStateProvider.CODEC.fieldOf("provider").xmap(AlterGroundDecorator::new, (var0) -> {
         return var0.provider;
      }).codec();
   }
}
