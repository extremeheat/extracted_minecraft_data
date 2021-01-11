package net.minecraft.world.gen.feature;

import java.util.Iterator;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class WorldGenBlockBlob extends WorldGenerator {
   private final Block field_150545_a;
   private final int field_150544_b;

   public WorldGenBlockBlob(Block var1, int var2) {
      super(false);
      this.field_150545_a = var1;
      this.field_150544_b = var2;
   }

   public boolean func_180709_b(World var1, Random var2, BlockPos var3) {
      while(true) {
         label50: {
            if (var3.func_177956_o() > 3) {
               if (var1.func_175623_d(var3.func_177977_b())) {
                  break label50;
               }

               Block var4 = var1.func_180495_p(var3.func_177977_b()).func_177230_c();
               if (var4 != Blocks.field_150349_c && var4 != Blocks.field_150346_d && var4 != Blocks.field_150348_b) {
                  break label50;
               }
            }

            if (var3.func_177956_o() <= 3) {
               return false;
            }

            int var12 = this.field_150544_b;

            for(int var5 = 0; var12 >= 0 && var5 < 3; ++var5) {
               int var6 = var12 + var2.nextInt(2);
               int var7 = var12 + var2.nextInt(2);
               int var8 = var12 + var2.nextInt(2);
               float var9 = (float)(var6 + var7 + var8) * 0.333F + 0.5F;
               Iterator var10 = BlockPos.func_177980_a(var3.func_177982_a(-var6, -var7, -var8), var3.func_177982_a(var6, var7, var8)).iterator();

               while(var10.hasNext()) {
                  BlockPos var11 = (BlockPos)var10.next();
                  if (var11.func_177951_i(var3) <= (double)(var9 * var9)) {
                     var1.func_180501_a(var11, this.field_150545_a.func_176223_P(), 4);
                  }
               }

               var3 = var3.func_177982_a(-(var12 + 1) + var2.nextInt(2 + var12 * 2), 0 - var2.nextInt(2), -(var12 + 1) + var2.nextInt(2 + var12 * 2));
            }

            return true;
         }

         var3 = var3.func_177977_b();
      }
   }
}
