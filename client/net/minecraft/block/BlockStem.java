package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockStem extends BlockBush implements IGrowable {
   public static final IntegerProperty field_176484_a;
   protected static final VoxelShape[] field_196388_b;
   private final BlockStemGrown field_149877_a;

   protected BlockStem(BlockStemGrown var1, Block.Properties var2) {
      super(var2);
      this.field_149877_a = var1;
      this.func_180632_j((IBlockState)((IBlockState)this.field_176227_L.func_177621_b()).func_206870_a(field_176484_a, 0));
   }

   public VoxelShape func_196244_b(IBlockState var1, IBlockReader var2, BlockPos var3) {
      return field_196388_b[(Integer)var1.func_177229_b(field_176484_a)];
   }

   protected boolean func_200014_a_(IBlockState var1, IBlockReader var2, BlockPos var3) {
      return var1.func_177230_c() == Blocks.field_150458_ak;
   }

   public void func_196267_b(IBlockState var1, World var2, BlockPos var3, Random var4) {
      super.func_196267_b(var1, var2, var3, var4);
      if (var2.func_201669_a(var3.func_177984_a(), 0) >= 9) {
         float var5 = BlockCrops.func_180672_a(this, var2, var3);
         if (var4.nextInt((int)(25.0F / var5) + 1) == 0) {
            int var6 = (Integer)var1.func_177229_b(field_176484_a);
            if (var6 < 7) {
               var1 = (IBlockState)var1.func_206870_a(field_176484_a, var6 + 1);
               var2.func_180501_a(var3, var1, 2);
            } else {
               EnumFacing var7 = EnumFacing.Plane.HORIZONTAL.func_179518_a(var4);
               BlockPos var8 = var3.func_177972_a(var7);
               Block var9 = var2.func_180495_p(var8.func_177977_b()).func_177230_c();
               if (var2.func_180495_p(var8).func_196958_f() && (var9 == Blocks.field_150458_ak || var9 == Blocks.field_150346_d || var9 == Blocks.field_196660_k || var9 == Blocks.field_196661_l || var9 == Blocks.field_196658_i)) {
                  var2.func_175656_a(var8, this.field_149877_a.func_176223_P());
                  var2.func_175656_a(var3, (IBlockState)this.field_149877_a.func_196523_e().func_176223_P().func_206870_a(BlockHorizontal.field_185512_D, var7));
               }
            }
         }

      }
   }

   public void func_196255_a(IBlockState var1, World var2, BlockPos var3, float var4, int var5) {
      super.func_196255_a(var1, var2, var3, var4, var5);
      if (!var2.field_72995_K) {
         Item var6 = this.func_176481_j();
         if (var6 != null) {
            int var7 = (Integer)var1.func_177229_b(field_176484_a);

            for(int var8 = 0; var8 < 3; ++var8) {
               if (var2.field_73012_v.nextInt(15) <= var7) {
                  func_180635_a(var2, var3, new ItemStack(var6));
               }
            }

         }
      }
   }

   @Nullable
   protected Item func_176481_j() {
      if (this.field_149877_a == Blocks.field_150423_aK) {
         return Items.field_151080_bb;
      } else {
         return this.field_149877_a == Blocks.field_150440_ba ? Items.field_151081_bc : null;
      }
   }

   public IItemProvider func_199769_a(IBlockState var1, World var2, BlockPos var3, int var4) {
      return Items.field_190931_a;
   }

   public ItemStack func_185473_a(IBlockReader var1, BlockPos var2, IBlockState var3) {
      Item var4 = this.func_176481_j();
      return var4 == null ? ItemStack.field_190927_a : new ItemStack(var4);
   }

   public boolean func_176473_a(IBlockReader var1, BlockPos var2, IBlockState var3, boolean var4) {
      return (Integer)var3.func_177229_b(field_176484_a) != 7;
   }

   public boolean func_180670_a(World var1, Random var2, BlockPos var3, IBlockState var4) {
      return true;
   }

   public void func_176474_b(World var1, Random var2, BlockPos var3, IBlockState var4) {
      int var5 = Math.min(7, (Integer)var4.func_177229_b(field_176484_a) + MathHelper.func_76136_a(var1.field_73012_v, 2, 5));
      IBlockState var6 = (IBlockState)var4.func_206870_a(field_176484_a, var5);
      var1.func_180501_a(var3, var6, 2);
      if (var5 == 7) {
         var6.func_196940_a(var1, var3, var1.field_73012_v);
      }

   }

   protected void func_206840_a(StateContainer.Builder<Block, IBlockState> var1) {
      var1.func_206894_a(field_176484_a);
   }

   public BlockStemGrown func_208486_d() {
      return this.field_149877_a;
   }

   static {
      field_176484_a = BlockStateProperties.field_208170_W;
      field_196388_b = new VoxelShape[]{Block.func_208617_a(7.0D, 0.0D, 7.0D, 9.0D, 2.0D, 9.0D), Block.func_208617_a(7.0D, 0.0D, 7.0D, 9.0D, 4.0D, 9.0D), Block.func_208617_a(7.0D, 0.0D, 7.0D, 9.0D, 6.0D, 9.0D), Block.func_208617_a(7.0D, 0.0D, 7.0D, 9.0D, 8.0D, 9.0D), Block.func_208617_a(7.0D, 0.0D, 7.0D, 9.0D, 10.0D, 9.0D), Block.func_208617_a(7.0D, 0.0D, 7.0D, 9.0D, 12.0D, 9.0D), Block.func_208617_a(7.0D, 0.0D, 7.0D, 9.0D, 14.0D, 9.0D), Block.func_208617_a(7.0D, 0.0D, 7.0D, 9.0D, 16.0D, 9.0D)};
   }
}
