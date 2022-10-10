package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Fluids;
import net.minecraft.init.Items;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.stats.StatList;
import net.minecraft.tags.FluidTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class BlockSeaGrass extends BlockBush implements IGrowable, ILiquidContainer {
   protected static final VoxelShape field_207798_a = Block.func_208617_a(2.0D, 0.0D, 2.0D, 14.0D, 12.0D, 14.0D);

   protected BlockSeaGrass(Block.Properties var1) {
      super(var1);
   }

   public VoxelShape func_196244_b(IBlockState var1, IBlockReader var2, BlockPos var3) {
      return field_207798_a;
   }

   protected boolean func_200014_a_(IBlockState var1, IBlockReader var2, BlockPos var3) {
      return Block.func_208061_a(var1.func_196952_d(var2, var3), EnumFacing.UP) && var1.func_177230_c() != Blocks.field_196814_hQ;
   }

   @Nullable
   public IBlockState func_196258_a(BlockItemUseContext var1) {
      IFluidState var2 = var1.func_195991_k().func_204610_c(var1.func_195995_a());
      return var2.func_206884_a(FluidTags.field_206959_a) && var2.func_206882_g() == 8 ? super.func_196258_a(var1) : null;
   }

   public IBlockState func_196271_a(IBlockState var1, EnumFacing var2, IBlockState var3, IWorld var4, BlockPos var5, BlockPos var6) {
      IBlockState var7 = super.func_196271_a(var1, var2, var3, var4, var5, var6);
      if (!var7.func_196958_f()) {
         var4.func_205219_F_().func_205360_a(var5, Fluids.field_204546_a, Fluids.field_204546_a.func_205569_a(var4));
      }

      return var7;
   }

   public void func_180657_a(World var1, EntityPlayer var2, BlockPos var3, IBlockState var4, @Nullable TileEntity var5, ItemStack var6) {
      if (!var1.field_72995_K && var6.func_77973_b() == Items.field_151097_aZ) {
         var2.func_71029_a(StatList.field_188065_ae.func_199076_b(this));
         var2.func_71020_j(0.005F);
         func_180635_a(var1, var3, new ItemStack(this));
      } else {
         super.func_180657_a(var1, var2, var3, var4, var5, var6);
      }

   }

   public IItemProvider func_199769_a(IBlockState var1, World var2, BlockPos var3, int var4) {
      return Items.field_190931_a;
   }

   public boolean func_176473_a(IBlockReader var1, BlockPos var2, IBlockState var3, boolean var4) {
      return true;
   }

   public boolean func_180670_a(World var1, Random var2, BlockPos var3, IBlockState var4) {
      return true;
   }

   public IFluidState func_204507_t(IBlockState var1) {
      return Fluids.field_204546_a.func_207204_a(false);
   }

   public void func_176474_b(World var1, Random var2, BlockPos var3, IBlockState var4) {
      IBlockState var5 = Blocks.field_203199_aR.func_176223_P();
      IBlockState var6 = (IBlockState)var5.func_206870_a(BlockSeaGrassTall.field_208065_c, DoubleBlockHalf.UPPER);
      BlockPos var7 = var3.func_177984_a();
      if (var1.func_180495_p(var7).func_177230_c() == Blocks.field_150355_j) {
         var1.func_180501_a(var3, var5, 2);
         var1.func_180501_a(var7, var6, 2);
      }

   }

   public boolean func_204510_a(IBlockReader var1, BlockPos var2, IBlockState var3, Fluid var4) {
      return false;
   }

   public boolean func_204509_a(IWorld var1, BlockPos var2, IBlockState var3, IFluidState var4) {
      return false;
   }

   public int func_200011_d(IBlockState var1, IBlockReader var2, BlockPos var3) {
      return Blocks.field_150355_j.func_176223_P().func_200016_a(var2, var3);
   }
}
