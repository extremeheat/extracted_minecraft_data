package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;

public class NoSurfaceOreFeature extends Feature<OreConfiguration> {
   NoSurfaceOreFeature(Codec<OreConfiguration> var1) {
      super(var1);
   }

   public boolean place(WorldGenLevel var1, ChunkGenerator var2, Random var3, BlockPos var4, OreConfiguration var5) {
      int var6 = var3.nextInt(var5.size + 1);
      BlockPos.MutableBlockPos var7 = new BlockPos.MutableBlockPos();

      for(int var8 = 0; var8 < var6; ++var8) {
         this.offsetTargetPos(var7, var3, var4, Math.min(var8, 7));
         if (var5.target.test(var1.getBlockState(var7), var3) && !this.isFacingAir(var1, var7)) {
            var1.setBlock(var7, var5.state, 2);
         }
      }

      return true;
   }

   private void offsetTargetPos(BlockPos.MutableBlockPos var1, Random var2, BlockPos var3, int var4) {
      int var5 = this.getRandomPlacementInOneAxisRelativeToOrigin(var2, var4);
      int var6 = this.getRandomPlacementInOneAxisRelativeToOrigin(var2, var4);
      int var7 = this.getRandomPlacementInOneAxisRelativeToOrigin(var2, var4);
      var1.setWithOffset(var3, var5, var6, var7);
   }

   private int getRandomPlacementInOneAxisRelativeToOrigin(Random var1, int var2) {
      return Math.round((var1.nextFloat() - var1.nextFloat()) * (float)var2);
   }

   private boolean isFacingAir(LevelAccessor var1, BlockPos var2) {
      BlockPos.MutableBlockPos var3 = new BlockPos.MutableBlockPos();
      Direction[] var4 = Direction.values();
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         Direction var7 = var4[var6];
         var3.setWithOffset(var2, var7);
         if (var1.getBlockState(var3).isAir()) {
            return true;
         }
      }

      return false;
   }
}
