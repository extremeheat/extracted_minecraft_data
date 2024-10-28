package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Iterator;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.ReplaceSphereConfiguration;

public class ReplaceBlobsFeature extends Feature<ReplaceSphereConfiguration> {
   public ReplaceBlobsFeature(Codec<ReplaceSphereConfiguration> var1) {
      super(var1);
   }

   public boolean place(FeaturePlaceContext<ReplaceSphereConfiguration> var1) {
      ReplaceSphereConfiguration var2 = (ReplaceSphereConfiguration)var1.config();
      WorldGenLevel var3 = var1.level();
      RandomSource var4 = var1.random();
      Block var5 = var2.targetState.getBlock();
      BlockPos var6 = findTarget(var3, var1.origin().mutable().clamp(Direction.Axis.Y, var3.getMinBuildHeight() + 1, var3.getMaxBuildHeight() - 1), var5);
      if (var6 == null) {
         return false;
      } else {
         int var7 = var2.radius().sample(var4);
         int var8 = var2.radius().sample(var4);
         int var9 = var2.radius().sample(var4);
         int var10 = Math.max(var7, Math.max(var8, var9));
         boolean var11 = false;
         Iterator var12 = BlockPos.withinManhattan(var6, var7, var8, var9).iterator();

         while(var12.hasNext()) {
            BlockPos var13 = (BlockPos)var12.next();
            if (var13.distManhattan(var6) > var10) {
               break;
            }

            BlockState var14 = var3.getBlockState(var13);
            if (var14.is(var5)) {
               this.setBlock(var3, var13, var2.replaceState);
               var11 = true;
            }
         }

         return var11;
      }
   }

   @Nullable
   private static BlockPos findTarget(LevelAccessor var0, BlockPos.MutableBlockPos var1, Block var2) {
      while(var1.getY() > var0.getMinBuildHeight() + 1) {
         BlockState var3 = var0.getBlockState(var1);
         if (var3.is(var2)) {
            return var1;
         }

         var1.move(Direction.DOWN);
      }

      return null;
   }
}
