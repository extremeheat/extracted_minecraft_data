package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockSign extends BlockContainer {
   protected BlockSign() {
      super(Material.field_151575_d);
      float var1 = 0.25F;
      float var2 = 1.0F;
      this.func_149676_a(0.5F - var1, 0.0F, 0.5F - var1, 0.5F + var1, var2, 0.5F + var1);
   }

   public AxisAlignedBB func_180640_a(World var1, BlockPos var2, IBlockState var3) {
      return null;
   }

   public AxisAlignedBB func_180646_a(World var1, BlockPos var2) {
      this.func_180654_a(var1, var2);
      return super.func_180646_a(var1, var2);
   }

   public boolean func_149686_d() {
      return false;
   }

   public boolean func_176205_b(IBlockAccess var1, BlockPos var2) {
      return true;
   }

   public boolean func_149662_c() {
      return false;
   }

   public boolean func_181623_g() {
      return true;
   }

   public TileEntity func_149915_a(World var1, int var2) {
      return new TileEntitySign();
   }

   public Item func_180660_a(IBlockState var1, Random var2, int var3) {
      return Items.field_151155_ap;
   }

   public Item func_180665_b(World var1, BlockPos var2) {
      return Items.field_151155_ap;
   }

   public boolean func_180639_a(World var1, BlockPos var2, IBlockState var3, EntityPlayer var4, EnumFacing var5, float var6, float var7, float var8) {
      if (var1.field_72995_K) {
         return true;
      } else {
         TileEntity var9 = var1.func_175625_s(var2);
         return var9 instanceof TileEntitySign ? ((TileEntitySign)var9).func_174882_b(var4) : false;
      }
   }

   public boolean func_176196_c(World var1, BlockPos var2) {
      return !this.func_181087_e(var1, var2) && super.func_176196_c(var1, var2);
   }
}
