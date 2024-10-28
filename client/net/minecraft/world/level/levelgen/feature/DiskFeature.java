package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Iterator;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.DiskConfiguration;

public class DiskFeature extends Feature<DiskConfiguration> {
   public DiskFeature(Codec<DiskConfiguration> var1) {
      super(var1);
   }

   public boolean place(FeaturePlaceContext<DiskConfiguration> var1) {
      DiskConfiguration var2 = (DiskConfiguration)var1.config();
      BlockPos var3 = var1.origin();
      WorldGenLevel var4 = var1.level();
      RandomSource var5 = var1.random();
      boolean var6 = false;
      int var7 = var3.getY();
      int var8 = var7 + var2.halfHeight();
      int var9 = var7 - var2.halfHeight() - 1;
      int var10 = var2.radius().sample(var5);
      BlockPos.MutableBlockPos var11 = new BlockPos.MutableBlockPos();
      Iterator var12 = BlockPos.betweenClosed(var3.offset(-var10, 0, -var10), var3.offset(var10, 0, var10)).iterator();

      while(var12.hasNext()) {
         BlockPos var13 = (BlockPos)var12.next();
         int var14 = var13.getX() - var3.getX();
         int var15 = var13.getZ() - var3.getZ();
         if (var14 * var14 + var15 * var15 <= var10 * var10) {
            var6 |= this.placeColumn(var2, var4, var5, var8, var9, var11.set(var13));
         }
      }

      return var6;
   }

   protected boolean placeColumn(DiskConfiguration var1, WorldGenLevel var2, RandomSource var3, int var4, int var5, BlockPos.MutableBlockPos var6) {
      boolean var7 = false;

      for(int var8 = var4; var8 > var5; --var8) {
         var6.setY(var8);
         if (var1.target().test(var2, var6)) {
            BlockState var9 = var1.stateProvider().getState(var2, var3, var6);
            var2.setBlock(var6, var9, 2);
            this.markAboveForPostProcessing(var2, var6);
            var7 = true;
         }
      }

      return var7;
   }
}
