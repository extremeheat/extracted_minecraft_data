package net.minecraft.world.level.levelgen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Iterator;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.TheEndGatewayBlockEntity;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;

public class EndGatewayFeature extends Feature<EndGatewayConfiguration> {
   public EndGatewayFeature(Function<Dynamic<?>, ? extends EndGatewayConfiguration> var1) {
      super(var1);
   }

   public boolean place(LevelAccessor var1, ChunkGenerator<? extends ChunkGeneratorSettings> var2, Random var3, BlockPos var4, EndGatewayConfiguration var5) {
      Iterator var6 = BlockPos.betweenClosed(var4.offset(-1, -2, -1), var4.offset(1, 2, 1)).iterator();

      while(true) {
         while(var6.hasNext()) {
            BlockPos var7 = (BlockPos)var6.next();
            boolean var8 = var7.getX() == var4.getX();
            boolean var9 = var7.getY() == var4.getY();
            boolean var10 = var7.getZ() == var4.getZ();
            boolean var11 = Math.abs(var7.getY() - var4.getY()) == 2;
            if (var8 && var9 && var10) {
               BlockPos var12 = var7.immutable();
               this.setBlock(var1, var12, Blocks.END_GATEWAY.defaultBlockState());
               var5.getExit().ifPresent((var3x) -> {
                  BlockEntity var4 = var1.getBlockEntity(var12);
                  if (var4 instanceof TheEndGatewayBlockEntity) {
                     TheEndGatewayBlockEntity var5x = (TheEndGatewayBlockEntity)var4;
                     var5x.setExitPosition(var3x, var5.isExitExact());
                     var4.setChanged();
                  }

               });
            } else if (var9) {
               this.setBlock(var1, var7, Blocks.AIR.defaultBlockState());
            } else if (var11 && var8 && var10) {
               this.setBlock(var1, var7, Blocks.BEDROCK.defaultBlockState());
            } else if ((var8 || var10) && !var11) {
               this.setBlock(var1, var7, Blocks.BEDROCK.defaultBlockState());
            } else {
               this.setBlock(var1, var7, Blocks.AIR.defaultBlockState());
            }
         }

         return true;
      }
   }
}
