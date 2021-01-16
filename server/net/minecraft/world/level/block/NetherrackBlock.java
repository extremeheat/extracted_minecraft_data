package net.minecraft.world.level.block;

import java.util.Iterator;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class NetherrackBlock extends Block implements BonemealableBlock {
   public NetherrackBlock(BlockBehaviour.Properties var1) {
      super(var1);
   }

   public boolean isValidBonemealTarget(BlockGetter var1, BlockPos var2, BlockState var3, boolean var4) {
      if (!var1.getBlockState(var2.above()).propagatesSkylightDown(var1, var2)) {
         return false;
      } else {
         Iterator var5 = BlockPos.betweenClosed(var2.offset(-1, -1, -1), var2.offset(1, 1, 1)).iterator();

         BlockPos var6;
         do {
            if (!var5.hasNext()) {
               return false;
            }

            var6 = (BlockPos)var5.next();
         } while(!var1.getBlockState(var6).is(BlockTags.NYLIUM));

         return true;
      }
   }

   public boolean isBonemealSuccess(Level var1, Random var2, BlockPos var3, BlockState var4) {
      return true;
   }

   public void performBonemeal(ServerLevel var1, Random var2, BlockPos var3, BlockState var4) {
      boolean var5 = false;
      boolean var6 = false;
      Iterator var7 = BlockPos.betweenClosed(var3.offset(-1, -1, -1), var3.offset(1, 1, 1)).iterator();

      while(var7.hasNext()) {
         BlockPos var8 = (BlockPos)var7.next();
         BlockState var9 = var1.getBlockState(var8);
         if (var9.is(Blocks.WARPED_NYLIUM)) {
            var6 = true;
         }

         if (var9.is(Blocks.CRIMSON_NYLIUM)) {
            var5 = true;
         }

         if (var6 && var5) {
            break;
         }
      }

      if (var6 && var5) {
         var1.setBlock(var3, var2.nextBoolean() ? Blocks.WARPED_NYLIUM.defaultBlockState() : Blocks.CRIMSON_NYLIUM.defaultBlockState(), 3);
      } else if (var6) {
         var1.setBlock(var3, Blocks.WARPED_NYLIUM.defaultBlockState(), 3);
      } else if (var5) {
         var1.setBlock(var3, Blocks.CRIMSON_NYLIUM.defaultBlockState(), 3);
      }

   }
}
