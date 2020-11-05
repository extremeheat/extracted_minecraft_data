package net.minecraft.world.level.levelgen.feature;

import java.util.Iterator;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.WallTorchBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class EndPodiumFeature extends Feature<NoneFeatureConfiguration> {
   public static final BlockPos END_PODIUM_LOCATION;
   private final boolean active;

   public EndPodiumFeature(boolean var1) {
      super(NoneFeatureConfiguration.CODEC);
      this.active = var1;
   }

   public boolean place(WorldGenLevel var1, ChunkGenerator var2, Random var3, BlockPos var4, NoneFeatureConfiguration var5) {
      Iterator var6 = BlockPos.betweenClosed(new BlockPos(var4.getX() - 4, var4.getY() - 1, var4.getZ() - 4), new BlockPos(var4.getX() + 4, var4.getY() + 32, var4.getZ() + 4)).iterator();

      while(true) {
         BlockPos var7;
         boolean var8;
         do {
            if (!var6.hasNext()) {
               for(int var9 = 0; var9 < 4; ++var9) {
                  this.setBlock(var1, var4.above(var9), Blocks.BEDROCK.defaultBlockState());
               }

               BlockPos var10 = var4.above(2);
               Iterator var11 = Direction.Plane.HORIZONTAL.iterator();

               while(var11.hasNext()) {
                  Direction var12 = (Direction)var11.next();
                  this.setBlock(var1, var10.relative(var12), (BlockState)Blocks.WALL_TORCH.defaultBlockState().setValue(WallTorchBlock.FACING, var12));
               }

               return true;
            }

            var7 = (BlockPos)var6.next();
            var8 = var7.closerThan(var4, 2.5D);
         } while(!var8 && !var7.closerThan(var4, 3.5D));

         if (var7.getY() < var4.getY()) {
            if (var8) {
               this.setBlock(var1, var7, Blocks.BEDROCK.defaultBlockState());
            } else if (var7.getY() < var4.getY()) {
               this.setBlock(var1, var7, Blocks.END_STONE.defaultBlockState());
            }
         } else if (var7.getY() > var4.getY()) {
            this.setBlock(var1, var7, Blocks.AIR.defaultBlockState());
         } else if (!var8) {
            this.setBlock(var1, var7, Blocks.BEDROCK.defaultBlockState());
         } else if (this.active) {
            this.setBlock(var1, new BlockPos(var7), Blocks.END_PORTAL.defaultBlockState());
         } else {
            this.setBlock(var1, new BlockPos(var7), Blocks.AIR.defaultBlockState());
         }
      }
   }

   static {
      END_PODIUM_LOCATION = BlockPos.ZERO;
   }
}
