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
      BlockPos var7 = findTarget(var1, var4.mutable().clamp(Direction.Axis.Y, 1, var1.getMaxBuildHeight() - 1), var6);
      if (var7 == null) {
         return false;
      } else {
         int var8 = var5.radius().sample(var3);
         boolean var9 = false;
         Iterator var10 = BlockPos.withinManhattan(var7, var8, var8, var8).iterator();

         while(var10.hasNext()) {
            BlockPos var11 = (BlockPos)var10.next();
            if (var11.distManhattan(var7) > var8) {
               break;
            }

            BlockState var12 = var1.getBlockState(var11);
            if (var12.is(var6)) {
               this.setBlock(var1, var11, var5.replaceState);
               var9 = true;
            }
         }

         return var9;
      }
   }

   @Nullable
   private static BlockPos findTarget(LevelAccessor var0, BlockPos.MutableBlockPos var1, Block var2) {
      while(var1.getY() > 1) {
         BlockState var3 = var0.getBlockState(var1);
         if (var3.is(var2)) {
            return var1;
         }

         var1.move(Direction.DOWN);
      }

      return null;
   }
}
