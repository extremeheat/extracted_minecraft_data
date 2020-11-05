package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Iterator;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.ReplaceSphereConfiguration;

public class ReplaceBlobsFeature extends Feature<ReplaceSphereConfiguration> {
   public ReplaceBlobsFeature(Codec<ReplaceSphereConfiguration> var1) {
      super(var1);
   }

   public boolean place(WorldGenLevel var1, ChunkGenerator var2, Random var3, BlockPos var4, ReplaceSphereConfiguration var5) {
      Block var6 = var5.targetState.getBlock();
      BlockPos var7 = findTarget(var1, var4.mutable().clamp(Direction.Axis.Y, var1.getMinBuildHeight() + 1, var1.getMaxBuildHeight() - 1), var6);
      if (var7 == null) {
         return false;
      } else {
         int var8 = var5.radius().sample(var3);
         int var9 = var5.radius().sample(var3);
         int var10 = var5.radius().sample(var3);
         int var11 = Math.max(var8, Math.max(var9, var10));
         boolean var12 = false;
         Iterator var13 = BlockPos.withinManhattan(var7, var8, var9, var10).iterator();

         while(var13.hasNext()) {
            BlockPos var14 = (BlockPos)var13.next();
            if (var14.distManhattan(var7) > var11) {
               break;
            }

            BlockState var15 = var1.getBlockState(var14);
            if (var15.is(var6)) {
               this.setBlock(var1, var14, var5.replaceState);
               var12 = true;
            }
         }

         return var12;
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
