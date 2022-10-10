package net.minecraft.world.gen.feature;

import java.util.Random;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class ShrubFeature extends AbstractTreeFeature<NoFeatureConfig> {
   private final IBlockState field_150528_a;
   private final IBlockState field_150527_b;

   public ShrubFeature(IBlockState var1, IBlockState var2) {
      super(false);
      this.field_150527_b = var1;
      this.field_150528_a = var2;
   }

   public boolean func_208519_a(Set<BlockPos> var1, IWorld var2, Random var3, BlockPos var4) {
      for(IBlockState var5 = var2.func_180495_p(var4); (var5.func_196958_f() || var5.func_203425_a(BlockTags.field_206952_E)) && var4.func_177956_o() > 0; var5 = var2.func_180495_p(var4)) {
         var4 = var4.func_177977_b();
      }

      Block var6 = var2.func_180495_p(var4).func_177230_c();
      if (Block.func_196245_f(var6) || var6 == Blocks.field_196658_i) {
         var4 = var4.func_177984_a();
         this.func_208520_a(var1, var2, var4, this.field_150527_b);

         for(int var7 = var4.func_177956_o(); var7 <= var4.func_177956_o() + 2; ++var7) {
            int var8 = var7 - var4.func_177956_o();
            int var9 = 2 - var8;

            for(int var10 = var4.func_177958_n() - var9; var10 <= var4.func_177958_n() + var9; ++var10) {
               int var11 = var10 - var4.func_177958_n();

               for(int var12 = var4.func_177952_p() - var9; var12 <= var4.func_177952_p() + var9; ++var12) {
                  int var13 = var12 - var4.func_177952_p();
                  if (Math.abs(var11) != var9 || Math.abs(var13) != var9 || var3.nextInt(2) != 0) {
                     BlockPos var14 = new BlockPos(var10, var7, var12);
                     IBlockState var15 = var2.func_180495_p(var14);
                     if (var15.func_196958_f() || var15.func_203425_a(BlockTags.field_206952_E)) {
                        this.func_202278_a(var2, var14, this.field_150528_a);
                     }
                  }
               }
            }
         }
      }

      return true;
   }
}
