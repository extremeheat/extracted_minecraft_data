package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockWeb extends Block {
   public BlockWeb(Block.Properties var1) {
      super(var1);
   }

   public void func_196262_a(IBlockState var1, World var2, BlockPos var3, Entity var4) {
      var4.func_70110_aj();
   }

   public boolean func_149686_d(IBlockState var1) {
      return false;
   }

   public IItemProvider func_199769_a(IBlockState var1, World var2, BlockPos var3, int var4) {
      return Items.field_151007_F;
   }

   protected boolean func_149700_E() {
      return true;
   }

   public BlockRenderLayer func_180664_k() {
      return BlockRenderLayer.CUTOUT;
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

   public BlockFaceShape func_193383_a(IBlockReader var1, IBlockState var2, BlockPos var3, EnumFacing var4) {
      return BlockFaceShape.UNDEFINED;
   }
}
