package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Particles;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockFurnace extends BlockContainer {
   public static final DirectionProperty field_176447_a;
   public static final BooleanProperty field_196325_b;

   protected BlockFurnace(Block.Properties var1) {
      super(var1);
      this.func_180632_j((IBlockState)((IBlockState)((IBlockState)this.field_176227_L.func_177621_b()).func_206870_a(field_176447_a, EnumFacing.NORTH)).func_206870_a(field_196325_b, false));
   }

   public int func_149750_m(IBlockState var1) {
      return (Boolean)var1.func_177229_b(field_196325_b) ? super.func_149750_m(var1) : 0;
   }

   public boolean func_196250_a(IBlockState var1, World var2, BlockPos var3, EntityPlayer var4, EnumHand var5, EnumFacing var6, float var7, float var8, float var9) {
      if (var2.field_72995_K) {
         return true;
      } else {
         TileEntity var10 = var2.func_175625_s(var3);
         if (var10 instanceof TileEntityFurnace) {
            var4.func_71007_a((TileEntityFurnace)var10);
            var4.func_195066_a(StatList.field_188061_aa);
         }

         return true;
      }
   }

   public TileEntity func_196283_a_(IBlockReader var1) {
      return new TileEntityFurnace();
   }

   public IBlockState func_196258_a(BlockItemUseContext var1) {
      return (IBlockState)this.func_176223_P().func_206870_a(field_176447_a, var1.func_195992_f().func_176734_d());
   }

   public void func_180633_a(World var1, BlockPos var2, IBlockState var3, EntityLivingBase var4, ItemStack var5) {
      if (var5.func_82837_s()) {
         TileEntity var6 = var1.func_175625_s(var2);
         if (var6 instanceof TileEntityFurnace) {
            ((TileEntityFurnace)var6).func_200225_a(var5.func_200301_q());
         }
      }

   }

   public void func_196243_a(IBlockState var1, World var2, BlockPos var3, IBlockState var4, boolean var5) {
      if (var1.func_177230_c() != var4.func_177230_c()) {
         TileEntity var6 = var2.func_175625_s(var3);
         if (var6 instanceof TileEntityFurnace) {
            InventoryHelper.func_180175_a(var2, var3, (TileEntityFurnace)var6);
            var2.func_175666_e(var3, this);
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

   public void func_180655_c(IBlockState var1, World var2, BlockPos var3, Random var4) {
      if ((Boolean)var1.func_177229_b(field_196325_b)) {
         double var5 = (double)var3.func_177958_n() + 0.5D;
         double var7 = (double)var3.func_177956_o();
         double var9 = (double)var3.func_177952_p() + 0.5D;
         if (var4.nextDouble() < 0.1D) {
            var2.func_184134_a(var5, var7, var9, SoundEvents.field_187652_bv, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
         }

         EnumFacing var11 = (EnumFacing)var1.func_177229_b(field_176447_a);
         EnumFacing.Axis var12 = var11.func_176740_k();
         double var13 = 0.52D;
         double var15 = var4.nextDouble() * 0.6D - 0.3D;
         double var17 = var12 == EnumFacing.Axis.X ? (double)var11.func_82601_c() * 0.52D : var15;
         double var19 = var4.nextDouble() * 6.0D / 16.0D;
         double var21 = var12 == EnumFacing.Axis.Z ? (double)var11.func_82599_e() * 0.52D : var15;
         var2.func_195594_a(Particles.field_197601_L, var5 + var17, var7 + var19, var9 + var21, 0.0D, 0.0D, 0.0D);
         var2.func_195594_a(Particles.field_197631_x, var5 + var17, var7 + var19, var9 + var21, 0.0D, 0.0D, 0.0D);
      }
   }

   public EnumBlockRenderType func_149645_b(IBlockState var1) {
      return EnumBlockRenderType.MODEL;
   }

   public IBlockState func_185499_a(IBlockState var1, Rotation var2) {
      return (IBlockState)var1.func_206870_a(field_176447_a, var2.func_185831_a((EnumFacing)var1.func_177229_b(field_176447_a)));
   }

   public IBlockState func_185471_a(IBlockState var1, Mirror var2) {
      return var1.func_185907_a(var2.func_185800_a((EnumFacing)var1.func_177229_b(field_176447_a)));
   }

   protected void func_206840_a(StateContainer.Builder<Block, IBlockState> var1) {
      var1.func_206894_a(field_176447_a, field_196325_b);
   }

   static {
      field_176447_a = BlockHorizontal.field_185512_D;
      field_196325_b = BlockRedstoneTorch.field_196528_a;
   }
}
