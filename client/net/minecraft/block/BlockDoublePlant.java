package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;

public class BlockDoublePlant extends BlockBush {
   public static final EnumProperty<DoubleBlockHalf> field_176492_b;

   public BlockDoublePlant(Block.Properties var1) {
      super(var1);
      this.func_180632_j((IBlockState)((IBlockState)this.field_176227_L.func_177621_b()).func_206870_a(field_176492_b, DoubleBlockHalf.LOWER));
   }

   public IBlockState func_196271_a(IBlockState var1, EnumFacing var2, IBlockState var3, IWorld var4, BlockPos var5, BlockPos var6) {
      DoubleBlockHalf var7 = (DoubleBlockHalf)var1.func_177229_b(field_176492_b);
      if (var2.func_176740_k() == EnumFacing.Axis.Y && var7 == DoubleBlockHalf.LOWER == (var2 == EnumFacing.UP) && (var3.func_177230_c() != this || var3.func_177229_b(field_176492_b) == var7)) {
         return Blocks.field_150350_a.func_176223_P();
      } else {
         return var7 == DoubleBlockHalf.LOWER && var2 == EnumFacing.DOWN && !var1.func_196955_c(var4, var5) ? Blocks.field_150350_a.func_176223_P() : super.func_196271_a(var1, var2, var3, var4, var5, var6);
      }
   }

   @Nullable
   public IBlockState func_196258_a(BlockItemUseContext var1) {
      BlockPos var2 = var1.func_195995_a();
      return var2.func_177956_o() < 255 && var1.func_195991_k().func_180495_p(var2.func_177984_a()).func_196953_a(var1) ? super.func_196258_a(var1) : null;
   }

   public void func_180633_a(World var1, BlockPos var2, IBlockState var3, EntityLivingBase var4, ItemStack var5) {
      var1.func_180501_a(var2.func_177984_a(), (IBlockState)this.func_176223_P().func_206870_a(field_176492_b, DoubleBlockHalf.UPPER), 3);
   }

   public boolean func_196260_a(IBlockState var1, IWorldReaderBase var2, BlockPos var3) {
      if (var1.func_177229_b(field_176492_b) != DoubleBlockHalf.UPPER) {
         return super.func_196260_a(var1, var2, var3);
      } else {
         IBlockState var4 = var2.func_180495_p(var3.func_177977_b());
         return var4.func_177230_c() == this && var4.func_177229_b(field_176492_b) == DoubleBlockHalf.LOWER;
      }
   }

   public void func_196390_a(IWorld var1, BlockPos var2, int var3) {
      var1.func_180501_a(var2, (IBlockState)this.func_176223_P().func_206870_a(field_176492_b, DoubleBlockHalf.LOWER), var3);
      var1.func_180501_a(var2.func_177984_a(), (IBlockState)this.func_176223_P().func_206870_a(field_176492_b, DoubleBlockHalf.UPPER), var3);
   }

   public void func_180657_a(World var1, EntityPlayer var2, BlockPos var3, IBlockState var4, @Nullable TileEntity var5, ItemStack var6) {
      super.func_180657_a(var1, var2, var3, Blocks.field_150350_a.func_176223_P(), var5, var6);
   }

   public void func_176208_a(World var1, BlockPos var2, IBlockState var3, EntityPlayer var4) {
      DoubleBlockHalf var5 = (DoubleBlockHalf)var3.func_177229_b(field_176492_b);
      boolean var6 = var5 == DoubleBlockHalf.LOWER;
      BlockPos var7 = var6 ? var2.func_177984_a() : var2.func_177977_b();
      IBlockState var8 = var1.func_180495_p(var7);
      if (var8.func_177230_c() == this && var8.func_177229_b(field_176492_b) != var5) {
         var1.func_180501_a(var7, Blocks.field_150350_a.func_176223_P(), 35);
         var1.func_180498_a(var4, 2001, var7, Block.func_196246_j(var8));
         if (!var1.field_72995_K && !var4.func_184812_l_()) {
            if (var6) {
               this.func_196391_a(var3, var1, var2, var4.func_184614_ca());
            } else {
               this.func_196391_a(var8, var1, var7, var4.func_184614_ca());
            }
         }
      }

      super.func_176208_a(var1, var2, var3, var4);
   }

   protected void func_196391_a(IBlockState var1, World var2, BlockPos var3, ItemStack var4) {
      var1.func_196949_c(var2, var3, 0);
   }

   public IItemProvider func_199769_a(IBlockState var1, World var2, BlockPos var3, int var4) {
      return (IItemProvider)(var1.func_177229_b(field_176492_b) == DoubleBlockHalf.LOWER ? super.func_199769_a(var1, var2, var3, var4) : Items.field_190931_a);
   }

   protected void func_206840_a(StateContainer.Builder<Block, IBlockState> var1) {
      var1.func_206894_a(field_176492_b);
   }

   public Block.EnumOffsetType func_176218_Q() {
      return Block.EnumOffsetType.XZ;
   }

   public long func_209900_a(IBlockState var1, BlockPos var2) {
      return MathHelper.func_180187_c(var2.func_177958_n(), var2.func_177979_c(var1.func_177229_b(field_176492_b) == DoubleBlockHalf.LOWER ? 0 : 1).func_177956_o(), var2.func_177952_p());
   }

   static {
      field_176492_b = BlockStateProperties.field_208163_P;
   }
}
