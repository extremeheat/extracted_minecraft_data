package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Iterator;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.DiskConfiguration;

public class BaseDiskFeature extends Feature<DiskConfiguration> {
   public BaseDiskFeature(Codec<DiskConfiguration> var1) {
      super(var1);
   }

   public boolean place(FeaturePlaceContext<DiskConfiguration> var1) {
      DiskConfiguration var2 = (DiskConfiguration)var1.config();
      BlockPos var3 = var1.origin();
      WorldGenLevel var4 = var1.level();
      boolean var5 = false;
      int var6 = var3.getY();
      int var7 = var6 + var2.halfHeight();
      int var8 = var6 - var2.halfHeight() - 1;
      boolean var9 = var2.state().getBlock() instanceof FallingBlock;
      int var10 = var2.radius().sample(var1.random());

      for(int var11 = var3.getX() - var10; var11 <= var3.getX() + var10; ++var11) {
         for(int var12 = var3.getZ() - var10; var12 <= var3.getZ() + var10; ++var12) {
            int var13 = var11 - var3.getX();
            int var14 = var12 - var3.getZ();
            if (var13 * var13 + var14 * var14 <= var10 * var10) {
               boolean var15 = false;

               for(int var16 = var7; var16 >= var8; --var16) {
                  BlockPos var17 = new BlockPos(var11, var16, var12);
                  BlockState var18 = var4.getBlockState(var17);
                  Block var19 = var18.getBlock();
                  boolean var20 = false;
                  if (var16 > var8) {
                     Iterator var21 = var2.targets().iterator();

                     while(var21.hasNext()) {
                        BlockState var22 = (BlockState)var21.next();
                        if (var22.is(var19)) {
                           var4.setBlock(var17, var2.state(), 2);
                           this.markAboveForPostProcessing(var4, var17);
                           var5 = true;
                           var20 = true;
                           break;
                        }
                     }
                  }

                  if (var9 && var15 && var18.isAir()) {
                     BlockState var23 = var2.state().is(Blocks.RED_SAND) ? Blocks.RED_SANDSTONE.defaultBlockState() : Blocks.SANDSTONE.defaultBlockState();
                     var4.setBlock(new BlockPos(var11, var16 + 1, var12), var23, 2);
                  }

                  var15 = var20;
               }
            }
         }
      }

      return var5;
   }
}
