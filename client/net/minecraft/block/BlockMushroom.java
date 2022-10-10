package net.minecraft.block;

import java.util.Iterator;
import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;

public class BlockMushroom extends BlockBush implements IGrowable {
   protected static final VoxelShape field_196385_a = Block.func_208617_a(5.0D, 0.0D, 5.0D, 11.0D, 6.0D, 11.0D);

   public BlockMushroom(Block.Properties var1) {
      super(var1);
   }

   public VoxelShape func_196244_b(IBlockState var1, IBlockReader var2, BlockPos var3) {
      return field_196385_a;
   }

   public void func_196267_b(IBlockState var1, World var2, BlockPos var3, Random var4) {
      if (var4.nextInt(25) == 0) {
         int var5 = 5;
         boolean var6 = true;
         Iterator var7 = BlockPos.func_177975_b(var3.func_177982_a(-4, -1, -4), var3.func_177982_a(4, 1, 4)).iterator();

         while(var7.hasNext()) {
            BlockPos var8 = (BlockPos)var7.next();
            if (var2.func_180495_p(var8).func_177230_c() == this) {
               --var5;
               if (var5 <= 0) {
                  return;
               }
            }
         }

         BlockPos var9 = var3.func_177982_a(var4.nextInt(3) - 1, var4.nextInt(2) - var4.nextInt(2), var4.nextInt(3) - 1);

         for(int var10 = 0; var10 < 4; ++var10) {
            if (var2.func_175623_d(var9) && var1.func_196955_c(var2, var9)) {
               var3 = var9;
            }

            var9 = var3.func_177982_a(var4.nextInt(3) - 1, var4.nextInt(2) - var4.nextInt(2), var4.nextInt(3) - 1);
         }

         if (var2.func_175623_d(var9) && var1.func_196955_c(var2, var9)) {
            var2.func_180501_a(var9, var1, 2);
         }
      }

   }

   protected boolean func_200014_a_(IBlockState var1, IBlockReader var2, BlockPos var3) {
      return var1.func_200015_d(var2, var3);
   }

   public boolean func_196260_a(IBlockState var1, IWorldReaderBase var2, BlockPos var3) {
      BlockPos var4 = var3.func_177977_b();
      IBlockState var5 = var2.func_180495_p(var4);
      Block var6 = var5.func_177230_c();
      if (var6 != Blocks.field_150391_bh && var6 != Blocks.field_196661_l) {
         return var2.func_201669_a(var3, 0) < 13 && this.func_200014_a_(var5, var2, var4);
      } else {
         return true;
      }
   }

   public boolean func_176485_d(IWorld var1, BlockPos var2, IBlockState var3, Random var4) {
      var1.func_175698_g(var2);
      Feature var5 = null;
      if (this == Blocks.field_150338_P) {
         var5 = Feature.field_202319_S;
      } else if (this == Blocks.field_150337_Q) {
         var5 = Feature.field_202318_R;
      }

      if (var5 != null && var5.func_212245_a(var1, var1.func_72863_F().func_201711_g(), var4, var2, IFeatureConfig.field_202429_e)) {
         return true;
      } else {
         var1.func_180501_a(var2, var3, 3);
         return false;
      }
   }

   public boolean func_176473_a(IBlockReader var1, BlockPos var2, IBlockState var3, boolean var4) {
      return true;
   }

   public boolean func_180670_a(World var1, Random var2, BlockPos var3, IBlockState var4) {
      return (double)var2.nextFloat() < 0.4D;
   }

   public void func_176474_b(World var1, Random var2, BlockPos var3, IBlockState var4) {
      this.func_176485_d(var1, var3, var4, var2);
   }

   public boolean func_201783_b(IBlockState var1, IBlockReader var2, BlockPos var3) {
      return true;
   }
}
