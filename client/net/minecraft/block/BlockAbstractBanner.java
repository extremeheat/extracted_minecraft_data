package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBanner;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public abstract class BlockAbstractBanner extends BlockContainer {
   private final EnumDyeColor field_196286_a;

   protected BlockAbstractBanner(EnumDyeColor var1, Block.Properties var2) {
      super(var2);
      this.field_196286_a = var1;
   }

   public boolean func_149686_d(IBlockState var1) {
      return false;
   }

   public boolean func_181623_g() {
      return true;
   }

   public TileEntity func_196283_a_(IBlockReader var1) {
      return new TileEntityBanner(this.field_196286_a);
   }

   public IItemProvider func_199769_a(IBlockState var1, World var2, BlockPos var3, int var4) {
      return Items.field_196191_eg;
   }

   public BlockFaceShape func_193383_a(IBlockReader var1, IBlockState var2, BlockPos var3, EnumFacing var4) {
      return BlockFaceShape.UNDEFINED;
   }

   public ItemStack func_185473_a(IBlockReader var1, BlockPos var2, IBlockState var3) {
      TileEntity var4 = var1.func_175625_s(var2);
      return var4 instanceof TileEntityBanner ? ((TileEntityBanner)var4).func_190615_l(var3) : super.func_185473_a(var1, var2, var3);
   }

   public void func_196255_a(IBlockState var1, World var2, BlockPos var3, float var4, int var5) {
      func_180635_a(var2, var3, this.func_185473_a(var2, var3, var1));
   }

   public void func_180657_a(World var1, EntityPlayer var2, BlockPos var3, IBlockState var4, @Nullable TileEntity var5, ItemStack var6) {
      if (var5 instanceof TileEntityBanner) {
         func_180635_a(var1, var3, ((TileEntityBanner)var5).func_190615_l(var4));
         var2.func_71029_a(StatList.field_188065_ae.func_199076_b(this));
      } else {
         super.func_180657_a(var1, var2, var3, var4, (TileEntity)null, var6);
      }

   }

   public void func_180633_a(World var1, BlockPos var2, IBlockState var3, @Nullable EntityLivingBase var4, ItemStack var5) {
      TileEntity var6 = var1.func_175625_s(var2);
      if (var6 instanceof TileEntityBanner) {
         ((TileEntityBanner)var6).func_195534_a(var5, this.field_196286_a);
      }

   }

   public EnumDyeColor func_196285_M_() {
      return this.field_196286_a;
   }
}
