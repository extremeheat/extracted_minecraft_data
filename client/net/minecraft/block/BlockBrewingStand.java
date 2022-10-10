package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Particles;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBrewingStand;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockBrewingStand extends BlockContainer {
   public static final BooleanProperty[] field_176451_a;
   protected static final VoxelShape field_196308_b;

   public BlockBrewingStand(Block.Properties var1) {
      super(var1);
      this.func_180632_j((IBlockState)((IBlockState)((IBlockState)((IBlockState)this.field_176227_L.func_177621_b()).func_206870_a(field_176451_a[0], false)).func_206870_a(field_176451_a[1], false)).func_206870_a(field_176451_a[2], false));
   }

   public EnumBlockRenderType func_149645_b(IBlockState var1) {
      return EnumBlockRenderType.MODEL;
   }

   public TileEntity func_196283_a_(IBlockReader var1) {
      return new TileEntityBrewingStand();
   }

   public boolean func_149686_d(IBlockState var1) {
      return false;
   }

   public VoxelShape func_196244_b(IBlockState var1, IBlockReader var2, BlockPos var3) {
      return field_196308_b;
   }

   public boolean func_196250_a(IBlockState var1, World var2, BlockPos var3, EntityPlayer var4, EnumHand var5, EnumFacing var6, float var7, float var8, float var9) {
      if (var2.field_72995_K) {
         return true;
      } else {
         TileEntity var10 = var2.func_175625_s(var3);
         if (var10 instanceof TileEntityBrewingStand) {
            var4.func_71007_a((TileEntityBrewingStand)var10);
            var4.func_195066_a(StatList.field_188081_O);
         }

         return true;
      }
   }

   public void func_180633_a(World var1, BlockPos var2, IBlockState var3, EntityLivingBase var4, ItemStack var5) {
      if (var5.func_82837_s()) {
         TileEntity var6 = var1.func_175625_s(var2);
         if (var6 instanceof TileEntityBrewingStand) {
            ((TileEntityBrewingStand)var6).func_200224_a(var5.func_200301_q());
         }
      }

   }

   public void func_180655_c(IBlockState var1, World var2, BlockPos var3, Random var4) {
      double var5 = (double)((float)var3.func_177958_n() + 0.4F + var4.nextFloat() * 0.2F);
      double var7 = (double)((float)var3.func_177956_o() + 0.7F + var4.nextFloat() * 0.3F);
      double var9 = (double)((float)var3.func_177952_p() + 0.4F + var4.nextFloat() * 0.2F);
      var2.func_195594_a(Particles.field_197601_L, var5, var7, var9, 0.0D, 0.0D, 0.0D);
   }

   public void func_196243_a(IBlockState var1, World var2, BlockPos var3, IBlockState var4, boolean var5) {
      if (var1.func_177230_c() != var4.func_177230_c()) {
         TileEntity var6 = var2.func_175625_s(var3);
         if (var6 instanceof TileEntityBrewingStand) {
            InventoryHelper.func_180175_a(var2, var3, (TileEntityBrewingStand)var6);
         }

         super.func_196243_a(var1, var2, var3, var4, var5);
      }
   }

   public boolean func_149740_M(IBlockState var1) {
      return true;
   }

   public int func_180641_l(IBlockState var1, World var2, BlockPos var3) {
      return Container.func_178144_a(var2.func_175625_s(var3));
   }

   public BlockRenderLayer func_180664_k() {
      return BlockRenderLayer.CUTOUT;
   }

   protected void func_206840_a(StateContainer.Builder<Block, IBlockState> var1) {
      var1.func_206894_a(field_176451_a[0], field_176451_a[1], field_176451_a[2]);
   }

   public BlockFaceShape func_193383_a(IBlockReader var1, IBlockState var2, BlockPos var3, EnumFacing var4) {
      return BlockFaceShape.UNDEFINED;
   }

   public boolean func_196266_a(IBlockState var1, IBlockReader var2, BlockPos var3, PathType var4) {
      return false;
   }

   static {
      field_176451_a = new BooleanProperty[]{BlockStateProperties.field_208184_k, BlockStateProperties.field_208185_l, BlockStateProperties.field_208186_m};
      field_196308_b = VoxelShapes.func_197872_a(Block.func_208617_a(1.0D, 0.0D, 1.0D, 15.0D, 2.0D, 15.0D), Block.func_208617_a(7.0D, 0.0D, 7.0D, 9.0D, 14.0D, 9.0D));
   }
}
