package net.minecraft.block;

import java.util.Iterator;
import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenBigMushroom;

public class BlockMushroom extends BlockBush implements IGrowable {
   protected BlockMushroom() {
      super();
      float var1 = 0.2F;
      this.func_149676_a(0.5F - var1, 0.0F, 0.5F - var1, 0.5F + var1, var1 * 2.0F, 0.5F + var1);
      this.func_149675_a(true);
   }

   public void func_180650_b(World var1, BlockPos var2, IBlockState var3, Random var4) {
      if (var4.nextInt(25) == 0) {
         int var5 = 5;
         boolean var6 = true;
         Iterator var7 = BlockPos.func_177975_b(var2.func_177982_a(-4, -1, -4), var2.func_177982_a(4, 1, 4)).iterator();

         while(var7.hasNext()) {
            BlockPos var8 = (BlockPos)var7.next();
            if (var1.func_180495_p(var8).func_177230_c() == this) {
               --var5;
               if (var5 <= 0) {
                  return;
               }
            }
         }

         BlockPos var9 = var2.func_177982_a(var4.nextInt(3) - 1, var4.nextInt(2) - var4.nextInt(2), var4.nextInt(3) - 1);

         for(int var10 = 0; var10 < 4; ++var10) {
            if (var1.func_175623_d(var9) && this.func_180671_f(var1, var9, this.func_176223_P())) {
               var2 = var9;
            }

            var9 = var2.func_177982_a(var4.nextInt(3) - 1, var4.nextInt(2) - var4.nextInt(2), var4.nextInt(3) - 1);
         }

         if (var1.func_175623_d(var9) && this.func_180671_f(var1, var9, this.func_176223_P())) {
            var1.func_180501_a(var9, this.func_176223_P(), 2);
         }
      }

   }

   public boolean func_176196_c(World var1, BlockPos var2) {
      return super.func_176196_c(var1, var2) && this.func_180671_f(var1, var2, this.func_176223_P());
   }

   protected boolean func_149854_a(Block var1) {
      return var1.func_149730_j();
   }

   public boolean func_180671_f(World var1, BlockPos var2, IBlockState var3) {
      if (var2.func_177956_o() >= 0 && var2.func_177956_o() < 256) {
         IBlockState var4 = var1.func_180495_p(var2.func_177977_b());
         if (var4.func_177230_c() == Blocks.field_150391_bh) {
            return true;
         } else if (var4.func_177230_c() == Blocks.field_150346_d && var4.func_177229_b(BlockDirt.field_176386_a) == BlockDirt.DirtType.PODZOL) {
            return true;
         } else {
            return var1.func_175699_k(var2) < 13 && this.func_149854_a(var4.func_177230_c());
         }
      } else {
         return false;
      }
   }

   public boolean func_176485_d(World var1, BlockPos var2, IBlockState var3, Random var4) {
      var1.func_175698_g(var2);
      WorldGenBigMushroom var5 = null;
      if (this == Blocks.field_150338_P) {
         var5 = new WorldGenBigMushroom(Blocks.field_150420_aW);
      } else if (this == Blocks.field_150337_Q) {
         var5 = new WorldGenBigMushroom(Blocks.field_150419_aX);
      }

      if (var5 != null && var5.func_180709_b(var1, var4, var2)) {
         return true;
      } else {
         var1.func_180501_a(var2, var3, 3);
         return false;
      }
   }

   public boolean func_176473_a(World var1, BlockPos var2, IBlockState var3, boolean var4) {
      return true;
   }

   public boolean func_180670_a(World var1, Random var2, BlockPos var3, IBlockState var4) {
      return (double)var2.nextFloat() < 0.4D;
   }

   public void func_176474_b(World var1, Random var2, BlockPos var3, IBlockState var4) {
      this.func_176485_d(var1, var3, var4, var2);
   }
}
