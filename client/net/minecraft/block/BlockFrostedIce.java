package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockFrostedIce extends BlockIce {
   public static final IntegerProperty field_185682_a;

   public BlockFrostedIce(Block.Properties var1) {
      super(var1);
      this.func_180632_j((IBlockState)((IBlockState)this.field_176227_L.func_177621_b()).func_206870_a(field_185682_a, 0));
   }

   public void func_196267_b(IBlockState var1, World var2, BlockPos var3, Random var4) {
      if ((var4.nextInt(3) == 0 || this.func_196456_a(var2, var3, 4)) && var2.func_201696_r(var3) > 11 - (Integer)var1.func_177229_b(field_185682_a) - var1.func_200016_a(var2, var3) && this.func_196455_e(var1, var2, var3)) {
         BlockPos.PooledMutableBlockPos var5 = BlockPos.PooledMutableBlockPos.func_185346_s();
         Throwable var6 = null;

         try {
            EnumFacing[] var7 = EnumFacing.values();
            int var8 = var7.length;

            for(int var9 = 0; var9 < var8; ++var9) {
               EnumFacing var10 = var7[var9];
               var5.func_189533_g(var3).func_189536_c(var10);
               IBlockState var11 = var2.func_180495_p(var5);
               if (var11.func_177230_c() == this && !this.func_196455_e(var11, var2, var5)) {
                  var2.func_205220_G_().func_205360_a(var5, this, MathHelper.func_76136_a(var4, 20, 40));
               }
            }
         } catch (Throwable var19) {
            var6 = var19;
            throw var19;
         } finally {
            if (var5 != null) {
               if (var6 != null) {
                  try {
                     var5.close();
                  } catch (Throwable var18) {
                     var6.addSuppressed(var18);
                  }
               } else {
                  var5.close();
               }
            }

         }

      } else {
         var2.func_205220_G_().func_205360_a(var3, this, MathHelper.func_76136_a(var4, 20, 40));
      }
   }

   private boolean func_196455_e(IBlockState var1, World var2, BlockPos var3) {
      int var4 = (Integer)var1.func_177229_b(field_185682_a);
      if (var4 < 3) {
         var2.func_180501_a(var3, (IBlockState)var1.func_206870_a(field_185682_a, var4 + 1), 2);
         return false;
      } else {
         this.func_196454_d(var1, var2, var3);
         return true;
      }
   }

   public void func_189540_a(IBlockState var1, World var2, BlockPos var3, Block var4, BlockPos var5) {
      if (var4 == this && this.func_196456_a(var2, var3, 2)) {
         this.func_196454_d(var1, var2, var3);
      }

      super.func_189540_a(var1, var2, var3, var4, var5);
   }

   private boolean func_196456_a(IBlockReader var1, BlockPos var2, int var3) {
      int var4 = 0;
      BlockPos.PooledMutableBlockPos var5 = BlockPos.PooledMutableBlockPos.func_185346_s();
      Throwable var6 = null;

      try {
         EnumFacing[] var7 = EnumFacing.values();
         int var8 = var7.length;

         for(int var9 = 0; var9 < var8; ++var9) {
            EnumFacing var10 = var7[var9];
            var5.func_189533_g(var2).func_189536_c(var10);
            if (var1.func_180495_p(var5).func_177230_c() == this) {
               ++var4;
               if (var4 >= var3) {
                  boolean var11 = false;
                  return var11;
               }
            }
         }

         return true;
      } catch (Throwable var21) {
         var6 = var21;
         throw var21;
      } finally {
         if (var5 != null) {
            if (var6 != null) {
               try {
                  var5.close();
               } catch (Throwable var20) {
                  var6.addSuppressed(var20);
               }
            } else {
               var5.close();
            }
         }

      }
   }

   protected void func_206840_a(StateContainer.Builder<Block, IBlockState> var1) {
      var1.func_206894_a(field_185682_a);
   }

   public ItemStack func_185473_a(IBlockReader var1, BlockPos var2, IBlockState var3) {
      return ItemStack.field_190927_a;
   }

   static {
      field_185682_a = BlockStateProperties.field_208168_U;
   }
}
