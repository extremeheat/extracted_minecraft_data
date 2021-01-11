package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class WorldGenSpikes extends WorldGenerator {
   private Block field_150520_a;

   public WorldGenSpikes(Block var1) {
      super();
      this.field_150520_a = var1;
   }

   public boolean func_180709_b(World var1, Random var2, BlockPos var3) {
      if (var1.func_175623_d(var3) && var1.func_180495_p(var3.func_177977_b()).func_177230_c() == this.field_150520_a) {
         int var4 = var2.nextInt(32) + 6;
         int var5 = var2.nextInt(4) + 1;
         BlockPos.MutableBlockPos var6 = new BlockPos.MutableBlockPos();

         int var7;
         int var8;
         int var9;
         int var10;
         for(var7 = var3.func_177958_n() - var5; var7 <= var3.func_177958_n() + var5; ++var7) {
            for(var8 = var3.func_177952_p() - var5; var8 <= var3.func_177952_p() + var5; ++var8) {
               var9 = var7 - var3.func_177958_n();
               var10 = var8 - var3.func_177952_p();
               if (var9 * var9 + var10 * var10 <= var5 * var5 + 1 && var1.func_180495_p(var6.func_181079_c(var7, var3.func_177956_o() - 1, var8)).func_177230_c() != this.field_150520_a) {
                  return false;
               }
            }
         }

         for(var7 = var3.func_177956_o(); var7 < var3.func_177956_o() + var4 && var7 < 256; ++var7) {
            for(var8 = var3.func_177958_n() - var5; var8 <= var3.func_177958_n() + var5; ++var8) {
               for(var9 = var3.func_177952_p() - var5; var9 <= var3.func_177952_p() + var5; ++var9) {
                  var10 = var8 - var3.func_177958_n();
                  int var11 = var9 - var3.func_177952_p();
                  if (var10 * var10 + var11 * var11 <= var5 * var5 + 1) {
                     var1.func_180501_a(new BlockPos(var8, var7, var9), Blocks.field_150343_Z.func_176223_P(), 2);
                  }
               }
            }
         }

         EntityEnderCrystal var12 = new EntityEnderCrystal(var1);
         var12.func_70012_b((double)((float)var3.func_177958_n() + 0.5F), (double)(var3.func_177956_o() + var4), (double)((float)var3.func_177952_p() + 0.5F), var2.nextFloat() * 360.0F, 0.0F);
         var1.func_72838_d(var12);
         var1.func_180501_a(var3.func_177981_b(var4), Blocks.field_150357_h.func_176223_P(), 2);
         return true;
      } else {
         return false;
      }
   }
}
