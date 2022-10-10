package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;

public class BlockCocoa extends BlockHorizontal implements IGrowable {
   public static final IntegerProperty field_176501_a;
   protected static final VoxelShape[] field_185535_b;
   protected static final VoxelShape[] field_185536_c;
   protected static final VoxelShape[] field_185537_d;
   protected static final VoxelShape[] field_185538_e;

   public BlockCocoa(Block.Properties var1) {
      super(var1);
      this.func_180632_j((IBlockState)((IBlockState)((IBlockState)this.field_176227_L.func_177621_b()).func_206870_a(field_185512_D, EnumFacing.NORTH)).func_206870_a(field_176501_a, 0));
   }

   public void func_196267_b(IBlockState var1, World var2, BlockPos var3, Random var4) {
      if (var2.field_73012_v.nextInt(5) == 0) {
         int var5 = (Integer)var1.func_177229_b(field_176501_a);
         if (var5 < 2) {
            var2.func_180501_a(var3, (IBlockState)var1.func_206870_a(field_176501_a, var5 + 1), 2);
         }
      }

   }

   public boolean func_196260_a(IBlockState var1, IWorldReaderBase var2, BlockPos var3) {
      Block var4 = var2.func_180495_p(var3.func_177972_a((EnumFacing)var1.func_177229_b(field_185512_D))).func_177230_c();
      return var4.func_203417_a(BlockTags.field_203289_r);
   }

   public boolean func_149686_d(IBlockState var1) {
      return false;
   }

   public VoxelShape func_196244_b(IBlockState var1, IBlockReader var2, BlockPos var3) {
      int var4 = (Integer)var1.func_177229_b(field_176501_a);
      switch((EnumFacing)var1.func_177229_b(field_185512_D)) {
      case SOUTH:
         return field_185538_e[var4];
      case NORTH:
      default:
         return field_185537_d[var4];
      case WEST:
         return field_185536_c[var4];
      case EAST:
         return field_185535_b[var4];
      }
   }

   @Nullable
   public IBlockState func_196258_a(BlockItemUseContext var1) {
      IBlockState var2 = this.func_176223_P();
      World var3 = var1.func_195991_k();
      BlockPos var4 = var1.func_195995_a();
      EnumFacing[] var5 = var1.func_196009_e();
      int var6 = var5.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         EnumFacing var8 = var5[var7];
         if (var8.func_176740_k().func_176722_c()) {
            var2 = (IBlockState)var2.func_206870_a(field_185512_D, var8);
            if (var2.func_196955_c(var3, var4)) {
               return var2;
            }
         }
      }

      return null;
   }

   public IBlockState func_196271_a(IBlockState var1, EnumFacing var2, IBlockState var3, IWorld var4, BlockPos var5, BlockPos var6) {
      return var2 == var1.func_177229_b(field_185512_D) && !var1.func_196955_c(var4, var5) ? Blocks.field_150350_a.func_176223_P() : super.func_196271_a(var1, var2, var3, var4, var5, var6);
   }

   public void func_196255_a(IBlockState var1, World var2, BlockPos var3, float var4, int var5) {
      int var6 = (Integer)var1.func_177229_b(field_176501_a);
      byte var7 = 1;
      if (var6 >= 2) {
         var7 = 3;
      }

      for(int var8 = 0; var8 < var7; ++var8) {
         func_180635_a(var2, var3, new ItemStack(Items.field_196130_bo));
      }

   }

   public ItemStack func_185473_a(IBlockReader var1, BlockPos var2, IBlockState var3) {
      return new ItemStack(Items.field_196130_bo);
   }

   public boolean func_176473_a(IBlockReader var1, BlockPos var2, IBlockState var3, boolean var4) {
      return (Integer)var3.func_177229_b(field_176501_a) < 2;
   }

   public boolean func_180670_a(World var1, Random var2, BlockPos var3, IBlockState var4) {
      return true;
   }

   public void func_176474_b(World var1, Random var2, BlockPos var3, IBlockState var4) {
      var1.func_180501_a(var3, (IBlockState)var4.func_206870_a(field_176501_a, (Integer)var4.func_177229_b(field_176501_a) + 1), 2);
   }

   public BlockRenderLayer func_180664_k() {
      return BlockRenderLayer.CUTOUT;
   }

   protected void func_206840_a(StateContainer.Builder<Block, IBlockState> var1) {
      var1.func_206894_a(field_185512_D, field_176501_a);
   }

   public BlockFaceShape func_193383_a(IBlockReader var1, IBlockState var2, BlockPos var3, EnumFacing var4) {
      return BlockFaceShape.UNDEFINED;
   }

   static {
      field_176501_a = BlockStateProperties.field_208167_T;
      field_185535_b = new VoxelShape[]{Block.func_208617_a(11.0D, 7.0D, 6.0D, 15.0D, 12.0D, 10.0D), Block.func_208617_a(9.0D, 5.0D, 5.0D, 15.0D, 12.0D, 11.0D), Block.func_208617_a(7.0D, 3.0D, 4.0D, 15.0D, 12.0D, 12.0D)};
      field_185536_c = new VoxelShape[]{Block.func_208617_a(1.0D, 7.0D, 6.0D, 5.0D, 12.0D, 10.0D), Block.func_208617_a(1.0D, 5.0D, 5.0D, 7.0D, 12.0D, 11.0D), Block.func_208617_a(1.0D, 3.0D, 4.0D, 9.0D, 12.0D, 12.0D)};
      field_185537_d = new VoxelShape[]{Block.func_208617_a(6.0D, 7.0D, 1.0D, 10.0D, 12.0D, 5.0D), Block.func_208617_a(5.0D, 5.0D, 1.0D, 11.0D, 12.0D, 7.0D), Block.func_208617_a(4.0D, 3.0D, 1.0D, 12.0D, 12.0D, 9.0D)};
      field_185538_e = new VoxelShape[]{Block.func_208617_a(6.0D, 7.0D, 11.0D, 10.0D, 12.0D, 15.0D), Block.func_208617_a(5.0D, 5.0D, 9.0D, 11.0D, 12.0D, 15.0D), Block.func_208617_a(4.0D, 3.0D, 7.0D, 12.0D, 12.0D, 15.0D)};
   }
}
