package net.minecraft.world.gen.feature;

import java.util.Iterator;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCoralWallFanDead;
import net.minecraft.block.BlockSeaPickle;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;

public abstract class CoralFeature extends Feature<NoFeatureConfig> {
   public CoralFeature() {
      super();
   }

   public boolean func_212245_a(IWorld var1, IChunkGenerator<? extends IChunkGenSettings> var2, Random var3, BlockPos var4, NoFeatureConfig var5) {
      IBlockState var6 = ((Block)BlockTags.field_205598_B.func_205596_a(var3)).func_176223_P();
      return this.func_204623_a(var1, var3, var4, var6);
   }

   protected abstract boolean func_204623_a(IWorld var1, Random var2, BlockPos var3, IBlockState var4);

   protected boolean func_204624_b(IWorld var1, Random var2, BlockPos var3, IBlockState var4) {
      BlockPos var5 = var3.func_177984_a();
      IBlockState var6 = var1.func_180495_p(var3);
      if ((var6.func_177230_c() == Blocks.field_150355_j || var6.func_203425_a(BlockTags.field_204116_z)) && var1.func_180495_p(var5).func_177230_c() == Blocks.field_150355_j) {
         var1.func_180501_a(var3, var4, 3);
         if (var2.nextFloat() < 0.25F) {
            var1.func_180501_a(var5, ((Block)BlockTags.field_204116_z.func_205596_a(var2)).func_176223_P(), 2);
         } else if (var2.nextFloat() < 0.05F) {
            var1.func_180501_a(var5, (IBlockState)Blocks.field_204913_jW.func_176223_P().func_206870_a(BlockSeaPickle.field_204902_a, var2.nextInt(4) + 1), 2);
         }

         Iterator var7 = EnumFacing.Plane.HORIZONTAL.iterator();

         while(var7.hasNext()) {
            EnumFacing var8 = (EnumFacing)var7.next();
            if (var2.nextFloat() < 0.2F) {
               BlockPos var9 = var3.func_177972_a(var8);
               if (var1.func_180495_p(var9).func_177230_c() == Blocks.field_150355_j) {
                  IBlockState var10 = (IBlockState)((Block)BlockTags.field_211922_B.func_205596_a(var2)).func_176223_P().func_206870_a(BlockCoralWallFanDead.field_211884_b, var8);
                  var1.func_180501_a(var9, var10, 2);
               }
            }
         }

         return true;
      } else {
         return false;
      }
   }
}
