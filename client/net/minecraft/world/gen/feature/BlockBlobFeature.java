package net.minecraft.world.gen.feature;

import java.util.Iterator;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;

public class BlockBlobFeature extends Feature<BlockBlobConfig> {
   public BlockBlobFeature() {
      super();
   }

   public boolean func_212245_a(IWorld var1, IChunkGenerator<? extends IChunkGenSettings> var2, Random var3, BlockPos var4, BlockBlobConfig var5) {
      while(true) {
         label50: {
            if (var4.func_177956_o() > 3) {
               if (var1.func_175623_d(var4.func_177977_b())) {
                  break label50;
               }

               Block var6 = var1.func_180495_p(var4.func_177977_b()).func_177230_c();
               if (var6 != Blocks.field_196658_i && !Block.func_196245_f(var6) && !Block.func_196252_e(var6)) {
                  break label50;
               }
            }

            if (var4.func_177956_o() <= 3) {
               return false;
            }

            int var14 = var5.field_202464_b;

            for(int var7 = 0; var14 >= 0 && var7 < 3; ++var7) {
               int var8 = var14 + var3.nextInt(2);
               int var9 = var14 + var3.nextInt(2);
               int var10 = var14 + var3.nextInt(2);
               float var11 = (float)(var8 + var9 + var10) * 0.333F + 0.5F;
               Iterator var12 = BlockPos.func_177980_a(var4.func_177982_a(-var8, -var9, -var10), var4.func_177982_a(var8, var9, var10)).iterator();

               while(var12.hasNext()) {
                  BlockPos var13 = (BlockPos)var12.next();
                  if (var13.func_177951_i(var4) <= (double)(var11 * var11)) {
                     var1.func_180501_a(var13, var5.field_202463_a.func_176223_P(), 4);
                  }
               }

               var4 = var4.func_177982_a(-(var14 + 1) + var3.nextInt(2 + var14 * 2), 0 - var3.nextInt(2), -(var14 + 1) + var3.nextInt(2 + var14 * 2));
            }

            return true;
         }

         var4 = var4.func_177977_b();
      }
   }
}
