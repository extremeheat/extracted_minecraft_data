package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Iterator;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.TheEndGatewayBlockEntity;
import net.minecraft.world.level.levelgen.feature.configurations.EndGatewayConfiguration;

public class EndGatewayFeature extends Feature<EndGatewayConfiguration> {
   public EndGatewayFeature(Codec<EndGatewayConfiguration> var1) {
      super(var1);
   }

   public boolean place(FeaturePlaceContext<EndGatewayConfiguration> var1) {
      BlockPos var2 = var1.origin();
      WorldGenLevel var3 = var1.level();
      EndGatewayConfiguration var4 = (EndGatewayConfiguration)var1.config();
      Iterator var5 = BlockPos.betweenClosed(var2.offset(-1, -2, -1), var2.offset(1, 2, 1)).iterator();

      while(true) {
         while(var5.hasNext()) {
            BlockPos var6 = (BlockPos)var5.next();
            boolean var7 = var6.getX() == var2.getX();
            boolean var8 = var6.getY() == var2.getY();
            boolean var9 = var6.getZ() == var2.getZ();
            boolean var10 = Math.abs(var6.getY() - var2.getY()) == 2;
            if (var7 && var8 && var9) {
               BlockPos var11 = var6.immutable();
               this.setBlock(var3, var11, Blocks.END_GATEWAY.defaultBlockState());
               var4.getExit().ifPresent((var3x) -> {
                  BlockEntity var4x = var3.getBlockEntity(var11);
                  if (var4x instanceof TheEndGatewayBlockEntity) {
                     TheEndGatewayBlockEntity var5 = (TheEndGatewayBlockEntity)var4x;
                     var5.setExitPosition(var3x, var4.isExitExact());
                     var4x.setChanged();
                  }

               });
            } else if (var8) {
               this.setBlock(var3, var6, Blocks.AIR.defaultBlockState());
            } else if (var10 && var7 && var9) {
               this.setBlock(var3, var6, Blocks.BEDROCK.defaultBlockState());
            } else if ((var7 || var9) && !var10) {
               this.setBlock(var3, var6, Blocks.BEDROCK.defaultBlockState());
            } else {
               this.setBlock(var3, var6, Blocks.AIR.defaultBlockState());
            }
         }

         return true;
      }
   }
}
